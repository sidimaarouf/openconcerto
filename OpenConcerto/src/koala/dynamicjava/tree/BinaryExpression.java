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

/**
 * This class represents the binary expression nodes of the syntax tree
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/04/25
 */

public abstract class BinaryExpression extends Expression {
    /**
     * The leftExpression property name
     */
    public final static String LEFT_EXPRESSION = "leftExpression";

    /**
     * The rightExpression property name
     */
    public final static String RIGHT_EXPRESSION = "rightExpression";

    /**
     * The LHS expression
     */
    private Expression leftExpression;

    /**
     * The RHS expression
     */
    private Expression rightExpression;

    /**
     * Initializes the expression
     * 
     * @param lexp the LHS expression
     * @param rexp the RHS expression
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     * @exception IllegalArgumentException if lexp is null or rexp is null
     */
    protected BinaryExpression(final Expression lexp, final Expression rexp, final String fn, final int bl, final int bc, final int el, final int ec) {
        super(fn, bl, bc, el, ec);

        if (lexp == null) {
            throw new IllegalArgumentException("lexp == null");
        }
        if (rexp == null) {
            throw new IllegalArgumentException("rexp == null");
        }

        this.leftExpression = lexp;
        this.rightExpression = rexp;
    }

    /**
     * Returns the left hand side expression
     */
    public Expression getLeftExpression() {
        return this.leftExpression;
    }

    /**
     * Sets the left hand side expression
     * 
     * @exception IllegalArgumentException if exp is null
     */
    public void setLeftExpression(final Expression exp) {
        if (exp == null) {
            throw new IllegalArgumentException("exp == null");
        }

        firePropertyChange(LEFT_EXPRESSION, this.leftExpression, this.leftExpression = exp);
    }

    /**
     * Returns the right hand side expression
     */
    public Expression getRightExpression() {
        return this.rightExpression;
    }

    /**
     * Sets the right hand side expression
     * 
     * @exception IllegalArgumentException if exp is null
     */
    public void setRightExpression(final Expression exp) {
        if (exp == null) {
            throw new IllegalArgumentException("exp == null");
        }

        firePropertyChange(RIGHT_EXPRESSION, this.rightExpression, this.rightExpression = exp);
    }
}
