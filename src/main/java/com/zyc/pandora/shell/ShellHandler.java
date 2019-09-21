package com.zyc.pandora.shell;

import com.zyc.pandora.domain.dto.LogParam;
import com.zyc.pandora.domain.dto.RangeLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

/**
 * @author : zhangyuchen
 * @date : 2019/9/12 11:05
 */
@Service
@Slf4j
public class ShellHandler {

    @Value("${project.path}")
    private String projectPath;
    @Value("${project.log.buffer}")
    private Integer projectLogBuffer;


    public File createRunShellFile(String fileName, String url, String project, String branch) throws IOException {
        Path appPath = Paths.get(String.format("/%s/%s", projectPath, project));
        File appDir = appPath.toFile();
        if (!appDir.exists()) {
            Files.createDirectories(appPath);
        }
        String shell =
                String.format(
                        "#!/bin/bash\n" +
                                "set -e \n" +
                                "branch=%s \n" +
                                "url=%s \n" +
                                "app=%s \n" +
                                "path=%s \n" +
                                "logpath=$path/run.log\n" +
                                "echo 项目部署主路径$path\n" +
                                "kid=$(ps -ef | awk '{if($0~\"%s\"&&$0!~\"awk\"&&$0!~\".sh\")print $2}')\n" +
                                "if [ \"$kid\" ]; then\n" +
                                "echo 存在已发布进程 \n" +
                                "kill -9 $kid\n" +
                                "echo kill执行成功 \n" +
                                "fi\n" +
                                "rm -rf $path\n" +
                                "echo 从远端仓库克隆代码中 克隆时间取决与网络 \n" +
                                "git clone -b $branch $url $path\n" +
                                "echo 克隆成功切换路径 \n" +
                                "cd $path/$app\n" +
                                "mvn compile\n" +
                                "mvn install\n" +
                                "mvn package\n" +
                                "cd target\n" +
                                "nohup java -jar *.jar >>$logpath 2>&1 &", branch, url, project, appPath.toRealPath(), project);
        Path shellPath = Paths.get(String.format("%s/%s_runShell.sh", projectPath, project));
        File shellFile = shellPath.toFile();
        shellFile.deleteOnExit();
        if (shellFile.createNewFile()) {
            Files.write(Paths.get(shellFile.toURI()), shell.getBytes(), StandardOpenOption.WRITE);
        }
        return shellFile;
    }


    public void runShell(File shell, String project) throws IOException {
        Runtime.getRuntime().exec(String.format("chmod 777 %s", shell.getAbsolutePath()));
        CompletableFuture.supplyAsync(() -> {
            RandomAccessFile raf;
            Long seek = 0L;
            try {
                Process process = Runtime.getRuntime().exec(String.format("%s", shell.getAbsolutePath()));
                BufferedReader inputStream;
                inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                StringBuilder lines = new StringBuilder();
                Integer lineCount = 0;
                while ((line = inputStream.readLine()) != null) {
                    log.info(line);
                    lines.append(line).append("\n\r");
                    lineCount++;
                    if (lineCount % projectLogBuffer == 0) {
                        raf = new RandomAccessFile(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)).toFile(), "rw");
                        raf.seek(seek);
                        raf.write(lines.toString().getBytes(), 0, lines.toString().getBytes().length);
                        seek += lines.toString().getBytes().length;
                        raf.close();
                        lines.setLength(0);
                    }
                }
                if (lines.length() > 0) {
                    raf = new RandomAccessFile(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)).toFile(), "rw");
                    raf.seek(seek);
                    raf.write(lines.toString().getBytes(), 0, lines.toString().getBytes().length);
                    raf.close();
                    lines.setLength(0);
                }
                int result = process.waitFor();
                if(result!=0){
                    String errLine = "脚本执行异常 错误码"+result;
                    log.warn(errLine);
                    raf = new RandomAccessFile(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)).toFile(), "rw");
                    raf.seek(seek);
                    raf.write(errLine.getBytes(), 0, errLine.getBytes().length);
                    raf.close();
                    lines.setLength(0);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        });
    }

    public RangeLog readLog(LogParam logParam) {
        Path logPath = Paths.get(String.format("/%s/%s/%s.log", projectPath, logParam.getProject(), logParam.getType()));
        RandomAccessFile raf = null;
        RangeLog result = new RangeLog();
        try {
            raf = new RandomAccessFile(logPath.toFile(), "r");
            raf.seek(logParam.getOffset());
            String line;
            StringBuilder log = new StringBuilder();
            while ((line = raf.readLine()) != null) {
                log.append(line).append("<br/>");
                if (line.contains("应用构建成功")) {
                    result.setOver(true);
                }
            }
            result.setData(log.toString());
            result.setOffset(raf.getFilePointer());
        } catch (IOException e) {
            log.warn(e.getMessage());
        } finally {
            try {
                if (raf != null) {
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
