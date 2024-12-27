package com.apiworkflow.apispec.adapter.out;

import com.apiworkflow.apispec.application.port.out.ApiSpecRepository;
import com.apiworkflow.apispec.domain.ApiSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class InMemoryApiSpecRepository implements ApiSpecRepository {

    private final Map<String, ApiSpec> store = new HashMap<>();

    @Override
    public void save(ApiSpec apiSpec) {
//        log.info("Saving dependency data for API: {}", apiSpec.getPaths());
//        store.put(apiSpec.getName() + ":" + apiSpec.getVersion(), apiSpec);
    }

    @Override
    public Optional<ApiSpec> findByNameAndVersion(String name, String version) {
        return Optional.ofNullable(store.get(name + ":" + version));
    }
}
