package jgp;

import java.math.*;
import static java.math.BigInteger.valueOf;
import static jgp.Ind.big2string;

import prolog.core.BigMath;
import prolog.logic.Fun;
import prolog.logic.Var;

/**
 *
 * @author Brad
 */
public class Eval extends BigMath {
  
    /**
     *
     * @param X
     * @param Y
     * @return
     */
    public final BigInteger nand(BigInteger X,BigInteger Y) {
    X=X.and(Y);
    return bignot(npowers,X);
  }
  
    /**
     *
     * @param X
     * @param Y
     * @return
     */
    public final BigInteger nor(BigInteger X,BigInteger Y) {
    X=X.or(Y);
    return bignot(npowers,X);
  }
  
    /**
     *
     * @param X
     * @param Y
     * @return
     */
    public final BigInteger impl(BigInteger X,BigInteger Y) {
    X=bignot(npowers,X);
    return X.or(Y);
  }
  
    /**
     *
     * @param X
     * @param nbits
     * @return
     */
    public static final BigInteger negate(BigInteger X,int nbits) {
    X=bignot(nbits,X);
    return X;
  }
  
  //GROWING:11101000 11010101=>11010101 00101010

    /**
     *
     * @param X
     * @param nbits
     * @return
     */
      public static final BigInteger grow(BigInteger X,int nbits) {
    int halfpow=nbits>>1;
    BigInteger lmask=bigones(halfpow);
    BigInteger low=lmask.and(X);
    BigInteger hi=low.shiftLeft(halfpow);
    low=bignot(halfpow,low);
    BigInteger R=hi.or(low);
    /*
    System.out.println("GROWING:"+
        Ind.big2string(X,nbits)+"=>"+
        Ind.big2string(R,nbits)); 
    */
    return R;
  }
  
    /**
     *
     * @param X
     * @param Y
     * @return
     */
    public static final BigInteger half_xor(BigInteger X,BigInteger Y) {
    Y=Y.or(X);
    return X.xor(Y);
  }
 
    /**
     *
     * @param X
     * @param Y
     * @return
     */
    public final BigInteger eq(BigInteger X,BigInteger Y) {
    return bignot(npowers,X.xor(Y));
  }
  
    /**
     *
     * @param IF
     * @param THEN
     * @param ELSE
     * @return
     */
    public final BigInteger ite(BigInteger IF,BigInteger THEN,BigInteger ELSE) {
    BigInteger YES=IF.and(THEN);
    BigInteger NO=bignot(npowers,IF).and(ELSE);
    return YES.or(NO);
  }
  
  private final int nvars;

    /**
     *
     */
    protected final int npowers;
  
  //private final BigInteger NVARS;
  private final BigInteger NVARS2;
  private final BigInteger BigOne;
    
  private final String name;
  
    /**
     *
     * @param name
     * @param nvars
     */
    public Eval(String name,int nvars){
    this.name=name;
    this.nvars=nvars;
    this.npowers=1<<nvars;
    //this.NVARS=BigInteger.valueOf(nvars);
    this.NVARS2=valueOf(nvars+2);
    this.BigOne=bigones(npowers);
  }

    /**
     *
     * @return
     */
    public int getArity() {
    return 2;
  }
  
    /**
     *
     * @return
     */
    public int getNvars() {
    return nvars;
  }
  
  /**
   * applies the primitive operation during synthesis
     * @return 
   */
  public BigInteger applyOp(BigInteger[] Bs) {
    //return Bs[0].add(Bs[Bs.length-1]);
     //BigInteger B=Bs[0].and(Bs[Bs.length-1]);
     //return bignot(npowers,B);
     return zero;
  }

    /**
     *
     * @param B
     * @return
     */
    public BigInteger eval(BigInteger B) {
    int order=B.compareTo(one);
    if(order<0) { // 0 represents constant 0
      return zero;
    }
    else if(order==0) {
      return BigOne; // 1 represents constant 1
    }
    else if(B.compareTo(NVARS2)<0) { // vars in [2..nvars+1]
      return lvar2bigint(nvars,B.intValue()-2);
    }
    else { // complex expressions in [nvars+2..]
      BigInteger[] tuple=bigint2tuple(getArity(),B);
      for(int i=0;i<tuple.length;i++) {
        tuple[i]=eval(tuple[i]);
      }
      return applyOp(tuple);
    }
  }
  
    /**
     *
     * @param B
     * @return
     */
    public Object toExpr(BigInteger B) {
    int order=B.compareTo(one);
    if(order<0) {
      return 0;
    }
    else if(order==0) {
      return 1;
    }
    else if(B.compareTo(NVARS2)<0) {
      return new Var(B.intValue()-2);
    }
    else {
      BigInteger[] tuple=bigint2tuple(getArity(),B);
      Object[] args=new Object[getArity()];
      for(int i=0;i<tuple.length;i++) {
        args[i]=toExpr(tuple[i]);
      }
      return new Fun(name,args);
    }
  }
  
    /**
     *
     * @return
     */
    public String showVars() {
    StringBuilder buf=new StringBuilder();
    buf.append("zero=").append(big2string(zero, npowers)).append(':').append(zero).append('\n');
    for(int i=0;i<nvars;i++) {
      BigInteger V=lvar2bigint(nvars,i);
      buf.append("V").append(i).append("=").append(big2string(V, npowers)).append(':').append(V).append('\n');
    }
    buf.append("one=").append(big2string(BigOne, npowers)).append(':').append(BigOne).append('\n');
    return buf.toString();
  }
}
