package io.github.karstenspang.subsets;

import java.math.BigInteger;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BigFractionTest {
    @Test
    @DisplayName("Bad arguments throw exceptions")
    void badArgs(){
        assertThrows(IllegalArgumentException.class,()->new BigFraction(new BigInteger("1"),new BigInteger("0")));
        assertThrows(NullPointerException.class,()->new BigFraction(null,new BigInteger("0")));
        assertThrows(NullPointerException.class,()->new BigFraction(new BigInteger("0"),null));
        assertThrows(NullPointerException.class,()->new BigFraction(null));
    }
    
    static Stream<Arguments> sameProvider(){
        return Stream.of(
            arguments("0","1",BigInteger.ZERO,BigInteger.ONE,BigInteger.ZERO,BigFraction.ZERO),
            arguments("2","3",null,null,BigInteger.ZERO,null),
            arguments("10","5",null,BigInteger.ONE,null,BigFraction.ZERO)
        );
    }
    
    @DisplayName("Methods return values that are identical to what they are supposed to be")
    @ParameterizedTest
    @MethodSource("sameProvider")
    void testSame(String n,String d,BigInteger num,BigInteger den,BigInteger ip,BigFraction fp)
    {
        BigFraction fr=new BigFraction(new BigInteger(n),new BigInteger(d));
        if (num!=null) assertSame(num,fr.numerator(),"numerator");
        if (den!=null) assertSame(den,fr.denominator(),"denominator");
        if (ip!=null) assertSame(ip,fr.integerPart(),"integer");
        if (fp!=null) assertSame(fp,fr.fractionalPart(),"fractional");
    }
    
    static Stream<Arguments> equalsProvider(){
        return Stream.of(
            arguments("0","1","0","1","0","0","1","0"),
            arguments("2","3","2","3","0","2","3","2/3"),
            arguments("10","5","2","1","2","0","1","2"),
            arguments("-2","-4","1","2","0","1","2","1/2"),
            arguments("2","-4","-1","2","0","-1","2","-1/2"),
            arguments("3","-2","-3","2","-1","-1","2","-3/2")
        );
    }
    
    @DisplayName("Methods return values that are equal to what they are supposed to be")
    @ParameterizedTest
    @MethodSource("equalsProvider")
    void testEquals(String n,String d,String num,String den,String ip,String fpn,String fpd,String str)
    {
        BigFraction fr=new BigFraction(new BigInteger(n),new BigInteger(d));
        assertEquals(new BigInteger(num),fr.numerator(),"numerator");
        assertEquals(new BigInteger(den),fr.denominator(),"denominator");
        assertEquals(new BigInteger(ip),fr.integerPart(),"integer");
        assertEquals(new BigFraction(new BigInteger(fpn),new BigInteger(fpd)),fr.fractionalPart(),"fraction");
        assertEquals(str,fr.toString(),"string");
    }
    
    static Stream<Arguments> opsProvider(){
        return Stream.of(
            arguments("1","2","1","3","5/6","1/6","1/6","3/2"),
            arguments("1","-2","2","3","1/6","-7/6","-1/3","-3/4")
        );
    }
    
    @DisplayName("Arithmetic operations return their expected result")
    @ParameterizedTest
    @MethodSource("opsProvider")
    void testOps(String n1,String d1,String n2,String d2,String sa,String ss,String sm,String sd)
    {
        BigFraction fr1=new BigFraction(new BigInteger(n1),new BigInteger(d1));
        BigFraction fr2=new BigFraction(new BigInteger(n2),new BigInteger(d2));
        assertEquals(sa,fr1.add(fr2).toString(),"add");
        assertEquals(ss,fr1.subtract(fr2).toString(),"subtract");
        assertEquals(sm,fr1.multiply(fr2).toString(),"multiply");
        assertEquals(sd,fr1.divide(fr2).toString(),"divide");
    }
    
    @Test
    @DisplayName("Divide by 0 throws ArithmeticException")
    void testDivide0()
    {
        BigFraction fr1=new BigFraction(BigInteger.ONE);
        BigFraction fr2=new BigFraction(BigInteger.ZERO);
        assertThrows(ArithmeticException.class,()->fr1.divide(fr2));
    }
    
    @Test
    @DisplayName("not equal to some other kind of object")
    void testNotEqualToAnythingElse()
    {
        assertFalse(new BigFraction(BigInteger.ONE).equals(BigInteger.ONE));
    }
    
    @Test
    @DisplayName("equals to itself")
    void testEqualToSelf()
    {
        BigFraction fr=new BigFraction(BigInteger.ONE);
        assertTrue(fr.equals(fr));
    }
    
    @Test
    @DisplayName("hashCode returns non-zero value")
    void testHash()
    {
        BigFraction fr=new BigFraction(BigInteger.ONE);
        assertTrue(fr.hashCode()!=0);
    }
}
