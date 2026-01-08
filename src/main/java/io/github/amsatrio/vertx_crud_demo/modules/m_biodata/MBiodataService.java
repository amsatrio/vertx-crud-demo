package io.github.amsatrio.vertx_crud_demo.modules.m_biodata;

import java.util.List;
import java.util.Objects;

import io.github.amsatrio.vertx_crud_demo.dto.request.FilterRequest;
import io.github.amsatrio.vertx_crud_demo.dto.request.SortRequest;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MBiodataService {
    private final MBiodataRepository repository;

    @Inject
    public MBiodataService(MBiodataRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository cannot be null");
    }

    public Future<Void> initTable() {
        return repository.initTable();
    }

    public Future<JsonObject> findById(Long id) {
        return repository.findById(id);
    }

    public Future<JsonObject> findBy(String key, String value) {
        return repository.findBy(key, value);
    }

    public Future<Void> create(JsonObject jsonObject) {
        return repository.create(jsonObject);
    }

    public Future<Void> update(JsonObject jsonObject) {
        return repository.update(jsonObject);
    }

    public Future<Void> deleteById(Long id) {
        return repository.deleteById(id);
    }

    public Future<Object> getPage(
            int page,
            int size,
            List<FilterRequest> filterRequests,
            List<SortRequest> sortRequests,
            String globalFilter) {
        return repository.getPage(page, size, filterRequests, sortRequests, globalFilter);
    }

    public Future<Void> saveAll(List<JsonObject> list) {
        return repository.saveAll(list);
    }
}
