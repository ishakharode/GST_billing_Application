package com.company.gstbilling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessRequest {

    @NotBlank(message = "Business name cannot be blank")
    private String businessName;

    @NotBlank(message = "GST number is required")
    @Size(min = 15, max = 15, message = "GST number must be exactly 15 characters")
    private String gstNumber;

    private String address;
    private String state;
    private String phone;
}
