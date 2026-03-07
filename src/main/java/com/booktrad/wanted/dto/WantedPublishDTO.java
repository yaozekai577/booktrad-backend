package com.booktrad.wanted.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WantedPublishDTO {

    private String title;

    private String author;

    private Long categoryId;

    private BigDecimal budget;

    private String desiredCondition;

    private String expectedLocation;

    private String description;

    private String contactPhone;
}
