package base.pojo;

/**
 * Created by EduHzy-019 on 2018-03-29.
 */
public class CommonReturn {
    private int code;
    private String data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CommonReturn{" +
                "code=" + code +
                ", data='" + data + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
