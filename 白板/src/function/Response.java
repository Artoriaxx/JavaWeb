package function;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Response implements Serializable {
    private static final long serialVersionUID = -8541802142240670120L;

    private String type;
    private Map<String, Object> dataMap;


    public Response() {
        this.dataMap = new HashMap<String, Object>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void insertDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public Object getData(String type) {
        return this.dataMap.get(type);
    }
    public void insertData(String type, Object data) {
        this.dataMap.put(type, data);
    }

}
