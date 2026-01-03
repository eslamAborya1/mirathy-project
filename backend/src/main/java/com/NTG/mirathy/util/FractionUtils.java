package com.NTG.mirathy.util;

import com.NTG.mirathy.DTOs.Fraction;

import java.util.List;

public class FractionUtils {
    public static Fraction subtractSumFromOne(List<Fraction> fractions) {

        long commonDenominator = 1;
        for (Fraction f : fractions) {
            commonDenominator *= f.denominator();
        }


        long sumNumerator = 0;
        for (Fraction f : fractions) {
            long scaledNumerator = f.numerator() * (commonDenominator / f.denominator());
            sumNumerator += scaledNumerator;
        }


        long resultNumerator = commonDenominator - sumNumerator;


        long gcd = gcd(Math.abs(resultNumerator), commonDenominator);
        return new Fraction(resultNumerator / gcd, commonDenominator / gcd);
    }

    public static Fraction SumTwoFractions(Fraction fraction1, Fraction fraction2) {
        long numerator1 = fraction1.numerator();
        long denominator1 = fraction1.denominator();
        long numerator2 = fraction2.numerator();
        long denominator2 = fraction2.denominator();

        long resultNumerator = numerator1 * denominator2+numerator2*denominator1;
        long resultDenominator = denominator1 * denominator2;
        long gcd = gcd(resultNumerator, resultDenominator);
        return new Fraction(resultNumerator/gcd, resultDenominator/gcd);
    }

    public static Fraction divideTwoFractions(Fraction fraction1, Fraction fraction2) {

        if (fraction2.numerator() == 0) {
            throw new ArithmeticException("Cannot divide by fraction with zero numerator");
        }

        long resultNumerator = fraction1.numerator() * fraction2.denominator();
        long resultDenominator = fraction1.denominator() * fraction2.numerator();

        long gcd = gcd(Math.abs(resultNumerator), Math.abs(resultDenominator));

        return new Fraction(resultNumerator / gcd, resultDenominator / gcd);
    }


    public static String text(Fraction fraction) {
        return fraction.numerator() + "/" + fraction.denominator();
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

}
