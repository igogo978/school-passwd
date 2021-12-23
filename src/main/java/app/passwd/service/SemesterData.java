package app.passwd.service;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SemesterData {
    private Logger logger = LoggerFactory.getLogger(SemesterData.class);

    public String getdata(String token, String endpoint) {
//        logger.info("token:" + token);
        logger.info("取得semester data - endpoint:" + endpoint);
        HttpClient httpClient = HttpClientBuilder.create().build();
        ClientHttpRequestFactory requestFactory
                = new HttpComponentsClientHttpRequestFactory(httpClient);

        //spring.springframework
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

        return StringEscapeUtils.unescapeJava(response.getBody());
    }
}
