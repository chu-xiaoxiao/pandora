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


    public File createRunShellFile(String fileName, String url, String project, String branch) throws IOException {
        Path appPath = Paths.get(String.format("/%s/%s", projectPath, project));
        File appDir = appPath.toFile();
        if (!appDir.exists()) {
            Files.createDirectories(appPath);
        }
        String shell =
                String.format("set -e \n" +
                        "branch=%s \n" +
                        "url=%s \n" +
                        "app=%s \n" +
                        "path=%s \n" +
                        "logpath=$path/run.log\n" +
                        "echo $path\n" +
                        "kid=$(ps -ef | awk '{if($0~\"%s\"&&$0!~\"awk\")print $2}')\n" +
                        "if [ -n $kid ]; then\n" +
                        "kill -9 $kid\n" +
                        "fi\n" +
                        "rm -rf $path\n" +
                        "git clone -b $branch $url $path\n" +
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
            RandomAccessFile raf = null;
            Long seek = 0L;
            try {
                Process process = Runtime.getRuntime().exec(String.format("sh %s", shell.getAbsolutePath()));
                BufferedReader inputStream;
                inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = inputStream.readLine()) != null) {
                    raf = new RandomAccessFile(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project )).toFile(), "rw");
                    raf.seek(seek);
                    log.info(line);
                    line += "\n\r";
                    raf.write(line.getBytes(),0,line.getBytes().length);
                    seek+=line.getBytes().length;
                    raf.close();
                }
                String end = "应用构建成功 应用启动中。。。";
                raf = new RandomAccessFile(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project )).toFile(), "rw");
                raf.write(end.getBytes(),0,end.getBytes().length);
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        });
        log.info("调用脚本成功");
    }

    public RangeLog readLog(LogParam logParam){
        Path logPath = Paths.get(String.format("/%s/%s/%s.log", projectPath,logParam.getProject(),logParam.getType()));
        RandomAccessFile raf = null;
        RangeLog result = new RangeLog();
        try {
            raf = new RandomAccessFile(logPath.toFile(), "r");
            raf.seek(logParam.getOffset());
            String line;
            StringBuilder log = new StringBuilder();
            while((line=raf.readLine())!=null){
                log.append(line).append("<br/>");
                if(line.contains("应用构建成功")){
                    result.setOver(true);
                }
            }
            result.setData(log.toString());
            result.setOffset(raf.getFilePointer());
        } catch (IOException e) {
            log.warn(e.getMessage());
        }finally {
            try {
                if(raf!=null){
                    raf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
