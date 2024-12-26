package com.apiworkflow.apispec.adapter.in;

import com.apiworkflow.apispec.application.port.in.AnalyzeDependencyUseCase;
import com.apiworkflow.apispec.domain.ApiSpec;
import com.apiworkflow.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebAdapter
@RestController
@RequiredArgsConstructor
@RequestMapping("/api-specs")
public class ApiSpecController {

    private final AnalyzeDependencyUseCase analyzeDependencyUseCase;
//
//    public ApiSpecController(AnalyzeDependencyUseCase analyzeDependencyUseCase) {
//        this.analyzeDependencyUseCase = analyzeDependencyUseCase;
//    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadApiSpec(@RequestBody ApiSpec request) {
        ApiSpec apiSpec = new ApiSpec(request.getDomain(),request.getName(), request.getVersion(), request.getPaths());
        analyzeDependencyUseCase.analyzeDependencies(apiSpec);
        return ResponseEntity.ok().build();
    }
}
