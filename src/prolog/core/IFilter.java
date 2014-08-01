package prolog.core;

import prolog.logic.*;

/**
 *
 * @author Brad
 */
public interface IFilter extends Stateful {

    /**
     *
     * @param VData
     * @return
     */
    public Object filterVertex(Object VData);

    /**
     *
     * @param EData
     * @return
     */
    public Object filterEdge(Object EData);
}
