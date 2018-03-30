package live.service;

import base.pojo.CommonReturn;
import base.service.BaseApiService;
import base.type.HttpMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Properties;

/**
 * Created by EduHzy-019 on 2018-03-30.
 */
public class LiveApiService {
    //配置文件
    public Properties properties = new Properties();
    //日志
    private static Log logger = LogFactory.getLog(LiveApiService.class);

    public LiveApiService(){
        File file = null;
        try {
            file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX+ "bjy_live_cfg.properties");
        } catch (FileNotFoundException e) {
            //未找到文件不抛出异常
            logger.info("bjy_live_cfg.properties配置文件未找到！使用默认直播配置文件");
        }
        try {
            if(file == null){
               file = ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX+ "bjy_def_live_cfg.properties");
            }
            //如果配置文件为空，使用默认的文件配置
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf-8");
            properties.load(inputStreamReader);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * API 2 : 创建房间
     * @return
     * @throws Exception
     */
    public CommonReturn create(LinkedHashMap<String,Object> params) throws Exception {
        //检查必填参数
        if(checkRequiredParams("create",params)){
            throw new RuntimeException("必填参数不可为空！");
        }
        int startTime = (int) params.get("start_time");
        int endTime = (int) params.get("end_time");

        //判断start_time和end_time是否为Unix时间戳（秒）
        if(isTimestampToSec(startTime) || isTimestampToSec(endTime)){
            throw new RuntimeException("start_time 或 end_time不是Unix时间戳（秒）！");
        }
        return BaseApiService.getInstance().callHttpsApi(HttpMethod.POST,"openapi/room/create",params);
    }

    /**
     * API 3 : 更新房间信息
     * @param params
     * @return
     */
    public CommonReturn update(LinkedHashMap<String,Object> params) throws Exception {
        //检查必填参数
        if(checkRequiredParams("update",params)){
            throw new RuntimeException("必填参数不可为空！");
        }
        return BaseApiService.getInstance().callHttpsApi(HttpMethod.POST,"https://api.baijiayun.com/openapi/room/update",params);
    }

    /**
     * API 4 : 删除房间
     * @param params
     * @return
     * @throws Exception
     */
    public CommonReturn delete(LinkedHashMap<String,Object> params) throws Exception {
        //检查必填参数
        if(checkRequiredParams("delete",params)){
            throw new RuntimeException("必填参数不可为空！");
        }
        return BaseApiService.getInstance().callHttpsApi(HttpMethod.POST,"https://api.baijiayun.com/openapi/room/delete",params);
    }

    /**
     * API 5 : 获取房间信息
     * @param params
     * @return
     * @throws Exception
     */
    public CommonReturn info(LinkedHashMap<String,Object> params) throws Exception {
        //检查必填参数
        if(checkRequiredParams("info",params)){
            throw new RuntimeException("必填参数不可为空！");
        }
        return BaseApiService.getInstance().callHttpsApi(HttpMethod.POST,"https://api.baijiayun.com/openapi/room/info",params);
    }

    /**
     * 通用请求百家云API方法，使用配置文件内的API信息，此方法不会做具体的api特殊规则的检查
     * @param apiName 在配置文件中api的name 如：bjy.live.api.xxx.method bjy.live.api.xxx.url    xxx 是即为apiName
     * @param params 请求的参数
     * @return
     */
    public CommonReturn requestApiByProperties(String apiName ,LinkedHashMap<String,Object> params) throws Exception {
        //获取配置文件中api参数的前缀
        String propertiesPrefix = "bjy.live.api.";
        //检查必填参数
        if(checkRequiredParams(apiName,params)){
            throw new RuntimeException("必填参数不可为空！");
        }
        HttpMethod httpMethod = HttpMethod.getByName(properties.getProperty(propertiesPrefix + apiName + ".method"));
        String url = properties.getProperty(propertiesPrefix + apiName + ".url");
        return BaseApiService.getInstance().callHttpsApi(httpMethod,url,params);
    }

    /**
     *  检查是否unix时间戳（秒）
     */
    private boolean isTimestampToSec(int timestamp){
        return timestamp > 9999999999L? true:false;
    }

    /**
     * 检查必填参数是否已填入
     * @param apiName
     * @param linkedHashMap
     * @return
     */
    private boolean checkRequiredParams(String apiName,LinkedHashMap<String,Object> linkedHashMap){
        //获取以逗号分隔的api必填参数
        String propertiesPrefix = "bjy.live.api.";
        String params = properties.getProperty(propertiesPrefix + apiName + ".params");
        //无除开必填参数以外的参数返回真
        if(params == ""){
            return true;
        }
        String paramsArr[] = params.split(",");
        //判断是否存在
        for (String s : paramsArr) {
            if(linkedHashMap.get(s) == null){
                return false;
            }
        }
        return true;
    }

}
