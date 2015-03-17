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
import org.komodo.web.client.panels.vdb.editor.diag.DiagCanvas;
import org.komodo.web.client.panels.vdb.editor.diag.DiagNode;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 *
 */
@Dependent
public class VdbEditor extends FlowPanel implements Constants {

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
        public EditorCss css();
    }

    /**
     * Editor css
     */
    interface EditorCss extends CssResource {
        String css();
    }

    private DiagCanvas canvas;
    private Integer width = 1024;
    private Integer height = 768;
    private final EditorCss css;

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

        canvas = new DiagCanvas(this, css.css());
    }

    /**
     * Set the editor content based on the given vdb
     *
     * @param vdb the komodo object representing the vdb
     */
    public void setContent(KomodoObjectBean vdb) {
        // Set the content of the editor
        DiagNode node = canvas.createNode(Random.nextInt(width), Random.nextInt(height));
        node.setImage(AppResource.INSTANCE.images().diagVdb_Image());
        node.setLabel(vdb.getName());
    }
}
