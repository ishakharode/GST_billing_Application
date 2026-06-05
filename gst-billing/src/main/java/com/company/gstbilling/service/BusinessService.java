package com.company.gstbilling.service;

import com.company.gstbilling.dto.request.BusinessRequest;
import com.company.gstbilling.dto.response.BusinessResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusinessService {

    BusinessResponse createBusiness(BusinessRequest request);

    BusinessResponse getBusinessById(Long id);

    Page<BusinessResponse> getAllBusinesses(Pageable pageable);
}
