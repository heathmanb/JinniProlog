package build;
import prolog.core.Javafier;

/**
 *
 * @author Brad
 */
public class JMain extends Javafier {

    /**
     *
     * @param args
     */
    public static void main (String args[]) {
   if(args.length>0) {
       javafy(args[0]);
        } else {
       run();
        }
 }
}