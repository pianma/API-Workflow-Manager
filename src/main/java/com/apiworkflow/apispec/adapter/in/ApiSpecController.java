package com.apiworkflow.apispec.adapter.in;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class ApiSpecController {

    @PostMapping("/dynamic-analyze-dependencies")
    public ResponseEntity<Map<String, Set<String>>> analyzeDependencies(@RequestBody String openApiSpec) {
        Map<String, Set<String>> apiDependencies = new HashMap<>();
        try {
            // OpenAPI 명세 파싱
            OpenAPIV3Parser parser = new OpenAPIV3Parser();
            OpenAPI openAPI = parser.readContents(openApiSpec, null, null).getOpenAPI();

            if (openAPI == null) {
                System.err.println("OpenAPI 파싱 실패");
                throw new IllegalArgumentException("유효하지 않은 OpenAPI 명세.");
            }

            System.out.println("파싱된 OpenAPI: " + openAPI);

            // Paths 및 Components 분석
            Map<String, PathItem> paths = openAPI.getPaths();
            Map<String, Schema> schemas = openAPI.getComponents().getSchemas();

            System.out.println("경로 정보: " + paths);
            System.out.println("스키마 정보: " + schemas);

            if (paths == null || schemas == null) {
                throw new IllegalArgumentException("경로나 스키마가 없음.");
            }

            // 모든 경로 및 요청/응답 스키마를 순회하며 분석
            for (String path : paths.keySet()) {
                PathItem pathItem = paths.get(path);

                pathItem.readOperations().forEach(operation -> {
                    // 요청 스키마 분석
                    if (operation.getRequestBody() != null) {
                        operation.getRequestBody().getContent().forEach((mediaType, media) -> {
                            if (media.getSchema() != null && media.getSchema().get$ref() != null) {
                                String schemaName = extractSchemaName(media.getSchema().get$ref());
                                System.out.println("요청 스키마 참조: " + schemaName);
                                extractDependencies(schemaName, schemas, path, apiDependencies);
                            }
                        });
                    }

                    // 응답 스키마 분석
                    if (operation.getResponses() != null) {
                        operation.getResponses().forEach((statusCode, apiResponse) -> {
                            if (apiResponse.getContent() != null) {
                                apiResponse.getContent().forEach((mediaType, media) -> {
                                    if (media.getSchema() != null && media.getSchema().get$ref() != null) {
                                        String schemaName = extractSchemaName(media.getSchema().get$ref());
                                        System.out.println("응답 스키마 참조: " + schemaName);
                                        extractDependencies(schemaName, schemas, path, apiDependencies);
                                    }
                                });
                            }
                        });
                    }
                });
            }

            System.out.println("API 의존성: " + apiDependencies);
            return ResponseEntity.ok(apiDependencies);
        } catch (Exception e) {
            System.err.println("의존성 분석 중 오류 발생: " + e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonMap("오류", Collections.singleton(e.getMessage())));
        }
    }

    private void extractDependencies(String schemaName, Map<String, Schema> schemas, String path, Map<String, Set<String>> apiDependencies) {
        Schema schema = schemas.get(schemaName);
        if (schema != null && schema.getProperties() != null) {
            schema.getProperties().forEach((propertyName, propertySchema) -> {
                if (propertySchema instanceof Schema) {
                    Schema<?> property = (Schema<?>) propertySchema;
                    if (property.get$ref() != null) {
                        String referencedSchema = extractSchemaName(property.get$ref());
                        apiDependencies.computeIfAbsent(path, k -> new HashSet<>()).add("/" + referencedSchema.toLowerCase());
                    }
                }
            });
        }
    }

    private static String extractSchemaName(String ref) {
        if (ref == null || !ref.contains("/")) {
            throw new IllegalArgumentException("유효하지 않은 스키마 참조: " + ref);
        }
        return ref.substring(ref.lastIndexOf("/") + 1);
    }
}
