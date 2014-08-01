package prolog.core;

import prolog.logic.*;

/**
 *
 * @author Brad
 */
public interface GraphVisitor extends Stateful {

    /**
     *
     */
    public void init();

    /**
     *
     */
    public void start();

    /**
     *
     */
    public void stop();

    /**
     *
     * @return
     */
    public Object end();

    /**
     *
     * @param V
     * @return
     */
    public boolean isVisited(Object V);

    /**
     *
     * @return
     */
    public boolean visitIn();

    /**
     *
     * @return
     */
    public boolean visitOut();

    /**
     *
     * @param V
     */
    public void visit(Object V);

    /**
     *
     * @param V
     */
    public void unvisit(Object V);
}
