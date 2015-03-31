/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.komodo.web.share.beans;

import java.util.List;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.komodo.spi.repository.PropertyValueType;

/**
 * A data bean for returning KomodoObject Property info
 */
@Portable
@Bindable
public class KomodoObjectPropertyBean {

    private String name;

    private Object value;

    private PropertyValueType valueType;

    private boolean multiple;

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the valueType
     */
    public PropertyValueType getValueType() {
        return this.valueType;
    }

    /**
     * @param valueType the valueType to set
     */
    public void setValueType(PropertyValueType valueType) {
        this.valueType = valueType;
    }

    /**
     * If {@link #isMultiple()} is false then returned value is of
     * type Double, String, Integer ...
     * If {@link #isMultiple()} is true then returned value is a
     * {@link List} of type Double, String, Integer ...
     *
     * @return the value.
     *
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * @return the mutliple flag
     */
    public boolean isMultiple() {
        return this.multiple;
    }

    /**
     * @param multiple the multiple flag
     */
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }
}
