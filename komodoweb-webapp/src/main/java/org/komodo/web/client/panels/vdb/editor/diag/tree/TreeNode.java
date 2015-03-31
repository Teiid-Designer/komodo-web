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
package org.komodo.web.client.panels.vdb.editor.diag.tree;

import com.github.gwtd3.api.layout.HierarchicalLayout.Node;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * Implementation of tree node used in the {@link TreeCanvas}
 */
public class TreeNode extends Node {

    protected TreeNode() {
        super();
    }

    protected final native int id() /*-{
        return this.id || -1;
    }-*/;

    protected final native int id(int id) /*-{
        return this.id = id;
    }-*/;

    protected final native void setAttr(String name, JavaScriptObject value) /*-{
        this[name] = value;
    }-*/;

    protected final native double setAttr(String name, double value) /*-{
        return this[name] = value;
    }-*/;

    protected final native JavaScriptObject getObjAttr(String name) /*-{
        return this[name];
    }-*/;

    protected final native double getNumAttr(String name) /*-{
        return this[name];
    }-*/;
}
