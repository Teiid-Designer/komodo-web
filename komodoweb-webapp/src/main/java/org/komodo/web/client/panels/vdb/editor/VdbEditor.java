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
package org.komodo.web.client.panels.vdb.editor;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import org.komodo.web.client.panels.vdb.editor.diag.DiagramCss;
import org.komodo.web.client.panels.vdb.editor.diag.tree.TreeCanvas;
import org.komodo.web.client.panels.vdb.editor.diag.tree.TreeVisitor;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 *
 */
@Dependent
public class VdbEditor extends FlowPanel implements HasSelectionHandlers<KomodoObjectBean[]>, Constants {

    /**
     * Bundle for css
     */
    public interface Bundle extends ClientBundle {

        /**
         * Instance for creating impl of this bundle
         */
        public static final Bundle INSTANCE = GWT.create(Bundle.class);

        /**
         * @return css for this editor
         */
        @Source("VdbEditor.css")
        public DiagramCss css();
    }

    private TreeCanvas canvas;
    private Integer width = 1024; // in px
    private Integer height = 2048; // in px
    private final DiagramCss css;

    /**
     * Constructor
     */
    public VdbEditor() {
        super();

        css = Bundle.INSTANCE.css();
        css.ensureInjected();
    }

    @PostConstruct
    private void init() {

        // Set the title of this panel - visible in the source of the web page
        setTitle(VDB_EDITOR);

        // Define the dimensions of this panel.
        setWidth(width + Unit.PX.getType());
        setHeight(height + Unit.PX.getType());

        canvas = new TreeCanvas(this, width, height, css);
        canvas.setSelectionHandler(this);
    }

    /**
     * Set the editor content based on the given vdb
     *
     * @param vdb the komodo object representing the vdb
     */
    public void setContent(KomodoObjectBean vdb) {
        // Set the content of the editor
        TreeVisitor visitor = new TreeVisitor(canvas);
        vdb.accept(visitor, visitor.createContext(null));
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<KomodoObjectBean[]> handler) {
        return addHandler(handler, SelectionEvent.getType());
    }
}
