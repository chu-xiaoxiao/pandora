package com.zyc.pandora.api;

import com.zyc.pandora.db.generator.DBGeneratorBuilder;
import com.zyc.pandora.domain.dto.db.GeneratorParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author : zhangyuchen
 * @date : 2019/9/21 11:10
 */
@RestController
@RequestMapping("/DB")
@Slf4j
public class DBGeneratorController {

    @Value("${project.path}")
    private String projectPath;
    @Resource
    private HttpServletResponse response;
    @RequestMapping("/generator")
    public void generator(GeneratorParam generatorParam){
        DBGeneratorBuilder builder = DBGeneratorBuilder.builder()
                .url(generatorParam.getUrl())
                .dbName(generatorParam.getDbName())
                .user(generatorParam.getUser())
                .password(generatorParam.getPassword())
                .baseModelName(generatorParam.getBaseModelName())
                .tableNames(Arrays.asList(generatorParam.getTableNames().split("\\|")))
                .build();
        try {
            response.setHeader("Content-Disposition", "attchement;filename=generator.zip");
            builder.newCG(projectPath).generateAndDownloadZip(response.getOutputStream());
        } catch (ClassNotFoundException | SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
