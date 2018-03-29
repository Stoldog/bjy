package base.service;

import base.pojo.CommonReturn;
import base.type.HttpMethod;
import base.type.ReturnCode;
import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import utils.HttpClientUtils;

import java.security.MessageDigest;
import java.util.*;
/**
 * Created by EduHzy-019 on 2018-03-29.
 *  调用百家云API的基本Service
 */
public abstract class BaseService {
    public static final String BJY_API_HOST = "https://api.baijiayun.com/";

    //日志
    private static Log logger = LogFactory.getLog(BaseService.class);

    /**
     * 统一调用百家云的相关api（params里面需要包含公共参数）
     * @param method
     * @param apiUrl
     * @param params
     * @return
     */
    public CommonReturn callHttpsApi(HttpMethod method, String apiUrl, LinkedHashMap<String,Object> params) throws Exception {
        //检查参数
        if(apiUrl == null || apiUrl == "" || params == null || HttpMethod.getByCode(method.typeCode).getTypeCode()==-1){
            throw new Exception("调用API的参数错误！请检查参数！");
        }
        CommonReturn commonReturn = new CommonReturn();
        int partnerId = 54124857;
        String secretKey = "e142b83823811ac3e4c714fcc8f4de45";
        //设置公共参数
        params.put("partner_id",partnerId);
        long time = new Date().getTime()/1000;
        params.put("timestamp",(int)time);
        //计算sign
        String sign = getSign(getPartnerKey(partnerId,secretKey,0),params);
        params.put("sign",sign);
        if(HttpMethod.GET == method){

        }else if(HttpMethod.POST == method){
            String jsonStr = HttpClientUtils.doPostSSL(BJY_API_HOST+apiUrl,params);
            if(jsonStr != "" && jsonStr != null){
                commonReturn = JSON.parseObject(jsonStr,CommonReturn.class);
            }
        }else {

        }
        return commonReturn;
    }

    /**
     * 获取/重置partner_key
     * @param partner_id 百家云分发给合作方ID：  partner_id
     * @param secret_key 百家云分发给合作方密钥  secret_key
     * @param regenerate 默认为0 为1时表示强制重新生成partner_key（默认情况下返回当前已经存在的partner_key）
     * @return
     */
    public String getPartnerKey(int partner_id,String secret_key,int regenerate){
        //请求路径
        StringBuilder sendUrl = new StringBuilder(BJY_API_HOST);
        sendUrl.append("openapi/partner/createkey");
        //提交的参数
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("partner_id",partner_id);
        params.put("secret_key",secret_key);
        params.put("regenerate",regenerate);
        //发送Https请求
        String jsonStr = HttpClientUtils.doPostSSL(sendUrl.toString(),params);
        CommonReturn commonReturn = JSON.parseObject(jsonStr,CommonReturn.class);
        //请求成功
        if(commonReturn != null){
            if(ReturnCode.getByCode(commonReturn.getCode())==ReturnCode.Success){
                Map<String,Object> dataMap = (Map<String, Object>) JSON.parse(commonReturn.getData());
                return dataMap.get("partner_key").toString();
            }
            //如果是其他的问题
            logger.error(commonReturn.getMsg());
            throw new RuntimeException("ReturnMessage: "+commonReturn.getMsg());
        }
        throw new RuntimeException("请求出错！");
    }

    /**
     * 获取签名sign，规则如下：
     *      1.将请求参数按key字典顺序(ASCII值大小)升序排序
     *      2.将排好序的参数拼成 key1=value1&key2=value2&...&keyN=valueN
     *      3.将以上拼好的串后面再拼上 &partner_key=<partner_key> ，其中 <partner_key> 替换成具体值
     *      4.对以上拼好的串算一个32位md5值（小写），即得到了签名
     *      5.partner_key总是拼在字符串最后面，并不参与key的排序
     *
     * @param params 调用API请求所需要的参数
     * @return
     */
    private String getSign(String partnerKey,LinkedHashMap<String,Object> params){
        //参数字符串
        StringBuilder sb = new StringBuilder();
        //参数集合
        List<String> keyList = new ArrayList<>();
        for (String s : params.keySet()) {
            keyList.add(s);
        }
        //默认进行ASCII码排序（从小至大）
        Collections.sort(keyList);
        //拼凑排序后的字符串
        for (String s : keyList) {
            sb.append(s).append("=").append(params.get(s)).append("&");
        }
        //将partner_key拼接在字符串最后
        sb.append("partner_key=").append(partnerKey);
        //进行md5加密，生成32位md5值（小写）
        return MD5(sb.toString()).toLowerCase();
    }

    /**
     * MD5加密
     * @param s
     * @return
     */
    private String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
            StringBuilder ret = new StringBuilder(bytes.length * 2);
            for (int i=0; i<bytes.length; i++) {
                ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
                ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
            }
            return ret.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
