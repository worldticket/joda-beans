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
package org.joda.beans.gen;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaBean;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.handle.HandleMetaBean;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandle;

/**
 * Mock address JavaBean, used for testing.
 * 
 * @author Stephen Colebourne
 */
@BeanDefinition(style = "handle", builderScope = "public")
public final class Handle implements ImmutableBean, Serializable {

    /**
     * The number.
     */
    @PropertyDefinition
    private final int number;
    /**
     * The number.
     */
    @PropertyDefinition
    private final boolean flag;
    /**
     * The street.
     */
    @PropertyDefinition(validate = "notNull", get = "field")
    private final String street;
    /**
     * The town.
     */
    @PropertyDefinition(get = "optionalGuava")
    private final String town;
    /**
     * The city.
     */
    @PropertyDefinition(validate = "notNull")
    private final String city;
    /**
     * The owner.
     */
    @PropertyDefinition(validate = "notNull")
    private final ImmPerson owner;
    /**
     * The list.
     */
    @PropertyDefinition(validate = "notNull")
    private final ImmutableList<String> list;
    /**
     * The currency.
     */
    @PropertyDefinition(get = "optionalGuava")
    private final Currency currency;

    //-----------------------------------------------------------------------
    // manual getter with a different name
    public String getStreetName() {
        return street;
    }

    //------------------------- AUTOGENERATED START -------------------------
    ///CLOVER:OFF
    /**
     * The constructor method handle.
     */
    private static final MethodHandle CONSTRUCTOR_HANDLE;
    static {
        try {
            CONSTRUCTOR_HANDLE = MethodHandles.lookup().findConstructor(
                    Handle.class,
                    MethodType.methodType(
                            void.class,
                            int.class,
                            boolean.class,
                            String.class,
                            String.class,
                            String.class,
                            ImmPerson.class,
                            List.class,
                            Currency.class));
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }
    /**
     * The meta-bean for {@code Handle}.
     */
    private static final MetaBean META_BEAN =
            HandleMetaBean.of(Handle.class, MethodHandles.lookup(), CONSTRUCTOR_HANDLE);

    /**
     * The meta-bean for {@code Handle}.
     * @return the meta-bean, not null
     */
    public static MetaBean meta() {
        return META_BEAN;
    }

    static {
        JodaBeanUtils.registerMetaBean(META_BEAN);
    }

    /**
     * The serialization version id.
     */
    private static final long serialVersionUID = 1L;

    private Handle(
            int number,
            boolean flag,
            String street,
            String town,
            String city,
            ImmPerson owner,
            List<String> list,
            Currency currency) {
        JodaBeanUtils.notNull(street, "street");
        JodaBeanUtils.notNull(city, "city");
        JodaBeanUtils.notNull(owner, "owner");
        JodaBeanUtils.notNull(list, "list");
        this.number = number;
        this.flag = flag;
        this.street = street;
        this.town = town;
        this.city = city;
        this.owner = owner;
        this.list = ImmutableList.copyOf(list);
        this.currency = currency;
    }

    @Override
    public MetaBean metaBean() {
        return META_BEAN;
    }

    @Override
    public <R> Property<R> property(String propertyName) {
        return metaBean().<R>metaProperty(propertyName).createProperty(this);
    }

    @Override
    public Set<String> propertyNames() {
        return metaBean().metaPropertyMap().keySet();
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number.
     * @return the value of the property
     */
    public int getNumber() {
        return number;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the number.
     * @return the value of the property
     */
    public boolean isFlag() {
        return flag;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the town.
     * @return the optional value of the property, not null
     */
    public Optional<String> getTown() {
        return Optional.fromNullable(town);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the city.
     * @return the value of the property, not null
     */
    public String getCity() {
        return city;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the owner.
     * @return the value of the property, not null
     */
    public ImmPerson getOwner() {
        return owner;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the list.
     * @return the value of the property, not null
     */
    public ImmutableList<String> getList() {
        return list;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the currency.
     * @return the optional value of the property, not null
     */
    public Optional<Currency> getCurrency() {
        return Optional.fromNullable(currency);
    }

    //-----------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            Handle other = (Handle) obj;
            return (number == other.number) &&
                    (flag == other.flag) &&
                    JodaBeanUtils.equal(street, other.street) &&
                    JodaBeanUtils.equal(town, other.town) &&
                    JodaBeanUtils.equal(city, other.city) &&
                    JodaBeanUtils.equal(owner, other.owner) &&
                    JodaBeanUtils.equal(list, other.list) &&
                    JodaBeanUtils.equal(currency, other.currency);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        hash = hash * 31 + JodaBeanUtils.hashCode(number);
        hash = hash * 31 + JodaBeanUtils.hashCode(flag);
        hash = hash * 31 + JodaBeanUtils.hashCode(street);
        hash = hash * 31 + JodaBeanUtils.hashCode(town);
        hash = hash * 31 + JodaBeanUtils.hashCode(city);
        hash = hash * 31 + JodaBeanUtils.hashCode(owner);
        hash = hash * 31 + JodaBeanUtils.hashCode(list);
        hash = hash * 31 + JodaBeanUtils.hashCode(currency);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(288);
        buf.append("Handle{");
        buf.append("number").append('=').append(number).append(',').append(' ');
        buf.append("flag").append('=').append(flag).append(',').append(' ');
        buf.append("street").append('=').append(street).append(',').append(' ');
        buf.append("town").append('=').append(town).append(',').append(' ');
        buf.append("city").append('=').append(city).append(',').append(' ');
        buf.append("owner").append('=').append(owner).append(',').append(' ');
        buf.append("list").append('=').append(list).append(',').append(' ');
        buf.append("currency").append('=').append(JodaBeanUtils.toString(currency));
        buf.append('}');
        return buf.toString();
    }

    ///CLOVER:ON
    //-------------------------- AUTOGENERATED END --------------------------
}
