package prolog.core;

import prolog.logic.*;

/**
 *
 * @author Brad
 */
public final class IEdge implements Stateful {

    /**
     *
     */
    public final int to;

    /**
     *
     */
    public Object data;

    /**
     *
     * @param to
     * @param data
     */
    public IEdge(int to, Object data) {
        this.to = to;
        this.data = data;
    }

    @Override
    public String toString() {
        return (null == data) ? null : data.toString();
    }
}
