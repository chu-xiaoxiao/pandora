package com.zyc.pandora.db.generator;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author : zhangyuchen
 * @date : 2019/9/21 10:52
 */
@Data
@Builder

public class DBGeneratorBuilder {

    private String dbName;

    /**
     * 基础包名
     */
    private String baseModelName;

    /**
     * 项目工作根路径
     */
    private String projectPath;

    private String user;

    private String password;

    /**
     * 数据库地址
     */
    private String url;

    private List<String> tableNames;

    /**
     * @param projectPath 项目工作根路径
     * @return
     */
    public DBGenerator newCG(String projectPath){
        DBGenerator cg = new DBGenerator();
        cg.dbName = this.dbName;
        cg.moduleName = this.baseModelName;
        String basePath = projectPath+"/db/"+this.dbName+"/"+cg.moduleName.replaceAll("\\.","/");
        cg.basePath = basePath;
        cg.beanPath = String.format("%s/entity_bean/%s",basePath,this.dbName);
        cg.mapperPath = String.format("%s/entity_mapper_dao/%s",basePath,dbName);
        cg.servicePath = String.format("%s/entity_service/%s",basePath,dbName);
        cg.iServicePath = String.format("%s/entity_service/%s",basePath,dbName);
        cg.xmlPath =String.format("%s/entity_mapper/%s",basePath,dbName);
        cg.beanPackage = String.format("%s.bean",this.baseModelName);
        cg.mapperPackage = String.format("%s.dao",this.baseModelName);
        cg.servicePackage = String.format("%s.service",this.baseModelName);
        cg.iServicePackage = String.format("%s.serviceImpl",this.baseModelName);
        cg.url = String.format("jdbc:mysql://%s/" + dbName + "?characterEncoding=utf8",this.url);
        cg.user  = this.user;
        cg.password = this.password;
        cg.tableNames =this.tableNames;
        return cg;
    }

}
