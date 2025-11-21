package VulTool;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecretScan {
    List<String> results=new ArrayList<>();

    public List<String> startScan(String text, MontoyaApi montoyaApi) {

    // 检测身份证号
    Pattern idCardPattern = Pattern.compile("(\\b\\d{15}\\b)|(\\b\\d{18}\\b)|(\\b\\d{17}(\\d|X|x)\\b)");
    Matcher idCardMatcher = idCardPattern.matcher(text);
        while (idCardMatcher.find()) {
        results.add("身份证号: " + idCardMatcher.group());
    }

    // 检测手机号
    Pattern phonePattern = Pattern.compile("\\b1[3-9]\\d{9}\\b");
    Matcher phoneMatcher = phonePattern.matcher(text);
        while (phoneMatcher.find()) {
        results.add("手机号: " + phoneMatcher.group());
    }

    // 检测邮箱地址
    Pattern emailPattern = Pattern.compile("\\b[a-zA-Z0-9_]+@[a-zA-Z0-9]+(\\.[a-zA-Z]+)+\\b");
    Matcher emailMatcher = emailPattern.matcher(text);
        while (emailMatcher.find()) {
        results.add("邮箱地址: " + emailMatcher.group());
    }

    // 检测银行卡号
    Pattern bankCardPattern = Pattern.compile("\\b\\d{16,19}\\b");
    Matcher bankCardMatcher = bankCardPattern.matcher(text);
        while (bankCardMatcher.find()) {
        results.add("银行卡号: " + bankCardMatcher.group());
    }

    // 检测URL
    Pattern urlPattern = Pattern.compile("\\b(http|https)://[\\w.-]+(\\.[\\w.-]+)+([/?].*)?\\b");
    Matcher urlMatcher = urlPattern.matcher(text);
        while (urlMatcher.find()) {
        results.add("URL: " + urlMatcher.group());
    }

        return results;
}

}
