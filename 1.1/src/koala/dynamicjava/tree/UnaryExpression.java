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
 * This class represents the unary expression nodes of the syntax tree
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/04/25
 */

public abstract class UnaryExpression extends Expression implements ExpressionContainer {
    /**
     * The target expression
     */
    private Expression expression;

    /**
     * Initializes the expression
     * 
     * @param exp the target expression
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     * @exception IllegalArgumentException if exp is null
     */
    protected UnaryExpression(final Expression exp, final String fn, final int bl, final int bc, final int el, final int ec) {
        super(fn, bl, bc, el, ec);

        if (exp == null) {
            throw new IllegalArgumentException("exp == null");
        }

        this.expression = exp;
    }

    /**
     * Returns the target expression
     */
    public Expression getExpression() {
        return this.expression;
    }

    /**
     * Sets the target expression
     * 
     * @exception IllegalArgumentException if e is null
     */
    public void setExpression(final Expression e) {
        if (e == null) {
            throw new IllegalArgumentException("e == null");
        }

        firePropertyChange(EXPRESSION, this.expression, this.expression = e);
    }
}
