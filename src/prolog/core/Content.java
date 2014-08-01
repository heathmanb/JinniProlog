package prolog.core;

import prolog.logic.*;

/**
 *
 * @author Brad
 */
public final class Content implements Stateful {

    Object key;
    Object value;
    final Object data;

    /**
     *
     * @param key
     * @param value
     * @param data
     */
    public Content(Object key, Object value, Object data) {
        this.key = key;
        this.value = value;
        this.data = data;
    }

    @Override
    public String toString() {
        return "(" + key + "," + value + "," + data + ")";
    }
}
