package VulTool;

import java.util.ArrayList;
import java.util.List;
import burp.api.montoya.MontoyaApi;

public class PayloadConfig {
    private List<String> payloads;
    private boolean enableActiveScan;
    private MontoyaApi api;
    public PayloadConfig(MontoyaApi api) {
        this.payloads = new ArrayList<>();
        this.enableActiveScan = false;
        this.api=api;

    }

    // Getters and Setters
    public List<String> getPayloads() { return payloads; }
    public boolean isEnableActiveScan() { return enableActiveScan; }
    public void setEnableActiveScan(boolean enableActiveScan) { this.enableActiveScan = enableActiveScan; }

    public void addPayload(String payload) {
        if (!payload.trim().isEmpty() && !payloads.contains(payload)) {
            payloads.add(payload);
        }
    }


    public void removePayload(String payload) {
        if (payloads.contains(payload)) {
            payloads.remove(payload);
        }
        payloads.remove(payload);
    }


    public void clearAllPayloads(){payloads.clear();
        payloads.clear();
    }
    public List<String> getAllPayloads() {
        for(String payload:payloads){
            api.logging().logToOutput("已有的poc"+payload);

        }
        return payloads;
    }
}