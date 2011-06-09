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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import koala.dynamicjava.tree.visitor.Visitor;

/**
 * This class represents the nodes of the syntax tree
 * 
 * @author Stephane Hillion
 * @version 1.1 - 1999/11/12
 */

public abstract class Node {
    /**
     * The filename property name
     */
    public final static String FILENAME = "filename";

    /**
     * The beginLine property name
     */
    public final static String BEGIN_LINE = "beginLine";

    /**
     * The endLine property name
     */
    public final static String END_LINE = "endLine";

    /**
     * The beginColumn property name
     */
    public final static String BEGIN_COLUMN = "beginColumn";

    /**
     * The endColumn property name
     */
    public final static String END_COLUMN = "endColumn";

    /**
     * The filename
     */
    private String filename;

    /**
     * The begin line in the source code
     */
    private int beginLine;

    /**
     * The begin column in the begin line
     */
    private int beginColumn;

    /**
     * The end line in the source code
     */
    private int endLine;

    /**
     * The end column in the end line
     */
    private int endColumn;

    /**
     * The support for the property change mechanism
     */
    private final PropertyChangeSupport propertyChangeSupport;

    /**
     * The properties
     */
    private final Map properties;

    /**
     * Initializes the node
     * 
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     */
    protected Node(final String fn, final int bl, final int bc, final int el, final int ec) {
        this.filename = fn;
        this.beginLine = bl;
        this.beginColumn = bc;
        this.endLine = el;
        this.endColumn = ec;
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.properties = new HashMap(11);
    }

    /**
     * Returns the filename. Can be null.
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * Sets the filename
     */
    public void setFilename(final String s) {
        firePropertyChange(FILENAME, this.filename, this.filename = s);
    }

    /**
     * Returns the begin line of this node in the source code
     */
    public int getBeginLine() {
        return this.beginLine;
    }

    /**
     * Sets the begin line
     */
    public void setBeginLine(final int i) {
        firePropertyChange(BEGIN_LINE, this.beginLine, this.beginLine = i);
    }

    /**
     * Returns the begin column of this node in the begin line
     */
    public int getBeginColumn() {
        return this.beginColumn;
    }

    /**
     * Sets the begin column
     */
    public void setBeginColumn(final int i) {
        firePropertyChange(BEGIN_COLUMN, this.beginColumn, this.beginColumn = i);
    }

    /**
     * Returns the end line of this node in the source code
     */
    public int getEndLine() {
        return this.endLine;
    }

    /**
     * Sets the end line
     */
    public void setEndLine(final int i) {
        firePropertyChange(END_LINE, this.endLine, this.endLine = i);
    }

    /**
     * Returns the end column of this node in the end line
     */
    public int getEndColumn() {
        return this.endColumn;
    }

    /**
     * Sets the end column
     */
    public void setEndColumn(final int i) {
        firePropertyChange(END_COLUMN, this.endColumn, this.endColumn = i);
    }

    // Properties support //////////////////////////////////////////////////

    /**
     * Sets the value of a property
     * 
     * @param name the property name
     * @param value the new value to set
     */
    public void setProperty(final String name, final Object value) {
        firePropertyChange(name, this.properties.put(name, value), value);
    }

    /**
     * Returns the value of a property
     * 
     * @param name the property name
     * @return null if the property was not previously set
     */
    public Object getProperty(final String name) {
        return this.properties.get(name);
    }

    /**
     * Returns the defined properties for this node.
     * 
     * @return a set of string
     */
    public Set getProperties() {
        return this.properties.keySet();
    }

    /**
     * Returns true if a property is defined for this node
     * 
     * @param name the name of the property
     */
    public boolean hasProperty(final String name) {
        return this.properties.containsKey(name);
    }

    /**
     * Adds a PropertyChangeListener to the listener list. The listener is registered for all
     * properties.
     * 
     * @param listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     * 
     * @param listener The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Adds a PropertyChangeListener for a specific property. The listener will be invoked only when
     * a call on firePropertyChange names that specific property.
     * 
     * @param propertyName The name of the property to listen on.
     * @param listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a PropertyChangeListener for a specific property.
     * 
     * @param propertyName The name of the property that was listened on.
     * @param listener The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Report a bound property update to any registered listeners. No event is fired if old and new
     * are equal and non-null.
     * 
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    protected void firePropertyChange(final String propertyName, final boolean oldValue, final boolean newValue) {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Report a bound property update to any registered listeners. No event is fired if old and new
     * are equal and non-null.
     * 
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    protected void firePropertyChange(final String propertyName, final int oldValue, final int newValue) {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Report a bound property update to any registered listeners. No event is fired if old and new
     * are equal and non-null.
     * 
     * @param propertyName The programmatic name of the property that was changed.
     * @param oldValue The old value of the property.
     * @param newValue The new value of the property.
     */
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        this.propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    // Visitors support ///////////////////////////////////////////////////////////

    /**
     * Allows a visitor to traverse the tree
     * 
     * @param visitor the visitor to accept
     */
    public abstract Object acceptVisitor(Visitor visitor);
}
