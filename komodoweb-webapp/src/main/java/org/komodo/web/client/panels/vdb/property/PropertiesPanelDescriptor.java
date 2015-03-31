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
package org.komodo.web.client.panels.vdb.property;

import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.user.client.ui.Widget;

/**
 * Properties panel descriptor
 * @param <T> the type of panel widget supported by this descriptor
 */
public abstract class PropertiesPanelDescriptor<T extends Widget> {

    private final String id;

    /**
     * Create new instance
     *
     * @param id id of descriptor
     */
    public PropertiesPanelDescriptor(String id) {
        this.id = id;
    }

    /**
     * @return id
     */
    public String id() {
        return id;
    }

    /**
     * @param parentWidth parent width
     * @param parentHeight parent height
     *
     * @return the widget for this descriptor
     */
    public abstract T create(double parentWidth, double parentHeight);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PropertiesPanelDescriptor other = (PropertiesPanelDescriptor)obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        return true;
    }

    /**
     * Populate the given panel using the given object. The descriptor
     * should check the panel is of the 
     *
     * @param panel the panel on which to set the content
     * @param kObject the object from which to derive the content
     */
    public abstract void setContent(T panel, KomodoObjectBean kObject);

}
