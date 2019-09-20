package com.zyc.pandora.api;

import com.zyc.pandora.domain.dto.LogParam;
import com.zyc.pandora.domain.dto.RangeLog;
import com.zyc.pandora.domain.dto.StartRequestParam;
import com.zyc.pandora.domain.vo.CommonResponse;
import com.zyc.pandora.shell.ShellHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

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
        String fileName = "runShell.sh";
        try {
            File runShellFile  =  shellHandler.createRunShellFile(fileName,startRequestParam.getUrl(),startRequestParam.getProject(),startRequestParam.getBranch());
            shellHandler.runShell(runShellFile,startRequestParam.getProject());
            return  CommonResponse.Builder.SUCC().initSuccMsg("启动脚本执行成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  CommonResponse.Builder.FAIL().initErrMsg("启动脚本执行失败");
    }

    @RequestMapping(value = "/log",method = RequestMethod.GET)
    public CommonResponse log(LogParam logParam){
        RangeLog result = shellHandler.readLog(logParam);
        return CommonResponse.Builder.SUCC().initSuccData(result);
    }
}
