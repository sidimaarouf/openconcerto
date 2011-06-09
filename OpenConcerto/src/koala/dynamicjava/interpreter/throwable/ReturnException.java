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

package koala.dynamicjava.interpreter.throwable;

import koala.dynamicjava.interpreter.error.ExecutionError;
import koala.dynamicjava.tree.Node;

/**
 * This error is thrown by an interpreted throw statement
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/26
 */

public class ReturnException extends ExecutionError {
    /**
     * Whether the return has a value
     * 
     * @serial
     */
    private final boolean withValue;

    /**
     * The returned object
     * 
     * @serial
     */
    private Object value;

    /**
     * Constructs an <code>ReturnException</code> with a value
     * 
     * @serial
     */
    public ReturnException(final String s, final Node n) {
        super(s, n);
        this.withValue = false;
    }

    /**
     * Constructs an <code>ReturnExceptionError</code> with the specified detail message, filename,
     * line, column and exception.
     * 
     * @param e the return exception
     * @param n the node in the syntax tree where the error occurs
     */
    public ReturnException(final String s, final Object o, final Node n) {
        super(s, n);
        this.withValue = true;
        this.value = o;
    }

    /**
     * Returns the value returned
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Whether or not the return statement had a value
     */
    public boolean hasValue() {
        return this.withValue;
    }
}
