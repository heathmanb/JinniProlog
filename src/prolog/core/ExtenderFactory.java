package prolog.core;

import prolog.kernel.*;
import java.lang.reflect.*;
import prolog.logic.*;

/**
 * Adds reflection based Built-ins
 */
public class ExtenderFactory implements ITerm, Stateful {

    public ExtenderFactory(Machine M) {
        this.M = M;
    }

    private Machine M;

    private boolean removeObject(Object O) {
        return M.removeObject(O);
    }

    @Override
    public final int putVar(Object o) throws TypeException {
        //throw new TypeException("attempt to pass Prolog variable to Java");
        return M.termReader.putVar(o);
    }

    @Override
    public final int putConst(String c) throws PrologException {
        return M.termReader.putConst(c);
    }

    @Override
    public final int putString(String s) throws PrologException {
        return M.termReader.putString(s);
    }

    @Override
    public final int putInt(int i) {
        return M.termReader.putInt(i);
    }

    @Override
    public final int putFloat(double d) {
        return M.termReader.putFloat(d);
    }

    @Override
    public final int putFun(String f, int[] args) throws PrologException {
        return M.termReader.putFun(f, args);
    }

    @Override
    public final int putObject(Object o) throws PrologException {
        return M.termReader.putObject(o);
    }

    @Override
    public Object getTerm(int xref, OTerm O) throws PrologException {
        return getTerm(xref);
    }

    final public Object getTerm(int xref) throws PrologException {
        return M.termReader.getTerm(xref);
    }

    /* Reflection related */
    public final int new_java_class(int xref) {
        int res = 0;
        String className = null;
        Class C = null;
        try {
            className = (String) getTerm(xref);
            if (null == className) {
                return res;
            }
            C = Class.forName(className);
        } catch (ClassNotFoundException | PrologException e) {
            if (null != className) switch (className) {
                case "boolean":
                    C = Boolean.TYPE;
                    break;
                case "char":
                    C = Character.TYPE;
                    break;
                case "byte":
                    C = Byte.TYPE;
                    break;
                case "short":
                    C = Short.TYPE;
                    break;
                case "int":
                    C = Integer.TYPE;
                    break;
                case "long":
                    C = Long.TYPE;
                    break;
                case "float":
                    C = Float.TYPE;
                    break;
                case "double":
                    C = Double.TYPE;
                    break;
                case "void":
                    C = Void.TYPE;
                    break;
                default:
                    JavaIO.errmes("cannot create class:" + className, e);
                    return res;
            }
        }
        try {
            res = putObject(C);
        } catch (PrologException e) {
            JavaIO.errmes("cannot internalize class object:" + className, e);
        }
        return res;
    }

    static Class[] getTypes(Object[] Os) {
        if (null == Os) {
            return null;
        }
        Class[] Cs = new Class[Os.length];
        for (int i = 0; i < Os.length; i++) {
            Object O = Os[i];
            Class C;
            if (O.toString().equals(Prolog.S_null)) {
                Os[i] = null;
                C = null;
            } else if (O instanceof Integer) {
                C = Integer.TYPE;
            } else if (O instanceof Double) {
                C = Double.TYPE;
            } else {
                C = O.getClass();
            }
            Cs[i] = C;
        }
        return Cs;
    }

    int putResult(Object O) throws PrologException {
        return putObject(O);
    }

    final Object[] getArgs(int xargs) throws PrologException {
        Object obj = getTerm(xargs);
        Object[] args;
        if (obj.toString().equals("void")) {
            args = null;
        } else {
            args = ((Fun) obj).args;
        }
        return args;
    }

    public final int new_java_object(int xref, int xargs) {
        Class cls = null;
        try {
            cls = (Class) getTerm(xref);
            if (null == cls) {
                return 0;
            }
            Object[] args = getArgs(xargs);
            Class[] types = getTypes(args);
            //JavaIO.println("constructor args="+args+" types="+types);
            Object O = instantiate(cls, args, types);
            if (null == O) {
                return 0;
            }
            return putObject(O);
        } catch (Exception e) {
            JavaIO.errmes("cannot create object in: " + cls, e);
            return 0;
        }
    }

    public final int invoke_java_method(int classref, int xref, int xmeth, int xargs) {
        Object obj = null;
        Class cls = null;
        String methodName = null;
        Object[] args;
        Class[] parameterTypes;
        try {
            obj = getTerm(xref);
            if (null == obj) {
                return 0;
            }
            if (classref > 0) {
                Object maybe = getTerm(classref);
                if (null == maybe) {
                    return 0;
                }
                if (maybe instanceof Class) {
                    cls = (Class) maybe;
                }
            }
            if (null == cls) {
                cls = (obj instanceof Class) ? (Class) obj : obj.getClass();
            }

            methodName = (String) getTerm(xmeth);
            if (null == methodName) {
                return 0;
            }
            args = getArgs(xargs);
            parameterTypes = getTypes(args);
            //JavaIO.println(methodName+": invoke args="+args+" types="+parameterTypes+" obj="+obj);
            Object res = invokeTheMethod(cls, obj, methodName, args, parameterTypes);
            return putResult(res);
        } catch (Exception e) {
            JavaIO.errmes("exception invoking method: " + methodName + "() on: " + obj, e);
            return 0;
        }
    }

    public final int get_java_field_handle(int xref, int xfield) {
        Object obj = null;
        String fieldName = null;
        try {
            obj = getTerm(xref);
            if (null == obj) {
                return 0;
            }
            fieldName = (String) getTerm(xfield);
            if (null == fieldName) {
                return 0;
            }
            //JavaIO.println(fieldName+"in obj="+obj);
            Class C;
            if (obj instanceof Class) {
                C = (Class) obj;
            } else {
                C = obj.getClass();
            }
            Field F = C.getField(fieldName);
            Object res = F;
            return putResult(res);
        } catch (NoSuchFieldException | SecurityException | PrologException e) {
            JavaIO.errmes("cannot find field: " + fieldName + "() of: " + obj, e);
            return 0;
        }
    }

    public final boolean delete_java_class(int xref) {
        /* should scan all obs of the class and delete them? */
        try {
            Class cls = (Class) getTerm(xref);
            return null != cls && removeObject(cls);
        } catch (PrologException e) {
            JavaIO.errmes("cannot delete class", e);
            return false;
        }
    }

    public final boolean delete_java_object(int xref) {
        try {
            Object O = getTerm(xref);
            return null != O && removeObject(O);
        } catch (PrologException e) {
            JavaIO.errmes("cannot delete object", e);
            return false;
        }
    }

    /**
     * Instantiate an Object of Given Class
     *
     * @param currentClass is the Class whose object is instantiated
     * @param args is the array of Objects passed as arguments to the
     * constructor
     * @param parameterTypes is the array of Classes which are the types for
     * args and used to find the constructor we are looking for
     * @return The Object of the currentClass created with the void constructor
     * @throws java.lang.Exception
     */
    public Object instantiate(
            Class currentClass,
            Object[] args,
            Class[] parameterTypes ) throws Exception {
        Object res = null;
        if (args == null) {
            res = currentClass.newInstance();
        } else {
            try {
                Constructor theConstructor = currentClass.getConstructor(parameterTypes);
                res = theConstructor.newInstance(args);
            } catch (NoSuchMethodException e) {
                boolean found = false;
                Constructor[] theConstructors = currentClass.getConstructors();
                for (Constructor theConstructor1 : theConstructors) {
                    if (theConstructor1.getParameterTypes().length == parameterTypes.length) {
                        JavaIO.traceln("length: " + theConstructor1.getParameterTypes().length);
                        Constructor theConstructor = theConstructor1;
                        try {
                            res = theConstructor.newInstance(args);
                            found = true;
                            break;
                        } catch (IllegalArgumentException iae) {
                            //Prolog.dump("Trying to instantiate ...."+theConstructor,iae);
                        }
                    }
                }
                if (!found) {
                    throw e;
                }
            }
        }
        return res;
    }

    /**
     * Invokes a Given Method of Given Object with given arguments
     *
     * @param theClass
     * @param theCurrent is the object (or class in case of static method) whose
     * method is invoked
     * @param methodName is the name of method to be invoked
     * @param args is the array of Objects passed as arguments to the method
     * @param parameterTypes is the array of Classes which are the types for
     * args and used to find the method we are looking for
     * @return The Object returned by given method
     * @throws java.lang.Exception
     */
    public Object invokeTheMethod(Class theClass, Object theCurrent, String methodName,
            Object[] args, Class[] parameterTypes) throws Exception {
        try {
            return tryMethod(theClass, theCurrent, methodName, args, parameterTypes);
        } catch (Exception e) {
            try {
                if (theCurrent instanceof Class) {
                    return tryMethod(theClass.getClass(), theClass, methodName, args, parameterTypes);
                }
            } catch (Exception ce) {
            }
            JavaIO.warnmes("method invocation error: CLASS=>" + theClass
                    + ", OBJECT=>" + theCurrent + ", method: " + methodName);
            throw e;
        }
    }

    private Object tryMethod(Class currentClass, Object currentObject, String methodName,
            Object[] args, Class[] parameterTypes) throws Exception {

    //JavaIO.println("entering invoke: CLASS=>"+currentClass+", OBJECT=>"+currentObject+", method: "+methodName);
        Object res = null;
        Method theMethod;

        if (args == null) {
            theMethod = currentClass.getMethod(methodName, (Class[]) null);
            res = theMethod.invoke(currentObject, (Object[]) null);
        } else {
            try {
                theMethod = currentClass.getMethod(methodName, parameterTypes);
                res = theMethod.invoke(currentObject, args);
            } catch (NoSuchMethodException e) {
                boolean found = false;
                Method[] theMethods = currentClass.getMethods();
                for (Method theMethod1 : theMethods) {
                    theMethod = theMethod1;
                    if (methodName.equals(theMethod.getName())
                            && theMethod.getParameterTypes().length == parameterTypes.length) {
                        try {
                            //Prolog.dump("Trying to invoke ...."+theMethod+"/"+parameterTypes.length);
                            res = theMethod.invoke(currentObject, args);
                            found = true;
                            break;
                        } catch (IllegalArgumentException iae) {
                            // trying more
                            //Prolog.dump("Bad arguments trying to invoke ...."+theMethod+"=>"+iae);
                        } catch (InvocationTargetException te) {
                            JavaIO.errmes("error in trying Java method: ", te.getTargetException());
                            return res;
                        }
                    }
                }
                if (!found) {
                    throw e;
                }
            } catch (InvocationTargetException te) {
                JavaIO.errmes("error in calling Java method: ", te.getTargetException());
                return res;
            }
        }
        return res;
    }
}
