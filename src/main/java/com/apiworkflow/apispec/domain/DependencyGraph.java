package com.apiworkflow.apispec.domain;
import java.util.*;

public class DependencyGraph {
    private final Map<String, List<String>> dependencies = new HashMap<>();

    public void addDependency(String api, String dependentApi) {
        dependencies.computeIfAbsent(api, k -> new ArrayList<>()).add(dependentApi);
    }

    public List<String> getDependents(String api) {
        return dependencies.getOrDefault(api, Collections.emptyList());
    }
}
