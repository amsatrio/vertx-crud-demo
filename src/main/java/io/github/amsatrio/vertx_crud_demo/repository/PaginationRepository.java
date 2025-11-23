package io.github.amsatrio.vertx_crud_demo.repository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.amsatrio.vertx_crud_demo.dto.enumerator.FilterMatchMode;
import io.github.amsatrio.vertx_crud_demo.dto.request.FilterRequest;
import io.github.amsatrio.vertx_crud_demo.dto.request.SortRequest;
import io.github.amsatrio.vertx_crud_demo.dto.response.PaginationResponse;
import io.github.amsatrio.vertx_crud_demo.util.AppConverter;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaginationRepository<T> {
    private final SqlClient sqlClient;

    public PaginationRepository(SqlClient sqlClient) {
        this.sqlClient = Objects.requireNonNull(sqlClient);
    }

    public Future<Object> getPage(
            Class<T> entityClass,
            String tableName,
            int page,
            int size,
            List<FilterRequest> filterRequests,
            List<SortRequest> sortRequests,
            String globalFilter) {
        Promise<Object> promise = Promise.promise();
        PaginationResponse<T> paginationResponse = new PaginationResponse<>();

        String query = "SELECT * FROM " + tableName + " ";
        String countQuery = "SELECT COUNT(*) as count_data FROM " + tableName + " ";

        String filterQuery = getFilterQuery(filterRequests);
        String globalFilterQuery = getGlobalFilterQuery(entityClass, globalFilter);
        String sortQuery = getSortQuery(sortRequests);

        if (!globalFilterQuery.isEmpty()) {
            query = query + " where " + globalFilterQuery;
            countQuery = countQuery + " where " + globalFilterQuery;
        }
        if (!filterQuery.isEmpty()) {
            if (query.contains("where")) {
                query = query + " and " + filterQuery;
                countQuery = countQuery + " and " + filterQuery;
            } else {
                query = query + " where " + filterQuery;
                countQuery = countQuery + " where " + filterQuery;
            }
        }
        if (!sortQuery.isEmpty()) {
            query = query + "\n" + sortQuery + "\n";
        }

        final String finalQuery = query;

        Map<String, Object> queryMap = new HashMap<>();

        getTotalData(countQuery, "count_data")
                .onFailure(exception -> {
                    promise.fail(exception);
                })
                .onSuccess(totalData -> {

                    paginationResponse.setTotalElements(totalData);

                    Long totalPages = totalData / size;
                    if (totalData % size != 0)
                        totalPages += 1;
                    paginationResponse.setTotalPages(totalPages);

                    Long longPage = (long) page;
                    if (longPage < 0 || longPage > totalPages - 1) {
                        longPage = totalPages - 1;
                    }

                    Boolean firstPage = longPage == 0;
                    Boolean lastPage = longPage == totalPages - 1;
                    paginationResponse.setFirst(firstPage);
                    paginationResponse.setLast(lastPage);

                    // PAGINATION
                    String pageQuery = finalQuery;
                    pageQuery = pageQuery + """
                            limit #{size}
                            offset #{offset}
                            """;
                    queryMap.put("size", size);
                    queryMap.put("offset", size * page);

                    log.debug("query: {}", pageQuery);
                    log.debug("query_map: {}", queryMap);

                    executePageQuery(entityClass, pageQuery, queryMap, paginationResponse, promise);
                });

        return promise.future();
    }

    private void executePageQuery(
            Class<T> entityClass,
            String pageQuery,
            Map<String, Object> queryMap,
            PaginationResponse<T> paginationResponse,
            Promise<Object> promise) {
                log.info("pageQuery: " + pageQuery);
        SqlTemplate
                .forQuery(sqlClient, pageQuery)
                .mapTo(getRowMapper(entityClass))
                .execute(queryMap)
                .onFailure(promise::fail)
                .onSuccess(rows -> {
                    if (rows.size() == 0) {
                        paginationResponse.setContent(new ArrayList<>());
                        promise.complete(paginationResponse);
                        return;
                    }

                    List<T> list = new ArrayList<>();
                    RowIterator<T> iterator = rows.iterator();
                    while (iterator.hasNext()) {
                        list.add(iterator.next());
                    }

                    paginationResponse.setContent(list);
                    promise.complete(paginationResponse);
                });
    }

    private String getGlobalFilterQuery(Class<T> entityClass, String globalFilter) {
        if (globalFilter == null || globalFilter.isEmpty()) {
            return "";
        }

        StringBuilder globalFilterQuery = new StringBuilder("(");
        boolean firstField = true;
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            // Skip fields with 'id' or non-String types
            if (field.getName().contains("id") || field.getType() != String.class) {
                continue;
            }

            if (!firstField) {
                globalFilterQuery.append(" OR ");
            }

            String globalFilterBy = AppConverter.camelToSnakeCase(field.getName());
            globalFilterQuery.append(globalFilterBy).append(" LIKE '%").append(globalFilter).append("%'");

            firstField = false;
        }
        globalFilterQuery.append(")");

        return firstField ? "" : globalFilterQuery.toString();
    }

    private String getFilterQuery(List<FilterRequest> filterRequests) {
        ObjectMapper objectMapper = new ObjectMapper();
        String queryFiltering = "(";
        for (FilterRequest filterRequest : filterRequests) {
            if (!queryFiltering.equals("(")) {
                queryFiltering += " AND ";
            }

            Object object1 = filterRequest.getValue();
            Object object2 = null;
            if (filterRequest.getValue().toString().startsWith("[")
                    && filterRequest.getValue().toString().endsWith("]")) {
                try {
                    List<Object> objectList = objectMapper.convertValue(filterRequest.getValue(),
                            new TypeReference<List<Object>>() {
                            });
                    if (objectList.size() == 2) {
                        filterRequest.setMatchMode(FilterMatchMode.BETWEEN);
                        object1 = objectList.get(0);
                        object2 = objectList.get(1);
                    } else if (objectList.size() == 1) {
                        object1 = objectList.get(0);
                    } else {
                        continue;
                    }
                } catch (Exception exception) {
                    log.error("object mapper convert data error:", exception);
                }
            }

            String value = object1.toString();
            String id = AppConverter.camelToSnakeCase(filterRequest.getId());

            switch (filterRequest.getMatchMode()) {
                case BETWEEN:
                    String value2 = object2.toString();
                    queryFiltering += id + " BETWEEN " + value + " AND " + value2;
                    break;
                case EQUALS:
                    queryFiltering += id + " = " + value;
                    break;
                case CONTAINS:
                    queryFiltering += id + " LIKE '%" + value + "%'";
                    break;
                case NOT:
                    queryFiltering += id + " <> " + value;
                    break;
                case LESS_THAN:
                    queryFiltering += id + " < " + value;
                    break;
                case GREATER_THAN:
                    queryFiltering += id + " > " + value;
                    break;
                default:
                    queryFiltering += id + " LIKE '%" + value + "%'";
                    break;
            }
        }
        queryFiltering += ")";
        if (queryFiltering.equals("()")) {
            queryFiltering = "";
        }

        return queryFiltering;
    }

    private String getSortQuery(List<SortRequest> sortRequests) {
        if (sortRequests.size() <= 0) {
            return "";
        }

        String query = "ORDER BY ";
        for (SortRequest sortRequest : sortRequests) {
            if (!query.equals("ORDER BY ")) {
                query += ",";
            }
            String id = AppConverter.camelToSnakeCase(sortRequest.getId());
            query += id + (sortRequest.isDesc() ? " DESC" : " ASC");
        }

        return query;
    }

    private Future<Long> getTotalData(String query, String columndId) {
        log.debug("getTotalData");

        Promise<Long> promise = Promise.promise();

        Map<String, Object> map = new HashMap<>();

        // get total items

        SqlTemplate
                .forQuery(sqlClient, query)
                .execute(map)
                .onFailure(exception -> {
                    promise.fail(exception);
                })
                .onSuccess(rows -> {
                    if (rows.size() == 0) {
                        // log.debug("data not found");
                        promise.complete(0L);
                    } else {
                        promise.complete(rows.iterator().next().getLong(columndId));
                    }
                });

        return promise.future();
    }

    @SuppressWarnings("unchecked")
    private RowMapper<T> getRowMapper(Class<T> entityClass) {
        try {
            Field instanceField = entityClass.getField("INSTANCE");
            return (RowMapper<T>) instanceField.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to get row mapper for " + entityClass.getSimpleName(), e);
        }
    }

}
