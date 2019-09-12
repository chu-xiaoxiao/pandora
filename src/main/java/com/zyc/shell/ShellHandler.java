package com.zyc.pandora.shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * @author : zhangyuchen
 * @date : 2019/9/12 11:05
 */
public class ShellHandler {

    public static File createShellFile() throws IOException {
        StringBuilder shell = new StringBuilder();
        shell.append("#!/bin/bash\n" +
                "echo '自动部署Springboot项目脚本...'\n" +
                "echo '1. 拉取github代码...'");
        File file = new File("runShell.sh");
        if(!file.exists()){
            file.createNewFile();
        }
        Files.write(Paths.get(file.toURI()), shell.toString().getBytes(), StandardOpenOption.WRITE);
        return file;
    }
}
