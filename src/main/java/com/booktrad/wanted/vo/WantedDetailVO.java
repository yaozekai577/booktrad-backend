package com.booktrad.wanted.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WantedDetailVO {

    private Long id;

    private Long buyerId;

    private String buyerName;

    private Double buyerRatingScore;

    private Integer buyerRatingCount;

    private String title;

    private String author;

    private Long categoryId;

    private BigDecimal budget;

    private String desiredCondition;

    private String expectedLocation;

    private String description;

    private String contactPhone;

    private Integer status;

    private String closeReason;

    private LocalDateTime closedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
