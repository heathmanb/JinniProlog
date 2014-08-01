package jgp;
import java.math.BigInteger;

/**
 *
 * @author Brad
 */
public class HammingDist implements DistEval {
      /**
       * computes the Hamming distance between B an C
     * @return 
       */
       public final int distance(BigInteger B,BigInteger C) {
          BigInteger D=B.xor(C);
          return D.bitCount();
       }
}
