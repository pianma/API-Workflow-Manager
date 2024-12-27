package com.apiworkflow.apispec.domain;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ApiSpec {
    private Map<String, ApiPath> paths; // API 경로 정보와 의존성 매핑

    @Data
    public static class ApiPath {
        private List<String> dependencies; // 의존하는 다른 API 경로
    }
}
