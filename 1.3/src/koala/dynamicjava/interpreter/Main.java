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

package koala.dynamicjava.interpreter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import koala.dynamicjava.parser.wrapper.JavaCCParserFactory;

/**
 * This file contains the entry point of the interpreter
 * 
 * @author Stephane Hillion
 * @version 1.1 - 1999/11/14
 */

public class Main {
    /**
     * The name of the application
     */
    private static String appname;

    /**
     * The main function
     */
    public static void main(final String[] args) {
        appname = args[0];

        String fname = "standard input";
        String lpath = "";
        String cpath = "";
        InputStream stream = System.in;
        String init = null;
        String statement = null;
        final Interpreter interpreter = new TreeInterpreter(new JavaCCParserFactory());
        interpreter.addLibrarySuffix(".java");

        // Read the options
        int arg0 = 1;
        for (;;) {
            if (args.length > arg0) {
                if (args[arg0].equals("-lp")) {
                    arg0++;
                    if (args.length <= arg0) {
                        usage();
                        return;
                    }
                    lpath = args[arg0++];
                } else if (args[arg0].equals("-cp")) {
                    arg0++;
                    if (args.length <= arg0) {
                        usage();
                        return;
                    }
                    cpath = args[arg0++];
                } else if (args[arg0].equals("-i")) {
                    arg0++;
                    if (args.length <= arg0) {
                        usage();
                        return;
                    }
                    init = args[arg0++];
                } else if (args[arg0].equals("-h")) {
                    usage();
                    return;
                } else if (args[arg0].equals("-c")) {
                    arg0++;
                    if (args.length <= arg0) {
                        usage();
                        return;
                    }

                    statement = args[arg0++] + ".main(new String[] {";
                    if (arg0 < args.length) {
                        statement += '"' + args[arg0++] + '"';
                    }
                    for (; arg0 < args.length; arg0++) {
                        statement += ", " + '"' + args[arg0] + '"';
                    }
                    statement += " });";
                    break;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        if (args.length > arg0 + 1) {
            usage();
            return;
        }

        setLibraryPath(interpreter, lpath);
        setClassPath(interpreter, cpath);

        // Load the initialization file
        if (init != null) {
            try {
                final InputStream is = new FileInputStream(init);

                try {
                    interpreter.interpret(is, init);
                } catch (final InterpreterException e) {
                    System.err.println(e.getMessage());
                } catch (final Throwable e) {
                    System.err.println(e);
                }

            } catch (final FileNotFoundException e) {
                System.out.println("File " + init + " not found.");
                return;
            }
        }

        // Load the input file
        if (args.length > arg0) {
            fname = args[arg0];
            try {
                stream = new FileInputStream(fname);
            } catch (final FileNotFoundException e) {
                System.out.println("File " + fname + " not found.");
                return;
            }
        }

        try {
            if (statement != null) {
                interpreter.interpret(new StringReader(statement), "main method call");
            } else {
                if (stream == System.in) {
                    System.out.println("Reading from standard input");

                    for (;;) {
                        String s = "";
                        final Reader r = new InputStreamReader(stream);
                        int brc1 = 0;
                        int brc2 = 0;
                        while (brc1 < 2 && brc2 < 2) {
                            final int ch = r.read();
                            if (ch == 10) {
                                brc1++;
                            } else if (ch == 13) {
                                brc2++;
                            } else {
                                brc1 = 0;
                                brc2 = 0;
                            }
                            s += (char) ch;
                        }
                        final Object result = interpreter.interpret(new StringReader(s), fname);
                        System.out.println("=> " + result);
                    }
                } else {
                    interpreter.interpret(stream, fname);
                }
            }
        } catch (final InterpreterException e) {
            System.err.println(e.getMessage());
        } catch (final Throwable e) {
            e.printStackTrace();
            System.err.println(e);
        }

        if (stream == System.in) {
            System.out.println("Interactive loop stopped");
        }
    }

    /**
     * Sets the library path for the given interpreter
     */
    private static void setLibraryPath(final Interpreter interp, String lpath) {
        int n;
        while ((n = lpath.indexOf(':')) != -1 || (n = lpath.indexOf(';')) != -1) {
            interp.addLibraryPath(lpath.substring(0, n));
            lpath = lpath.substring(n + 1, lpath.length());
        }
        interp.addLibraryPath(lpath);
    }

    /**
     * Sets the class path for the given interpreter
     */
    private static void setClassPath(final Interpreter interp, String cpath) {
        int n;
        while ((n = cpath.indexOf(':')) != -1 || (n = cpath.indexOf(';')) != -1) {
            interp.addClassPath(cpath.substring(0, n));
            cpath = cpath.substring(n + 1, cpath.length());
        }
        interp.addClassPath(cpath);
    }

    /**
     * Prints the usage
     */
    private static void usage() {
        System.out.println("Usage:");
        System.out.println("       " + appname + " [options] [file]");
        System.out.println("     or");
        System.out.println("       " + appname + " [options] -c classname [args ...]");
        System.out.println("Options:");
        System.out.println("       -cp path   " + "the class path ((semi)colon separated paths)");
        System.out.println("       -lp path   " + "the library path ((semi)colon separated paths)");
        System.out.println("       -i file    " + "the initialization file");
    }
}
