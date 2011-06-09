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

package koala.dynamicjava.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a type declaration
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/10
 */

public abstract class TypeDeclaration extends Node {
    /**
     * The accessFlags property name
     */
    public final static String ACCESS_FLAGS = "accessFlags";

    /**
     * The name property name
     */
    public final static String NAME = "name";

    /**
     * The interfaces property name
     */
    public final static String INTERFACES = "interfaces";

    /**
     * The members property name
     */
    public final static String MEMBERS = "members";

    /**
     * The access flags
     */
    private int accessFlags;

    /**
     * The name of this class
     */
    private String name;

    /**
     * The implemented interfaces
     */
    private List interfaces;

    /**
     * The members
     */
    private List members;

    /**
     * Creates a new class declaration
     * 
     * @param flags the access flags
     * @param name the name of the class to declare
     * @param impl the list of implemented interfaces (List of Token). Can be null.
     * @param body the list of fields declarations
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     * @exception IllegalArgumentException if name is null or body is null
     */
    protected TypeDeclaration(final int flags, final String name, final List impl, final List body, final String fn, final int bl, final int bc, final int el, final int ec) {
        super(fn, bl, bc, el, ec);

        if (name == null) {
            throw new IllegalArgumentException("name == null");
        }
        if (body == null) {
            throw new IllegalArgumentException("body == null");
        }

        this.accessFlags = flags;
        this.name = name;
        if (impl != null) {
            this.interfaces = new LinkedList();
            final Iterator it = impl.iterator();
            while (it.hasNext()) {
                this.interfaces.add(TreeUtilities.listToName((List) it.next()));
            }
        }
        this.members = body;
    }

    /**
     * Returns the access flags for this class
     */
    public int getAccessFlags() {
        return this.accessFlags;
    }

    /**
     * Sets the access flags for this constructor
     */
    public void setAccessFlags(final int f) {
        firePropertyChange(ACCESS_FLAGS, this.accessFlags, this.accessFlags = f);
    }

    /**
     * Returns the name of this class
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the type's name
     * 
     * @exception IllegalArgumentException if s is null
     */
    public void setName(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("s == null");
        }

        firePropertyChange(NAME, this.name, this.name = s);
    }

    /**
     * Returns a list that contains the names (String) of the implemented interfaces. Can be null.
     */
    public List getInterfaces() {
        return this.interfaces;
    }

    /**
     * Sets the interfaces (a list of strings)
     */
    public void setInterfaces(final List l) {
        firePropertyChange(INTERFACES, this.interfaces, this.interfaces = l);
    }

    /**
     * Returns the list of the declared members
     */
    public List getMembers() {
        return this.members;
    }

    /**
     * Sets the members
     * 
     * @exception IllegalArgumentException if l is null
     */
    public void setMembers(final List l) {
        if (l == null) {
            throw new IllegalArgumentException("l == null");
        }

        firePropertyChange(MEMBERS, this.members, this.members = l);
    }
}
