/*
 * Copyright (c) 2010 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to technology embodied in the product
 * that is described in this document. In particular, and without limitation, these intellectual property
 * rights may include one or more of the U.S. patents listed at http://www.sun.com/patents and one or
 * more additional patents or pending patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software. Government users are subject to the Sun
 * Microsystems, Inc. standard license agreement and applicable provisions of the FAR and its
 * supplements.
 *
 * Use is subject to license terms. Sun, Sun Microsystems, the Sun logo, Java and Solaris are trademarks or
 * registered trademarks of Sun Microsystems, Inc. in the U.S. and other countries. All SPARC trademarks
 * are used under license and are trademarks or registered trademarks of SPARC International, Inc. in the
 * U.S. and other countries.
 *
 * UNIX is a registered trademark in the U.S. and other countries, exclusively licensed through X/Open
 * Company, Ltd.
 */
package com.sun.c1x.ci;

/**
 * Represents a value that is yet to be bound to a {@linkplain CiLocation machine location}.
 * 
 * @author Doug Simon
 */
public class CiVariable extends CiLocation {
    
    /**
     * The identifier of the variable. This is a non-zero index in a contiguous 0-based name space. 
     */
    public final int index;

    /**
     * Creates a new variable.
     * @param kind
     * @param index
     */
    private CiVariable(CiKind kind, int index) {
        super(kind);
        this.index = index;
    }
    
    private static CiVariable[] generate(CiKind kind, int count) {
        CiVariable[] variables = new CiVariable[count];
        for (int i = 0; i < count; i++) {
            variables[i] = new CiVariable(kind, i);
        }
        return variables;
    }
    
    /**
     * Cache of common variables.
     */
    private static final CiVariable[][] cache = new CiVariable[CiKind.values().length][];
    static {
        for (CiKind kind : CiKind.values()) {
            if (kind.sizeInSlots() > 0) {
                cache[kind.ordinal()] = generate(kind, 10);
            }
        }
    }
    
    public static CiVariable forIndex(int index, CiKind kind) {
        return forIndex(kind, index);
    }
    
    /**
     * Gets a variable for a given kind and index.
     * 
     * @param kind
     * @param index
     * @return
     */
    public static CiVariable forIndex(CiKind kind, int index) {
        assert index >= 0;
        assert kind.jvmSlots > 0;
        CiVariable[] cachedVars = cache[kind.ordinal()];
        if (index < cachedVars.length) {
            return cachedVars[index];
        }
        return new CiVariable(kind, index);
    }
    
    @Override
    public int variableNumber() {
        return index;
    }
    
    @Override
    public String toString() {
        return "v" + index + ":" + kind;
    }
}
