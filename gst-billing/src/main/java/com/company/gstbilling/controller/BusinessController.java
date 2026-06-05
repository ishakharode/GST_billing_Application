package com.company.gstbilling.controller;

import com.company.gstbilling.dto.request.BusinessRequest;
import com.company.gstbilling.dto.response.ApiResponse;
import com.company.gstbilling.dto.response.BusinessResponse;
import com.company.gstbilling.service.BusinessService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    /**
     * POST /api/business
     * Register a new business.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BusinessResponse>> createBusiness(
            @Valid @RequestBody BusinessRequest request) {

        BusinessResponse response = businessService.createBusiness(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Business registered successfully", response));
    }

    /**
     * GET /api/business/{id}
     * Get business by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessResponse>> getBusinessById(@PathVariable Long id) {
        BusinessResponse response = businessService.getBusinessById(id);
        return ResponseEntity.ok(ApiResponse.success("Business fetched successfully", response));
    }

    /**
     * GET /api/business?page=0&size=10&sortBy=businessName&sortDir=asc
     * Get all businesses with pagination.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BusinessResponse>>> getAllBusinesses(
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size,
            @RequestParam(defaultValue = "id")   String sortBy,
            @RequestParam(defaultValue = "asc")  String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BusinessResponse> businesses = businessService.getAllBusinesses(pageable);
        return ResponseEntity.ok(ApiResponse.success("Businesses fetched successfully", businesses));
    }
}
