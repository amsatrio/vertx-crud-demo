package io.github.amsatrio.vertx_crud_demo.modules.health;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HealthCache {
    private String status;
    private Float latency;
}
