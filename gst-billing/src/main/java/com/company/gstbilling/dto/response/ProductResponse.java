package com.company.gstbilling.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String productName;
    private Double price;
    private Double gstPercentage;
    private LocalDateTime createdAt;
}
