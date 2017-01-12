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
package org.joda.beans.impl.handle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.MetaBean;
import org.joda.beans.MetaProperty;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.PropertyMap;
import org.joda.beans.impl.BasicPropertyMap;

/**
 * A meta-bean implementation that operates using method handles.
 * <p>
 * The properties are found using the {@link PropertyDefinition} annotation.
 * Only immutable beans are supported.
 * There must be a constructor matching the property definitions (arguments of same order and types).
 * 
 * @author Stephen Colebourne
 * @param <T>  the type of the bean
 */
public final class HandleMetaBean<T extends Bean> implements MetaBean {

    /** The bean type. */
    private final Class<? extends Bean> beanType;
    /** The meta-property instances of the bean. */
    private final Map<String, MetaProperty<?>> metaPropertyMap;
    /** The constructor to use. */
    private final MethodHandle constructor;
    /** The construction data array. */
    private final Object[] constructionData;

    /**
     * Obtains an instance of the meta-bean.
     * <p>
     * The properties will be determined using reflection to find the
     * {@link PropertyDefinition} annotation.
     * 
     * @param <B>  the type of the bean
     * @param beanClass  the bean class, not null
     * @param lookup  the method handle lookup, not null
     * @param constructorHandle  the constructor handle, not null
     * @return the meta-bean, not null
     */
    public static <B extends Bean> HandleMetaBean<B> of(
            Class<B> beanClass,
            MethodHandles.Lookup lookup,
            MethodHandle constructorHandle) {
        return new HandleMetaBean<>(beanClass, lookup, constructorHandle);
    }

    /**
     * Constructor.
     * 
     * @param beanType  the bean type, not null
     */
    private HandleMetaBean(Class<T> beanType, MethodHandles.Lookup lookup, MethodHandle constructorHandle) {
        if (beanType == null) {
            throw new NullPointerException("Bean class must not be null");
        }
        if (lookup == null) {
            throw new NullPointerException("Lookup must not be null");
        }
        this.beanType = beanType;
        Map<String, MetaProperty<?>> map = new HashMap<>();
        Field[] fields = beanType.getDeclaredFields();
        List<Class<?>> propertyTypes = new ArrayList<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && field.getAnnotation(PropertyDefinition.class) != null) {
                PropertyDefinition pdef = field.getAnnotation(PropertyDefinition.class);
                String name = field.getName();
                if (pdef.get().equals("field") || pdef.get().equals("optional")) {
                    map.put(name, HandleMetaProperty.of(this, field, lookup, name, propertyTypes.size()));
                    propertyTypes.add(field.getType());
                } else if (!pdef.get().equals("")) {
                    String getterName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method method = null;
                    if (field.getType() == boolean.class) {
                        method = findMethod(beanType, "is" + name.substring(0, 1).toUpperCase() + name.substring(1));
                    }
                    if (method == null) {
                        method = findMethod(beanType, getterName);
                        if (method == null) {
                            throw new IllegalArgumentException(
                                "Unable to find property getter: " + beanType.getSimpleName() + "." + getterName + "()");
                        }
                    }
                    map.put(name, HandleMetaProperty.of(this, field, method, lookup, name, propertyTypes.size()));
                    propertyTypes.add(field.getType());
                }
            }
        }
        this.metaPropertyMap = Collections.unmodifiableMap(map);
        Constructor<T> constructor = findConstructor(beanType, propertyTypes);
        this.constructor = constructorHandle;
//        try {
//            this.constructor = lookup.findConstructor(beanType, MethodType.methodType(Void.TYPE, constructor.getParameterTypes()));
//        } catch (NoSuchMethodException ex) {
//            throw new IllegalArgumentException("Unable to find constructor: " + beanType.getSimpleName());
//        } catch (IllegalAccessException ex) {
//            throw new IllegalArgumentException("Unable to access constructor: " + beanType.getSimpleName());
//        }
        this.constructionData = buildConstructionData(constructor);
    }

    // finds a method on class or public method on super-type
    private static Method findMethod(Class<? extends Bean> beanType, String getterName) {
        try {
            return beanType.getDeclaredMethod(getterName);
        } catch (NoSuchMethodException ex) {
            try {
                return beanType.getMethod(getterName);
            } catch (NoSuchMethodException ex2) {
                return null;
            }
        }
    }

    // finds constructor which matches types exactly
    public static <T extends Bean> MethodHandle findConstructorHandle(Class<T> beanType, MethodHandles.Lookup lookup) {
        Field[] fields = beanType.getDeclaredFields();
        List<Class<?>> propertyTypes = new ArrayList<>();
        for (Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers()) && field.getAnnotation(PropertyDefinition.class) != null) {
                PropertyDefinition pdef = field.getAnnotation(PropertyDefinition.class);
                if (!pdef.get().equals("")) {
                    propertyTypes.add(field.getType());
                }
            }
        }
        Constructor<?> constructor = findConstructor(beanType, propertyTypes);
        try {
            return lookup.findConstructor(beanType, MethodType.methodType(Void.TYPE, constructor.getParameterTypes()));
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException("Unable to find constructor: " + beanType.getSimpleName());
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Unable to access constructor: " + beanType.getSimpleName());
        }
    }

    // finds constructor which matches types exactly
    private static <T extends Bean> Constructor<T> findConstructor(Class<T> beanType, List<Class<?>> propertyTypes) {
        Class<?>[] types = propertyTypes.toArray(new Class<?>[propertyTypes.size()]);
        try {
            Constructor<T> con = beanType.getDeclaredConstructor(types);
            return con;
            
        } catch (NoSuchMethodException ex) {
            // try a more lenient search
            // this handles cases where field is a concrete class and constructor is an interface
            @SuppressWarnings("unchecked")
            Constructor<T>[] cons = (Constructor<T>[]) beanType.getDeclaredConstructors();
            Constructor<T> match = null;
            for (int i = 0; i < cons.length; i++) {
                Constructor<T> con = cons[i];
                Class<?>[] conTypes = con.getParameterTypes();
                if (conTypes.length == types.length) {
                    for (int j = 0; j < types.length; j++) {
                        if (!conTypes[j].isAssignableFrom(types[j])) {
                            break;
                        }
                    }
                    if (match != null) {
                        throw new UnsupportedOperationException("Unable to find constructor: More than one matches");
                    }
                    match = con;
                }
            }
            if (match == null) {
                throw new UnsupportedOperationException("Unable to find constructor: " + beanType.getSimpleName(), ex);
            }
            return match;
        }
    }

    // array used to collect data when building
    // needs to have default values for primitives
    private static Object[] buildConstructionData(Constructor<?> constructor) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == boolean.class) {
                args[i] = false;
            } else if (parameterTypes[i] == int.class) {
                args[i] = (int) 0;
            } else if (parameterTypes[i] == long.class) {
                args[i] = (long) 0;
            } else if (parameterTypes[i] == short.class) {
                args[i] = (short) 0;
            } else if (parameterTypes[i] == byte.class) {
                args[i] = (byte) 0;
            } else if (parameterTypes[i] == float.class) {
                args[i] = (float) 0;
            } else if (parameterTypes[i] == double.class) {
                args[i] = (double) 0;
            } else if (parameterTypes[i] == char.class) {
                args[i] = (char) 0;
            }
        }
        return args;
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    T build(Object[] args) {
        try {
            return (T) constructor.invokeWithArguments(args);
            
        } catch (Error ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new IllegalArgumentException(
                    "Bean cannot be created: " + beanName() + " from " + args, ex);
        }
    }

    //-----------------------------------------------------------------------
    @Override
    public BeanBuilder<T> builder() {
        return new HandleBeanBuilder<>(this, constructionData.clone());
    }

    @Override
    public PropertyMap createPropertyMap(Bean bean) {
        return BasicPropertyMap.of(bean);
    }

    //-----------------------------------------------------------------------
    @Override
    public String beanName() {
        return beanType.getName();
    }

    @Override
    public Class<? extends Bean> beanType() {
        return beanType;
    }

    //-----------------------------------------------------------------------
    @Override
    public int metaPropertyCount() {
        return metaPropertyMap.size();
    }

    @Override
    public boolean metaPropertyExists(String propertyName) {
        return metaPropertyMap.containsKey(propertyName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MetaProperty<R> metaProperty(String propertyName) {
        MetaProperty<?> metaProperty = metaPropertyMap.get(propertyName);
        if (metaProperty == null) {
            throw new NoSuchElementException("Property not found: " + propertyName);
        }
        return (MetaProperty<R>) metaProperty;
    }

    @Override
    public Iterable<MetaProperty<?>> metaPropertyIterable() {
        return metaPropertyMap.values();
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
        return metaPropertyMap;
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HandleMetaBean) {
            HandleMetaBean<?> other = (HandleMetaBean<?>) obj;
            return this.beanType.equals(other.beanType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return beanType.hashCode() + 3;
    }

    /**
     * Returns a string that summarises the meta-bean.
     * 
     * @return a summary string, not null
     */
    @Override
    public String toString() {
        return "MetaBean:" + beanName();
    }

}
