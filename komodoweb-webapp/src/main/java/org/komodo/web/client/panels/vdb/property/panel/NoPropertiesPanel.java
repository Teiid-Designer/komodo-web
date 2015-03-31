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
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Properties panel for when no object has been selected
 */
public class NoPropertiesPanel extends SimplePanel {

    /**
     * Descriptor for this panel
     */
    public static class Descriptor extends PropertiesPanelDescriptor<NoPropertiesPanel> {

        /**
         * Create new instance
         */
        public Descriptor() {
            super(NoPropertiesPanel.class.getName());
        }

        @Override
        public NoPropertiesPanel create(double parentWidth, double parentHeight) {
            return new NoPropertiesPanel(parentWidth, parentHeight);
        }

        @Override
        public void setContent(NoPropertiesPanel panel, KomodoObjectBean kObject) {
            // Nothing to do
        }
    }

    private Label noPropsLabel = new Label("<No single object selected>"); //$NON-NLS-1$

    /**
     * Create new instance
     *
     * @param parentWidth parent width
     * @param parentHeight parent height
     */
    protected NoPropertiesPanel(double parentWidth, double parentHeight) {
        Style style = getElement().getStyle();
        style.setFontWeight(FontWeight.BOLD);
        style.setTextAlign(TextAlign.CENTER);
        style.setLineHeight(parentHeight, Unit.EM);

        add(noPropsLabel);
    }
}
