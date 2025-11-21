import VulTool.PayloadConfig;
import VulTool.TableData;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.params.HttpParameter;
import burp.api.montoya.http.message.params.ParsedHttpParameter;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import VulTool.SSRFDetector;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyHttpHandler implements HttpHandler {
    private final MyTableModel tableModel;
    private final MontoyaApi api;
    private final PayloadConfig payloadConfig;
    private final AtomicInteger id = new AtomicInteger(1);
    private final SSRFDetector ssrfDetector;
    public MyHttpHandler(MyTableModel tableModel, MontoyaApi api, PayloadConfig payloadConfig,SSRFDetector ssrfDetector) {
        this.tableModel = tableModel;
        this.api = api;
        this.payloadConfig = payloadConfig;
        this.ssrfDetector=ssrfDetector;
    }

    @Override
    public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {

        return RequestToBeSentAction.continueWith(requestToBeSent);
    }

    @Override
    public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
        // 检测SSRF
        if (payloadConfig.isEnableActiveScan()) {
            if (MyFilterRequest.fromRepeater(responseReceived) || MyFilterRequest.fromProxy(responseReceived)) {
                CheckSSRF(responseReceived);
            }
        }
        return ResponseReceivedAction.continueWith(responseReceived);
    }

    private void CheckSSRF(HttpResponseReceived responseReceived) {
        HttpRequest httpRequest = responseReceived.initiatingRequest();

        // 获取所有参数（包括URL、BODY、JSON等）
        List<ParsedHttpParameter> allParams = httpRequest.parameters();

        // 检查每个参数是否为SSRF敏感参数
        for (HttpParameter param : allParams) {
            api.logging().logToOutput("param: "+param);
            if (isSSRFSensitiveParameter(param.name())||checkUrl(param.value())) {
                // 为每个敏感参数生成测试请求
                for (String domain:payloadConfig.getAllPayloads()){
                    SSRFWithDNSLOG(httpRequest, param,domain);
                }

            }
        }

    }
    private boolean checkUrl(String value) {
        value=value.toLowerCase();
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        if (value.startsWith("http")){
            return true;
        }
        return false;
    }
    private void SSRFWithDNSLOG(HttpRequest originalRequest, HttpParameter vulnerableParam,String domain) {
        // 生成唯一的DNSLOG子域名
        String test = generateRandomSubdomain();
        String payload="";
        if (!domain.startsWith("http")) {
            payload = "http://" + domain ;

        }
        if (!payload.endsWith("/")){
            payload+="/";
        }
        payload+=test;
        try {
            // 创建新的参数，只修改值，保持其他属性不变
            HttpParameter testParam = HttpParameter.parameter(
                    vulnerableParam.name(),
                    payload,
                    vulnerableParam.type()
            );

            // 替换原始请求中的参数
            HttpRequest testRequest = originalRequest.withParameter(testParam);

            // 发送测试请求
            HttpResponse response = api.http().sendRequest(testRequest).response();
            HttpRequestResponse httpRequestResponse=HttpRequestResponse.httpRequestResponse(testRequest,response);
            // 记录到表格
            TableData data = new TableData(id.getAndIncrement(),testRequest.url(),response.statusCode(),response.body().length(),"Param: "+vulnerableParam.name(),httpRequestResponse);


            tableModel.add(data);

            api.logging().logToOutput("SSRF测试已发送: " + testRequest.url() +
                    " 参数: " + vulnerableParam.name() +
                    " 类型: " + vulnerableParam.type() +
                    " Payload: " + payload);

        } catch (Exception e) {
            api.logging().logToError("SSRF测试失败: " + e.getMessage());
        }
    }
    private String generateRandomSubdomain() {
        return "ssrf-"+ System.currentTimeMillis();
    }

    // 检测是否为SSRF相关参数名
    private boolean isSSRFSensitiveParameter(String paramName) {
        List<String> sensitiveKeywords=ssrfDetector.getParams();
        paramName = paramName.toLowerCase();
        for (String keyword : sensitiveKeywords) {
            keyword=keyword.toLowerCase();
            if (paramName.equals(keyword)) {
                return true;
            }
        }
        return false;
    }


}