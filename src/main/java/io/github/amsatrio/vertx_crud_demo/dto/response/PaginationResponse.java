package io.github.amsatrio.vertx_crud_demo.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationResponse<T> implements Serializable {
    private List<T> content;
    private Boolean last;
    private Boolean first;
    private Long totalElements;
    private Long totalPages;

    // === GETTER

    public List<T> getContent() {
        return content == null ? null : new ArrayList<>(content);
    }

    // === SETTER

    public void setContent(List<T> content) {
        this.content = content == null ? null : new ArrayList<>(content);
    }

}
