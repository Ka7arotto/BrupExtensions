import VulTool.SpringBootScan;
import VulTool.TableData;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

import java.util.HashMap;
import java.util.Map;
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
            detectSpringBootVulnerability(responseReceived);
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }

    private void detectSpringBootVulnerability(HttpResponseReceived responseReceived) throws InterruptedException {
        HttpRequest httpRequest = responseReceived.initiatingRequest();
        try {
            String baseUrl = "";
            SpringBootScan springBootScan = new SpringBootScan(baseUrl, httpRequest, montoyaApi);
            boolean checkSpring = springBootScan.checkSpring();
            if (checkSpring) {
                HashMap<HttpRequest, HttpResponse> hashMap = springBootScan.PathScan();
                if (hashMap != null && !hashMap.isEmpty()) {
                    for (Map.Entry<HttpRequest, HttpResponse> entry : hashMap.entrySet()) {
                        HttpRequest request = entry.getKey();
                        HttpResponse response = entry.getValue();
                        TableData tableData = new TableData(id.getAndIncrement(), request.url(), response.statusCode(), response.body().length(), "Find Path!", HttpRequestResponse.httpRequestResponse(request, response));
                        tableModel.add(tableData);
                    }

                }

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
