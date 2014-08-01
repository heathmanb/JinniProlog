package jgp;

import java.math.*;
import prolog.core.BigMath;

/**
 *
 * @author Brad
 */
public class EvalAndXOR extends Eval {
 
    /**
     *
     * @param nvars
     */
    public EvalAndXOR(int nvars){
    super("andxor",nvars);
  }
  
    /**
     *
     * @return
     */
    public int getArity() {
    return 3;
  }
  
  /**
   * applies the primitive operation during synthesis
     * @return 
   */
  public BigInteger applyOp(BigInteger[] Bs) { 
     BigInteger Op=Bs[0];
     BigInteger A=Bs[1];
     BigInteger B=Bs[2];
     BigInteger Op0=eq(Op,zero);
     BigInteger Op1=Op.xor(zero);
     Op0=Op0.and(A.and(B));
     Op1=Op1.and(A.xor(B));
     return Op0.or(Op1);
  }
}
