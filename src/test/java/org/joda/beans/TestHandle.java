/*
 *  Copyright 2001-2017 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.beans;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Currency;

import org.joda.beans.gen.Direct;
import org.joda.beans.gen.Handle;
import org.joda.beans.gen.ImmPerson;
import org.joda.beans.gen.Light;
import org.joda.beans.ser.JodaBeanSer;
import org.testng.annotations.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Test style=handle.
 */
@Test
public class TestHandle {

    public void test_immutable() {
        ImmPerson person = ImmPerson.builder().forename("John").surname("Doggett").build();
        Handle bean = (Handle) Handle.meta().builder()
                .setString("number", "12")
                .setString("street", "Park Lane")
                .setString("city", "Smallville")
                .set("owner", person)
                .set("list", new ArrayList<String>())
                .set("currency", Currency.getInstance("USD"))
                .build();
        
        assertEquals(bean.getNumber(), 12);
        assertEquals(bean.getTown(), Optional.absent());
        assertEquals(bean.getCity(), "Smallville");
        assertEquals(bean.getStreetName(), "Park Lane");
        assertEquals(bean.getOwner(), person);
        assertEquals(bean.getList(), ImmutableList.of());
        
        assertEquals(bean.metaBean().beanType(), Handle.class);
        assertEquals(bean.metaBean().metaPropertyCount(), 8);
        assertEquals(bean.metaBean().metaPropertyExists("number"), true);
        assertEquals(bean.metaBean().metaPropertyExists("town"), true);
        assertEquals(bean.metaBean().metaPropertyExists("foobar"), false);
        
        MetaProperty<Object> mp = bean.metaBean().metaProperty("number");
        assertEquals(mp.propertyType(), int.class);
        assertEquals(mp.declaringType(), Handle.class);
        assertEquals(mp.get(bean), 12);
        assertEquals(mp.style(), PropertyStyle.IMMUTABLE);
        
        MetaProperty<Object> mp2 = bean.metaBean().metaProperty("town");
        assertEquals(mp2.propertyType(), Optional.class);
        assertEquals(mp2.declaringType(), Handle.class);
        assertEquals(mp2.get(bean), Optional.absent());
        assertEquals(mp2.style(), PropertyStyle.IMMUTABLE);
        
        assertTrue(JodaBeanSer.PRETTY.xmlWriter().write(bean).contains("<currency>USD<"));
        assertFalse(JodaBeanSer.PRETTY.xmlWriter().write(bean).contains("<town>"));
    }

    public static void main(String[] args) {
        ImmPerson person = ImmPerson.builder().forename("John").surname("Doggett").build();
        for (int j = 0; j < 2; j++) {
            System.out.println("Light");
            for (int i = 0; i < 8; i++) {
                light(person);
            }
            System.out.println("Handle");
            for (int i = 0; i < 8; i++) {
                handle(person);
            }
            System.out.println("Direct");
            for (int i = 0; i < 8; i++) {
                direct(person);
            }
        }
    }

    private static void handle(ImmPerson person) {
        long start = System.nanoTime();
        int total = 0;
        for (int i = 0; i < 10000000; i++) {
            Handle bean = (Handle) Handle.meta().builder()
                    .setString("number", "12")
                    .setString("street", "Park Lane")
                    .setString("city", "Smallville")
                    .set("owner", person)
                    .set("list", new ArrayList<String>())
                    .set("currency", Currency.getInstance("USD"))
                    .build();
            total += bean.getCurrency().get().getCurrencyCode().length();
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000) + "ms " + total);
    }

    private static void light(ImmPerson person) {
        long start = System.nanoTime();
        int total = 0;
        for (int i = 0; i < 10000000; i++) {
            Light bean = (Light) Light.meta().builder()
                    .setString("number", "12")
                    .setString("street", "Park Lane")
                    .setString("city", "Smallville")
                    .set("owner", person)
                    .set("list", new ArrayList<String>())
                    .set("currency", Currency.getInstance("USD"))
                    .build();
            total += bean.getCurrency().get().getCurrencyCode().length();
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000) + "ms " + total);
    }

    private static void direct(ImmPerson person) {
        long start = System.nanoTime();
        int total = 0;
        for (int i = 0; i < 10000000; i++) {
            Direct bean = (Direct) Direct.meta().builder()
                    .setString("number", "12")
                    .setString("street", "Park Lane")
                    .setString("city", "Smallville")
                    .set("owner", person)
                    .set("list", new ArrayList<String>())
                    .set("currency", Currency.getInstance("USD"))
                    .build();
            total += bean.getCurrency().get().getCurrencyCode().length();
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000) + "ms " + total);
    }

    private static void handle2(ImmPerson person) {
        Handle bean = (Handle) Handle.meta().builder()
                .setString("number", "12")
                .setString("street", "Park Lane")
                .setString("city", "Smallville")
                .set("owner", person)
                .set("list", new ArrayList<String>())
                .set("currency", Currency.getInstance("USD"))
                .build();
        MetaProperty<Object> mp = bean.metaBean().metaProperty("number");
        MetaProperty<Object> mp2 = bean.metaBean().metaProperty("street");
        long start = System.nanoTime();
        int total = 0;
        for (int i = 0; i < 1000000; i++) {
            total += ((Integer) mp.get(bean)) + mp.get(bean).toString().length() + mp2.get(bean).toString().length();
            if (mp2.get(bean).toString().equals("Park Lane")) total++;
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000) + "ms " + total);
    }

    private static void light2(ImmPerson person) {
        Light bean = (Light) Light.meta().builder()
                .setString("number", "12")
                .setString("street", "Park Lane")
                .setString("city", "Smallville")
                .set("owner", person)
                .set("list", new ArrayList<String>())
                .set("currency", Currency.getInstance("USD"))
                .build();
        MetaProperty<Object> mp = bean.metaBean().metaProperty("number");
        MetaProperty<Object> mp2 = bean.metaBean().metaProperty("street");
        long start = System.nanoTime();
        int total = 0;
        for (int i = 0; i < 1000000; i++) {
            total += ((Integer) mp.get(bean)) + mp.get(bean).toString().length() + mp2.get(bean).toString().length();
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000) + "ms " + total);
    }

    private static void direct2(ImmPerson person) {
        Direct bean = (Direct) Direct.meta().builder()
                .setString("number", "12")
                .setString("street", "Park Lane")
                .setString("city", "Smallville")
                .set("owner", person)
                .set("list", new ArrayList<String>())
                .set("currency", Currency.getInstance("USD"))
                .build();
        MetaProperty<Object> mp = bean.metaBean().metaProperty("number");
        MetaProperty<Object> mp2 = bean.metaBean().metaProperty("street");
        long start = System.nanoTime();
        int total = 0;
        for (int i = 0; i < 1000000; i++) {
            total += ((Integer) mp.get(bean)) + mp.get(bean).toString().length() + mp2.get(bean).toString().length();
        }
        long end = System.nanoTime();
        System.out.println(((end - start) / 1000000) + "ms " + total);
    }

//    public void test_mutable() {
//        MutableHandle bean = (MutableHandle) MutableHandle.meta().builder()
//                .setString("number", "12")
//                .setString("text", "Park Lane")
//                .setString("city", "London")
//                .set("list", new ArrayList<String>())
//                .set("currency", Currency.getInstance("USD"))
//                .build();
//        
//        assertEquals(bean.getNumber(), 12);
//        assertEquals(bean.getText(), "Park Lane");
//        assertEquals(bean.getList(), ImmutableList.of());
//        assertEquals(bean.getCurrency(), Optional.of(Currency.getInstance("USD")));
//        
//        assertEquals(bean.metaBean().beanType(), MutableHandle.class);
//        assertEquals(bean.metaBean().metaPropertyCount(), 6);
//        assertEquals(bean.metaBean().metaPropertyExists("number"), true);
//        assertEquals(bean.metaBean().metaPropertyExists("text"), true);
//        assertEquals(bean.metaBean().metaPropertyExists("foobar"), false);
//        
//        MetaProperty<Object> mp = bean.metaBean().metaProperty("number");
//        assertEquals(mp.propertyType(), int.class);
//        assertEquals(mp.declaringType(), MutableHandle.class);
//        assertEquals(mp.get(bean), 12);
//        assertEquals(mp.style(), PropertyStyle.READ_WRITE);
//        
//        MetaProperty<Object> mp2 = bean.metaBean().metaProperty("currency");
//        assertEquals(mp2.propertyType(), Optional.class);
//        assertEquals(mp2.declaringType(), MutableHandle.class);
//        assertEquals(mp2.get(bean), Optional.of(Currency.getInstance("USD")));
//        assertEquals(mp2.style(), PropertyStyle.READ_WRITE);
//        
//        assertTrue(JodaBeanSer.PRETTY.xmlWriter().write(bean).contains("<currency>USD<"));
//        assertFalse(JodaBeanSer.PRETTY.xmlWriter().write(bean).contains("<town>"));
//    }

}
