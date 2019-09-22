package com.zyc.pandora.shell.dto;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;

/**
 * @author : zhangyuchen
 * @date : 2019/9/22 23:23
 */
@NoArgsConstructor
@Slf4j
public class LogFile {


    private Path path;
    private Long seek;


    public LogFile(Path path) {
        this.path = path;
        this.seek = 0L;
    }

    public void appendLine(String line) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(this.path.toFile(), "rw");
        raf.seek(seek);
        raf.writeUTF(line);
        this.seek += line.getBytes().length;
        raf.close();
    }

    public void appendInputStream(InputStream inputStream,Integer bufferedSize) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder lines = new StringBuilder();
        Integer lineCount = 0;
        while ((line = bufferedReader.readLine()) != null) {
            log.info(line);
            lines.append(line).append("\n\r");
            lineCount++;
            if (lineCount % bufferedSize == 0) {
                this.appendLine(lines.toString());
                lines.setLength(0);
            }
        }
        if (lines.length() > 0) {
            this.appendLine(lines.toString());
            lines.setLength(0);
        }
    }

    public void resetSeek(Long seek){
        this.seek = seek;
    }
}
