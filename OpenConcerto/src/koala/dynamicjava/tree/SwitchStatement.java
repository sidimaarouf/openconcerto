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

import java.util.List;

import koala.dynamicjava.tree.visitor.Visitor;

/**
 * This class represents the switch statement nodes of the syntax tree
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/25
 */

public class SwitchStatement extends Statement {
    /**
     * The selector property name
     */
    public final static String SELECTOR = "selector";

    /**
     * The bindings property name
     */
    public final static String BINDINGS = "bindings";

    /**
     * The selector
     */
    private Expression selector;

    /**
     * The list of case bindings
     */
    private List bindings;

    /**
     * Creates a new switch statement
     * 
     * @param sel the selector
     * @param cases the case bindings (SwitchBlocks)
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     * @exception IllegalArgumentException if sel is null or cases is null
     */
    public SwitchStatement(final Expression sel, final List cases, final String fn, final int bl, final int bc, final int el, final int ec) {
        super(fn, bl, bc, el, ec);

        if (sel == null) {
            throw new IllegalArgumentException("sel == null");
        }
        if (cases == null) {
            throw new IllegalArgumentException("cases == null");
        }

        this.selector = sel;
        this.bindings = cases;
    }

    /**
     * Gets the selector
     */
    public Expression getSelector() {
        return this.selector;
    }

    /**
     * Sets the selector
     * 
     * @exception IllegalArgumentException if e is null
     */
    public void setSelector(final Expression e) {
        if (e == null) {
            throw new IllegalArgumentException("e == null");
        }

        firePropertyChange(SELECTOR, this.selector, this.selector = e);
    }

    /**
     * Returns the 'case' bindings
     */
    public List getBindings() {
        return this.bindings;
    }

    /**
     * Sets the bindings
     * 
     * @exception IllegalArgumentException if e is null
     */
    public void setBindings(final List l) {
        if (l == null) {
            throw new IllegalArgumentException("l == null");
        }

        firePropertyChange(BINDINGS, this.bindings, this.bindings = l);
    }

    /**
     * Allows a visitor to traverse the tree
     * 
     * @param visitor the visitor to accept
     */
    @Override
    public Object acceptVisitor(final Visitor visitor) {
        return visitor.visit(this);
    }
}
