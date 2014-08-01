package prolog.kernel;

import prolog.logic.Stateful;

/**
 *
 * @author Brad
 */
public interface TextSink extends Stateful {

  //public void open();

    /**
     *
     * @param s
     */
        public void append_text(String s);

  //public void close();
}
