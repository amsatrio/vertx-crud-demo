package io.github.amsatrio.vertx_crud_demo.dto.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponse {
    private String name;
    private String hash;
    private Long size;
    private String type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdOn;
    private Long createdBy;
}
