package com.company.gstbilling.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessResponse {

    private Long id;
    private String businessName;
    private String gstNumber;
    private String address;
    private String state;
    private String phone;
    private LocalDateTime createdAt;
}
