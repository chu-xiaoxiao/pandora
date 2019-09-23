package com.zyc.pandora.api;

import com.alibaba.fastjson.JSONObject;
import com.zyc.pandora.domain.dto.LogParam;
import com.zyc.pandora.domain.dto.RangeLog;
import com.zyc.pandora.domain.dto.StartRequestParam;
import com.zyc.pandora.domain.vo.CommonResponse;
import com.zyc.pandora.shell.ShellEnum;
import com.zyc.pandora.shell.ShellHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

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
    @Resource
    private ShellHandler shellHandler;

    @RequestMapping(value = "/deploy",method = RequestMethod.POST)
    public CommonResponse start(@RequestBody StartRequestParam startRequestParam){
        try {
            File runShellFile  =  shellHandler.createShellFile("runShell", startRequestParam.getProject(),ShellEnum.runShell, JSONObject.parseObject(JSONObject.toJSONString(startRequestParam)));
            shellHandler.runShell(runShellFile,startRequestParam.getProject());
            ///shellHandler.checkHealth(startRequestParam.getProject(),startRequestParam.getCheckHealthUrl());
            return  CommonResponse.Builder.SUCC().initSuccMsg("启动脚本执行成功");
        } catch (Exception e) {
            return CommonResponse.Builder.FAIL().initErrMsg("启动脚本执行失败"+e.getMessage());
        }
    }

    @RequestMapping(value = "/log",method = RequestMethod.GET)
    public CommonResponse log(LogParam logParam){
        RangeLog result = shellHandler.readLog(logParam);
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        return CommonResponse.Builder.SUCC().initSuccData(result);
    }
}
