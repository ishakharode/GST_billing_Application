package com.company.gstbilling;

import com.company.gstbilling.exception.InvalidInputException;
import com.company.gstbilling.util.GSTCalculator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GSTCalculatorTest {

    @Test
    void testIntraStateDetection() {
        assertTrue(GSTCalculator.isIntraState("Maharashtra", "Maharashtra"));
        assertTrue(GSTCalculator.isIntraState("maharashtra", "MAHARASHTRA"));
        assertFalse(GSTCalculator.isIntraState("Maharashtra", "Gujarat"));
    }

    @Test
    void testGSTAmountCalculation() {
        // price=100, qty=3, gst=18% → (100 * 3 * 18) / 100 = 54
        double gst = GSTCalculator.calculateGSTAmount(100.0, 3, 18.0);
        assertEquals(54.0, gst, 0.001);
    }

    @Test
    void testBaseAmountCalculation() {
        double base = GSTCalculator.calculateBaseAmount(100.0, 3);
        assertEquals(300.0, base, 0.001);
    }

    @Test
    void testValidGSTPercentages() {
        // Should not throw
        assertDoesNotThrow(() -> GSTCalculator.validateGSTPercentage(0.0));
        assertDoesNotThrow(() -> GSTCalculator.validateGSTPercentage(5.0));
        assertDoesNotThrow(() -> GSTCalculator.validateGSTPercentage(12.0));
        assertDoesNotThrow(() -> GSTCalculator.validateGSTPercentage(18.0));
        assertDoesNotThrow(() -> GSTCalculator.validateGSTPercentage(28.0));
    }

    @Test
    void testInvalidGSTPercentage() {
        assertThrows(InvalidInputException.class,
                () -> GSTCalculator.validateGSTPercentage(15.0));
    }

    @Test
    void testCGSTSGSTSplit() {
        // intra-state: 18% GST on 1000 = 180 total → 90 CGST + 90 SGST
        double gstAmount = GSTCalculator.calculateGSTAmount(1000.0, 1, 18.0);
        double cgst = GSTCalculator.round(gstAmount / 2);
        double sgst = GSTCalculator.round(gstAmount / 2);
        assertEquals(90.0, cgst, 0.001);
        assertEquals(90.0, sgst, 0.001);
        assertEquals(180.0, cgst + sgst, 0.001);
    }

    @Test
    void testIGSTInterState() {
        // inter-state: 18% GST on 1000 = 180 full IGST
        double gstAmount = GSTCalculator.calculateGSTAmount(1000.0, 1, 18.0);
        double igst = GSTCalculator.round(gstAmount);
        assertEquals(180.0, igst, 0.001);
    }

    @Test
    void testGrandTotalFormula() {
        double price = 500.0;
        int qty = 2;
        double gstPct = 12.0;

        double base = GSTCalculator.calculateBaseAmount(price, qty);   // 1000
        double gst  = GSTCalculator.calculateGSTAmount(price, qty, gstPct); // 120
        double grand = GSTCalculator.round(base + gst);                // 1120

        assertEquals(1000.0, base, 0.001);
        assertEquals(120.0,  gst,  0.001);
        assertEquals(1120.0, grand, 0.001);
    }
}
