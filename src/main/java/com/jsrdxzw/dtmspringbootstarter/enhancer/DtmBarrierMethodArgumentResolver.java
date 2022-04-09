package com.jsrdxzw.dtmspringbootstarter.enhancer;

import com.jsrdxzw.dtmspringbootstarter.annotations.DtmBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.barrier.BranchBarrier;
import com.jsrdxzw.dtmspringbootstarter.core.http.ro.DtmServerRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author xuzhiwei
 * @date 2022/4/9 10:28
 */
public class DtmBarrierMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(DtmBarrier.class) != null && parameter.getParameterType() == BranchBarrier.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        DtmServerRequest dtmServerRequest = DtmServerRequest.builder()
                .dtm(retrieveValue(request, "dtm"))
                .gid(retrieveValue(request, "gid"))
                .branchId(retrieveValue(request, "branch_id"))
                .transType(retrieveValue(request, "trans_type"))
                .op(retrieveValue(request, "op"))
                .build();
        return new BranchBarrier(dtmServerRequest);
    }

    private <T> T retrieveValue(HttpServletRequest request, String parameterName) {
        if (Objects.nonNull(request.getParameter(parameterName))) {
            return (T) request.getParameter(parameterName);
        }
        return null;
    }
}
