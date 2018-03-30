package base.service;

import java.io.IOException;

/**
 * Created by EduHzy-019 on 2018-03-30.
 */
public class BaseApiService extends AbstractBaseApiService{
    /**
     * 私有化构造方法
     */
    private BaseApiService(){
        //调用获取初始化百家云开发方参数的方法
        try {
            readProperties();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("初始化参数失败，请检查是否添加配置文件或配置参数是否正常！",e);
        }
    }

    /**
     * 内部静态类的单例模式
     */
    private static class BaseApiServiceHolder{
        private static BaseApiService baseApiService = new BaseApiService();
    }

    /**
     * 获取实例的方法
     * @return
     */
    public static BaseApiService getInstance(){
        return BaseApiServiceHolder.baseApiService;
    }
}
