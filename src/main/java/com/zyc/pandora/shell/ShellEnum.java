package com.zyc.pandora.shell;

import com.alibaba.fastjson.JSONObject;

/**
 * @author : zhangyuchen
 * @date : 2019/9/22 09:11
 */
public enum ShellEnum {
    /**
     * 运行脚本
     */
    runShell{
        @Override
        public String create(JSONObject param) {
            String shell =
                    replacePlaceholder(
                            "#!/bin/bash\n" +
                                    "set -e \n" +
                                    "branch=#{branch} \n" +
                                    "url=#{url} \n" +
                                    "app=#{project} \n" +
                                    "path=#{workPath} \n" +
                                    "logpath=$path/run.log\n" +
                                    "echo 项目部署主路径$path\n" +
                                    "kid=$(ps -ef | awk '{if($0~\"#{project}\"&&$0!~\"awk\"&&$0!~\".sh\")print $2}')\n" +
                                    "if [ \"$kid\" ]; then\n" +
                                    "echo 存在已发布进程 \n" +
                                    "kill -9 $kid\n" +
                                    "echo kill执行成功 \n" +
                                    "fi\n" +
                                    "rm -rf $path\n" +
                                    "echo 从远端仓库克隆代码中 克隆时间取决与网络 \n" +
                                    "git clone -b $branch $url $path\n" +
                                    "echo 克隆成功切换路径 \n" +
                                    "cd #{pomPath}\n" +
                                    "mvn compile\n" +
                                    "mvn install\n" +
                                    "mvn package\n" +
                                    "cd target\n" +
                                    "nohup java -jar --spring.profiles.active=#{profiles} *.jar >>$logpath 2>&1 &", param);
            return shell;
        }
    };
    public abstract String create(JSONObject param);

    private static String replacePlaceholder(String src,JSONObject param){
        for (String key : param.keySet()){
            src = src.replaceAll(String.format("#\\{%s\\}",key),param.getString(key));
        }
        return src;
    }
}
