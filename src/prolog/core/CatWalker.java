package prolog.core;

import prolog.logic.*;

/**
 *
 * @author Brad
 */
public interface CatWalker extends Stateful {

    /**
     *
     */
    public void atStart();

    /**
     *
     */
    public void beforeProps();

    /**
     *
     * @param vertex
     * @param key
     * @param value
     */
    public void onProp(Object vertex, Object key, Object value);

    /**
     *
     */
    public void afterProps();

    /**
     *
     */
    public void beforeMorphisms();

    /**
     *
     * @param from
     * @param to
     * @param m
     * @param md
     */
    public void onMorphism(Object from, Object to, Object m, Object md);

    /**
     *
     */
    public void afterMorphisms();

    /**
     *
     * @return
     */
    public Object atEnd();
}
