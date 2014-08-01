package jgp;

import java.math.*;
import prolog.core.BigMath;

/**
 *
 * @author Brad
 */
public class EvalHalfXOR extends Eval {
 
    /**
     *
     * @param nvars
     */
    public EvalHalfXOR(int nvars){
    super("<",nvars);
  }
  
  /**
   * applies the primitive operation during synthesis
     * @return 
   */
  public BigInteger applyOp(BigInteger[] Bs) {    
     return half_xor(Bs[0],Bs[1]);
  }
}
