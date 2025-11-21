package VulTool;

import java.util.ArrayList;
import java.util.List;
import burp.api.montoya.MontoyaApi;

public class PayloadConfig {
    private List<String> urlPayloads;
    private List<String> dnsPayloads;
    private boolean enableActiveScan;
    private MontoyaApi api;
    public PayloadConfig(MontoyaApi api) {
        this.urlPayloads = new ArrayList<>();
        this.dnsPayloads = new ArrayList<>();
        this.enableActiveScan = false;
        this.api=api;

    }

    // Getters and Setters
    public List<String> getUrlPayloads() { return urlPayloads; }
    public List<String> getDnsPayloads() { return dnsPayloads; }
    public boolean isEnableActiveScan() { return enableActiveScan; }
    public void setEnableActiveScan(boolean enableActiveScan) { this.enableActiveScan = enableActiveScan; }

    public void addUrlPayload(String payload) {
        if (!payload.trim().isEmpty() && !urlPayloads.contains(payload)) {
            urlPayloads.add(payload);
        }
    }

    public void addDnsPayload(String payload) {
        if (!payload.trim().isEmpty() && !dnsPayloads.contains(payload)) {
            dnsPayloads.add(payload);
        }
    }

    public void removeUrlPayload(String payload) {
        if (urlPayloads.contains(payload)) {
            urlPayloads.remove(payload);
        }
        urlPayloads.remove(payload);
    }

    public void removeDnsPayload(String payload) {
        if (dnsPayloads.contains(payload)) {
            dnsPayloads.remove(payload);
        }
    }

    public void clearAllPayloads(){dnsPayloads.clear();
        urlPayloads.clear();
    }
    public List<String> getAllPayloads() {
        List<String> allPayloads = new ArrayList<>();
        allPayloads.addAll(urlPayloads);
        allPayloads.addAll(dnsPayloads);
        for(String payload:allPayloads){
            api.logging().logToOutput("已有的poc"+payload);

        }
        return allPayloads;
    }
}