package app.passwd.service;

import app.passwd.model.StudentNameList;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class SemesterData {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

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


    public void getStudentNameList(String token, String endpoint) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        StudentNameList studentNameList = new StudentNameList();
        studentNameList.setKind("list_class");
        studentNameList.setClass_no("");

        List<String> items = new ArrayList<>();
        studentNameList.getItem_key().addAll(items);

        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity(studentNameList, headers);

        String result = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class).getBody();
//            logger.info("new password:"+ account.getPassword());
        logger.info(result);

    }


}
