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
 * This class represents the instanceof expression nodes of the syntax tree
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/04/24
 */

public class InstanceOfExpression extends Expression implements ExpressionContainer {
    /**
     * The referenceType property name
     */
    public final static String REFERENCE_TYPE = "referenceType";

    /**
     * The expression to check
     */
    private Expression expression;

    /**
     * The type to check
     */
    private Type referenceType;

    /**
     * Initializes the expression
     * 
     * @param exp the expression to test
     * @param t the type to check
     * @exception IllegalArgumentException if exp is null or t is null
     */
    public InstanceOfExpression(final Expression exp, final Type t) {
        this(exp, t, null, 0, 0, 0, 0);
    }

    /**
     * Initializes the expression
     * 
     * @param exp the expression to test
     * @param t the type to check
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     * @exception IllegalArgumentException if exp is null or t is null
     */
    public InstanceOfExpression(final Expression exp, final Type t, final String fn, final int bl, final int bc, final int el, final int ec) {
        super(fn, bl, bc, el, ec);

        if (exp == null) {
            throw new IllegalArgumentException("exp == null");
        }
        if (t == null) {
            throw new IllegalArgumentException("t == null");
        }

        this.expression = exp;
        this.referenceType = t;
    }

    /**
     * Returns the expression to check
     */
    public Expression getExpression() {
        return this.expression;
    }

    /**
     * Sets the expression to check
     * 
     * @exception IllegalArgumentException if e is null
     */
    public void setExpression(final Expression e) {
        if (e == null) {
            throw new IllegalArgumentException("e == null");
        }

        firePropertyChange(EXPRESSION, this.expression, this.expression = e);
    }

    /**
     * Returns the type to check
     */
    public Type getReferenceType() {
        return this.referenceType;
    }

    /**
     * Sets the type to check
     * 
     * @exception IllegalArgumentException if t is null
     */
    public void setReferenceType(final Type t) {
        if (t == null) {
            throw new IllegalArgumentException("t == null");
        }

        firePropertyChange(REFERENCE_TYPE, this.referenceType, this.referenceType = t);
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
