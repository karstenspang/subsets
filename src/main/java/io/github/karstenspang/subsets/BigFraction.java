package io.github.karstenspang.subsets;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

/**
 * An immutable, arbitrary precision rational number.
 * It is based on {@link BigInteger}, and have the same restrictions.
 * It is normalized such that:
 * <ul>
 *  <li>The denominator is positive.</li>
 *  <li>The numerator and denominator are relative prime.</li>
 *  <li>For whole numbers, the denominator is identically {@link BigInteger#ONE}.</li>
 *  <li>For {@code 0}, the numerator is identically {@link BigInteger#ZERO}.</li>
 * </ul>
 */
public class BigFraction {
    private final BigInteger numerator;
    private final BigInteger denominator;
    
    /** The value {@code 0} */
    public static final BigFraction ZERO=new BigFraction(BigInteger.ZERO);
    
    /**
     * Construct the fraction from numerator and denominator.
     * @param numerator Numerator of the fraction.
     * @param denominator Denominator of the fraction.
     * @throws NullPointerException if any argument is {@code null}.
     * @throws IllegalArgumentException if {@code denominator} is {@code 0}.
     */
    public BigFraction(BigInteger numerator,BigInteger denominator){
        Objects.requireNonNull(numerator,"numerator is null");
        int sign=Objects.requireNonNull(denominator,"denominator is null").signum();
        if (sign==0) throw new IllegalArgumentException("denominator is 0");
        if (sign<0){
            denominator=denominator.negate();
            numerator=numerator.negate();
        }
        if (BigInteger.ZERO.equals(numerator)){
            this.numerator=BigInteger.ZERO;
            this.denominator=BigInteger.ONE;
        }
        else{
            BigInteger gcd=denominator.gcd(numerator);
            if (!BigInteger.ONE.equals(gcd)){
                numerator=numerator.divide(gcd);
                denominator=denominator.divide(gcd);
            }
            if (BigInteger.ONE.equals(denominator)){
                denominator=BigInteger.ONE;
            }
            this.numerator=numerator;
            this.denominator=denominator;
        }
    }
    
    /**
     * Construct a whole number from the number alone.
     * @param number Value of the fraction
     * @throws NullPointerException if {@code number} is {@code null}.
     */
    public BigFraction(BigInteger number){
        Objects.requireNonNull(number,"number is null");
        if (BigInteger.ZERO.equals(number)){
            number=BigInteger.ZERO;
        }
        this.numerator=number;
        this.denominator=BigInteger.ONE;
    }
    
    /**
     * Get the numerator of {@code this}.
     * @return the numerator. If {@code this} is {@code 0}, {@link BigInteger#ZERO} is returned.
     */
    public BigInteger numerator(){return numerator;}
    
    /**
     * Get the denominator of {@code this}.
     * @return the denominator. If {@code this} is a whole number, {@link BigInteger#ONE} is returned.
     */
    public BigInteger denominator(){return denominator;}
    
    /**
     * Get the fractional part. Its sign is the same as that of {@code this}.
     * If {@code this} is a whole number, {@link #ZERO} is returned.
     * If {@code this} is a proper fraction, {@code this} is returned.
     * @return the fractional part of {@code this}.
     */
    public BigFraction fractionalPart(){
        if (denominator==BigInteger.ONE) return ZERO;
        if (numerator.abs().compareTo(denominator)<0) return this;
        return new BigFraction(numerator.remainder(denominator),denominator);
    }
    
    /**
     * Get the integer part. Its sign is the same as that of {@code this}.
     * If {@code this} is a proper fraction (including {@code 0}), {@link BigInteger#ZERO} is returned.
     * @return the integer part of {@code this}.
     */
    public BigInteger integerPart(){
        if (denominator==BigInteger.ONE) return numerator;
        if (numerator.abs().compareTo(denominator)<0) return BigInteger.ZERO;
        return numerator.divide(denominator);
    }
    
    /**
     * Get the string representation of {@code this}.
     * @return if a whole number, the string represenation of that, otherwhise
     *      the string representations of the numerator and denominator
     *      separated by {@code /}.
     */
    public String toString(){
        if (denominator==BigInteger.ONE) return numerator.toString();
        return numerator.toString()+"/"+denominator.toString();
    }
    
    /**
     * Add another fraction to {@code this}.
     * @param addend fraction to add
     * @return the result of the addition
     */
    public BigFraction add(BigFraction addend){
        return new BigFraction(this.numerator.multiply(addend.denominator).add(addend.numerator.multiply(this.denominator)),this.denominator.multiply(addend.denominator));
    }
    
    /**
     * Subtract another fraction from {@code this}.
     * @param subtrahend fraction to subtract
     * @return the result of the subtraction
     */
    public BigFraction subtract(BigFraction subtrahend){
        return new BigFraction(this.numerator.multiply(subtrahend.denominator).subtract(subtrahend.numerator.multiply(this.denominator)),this.denominator.multiply(subtrahend.denominator));
    }
    
    /**
     * Multiply {@code this} by another fraction.
     * @param factor fraction to multiply by
     * @return the result of the multiplication
     */
    public BigFraction multiply(BigFraction factor){
        return new BigFraction(this.numerator.multiply(factor.numerator),this.denominator.multiply(factor.denominator));
    }
    
    /**
     * Divide {@code this} by another fraction.
     * @param divisor fraction to divide by
     * @return the result of the division
     * @throws ArithmeticException if the divisor is {@code 0}.
     */
    public BigFraction divide(BigFraction divisor){
        if (divisor.numerator==BigInteger.ZERO) throw new ArithmeticException("divisor is 0");
        return new BigFraction(this.numerator.multiply(divisor.denominator),this.denominator.multiply(divisor.numerator));
    }
    
    /**
     * Test if another object is equal to {@code this}.
     * Since {@link BigFraction}s are always normalized, they
     * have the same value if their numerators and denominators match.
     * @param other Object to compare.
     * @return {@code true} if {@code other} is a {@link BigFraction} and
     *         has the same value as {@code this}, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other)
    {
        if (other==this) return true;
        if (!(other instanceof BigFraction)) return false;
        BigFraction that=(BigFraction)other;
        Object[] ours=new Object[]{this.numerator,this.denominator};
        Object[] theirs=new Object[]{that.numerator,that.denominator};
        return Arrays.equals(ours,theirs);
    }
    
    /**
     * Hash code for {@link BigFraction}.
     * @return the hash code
     */
    @Override
    public int hashCode(){
        return Objects.hash(numerator,denominator);
    }
}
