package com.booktrad.wanted.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WantedMatchBookVO {

    private Long bookId;

    private Long sellerId;

    private String sellerName;

    private Double sellerRatingScore;

    private String title;

    private String author;

    private Long categoryId;

    private BigDecimal price;

    private String coverImg;

    private String preferredLocation;
}
