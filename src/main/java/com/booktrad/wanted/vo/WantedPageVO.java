package com.booktrad.wanted.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WantedPageVO {

    private Long id;

    private Long buyerId;

    private String buyerName;

    private Double buyerRatingScore;

    private String title;

    private String author;

    private Long categoryId;

    private BigDecimal budget;

    private String desiredCondition;

    private String expectedLocation;

    private String description;

    private Integer status;

    private LocalDateTime createdAt;

    private Integer isOwner;
}
