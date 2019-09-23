package com.zyc.pandora.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @Author: chengcaiyi
 * @Date: 2019-03-25 21:05
 * @Version 1.0
 */
public class HttpUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    /**
     * 编码格式。发送编码格式统一用UTF-8
     */
    private static final String ENCODING = "UTF-8";

    /**
     * post 请求
     */
    private static final String POST_METHOD = "post";

    /**
     * 设置连接超时时间，单位毫秒。
     */
    private static final int CONNECT_TIMEOUT = 10000;

    /**
     * 请求获取数据的超时时间(即响应时间)，单位毫秒。
     */
    private static final int SOCKET_TIMEOUT = 10000;

    /**
     * 异步请求重试测试
     */
    private static final int RETRY_TIMES = 5;

    /**
     * 重拾间隔时间 1分钟
     */
    private static final int RETRY_TIME = 60000;


    /**
     * 发送post请求；不带请求头和请求参数
     *
     * @param url 请求地址
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url) throws Exception {
        return doPost(url, null, null);
    }

    /**
     * 发送post请求；带请求参数
     *
     * @param url    请求地址
     * @param params 参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url, String params) throws Exception {
        return doPost(url, null, params);
    }

    /**
     * 发送post请求；带请求头和请求参数
     *
     * @param url     请求地址
     * @param headers 请求头集合
     * @param params  请求参数集合
     * @return
     * @throws Exception
     */
    public static HttpClientResult doPost(String url, JSONObject headers, String params) throws Exception {
        // 创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建http对象
        HttpPost httpPost = new HttpPost(url);
        /**
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection
         * 超时时间，单位毫秒。这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        httpPost.setConfig(requestConfig);
        packageHeader(headers, httpPost);

        // 封装请求参数
        packageParam(params, httpPost);

        // 执行请求并获得响应结果
        return getHttpClientResult(httpClient, httpPost);
    }

    /**
     * Description: 封装请求头
     *
     * @param params
     * @param httpMethod
     */
    public static void packageHeader(JSONObject params, HttpRequestBase httpMethod) {
        if(POST_METHOD.equals(httpMethod.getMethod().toLowerCase())){
            httpMethod.setHeader("Content-Type", "application/json;charset=utf-8");
            httpMethod.setHeader("Accept", "application/json;charset=utf-8");
            httpMethod.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
            httpMethod.setHeader("Accept-Encoding", "gzip, deflate, br");
        }
        // 封装请求头
        if (params != null) {
            Set<Map.Entry<String, Object>> entrySet = params.entrySet();
            for (Map.Entry<String, Object> entry : entrySet) {
                // 设置到请求头到HttpRequestBase对象中
                httpMethod.setHeader(entry.getKey(), entry.getValue().toString());
            }
        }
    }

    /**
     * Description: 封装请求参数
     *
     * @param params
     * @param httpMethod
     */
    public static void packageParam(String params, HttpEntityEnclosingRequestBase httpMethod) {
        // 封装请求参数
        if (params != null) {
            // 设置到请求的http对象中
            StringEntity s = new StringEntity(params, ENCODING);
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpMethod.setEntity(s);
        }
    }

    /**
     * Description: 获得响应结果
     *
     * @param httpClient
     * @param httpMethod
     * @return
     * @throws Exception
     */
    private static HttpClientResult getHttpClientResult(CloseableHttpClient httpClient, HttpRequestBase httpMethod) throws Exception {
        CloseableHttpResponse httpResponse = null;
        HttpClientResult result = new HttpClientResult();
        try {
            // 执行请求
            httpResponse = httpClient.execute(httpMethod);
            // 获取返回结果
            if (httpResponse != null && httpResponse.getStatusLine() != null) {
                String content = "";
                if (httpResponse.getEntity() != null) {
                    content = EntityUtils.toString(httpResponse.getEntity(), ENCODING);
                }
                result.setCode(httpResponse.getStatusLine().getStatusCode());
                result.setContent(content);
            }
            return result;
        } finally {
            release(httpClient, httpResponse);
        }
    }

    /**
     * Description: 释放资源
     *
     * @param httpResponse
     */
    public static void release(CloseableHttpClient httpClient, CloseableHttpResponse httpResponse) throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
        if (httpResponse != null) {
            httpResponse.close();
        }
    }

}
