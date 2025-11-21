package Utils;
import java.util.HashMap;

public class HashMapRequest {
    public static HashMap<String,IdwithParam> hashmapStrWithReq=new HashMap<String, IdwithParam>();

    public static class IdwithParam{
            public int requestId;
            public String param;

            public IdwithParam(int requestId, String param) {

                this.requestId = requestId;
                this.param = param;
        }
    }
    public String addRequestHashMap(Integer requestId, String param){
        String xssTag=generateRandomXssTag();
        hashmapStrWithReq.put(xssTag,new IdwithParam(requestId,param));
        return  xssTag;
    }

    private String generateRandomXssTag() {
        return "2004"+ System.currentTimeMillis();
    }

}
