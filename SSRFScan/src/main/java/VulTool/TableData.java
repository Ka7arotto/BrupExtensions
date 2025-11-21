package VulTool;

import burp.api.montoya.http.message.HttpRequestResponse;

public class TableData {
    private int id;
    private String url;
    private int status;
    private int length;
    private String Vulnerability;
    private HttpRequestResponse httpRequestResponse;


    public HttpRequestResponse getHttpRequestResponse() {
        return httpRequestResponse;
    }

    public void setHttpRequestResponse(HttpRequestResponse httpRequestResponse) {
        this.httpRequestResponse = httpRequestResponse;
    }

    public TableData(int id, String url, int status, int length, String vulnerability, HttpRequestResponse httpRequestResponse) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.length = length;
        this.Vulnerability = vulnerability;
        this.httpRequestResponse=httpRequestResponse;
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

    public int getLength() {
        return length;
    }

    public String getVulnerability() {
        return Vulnerability;
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

    public void setLength(int length) {
        this.length = length;
    }

    public void setVulnerability(String vulnerability) {
        Vulnerability = vulnerability;
    }
}
