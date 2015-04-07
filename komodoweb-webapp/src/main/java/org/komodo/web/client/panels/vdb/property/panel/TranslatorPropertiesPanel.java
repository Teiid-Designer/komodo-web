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
import org.komodo.web.client.widgets.CustomPropertiesTable;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Properties panel for the top-level vdb node
 */
@UiTemplate("./TranslatorPropertiesPanel.ui.xml")
public class TranslatorPropertiesPanel extends AbstractPropertiesPanel {

    /**
     * Descriptor for this panel
     */
    public static class Descriptor extends PropertiesPanelDescriptor<TranslatorPropertiesPanel> {

        /**
         * Create new instance
         */
        public Descriptor() {
            super(TranslatorPropertiesPanel.class.getName());
        }

        @Override
        public TranslatorPropertiesPanel create(double parentWidth, double parentHeight) {
            return new TranslatorPropertiesPanel(parentWidth, parentHeight);
        }

        @Override
        public void setContent(TranslatorPropertiesPanel panel, KomodoObjectBean kObject) {
            LOGGER.severe("Setting content for translator panel to: " + kObject);
            panel.setKomodoObject(kObject);
        }
    }

    interface TranslatorPropertiesPanelUiBinder extends UiBinder<Widget, TranslatorPropertiesPanel> {
        // Nothing required
    }

    private static final TranslatorPropertiesPanelUiBinder uiBinder = GWT.create(TranslatorPropertiesPanelUiBinder.class);

    @UiField
    TextBox typeBox;

    @UiField
    TextArea descriptionArea;

    @UiField
    CustomPropertiesTable customPropsTable;

    /**
     * Create new instance
     *
     * @param parentWidth parent width
     * @param parentHeight parent height
     */
    protected TranslatorPropertiesPanel(double parentWidth, double parentHeight) {
        initWidget(uiBinder.createAndBindUi(this));
    }

    private String typeProperty() {
        return VdbLexicon.Translator.TYPE;
    }

    private String descriptionProperty() {
        return VdbLexicon.Translator.DESCRIPTION;
    }

    @Override
    protected void update(KomodoObjectBean kObject) {
        if (kObject == null)
            return;

        String propertyName = typeProperty();
        typeBox.setText(getValueAsString(kObject.getProperty(propertyName)));

        propertyName = descriptionProperty();
        descriptionArea.setText(getValueAsString(kObject.getProperty(propertyName)));
    }

    @Override
    public void setKomodoObject(KomodoObjectBean kObject) {
        LOGGER.severe("Setting translator panel object and calling update");
        super.setKomodoObject(kObject);
        LOGGER.severe("Setting props table object");
        customPropsTable.setKomodoObject(kObject);
        LOGGER.severe("Completed setting object");
    }

    @UiHandler("typeBox")
    protected void onTypeBoxBlur(BlurEvent event) {
        if (kObjectPath == null)
          return;

      String typeProperty = typeProperty();
      updateProperty(typeProperty, typeBox.getText());
    }

    @UiHandler("descriptionArea")
    protected void onDescriptionAreaBlur(BlurEvent event) {
        if (kObjectPath == null)
          return;

      String descProperty = descriptionProperty();
      updateProperty(descProperty, descriptionArea.getText());
    }
}
