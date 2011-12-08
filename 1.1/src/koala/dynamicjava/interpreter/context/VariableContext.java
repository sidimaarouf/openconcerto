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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class encapsulates the behaviour of Java scopes.
 * 
 * @author Stephane Hillion
 * @version 2.1 - 2000/01/05
 */

public class VariableContext implements SimpleContext {
    /**
     * The scopes
     */
    protected Link scopes;

    /**
     * The current scope
     */
    protected Scope scope;

    /**
     * The current scope for variables
     */
    protected Scope cscope;

    /**
     * Creates a new context initialized with an empty initial scope
     */
    public VariableContext() {
        enterScope();
    }

    /**
     * Creates a new context initialized with the given entries defined in the initial scope.
     * 
     * @param entries a set of string
     */
    public VariableContext(final Set entries) {
        this();
        final Iterator it = entries.iterator();
        while (it.hasNext()) {
            ((AbstractVariable) it.next()).set(this, null);
        }
    }

    /**
     * Enters a scope
     */
    public void enterScope() {
        this.scopes = LinkFactory.createLink(this.scopes);
        this.scope = this.scopes.scope;
        this.cscope = this.scopes.cscope;
    }

    /**
     * Enters a scope and defines the given entries to null.
     * 
     * @param entries a set of string
     */
    public void enterScope(final Set entries) {
        enterScope();
        final Iterator it = entries.iterator();
        while (it.hasNext()) {
            ((AbstractVariable) it.next()).set(this, null);
        }
    }

    /**
     * Defines the given variables
     */
    public void defineVariables(final Set vars) {
        final Iterator it = vars.iterator();
        while (it.hasNext()) {
            final AbstractVariable v = (AbstractVariable) it.next();

            if (v.get(this) == Scope.NO_SUCH_KEY) {
                v.set(this, null);
            }
        }
    }

    /**
     * Leaves the current scope
     * 
     * @return the set of variable defined in this scope
     */
    public Set leaveScope() {
        final Set result = new HashSet(11);
        Iterator it = this.scope.keySet().iterator();
        while (it.hasNext()) {
            result.add(new Variable((String) it.next()));
        }
        it = this.cscope.keySet().iterator();
        while (it.hasNext()) {
            result.add(new Constant((String) it.next()));
        }
        this.scopes = this.scopes.next;
        this.scope = this.scopes.scope;
        this.cscope = this.scopes.cscope;
        return result;
    }

    /**
     * Returns the current scope variables in a set
     */
    public Set getCurrentScopeVariables() {
        final Set result = new HashSet(11);
        Iterator it = this.scope.keySet().iterator();
        while (it.hasNext()) {
            result.add(new Variable((String) it.next()));
        }
        it = this.cscope.keySet().iterator();
        while (it.hasNext()) {
            result.add(new Constant((String) it.next()));
        }
        return result;
    }

    /**
     * Returns the current scope variables (strings) in a set
     */
    public Set getCurrentScopeVariableNames() {
        final Set result = new HashSet(11);
        Iterator it = this.scope.keySet().iterator();
        while (it.hasNext()) {
            result.add(it.next());
        }
        it = this.cscope.keySet().iterator();
        while (it.hasNext()) {
            result.add(it.next());
        }
        return result;
    }

    /**
     * Tests whether an entry is defined in this context
     * 
     * @param name the name of the entry
     */
    public boolean isDefinedVariable(final String name) {
        for (Link l = this.scopes; l != null; l = l.next) {
            if (l.scope.get(name) != Scope.NO_SUCH_KEY) {
                return true;
            } else if (l.cscope.get(name) != Scope.NO_SUCH_KEY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests whether a variable is final in this context
     * 
     * @param name the name of the entry
     * @return false if the variable is not final
     * @exception IllegalStateException if the variable is not defined
     */
    public boolean isFinal(final String name) {
        for (Link l = this.scopes; l != null; l = l.next) {
            if (l.cscope.get(name) != Scope.NO_SUCH_KEY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Defines a new variable in the current scope
     * 
     * @param name the name of the new entry
     * @param value the value of the entry
     * @exception IllegalStateException if the variable is already defined
     */
    public void define(final String name, final Object value) {
        if (this.scope.put(name, value) != Scope.NO_SUCH_KEY) {
            throw new IllegalStateException(name);
        }
    }

    /**
     * Defines a new constant variable in the current scope
     * 
     * @param name the name of the new entry
     * @param value the value of the entry
     * @exception IllegalStateException if the variable is already defined
     */
    public void defineConstant(final String name, final Object value) {
        if (this.cscope.put(name, value) != Scope.NO_SUCH_KEY || this.scope.get(name) != Scope.NO_SUCH_KEY) {
            throw new IllegalStateException(name);
        }
    }

    /**
     * Returns the value of a variable with the given name
     * 
     * @param name the name of the value to get
     * @exception IllegalStateException if the variable is not defined
     */
    public Object get(final String name) {
        for (Link l = this.scopes; l != null; l = l.next) {
            Object result = l.scope.get(name);
            if (result != Scope.NO_SUCH_KEY || (result = l.cscope.get(name)) != Scope.NO_SUCH_KEY) {
                return result;
            }
        }
        throw new IllegalStateException(name);
    }

    /**
     * Sets the value of a defined variable
     * 
     * @param name the name of the entry
     * @param value the value of the entry
     * @exception IllegalStateException if the variable is not defined or is final
     */
    public void set(final String name, final Object value) {
        for (Link l = this.scopes; l != null; l = l.next) {
            final Object val = l.scope.get(name);
            if (val != Scope.NO_SUCH_KEY) {
                l.scope.put(name, value);
                return;
            }
        }
        throw new IllegalStateException(name);
    }

    /**
     * Sets the value of a constant variable in the current scope
     * 
     * @param name the name of the entry
     * @param value the value of the entry
     */
    public void setConstant(final String name, final Object value) {
        this.cscope.put(name, value);
    }

    /**
     * Sets the value of a variable in the current scope
     * 
     * @param name the name of the entry
     * @param value the value of the entry
     */
    public void setVariable(final String name, final Object value) {
        this.scope.put(name, value);
    }

    /**
     * Creates a map that contains the constants in this context
     */
    public Map getConstants() {
        final Map result = new HashMap(11);
        for (Link l = this.scopes; l != null; l = l.next) {
            final Iterator it = l.cscope.keySet().iterator();
            while (it.hasNext()) {
                final String s = (String) it.next();
                result.put(s, l.cscope.get(s));
            }
        }
        return result;
    }

    /**
     * To store the variables
     */
    protected static abstract class AbstractVariable {
        /**
         * The constant name
         */
        public String name;

        /**
         * Sets the variable in the current scope
         */
        public abstract void set(VariableContext ctx, Object value);

        /**
         * Sets the variable in the current scope
         */
        public abstract Object get(VariableContext ctx);

        /**
         * Returns the hashCode
         */
        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }

    /**
     * To store the variables
     */
    protected static class Variable extends AbstractVariable {
        /**
         * Creates a new variable
         */
        public Variable(final String s) {
            this.name = s;
        }

        /**
         * Sets the variable in the current scope
         */
        @Override
        public void set(final VariableContext ctx, final Object value) {
            ctx.scope.put(this.name, value);
        }

        /**
         * Sets the variable in the current scope
         */
        @Override
        public Object get(final VariableContext ctx) {
            return ctx.scope.get(this.name);
        }
    }

    /**
     * To store the constants
     */
    protected class Constant extends AbstractVariable {
        /**
         * Creates a new variable
         */
        public Constant(final String s) {
            this.name = s;
        }

        /**
         * Sets the variable in the current scope
         */
        @Override
        public void set(final VariableContext ctx, final Object value) {
            ctx.cscope.put(this.name, value);
        }

        /**
         * Sets the variable in the current scope
         */
        @Override
        public Object get(final VariableContext ctx) {
            return ctx.cscope.get(this.name);
        }
    }

    /**
     * To store one scope
     */
    protected static class Link {
        /**
         * The current scope
         */
        public Scope scope = new Scope();

        /**
         * The current scope for constants
         */
        public Scope cscope = new Scope();

        /**
         * The next scope
         */
        public Link next;

        /**
         * Creates a new link
         */
        public Link(final Link next) {
            this.next = next;
        }
    }

    /**
     * To manage the creation of scopes and links
     */
    protected static class LinkFactory {
        /**
         * The table size
         */
        protected final static int SIZE = 10;

        /**
         * The table used to recycle the links
         */
        protected final static WeakReference[] links = new WeakReference[SIZE];

        /**
         * Creates a new link
         */
        public static Link createLink(final Link next) {
            /**
             * for (int i = 0; i < SIZE; i++) { WeakReference r = links[i]; Link l = null; if (r !=
             * null) { l = (Link)r.get(); if (l != null) { links[i] = null; l.next = next; return l;
             * } else { links[i] = null; } } }
             */
            return new Link(next);
        }

        /**
         * Notifies the factory to recycle the given link
         */
        public static void recycle(final Link l) {
            /*
             * l.scope.clear(); for (int i = 0; i < SIZE; i++) { if (links[i] == null) { links[i] =
             * new WeakReference(l); return; } }
             */
        }
    }

    /**
     * A table which maps a string with an object
     */
    protected static class Scope {
        /**
         * The load factor
         */
        protected final static float LOAD_FACTOR = 0.75f;

        /**
         * The initial capacity
         */
        protected final static int INITIAL_CAPACITY = 11;

        /**
         * The object used to notify that a key do not exists
         */
        protected final static Object NO_SUCH_KEY = new Object();

        /**
         * The underlying array
         */
        protected Entry[] table;

        /**
         * The number of entries
         */
        protected int count;

        /**
         * The resizing threshold
         */
        protected int threshold;

        /**
         * Creates a new scope
         */
        public Scope() {
            this.table = new Entry[INITIAL_CAPACITY];
            this.threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
        }

        /**
         * Gets the value of a variable
         * 
         * @return the value or NO_SUCH_KEY
         */
        public Object get(final String key) {
            final int hash = key.hashCode() & 0x7FFFFFFF;
            final int index = hash % this.table.length;

            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash && e.key.equals(key)) {
                    return e.value;
                }
            }
            return NO_SUCH_KEY;
        }

        /**
         * Sets a new value for the given variable
         * 
         * @return the old value or NO_SUCH_KEY
         */
        public Object put(final String key, final Object value) {
            final int hash = key.hashCode() & 0x7FFFFFFF;
            int index = hash % this.table.length;

            for (Entry e = this.table[index]; e != null; e = e.next) {
                if (e.hash == hash && e.key.equals(key)) {
                    final Object old = e.value;
                    e.value = value;
                    return old;
                }
            }

            // The key is not in the hash table
            if (this.count++ >= this.threshold) {
                rehash();
                index = hash % this.table.length;
            }

            final Entry e = EntryFactory.createEntry(hash, key, value, this.table[index]);
            this.table[index] = e;
            return NO_SUCH_KEY;
        }

        /**
         * Returns a set that contains the keys
         */
        public Set keySet() {
            final Set result = new HashSet(11);
            for (int i = this.table.length - 1; i >= 0; i--) {
                for (Entry e = this.table[i]; e != null; e = e.next) {
                    result.add(e.key);
                }
            }
            return result;
        }

        /**
         * Clears this scope
         */
        public void clear() {
            this.count = 0;
            for (int i = this.table.length - 1; i >= 0; i--) {
                this.table[i] = null;
            }
        }

        /**
         * Rehash the table
         */
        protected void rehash() {
            final Entry[] oldTable = this.table;

            this.table = new Entry[oldTable.length * 2 + 1];
            this.threshold = (int) (this.table.length * LOAD_FACTOR);

            for (int i = oldTable.length - 1; i >= 0; i--) {
                for (Entry old = oldTable[i]; old != null;) {
                    final Entry e = old;
                    old = old.next;

                    final int index = e.hash % this.table.length;
                    e.next = this.table[index];
                    this.table[index] = e;
                }
            }
        }

        /**
         * To manage collisions
         */
        protected static class Entry {
            /**
             * The hash code
             */
            public int hash;

            /**
             * The variable
             */
            public String key;

            /**
             * The value
             */
            public Object value;

            /**
             * The next entry
             */
            public Entry next;

            /**
             * Creates a new entry
             */
            public Entry(final int hash, final String key, final Object value, final Entry next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        /**
         * To create an entry
         */
        protected static class EntryFactory {
            /**
             * Creates a new entry
             */
            public static Entry createEntry(final int hash, final String key, final Object value, final Entry next) {
                return new Entry(hash, key, value, next);
            }
        }
    }
}
