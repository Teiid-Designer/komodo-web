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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.komodo.web.client.panels.vdb.property.PropertiesPanelDescriptor;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
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
public class VdbPropertiesPanel extends Composite
                                                     implements HasValueChangeHandlers<KomodoObjectPropertyBean>,
                                                     Constants {

    private static final Logger LOGGER = Logger.getLogger(VdbPropertiesPanel.class.getName());

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
            panel.setKomodoObject(kObject);
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

    /**
     * @param kObject the selected object
     */
    public void setKomodoObject(KomodoObjectBean kObject) {
        this.kObject = kObject;
        this.update();
    }

    private KomodoObjectPropertyBean previewProperty() {
        KomodoObjectPropertyBean previewProperty = kObject.getProperty(VdbLexicon.Vdb.PREVIEW);
        return previewProperty;
    }

    private KomodoObjectPropertyBean descriptionProperty() {
        KomodoObjectPropertyBean descProperty = kObject.getProperty(VdbLexicon.Vdb.DESCRIPTION);
        return descProperty;
    }

    private KomodoObjectPropertyBean versionProperty() {
        KomodoObjectPropertyBean versionProperty = kObject.getProperty(VdbLexicon.Vdb.VERSION);
        return versionProperty;
    }

    private KomodoObjectPropertyBean nameProperty() {
        KomodoObjectPropertyBean nameProperty = kObject.getProperty(VdbLexicon.Vdb.NAME);
        return nameProperty;
    }

    private void update() {
        KomodoObjectPropertyBean nameProperty = nameProperty();
        Object value = nameProperty.getValue();
        nameBox.setText(value != null ? value.toString() : EMPTY_STRING);

        KomodoObjectPropertyBean versionProperty = versionProperty();
        value = versionProperty.getValue();
        versionBox.setText(value != null ? value.toString() : EMPTY_STRING);

        KomodoObjectPropertyBean descProperty = descriptionProperty();
        value = descProperty.getValue();
        descriptionArea.setText(value != null ? value.toString() : EMPTY_STRING);

        KomodoObjectPropertyBean previewProperty = previewProperty();
        value = previewProperty.getValue();
        previewBox.setValue(value instanceof Boolean ? (Boolean) value : false);
    }

    private void changeProperty(final KomodoObjectPropertyBean property, Object value) {
        if (property.getValue() == null && value == null)
            return;

        if (property.getValue() != null && property.getValue().equals(value))
            return;

        if (value != null && value.equals(property.getValue()))
            return;

        //
        // TODO
        // When updating the name (which is part of the path) need to consider
        // ramifications if the node being update is the root of the canvas
        //
        final HasValueChangeHandlers<KomodoObjectPropertyBean> source = VdbPropertiesPanel.this;
        final KomodoObjectPropertyBean oldProperty = property.copy();
        property.setValue(value);

        KomodoRpcService.get().updateProperty(property, new IRpcServiceInvocationHandler<KomodoObjectPropertyBean>() {
            @Override
            public void onReturn(final KomodoObjectPropertyBean result) {
                ValueChangeEvent.fireIfNotEqual(source, oldProperty, property);
            }

            @Override
            public void onError(Throwable error) {
                String msg = "Failed to update the property" + property.getName() + ": " + error.getMessage();  //$NON-NLS-1$//$NON-NLS-2$
                Window.alert(msg);
                LOGGER.log(Level.SEVERE, msg, error);
            }
        });
    }

    @UiHandler("nameBox")
    protected void onNameBoxBlur(BlurEvent event) {
        if (kObject == null)
          return;

      KomodoObjectPropertyBean nameProperty = nameProperty();
      changeProperty(nameProperty, nameBox.getText());
    }

    @UiHandler("versionBox")
    protected void onVersionBoxBlur(BlurEvent event) {
        if (kObject == null)
          return;

      KomodoObjectPropertyBean versionProperty = versionProperty();
      changeProperty(versionProperty, versionBox.getText());
    }

    @UiHandler("descriptionArea")
    protected void onDescriptionAreaBlur(BlurEvent event) {
        if (kObject == null)
          return;

      KomodoObjectPropertyBean descProperty = descriptionProperty();
      changeProperty(descProperty, descriptionArea.getText());
    }

    @UiHandler("previewBox")
    protected void onPreviewBoxBlur(BlurEvent event) {
        if (kObject == null)
          return;

      KomodoObjectPropertyBean previewProperty = previewProperty();
      changeProperty(previewProperty, previewBox.getValue());
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<KomodoObjectPropertyBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
