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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Properties panel for the top-level vdb node
 */
public class VdbPropertiesPanel extends VerticalPanel {

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

    private Label nameLabel = new Label("Name"); //$NON-NLS-1$

    private TextBox name = new TextBox();

    private Label versionLabel = new Label("Version"); //$NON-NLS-1$

    private TextBox version = new TextBox();

    private Label descriptionLabel = new Label("Version"); //$NON-NLS-1$

    private TextArea description = new TextArea();

    private CheckBox previewBox = new CheckBox("Preview Vdb"); //$NON-NLS-1$

    private Button applyButton = new Button("Apply"); //$NON-NLS-1$

    /**
     * Create new instance
     *
     * @param parentWidth parent width
     * @param parentHeight parent height
     */
    protected VdbPropertiesPanel(double parentWidth, double parentHeight) {

        HorizontalPanel namePanel = new HorizontalPanel();
        namePanel.add(nameLabel);
        namePanel.add(name);

        HorizontalPanel versionPanel = new HorizontalPanel();
        versionPanel.add(versionLabel);
        versionPanel.add(version);

        HorizontalPanel descriptionPanel = new HorizontalPanel();
        descriptionPanel.add(descriptionLabel);
        descriptionPanel.add(description);

        add(namePanel);
        add(versionPanel);
        add(descriptionPanel);
        add(previewBox);
        add(applyButton);
    }
    
}
