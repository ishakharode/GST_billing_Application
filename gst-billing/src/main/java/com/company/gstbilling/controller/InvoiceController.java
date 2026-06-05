package com.company.gstbilling.controller;

import com.company.gstbilling.dto.request.InvoiceRequest;
import com.company.gstbilling.dto.response.ApiResponse;
import com.company.gstbilling.dto.response.InvoiceResponse;
import com.company.gstbilling.service.InvoiceService;
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
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    /**
     * POST /api/invoices
     * Create a new GST invoice with automatic GST calculation.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {

        InvoiceResponse response = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invoice created successfully", response));
    }

    /**
     * GET /api/invoices/{id}
     * Get a specific invoice with all line items.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(@PathVariable Long id) {
        InvoiceResponse response = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success("Invoice fetched successfully", response));
    }

    /**
     * GET /api/invoices?page=0&size=10&sortBy=invoiceDate&sortDir=desc
     * Get all invoices with pagination.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getAllInvoices(
            @RequestParam(defaultValue = "0")           int page,
            @RequestParam(defaultValue = "10")          int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "desc")        String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InvoiceResponse> invoices = invoiceService.getAllInvoices(pageable);
        return ResponseEntity.ok(ApiResponse.success("Invoices fetched successfully", invoices));
    }

    /**
     * GET /api/invoices/business/{businessId}?page=0&size=10
     * Get all invoices for a specific business.
     */
    @GetMapping("/business/{businessId}")
    public ResponseEntity<ApiResponse<Page<InvoiceResponse>>> getInvoicesByBusiness(
            @PathVariable Long businessId,
            @RequestParam(defaultValue = "0")           int page,
            @RequestParam(defaultValue = "10")          int size,
            @RequestParam(defaultValue = "invoiceDate") String sortBy,
            @RequestParam(defaultValue = "desc")        String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InvoiceResponse> invoices = invoiceService.getInvoicesByBusinessId(businessId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Business invoices fetched successfully", invoices));
    }
}
