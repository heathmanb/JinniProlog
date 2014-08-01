package prolog.core;

import java.util.BitSet;
import prolog.logic.*;

/**
 * Implements efficient Role management on a given ObjectDict, assumed
 * immutable. Roles are implemented as BitSets to which the dictionary based
 * integer ordinal of a key is used to turn on/off a corresponding key.
 */
public class RoleMap implements Stateful {

    private static BitSet emptySet = new BitSet();

    final private ObjectDict map;
    final private BitSet roles;

    /**
     * Creates a RoleMap based on an external role dictionary which contain
     * meaningful names for the rules represented as bits in a BitSet.
     */
    RoleMap(ObjectDict map) {
        this.map = map;
        this.roles = new BitSet();
    }

    /**
     * Creates a RoleMap with a new empty Role dictionary.
     */
    RoleMap() {
        this(new ObjectDict());
    }

    /**
     * Creates a RoleMap based on given dictionary and roles
     */
    RoleMap(ObjectDict map, BitSet roles) {
        this.map = map;
        this.roles = roles;
    }

    /**
     * Creates a RoleMap based on another RoleMap's role dictionary.
     */
    RoleMap(RoleMap other) {
        this(other.map);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new RoleMap(map, (BitSet) (roles.clone()));
    }

    /**
     *
     * @return
     */
    public BitSet getRoles() {
        return roles;
    }

    /**
     * Adds a new role and associated value.
     * @param key
     * @param val
     */
    public void add(Object key, Object val) {
        map.put(key, val);
    }

    /**
     * gets the attribute associated to a role
     * @param key
     * @return 
     */
    public Object getValue(Object key) {
        return map.get(key);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValue(Object key, Object value) {
        Entry entry = map.getEntry(key);
        entry.value = value;
    }

    /**
     *
     * @param bit
     */
    public void set(int bit) {
        roles.set(bit);
    }

    /**
     *
     * @param bit
     * @return
     */
    public boolean get(int bit) {
        return roles.get(bit);
    }

    /**
     *
     * @param bit
     */
    public void clear(int bit) {
        roles.clear(bit);
    }

    /**
     *
     */
    public void clear() {
        roles.and(emptySet);
    }

    /**
     *
     * @param key
     */
    public void set(Object key) {
        roles.set(map.getOrdinal(key));
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean get(Object key) {
        return roles.get(map.getOrdinal(key));
    }

    /**
     *
     * @param key
     */
    public void clear(Object key) {
        roles.clear(map.getOrdinal(key));
    }

    /**
     *
     * @param other
     */
    public void and(RoleMap other) {
        roles.and(other.getRoles());
    }

    /**
     *
     * @param other
     */
    public void or(RoleMap other) {
        roles.or(other.getRoles());
    }

    /**
     *
     * @param other
     */
    public void xor(RoleMap other) {
        roles.xor(other.getRoles());
    }

    /**
     *
     * @param other
     */
    public void andNot(RoleMap other) {
        //return roles.clone().andNot(other.getRoles()); // only in 1.2
        BitSet oroles = (BitSet) other.getRoles().clone();
        oroles.xor(emptySet);
        roles.and(oroles);
    }

    /**
     *
     * @return
     */
    public ObjectDict toDict() {
        ObjectIterator I = map.getKeys();
        ObjectDict D = new ObjectDict();
        while (I.hasNext()) {
            Object k = I.next();
            if (get(k)) {
                D.put(k, getValue(k));
            }
        }
        return D;
    }

    @Override
    public String toString() {
        return toDict().toString();
    }
}
