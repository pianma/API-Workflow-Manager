package com.apiworkflow.apispec.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ApiSpec {
    private String domain;
    private String name;
    private String version;
    private Map<String, String> paths; // 경로와 HTTP 메서드 매핑
}
