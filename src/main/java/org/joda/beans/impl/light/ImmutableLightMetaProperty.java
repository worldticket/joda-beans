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
package org.joda.beans.impl.light;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.joda.beans.Bean;
import org.joda.beans.MetaBean;
import org.joda.beans.PropertyStyle;

/**
 * An immutable meta-property based on a getter interface.
 * 
 * @param <P>  the type of the property content
 * @author Stephen Colebourne
 */
final class ImmutableLightMetaProperty<P> extends LightMetaProperty<P> {

    /** The read method. */
    private final PropertyGetter getter;

    //-----------------------------------------------------------------------
    /**
     * Creates an instance from a {@code Field}.
     * 
     * @param <P>  the property type
     * @param metaBean  the meta bean, not null
     * @param field  the field, not null
     * @param constructorIndex  the index of the property in the constructor
     * @return the property, not null
     */
    @SuppressWarnings("unchecked")
    static <P> ImmutableLightMetaProperty<P> of(
            MetaBean metaBean,
            final Field field,
            final String propertyName,
            int constructorIndex) {
        
        PropertyGetter getter = new PropertyGetter() {
            @Override
            public Object get(Bean bean) {
                try {
                    return field.get(bean);
                } catch (IllegalArgumentException ex) {
                    throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
                } catch (IllegalAccessException ex) {
                    throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
                }
            }
        };
        return new ImmutableLightMetaProperty<P>(
                metaBean, 
                propertyName, 
                (Class<P>) field.getType(), 
                field.getGenericType(), 
                Arrays.asList(field.getAnnotations()), 
                getter,
                constructorIndex);
    }

    /**
     * Creates an instance from a {@code Method}.
     * 
     * @param <P>  the property type
     * @param metaBean  the meta bean, not null
     * @param method  the method, not null
     * @param constructorIndex  the index of the property in the constructor
     * @return the property, not null
     */
    @SuppressWarnings("unchecked")
    static <P> ImmutableLightMetaProperty<P> of(
            MetaBean metaBean,
            Field field,
            final Method method,
            final String propertyName,
            int constructorIndex) {
        
        PropertyGetter getter = new PropertyGetter() {
            @Override
            public Object get(Bean bean) {
                try {
                    return method.invoke(bean);
                } catch (IllegalArgumentException ex) {
                    throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
                } catch (IllegalAccessException ex) {
                    throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
                } catch (InvocationTargetException ex) {
                    if (ex.getCause() instanceof RuntimeException) {
                        throw (RuntimeException) ex.getCause();
                    }
                    throw new RuntimeException(ex);
                }
            }
        };
        // special case for optional
        Class<P> propertyType = (Class<P>) field.getType();
        Type propertyGenericType = field.getGenericType();
        if (method.getReturnType().getName().contains("Optional")) {
            propertyType = (Class<P>) method.getReturnType();
            propertyGenericType = method.getGenericReturnType();
        }
        return new ImmutableLightMetaProperty<P>(
                metaBean, 
                propertyName, 
                propertyType, 
                propertyGenericType, 
                Arrays.asList(field.getAnnotations()), 
                getter,
                constructorIndex);
    }

    /**
     * Creates an instance.
     * 
     * @param metaBean  the meta bean, not null
     * @param propertyName  the property name, not empty
     * @param propertyType  the property type
     * @param propertyGenericType  the property generic type
     * @param annotations  the annotations
     * @param getter  the property getter
     * @param constructorIndex  the index of the property in the constructor
     */
    ImmutableLightMetaProperty(
            MetaBean metaBean, 
            String propertyName,
            Class<P> propertyType,
            Type propertyGenericType,
            List<Annotation> annotations,
            PropertyGetter getter,
            int constructorIndex) {
        
        super(metaBean, propertyName, propertyType, propertyGenericType, annotations, constructorIndex);
        this.getter = getter;
    }

    //-----------------------------------------------------------------------
    @Override
    public PropertyStyle style() {
        return PropertyStyle.IMMUTABLE;
    }

    //-----------------------------------------------------------------------
    @Override
    @SuppressWarnings("unchecked")
    public P get(Bean bean) {
        return (P) getter.get(bean);
    }

    @Override
    public void set(Bean bean, Object value) {
        throw new UnsupportedOperationException("Property cannot be written: " + name());
    }

}
