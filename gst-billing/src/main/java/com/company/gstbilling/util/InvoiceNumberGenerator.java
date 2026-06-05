package com.company.gstbilling.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class InvoiceNumberGenerator {

    private static final AtomicInteger counter = new AtomicInteger(1000);

    /**
     * Generates a unique invoice number like: INV-20240601-1001
     */
    public static String generate() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = counter.incrementAndGet();
        return "INV-" + datePart + "-" + seq;
    }
}
