package com.apiworkflow.apispec.domain;

import lombok.Data;

import java.util.List;

@Data
public class ApiPath {
    private String domain;
    private String name;
    private String version;
    private List<String> dependencies; // 호출하는 API 목록
}
