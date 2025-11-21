package VulTool;
import burp.api.montoya.MontoyaApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSRFDetector {
    private List<String> sensitiveKeywords;
    private MontoyaApi api;
    // 默认敏感关键词
    private static final List<String> DEFAULT_KEYWORDS = Arrays.asList(
            "url", "uri", "link", "path", "file", "load", "redirect",
            "forward", "proxy", "api", "endpoint", "service", "callback",
            "domain", "host", "address", "dns", "ip", "dest", "target",
            "request", "image", "picture", "src", "location", "next",
            "return", "goto", "page", "site", "web", "http", "https"
    );

    public SSRFDetector(MontoyaApi montoyaApi) {
        this.sensitiveKeywords = new ArrayList<>(DEFAULT_KEYWORDS);
        this.api=montoyaApi;
    }

    public void addKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            String normalized = keyword.toLowerCase().trim();
            if (!sensitiveKeywords.contains(normalized)) {
                sensitiveKeywords.add(normalized);
                api.logging().logToOutput("添加SSRF关键词: " + normalized);
            }
        }
    }
    // 批量添加关键词
    public void addKeywords(List<String> keywords) {
        if (keywords != null) {
            for (String keyword : keywords) {
                addKeyword(keyword);
            }
            api.logging().logToOutput("批量添加SSRF关键词: " + keywords.size() + " 个");
        }
    }
    // 删除关键词
    public void removeKeyword(String keyword) {
        if (keyword != null) {
            String normalized = keyword.toLowerCase().trim();
            if (sensitiveKeywords.remove(normalized)) {
                api.logging().logToOutput("删除SSRF关键词: " + normalized);
            }
        }
    }
    public void clear(){
        sensitiveKeywords.clear();
    }
    public List<String> getParams(){
        return sensitiveKeywords;
    }

}