package io.github.amsatrio.vertx_crud_demo.modules.m_biodata;

import io.github.amsatrio.vertx_crud_demo.config.database.DatabaseClientSelector;
import io.github.amsatrio.vertx_crud_demo.config.database.DatabaseType;
import io.github.amsatrio.vertx_crud_demo.dto.exception.DataExistException;
import io.github.amsatrio.vertx_crud_demo.dto.exception.DataNotFoundException;
import io.github.amsatrio.vertx_crud_demo.dto.request.FilterRequest;
import io.github.amsatrio.vertx_crud_demo.dto.request.SortRequest;
import io.github.amsatrio.vertx_crud_demo.repository.PaginationRepository;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.SqlTemplate;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Singleton
public class MBiodataRepository {

    private final SqlClient sqlClient;
    private final PaginationRepository<MBiodata> paginationRepository;

    @Inject
    public MBiodataRepository(DatabaseClientSelector databaseConfig) {
        this.sqlClient = databaseConfig.getSqlClient(DatabaseType.MARIADB);
        this.paginationRepository = new PaginationRepository<>(sqlClient);
    }

    // === CRUD ===

    // === READ
    public Future<JsonObject> findById(Long id) {
        log.debug("findById id: " + id);

        Promise<JsonObject> promise = Promise.promise();

        SqlTemplate
                .forQuery(sqlClient, "select * from m_biodata where id=#{id}")
                .mapTo(MBiodata.INSTANCE)
                .execute(Collections.singletonMap("id", id))
                .onFailure(promise::fail)
                .onSuccess(rows -> {
                    if (rows.size() == 0) {
                        promise.fail(new DataNotFoundException("data not found"));
                    } else {
                        promise.complete(rows.iterator().next().toJsonObject());
                    }
                });
        return promise.future();

    }

    public Future<JsonObject> findBy(String key, String value) {
        log.debug("findBy key: " + key + ", value: " + value);

        value = "%" + value + "%";

        Promise<JsonObject> promise = Promise.promise();

        String template = "select * from m_biodata where " + key + " like #{value} limit 1";
        log.info("template: " + template);

        SqlTemplate
                .forQuery(sqlClient, template)
                .mapTo(MBiodata.INSTANCE)
                .execute(Collections.singletonMap("value", value))
                .onFailure(exception -> {
                    promise.fail(exception);
                })
                .onSuccess(rows -> {
                    if (rows.size() == 0) {
                        promise.fail(new DataNotFoundException("data not found"));
                    } else {
                        promise.complete(rows.iterator().next().toJsonObject());
                    }
                });

        return promise.future();

    }

    // === CREATE
    public Future<Void> create(JsonObject jsonObject) {
        log.debug("create");

        Promise<Void> promise = Promise.promise();

        mBiodataExist(jsonObject.getLong("id"))
                .onFailure(promise::fail)
                .onSuccess(exist -> {
                    if (exist) {
                        promise.fail(new DataExistException("data exist"));
                        return;
                    }

                    Map<String, Object> map = jsonObject.getMap();
                    log.debug("map: " + map.toString());
                    if (map.containsKey("image")) {
                        if (map.get("image") != null) {
                            String base64 = map.get("image").toString();
                            base64 = base64.replaceFirst("data:image/jpeg;base64,", "");
                            base64 = base64.replaceFirst("data:image/jpg;base64,", "");
                            base64 = base64.replaceFirst("data:image/png;base64,", "");
                            byte[] imageData = Base64.getDecoder().decode(base64);
                            map.replace("image", Buffer.buffer(imageData));
                        }
                    }

                    // insert new data
                    SqlTemplate
                            .forUpdate(sqlClient,
                                    """
                                            insert into \s
                                            m_biodata (id,fullname,mobile_phone,image,image_path,created_by,created_on,modified_by,modified_on,deleted_by,deleted_on,is_delete) \s
                                            values (#{id},#{fullname},#{mobilePhone},#{image},#{imagePath},#{createdBy},#{createdOn},#{modifiedBy},#{modifiedOn},#{deletedBy},#{deletedOn},#{isDelete})""")
                            .execute(map)
                            .onFailure(promise::fail)
                            .onSuccess(v -> {
                                promise.complete();
                            });

                });
        return promise.future();

    }

    // === UPDATE
    public Future<Void> update(JsonObject jsonObject) {
        log.debug("update");

        Promise<Void> promise = Promise.promise();

        mBiodataExist(jsonObject.getLong("id"))
                .onFailure(promise::fail)
                .onSuccess(exist -> {
                    if (!exist) {
                        promise.fail(new DataNotFoundException("data not found"));
                        return;
                    }

                    Map<String, Object> map = jsonObject.getMap();
                    log.debug("map: " + map.toString());
                    if (map.containsKey("image")) {
                        if (map.get("image") != null) {
                            String base64 = map.get("image").toString();
                            base64 = base64.replaceFirst("data:image/jpeg;base64,", "");
                            base64 = base64.replaceFirst("data:image/jpg;base64,", "");
                            base64 = base64.replaceFirst("data:image/png;base64,", "");
                            byte[] imageData = Base64.getDecoder().decode(base64);
                            map.replace("image", Buffer.buffer(imageData));
                        }
                    }

                    // update data
                    SqlTemplate
                            .forUpdate(sqlClient,
                                    """
                                            update m_biodata \s
                                            set id=#{id},fullname=#{fullname},mobile_phone=#{mobilePhone},image=#{image},image_path=#{imagePath},modified_by=#{modifiedBy},modified_on=#{modifiedOn},deleted_by=#{deletedBy},deleted_on=#{deletedOn},is_delete=#{isDelete} \s
                                            where id=#{id}""")
                            .execute(map)
                            .onFailure(promise::fail)
                            .onSuccess(v -> {
                                promise.complete();
                            });

                });
        return promise.future();
    }

    // === DELETE
    public Future<Void> deleteById(Long id) {
        log.debug("deleteById " + "id: " + id);

        Promise<Void> promise = Promise.promise();

        SqlTemplate
                .forQuery(sqlClient, "delete from m_biodata where id=#{id}")
                .execute(Collections.singletonMap("id", id))
                .onFailure(exception -> {
                    promise.fail(exception);
                })
                .onSuccess(rows -> {
                    promise.complete();
                });

        return promise.future();

    }

    // === PAGINATION
    public Future<Object> getPage(
            int page,
            int size,
            List<FilterRequest> filterRequests,
            List<SortRequest> sortRequests,
            String globalFilter) {
        return paginationRepository.getPage(MBiodata.class, "m_biodata", page, size, filterRequests, sortRequests,
                globalFilter);
    }


    private Future<Boolean> mBiodataExist(Long id) {
        Promise<Boolean> promise = Promise.promise();

        // check if data exist
        SqlTemplate
                .forQuery(sqlClient, "select id from m_biodata where id=#{id}")
                .execute(Collections.singletonMap("id", id))
                .onFailure(promise::fail)
                .onSuccess(rows -> {
                    promise.complete(rows.size() > 0);
                });

        return promise.future();
    }

}