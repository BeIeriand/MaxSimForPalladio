/*
 * Copyright (c) 2012, 2012, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.truffle.api.dsl.test;

import static com.oracle.truffle.api.dsl.test.TestHelper.*;
import static org.junit.Assert.*;

import org.junit.*;

import com.oracle.truffle.api.dsl.*;
import com.oracle.truffle.api.dsl.test.NodeFieldTestFactory.*;
import com.oracle.truffle.api.dsl.test.NodeFieldTestFactory.TestContainerFactory.TestContainerContainerFieldFactory;
import com.oracle.truffle.api.dsl.test.TypeSystemTest.ValueNode;

public class NodeFieldTest {

    @Test
    public void testIntField() {
        assertEquals(42, createCallTarget(IntFieldTestNodeFactory.create(42)).call());
    }

    @NodeField(name = "field", type = int.class)
    abstract static class IntFieldTestNode extends ValueNode {

        public abstract int getField();

        @Specialization
        int intField() {
            return getField();
        }

    }

    @Test
    public void testIntFieldNoGetter() {
        assertEquals(42, createCallTarget(IntFieldNoGetterTestNodeFactory.create(42)).call());
    }

    @NodeField(name = "field", type = int.class)
    abstract static class IntFieldNoGetterTestNode extends ValueNode {

        @Specialization
        int intField(int field) {
            return field;
        }

    }

    @Test
    public void testMultipleFields() {
        assertEquals(42, createCallTarget(MultipleFieldsTestNodeFactory.create(21, 21)).call());
    }

    @NodeFields({@NodeField(name = "field0", type = int.class), @NodeField(name = "field1", type = int.class)})
    abstract static class MultipleFieldsTestNode extends ValueNode {

        public abstract int getField0();

        public abstract int getField1();

        @Specialization
        int intField() {
            return getField0() + getField1();
        }

    }

    @Test
    public void testStringField() {
        assertEquals("42", createCallTarget(StringFieldTestNodeFactory.create("42")).call());
    }

    @NodeField(name = "field", type = String.class)
    abstract static class StringFieldTestNode extends ValueNode {

        public abstract String getField();

        @Specialization
        String stringField() {
            return getField();
        }

    }

    @Test
    public void testRewrite() {
        assertEquals("42", createCallTarget(RewriteTestNodeFactory.create("42")).call());
    }

    @NodeField(name = "field", type = String.class)
    abstract static class RewriteTestNode extends ValueNode {

        public abstract String getField();

        @Specialization(order = 1, rewriteOn = RuntimeException.class)
        String alwaysRewrite() {
            throw new RuntimeException();
        }

        @Specialization(order = 2)
        String returnField() {
            return getField();
        }
    }

    @Test
    public void testStringContainer() {
        assertEquals(42, createCallTarget(TestContainerContainerFieldFactory.create(42, "42")).call());
    }

    @NodeField(name = "field", type = int.class)
    abstract static class IntContainerNode extends ValueNode {

        public abstract int getField();

    }

    @NodeContainer(IntContainerNode.class)
    @NodeField(name = "anotherField", type = String.class)
    abstract static class TestContainer {

        @Specialization
        static int containerField(int field, String anotherField) {
            return anotherField.equals("42") ? field : -1;
        }

    }

}
