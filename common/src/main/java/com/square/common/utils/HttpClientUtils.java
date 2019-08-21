package com.square.common.utils;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HttpClient4.3工具类
 */
public class HttpClientUtils {

    private static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class); // 日志记录
    private static RequestConfig requestConfig;
    private final static int timeout = 30000;
    private static HttpClientUtils httpUtils;

    /**
     * 获取实例
     *
     * @return
     */
    public static HttpClientUtils getInstance() {
        if (httpUtils == null)
            httpUtils = new HttpClientUtils();
        return httpUtils;
    }

    static {
        // 设置请求和传输超时时间
        requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
    }


    /**
     * post请求传输json参数
     *
     * @param url        url地址
     * @param jsonString 参数
     */
    public static String httpPostJson(String url, String jsonString) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String resultString = "";
        HttpPost httpPost = new HttpPost(url);
        // 设置请求和传输超时时间
        httpPost.setConfig(requestConfig);
        try {
            if (null != jsonString) {
                StringEntity entity = new StringEntity(jsonString, "UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                logger.info("response status:" + response.getStatusLine().getStatusCode());
                logger.info("response message:" + EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            logger.error("httpPostJson请求提交失败:" + url, e);
        } finally {
            httpPost.releaseConnection();
        }
        return resultString;
    }

    /**
     * put请求传输json参数
     *
     * @param url        url地址
     * @param jsonString 参数
     */
    public static String httpPutJson(String url, String jsonString) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String resultString = "";
        HttpPut httpPut = new HttpPut(url);
        // 设置请求和传输超时时间
        httpPut.setConfig(requestConfig);
        try {
            if (null != jsonString) {
                StringEntity entity = new StringEntity(jsonString, "UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPut.setEntity(entity);
            }
            CloseableHttpResponse response = httpClient.execute(httpPut);
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                logger.info("response status:" + response.getStatusLine().getStatusCode());
                logger.info("response message:" + EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            logger.error("httpPutJson请求提交失败:" + url, e);
        } finally {
            httpPut.releaseConnection();
        }
        return resultString;
    }

    /**
     * post请求传输String参数 例如：name=Jack&sex=1&type=2
     * Content-type:application/x-www-form-urlencoded
     *
     * @param url      url地址
     * @param strParam 参数
     */
    public static String httpPost(String url, String strParam) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String resultString = "";
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        try {
            if (null != strParam) {
                // 解决中文乱码问题
                StringEntity entity = new StringEntity(strParam, "UTF-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/x-www-form-urlencoded");
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                logger.info("response status:" + response.getStatusLine().getStatusCode());
                logger.info("response message:" + EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            logger.error("httpPost请求提交失败:" + url, e);
        } finally {
            httpPost.releaseConnection();
        }
        return resultString;
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     */
    public static String httpGet(String url) {
        // get请求返回结果
        String resultString = "";
        CloseableHttpClient client = HttpClients.createDefault();
        // 发送get请求
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(request);
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                // logger.info("response status:" + response.getStatusLine().getStatusCode());
                ///logger.info("response message:" + EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            // logger.error("httpGet请求提交失败:" + url, e);
        } finally {
            request.releaseConnection();
        }
        return resultString;
    }

    /**
     * 发送delete请求
     *
     * @param url 路径
     */
    public static String httpDelete(String url) {
        // get请求返回结果
        String resultString = "";
        CloseableHttpClient client = HttpClients.createDefault();
        // 发送get请求
        HttpDelete httpDelete = new HttpDelete(url);
        httpDelete.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = client.execute(httpDelete);
            // 请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                // 读取服务器返回过来的json字符串数据
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            } else {
                logger.info("response status:" + response.getStatusLine().getStatusCode());
                logger.info("response message:" + EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            logger.error("httpDelete请求提交失败:" + url, e);
        } finally {
            httpDelete.releaseConnection();
        }
        return resultString;
    }

    /**
     * 执行get请求,返回doc
     *
     * @param url
     * @return
     * @throws Exception
     */
    public Document executeGetAsDocument(String url) throws Exception {
        String html = httpGet(url).replace("\r", "").replace("\n", "");
        return Jsoup.parse(html);
    }


    public static void main(String[] args) {

//        String url = "http://192.168.121.41:9200/_snapshot/isec_backup/*/";
//        String result = HttpClientUtils.httpGet(url);
//        System.out.println(result);

//        String url = "http://192.168.121.41:9200/_snapshot/isec_backup/day_*/";
//        String result = HttpClientUtils.httpGet(url);
//        System.out.println(result);


//        String url = "http://192.168.121.41:9200/_snapshot/isec_backup/day_snapshot_01-jg-user_connection2-201808091505_222";
//        String params = "{" +
//                "\"indices\": \"01-jg-user_connection2-201808091505\"," +
//                "\"ignore_unavailable\": true," +
//                "\"include_global_state\": false" +
//            "}";
//        String result = HttpClientUtils.httpPutJson(url, params);
//        System.out.println(result);

//        String url2 = "http://192.168.121.41:9200/_snapshot/isec_backup/day_snapshot_03-lm-tag_from_to-201808071635_333";
//        String params2= "{" +
//                "\"indices\": \"03-lm-tag_from_to-201808071635\"," +
//                "\"ignore_unavailable\": true," +
//                "\"include_global_state\": false" +
//                "}";
//        String result2 = HttpClientUtils.httpPutJson(url2, params2);
//        System.out.println(result2);

//        String url = "http://192.168.121.41:9200/_snapshot/isec_backup/01-lm-user_register2-201808091506_333";
//        String result = HttpClientUtils.httpDelete(url);
//        System.out.println(result);

    }

}