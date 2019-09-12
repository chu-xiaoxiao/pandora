package com.zyc.pandora.api;

import com.zyc.pandora.shell.ShellHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author : zhangyuchen
 * @date : 2019/9/12 10:52
 */
@RestController
@Controller
@Slf4j
public class CommandController {


    @RequestMapping("/start")
    public void start(){
        try {
            ShellHandler.createShellFile();
            Runtime.getRuntime().exec("chmod 777  ./runShell.sh");
            Process process = Runtime.getRuntime().exec("./runShell.sh");
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                log.error("call shell failed. error code is :" + exitValue);
            }
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line = input.readLine())!=null){
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
