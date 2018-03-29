package base.type;

/**
 * 以下code的枚举类定义是参照百家云的错误码定义，文档地址如下：
 *      http://dev.baijiayun.com/default/wiki/detail/11#h43
 */
public enum ReturnCode {
    Success(0,"成功"),
    GeneralError(1,"普通错误（没有明确code码的错误）"),
    UrlNotExsit(404,"请求路径不存在"),
    ParamError(1001,"参数错误"),
    SignCalculateError(1002,"签名计算错误"),
    AccountNotExsit(2001,"账号不存在"),
    AccountAuthorityError(2002,"账号权限错误"),
    ServiceError(2003,"直播账号未开或服务停止"),
    RoomNumberNotExsit(3001,"房间号不存在"),
    RoomHasBeenDeleted(3002,"该房间已删除"),
    FileUploadFaild(4001,"文件上传失败"),
    ERROR(-1,"无此Code,请检查数据")
    ;
    private int code;
    private String msg;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public static ReturnCode getByCode(int code){
        for (ReturnCode returnCode : ReturnCode.values()) {
            if(returnCode.getCode() == code){
                return returnCode;
            }
        }
        return ReturnCode.ERROR;
    }
}
