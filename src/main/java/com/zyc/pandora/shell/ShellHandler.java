package com.zyc.pandora.shell;

import com.alibaba.fastjson.JSONObject;
import com.zyc.pandora.domain.dto.LogParam;
import com.zyc.pandora.domain.dto.RangeLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
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

    public List<String> getProjectBranch(String url){
        return null;
    }

    public File createShellFile(String fileName, String workPath, ShellEnum shellEnum, JSONObject param) throws IOException {
        Path appPath = Paths.get(String.format("/%s/%s", projectPath, workPath));
        File appDir = appPath.toFile();
        if (!appDir.exists()) {
            Files.createDirectories(appPath);
        }
        Path shellPath = Paths.get(String.format("%s/%s_%s.sh", projectPath, workPath,fileName));
        File shellFile = shellPath.toFile();
        shellFile.deleteOnExit();
        param.put("projectPath",projectPath);
        param.put("workPath",workPath);
        param.put("shellPath",String.format("%s%s",projectPath,workPath));
        if (shellFile.createNewFile()) {
            Files.write(Paths.get(shellFile.toURI()), shellEnum.create(param).getBytes(), StandardOpenOption.WRITE);
        }
        return shellFile;
    }


    public void runShell(File shell, String project) throws IOException {
        Runtime.getRuntime().exec(String.format("chmod 777 %s", shell.getAbsolutePath()));
        CompletableFuture.supplyAsync(() -> {
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
                        seek = writeLine(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)),seek,lines.toString());
                        lines.setLength(0);
                    }
                }
                if (lines.length() > 0) {
                    seek = writeLine(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)),seek,lines.toString());
                    lines.setLength(0);
                }
                int result = process.waitFor();
                if(result!=0){
                    String errLine = "脚本执行异常 错误码"+result;
                    log.warn(errLine);
                    seek = writeLine(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)),seek,lines.toString());
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
            result.setData(new String(log.toString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
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


    public Long writeLine(Path path,Long seek,String line) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw");
        raf.seek(seek);
        raf.write(line.getBytes(), 0, line.getBytes().length);
        seek += line.getBytes().length;
        raf.close();
        return seek;
    }
}
