package com.company.gstbilling.service.impl;

import com.company.gstbilling.dto.request.InvoiceItemRequest;
import com.company.gstbilling.dto.request.InvoiceRequest;
import com.company.gstbilling.dto.response.InvoiceItemResponse;
import com.company.gstbilling.dto.response.InvoiceResponse;
import com.company.gstbilling.entity.*;
import com.company.gstbilling.exception.ResourceNotFoundException;
import com.company.gstbilling.repository.BusinessRepository;
import com.company.gstbilling.repository.InvoiceRepository;
import com.company.gstbilling.repository.ProductRepository;
import com.company.gstbilling.service.InvoiceService;
import com.company.gstbilling.util.GSTCalculator;
import com.company.gstbilling.util.InvoiceNumberGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;

    // ── Create Invoice ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        log.debug("Creating invoice for businessId: {}", request.getBusinessId());

        // 1. Fetch business
        Business business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business", "id", request.getBusinessId()));

        // 2. Determine GST type (intra-state → CGST+SGST, inter-state → IGST)
        boolean intraState = GSTCalculator.isIntraState(business.getState(), request.getCustomerState());
        String gstType = intraState ? "CGST_SGST" : "IGST";

        // 3. Build invoice shell
        Invoice invoice = Invoice.builder()
                .invoiceNumber(InvoiceNumberGenerator.generate())
                .invoiceDate(LocalDate.now())
                .customerName(request.getCustomerName())
                .customerState(request.getCustomerState())
                .gstType(gstType)
                .business(business)
                .totalAmount(0.0)
                .totalGST(0.0)
                .grandTotal(0.0)
                .items(new ArrayList<>())
                .build();

        // 4. Process each line item
        double totalBaseAmount = 0.0;
        double totalGSTAmount = 0.0;

        List<InvoiceItem> invoiceItems = new ArrayList<>();

        for (InvoiceItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));

            double baseAmount = GSTCalculator.calculateBaseAmount(product.getPrice(), itemReq.getQuantity());
            double gstAmount  = GSTCalculator.calculateGSTAmount(product.getPrice(), itemReq.getQuantity(), product.getGstPercentage());

            // Distribute GST based on intra/inter state rule
            double cgst = intraState ? GSTCalculator.round(gstAmount / 2) : 0.0;
            double sgst = intraState ? GSTCalculator.round(gstAmount / 2) : 0.0;
            double igst = intraState ? 0.0 : GSTCalculator.round(gstAmount);

            InvoiceItem item = InvoiceItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .price(product.getPrice())
                    .totalAmount(GSTCalculator.round(baseAmount))
                    .gstAmount(GSTCalculator.round(gstAmount))
                    .cgstAmount(cgst)
                    .sgstAmount(sgst)
                    .igstAmount(igst)
                    .invoice(invoice)
                    .build();

            invoiceItems.add(item);

            totalBaseAmount += baseAmount;
            totalGSTAmount  += gstAmount;
        }

        // 5. Set totals on invoice
        invoice.setItems(invoiceItems);
        invoice.setTotalAmount(GSTCalculator.round(totalBaseAmount));
        invoice.setTotalGST(GSTCalculator.round(totalGSTAmount));
        invoice.setGrandTotal(GSTCalculator.round(totalBaseAmount + totalGSTAmount));

        // 6. Save (cascades to items)
        Invoice saved = invoiceRepository.save(invoice);
        log.info("Invoice created: {} for business: {}", saved.getInvoiceNumber(), business.getBusinessName());

        return mapToResponse(saved);
    }

    // ── Read APIs ───────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return mapToResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesByBusinessId(Long businessId, Pageable pageable) {
        // Ensure business exists
        if (!businessRepository.existsById(businessId)) {
            throw new ResourceNotFoundException("Business", "id", businessId);
        }
        return invoiceRepository.findByBusinessId(businessId, pageable)
                .map(this::mapToResponse);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────

    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceItemResponse> itemResponses = invoice.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .invoiceDate(invoice.getInvoiceDate())
                .customerName(invoice.getCustomerName())
                .customerState(invoice.getCustomerState())
                .gstType(invoice.getGstType())
                .totalAmount(invoice.getTotalAmount())
                .totalGST(invoice.getTotalGST())
                .grandTotal(invoice.getGrandTotal())
                .businessId(invoice.getBusiness().getId())
                .businessName(invoice.getBusiness().getBusinessName())
                .businessGstNumber(invoice.getBusiness().getGstNumber())
                .businessState(invoice.getBusiness().getState())
                .createdAt(invoice.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    private InvoiceItemResponse mapItemToResponse(InvoiceItem item) {
        return InvoiceItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getProductName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .totalAmount(item.getTotalAmount())
                .gstAmount(item.getGstAmount())
                .cgstAmount(item.getCgstAmount())
                .sgstAmount(item.getSgstAmount())
                .igstAmount(item.getIgstAmount())
                .build();
    }
}
