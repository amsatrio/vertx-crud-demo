package io.github.amsatrio.vertx_crud_demo.modules.hello_world;

import io.vertx.ext.web.validation.RequestPredicate;
import io.vertx.ext.web.validation.ValidationHandler;
import io.vertx.ext.web.validation.builder.Bodies;
import io.vertx.ext.web.validation.builder.Parameters;
import io.vertx.ext.web.validation.builder.ValidationHandlerBuilder;
import io.vertx.json.schema.SchemaRepository;
import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.common.dsl.Schemas;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class HelloWorld {
    private String name;


    public static ObjectSchemaBuilder validationSchemaBuilder() {
        return Schemas.objectSchema()
                .property("name", Schemas.stringSchema());
    }


    public static ValidationHandler validationBodyHandler(SchemaRepository schemaRepository) {
        return ValidationHandlerBuilder
                .create(schemaRepository)
                .body(Bodies.json(validationSchemaBuilder()))
                .body(Bodies.formUrlEncoded(validationSchemaBuilder()))
                .predicate(RequestPredicate.BODY_REQUIRED)
                .build();
    }
    public static ValidationHandler validationPathParamNameHandler(SchemaRepository schemaRepository) {
        log.info("validationPathParamIdHandler");
        return ValidationHandlerBuilder
                .create(schemaRepository)
                .pathParameter(Parameters.param(
                        "name",
                        Schemas.stringSchema()))
                .build();
    }
}
