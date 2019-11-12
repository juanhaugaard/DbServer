package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class FormPost {

    private RestTemplate restTemplate;

    public FormPost() {
        restTemplate = new RestTemplate();
    }

    public String doFormPostUrlEncoded(final String url, final Map<String, String> params) {
        String ret = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        params.forEach(map::add);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (response != null) {
            if (HttpStatus.OK.equals(response.getStatusCode())) {
                ret = response.getBody();
            } else if (HttpStatus.FOUND.equals(response.getStatusCode())){
                List<String> locations = response.getHeaders().get("Location");
                String location = locations != null ? String.join(",", locations) : "Location Header not found";
                log.warn("Response status: {}-'{}', '{}'", response.getStatusCode().value(), response.getStatusCode().getReasonPhrase(), location);
            } else {
                log.warn("Response status: {}-'{}'", response.getStatusCode().value(), response.getStatusCode().getReasonPhrase());
            }
        }
        return ret;
    }
}
