/*
 *  Copyright 2001-present Stephen Colebourne
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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.beans.gen.Address;
import org.joda.beans.gen.ImmAddress;
import org.joda.beans.gen.ImmPerson;
import org.joda.beans.gen.MetaBeanLoad;
import org.joda.beans.gen.Pair;
import org.joda.beans.gen.Person;
import org.joda.beans.impl.flexi.FlexiBean;
import org.joda.beans.impl.map.MapBean;
import org.joda.beans.query.ChainedBeanQuery;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultiset;

/**
 * Test utils.
 */
@Test
public class TestJodaBeanUtils {

    //-----------------------------------------------------------------------
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_registerMetaBean() {
        // register once OK
        assertNotNull(ImmPerson.meta());
        // register second time not OK
        JodaBeanUtils.registerMetaBean(ImmPerson.meta());
    }

    //-----------------------------------------------------------------------
    public void test_notNull_ok() {
        JodaBeanUtils.notNull("", "name");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_notNull_notOk() {
        JodaBeanUtils.notNull(null, "name");
    }

    //-----------------------------------------------------------------------
    public void test_notEmpty_String_ok() {
        JodaBeanUtils.notEmpty("Blah", "name");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_notEmpty_String_notOk_empty() {
        JodaBeanUtils.notEmpty("", "name");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_notEmpty_String_notOk_null() {
        JodaBeanUtils.notEmpty((String) null, "name");
    }

    //-----------------------------------------------------------------------
    public void test_notEmpty_Collection_ok() {
        JodaBeanUtils.notEmpty(Arrays.asList("Blah"), "name");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_notEmpty_Collection_notOk_empty() {
        JodaBeanUtils.notEmpty(new ArrayList<String>(), "name");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_notEmpty_Collection_notOk_null() {
        JodaBeanUtils.notEmpty((Collection<?>) null, "name");
    }

    //-----------------------------------------------------------------------
    public void test_notEmpty_Map_ok() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("A", "B");
        JodaBeanUtils.notEmpty(map, "name");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_notEmpty_Map_notOk_empty() {
        JodaBeanUtils.notEmpty(new HashMap<String, String>(), "name");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_notEmpty_Map_notOk_null() {
        JodaBeanUtils.notEmpty((Map<?, ?>) null, "name");
    }

    //-----------------------------------------------------------------------
    public void test_metaBean() {
        MetaBean metaBean = JodaBeanUtils.metaBean(MetaBeanLoad.class);
        assertNotNull(metaBean);
        assertEquals(metaBean, MetaBeanLoad.meta());
    }

    public void test_metaBean_FlexiBean() {
        assertEquals(JodaBeanUtils.metaBean(FlexiBean.class).builder().build().getClass(), FlexiBean.class);
    }

    public void test_metaBean_MapBean() {
        assertEquals(JodaBeanUtils.metaBean(MapBean.class).builder().build().getClass(), MapBean.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void test_metaBean_notFound() {
        JodaBeanUtils.metaBean(String.class);
    }

    //-----------------------------------------------------------------------
    public void test_propertiesEqual_propertiesHashCode() {
        Pair a = new Pair();
        a.setFirst("A");
        
        FlexiBean b = new FlexiBean();
        b.set("first", "A");
        assertEquals(JodaBeanUtils.propertiesEqual(a, b), false);
        assertEquals(JodaBeanUtils.propertiesEqual(b, a), false);
        
        b.set("second", null);
        assertEquals(JodaBeanUtils.propertiesEqual(a, b), true);
        assertEquals(JodaBeanUtils.propertiesEqual(b, a), true);
        assertEquals(JodaBeanUtils.propertiesHashCode(a), JodaBeanUtils.propertiesHashCode(b));
        
        b.set("second", "B");
        assertEquals(JodaBeanUtils.propertiesEqual(a, b), false);
        assertEquals(JodaBeanUtils.propertiesEqual(b, a), false);
        
        a.setSecond("B");
        assertEquals(JodaBeanUtils.propertiesEqual(a, b), true);
        assertEquals(JodaBeanUtils.propertiesEqual(b, a), true);
        assertEquals(JodaBeanUtils.propertiesHashCode(a), JodaBeanUtils.propertiesHashCode(b));
    }

    //-------------------------------------------------------------------------
    public void test_equal() {
        assertEquals(JodaBeanUtils.equal("A", new Character('A').toString()), true);
        assertEquals(JodaBeanUtils.equal("A", "B"), false);
        assertEquals(JodaBeanUtils.equal("A", null), false);
        assertEquals(JodaBeanUtils.equal(null, "A"), false);
    }

    public void test_equal_ObjectArray() {
        Object[] a1 = new Object[] {1, 2, 3};
        Object[] a2 = new Object[] {1, 2, 3};
        Object[] b = new Object[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_IntegerArray() {
        Object[] a1 = new Integer[] {1, 2, 3};
        Object[] a2 = new Integer[] {1, 2, 3};
        Object[] b = new Integer[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_IntegerNumberArray() {
        Object[] a = new Integer[] {1, 2, 3};
        Object[] b = new Number[] {1, 2, 3};
        assertEquals(JodaBeanUtils.equal(a, b), false);
        assertEquals(JodaBeanUtils.equal(b, a), false);
    }

    public void test_equal_IntegerIntArray() {
        Object[] a = new Integer[] {1, 2, 3};
        int[] b = new int[] {1, 2, 3};
        assertEquals(JodaBeanUtils.equal(a, b), false);
        assertEquals(JodaBeanUtils.equal(b, a), false);
    }

    public void test_equal_IntArray() {
        int[] a1 = new int[] {1, 2, 3};
        int[] a2 = new int[] {1, 2, 3};
        int[] b = new int[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_MixedArrays() {
        byte[] b = new byte[] {1, 2, 3};
        short[] s = new short[] {1, 2, 3};
        int[] i = new int[] {1, 2, 3};
        long[] l = new long[] {1, 2, 4};
        boolean[] bl = new boolean[] {true, false, true};
        char[] c = new char[] {'1', '2', '3'};
        float[] f = new float[] {1f, 2f, 3f};
        double[] d = new double[] {1d, 2d, 3d};
        assertEquals(JodaBeanUtils.equal(b, i), false);
        assertEquals(JodaBeanUtils.equal(s, i), false);
        assertEquals(JodaBeanUtils.equal(i, l), false);
        assertEquals(JodaBeanUtils.equal(l, i), false);
        assertEquals(JodaBeanUtils.equal(bl, i), false);
        assertEquals(JodaBeanUtils.equal(c, i), false);
        assertEquals(JodaBeanUtils.equal(f, i), false);
        assertEquals(JodaBeanUtils.equal(d, i), false);
    }

    public void test_equal_LongArray() {
        long[] a1 = new long[] {1, 2, 3};
        long[] a2 = new long[] {1, 2, 3};
        long[] b = new long[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_DoubleArray() {
        double[] a1 = new double[] {1, 2, 3};
        double[] a2 = new double[] {1, 2, 3};
        double[] b = new double[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_FloatArray() {
        float[] a1 = new float[] {1, 2, 3};
        float[] a2 = new float[] {1, 2, 3};
        float[] b = new float[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_ShortArray() {
        short[] a1 = new short[] {1, 2, 3};
        short[] a2 = new short[] {1, 2, 3};
        short[] b = new short[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_CharArray() {
        char[] a1 = new char[] {1, 2, 3};
        char[] a2 = new char[] {1, 2, 3};
        char[] b = new char[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_ByteArray() {
        byte[] a1 = new byte[] {1, 2, 3};
        byte[] a2 = new byte[] {1, 2, 3};
        byte[] b = new byte[] {1, 2, 4};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    public void test_equal_BooleanArray() {
        boolean[] a1 = new boolean[] {true, false};
        boolean[] a2 = new boolean[] {true, false};
        boolean[] b = new boolean[] {true, true};
        assertEquals(JodaBeanUtils.equal(a1, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, a2), true);
        assertEquals(JodaBeanUtils.equal(a2, a1), true);
        assertEquals(JodaBeanUtils.equal(a1, b), false);
        assertEquals(JodaBeanUtils.equal(b, a1), false);
    }

    //-----------------------------------------------------------------------
    public void test_equal_floats() {
        assertEquals(JodaBeanUtils.equal(1.01f, 1.01f), true);
        assertEquals(JodaBeanUtils.equal(1.0f, 1.2f), false);
    }

    public void test_equalWithTolerance_floats_zeroTolerance() {
        double tolerance = 0d;
        assertEquals(JodaBeanUtils.equalWithTolerance(1.01f, 1.01f, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, 1.2f, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NaN, Float.NaN, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NaN, 1f, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, Float.NaN, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.POSITIVE_INFINITY, Float.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.MAX_VALUE, Float.POSITIVE_INFINITY, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NEGATIVE_INFINITY, -Float.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(-Float.MAX_VALUE, Float.NEGATIVE_INFINITY, tolerance), false);
    }

    public void test_equalWithTolerance_floats_someTolerance() {
        double tolerance = 0.125d;
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, 1.250001f, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, 1.125f, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, 1.124999f, tolerance), true);

        assertEquals(JodaBeanUtils.equalWithTolerance(1f, 0.875001f, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, 0.875f, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, 0.874999f, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NaN, Float.NaN, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NaN, 1f, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(1f, Float.NaN, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.POSITIVE_INFINITY, Float.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.MAX_VALUE, Float.POSITIVE_INFINITY, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, 0d), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Float.NEGATIVE_INFINITY, -Float.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(-Float.MAX_VALUE, Float.NEGATIVE_INFINITY, tolerance), false);
    }

    //-----------------------------------------------------------------------
    public void test_equal_doubles() {
        assertEquals(JodaBeanUtils.equal(1.01d, 1.01d), true);
        assertEquals(JodaBeanUtils.equal(1.0d, 1.2d), false);

        assertEquals(JodaBeanUtils.equal(Double.NaN, Double.NaN), true);
        assertEquals(JodaBeanUtils.equal(Double.NaN, 1d), false);
        assertEquals(JodaBeanUtils.equal(1d, Double.NaN), false);

        assertEquals(JodaBeanUtils.equal(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY), true);
        assertEquals(JodaBeanUtils.equal(Double.POSITIVE_INFINITY, Double.MAX_VALUE), false);
        assertEquals(JodaBeanUtils.equal(Double.MAX_VALUE, Double.POSITIVE_INFINITY), false);

        assertEquals(JodaBeanUtils.equal(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), true);
        assertEquals(JodaBeanUtils.equal(Double.NEGATIVE_INFINITY, -Double.MAX_VALUE), false);
        assertEquals(JodaBeanUtils.equal(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY), false);
    }

    public void test_equalWithTolerance_doubles_zeroTolerance() {
        double tolerance = 0d;
        assertEquals(JodaBeanUtils.equalWithTolerance(1.01d, 1.01d, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, 1.2d, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NaN, Double.NaN, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NaN, 1d, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, Double.NaN, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.POSITIVE_INFINITY, Double.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.MAX_VALUE, Double.POSITIVE_INFINITY, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NEGATIVE_INFINITY, -Double.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, tolerance), false);
    }

    public void test_equalWithTolerance_doubles_someTolerance() {
        double tolerance = 0.125d;
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, 1.250001d, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, 1.125d, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, 1.124999d, tolerance), true);

        assertEquals(JodaBeanUtils.equalWithTolerance(1d, 0.875001d, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, 0.875d, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, 0.874999d, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NaN, Double.NaN, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NaN, 1d, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(1d, Double.NaN, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, tolerance), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.POSITIVE_INFINITY, Double.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.MAX_VALUE, Double.POSITIVE_INFINITY, tolerance), false);

        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0d), true);
        assertEquals(JodaBeanUtils.equalWithTolerance(Double.NEGATIVE_INFINITY, -Double.MAX_VALUE, tolerance), false);
        assertEquals(JodaBeanUtils.equalWithTolerance(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY, tolerance), false);
    }

    //-----------------------------------------------------------------------
    public void test_hashCode_Object() {
        assertEquals(JodaBeanUtils.hashCode("A"), "A".hashCode());
        assertEquals(JodaBeanUtils.hashCode(null), 0);
        assertEquals(JodaBeanUtils.hashCode(new byte[] {1}), Arrays.hashCode(new byte[] {1}));
        assertEquals(JodaBeanUtils.hashCode(new short[] {1}), Arrays.hashCode(new short[] {1}));
        assertEquals(JodaBeanUtils.hashCode(new int[] {1}), Arrays.hashCode(new int[] {1}));
        assertEquals(JodaBeanUtils.hashCode(new long[] {1}), Arrays.hashCode(new long[] {1}));
        assertEquals(JodaBeanUtils.hashCode(new boolean[] {true}), Arrays.hashCode(new boolean[] {true}));
        assertEquals(JodaBeanUtils.hashCode(new char[] {'1'}), Arrays.hashCode(new char[] {'1'}));
        assertEquals(JodaBeanUtils.hashCode(new float[] {1f}), Arrays.hashCode(new float[] {1f}));
        assertEquals(JodaBeanUtils.hashCode(new double[] {1d}), Arrays.hashCode(new double[] {1d}));
    }

    //-----------------------------------------------------------------------
    public void test_clone() {
        Person p = new Person();
        p.setForename("Stephen");
        p.setSurname("Colebourne");
        p.getOtherAddressMap().put("A", new Address());
        p.getOtherAddressMap().get("A").setCity("London");
        Person cloned = JodaBeanUtils.clone(p);
        assertNotSame(cloned, p);
        assertEquals(cloned, p);
        p.getOtherAddressMap().put("B", new Address());
        assertFalse(cloned.equals(p));
    }

    //-----------------------------------------------------------------------
    public void test_listType_Person_addressList() {
        MetaProperty<List<Address>> test = Person.meta().addressList();
        
        assertEquals(JodaBeanUtils.collectionType(test, Person.class), Address.class);
    }

    public void test_listType_Person_addressesList() {
        MetaProperty<List<List<Address>>> test = Person.meta().addressesList();
        
        assertEquals(JodaBeanUtils.collectionType(test, Person.class), List.class);
    }

    public void test_multisetType_Person_otherAddressMap() {
        MetaProperty<ImmutableMultiset<String>> test = ImmPerson.meta().codeCounts();
        
        assertEquals(JodaBeanUtils.collectionType(test, Person.class), String.class);
    }

    public void test_integerType_Person_collectionTypeInvalid() {
        MetaProperty<Integer> test = ImmPerson.meta().age();
        
        assertEquals(JodaBeanUtils.collectionType(test, Person.class), null);
    }

    //-------------------------------------------------------------------------
    public void test_collectionTypeTypes_valid() {
        MetaProperty<List<List<Address>>> test = Person.meta().addressesList();
        
        List<Class<?>> expected = ImmutableList.<Class<?>>of(Address.class);
        assertEquals(JodaBeanUtils.collectionTypeTypes(test, Person.class), expected);
    }

    public void test_collectionTypeTypes_invalidNoGenerics() {
        MetaProperty<List<Address>> test = Person.meta().addressList();
        
        List<Class<?>> expected = Collections.emptyList();
        assertEquals(JodaBeanUtils.collectionTypeTypes(test, Person.class), expected);
    }

    public void test_collectionTypeTypes_invalidNotCollection() {
        MetaProperty<ImmutableMap<String, List<Integer>>> test = ImmAddress.meta().listNumericInMap();
        
        List<Class<?>> expected = Collections.emptyList();
        assertEquals(JodaBeanUtils.collectionTypeTypes(test, ImmAddress.class), expected);
    }

    //-------------------------------------------------------------------------
    public void test_mapType_Person_otherAddressMap() {
        MetaProperty<Map<String, Address>> test = Person.meta().otherAddressMap();
        
        assertEquals(JodaBeanUtils.mapKeyType(test, Person.class), String.class);
        assertEquals(JodaBeanUtils.mapValueType(test, Person.class), Address.class);
    }

    public void test_integerType_Person_mapKeyTypeInvalid() {
        MetaProperty<Integer> test = ImmPerson.meta().age();
        
        assertEquals(JodaBeanUtils.mapKeyType(test, Person.class), null);
        assertEquals(JodaBeanUtils.mapValueType(test, Person.class), null);
    }

    public void test_collectionType_Person_mapKeyTypeInvalid() {
        MetaProperty<List<Address>> test = Person.meta().addressList();
        
        assertEquals(JodaBeanUtils.mapKeyType(test, Person.class), null);
        assertEquals(JodaBeanUtils.mapValueType(test, Person.class), null);
    }

    //-------------------------------------------------------------------------
    public void test_mapValueTypeTypes_valid() {
        MetaProperty<ImmutableMap<String, List<Integer>>> test = ImmAddress.meta().listNumericInMap();
        
        List<Class<?>> expected = ImmutableList.<Class<?>>of(Integer.class);
        assertEquals(JodaBeanUtils.mapValueTypeTypes(test, ImmAddress.class), expected);
    }

    public void test_mapValueTypeTypes_invalidNoGenerics() {
        MetaProperty<Map<String, Address>> test = Person.meta().otherAddressMap();
        
        List<Class<?>> expected = Collections.emptyList();
        assertEquals(JodaBeanUtils.mapValueTypeTypes(test, Person.class), expected);
    }

    public void test_mapValueTypeTypes_invalidNotMap() {
        MetaProperty<List<Address>> test = Person.meta().addressList();
        
        List<Class<?>> expected = Collections.emptyList();
        assertEquals(JodaBeanUtils.mapValueTypeTypes(test, Person.class), expected);
    }

    //-------------------------------------------------------------------------
    @Test
    public void equalIgnoring() {
        Bean bean1 = createBean("123", "321", "name1");
        Bean bean2 = createBean("124", "321", "name1");
        // first not ignored
        assertFalse(JodaBeanUtils.equalIgnoring(bean1, bean2));
        assertFalse(JodaBeanUtils.equalIgnoring(bean1, bean2, bean1.metaBean().metaProperty("second")));
        assertFalse(JodaBeanUtils.equalIgnoring(bean1, bean2, bean1.metaBean().metaProperty("second"), bean1.metaBean().metaProperty("name")));
        // first is ignored
        assertTrue(JodaBeanUtils.equalIgnoring(bean1, bean2, bean1.metaBean().metaProperty("first")));
        assertTrue(JodaBeanUtils.equalIgnoring(bean1, bean2, bean1.metaBean().metaProperty("first"), bean1.metaBean().metaProperty("second")));
    }

    @Test
    public void equalIgnoring_same() {
        Bean bean1 = createBean("123", "321", "name1");
        Bean bean2 = createBean("124", "321", "name1");
        assertTrue(JodaBeanUtils.equalIgnoring(bean1, bean1));
        assertTrue(JodaBeanUtils.equalIgnoring(bean2, bean2));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void equalIgnoring_nullFirst() {
        Bean bean = createBean("124", "321", "name1");
        JodaBeanUtils.equalIgnoring(null, bean);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void equalIgnoring_nullSecond() {
        Bean bean = createBean("124", "321", "name1");
        JodaBeanUtils.equalIgnoring(bean, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void equalIgnoring_nullArray() {
        Bean bean = createBean("124", "321", "name1");
        JodaBeanUtils.equalIgnoring(bean, bean, (MetaProperty<?>[]) null);
    }

    private static Bean createBean(String first, String second, String name) {
        FlexiBean bean = new FlexiBean();
        bean.propertySet("first", first);
        bean.propertySet("second", second);
        bean.propertySet("name", name);
        return bean;
    }

    //-------------------------------------------------------------------------
    public void test_compare_ascending() {
        Address address1 = new Address();
        address1.setOwner(new Person());
        address1.getOwner().setSurname("Joda");
        Address address2 = new Address();
        address2.setOwner(new Person());
        address2.getOwner().setSurname("Beans");
        ChainedBeanQuery<String> bq = ChainedBeanQuery.of(Address.meta().owner(), Person.meta().surname());
        
        Comparator<Bean> asc = JodaBeanUtils.comparator(bq, true);
        assertEquals(asc.compare(address1, address1) == 0, true);
        assertEquals(asc.compare(address1, address2) > 1, true);
        assertEquals(asc.compare(address2, address1) < 1, true);
    }

    public void test_compare_descending() {
        Address address1 = new Address();
        address1.setOwner(new Person());
        address1.getOwner().setSurname("Joda");
        Address address2 = new Address();
        address2.setOwner(new Person());
        address2.getOwner().setSurname("Beans");
        ChainedBeanQuery<String> bq = ChainedBeanQuery.of(Address.meta().owner(), Person.meta().surname());
        
        Comparator<Bean> desc = JodaBeanUtils.comparator(bq, false);
        assertEquals(desc.compare(address1, address1) == 0, true);
        assertEquals(desc.compare(address1, address2) < 1, true);
        assertEquals(desc.compare(address2, address1) > 1, true);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compare_ascending_null() {
        JodaBeanUtils.comparatorAscending(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void test_compare_descending_null() {
        JodaBeanUtils.comparatorDescending(null);
    }

}
