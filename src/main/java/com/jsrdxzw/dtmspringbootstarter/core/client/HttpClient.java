package com.jsrdxzw.dtmspringbootstarter.core.client;

import com.jsrdxzw.dtmspringbootstarter.core.enums.DtmResultEnum;
import com.jsrdxzw.dtmspringbootstarter.core.enums.HttpMethod;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmRequestBranchRequest;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.DtmServerResult;
import com.jsrdxzw.dtmspringbootstarter.core.http.vo.GenGidResult;
import com.jsrdxzw.dtmspringbootstarter.exception.DtmException;
import com.jsrdxzw.dtmspringbootstarter.utils.JsonUtil;
import lombok.SneakyThrows;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xuzhiwei
 * @date 2022/4/7 14:23
 */
@Component
public class HttpClient {

    @Value("${dtm.http-server}")
    private String dtmServerUrl;

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS).build();

    @SneakyThrows
    public GenGidResult getNewGid() {
        String gidUrl = dtmServerUrl + "/newGid";
        Response response = get(gidUrl);
        if (Objects.isNull(response.body())) {
            throw new RuntimeException("can not get gid from gtm server");
        }
        String bodyResp = response.body().string();
        return JsonUtil.parseObject(bodyResp, GenGidResult.class);
    }

    @SneakyThrows
    public DtmServerResult transCallDtm(Object body, String operation) {
        String requestUrl = dtmServerUrl + "/" + operation;
        String data = JsonUtil.writeToString(body);
        Response response = post(requestUrl, data);
        if (Objects.isNull(response.body())) {
            throw new RuntimeException("can not get gid from gtm server");
        }
        String bodyResp = response.body().string();
        return JsonUtil.parseObject(bodyResp, DtmServerResult.class);
    }

    @SneakyThrows
    public Response transRequestBranch(DtmRequestBranchRequest request) {
        String data = JsonUtil.writeToString(request.getBody());
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("dtm", request.getDtm());
        queryParam.put("trans_type", request.getTransType());
        queryParam.put("branch_id", request.getBranchId());
        queryParam.put("gid", request.getGid());
        queryParam.put("op", request.getOp());
        Headers headers = null;
        if (!CollectionUtils.isEmpty(request.getBranchHeaders())) {
            headers = Headers.of(request.getBranchHeaders());
        }
        if (HttpMethod.POST.equals(request.getMethod())) {
            return post(request.getUrl(), data, headers, queryParam);
        }
        if (HttpMethod.GET.equals(request.getMethod())) {
            return get(request.getUrl(), headers, queryParam);
        }
        throw new RuntimeException("unsupported http method:" + request.getMethod());
    }

    /**
     * get request
     *
     * @param url
     * @return
     * @throws IOException
     */
    public Response get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        return CLIENT.newCall(request).execute();
    }

    public Response get(String url, Headers headers, Map<String, String> parameters) throws IOException {
        HttpUrl httpUrl = generateHttpUrl(url, parameters);
        Request.Builder builder = new Request.Builder().url(httpUrl).get();
        if (headers != null) {
            builder.headers(headers);
        }
        return CLIENT.newCall(builder.build()).execute();
    }

    /**
     * post request
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url(url).post(body).build();
        return CLIENT.newCall(request).execute();
    }

    public Response post(String url, String json, Headers headers, Map<String, String> parameters) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        HttpUrl httpUrl = generateHttpUrl(url, parameters);
        Request.Builder post = new Request.Builder().url(httpUrl).post(body);
        if (headers != null) {
            post.headers(headers);
        }
        return CLIENT.newCall(post.build()).execute();
    }

    private HttpUrl generateHttpUrl(String url, Map<String, String> parameters) {
        URI uri = URI.create(url);
        HttpUrl.Builder host = new HttpUrl.Builder()
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort())
                .encodedPath(uri.getPath());
        if (!CollectionUtils.isEmpty(parameters)) {
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                host.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return host.build();
    }

    @SneakyThrows
    public void catchErrorFromResponse(Response response) {
        String content = response.body().string();
        if (response.code() == DtmResultEnum.FAILURE.getStatus().value() || content.contains(DtmResultEnum.FAILURE.getDesc())) {
            throw DtmException.failure();
        }
        if (response.code() == DtmResultEnum.ONGOING.getStatus().value() || content.contains(DtmResultEnum.ONGOING.getDesc())) {
            throw DtmException.ongoing();
        }
    }
}
