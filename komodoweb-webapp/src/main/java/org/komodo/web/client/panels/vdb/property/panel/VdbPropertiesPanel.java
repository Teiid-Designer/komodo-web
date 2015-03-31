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
package org.komodo.web.client.panels.vdb.property.panel;

import org.komodo.web.client.panels.vdb.property.PropertiesPanelDescriptor;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Properties panel for the top-level vdb node
 */
@UiTemplate("./VdbPropertiesPanel.ui.xml")
public class VdbPropertiesPanel extends Composite {

    /**
     * Descriptor for this panel
     */
    public static class Descriptor extends PropertiesPanelDescriptor<VdbPropertiesPanel> {

        /**
         * Create new instance
         */
        public Descriptor() {
            super(VdbPropertiesPanel.class.getName());
        }

        @Override
        public VdbPropertiesPanel create(double parentWidth, double parentHeight) {
            return new VdbPropertiesPanel(parentWidth, parentHeight);
        }

        @Override
        public void setContent(VdbPropertiesPanel panel, KomodoObjectBean kObject) {
            //TODO
        }
    }

    interface VdbPropertiesPanelUiBinder extends UiBinder<Widget, VdbPropertiesPanel> {
        // Nothing required
    }

    private static VdbPropertiesPanelUiBinder uiBinder = GWT.create(VdbPropertiesPanelUiBinder.class);

    @UiField
    TextBox nameBox;

    @UiField
    TextBox versionBox;

    @UiField
    TextArea descriptionArea;

    @UiField
    CheckBox previewBox;

    @UiField
    Button applyButton;

    KomodoObjectBean kObject;

    /**
     * Create new instance
     *
     * @param parentWidth parent width
     * @param parentHeight parent height
     */
    protected VdbPropertiesPanel(double parentWidth, double parentHeight) {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
}
