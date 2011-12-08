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

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * This class offers convenience methods to decode resource bundle entries
 * 
 * @author Stephane Hillion
 * @version 1.1 - 1999/10/28
 */

public class ResourceManager {
    /** The managed resource bundle */
    protected ResourceBundle bundle;

    /**
     * Creates a new resource manager
     * 
     * @param rb a resource bundle
     */
    public ResourceManager(final ResourceBundle rb) {
        this.bundle = rb;
    }

    /**
     * Returns the string that is mapped with the given key
     * 
     * @param key a key in the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     */
    public String getString(final String key) throws MissingResourceException {
        return this.bundle.getString(key);
    }

    /**
     * Returns the tokens that compose the string mapped with the given key. Delimiters
     * (" \t\n\r\f") are not returned.
     * 
     * @param key a key of the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     */
    public List getStringList(final String key) throws MissingResourceException {
        return getStringList(key, " \t\n\r\f", false);
    }

    /**
     * Returns the tokens that compose the string mapped with the given key. Delimiters are not
     * returned.
     * 
     * @param key a key of the resource bundle
     * @param delim the delimiters of the tokens
     * @throws MissingResourceException if key is not the name of a resource
     */
    public List getStringList(final String key, final String delim) throws MissingResourceException {
        return getStringList(key, delim, false);
    }

    /**
     * Returns the tokens that compose the string mapped with the given key
     * 
     * @param key a key of the resource bundle
     * @param delim the delimiters of the tokens
     * @param returnDelims if true, the delimiters are returned in the list
     * @throws MissingResourceException if key is not the name of a resource
     */
    public List getStringList(final String key, final String delim, final boolean returnDelims) throws MissingResourceException {
        final List result = new ArrayList();
        final StringTokenizer st = new StringTokenizer(getString(key), delim, returnDelims);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }

    /**
     * Returns the boolean mapped with the given key
     * 
     * @param key a key of the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     * @throws ResourceFormatException if the resource is malformed
     */
    public boolean getBoolean(final String key) throws MissingResourceException, ResourceFormatException {
        final String b = getString(key);

        if (b.equals("true")) {
            return true;
        } else if (b.equals("false")) {
            return false;
        } else {
            throw new ResourceFormatException("Malformed boolean", this.bundle.getClass().getName(), key);
        }
    }

    /**
     * Returns the integer mapped with the given string
     * 
     * @param key a key of the resource bundle
     * @throws MissingResourceException if key is not the name of a resource
     * @throws ResourceFormatException if the resource is malformed
     */
    public int getInteger(final String key) throws MissingResourceException, ResourceFormatException {
        final String i = getString(key);

        try {
            return Integer.parseInt(i);
        } catch (final NumberFormatException e) {
            throw new ResourceFormatException("Malformed integer", this.bundle.getClass().getName(), key);
        }
    }
}
