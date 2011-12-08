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
 * This class represents an interface declaration
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/27
 */

public class InterfaceDeclaration extends TypeDeclaration {
    /**
     * Creates a new interface declaration
     * 
     * @param flags the access flags
     * @param name the name of the interface to declare
     * @param impl the list of implemented interfaces. Can be null.
     * @param body the list of fields declarations
     */
    public InterfaceDeclaration(final int flags, final String name, final List impl, final List body) {
        this(flags, name, impl, body, null, 0, 0, 0, 0);
    }

    /**
     * Creates a new interface declaration
     * 
     * @param flags the access flags
     * @param name the name of the interface to declare
     * @param impl the list of implemented interfaces. Can be null.
     * @param body the list of fields declarations
     * @param fn the filename
     * @param bl the begin line
     * @param bc the begin column
     * @param el the end line
     * @param ec the end column
     */
    public InterfaceDeclaration(final int flags, final String name, final List impl, final List body, final String fn, final int bl, final int bc, final int el, final int ec) {
        super(flags, name, impl, body, fn, bl, bc, el, ec);
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
