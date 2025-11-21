import VulTool.SecretScan;
import VulTool.TableData;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MyHttpHandler implements HttpHandler {
    private final MyTableModel tableModel;
    private final MontoyaApi montoyaApi;
    private final AtomicInteger id = new AtomicInteger(1);  //原子类

    public MyHttpHandler(MyTableModel tableModel, MontoyaApi api) {
        this.tableModel = tableModel;
        this.montoyaApi = api;

    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {
        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) throws InterruptedException {
        if (MyFilterRequest.fromProxy(responseReceived) || MyFilterRequest.fromRepeater(responseReceived)) {
            SecretCheck(responseReceived);
            CorsCheck(responseReceived);
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
    private  void CorsCheck(HttpResponseReceived responseReceived) {
            HttpRequest corsHttpRequest = responseReceived.initiatingRequest();
            HttpRequestResponse httpRequestResponse= HttpRequestResponse.httpRequestResponse(corsHttpRequest, responseReceived);

            if (responseReceived.statusCode() == 200 && responseReceived.hasHeader("Access-Control-Allow-Origin", "null") &&(responseReceived.hasHeader("Access-Control-Allow-Credentials", "true"))) {
                TableData tableData=new TableData(id.getAndIncrement(),corsHttpRequest.url(),responseReceived.statusCode(),responseReceived.bodyToString().length(),"Cors Vul",httpRequestResponse);
                tableModel.add(tableData);
                return;
            }
        if (responseReceived.statusCode() == 200 && responseReceived.hasHeader("Access-Control-Allow-Origin", "*") &&(responseReceived.hasHeader("Access-Control-Allow-Credentials", "true"))) {
            TableData tableData=new TableData(id.getAndIncrement(),corsHttpRequest.url(),responseReceived.statusCode(),responseReceived.bodyToString().length(),"Cors Vul",httpRequestResponse);
            tableModel.add(tableData);
            return;
        }

            HttpRequest corsHttpRequest2 = responseReceived.initiatingRequest().withHeader("Origin", "https://www.baidu.com");
            HttpResponse corsResponse2 = montoyaApi.http().sendRequest(corsHttpRequest).response();
            HttpRequestResponse httpRequestResponse2= HttpRequestResponse.httpRequestResponse(corsHttpRequest2, corsResponse2);

        if (corsResponse2.statusCode() == 200 && corsResponse2.hasHeader("Access-Control-Allow-Origin", "https://www.baidu.com") &&(corsResponse2.hasHeader("Access-Control-Allow-Credentials", "true"))) {
                TableData tableData=new TableData(id.getAndIncrement(),corsHttpRequest2.url(),corsResponse2.statusCode(),corsResponse2.bodyToString().length(),"Cors Vul",httpRequestResponse2);
                tableModel.add(tableData);
        }

    }
    private void SecretCheck(HttpResponseReceived responseReceived) {
        HttpRequest request = responseReceived.initiatingRequest();
        SecretScan secretScan= new SecretScan();
        HttpResponse response = montoyaApi.http().sendRequest(request).response();
        String responseBody = response.bodyToString();
        List<String> result = secretScan.startScan(responseBody,montoyaApi);
        montoyaApi.logging().logToOutput(result.toString());
        HttpRequestResponse httpRequestResponse= HttpRequestResponse.httpRequestResponse(request, response);
        if (result != null && !result.isEmpty()) {
            for (String sensitiveInfo : result) {
                TableData tableData=new TableData(id.getAndIncrement(),request.url(),response.statusCode(),response.bodyToString().length(),"Find Secret:"+sensitiveInfo,httpRequestResponse);
                tableModel.add(tableData);
            }
        }
    }

}
