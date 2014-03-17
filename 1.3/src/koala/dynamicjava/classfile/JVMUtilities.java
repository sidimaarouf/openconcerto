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

package koala.dynamicjava.classfile;

import java.util.HashMap;
import java.util.Map;

/**
 * This interface contains utility functions.
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/06
 */

public abstract class JVMUtilities {
    /**
     * The table of the base type characters. Keys are classes
     */
    private final static Map types = new HashMap(11, 1.0f);

    /**
     * The table of the base type characters. Keys are strings
     */
    private final static Map stypes = new HashMap(11, 1.0f);

    static {
        types.put(byte.class, "B");
        types.put(char.class, "C");
        types.put(double.class, "D");
        types.put(float.class, "F");
        types.put(int.class, "I");
        types.put(long.class, "J");
        types.put(short.class, "S");
        types.put(boolean.class, "Z");
        types.put(void.class, "V");

        stypes.put("byte", "B");
        stypes.put("char", "C");
        stypes.put("double", "D");
        stypes.put("float", "F");
        stypes.put("int", "I");
        stypes.put("long", "J");
        stypes.put("short", "S");
        stypes.put("boolean", "Z");
        stypes.put("void", "V");
    }

    /**
     * Returns the string that represents internally the given class
     */
    public static String getName(final Class c) {
        final String s = (String) types.get(c);
        if (s != null) {
            return s;
        } else {
            return c.getName().replace('.', '/');
        }
    }

    /**
     * Returns the string that represents internally the given class name
     */
    public static String getName(final String c) {
        final String s = (String) stypes.get(c);
        if (s != null) {
            return s;
        } else {
            if (c.endsWith("[]")) {
                if (c.endsWith("[][]")) {
                    return "[" + getName(c.substring(0, c.length() - 2));
                } else {
                    return "[" + getReturnTypeName(c.substring(0, c.length() - 2));
                }
            } else {
                return c.replace('.', '/');
            }
        }
    }

    /**
     * Returns the string that represents internally the given class
     */
    public static String getReturnTypeName(final Class c) {
        final String s = (String) types.get(c);
        if (s != null) {
            return s;
        } else {
            return (c.isArray() ? c.getName() : "L" + c.getName() + ";").replace('.', '/');
        }
    }

    /**
     * Returns the string that represents internally the given class name
     */
    public static String getReturnTypeName(final String c) {
        final String s = (String) stypes.get(c);
        if (s != null) {
            return s;
        } else {
            if (c.endsWith("[]")) {
                return "[" + getReturnTypeName(c.substring(0, c.length() - 2));
            } else {
                return (c.startsWith("[") ? c : "L" + c + ";").replace('.', '/');
            }
        }
    }

    /**
     * Returns the string that represents internally the given class
     */
    public static String getParameterTypeName(final Class c) {
        return getReturnTypeName(c);
    }

    /**
     * Returns the string that represents internally the given class name
     */
    public static String getParameterTypeName(final String c) {
        return getReturnTypeName(c);
    }

    /**
     * Creates a method descriptor
     * 
     * @param rt the return type name as returned by getReturnTypeName
     * @param pt the parameters type names as returned by getParameterTypeName
     */
    public static String createMethodDescriptor(final String rt, final String[] pt) {
        if (pt != null) {
            String result = "(";
            for (int i = 0; i < pt.length; i++) {
                result += pt[i];
            }
            return result + ")" + rt;
        } else {
            return rt;
        }
    }

    /**
     * No need to create instances of this class
     */
    private JVMUtilities() {
    }
}
