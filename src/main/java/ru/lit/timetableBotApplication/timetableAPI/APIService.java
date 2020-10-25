package ru.lit.timetableBotApplication.timetableAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import ru.lit.timetableBotApplication.model.ResponseGroup;
import ru.lit.timetableBotApplication.model.ResponseLesson;

@Slf4j
@Service
public class APIService {

    private final RestTemplate restTemplate;
    @Value("${timetable.server}")
    private String apiAdress;

    public APIService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }


    public ResponseLesson[] getSchedule(String from, String to, String groupId) {
        log.info("Send request to timetable server: Getting schedule");
        String requestURL = String.format("%s/loadSchedule?from=%s&to=%s&groupId=%s", apiAdress, from, to, groupId);
        ResponseEntity res = restTemplate.getForEntity(requestURL, ResponseLesson[].class);
        return (ResponseLesson[]) res.getBody();
    }

    public String getGroupName(String groupId) {
        log.info("Send request to timetable server: Group name");
        ResponseEntity res = restTemplate.getForEntity(apiAdress + "/loadGroupName?groupId=" + groupId,
            String.class);
        return (String) res.getBody();
    }

    public ResponseGroup[] getSubGroup(String groupId) {
        log.info("Send request to timetable server: Sub group");
        String requestURL = String.format("%s/loadSubGroup?groupId=%s", this.apiAdress, groupId);
        ResponseEntity res = restTemplate.getForEntity(requestURL, ResponseGroup[].class);
        return (ResponseGroup[]) res.getBody();
    }


}
