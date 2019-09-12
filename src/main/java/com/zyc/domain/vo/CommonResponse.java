package com.zyc.pandora.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : zhangyuchen
 * @date : 2019/3/11 15:24
 */
@Data
public class CommonResponse<T> implements Serializable {
    private static final long serialVersionUID = 7917345507074842804L;
    /**
     * success/fail
     */
    private String ret;
    /**
     * 0成功 非0具体错误原因
     */
    private String code;
    /**
     * 具体错误描述or成功描述
     */
    private String message;
    /**
     * 存放业务数据
     */
    private T data;
    public static final String SUCCESS_MESSAGE = "Success!";
    public static final String SUCCESS_CODE = "0";
    public static final String FAIL_MESSAGE = "Fail!";
    public static final String FAIL_CODE = "1";

    public static class Builder {
        public static CommonResponse SUCC() {
            CommonResponse vo = new CommonResponse();
            vo.setRet("success");
            vo.setCode("0");
            return vo;
        }

        public static CommonResponse FAIL() {
            CommonResponse vo = new CommonResponse();
            vo.setRet("fail");
            return vo;
        }
    }

    public CommonResponse initErrMsg(String message) {
        this.message = message;
        return this;
    }

    public CommonResponse initErrCodeAndMsg(String code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }

    public CommonResponse initErrCodeAndData(String code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public CommonResponse initSuccDataAndMsg(String code, String message) {
        this.code = code;
        this.message = message;
        return this;
    }


    public CommonResponse initSuccMsg(String message) {
        this.message = message;
        return this;
    }

    public CommonResponse initSuccData(T data) {
        this.data = data;
        return this;
    }

    /**
     * 无参响应成功
     * 推荐使用
     *
     * @return
     */
    public static CommonResponse responseSuccess() {
        CommonResponse vo = new CommonResponse();
        vo.setCode(SUCCESS_CODE);
        vo.setMessage(SUCCESS_MESSAGE);
        vo.setRet("Success");
        return vo;
    }

    /**
     * 传message响应成功
     * 推荐使用
     *
     * @param message
     * @return
     */
    public static CommonResponse responseSuccess(String message) {
        CommonResponse vo = new CommonResponse();
        vo.setCode(SUCCESS_CODE);
        vo.setMessage(message);
        vo.setRet("Success");
        return vo;
    }

    /**
     * 传code和message响应成功
     * 推荐使用
     *
     * @param code
     * @param message
     * @return
     */
    public static CommonResponse responseSuccess(String code, String message) {
        CommonResponse vo = new CommonResponse();
        vo.setCode(code);
        vo.setMessage(message);
        vo.setRet("Success");
        return vo;
    }

    /**
     * 无参响应失败
     * 推荐使用
     *
     * @return
     */
    public static CommonResponse responseFail() {
        CommonResponse vo = new CommonResponse();
        vo.setCode(FAIL_CODE);
        vo.setMessage(FAIL_MESSAGE);
        vo.setRet("Fail");
        return vo;
    }

    /**
     * 传message响应失败
     * 推荐使用
     *
     * @param message
     * @return
     */
    public static CommonResponse responseFail(String message) {
        CommonResponse vo = new CommonResponse();
        vo.setCode(FAIL_CODE);
        vo.setMessage(message);
        vo.setRet("Fail");
        return vo;
    }

    /**
     * 传code和message响应失败
     * 推荐使用
     *
     * @param code
     * @param message
     * @return
     */
    public static CommonResponse responseFail(String code, String message) {
        CommonResponse vo = new CommonResponse();
        vo.setMessage(message);
        vo.setCode(code);
        vo.setRet("Fail");
        return vo;
    }

}