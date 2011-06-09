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

package koala.dynamicjava.classfile;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents a component of the bytecode 'ClassFile' format that contains attributes
 * 
 * @author Stephane Hillion
 * @version 1.0 - 1999/05/28
 */

public abstract class AttributeOwnerComponent extends BytecodeComponent {
    /**
     * The access flags
     */
    protected short accessFlags;

    /**
     * The attributes
     */
    protected List attributes;

    /**
     * Initializes a new bytecode component
     * 
     * @param cp the constant pool
     * @param af the access flags
     * @param ni the name index in the constant pool
     */
    protected AttributeOwnerComponent() {
        super(null, (short) 0);
        this.attributes = new LinkedList();
    }

    /**
     * Sets the access flags for this class
     */
    public void setAccessFlags(final int flags) {
        this.accessFlags |= (short) flags;
    }

    /**
     * Sets the synthetic attribute to this field
     */
    public void setSyntheticAttribute() {
        this.attributes.add(new SimpleAttribute(this.constantPool, "Synthetic"));
    }
}
