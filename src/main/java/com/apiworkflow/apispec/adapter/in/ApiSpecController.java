package com.apiworkflow.apispec.adapter.in;

import com.apiworkflow.apispec.application.port.in.AnalyzeDependencyUseCase;
import com.apiworkflow.apispec.domain.ApiSpec;
import com.apiworkflow.common.WebAdapter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.models.media.Schema;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Operation;

import java.util.HashMap;
import java.util.Map;

@WebAdapter
@RestController
@RequiredArgsConstructor
public class ApiSpecController {

    private final AnalyzeDependencyUseCase analyzeDependencyUseCase;

    @PostMapping("/receive-openapi")
    public ResponseEntity<Map<String, String>> receiveOpenApi(@RequestBody String openApiSpec) {
        Map<String, String> apiDependencies = new HashMap<>();
        try {
            // OpenAPI 명세 파싱
            OpenAPIV3Parser parser = new OpenAPIV3Parser();
            OpenAPI openAPI = parser.readContents(openApiSpec, null, null).getOpenAPI();

            if (openAPI == null) {
                throw new IllegalArgumentException("Invalid OpenAPI specification provided.");
            }

            // 스키마 분석 및 의존성 탐지
            analyzeDependencies(openAPI, apiDependencies);

            apiDependencies.forEach((api, dependency) -> {
                System.out.printf("API '%s' 의 요청 데이터가 '%s'%n", api, dependency);
            });

            return ResponseEntity.ok(apiDependencies);
        } catch (Exception e) {
            System.err.println("OpenAPI 명세 파싱 실패: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    private void analyzeDependencies(OpenAPI openAPI, Map<String, String> apiDependencies) {
        // 스키마 분석 및 의존성 탐지 로직
        Map<String, Schema> schemas = openAPI.getComponents().getSchemas();
        Map<String, PathItem> paths = openAPI.getPaths();

        for (String path : paths.keySet()) {
            PathItem pathItem = paths.get(path);

            for (PathItem.HttpMethod method : pathItem.readOperationsMap().keySet()) {
                Operation operation = pathItem.readOperationsMap().get(method);

                if (operation.getRequestBody() != null) {
                    operation.getRequestBody().getContent().forEach((mediaType, media) -> {
                        if (media.getSchema() != null && media.getSchema().get$ref() != null) {
                            String requestSchema = extractSchemaName(media.getSchema().get$ref());
                            schemas.forEach((schemaName, schema) -> {
                                if (requestSchema.equals(schemaName)) {
                                    if (schema.getProperties() != null && schema.getProperties().containsKey("userId")) {
                                        apiDependencies.put(path, "/users API: 'userId' 필드가 /users API의 응답 데이터 User 스키마에서 유래했습니다.");
                                    }
                                    if (schema.getProperties() != null && schema.getProperties().containsKey("productId")) {
                                        apiDependencies.put(path, "/products API: 'productId' 필드가 /products API의 응답 데이터 Product 스키마에서 유래했습니다.");
                                    }
                                }
                            });
                        }
                    });
                }

                if (operation.getResponses() != null) {
                    operation.getResponses().forEach((statusCode, apiResponse) -> {
                        if (apiResponse.getContent() != null) {
                            apiResponse.getContent().forEach((mediaType, media) -> {
                                if (media.getSchema() != null && media.getSchema().get$ref() != null) {
                                    String responseSchema = extractSchemaName(media.getSchema().get$ref());
                                    schemas.forEach((schemaName, schema) -> {
                                        if (responseSchema.equals(schemaName)) {
                                            if (schema.getProperties() != null && schema.getProperties().containsKey("userId")) {
                                                apiDependencies.put(path, "/users/{id} API: 'userId' 필드가 /users/{id} API의 응답 데이터 User 스키마에서 유래했습니다.");
                                            }
                                            if (schema.getProperties() != null && schema.getProperties().containsKey("productId")) {
                                                apiDependencies.put(path, "/products API: 'productId' 필드가 /products API의 응답 데이터 Product 스키마에서 유래했습니다.");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    private static String extractSchemaName(String ref) {
        if (ref == null || !ref.contains("/")) {
            throw new IllegalArgumentException("Invalid schema reference: " + ref);
        }
        return ref.substring(ref.lastIndexOf("/") + 1);
    }
}
