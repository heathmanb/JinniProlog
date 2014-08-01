package prolog.core;

import prolog.logic.*;

/**
 *
 * @author Brad
 */
public final class IVertex implements Stateful {

    /**
     *
     */
    public Object key;

    /**
     *
     */
    public Object data;

    /**
     *
     */
    public final IEdge[] inLinks;

    /**
     *
     */
    public final IEdge[] outLinks;

    /**
     *
     * @param key
     * @param data
     * @param inDegree
     * @param outDegree
     */
    public IVertex(Object key, Object data, int inDegree, int outDegree) {
        this.key = key;
        this.data = data;
        this.inLinks = new IEdge[inDegree];
        this.outLinks = new IEdge[outDegree];
    }

    @Override
    public String toString() {
        return key + "->" + data;
    }
}
