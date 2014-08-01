package jgp;

import java.math.*;
import prolog.core.BigMath;

/**
 *
 * @author Brad
 */
public class EvalNAND extends Eval {
 
    /**
     *
     * @param nvars
     */
    public EvalNAND(int nvars){
    super("nand",nvars);
  }
  
  /**
   * applies the primitive operation during synthesis
     * @return 
   */
  public BigInteger applyOp(BigInteger[] Bs) {
     return nand(Bs[0],Bs[1]);
  }
}
