package com.company.gstbilling.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalAmount;     // price * quantity
    private Double gstAmount;       // total GST on this item
    private Double cgstAmount;      // applicable if intra-state
    private Double sgstAmount;      // applicable if intra-state
    private Double igstAmount;      // applicable if inter-state
}
