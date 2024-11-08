package com.bongbong.mineage.duels.velocity.utils;

import java.math.RoundingMode;

public final class NumberUtil {
    private NumberUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Integer getInteger(final String s) {
        try {
            return Integer.parseInt(s);
        } catch (final NumberFormatException ex) {
            return null;
        }
    }

    public static Double getDouble(final String s) {
        try {
            return Double.parseDouble(s);
        } catch (final NumberFormatException ex) {
            return null;
        }
    }

    public static double roundUp(final Number number, final int decimalPlaces) {
        return Double.parseDouble(JavaUtils.format(number, decimalPlaces, RoundingMode.HALF_UP));
    }

    public static double roundDown(final Number number, final int decimalPlaces) {
        return Double.parseDouble(JavaUtils.format(number, decimalPlaces, RoundingMode.HALF_DOWN));
    }
}

