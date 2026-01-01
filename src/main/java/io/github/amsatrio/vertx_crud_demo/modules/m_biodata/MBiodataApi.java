package io.github.amsatrio.vertx_crud_demo.modules.m_biodata;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.github.amsatrio.vertx_crud_demo.dto.request.FilterRequest;
import io.github.amsatrio.vertx_crud_demo.dto.request.SortRequest;
import io.github.amsatrio.vertx_crud_demo.handler.exception.ApiExceptionHandler;
import io.github.amsatrio.vertx_crud_demo.handler.response.ApiResponseHandler;
import io.github.amsatrio.vertx_crud_demo.util.AppGenerator;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class MBiodataApi {
    private final ApiExceptionHandler apiExceptionHandler;
    private final ApiResponseHandler apiResponseHandler;
    private final MBiodataService mBiodataService;

    @Inject
    public MBiodataApi(MBiodataService mBiodataService) {
        this.apiExceptionHandler = new ApiExceptionHandler();
        this.apiResponseHandler = new ApiResponseHandler();
        this.mBiodataService = Objects.requireNonNull(mBiodataService, "MBiodataService cannot be null");
    }

    public void init(Router router) {
        router.get("/v1/m-biodata/generate/:size")
                .handler(this::generate);
        router.get("/v1/m-biodata").handler(this::getPage);
        router.get("/v1/m-biodata/:key/:value")
                .handler(this::findBy);
        router.get("/v1/m-biodata/:id")
                .handler(MBiodata.validationPathParamIdHandler())
                .handler(this::findById);
        router.post("/v1/m-biodata")
                .handler(MBiodata.validationBodyHandler())
                .handler(this::create);
        router.put("/v1/m-biodata")
                .handler(MBiodata.validationBodyHandler())
                .handler(this::update);
        router.delete("/v1/m-biodata/:id")
                .handler(MBiodata.validationPathParamIdHandler())
                .handler(this::deleteById);
    }

    public void findById(RoutingContext routingContext) {
        log.debug("findById");
        String idString = routingContext.pathParam("id");
        if (idString == null) {
            routingContext.response().setStatusCode(400).end("Missing ID parameter");
            return;
        }

        Long id = Long.parseLong(idString);

        mBiodataService.findById(id)
                .onFailure(exception -> {
                    apiExceptionHandler.error(routingContext, exception);
                }).onSuccess(data -> {
                    apiResponseHandler.success(routingContext, data);
                });
    }

    public void findBy(RoutingContext routingContext) {
        log.debug("findBy");

        String key = routingContext.pathParam("key");
        String value = routingContext.pathParam("value");

        mBiodataService.findBy(key, value)
                .onFailure(exception -> {
                    apiExceptionHandler.error(routingContext, exception);
                }).onSuccess(data -> {
                    apiResponseHandler.success(routingContext, data);
                });
    }

    public void create(RoutingContext routingContext) {
        log.debug("create");

        Long accessUserId = routingContext.get("jwt_user_id");
        if (accessUserId == null) {
            accessUserId = 0L;
        }

        RequestBody requestBody = routingContext.body();

        JsonObject jsonObject = requestBody.asJsonObject();

        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        Date date = new Date();

        // set log data
        if (!jsonObject.containsKey("id")) {
            jsonObject.put("id", date.getTime());
        }
        jsonObject.put("createdBy", accessUserId);
        jsonObject.put("createdOn", LocalDateTime.now());
        jsonObject.put("isDelete", false);
        jsonObject.remove("deletedBy");
        jsonObject.remove("deletedOn");
        jsonObject.remove("modifiedOn");
        jsonObject.remove("modifiedBy");

        mBiodataService.create(jsonObject)
                .onFailure(exception -> {
                    apiExceptionHandler.error(routingContext, exception);
                }).onSuccess(data -> {
                    apiResponseHandler.success(routingContext);
                });

    }

    public void update(RoutingContext routingContext) {
        log.debug("update");

        Long accessUserId = routingContext.get("jwt_user_id");
        if (accessUserId == null) {
            accessUserId = 0L;
        }

        RequestBody requestBody = routingContext.body();

        JsonObject jsonObject = requestBody.asJsonObject();

        // set log data
        jsonObject.put("modifiedBy", accessUserId);
        jsonObject.put("modifiedOn", LocalDateTime.now());
        if (jsonObject.getBoolean("isDelete")) {
            jsonObject.put("deletedBy", accessUserId);
            jsonObject.put("deletedOn", LocalDateTime.now());
        } else {
            jsonObject.remove("deletedBy");
            jsonObject.remove("deletedOn");
        }
        jsonObject.remove("createdOn");
        jsonObject.remove("createdBy");

        mBiodataService.update(jsonObject)
                .onFailure(exception -> {
                    apiExceptionHandler.error(routingContext, exception);
                }).onSuccess(data -> {
                    apiResponseHandler.success(routingContext);
                });

    }

    public void deleteById(RoutingContext routingContext) {
        log.debug("deleteById");

        long id = Long.parseLong(routingContext.pathParam("id"));

        mBiodataService.deleteById(id)
                .onFailure(exception -> {
                    apiExceptionHandler.error(routingContext, exception);
                }).onSuccess(data -> {
                    apiResponseHandler.success(routingContext);
                });
    }

    public void generate(RoutingContext routingContext) {
        log.debug("generate");

        Long accessUserId = routingContext.get("jwt_user_id");
        if (accessUserId == null) {
            accessUserId = 0L;
        }

        Long size = Long.parseLong(routingContext.pathParam("size"));

        List<JsonObject> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {

            JsonObject jsonObject = new JsonObject();

            // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd
            // HH:mm:ss");
            Date date = new Date();

            // set log data
            if (!jsonObject.containsKey("id")) {
                jsonObject.put("id", (date.getTime() / 2) + i);
            }
            jsonObject.put("createdBy", accessUserId);
            jsonObject.put("createdOn", LocalDateTime.now());
            jsonObject.put("isDelete", false);
            jsonObject.put("fullname", AppGenerator.randomName());
            jsonObject.put("mobilePhone", AppGenerator.generateMobileNumber("08", 10));
            String folder = AppGenerator.generateGibberish(6); 
            String fileName = AppGenerator.generateGibberish(7) + "." + AppGenerator.generateGibberish(3);;
            jsonObject.put("imagePath", "/home/user0/image/" + folder + "/" + fileName);
            jsonObject.remove("deletedBy");
            jsonObject.remove("deletedOn");
            jsonObject.remove("modifiedOn");
            jsonObject.remove("modifiedBy");

            list.add(jsonObject);
        }

        mBiodataService.saveAll(list)
                .onFailure(exception -> {
                    apiExceptionHandler.error(routingContext, exception);
                }).onSuccess(data -> {
                    apiResponseHandler.success(routingContext);
                });

    }

    public void getPage(RoutingContext routingContext) {
        log.debug("getPage");

        String stringPage = routingContext.queryParams().get("page");
        stringPage = stringPage != null ? stringPage : "";
        stringPage = !stringPage.isEmpty() ? stringPage : "0";
        int page = Integer.parseInt(stringPage);

        String stringSize = routingContext.queryParams().get("size");
        stringSize = stringSize != null ? stringSize : "";
        stringSize = !stringSize.isEmpty() ? stringSize : "5";
        int size = Integer.parseInt(stringSize);

        String stringFilter = routingContext.queryParams().get("filter");
        stringFilter = stringFilter != null ? stringFilter : "";
        stringFilter = !stringFilter.isEmpty() ? stringFilter : "[]";

        String stringSort = routingContext.queryParams().get("sort");
        stringSort = stringSort != null ? stringSort : "";
        stringSort = !stringSort.isEmpty() ? stringSort : "[]";

        String stringSearch = routingContext.queryParams().get("search");
        stringSearch = stringSearch != null ? stringSearch : "";

        List<FilterRequest> filterRequests = new FilterRequest().stringToList(stringFilter);
        List<SortRequest> sortRequests = new SortRequest().stringToList(stringSort);

        log.debug("request query param, filter: {}", filterRequests);
        log.debug("request query param, sort: {}", sortRequests);

        mBiodataService.getPage(page, size, filterRequests, sortRequests, stringSearch)
                .onFailure(exception -> {
                    apiExceptionHandler.error(routingContext, exception);
                })
                .onSuccess(data -> {
                    apiResponseHandler.success(routingContext, data);
                });
    }
}
