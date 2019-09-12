package com.zyc.pandora.api;

import com.zyc.pandora.domain.dto.StartRequestParam;
import com.zyc.pandora.domain.vo.CommonResponse;
import com.zyc.pandora.shell.ShellHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.*;
import java.util.Map;

/**
 * @author : zhangyuchen
 * @date : 2019/9/12 10:52
 */
@RestController
@Controller
@Slf4j
public class CommandController {

    @Resource
    private HttpServletResponse response;

    @RequestMapping(value = "/deploy",method = RequestMethod.POST)
    public void start(@RequestBody StartRequestParam startRequestParam){
        try {
            String fileName = "runShell.sh";
            Map map = System.getenv();
            ShellHandler.createRunShellFile(fileName,startRequestParam.getUrl(),startRequestParam.getProject(),startRequestParam.getBranch());
            Runtime.getRuntime().exec("chmod 777 ./"+fileName);
            Process process = Runtime.getRuntime().exec("sh ./"+fileName);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                log.error("call shell failed. error code is :" + exitValue);
            }
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            OutputStream outputStream = response.getOutputStream();
            String line;
            while((line=inputStream.readLine())!=null){
                ((ServletOutputStream) outputStream).println(line+"<br/>");
                log.info(line);
                outputStream.flush();
            }
            log.info("发布成功");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/log",method = RequestMethod.POST)
    public void log(@RequestBody StartRequestParam startRequestParam){

    }
}
