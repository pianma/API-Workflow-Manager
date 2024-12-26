package com.apiworkflow.apispec.application.port.out;

import com.apiworkflow.apispec.domain.ApiSpec;

import java.util.Optional;

public interface ApiSpecRepository {
    void save(ApiSpec apiSpec);
    Optional<ApiSpec> findByNameAndVersion(String name, String version);
}
