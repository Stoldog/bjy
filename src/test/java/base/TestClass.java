package base;

import base.pojo.CommonReturn;
import base.service.BaseApiService;
import base.type.HttpMethod;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

public class TestClass {

    public void testGetPartnerKey(){
        String str = BaseApiService.getInstance().getPartnerKey(54124857,"e142b83823811ac3e4c714fcc8f4de45",0);
        System.out.println(str);
    }


    public void testGetSign(){
        LinkedHashMap params = new LinkedHashMap();
        params.put("partner_id",54124857);
        params.put("title","测试教室");
        params.put("start_time",1501575608);
        params.put("end_time",1501579208);
        params.put("type",2);
        String partnerKey = BaseApiService.getInstance().getPartnerKey(54124857,"e142b83823811ac3e4c714fcc8f4de45",0);
        //String str = baseService.getSign(partnerKey,params);
        //System.out.println(str);
    }


    public void testCallHttpsApi() throws Exception {
        LinkedHashMap<String,Object> params = new LinkedHashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String apiUrl = "openapi/room/create";
        params.put("title","测试开启直播间");
        long startTime =  format.parse("2018-03-29 17:38:00").getTime()/1000;
        long endTime =  format.parse("2018-03-29 19:10:00").getTime()/1000;
        params.put("start_time",(int)startTime);
        params.put("end_time",(int)endTime);
        CommonReturn commonReturn = BaseApiService.getInstance().callHttpsApi(HttpMethod.POST,apiUrl,params);
        System.out.println(commonReturn);
        System.out.println(commonReturn.getCode());
        System.out.println(commonReturn.getMsg());
        System.out.println(commonReturn.getData());
    }
    @Test
    public void testGetProperties() throws IOException {
        //baseService.readProperties();
    }
}
