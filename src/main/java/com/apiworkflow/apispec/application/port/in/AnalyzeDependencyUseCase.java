package com.apiworkflow.apispec.application.port.in;

import com.apiworkflow.apispec.domain.ApiSpec;

public interface AnalyzeDependencyUseCase {
    void analyzeDependencies(ApiSpec apiSpec);
}
