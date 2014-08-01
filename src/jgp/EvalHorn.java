package jgp;

import java.math.*;
import prolog.core.BigMath;

/**
 *
 * @author Brad
 */
public class EvalHorn extends Eval {
 
    /**
     *
     * @param nvars
     */
    public EvalHorn(int nvars){
    super("ifand",nvars);
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
     BigInteger A=Bs[0];
     BigInteger B=Bs[1];
     BigInteger H=Bs[2];
     BigInteger C=A.and(B);
     return impl(C,H);
  }
}
