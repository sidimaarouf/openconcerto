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

package koala.dynamicjava.gui.resource;

/**
 * Signals a format error in a resource bundle
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/04/18
 */

public class ResourceFormatException extends RuntimeException {
    /**
     * The class name of the resource bundle requested
     * 
     * @serial
     */
    private final String className;

    /**
     * The name of the specific resource requested by the user
     * 
     * @serial
     */
    private final String key;

    /**
     * Constructs a ResourceFormatException with the specified information. A detail message is a
     * String that describes this particular exception.
     * 
     * @param s the detail message
     * @param classname the name of the resource class
     * @param key the key for the malformed resource.
     */
    public ResourceFormatException(final String s, final String className, final String key) {
        super(s);
        this.className = className;
        this.key = key;
    }

    /**
     * Gets parameter passed by constructor.
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Gets parameter passed by constructor.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Returns a printable representation of this object
     */
    @Override
    public String toString() {
        return super.toString() + " (" + getKey() + ", bundle: " + getClassName() + ")";
    }
}
