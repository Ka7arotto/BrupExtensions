package VulTool;

import burp.api.montoya.http.message.HttpRequestResponse;

import java.util.ArrayList;

public class TableData {
    private int id;
    private String url;
    private int status;
    private String XssId;
    private String XssParam;
    private HttpRequestResponse httpRequestResponse;



    public TableData(int id, String url, int status, HttpRequestResponse httpRequestResponse) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.XssId = null;
        this.XssParam = null;
        this.httpRequestResponse=httpRequestResponse;
    }
    public TableData(int id, String url, int status, String xssId, String xssParam, HttpRequestResponse httpRequestResponse) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.XssId = xssId;
        this.XssParam = xssParam;
        this.httpRequestResponse=httpRequestResponse;
    }
    public HttpRequestResponse getHttpRequestResponse() {
        return httpRequestResponse;
    }

    public void setHttpRequestResponse(HttpRequestResponse httpRequestResponse) {
        this.httpRequestResponse = httpRequestResponse;
    }

    public void setXssParam(String xssParam) {
        XssParam = xssParam;
    }

    public void setXssId(String xssId) {
        XssId = xssId;
    }
    public int getId() {
        return id;
    }


    public String getUrl() {
        return url;
    }

    public int getStatus() {
        return status;
    }

    public String getXssId() {
        return XssId;
    }

    public String getXssParam() {
        return XssParam;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
