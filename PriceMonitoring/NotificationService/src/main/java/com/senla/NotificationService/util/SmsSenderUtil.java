package com.senla.NotificationService.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
public class SmsSenderUtil {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${sms.api.key}")
    private String apiKey;
    @Value("${sms.project.name}")
    private String project;
    @Value("${sms.url}")
    private String url;

    public void sendSms(String phoneNumber, String message) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        map.add("project", project);
        map.add("recipients", phoneNumber);
        map.add("message", message);
        map.add("apikey", apiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(
                "application",
                "x-www-form-urlencoded",
                StandardCharsets.UTF_8)
        );

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        restTemplate.postForObject(url, request, String.class);
    }
}
