/*
 * DynamicJava - Copyright (C) 1999 Dyade
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: The above copyright notice and this
 * permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL DYADE BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * Except as contained in this notice, the name of Dyade shall not be used in advertising or
 * otherwise to promote the sale, use or other dealings in this Software without prior written
 * authorization from Dyade.
 */

package koala.dynamicjava.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class contains a collection of utility methods for reflection.
 * 
 * @author Stephane Hillion
 * @version 1.3 - 1999/11/28
 */

public class ReflectionUtilities {
    /**
     * Looks for a constructor in the given class or in super classes of this class.
     * 
     * @param cl the class of which the constructor is a member
     * @param ac the arguments classes (possibly not the exact declaring classes)
     */
    public static Constructor lookupConstructor(final Class cl, final Class[] ac) throws NoSuchMethodException {
        final List ms = getConstructors(cl, ac.length);
        final List mm = new LinkedList();

        // Search for the constructors with good parameter types and
        // put them in 'mm'
        Iterator it = ms.iterator();
        while (it.hasNext()) {
            final Constructor m = (Constructor) it.next();
            if (hasCompatibleSignatures(m.getParameterTypes(), ac)) {
                mm.add(m);
            }
        }

        if (mm.isEmpty()) {
            throw new NoSuchMethodException(cl.getName() + " constructor");
        }

        // Select the most specific constructor
        it = mm.iterator();
        Constructor result = (Constructor) it.next();

        while (it.hasNext()) {
            result = selectTheMostSpecificConstructor(result, (Constructor) it.next());
        }

        return result;
    }

    /**
     * Gets all the constructors in the given class or super classes, even the redefined
     * constructors are returned.
     * 
     * @param cl the class where the constructor was declared
     * @param params the number of parameters
     * @return a list that contains the found constructors, an empty list if no matching constructor
     *         was found.
     */
    public static List getConstructors(final Class cl, final int params) {
        final List result = new LinkedList();
        final Constructor[] ms = cl.getDeclaredConstructors();

        for (int i = 0; i < ms.length; i++) {
            if (ms[i].getParameterTypes().length == params) {
                result.add(ms[i]);
            }
        }
        return result;
    }

    /**
     * Looks for a method in the given class or in super classes of this class.
     * 
     * @param cl the class of which the method is a member
     * @param name the name of the method
     * @param ac the arguments classes (possibly not the exact declaring classes)
     */
    public static Method lookupMethod(final Class cl, final String name, final List ac) throws NoSuchMethodException {
        return lookupMethod(cl, name, (Class[]) ac.toArray());
    }

    /**
     * Looks for a method in the given class or in super classes of this class.
     * 
     * @param cl the class of which the method is a member
     * @param name the name of the method
     * @param ac the arguments classes (possibly not the exact declaring classes)
     */
    public static Method lookupMethod(final Class cl, final String name, final Class[] ac) throws NoSuchMethodException {
        final List ms = getMethods(cl, name, ac.length);
        final List mm = new LinkedList();

        // Search for the methods with good parameter types and
        // put them in 'mm'
        Iterator it = ms.iterator();
        while (it.hasNext()) {
            final Method m = (Method) it.next();
            if (hasCompatibleSignatures(m.getParameterTypes(), ac)) {
                mm.add(m);
            }
        }

        if (mm.isEmpty()) {
            throw new NoSuchMethodException(name);
        }

        // Select the most specific method
        it = mm.iterator();
        Method result = (Method) it.next();

        while (it.hasNext()) {
            result = selectTheMostSpecificMethod(result, (Method) it.next());
        }

        return result;
    }

    /**
     * Gets all the methods with the given name in the given class or super classes. Even the
     * redefined methods are returned.
     * 
     * @param cl the class where the method was declared
     * @param name the name of the method
     * @param params the number of parameters
     * @return a list that contains the found methods, an empty list if no matching method was
     *         found.
     */
    public static List getMethods(final Class cl, final String name, final int params) {
        final List result = new LinkedList();

        if (cl.isInterface()) {
            final Method[] ms = cl.getDeclaredMethods();
            for (int i = 0; i < ms.length; i++) {
                if (ms[i].getName().equals(name) && ms[i].getParameterTypes().length == params) {
                    result.add(ms[i]);
                }
            }
            final Class[] cs = cl.getInterfaces();
            for (int i = 0; i < cs.length; i++) {
                result.addAll(getMethods(cs[i], name, params));
            }
            if (cs.length == 0) {
                result.addAll(getMethods(Object.class, name, params));
            }
        } else {
            Class c = cl;
            while (c != null) {
                final Method[] ms = c.getDeclaredMethods();

                for (int i = 0; i < ms.length; i++) {
                    if (ms[i].getName().equals(name) && ms[i].getParameterTypes().length == params) {
                        result.add(ms[i]);
                    }
                }
                c = c.getSuperclass();
            }
        }
        return result;
    }

    /**
     * Looks up for a method in an outer classes of this class.
     * 
     * @param cl the inner class
     * @param name the name of the method
     * @param ac the arguments classes (possibly not the exact declaring classes)
     */
    public static Method lookupOuterMethod(final Class cl, final String name, final Class[] ac) throws NoSuchMethodException {
        boolean sc = Modifier.isStatic(cl.getModifiers());
        Class c = cl != null ? cl.getDeclaringClass() : null;
        while (c != null) {
            sc |= Modifier.isStatic(c.getModifiers());
            try {
                final Method m = lookupMethod(c, name, ac);
                if (!sc || Modifier.isStatic(m.getModifiers())) {
                    return m;
                }
            } catch (final NoSuchMethodException e) {
            }
            c = c.getDeclaringClass();
        }
        throw new NoSuchMethodException(name);
    }

    /**
     * Returns a field with the given name declared in the given class or in the superclasses of the
     * given class
     * 
     * @param cl the class where the field must look for the field
     * @param name the name of the field
     */
    public static Field getField(final Class cl, final String name) throws NoSuchFieldException, AmbiguousFieldException {
        Class c = cl;
        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (final NoSuchFieldException e) {
                final Class[] ints = c.getInterfaces();
                Field f = null;
                for (int i = 0; i < ints.length; i++) {
                    Field tmp = null;
                    try {
                        tmp = getField(ints[i], name);
                    } catch (final NoSuchFieldException ex) {
                    }
                    if (tmp != null) {
                        if (f != null && !f.equals(tmp)) {
                            throw new AmbiguousFieldException(name);
                        }
                        f = tmp;
                    }
                }
                if (f != null) {
                    return f;
                }
            }
            c = c.getSuperclass();
        }
        throw new NoSuchFieldException(name);
    }

    /**
     * Returns a field with the given name declared in one of the outer classes of the given class
     * 
     * @param cl the inner class
     * @param name the name of the field
     */
    public static Field getOuterField(final Class cl, final String name) throws NoSuchFieldException, AmbiguousFieldException {
        boolean sc = Modifier.isStatic(cl.getModifiers());
        Class c = cl != null ? cl.getDeclaringClass() : null;
        while (c != null) {
            sc |= Modifier.isStatic(c.getModifiers());
            try {
                final Field f = getField(c, name);
                if (!sc || Modifier.isStatic(f.getModifiers())) {
                    return f;
                }
            } catch (final NoSuchFieldException e) {
            }
            c = c.getDeclaringClass();
        }
        throw new NoSuchFieldException(name);
    }

    /**
     * Returns the method with the most specific signature. It is assumed that m1 and m2 have the
     * same number of parameters.
     */
    protected static Method selectTheMostSpecificMethod(final Method m1, final Method m2) {
        final Class[] a1 = m1.getParameterTypes();
        final Class[] a2 = m2.getParameterTypes();

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return isCompatible(a1[i], a2[i]) ? m2 : m1;
            }
        }
        return m1;
    }

    /**
     * Returns the constructor with the most specific signature. It is assumed that m1 and m2 have
     * the same number of parameters.
     */
    protected static Constructor selectTheMostSpecificConstructor(final Constructor c1, final Constructor c2) {
        final Class[] a1 = c1.getParameterTypes();
        final Class[] a2 = c2.getParameterTypes();

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                if (isCompatible(a1[i], a2[i])) {
                    return c2;
                } else {
                    return c1;
                }
            }
        }

        return c1;
    }

    /**
     * For each element (class) of the given arrays, tests if the first array element is assignable
     * from the second array element. The two arrays are assumed to have the same length.
     */
    public static boolean hasCompatibleSignatures(final Class[] a1, final Class[] a2) {
        for (int i = 0; i < a1.length; i++) {
            if (!isCompatible(a1[i], a2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Whether 'c1' is assignable from 'c2'
     */
    public static boolean isCompatible(final Class c1, final Class c2) {
        if (c1.isPrimitive()) {
            if (c1 != c2) {
                if (c1 == int.class) {
                    return c2 == byte.class || c2 == short.class || c2 == char.class;
                } else if (c1 == long.class) {
                    return c2 == byte.class || c2 == short.class || c2 == int.class;
                } else if (c1 == short.class) {
                    return c2 == byte.class;
                } else if (c1 == float.class) {
                    return c2 == byte.class || c2 == short.class || c2 == int.class || c2 == long.class;
                } else if (c1 == double.class) {
                    return c2 == byte.class || c2 == short.class || c2 == int.class || c2 == long.class || c2 == float.class;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return c2 == null ? true : c1.isAssignableFrom(c2);
        }
    }

    /**
     * This class contains only static methods, so it is not useful to create instances of it.
     */
    protected ReflectionUtilities() {
    }
}
