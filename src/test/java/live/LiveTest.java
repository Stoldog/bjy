package live;

import base.pojo.CommonReturn;
import live.service.LiveApiService;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

/**
 * Created by EduHzy-019 on 2018-03-30.
 */
public class LiveTest {
    LiveApiService liveApiService = new LiveApiService();


    public void testCreateRoom() throws Exception {
        LinkedHashMap<String,Object> params = new LinkedHashMap();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        params.put("title","测试CreateRoom开启直播间");
        long startTime =  format.parse("2018-03-30 15:48:00").getTime()/1000;
        long endTime =  format.parse("2018-03-30 23:59:59").getTime()/1000;
        params.put("start_time",(int)startTime);
        params.put("end_time",(int)endTime);
        params.put("template_name","triple");
        CommonReturn commonReturn = liveApiService.create(params);
        System.out.println(commonReturn);
        System.out.println(commonReturn.getCode());
        System.out.println(commonReturn.getMsg());
        System.out.println(commonReturn.getData());
    }

    public void testUpdateRoom() throws Exception {
        LinkedHashMap<String,Object> params = new LinkedHashMap();
        params.put("room_id",18033085677417L);
        params.put("title","测试UpdateRoom更新直播间");
        CommonReturn commonReturn = liveApiService.update(params);
        System.out.println(commonReturn);
        System.out.println(commonReturn.getCode());
        System.out.println(commonReturn.getMsg());
        System.out.println(commonReturn.getData());
    }


    public void testGetProperties(){
        String s = liveApiService.properties.getProperty("bjy.live.api.list.params");
        System.out.println(s);
        System.out.println(liveApiService.properties.getProperty("bjy.live.api.list.url"));
    }

    @Test
    public void testRequestApiByProperties() throws Exception {
        LinkedHashMap<String,Object> params = new LinkedHashMap();
        CommonReturn commonReturn = liveApiService.requestApiByProperties("list",params);
        System.out.println(commonReturn);
        System.out.println(commonReturn.getCode());
        System.out.println(commonReturn.getMsg());
        System.out.println(commonReturn.getData());
    }
}
