package io.github.amsatrio.vertx_crud_demo.dto.request;

import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;
import io.vertx.json.schema.common.dsl.Schemas;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {
    private String id;
    private String password;

    public ObjectSchemaBuilder validationSchemaBuilder() {
        return Schemas.objectSchema()
                .property("id", Schemas.stringSchema())
                .property("password", Schemas.stringSchema());
    }
}
