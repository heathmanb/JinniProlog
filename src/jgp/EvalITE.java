package jgp;

import java.math.*;
import prolog.core.BigMath;

/**
 *
 * @author Brad
 */
public class EvalITE extends Eval {
 
    /**
     *
     * @param nvars
     */
    public EvalITE(int nvars){
    super("ite",nvars);
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
     return ite(Bs[0],Bs[1],Bs[2]);
  }
}
