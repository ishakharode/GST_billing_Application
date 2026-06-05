package com.company.gstbilling.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String customerName;
    private String customerState;
    private String gstType;         // CGST_SGST or IGST
    private Double totalAmount;     // sum of all item totals before GST
    private Double totalGST;        // total GST amount
    private Double grandTotal;      // totalAmount + totalGST
    private Long businessId;
    private String businessName;
    private String businessGstNumber;
    private String businessState;
    private LocalDateTime createdAt;
    private List<InvoiceItemResponse> items;
}
