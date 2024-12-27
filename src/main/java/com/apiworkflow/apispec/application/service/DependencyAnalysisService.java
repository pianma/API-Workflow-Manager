package com.apiworkflow.apispec.application.service;

import com.apiworkflow.apispec.application.port.in.AnalyzeDependencyUseCase;
import com.apiworkflow.apispec.application.port.out.ApiSpecRepository;
import com.apiworkflow.apispec.domain.ApiSpec;
import com.apiworkflow.apispec.domain.DependencyGraph;
import com.apiworkflow.common.UseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@UseCase
@Service
@Transactional
public class DependencyAnalysisService implements AnalyzeDependencyUseCase {

    private final ApiSpecRepository apiSpecRepository;

    @Override
    public void analyzeDependencies(ApiSpec apiSpec) {
        DependencyGraph graph = new DependencyGraph();

//        apiSpec.getPaths().forEach((path, apiPath) -> {
//            apiPath.getMethods().forEach((method, apiMethod) -> {
//                System.out.println("Analyzing dependencies for " + method + " " + path);
//                if (apiMethod.getDependencies() != null) {
//                    apiMethod.getDependencies().forEach(dependency -> {
//                        System.out.println("Dependency: " + dependency);
//                    });
//                }
//            });
//        });

        apiSpecRepository.save(apiSpec); // 스펙 저장
    }
}