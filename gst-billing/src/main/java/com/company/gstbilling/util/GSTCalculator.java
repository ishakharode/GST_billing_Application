package com.company.gstbilling.util;

import com.company.gstbilling.exception.InvalidInputException;

import java.util.List;
import java.util.Set;

public class GSTCalculator {

    private static final Set<Double> VALID_GST_RATES = Set.of(0.0, 5.0, 12.0, 18.0, 28.0);

    /**
     * Validates that the given GST percentage is one of the allowed values.
     */
    public static void validateGSTPercentage(Double gstPercentage) {
        if (!VALID_GST_RATES.contains(gstPercentage)) {
            throw new InvalidInputException(
                    "Invalid GST percentage: " + gstPercentage +
                    ". Allowed values are: 0, 5, 12, 18, 28"
            );
        }
    }

    /**
     * Calculates the base amount (price * quantity) — before GST.
     */
    public static double calculateBaseAmount(double price, int quantity) {
        return price * quantity;
    }

    /**
     * Calculates the GST amount for a line item.
     * Formula: (price × quantity × gstPercentage) / 100
     */
    public static double calculateGSTAmount(double price, int quantity, double gstPercentage) {
        return (price * quantity * gstPercentage) / 100;
    }

    /**
     * Determines whether CGST+SGST or IGST applies.
     *
     * Rule:
     *   - Same state → CGST + SGST (50% each of GST amount)
     *   - Different states → IGST (100% of GST amount)
     */
    public static boolean isIntraState(String businessState, String customerState) {
        if (businessState == null || customerState == null) return false;
        return businessState.trim().equalsIgnoreCase(customerState.trim());
    }

    /**
     * Round to 2 decimal places
     */
    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
