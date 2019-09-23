package com.zyc.pandora.api;

import com.zyc.pandora.domain.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

/**
 * @author : zhangyuchen
 * @date : 2019/9/23 16:58
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Value("${file.basePath}")
    private String fileBasePath;
    @Value("${file.ip}")
    private String fileIP;

    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public CommonResponse upload(@RequestParam("file") MultipartFile file){
        File file1 = new File(fileBasePath);
        if(!file1.exists()){
            file1.mkdirs();
        }
        if (file.isEmpty()) {
            return CommonResponse.Builder.FAIL().initErrMsg("上传失败 文件为空");
        }
        String fileName = String.format("%s_%s_%s_%s",Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH),UUID.randomUUID().toString().replaceAll("\\-",""));;
        String filePath = fileBasePath;
        File dest = new File(filePath + fileName);
        try {
            file.transferTo(dest);
            log.info("上传成功");
            return CommonResponse.Builder.SUCC().initSuccData(fileIP+filePath+fileName);
        } catch (IOException e) {
            log.error(e.toString(), e);
        }
        return CommonResponse.Builder.FAIL().initErrMsg("上传失败");
    }

}
