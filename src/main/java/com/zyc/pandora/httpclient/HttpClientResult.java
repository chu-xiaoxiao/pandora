package com.zyc.pandora.httpclient;

import lombok.Data;
import org.apache.http.HttpStatus;

import java.io.Serializable;

@Data
public class HttpClientResult implements Serializable {

    private static final long serialVersionUID = -7514146213147452693L;

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;

    public HttpClientResult() {
        this.code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
        this.content = "";
    }
}
