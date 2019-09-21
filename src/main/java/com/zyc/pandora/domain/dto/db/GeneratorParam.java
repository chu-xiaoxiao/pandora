package com.zyc.pandora.domain.dto.db;

import lombok.Data;

/**
 * @author : zhangyuchen
 * @date : 2019/9/21 11:11
 */
@Data
public class GeneratorParam {
    private String dbName;

    /**
     * 基础包名
     */
    private String baseModelName;


    private String user;

    private String password;

    private String tableNames;
    /**
     * 数据库地址
     */
    private String url;
}
