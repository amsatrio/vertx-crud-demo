package io.github.amsatrio.vertx_crud_demo.modules.m_biodata;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.format.SnakeCase;
import io.vertx.codegen.json.annotations.JsonGen;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.validation.RequestPredicate;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.Draft;
import io.vertx.json.schema.JsonSchemaOptions;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.json.schema.common.dsl.Keywords;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.common.dsl.Schemas;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.annotations.Column;
import io.vertx.sqlclient.templates.annotations.ParametersMapped;
import io.vertx.sqlclient.templates.annotations.RowMapped;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@JsonGen
@RowMapped(formatter = SnakeCase.class)
@ParametersMapped(formatter = SnakeCase.class)
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Slf4j
public class MBiodata implements Serializable {

    @JsonProperty("id")
    @Column(name = "id")
    private Long id;

    @JsonProperty("fullname")
    @Column(name = "fullname")
    private String fullname;

    @JsonProperty("mobilePhone")
    @Column(name = "mobile_phone")
    private String mobilePhone;

    @JsonProperty("image")
    @Column(name = "image")
    private Object image;

    @JsonProperty("imagePath")
    @Column(name = "image_path")
    private String imagePath;

    @JsonProperty("createdBy")
    @Column(name = "created_by")
    private Long createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("createdOn")
    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @JsonProperty("modifiedBy")
    @Column(name = "modified_by")
    private Long modifiedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("modifiedOn")
    @Column(name = "modified_on")
    private LocalDateTime modifiedOn;

    @JsonProperty("deletedBy")
    @Column(name = "deleted_by")
    private Long deletedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("deletedOn")
    @Column(name = "deleted_on")
    private LocalDateTime deletedOn;

    @JsonProperty("isDelete")
    @Column(name = "is_delete")
    private Boolean isDelete;

    public static final RowMapper<MBiodata> INSTANCE = new RowMapper<MBiodata>() {
        @Override
        public MBiodata map(Row row) {
            MBiodata mBiodata = new MBiodata();

            mBiodata.setId(row.getLong("id"));
            mBiodata.setFullname(row.getString("fullname"));
            mBiodata.setMobilePhone(row.getString("mobile_phone"));
            mBiodata.setImage(row.getBuffer("image") == null ? null
                    : Base64.getEncoder().encodeToString(row.getBuffer("image").getBytes()));
            mBiodata.setImagePath(row.getString("image_path"));
            mBiodata.setCreatedBy(row.getLong("created_by"));
            mBiodata.setCreatedOn(row.getLocalDateTime("created_on"));
            mBiodata.setModifiedBy(row.getLong("modified_by"));
            mBiodata.setModifiedOn(row.getLocalDateTime("modified_on"));
            mBiodata.setDeletedBy(row.getLong("deleted_by"));
            mBiodata.setDeletedOn(row.getLocalDateTime("deleted_on"));
            mBiodata.setIsDelete(row.getBoolean("is_delete"));

            return mBiodata;
        }
    };

    public JsonObject toJsonObject() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        JsonObject jsonObject = new JsonObject();
        jsonObject.put("id", id);
        jsonObject.put("fullname", fullname);
        jsonObject.put("mobilePhone", mobilePhone);
        jsonObject.put("image", image);
        jsonObject.put("imagePath", imagePath);
        jsonObject.put("createdBy", createdBy);
        jsonObject.put("createdOn", createdOn == null ? null : createdOn.format(dateTimeFormatter));
        jsonObject.put("modifiedBy", modifiedBy);
        jsonObject.put("modifiedOn", modifiedOn == null ? null : modifiedOn.format(dateTimeFormatter));
        jsonObject.put("deletedBy", deletedBy);
        jsonObject.put("deletedOn", deletedOn == null ? null : deletedOn.format(dateTimeFormatter));
        jsonObject.put("isDelete", isDelete);

        return jsonObject;
    }

    public static ValidationHandler validationBodyHandler() {
        SchemaRepository schemaRepository = SchemaRepository
            .create(new JsonSchemaOptions().setBaseUri("https://vertx.io").setDraft(Draft.DRAFT7));
        ObjectSchemaBuilder objectSchemaBuilder = Schemas.objectSchema()
                .property("id", Schemas.numberSchema())
                .property("fullname", Schemas.stringSchema().nullable().with(Keywords.maxLength(255)))
                .property("mobilePhone", Schemas.stringSchema().nullable().with(Keywords.maxLength(15)))
                .property("image", Schemas.stringSchema().nullable())
                .property("imagePath", Schemas.stringSchema().nullable().with(Keywords.maxLength(255)))
                .property("isDelete", Schemas.booleanSchema().nullable());

        return ValidationHandlerBuilder
                .create(schemaRepository)
                .body(Bodies.json(objectSchemaBuilder))
                .body(Bodies.formUrlEncoded(objectSchemaBuilder))
                .predicate(RequestPredicate.BODY_REQUIRED)
                .build();
    }
    public static ValidationHandler validationPathParamIdHandler() {
        SchemaRepository schemaRepository = SchemaRepository
            .create(new JsonSchemaOptions().setBaseUri("https://vertx.io").setDraft(Draft.DRAFT7));
        return ValidationHandlerBuilder
                .create(schemaRepository)
                .pathParameter(Parameters.param(
                        "id",
                        Schemas.numberSchema().with(Keywords.minimum(1))))
                .build();
    }

}
