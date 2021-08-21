package com.squadb.workassistantapi.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * third party book api 의 메타 정보
 */
@Getter
public class BookSearchMeta {

    @JsonProperty("is_end")
    private boolean end;

    @JsonProperty("pageable_count")
    private int pageableCount;

    @JsonProperty("total_count")
    private int totalCount;
}
