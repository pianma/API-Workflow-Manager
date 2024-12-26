package com.apiworkflow.apispec.application.service;

import com.apiworkflow.apispec.application.port.in.AnalyzeDependencyUseCase;
import com.apiworkflow.apispec.application.port.out.ApiSpecRepository;
import com.apiworkflow.apispec.domain.ApiSpec;
import com.apiworkflow.apispec.domain.DependencyGraph;
import com.apiworkflow.common.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
@Transactional
public class DependencyAnalysisService implements AnalyzeDependencyUseCase {

    private final ApiSpecRepository apiSpecRepository;

    @Override
    public void analyzeDependencies(ApiSpec apiSpec) {
        DependencyGraph graph = new DependencyGraph();

        // 간단한 의존성 추가 예제
        apiSpec.getPaths().forEach((path, method) -> {
            graph.addDependency(apiSpec.getName(), path); // 의존성 추가
        });

        apiSpecRepository.save(apiSpec); // 스펙 저장
    }
}