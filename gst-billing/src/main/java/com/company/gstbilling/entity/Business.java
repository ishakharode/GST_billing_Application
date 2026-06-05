package com.company.gstbilling.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "businesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Business name cannot be null or blank")
    @Column(name = "business_name", nullable = false)
    private String businessName;

    @NotBlank(message = "GST number is required")
    @Size(min = 15, max = 15, message = "GST number must be exactly 15 characters")
    @Column(name = "gst_number", nullable = false, unique = true, length = 15)
    private String gstNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "state")
    private String state;

    @Column(name = "phone")
    private String phone;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "business", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> invoices = new ArrayList<>();
}
