package com.company.gstbilling.service;

import com.company.gstbilling.dto.request.InvoiceRequest;
import com.company.gstbilling.dto.response.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request);

    InvoiceResponse getInvoiceById(Long id);

    Page<InvoiceResponse> getAllInvoices(Pageable pageable);

    Page<InvoiceResponse> getInvoicesByBusinessId(Long businessId, Pageable pageable);
}
