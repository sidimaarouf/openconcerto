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

package koala.dynamicjava.interpreter.context;

import java.lang.reflect.Method;

import koala.dynamicjava.tree.Expression;

/**
 * This error is thrown by a context when it modify the syntax tree
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/07/04
 */

public class MethodModificationError extends Error {
    /**
     * The modified expression
     * 
     * @serial
     */
    private final Expression expression;

    /**
     * The method
     */
    private final Method method;

    /**
     * Constructs an <code>MethodModificationError</code>
     * 
     * @param e the new expression
     * @param m the method found
     */
    public MethodModificationError(final Expression e, final Method m) {
        this.expression = e;
        this.method = m;
    }

    /**
     * Returns the expression
     */
    public Expression getExpression() {
        return this.expression;
    }

    /**
     * Returns the method
     */
    public Method getMethod() {
        return this.method;
    }
}
