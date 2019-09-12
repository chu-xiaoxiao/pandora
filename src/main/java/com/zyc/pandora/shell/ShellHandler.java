package com.zyc.pandora.shell;

import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

/**
 * @author : zhangyuchen
 * @date : 2019/9/12 11:05
 */
public class ShellHandler {

    public static File createRunShellFile(String fileName,String url,String project,String branch) throws IOException {
        StringBuilder shell = new StringBuilder();
        shell.append(
                "branch="+branch+"\n" +
                "url="+url+"\n" +
                "app="+project+"\n" +
                "logpath=log.log\n" +
                "path=${url##*/}\n" +
                "path=${path%.*}\n" +
                "echo $path\n" +
                "rm -rf $path\n" +
                "git clone -b $branch $url\n" +
                "cd $path\n" +
                "cd $app\n" +
                        "java -h \n"+
                "mvn compile\n" +
                "mvn install\n" +
                "mvn package\n" +
                "cd target\n"+
                "nohup java -jar *.jar >>$logpath 2>&1 &\n"
        );
        File file = new File(fileName);
        file.deleteOnExit();
        file.createNewFile();
        Files.write(Paths.get(file.toURI()), shell.toString().getBytes(), StandardOpenOption.WRITE);
        return file;
    }

/*    public static File getCreateRunShellFileLog(String url,String project){
        File file = new File()
    }*/
}
