package io.github.karstenspang.subsets;

import java.math.BigInteger;


/**
 * Contains a single static method to calculate combinatorials.
 */
public class Combinatorial {
    
    /**
     * Calculates the combinatorial <img src="doc-files/comb.svg" alt="K(m,n)=\left(\begin{array}{c}m\\n\end{array}\right)=\frac{m!}{n!(m-n)!}" style="vertical-align:middle">.
     * This is the number of ways you can select {@code n} out of
     * {@code m} elements. In other words, the number of subsets
     * with {@code n} elements of a set with {@code m} elements.
     * @param m The number of elements to choose from
     * @param n The number of elements to choose
     * @return The number of ways to choose
     * @throws IllegalArgumentException if {@code m<0}, {@code n<0}, or {@code n>m}
     */
    // The image was produced by processing the alt text using https://viereck.ch/latex-to-svg/
    public static BigInteger comb(int m,int n){
        if (m<0 || n<0 || n>m) throw new IllegalArgumentException();
        if (n+n>m) n=m-n;
        BigInteger num=BigInteger.ONE;
        BigInteger den=BigInteger.ONE;
        BigInteger numfact=new BigInteger(String.valueOf(m));
        BigInteger denfact=BigInteger.ONE;
        for (int i=0;i<n;i++){
            num=num.multiply(numfact);
            den=den.multiply(denfact);
            numfact=numfact.subtract(BigInteger.ONE);
            denfact=denfact.add(BigInteger.ONE);
        }
        return num.divide(den);
    }
    
    private Combinatorial(){}
}
