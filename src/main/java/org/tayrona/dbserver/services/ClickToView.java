package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tayrona.dbserver.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ClickToView {
    private static final String URL = "https://wmc2.rbc.com/webmail/do/push/Push/display";
    private static final String PARAM_KEY_1 = "EmailAddress";
    private static final String PARAM_KEY_2 = "Version";
    private static final String PARAM_KEY_3 = "password";
    private static final String PARAM_KEY_4 = "WebPushMessage";
    private static final String PARAM_VALUE_1 = "juanhaugaard@yahoo.com";
    private static final String PARAM_VALUE_2 = "1.0";
    private static final String PARAM_VALUE_3 = "Jh123456";
    private static final String PARAM_VALUE_4 = "WebPushMessage.txt";

    @Autowired
    private FormPost formPost;

    public String PostToRBC() {
        Map<String, String> params = new HashMap<>();
        try {
            String param4 = Utils.loadTextOrResource(PARAM_VALUE_4);
            param4 = param4.replaceAll("\n", "");
            params.put(PARAM_KEY_1, PARAM_VALUE_1);
            params.put(PARAM_KEY_2, PARAM_VALUE_2);
            params.put(PARAM_KEY_3, PARAM_VALUE_3);
            params.put(PARAM_KEY_4, param4);
            return formPost.doFormPostUrlEncoded(URL, params);
        } catch (IOException e) {
            log.error("{}: '{}'", e.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }
}
