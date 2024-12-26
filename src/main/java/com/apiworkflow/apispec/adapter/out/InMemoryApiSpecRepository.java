package com.apiworkflow.apispec.adapter.out;

import com.apiworkflow.apispec.application.port.out.ApiSpecRepository;
import com.apiworkflow.apispec.domain.ApiSpec;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryApiSpecRepository implements ApiSpecRepository {

    private final Map<String, ApiSpec> store = new HashMap<>();

    @Override
    public void save(ApiSpec apiSpec) {
        store.put(apiSpec.getName() + ":" + apiSpec.getVersion(), apiSpec);
    }

    @Override
    public Optional<ApiSpec> findByNameAndVersion(String name, String version) {
        return Optional.ofNullable(store.get(name + ":" + version));
    }
}
