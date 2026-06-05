package com.company.gstbilling.service.impl;

import com.company.gstbilling.dto.request.BusinessRequest;
import com.company.gstbilling.dto.response.BusinessResponse;
import com.company.gstbilling.entity.Business;
import com.company.gstbilling.exception.InvalidInputException;
import com.company.gstbilling.exception.ResourceNotFoundException;
import com.company.gstbilling.repository.BusinessRepository;
import com.company.gstbilling.service.BusinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;

    @Override
    @Transactional
    public BusinessResponse createBusiness(BusinessRequest request) {
        log.debug("Creating business with GST: {}", request.getGstNumber());

        // Check duplicate GST number
        if (businessRepository.existsByGstNumber(request.getGstNumber())) {
            throw new InvalidInputException(
                    "Business with GST number " + request.getGstNumber() + " already exists"
            );
        }

        Business business = Business.builder()
                .businessName(request.getBusinessName())
                .gstNumber(request.getGstNumber().toUpperCase())
                .address(request.getAddress())
                .state(request.getState())
                .phone(request.getPhone())
                .build();

        Business saved = businessRepository.save(business);
        log.info("Business created with id: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessResponse getBusinessById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business", "id", id));
        return mapToResponse(business);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusinessResponse> getAllBusinesses(Pageable pageable) {
        return businessRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    // ── Mapper ────────────────────────────────────────────────────────────────

    private BusinessResponse mapToResponse(Business business) {
        return BusinessResponse.builder()
                .id(business.getId())
                .businessName(business.getBusinessName())
                .gstNumber(business.getGstNumber())
                .address(business.getAddress())
                .state(business.getState())
                .phone(business.getPhone())
                .createdAt(business.getCreatedAt())
                .build();
    }
}
