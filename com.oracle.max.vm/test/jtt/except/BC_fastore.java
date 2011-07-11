/*
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/*
 * @Harness: java
 * @Runs: (-2, 0.01f) = !java.lang.NullPointerException; (-1, -1.4f) = !java.lang.ArrayIndexOutOfBoundsException;
 * @Runs: (0, 0.01f) = 0.01f;
 * @Runs: (4, 0.01f) = !java.lang.ArrayIndexOutOfBoundsException
 */
package jtt.except;

public class BC_fastore {

    static float[] arr = {0, 0, 0, 0};

    public static float test(int arg, float val) {
        final float[] array = arg == -2 ? null : arr;
        array[arg] = val;
        return array[arg];
    }
}
