package com.booktrad.wanted.dto;

import lombok.Data;

@Data
public class WantedQueryDTO {

    private String keyword;

    private Long categoryId;

    private Integer status;

    private String sortField;

    private String sortOrder;

    private Integer page;

    private Integer size;
}
