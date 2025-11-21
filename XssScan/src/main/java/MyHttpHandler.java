import Utils.HashMapRequest;
import VulTool.PayloadConfig;
import VulTool.TableData;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.HttpParameterType;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MyHttpHandler implements HttpHandler {
    private final MyTableModel tableModel;
    private final MontoyaApi api;
    public static AtomicInteger id = new AtomicInteger(1);
    private final HashMapRequest hashMapRequest=new HashMapRequest();
    public static boolean isOpen = false;
    // 可能包含XSS的Content-Type类型
    private static final String[] XSS_CONTENT_TYPES = {
            "text/html",
            "text/javascript",
            "application/javascript",
    };


    public MyHttpHandler(MyTableModel tableModel, MontoyaApi api) {
        this.tableModel = tableModel;
        this.api = api;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {

        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        // 开始检测xss
        if (isOpen && isResponseContentTypeVulnerable(responseReceived)) {
            if (MyFilterRequest.fromRepeater(responseReceived) || MyFilterRequest.fromProxy(responseReceived)) {
                ChechStoredXss(responseReceived);
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }
    // 判断响应格式是否可能包含XSS
    public static boolean isResponseContentTypeVulnerable(HttpResponse response) {
        if (response == null) {
            return false;
        }

        // 获取Content-Type头部
        String contentType = response.headerValue("Content-Type");
        if (contentType == null) {
            return false;
        }

        // 转换为小写便于比较
        contentType = contentType.toLowerCase();

        // 检查是否为HTML、XML、JavaScript等可能包含XSS的类型
        for (String vulnerableType : XSS_CONTENT_TYPES) {
            if (contentType.contains(vulnerableType)) {
                return true;
            }
        }

        return false;
    }
    private void ChechStoredXss(HttpResponseReceived responseReceived) {
        HttpRequest httpRequest = responseReceived.initiatingRequest();
        // 获取所有参数，然后过滤出GET和POST参数
        List<ParsedHttpParameter> allParams = httpRequest.parameters();
        List<ParsedHttpParameter> getFilterParams = allParams.stream()
                .filter(param -> param.type() == HttpParameterType.URL || param.type() == HttpParameterType.BODY||param.type() == HttpParameterType.JSON)
                .collect(Collectors.toList());

        for (HttpParameter param : getFilterParams) {
            TestXssPayload(httpRequest,param);
        }

    }

    private void TestXssPayload(HttpRequest originalRequest, HttpParameter param) {
        try {
            Integer recordId = id.getAndIncrement();
            String xssTag = hashMapRequest.addRequestHashMap(recordId, param.name());

            HttpParameter testParam = HttpParameter.parameter(
                    param.name(),
                    xssTag,
                    param.type()
            );

            HttpRequest testRequest = originalRequest.withParameter(testParam);
            HttpResponse response = api.http().sendRequest(testRequest).response();
            HttpRequestResponse httpRequestResponse = HttpRequestResponse.httpRequestResponse(testRequest, response);

            boolean isXssFound = false;
            HashMapRequest.IdwithParam foundEntry = null;

            // 检查响应中是否包含任何XSS标记
            for (Map.Entry<String, HashMapRequest.IdwithParam> entry : hashMapRequest.hashmapStrWithReq.entrySet()) {
                if (response.bodyToString().contains(entry.getKey())) {
                    isXssFound = true;
                    foundEntry = entry.getValue();
                    break;
                }
            }

            // 创建表格数据
            TableData data;
            if (isXssFound && foundEntry != null) {
                data = new TableData(recordId, testRequest.url(), response.statusCode(),
                        String.valueOf(foundEntry.requestId), foundEntry.param, httpRequestResponse);
                api.logging().logToOutput("XSS风险! ID: " + recordId + ", Param: " + foundEntry.param);
            } else {
                data = new TableData(recordId, testRequest.url(), response.statusCode(), httpRequestResponse);
            }

            tableModel.add(data);

        } catch (Exception e) {
            api.logging().logToError("Error in TestXssPayload: " + e.getMessage());
        }
    }

}
