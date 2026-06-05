package com.company.gstbilling.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    // Unit price (snapshot at time of invoice)
    @Column(name = "price", nullable = false)
    private Double price;

    // GST amount for this line item
    @Column(name = "gst_amount")
    private Double gstAmount;

    // CGST (50% of gstAmount if intra-state)
    @Column(name = "cgst_amount")
    private Double cgstAmount;

    // SGST (50% of gstAmount if intra-state)
    @Column(name = "sgst_amount")
    private Double sgstAmount;

    // IGST (100% of gstAmount if inter-state)
    @Column(name = "igst_amount")
    private Double igstAmount;

    // price * quantity (before GST)
    @Column(name = "total_amount")
    private Double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
}
