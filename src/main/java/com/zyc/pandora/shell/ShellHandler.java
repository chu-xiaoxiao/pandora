package com.zyc.pandora.shell;

import com.alibaba.fastjson.JSONObject;
import com.zyc.pandora.domain.dto.LogParam;
import com.zyc.pandora.domain.dto.RangeLog;
import com.zyc.pandora.httpclient.HttpUtils;
import com.zyc.pandora.shell.dto.LogFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
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
    private Integer logBuffer;
    @Value("${project.log.checkHealthCount}")
    private Integer checkHealthCount;

    public List<String> getProjectBranch(String url) {
        return null;
    }

    public File createShellFile(String fileName, String workPath, ShellEnum shellEnum, JSONObject param) throws IOException {
        Path appPath = Paths.get(String.format("/%s/%s/", projectPath, workPath));
        File appDir = appPath.toFile();
        if (!appDir.exists()) {
            Files.createDirectories(appPath);
        }else{
            Files.delete(appPath);
            Files.createDirectories(appPath);
        }
        Path shellPath = Paths.get(String.format("%s/%s_%s.sh", projectPath, workPath, fileName));
        File shellFile = shellPath.toFile();
        shellFile.deleteOnExit();
        param.put("projectPath", projectPath);
        param.put("workPath", String.format("%s/%s", projectPath, workPath));
        if(param.getBoolean("rootProject")){
            param.put("pomPath", String.format("%s/%s", projectPath,param.getString("project")));
        }else{
            param.put("pomPath", String.format("%s/%s/%s", projectPath,param.getString("project"), workPath));
        }
        if (shellFile.createNewFile()) {
            Files.write(Paths.get(shellFile.toURI()), shellEnum.create(param).getBytes(), StandardOpenOption.WRITE);
        }
        return shellFile;
    }


    public void runShell(File shell, String project) throws IOException {
        Runtime.getRuntime().exec(String.format("chmod 777 %s", shell.getAbsolutePath()));
        CompletableFuture.supplyAsync(() -> {
            try {
                LogFile logFile = new LogFile(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)));
                Process process = Runtime.getRuntime().exec(String.format("%s", shell.getAbsolutePath()));
                logFile.appendInputStream(process.getInputStream(),logBuffer);
                logFile.appendInputStream(process.getErrorStream(),logBuffer);
                int result = process.waitFor();
                if (result != 0) {
                    String errLine = "脚本执行异常 错误码" + result+"\n\r";
                    log.warn(errLine);
                    logFile.appendLine(errLine);
                }else{
                    logFile.appendLine("\n\r项目部署成功");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        });
    }


    public void checkHealth(String project,String checkHealthUrl) throws Exception {
        LogFile logFile = new LogFile(Paths.get(String.format("/%s/%s/packageLog.log", projectPath, project)));
        if(StringUtils.isEmpty(checkHealthUrl)){
            logFile.appendLine("未填写探活地址 不进行探活操作");
            return;
        }
        Integer count = 0;
        while(HttpUtils.doPost("http://localhost/"+checkHealthUrl).getCode()!=200&&count<checkHealthCount){
            Thread.sleep(1000);
            logFile.appendLine("探活中");
        }
        if(count>=checkHealthCount){
            logFile.appendLine("探活失败 项目启动超时");
        }else {
            logFile.appendLine("项目启动成功");
        }
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


}
