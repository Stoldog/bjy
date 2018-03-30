package utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils{
    private HttpClientUtils(){}

    private static Log logger = LogFactory.getLog(HttpClientUtils.class);


    /**
     * 发送HTTP_GET请求
     * 该方法会自动关闭连接,释放资源
     * @param reqURL    请求地址(含参数)
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String doGet(String reqURL, String decodeCharset){
        long responseLength = 0;       //响应长度
        String responseContent = null; //响应内容
        HttpClient httpClient = new DefaultHttpClient(); //创建默认的httpClient实例
        HttpGet httpGet = new HttpGet(reqURL);           //创建org.apache.http.client.methods.HttpGet
        try{
            HttpResponse response = httpClient.execute(httpGet); //执行GET请求
            HttpEntity entity = response.getEntity();            //获取响应实体
            if(null != entity){
                responseLength = entity.getContentLength();
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity); //Consume response content
            }
            System.out.println("请求地址: " + httpGet.getURI());
            System.out.println("响应状态: " + response.getStatusLine());
            System.out.println("响应长度: " + responseLength);
            System.out.println("响应内容: " + responseContent);
        }catch(ClientProtocolException e){
            logger.debug("该异常通常是协议错误导致,比如构造HttpGet对象时传入的协议不对(将'http'写成'htp')或者服务器端返回的内容不符合HTTP协议要求等,堆栈信息如下", e);
        }catch(ParseException e){
            logger.debug(e.getMessage(), e);
        }catch(IOException e){
            logger.debug("该异常通常是网络原因引起的,如HTTP服务器未启动等,堆栈信息如下", e);
        }finally{
            httpClient.getConnectionManager().shutdown(); //关闭连接,释放资源
        }
        return responseContent;
    }

    public static String doGetSSL(String reqURL,Map<String,Object> params){
        return doGetSSL(reqURL,params,null);
    }

    public static String doGetSSL(String reqURL,Map<String,Object> params,String decodeCharset){
        String responseContent = "";
        HttpClient httpClient = new DefaultHttpClient();
        X509TrustManager xtm = new X509TrustManager(){
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {return null;}
        };
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{xtm}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));
            StringBuilder sb = new StringBuilder(reqURL+"?");
            ArrayList<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
            for (Map.Entry<String, Object> strEntry : params.entrySet()) {
                data.add(new BasicNameValuePair(strEntry.getKey(), (String) strEntry.getValue()));
            }
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(data, HTTP.UTF_8);
            HttpGet httpGet = new HttpGet(reqURL+ urlEncodedFormEntity.toString());
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            logger.debug("与[" + reqURL + "]通信过程中发生异常,堆栈信息为", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }


    /**
     * 发送HTTP_POST请求
     * 该方法为<code>sendPostRequest(String,String,boolean,String,String)</code>的简化方法
     * 该方法在对请求数据的编码和响应数据的解码时,所采用的字符集均为UTF-8
     * 当<code>isEncoder=true</code>时,其会自动对<code>sendData</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,"UTF-8")</code>
     * @param isEncoder 用于指明请求数据是否需要UTF-8编码,true为需要
     */
    public static String doPost(String reqURL, String sendData, boolean isEncoder){
        return doPost(reqURL, sendData, isEncoder, null, null);
    }


    /**
     * 发送HTTP_POST请求
     * 该方法会自动关闭连接,释放资源
     * 当<code>isEncoder=true</code>时,其会自动对<code>sendData</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
     * @param reqURL        请求地址
     * @param sendData      请求参数,若有多个参数则应拼接成param11=value11&22=value22&33=value33的形式后,传入该参数中
     * @param isEncoder     请求数据是否需要encodeCharset编码,true为需要
     * @param encodeCharset 编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String doPost(String reqURL, String sendData, boolean isEncoder, String encodeCharset, String decodeCharset){
        String responseContent = null;
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(reqURL);
        //httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
        try{
            if(isEncoder){
                List formParams = new ArrayList();
                for(String str : sendData.split("&")){
                    formParams.add(new BasicNameValuePair(str.substring(0,str.indexOf("=")), str.substring(str.indexOf("=")+1)));
                }
                httpPost.setEntity(new StringEntity(URLEncodedUtils.format(formParams, encodeCharset==null ? "UTF-8" : encodeCharset)));
            }else{
                httpPost.setEntity(new StringEntity(sendData));
            }

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        }catch(Exception e){
            logger.debug("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
        }finally{
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }


    /**
     * 发送HTTP_POST请求
     * 该方法会自动关闭连接,释放资源
     * 该方法会自动对<code>params</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
     * @param reqURL        请求地址
     * @param params        请求参数
     * @param encodeCharset 编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String doPost(String reqURL, Map<String,Object> params, String encodeCharset, String decodeCharset){
        String responseContent = null;
        HttpClient httpClient = new DefaultHttpClient();

        HttpPost httpPost = new HttpPost(reqURL);
        List formParams = new ArrayList(); //创建参数队列
        for(Map.Entry<String,Object> entry : params.entrySet()){
            formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        try{
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset==null ? "UTF-8" : encodeCharset));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        }catch(Exception e){
            logger.debug("与[" + reqURL + "]通信过程中发生异常,堆栈信息如下", e);
        }finally{
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }


    /**
     * 发送HTTPS_POST请求
     * 该方法为<code>sendPostSSLRequest(String,Map,String,String)</code>方法的简化方法
     * 该方法在对请求数据的编码和响应数据的解码时,所采用的字符集均为UTF-8
     * 该方法会自动对<code>params</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,"UTF-8")</code>
     */
    public static String doPostSSL(String reqURL, Map params){
        return doPostSSL(reqURL, params, null, null);
    }


    /**
     * 发送HTTPS_POST请求
     * 该方法会自动关闭连接,释放资源
     * 该方法会自动对<code>params</code>中的[中文][|][ ]等特殊字符进行<code>URLEncoder.encode(string,encodeCharset)</code>
     * @param reqURL        请求地址
     * @param params        请求参数
     * @param encodeCharset 编码字符集,编码请求数据时用之,其为null时默认采用UTF-8解码
     * @param decodeCharset 解码字符集,解析响应数据时用之,其为null时默认采用UTF-8解码
     * @return 远程主机响应正文
     */
    public static String doPostSSL(String reqURL, Map<String,Object> params, String encodeCharset, String decodeCharset){
        String responseContent = "";
        HttpClient httpClient = new DefaultHttpClient();
        X509TrustManager xtm = new X509TrustManager(){
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
            public X509Certificate[] getAcceptedIssuers() {return null;}
        };
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, new TrustManager[]{xtm}, null);
            SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
            httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

            HttpPost httpPost = new HttpPost(reqURL);
            List formParams = new ArrayList();
            for(Map.Entry entry : params.entrySet()){
                formParams.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(formParams, encodeCharset==null ? "UTF-8" : encodeCharset));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset==null ? "UTF-8" : decodeCharset);
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            logger.debug("与[" + reqURL + "]通信过程中发生异常,堆栈信息为", e);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return responseContent;
    }

    /**
     * 发送HTTP_POST请求,json格式数据
     * @param url
     * @param body
     * @return
     * @throws Exception
     */
    public static String sendPostByJson(String url, String body) throws Exception {
        CloseableHttpClient httpclient = HttpClients.custom().build();
        HttpPost post = null;
        String resData = null;
        CloseableHttpResponse result = null;
        try {
            post = new HttpPost(url);
            HttpEntity entity2 = new StringEntity(body, Consts.UTF_8);
            post.setConfig(RequestConfig.custom().setConnectTimeout(30000).setSocketTimeout(30000).build());
            post.setHeader("Content-Type", "application/json");
            post.setEntity(entity2);
            result = httpclient.execute(post);
            if (HttpStatus.SC_OK == result.getStatusLine().getStatusCode()) {
                resData = EntityUtils.toString(result.getEntity());
            }
        } finally {
            if (result != null) {
                result.close();
            }
            if (post != null) {
                post.releaseConnection();
            }
            httpclient.close();
        }
        return resData;
    }

}
