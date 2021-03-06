/*
 * Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.max.vm.jdk;

import static com.sun.max.vm.intrinsics.MaxineIntrinsicIDs.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import sun.reflect.annotation.*;

import com.sun.max.annotate.*;
import com.sun.max.vm.actor.member.*;

/**
 * Method substitutions for {@link Method}.
 *
 */
@METHOD_SUBSTITUTIONS(Method.class)
public final class JDK_java_lang_reflect_Method {

    private JDK_java_lang_reflect_Method() {
    }

    /**
     * Casts this to {@link Method}.
     */
    @INTRINSIC(UNSAFE_CAST)
    private native Method thisMethod();

    /**
     * Gets the method actor associated with this method.
     * @return the method actor for this method
     */
    @INLINE
    private MethodActor thisMethodActor() {
        return MethodActor.fromJava(thisMethod());
    }

    @ALIAS(declaringClass = Method.class)
    private Map<Class, Annotation> declaredAnnotations;

    /**
     * Gets the declared annotations for this method.
     * @return a map from the declaring class to its annotations
     */
    @SuppressWarnings({ "cast", "unchecked"})
    @SUBSTITUTE
    private synchronized Map<Class, Annotation> declaredAnnotations() {
        final MethodActor methodActor = thisMethodActor();
        if (declaredAnnotations == null) {
            byte[] annotations = methodActor.runtimeVisibleAnnotationsBytes();
            ConstantPoolAdapter cp = new ConstantPoolAdapter(methodActor.holder().constantPool());
            Class declaringClass = methodActor.holder().toJava();
            // JDK 7 uses the method signature Map<Class<? extends Annotation>, Annotation>. In order to be compatible with both JDK 6 and JDK 7, use the casts below
            declaredAnnotations = (Map<Class, Annotation>) ((Object) AnnotationParser.parseAnnotations(
                annotations, cp, declaringClass));
        }
        return declaredAnnotations;
    }
}
