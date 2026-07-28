package com.gitinbits.config;

import com.gitinbits.dto.context.DataSourceType;
import com.gitinbits.dto.context.GitHubContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GitHubContextResolver implements HandlerMethodArgumentResolver {

    private static final String HEADER_SOURCE_TYPE = "X-GitHub-Source-Type";
    private static final String HEADER_SOURCE_VALUE = "X-GitHub-Source-Value";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(GitHubContext.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String typeStr = request.getHeader(HEADER_SOURCE_TYPE);
        String value = request.getHeader(HEADER_SOURCE_VALUE);

        if (typeStr == null || typeStr.isBlank() || value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing GitHub source headers");
        }

        DataSourceType type;
        try {
            type = DataSourceType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid GitHub source type: " + typeStr);
        }

        String accountName = value;
        String repositoryName = null;

        if (type == DataSourceType.PUBLIC_REPOSITORY) {
            String[] parts = value.split("/");
            if (parts.length != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid repository format. Expected owner/repo.");
            }
            accountName = parts[0];
            repositoryName = parts[1];
        }

        return new GitHubContext(type, accountName, repositoryName);
    }
}
