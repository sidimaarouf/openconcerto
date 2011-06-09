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

import koala.dynamicjava.tree.visitor.Visitor;

/**
 * This class represents the if-then-else statement nodes of the syntax tree
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/16
 */

public class IfThenElseStatement extends IfThenStatement {
    /**
     * The elseStatement property name
     */
    public final static String ELSE_STATEMENT = "elseStatement";

    /**
     * The 'else' statement
     */
    private Node elseStatement;

    /**
     * Creates a new while statement
     * 
     * @param cond the condition
     * @param tstmt the then statement
     * @param estmt the else statement
     * @exception IllegalArgumentException if cond is null or tstmt is null or estmt is null
     */
    public IfThenElseStatement(final Expression cond, final Node tstmt, final Node estmt) {
        this(cond, tstmt, estmt, null, 0, 0, 0, 0);
    }

    /**
     * Creates a new while statement
     * 
     * @param cond the condition
     * @param tstmt the then statement
     * @param estmt the else statement
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     * @exception IllegalArgumentException if cond is null or tstmt is null or estmt is null
     */
    public IfThenElseStatement(final Expression cond, final Node tstmt, final Node estmt, final String fn, final int bl, final int bc, final int el, final int ec) {
        super(cond, tstmt, fn, bl, bc, el, ec);

        if (estmt == null) {
            throw new IllegalArgumentException("estmt == null");
        }

        this.elseStatement = estmt;
    }

    /**
     * Returns the else statement of this statement
     */
    public Node getElseStatement() {
        return this.elseStatement;
    }

    /**
     * Sets the else statement of this statement
     * 
     * @exception IllegalArgumentException if node is null
     */
    public void setElseStatement(final Node node) {
        if (node == null) {
            throw new IllegalArgumentException("node == null");
        }

        firePropertyChange(ELSE_STATEMENT, this.elseStatement, this.elseStatement = node);
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
