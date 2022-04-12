package com.jsrdxzw.dtmspringbootstarter.core.http;

import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRequestBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.DtmServerResult;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.GenGidResult;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xuzhiwei
 * @date 2022/4/7 14:23
 */
@Slf4j
@Data
public class HttpClient {

    @Autowired
    private RestTemplate restTemplate;

    private String dtmServerUrl;

    public HttpClient(String dtmServerUrl) {
        this.dtmServerUrl = dtmServerUrl;
    }

    public GenGidResult getNewGid() {
        String gidUrl = dtmServerUrl + "/newGid";
        ResponseEntity<GenGidResult> response = restTemplate.getForEntity(gidUrl, GenGidResult.class);
        return response.getBody();
    }

    public DtmServerResult transCallDtm(Object body, String operation) {
        String requestUrl = dtmServerUrl + "/" + operation;
        ResponseEntity<DtmServerResult> response = restTemplate.postForEntity(requestUrl, body, DtmServerResult.class);
        if (response.getBody() == null) {
            throw new RuntimeException("can not get gid from gtm server");
        }
        return response.getBody();
    }

    public ResponseEntity<DtmServerResult> transRequestBranch(DtmRequestBranchRequest request) {
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("dtm", request.getDtm());
        queryParam.put("trans_type", request.getTransType());
        queryParam.put("branch_id", request.getBranchId());
        queryParam.put("gid", request.getGid());
        queryParam.put("op", request.getOp());
        HttpHeaders headers = new HttpHeaders();
        if (!CollectionUtils.isEmpty(request.getBranchHeaders())) {
            for (Map.Entry<String, String> entry : request.getBranchHeaders().entrySet()) {
                headers.add(entry.getKey(), entry.getValue());
            }
        }
        if (HttpMethod.POST.equals(request.getMethod())) {
            return post(request.getUrl(), request.getBody(), headers, queryParam);
        }
        if (HttpMethod.GET.equals(request.getMethod())) {
            return get(request.getUrl(), headers, queryParam);
        }
        log.error("unsupported http method: {}", request.getMethod());
        throw new RuntimeException("unsupported http method:" + request.getMethod());
    }

    public ResponseEntity<DtmServerResult> get(String baseUrl, HttpHeaders headers, Map<String, Object> parameters) {
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String url = componentQueryParams(baseUrl, parameters);

        return restTemplate.exchange(url, HttpMethod.GET, entity, DtmServerResult.class);
    }

    public ResponseEntity<DtmServerResult> post(String baseUrl, Object body, HttpHeaders headers, Map<String, Object> parameters) {
        HttpEntity<?> entity = new HttpEntity<>(body, headers);

        String url = componentQueryParams(baseUrl, parameters);

        return restTemplate.exchange(url, HttpMethod.POST, entity, DtmServerResult.class);
    }

    public void catchErrorFromResponse(ResponseEntity<DtmServerResult> response) {
        if (response.getStatusCode().value() == DtmResultEnum.FAILURE.getStatus().value() ||
                (response.getBody() != null && DtmResultEnum.FAILURE.equals(response.getBody().getResult()))) {
            throw DtmException.failure();
        }
        if (response.getStatusCode().value() == DtmResultEnum.ONGOING.getStatus().value()
                || (response.getBody() != null && DtmResultEnum.ONGOING.equals(response.getBody().getResult()))) {
            throw DtmException.ongoing();
        }
    }

    private String componentQueryParams(String baseUrl, Map<String, Object> params) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseUrl);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        return uriComponentsBuilder.build().toUriString();
    }
}
