package com.company.gstbilling.repository;

import com.company.gstbilling.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Page<Invoice> findByBusinessId(Long businessId, Pageable pageable);

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    long countByBusinessId(Long businessId);
}
