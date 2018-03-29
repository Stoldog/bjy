package base.type;

public enum HttpMethod{
    GET(1,"GET请求"),
    POST(2,"POST请求"),
    ERROR(-1,"错误请求");

    public int typeCode;
    public String typeName;

    HttpMethod(int typeCode, String typeName){
        this.typeCode=typeCode;
        this.typeName=typeName;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public static HttpMethod getByCode(int typeCode){
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if(httpMethod.typeCode == typeCode){
                return httpMethod;
            }
        }
        return HttpMethod.ERROR;
    }

}
