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
package org.komodo.web.client.panels.vdb.editor.diag;

import org.komodo.web.share.Constants;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.core.Selection;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class DiagCanvas implements Constants {

    private final Selection svg;
    private final String css;

    /**
     * @param widget the widget to place the canvas on
     * @param css the css settings to be observed by the diagram
     */
    public DiagCanvas(Widget widget, String css) {
        // Create the svg canvas by selecting the 'div' of the widget and
        // appending an 'svg' div and inside that a 'g' div
        this.svg = D3.select(widget)
                    .append(SVG_ELEMENT)
                    .append(GROUP_ELEMENT);

        this.css = css;
    }

    /**
     * @return the svg
     */
    Selection svg() {
        return svg;
    }

    /**
     * @return the css
     */
    public String css() {
        return this.css;
    }

    /**
     * @param x the x co-ordinate
     * @param y the y co-ordinate
     * @return the new node
     */
    public DiagNode createNode(int x, int y) {
        DiagNode node = new DiagNode(this, x, y);
        return node;
    }
}
