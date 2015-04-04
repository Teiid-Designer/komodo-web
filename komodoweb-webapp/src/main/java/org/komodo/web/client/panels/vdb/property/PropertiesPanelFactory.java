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

import java.util.HashMap;
import java.util.Map;
import org.komodo.web.client.panels.vdb.property.panel.NoPropertiesPanel;
import org.komodo.web.client.panels.vdb.property.panel.VdbPropertiesPanel;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class PropertiesPanelFactory {

    private Map<String, Widget> panelIndex = new HashMap<String, Widget>();

    private double parentWidth;

    private double parentHeight;

    private ValueChangeHandler<KomodoObjectPropertyBean> valueChangeHandler;

    /**
     * @param parentWidth parent width
     * @param parentHeight parent height
     */
    public void setParentDimensions(double parentWidth, double parentHeight) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
    }

    /**
     * Sets the associated value change handler if the panel needs to
     * advertise its update of properties
     *
     * @param handler the handler
     */
    public void setValueChangeHandler(ValueChangeHandler<KomodoObjectPropertyBean> handler) {
        this.valueChangeHandler = handler;
    }

    /**
     * @return the parentWidth
     */
    public double getParentWidth() {
        return this.parentWidth;
    }

    /**
     * @return the parentHeight
     */
    public double getParentHeight() {
        return this.parentHeight;
    }

    private Widget create(PropertiesPanelDescriptor descriptor, KomodoObjectBean kObject) {
        Widget panel = panelIndex.get(descriptor.id());
        if (panel == null) {
            panel = descriptor.create(parentWidth, parentHeight);
            panelIndex.put(descriptor.id(), panel);

            if (panel instanceof HasValueChangeHandlers) {
                ((HasValueChangeHandlers)panel).addValueChangeHandler(valueChangeHandler);
            }
        }

        descriptor.setContent(panel, kObject);

        return panel;
    }

    /**
     * @param kObject the object
     *
     * @return panel associated with the given object
     */
    public Widget create(KomodoObjectBean kObject) {
        if (kObject == null) {
            return create(new NoPropertiesPanel.Descriptor(), null);
        }

        switch (kObject.getType()) {
            case VDB:
                return create(new VdbPropertiesPanel.Descriptor(), kObject);
            case ACCESS_PATTERN:
                break;
            case COLUMN:
                break;
            case DATA_TYPE_RESULT_SET:
                break;
            case DDL_SCHEMA:
                break;
            case FOREIGN_KEY:
                break;
            case INDEX:
                break;
            case MODEL:
                break;
            case PARAMETER:
                break;
            case PRIMARY_KEY:
                break;
            case PUSHDOWN_FUNCTION:
                break;
            case RESULT_SET_COLUMN:
                break;
            case SCHEMA:
                break;
            case STATEMENT_OPTION:
                break;
            case STORED_PROCEDURE:
                break;
            case TABLE:
                break;
            case TABULAR_RESULT_SET:
                break;
            case TEIID:
                break;
            case TSQL_SCHEMA:
                break;
            case UNIQUE_CONSTRAINT:
                break;
            case UNKNOWN:
                break;
            case USER_DEFINED_FUNCTION:
                break;
            case VDB_CONDITION:
                break;
            case VDB_DATA_ROLE:
                break;
            case VDB_ENTRY:
                break;
            case VDB_IMPORT:
                break;
            case VDB_MASK:
                break;
            case VDB_MODEL_SOURCE:
                break;
            case VDB_PERMISSION:
                break;
            case VDB_SCHEMA:
                break;
            case VDB_TRANSLATOR:
                break;
            case VIEW:
                break;
            case VIRTUAL_PROCEDURE:
                break;
            default:
                break;
        }

        return null;
    }
}
