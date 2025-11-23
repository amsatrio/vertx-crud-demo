package io.github.amsatrio.vertx_crud_demo.dto.request;


import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.vertx.core.json.jackson.DatabindCodec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SortRequest implements Serializable {
    private String id;
    private boolean desc;

        public List<SortRequest> stringToList(String input) {
                ObjectMapper objectMapper = DatabindCodec.mapper().registerModule(new JavaTimeModule());
        try {
            return objectMapper.readValue(input, new TypeReference<List<SortRequest>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize response from String", e);
        }
    }
}
