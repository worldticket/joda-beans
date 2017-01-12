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

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.joda.beans.Bean;
import org.joda.beans.ImmutableBean;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyStyle;
import org.joda.beans.impl.BasicMetaProperty;
import org.joda.beans.impl.BasicProperty;
import org.joda.beans.impl.light.PropertyGetter;

/**
 * An immutable meta-property based on a getter interface.
 * <p>
 * The {@link PropertyGetter} interface is used to query the target.
 * 
 * @param <P>  the type of the property content
 * @author Stephen Colebourne
 */
final class HandleMetaProperty<P> extends BasicMetaProperty<P> {

    /** The meta-bean. */
    private final MetaBean metaBean;
    /** The type of the property. */
    private final Class<P> propertyType;
    /** The type of the property. */
    private final Type propertyGenericType;
    /** The annotations. */
    private final List<Annotation> annotations;
    /** The read method. */
    private final MethodHandle getter;
    /** The index of the property in the constructor. */
    private final int constructorIndex;

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
    static <P> HandleMetaProperty<P> of(
            MetaBean metaBean,
            Field field,
            MethodHandles.Lookup lookup,
            String propertyName,
            int constructorIndex) {
        
        MethodHandle getter;
        try {
            getter = lookup.findGetter(field.getDeclaringClass(), field.getName(), field.getType());
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
        } catch (NoSuchFieldException ex) {
            throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
        }
        return new HandleMetaProperty<>(
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
    static <P> HandleMetaProperty<P> of(
            MetaBean metaBean,
            Field field,
            final Method method,
            MethodHandles.Lookup lookup,
            final String propertyName,
            int constructorIndex) {
        
        MethodHandle getter;
        try {
            MethodType type = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
            getter = lookup.findVirtual(field.getDeclaringClass(), method.getName(), type);
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
        } catch (NoSuchMethodException ex) {
            throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException("Property cannot be read: " + propertyName, ex);
        }
        // special case for optional
        Class<P> propertyType = (Class<P>) field.getType();
        Type propertyGenericType = field.getGenericType();
        if (method.getReturnType().getName().contains("Optional")) {
            propertyType = (Class<P>) method.getReturnType();
            propertyGenericType = method.getGenericReturnType();
        }
        return new HandleMetaProperty<>(
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
    HandleMetaProperty(
            MetaBean metaBean, 
            String propertyName,
            Class<P> propertyType,
            Type propertyGenericType,
            List<Annotation> annotations,
            MethodHandle getter,
            int constructorIndex) {
        
        super(propertyName);
        this.metaBean = metaBean;
        this.propertyType = propertyType;
        this.propertyGenericType = propertyGenericType;
        this.annotations = annotations;
        this.getter = getter.asType(MethodType.methodType(Object.class, Bean.class));
        this.constructorIndex = constructorIndex;
    }

    //-----------------------------------------------------------------------
    @Override
    public Property<P> createProperty(Bean bean) {
        return BasicProperty.of(bean, this);
    }

    @Override
    public MetaBean metaBean() {
        return metaBean;
    }

    @Override
    public Class<?> declaringType() {
        return metaBean.beanType();
    }

    @Override
    public Class<P> propertyType() {
        return propertyType;
    }

    @Override
    public Type propertyGenericType() {
        return propertyGenericType;
    }

    @Override
    public PropertyStyle style() {
        return ImmutableBean.class.isAssignableFrom(metaBean.beanType()) ? PropertyStyle.IMMUTABLE : PropertyStyle.READ_WRITE;
    }

    @Override
    public List<Annotation> annotations() {
        return annotations;
    }

    //-----------------------------------------------------------------------
    @Override
    public P get(Bean bean) {
        try {
            return (P) getter.invokeExact(bean);
        } catch (RuntimeException | Error ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void set(Bean bean, Object value) {
        throw new UnsupportedOperationException("Property cannot be written: " + name());
    }

    int getConstructorIndex() {
        return constructorIndex;
    }

}
