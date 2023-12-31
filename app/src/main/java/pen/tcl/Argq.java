package pen.tcl;

import tcl.lang.TclObject;

import java.util.ArrayList;

/**
 * A JTcl Command argument list, packaged for convenient processing.
 */
public class Argq {
    //-------------------------------------------------------------------------
    // Instance Variables

    private final TclObject[] args;
    private final int prefixTokens;
    private int nextArg;

    //-------------------------------------------------------------------------
    // Constructor

    /**
     * Creates a new argument queue given the argument list of the number
     * of prefix tokens to skip.
     * @param args The complete set of arguments.
     * @param prefixTokens The number of prefix tokens.
     * @throws IllegalArgumentException if args.length < prefixTokens
     */
    public Argq(TclObject[] args, int prefixTokens) {
        if (args.length < prefixTokens) {
            throw new IllegalStateException("prefixTokens exceeds the array length");
        }
        this.args = args;
        this.prefixTokens = prefixTokens;
        this.nextArg = prefixTokens;
    }

    //-------------------------------------------------------------------------
    // Queue API

    /**
     * Gets whether there is at least one more argument to process.
     * @return true or false
     */
    public boolean hasNext() {
        return nextArg < args.length;
    }

    /**
     * Gets the number of arguments not yet processed.
     * @return The number
     */
    public int argsLeft() {
        return args.length - nextArg;
    }

    /**
     * Gets and consumes the next argument.
     * @return The argument.
     * @throws IllegalStateException if !hasNext().
     */
    public TclObject next() {
        if (!hasNext()) {
            throw new IllegalStateException("Argument queue exhausted.");
        }

        return args[nextArg++];
    }

    /**
     * Gets the next argument, leaving the queue unchanged.
     * @return The argument.
     * @throws IllegalStateException if !hasNext().
     */
    public TclObject peek() {
        if (!hasNext()) {
            throw new IllegalStateException("Argument queue exhausted.");
        }

        return args[nextArg];
    }

    /**
     * Resets the queue to point at the first argument.
     */
    public void reset() {
        nextArg = prefixTokens;
    }

    //-------------------------------------------------------------------------
    // Array API

    public int getPrefixTokens() {
        return prefixTokens;
    }

    /**
     * Returns the complete arguments array, including the command prefix
     * tokens.
     * @return the array
     */
    public TclObject[] asCommandArray() {
        return args;
    }

    /**
     * Returns the total number of non-prefix arguments.
     * @return The number
     */
    public int size() {
        return args.length - prefixTokens;
    }

    /**
     * Returns the argument, where index=0 is the first non-prefix argument.
     * @param index The index
     * @return The argument
     */
    public TclObject arg(int index) {
        return args[index + prefixTokens];
    }

    //------------------------------------------------------------------------
    // Object API

    @Override
    public String toString() {
        var prefix = new ArrayList<String>();
        var tokens = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            if (i < prefixTokens) {
                prefix.add(args[i].toString());
            } else {
                tokens.add(args[i].toString());
            }
        }

        var buff = new StringBuilder();
        buff.append(String.join(",", prefix));
        if (!tokens.isEmpty()) {
            buff.append(":")
                .append(String.join(",", tokens));
        }

        return buff.toString();
    }
}
