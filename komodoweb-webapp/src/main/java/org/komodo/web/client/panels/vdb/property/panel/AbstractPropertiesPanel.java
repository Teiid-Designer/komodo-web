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
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.client.widgets.KObjectExecutor;
import org.komodo.web.client.widgets.KObjectOperation;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;

/**
 *
 */
public abstract class AbstractPropertiesPanel extends Composite 
    implements HasValueChangeHandlers<KomodoObjectPropertyBean>, Constants {

    protected static final Logger LOGGER = Logger.getLogger(AbstractPropertiesPanel.class.getName());

    /**
     * Make icons available as SafeHtml
     * @param resource
     * @return
     */
    protected static SafeHtml makeImage(ImageResource resource) {
        AbstractImagePrototype proto = AbstractImagePrototype.create(resource);

        // String html = proto.getHTML().replace("style='",
        // "style='left:0px;top:0px;"); // position:absolute;
        //
        // return SafeHtmlUtils.fromTrustedString(html);

        return proto.getSafeHtml();
    }

    protected String kObjectPath;

    /**
     * Update to refresh the widget
     *
     * @param kObject the object to perform the update from
     */
    protected abstract void update(KomodoObjectBean kObject);

    protected String getValueAsString(KomodoObjectPropertyBean property) {
        if (property == null)
            return EMPTY_STRING;

        Object value = property.getValue();
        if (value == null)
            return EMPTY_STRING;

        return value.toString();
    }

    protected Boolean getValueAsBoolean(KomodoObjectPropertyBean property) {
        if (property == null)
            return false;

        Object value = property.getValue();
        if (value instanceof Boolean)
            return (Boolean) value;

        return false;
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

    protected void updateProperty(final String propertyName, final Object newValue) {
        //
        // TODO
        // When updating the name (which is part of the path) need to consider
        // ramifications if the node being update is the root of the canvas
        //
        KObjectOperation operation = new KObjectOperation() {

            @Override
            public void execute(KomodoObjectBean kObject) {
                final HasValueChangeHandlers<KomodoObjectPropertyBean> source = AbstractPropertiesPanel.this;
                final KomodoObjectPropertyBean property = kObject.getProperty(propertyName);
                final KomodoObjectPropertyBean oldProperty = property.copy();

                KomodoRpcService.get().updateProperty(property, newValue, new IRpcServiceInvocationHandler<KomodoObjectPropertyBean>() {
                    @Override
                    public void onReturn(final KomodoObjectPropertyBean result) {
                        property.setValue(result.getValue());
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
        };

        KObjectExecutor executor = new KObjectExecutor();
        executor.executeOperation(kObjectPath, operation);
    }
}
