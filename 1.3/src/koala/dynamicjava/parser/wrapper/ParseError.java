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

package koala.dynamicjava.parser.wrapper;

/**
 * This error is thrown when an unexpected error append while parsing a statement
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/03
 */

public class ParseError extends Error {
    /**
     * The file name
     * 
     * @serial
     */
    private final String filename;

    /**
     * The line in the source code where the error occured
     * 
     * @serial
     */
    private final int line;

    /**
     * The column in the source code where the error occured
     * 
     * @serial
     */
    private final int column;

    /**
     * Constructs an <code>ExecutionError</code> with no detail message.
     */
    public ParseError() {
        this("");
    }

    /**
     * Constructs an <code>ExecutionError</code> with the specified detail message.
     * 
     * @param s the detail message.
     */
    public ParseError(final String s) {
        this(s, "", -1, -1);
    }

    /**
     * Constructs an <code>ExecutionError</code> with the specified detail message, filename, line
     * and column.
     * 
     * @param s the detail message.
     * @param fn the file name.
     * @param l the line in the source code.
     * @param c the column in the source code.
     */
    public ParseError(final String s, final String fn, final int l, final int c) {
        super(s);
        this.filename = fn;
        this.line = l;
        this.column = c;
    }

    /**
     * Returns the name of the source file
     */
    public String getFilename() {
        return this.filename;
    }

    /**
     * Returns the line in the source code where the error occured
     */
    public int getLine() {
        return this.line;
    }

    /**
     * Returns the column in the source code where the error occured
     */
    public int getColumn() {
        return this.column;
    }
}
