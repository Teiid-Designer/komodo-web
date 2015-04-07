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
package org.komodo.web.client.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import org.modeshape.jcr.JcrLexicon;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Table for custom properties
 */
@UiTemplate("./CustomPropertiesTable.ui.xml")
public class CustomPropertiesTable extends Composite
    implements HasValueChangeHandlers<KomodoObjectPropertyBean>, Constants {

    private static final Logger LOGGER = Logger.getLogger(CustomPropertiesTable.class.getName());

    private static final String REMOVE_BUTTON_PREFIX = "Remove Row "; //$NON-NLS-1$

    private static final ImageResource ICON_ADD = AppResource.INSTANCE.images().addIconImage();

    private static final ImageResource ICON_REMOVE = AppResource.INSTANCE.images().removeIconImage();

    interface BinderStyle extends CssResource {
        String propsTableCell();

        String propertyButton();
    }

    interface CustomPropertiesTableUiBinder extends UiBinder<Widget, CustomPropertiesTable> {
        // Nothing required
    }

    private static final CustomPropertiesTableUiBinder uiBinder = GWT.create(CustomPropertiesTableUiBinder.class);

    @UiField
    BinderStyle style;

    @UiField
    FlexTable propsTable;

    @UiField
    TextBox newPropName;

    @UiField
    TextBox newPropValue;

    @UiField
    Image addPropertyButton;

    private String kObjectPath;

    /**
     * New instance
     */
    public CustomPropertiesTable() {
        initWidget(uiBinder.createAndBindUi(this));
        init();

        addPropertyButton.setResource(ICON_ADD);
    }

    private void init() {
        propsTable.removeAllRows();
        propsTable.setText(0, 0, "Name"); //$NON-NLS-1$
        propsTable.setText(0, 1, "Value"); //$NON-NLS-1$
        propsTable.setText(0, 2, "Remove"); //$NON-NLS-1$
    }

    private String getValueAsString(KomodoObjectPropertyBean property) {
        if (property == null)
            return EMPTY_STRING;

        Object value = property.getValue();
        if (value == null)
            return EMPTY_STRING;

        return value.toString();
    }

    private Label createLabel(String labelText) {
        Label label = new Label(labelText);
        label.addStyleName(style.propsTableCell());
        return label;
    }

    private Widget createRemoveButton(int row) {
        Image btn = new Image();
        btn.setResource(ICON_REMOVE);
        btn.setTitle(REMOVE_BUTTON_PREFIX + row);
        btn.addStyleName(style.propertyButton());
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Image btn = (Image) event.getSource();
                String title = btn.getTitle();
                String indexStr = title.substring(REMOVE_BUTTON_PREFIX.length());
                int index = Integer.parseInt(indexStr);

                if (index == -1)
                    return;

                removeTableRow(index);
            }
        });

        return btn;
    }

    /**
     * Add the property as a row in tha table
     *
     * @param property the property
     */
    public void addProperty(KomodoObjectPropertyBean property) {
        int numRows = propsTable.getRowCount();

        // Remove the row in the misc table
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Adding row " + numRows + " to table"); //$NON-NLS-1$ //$NON-NLS-2$

        propsTable.setWidget(numRows, 0, createLabel(property.getName()));
        propsTable.setWidget(numRows, 1, createLabel(getValueAsString(property)));
        propsTable.setWidget(numRows, 2, createRemoveButton(numRows));

        newPropName.setText(EMPTY_STRING);
        newPropValue.setText(EMPTY_STRING);
    }

    private boolean contains(String propertyName) {
        for (int i = 1; i < propsTable.getRowCount(); ++i) {
            Label propLabel = (Label) propsTable.getWidget(i, 0);
            if (propLabel.getText().equalsIgnoreCase(propertyName))
                return true;
        }

        return false;
    }

    @UiHandler("addPropertyButton")
    protected void onAddRowButtonClicked(ClickEvent event) {
        /*
         * Add a new property
         */
        final HasValueChangeHandlers<KomodoObjectPropertyBean> source = this;
        final String propertyName = newPropName.getText();
        if (propertyName.isEmpty())
            return;

        final String propertyValue = newPropValue.getText();
        if (propertyValue.isEmpty())
            return;

        // Avoid adding duplicate properties
        if (contains(newPropName.getText()))
            return;

        KomodoRpcService.get().addProperty(kObjectPath, propertyName, propertyValue, new IRpcServiceInvocationHandler<KomodoObjectBean>() {
            @Override
            public void onReturn(final KomodoObjectBean result) {
                // Update the source komodo object to that from the server
                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.fine("Adding property to komodo object: " + result.getPath()); //$NON-NLS-1$

                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.fine("Firing value change event for added property"); //$NON-NLS-1$

                ValueChangeEvent.fireIfNotEqual(source, null, result.getProperty(propertyName));

                KomodoObjectPropertyBean property = result.getProperty(propertyName);
                addProperty(property);
            }

            @Override
            public void onError(Throwable error) {
                String msg = "Failed to add the property" + propertyName + ": " + error.getMessage();  //$NON-NLS-1$//$NON-NLS-2$
                Window.alert(msg);
                LOGGER.log(Level.SEVERE, msg, error);
            }
        });
    }

    private void removeTableRow(final int row) {
        Label nameLabel = (Label) propsTable.getWidget(row, 0);
        final String propertyName = nameLabel.getText();

        /*
         * Delete the property
         */
        final HasValueChangeHandlers<KomodoObjectPropertyBean> source = this;
        KObjectOperation operation = new KObjectOperation() {

            @Override
            public void execute(KomodoObjectBean kObject) {

                final KomodoObjectPropertyBean property = kObject.getProperty(propertyName);
                if (property == null)
                    return;

                KomodoRpcService.get().removeProperty(property, new IRpcServiceInvocationHandler<KomodoObjectBean>() {
                    @Override
                    public void onReturn(final KomodoObjectBean result) {
                        // Update the source komodo object to that from the server
                        if (LOGGER.isLoggable(Level.FINE))
                            LOGGER.fine("Removed property from komodo object: " + result.getPath()); //$NON-NLS-1$s

                        if (LOGGER.isLoggable(Level.FINE))
                            LOGGER.fine("Firing value change event for removed property"); //$NON-NLS-1$

                        ValueChangeEvent.fireIfNotEqual(source, property, null);

                        // Remove the row in the misc table
                        if (LOGGER.isLoggable(Level.FINE))
                            LOGGER.fine("Removing row " + row + " from table"); //$NON-NLS-1$ //$NON-NLS-2$

                        propsTable.removeRow(row);
                    }

                    @Override
                    public void onError(Throwable error) {
                        String msg = "Failed to remove the property" + property.getName() + ": " + error.getMessage(); //$NON-NLS-1$//$NON-NLS-2$
                        Window.alert(msg);
                        LOGGER.log(Level.SEVERE, msg, error);
                    }
                });
            }
        };

        KObjectExecutor executor = new KObjectExecutor();
        executor.executeOperation(kObjectPath, operation);
    }

    protected List<KomodoObjectPropertyBean> getCustomProperties(KomodoObjectBean kObject, String... propertiesToIgnore) {
        List<String> propsToIgnore = Arrays.asList(propertiesToIgnore);

        List<KomodoObjectPropertyBean> properties = new ArrayList<KomodoObjectPropertyBean>();

        for (KomodoObjectPropertyBean property : kObject.getProperties()) {
            String name = property.getName();
            if (propsToIgnore.contains(name))
                continue;

            // Ignore jcr properties since these are internal to modeshape
            if (name.startsWith(JcrLexicon.Namespace.PREFIX))
                continue;

            //
            // Ignore modeshape vdb properties as <property> type properties will
            // not have a vdb prefix but simply be the property name on its own, eg.
            // UseConnectedMetadata or vdb-property1.
            //
            if (name.startsWith(VdbLexicon.Namespace.PREFIX + COLON))
                continue;

            properties.add(property);
        }

        return properties;
    }

    protected void update(KomodoObjectBean kObject) {
        // Reset the table to show only titles
        init();

        if (kObject == null)
            return;

        List<KomodoObjectPropertyBean> customProperties = getCustomProperties(kObject, VdbLexicon.Translator.TYPE, VdbLexicon.Translator.DESCRIPTION);
        for (int i = 0; i < customProperties.size(); ++i) {
            KomodoObjectPropertyBean customProperty = customProperties.get(i);
            addProperty(customProperty);
        }
    }

    /**
     * @param kObject the selected object
     */
    public void setKomodoObject(KomodoObjectBean kObject) {
        this.kObjectPath = kObject.getPath();
        update(kObject);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<KomodoObjectPropertyBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }
}
