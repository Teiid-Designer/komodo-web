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
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Transform;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 */
public class DiagNode implements Constants {

    private final Selection node;

    private ImageResource image;

    private String label;

    /**
     * @param canvas the canvas
     * @param x the x co-ordinate
     * @param y the y co-ordinate
     */
    public DiagNode(DiagCanvas canvas, int x, int y) {
        node = canvas.svg().append(GROUP_ELEMENT)
                                       .classed(canvas.css(), true)
                                       .attr(CSS_CLASS, NODE)
                                       .attr(SVG_TRANSFORM,
                                             Transform.parse(EMPTY_STRING).translate(x ,y).toString());
    }

    private void update() {
        if (image != null) {
            Selection imgSelection = node.select(HTML_IMAGE);
            if (imgSelection.empty())
                imgSelection = node.append(HTML_IMAGE);

            imgSelection.attr(HTML_XLINK_REF, image.getSafeUri().asString())
                               .attr(HTML_X, 0 + Unit.PX.getType())
                               .attr(HTML_Y, 0 + Unit.PX.getType())
                               .attr(HTML_WIDTH, image.getWidth() + Unit.PX.getType())
                               .attr(HTML_HEIGHT, image.getHeight() + Unit.PX.getType());
        }

        if (label != null) {
            Selection txtSelection = node.select(HTML_TEXT);
            if (txtSelection.empty())
                txtSelection = node.append(HTML_TEXT);

            /*
             * text-anchor="middle" will anchor the text using the centre
             * of the label at the location specified by the x attribute
             *
             * Note: the y and dy attributes locate the text using the
             *          bottom-left of the text block
             */
            double imageWidth = image != null ? image.getWidth() : 32;
            double imageHeight = image != null ? image.getHeight() : 32;

            txtSelection.attr(HTML_DY, imageHeight + 20)
                         .attr(HTML_X, imageWidth / 2)
                         .attr(HTML_TEXT_ANCHOR, MIDDLE)
                         .text(label)
                         .style(CSS_FILL_OPACITY, 1);
        }
    }

    /**
     * @param image the image resource to set
     */
    public void setImage(ImageResource image) {
        if (this.image == image)
            return;

        this.image = image;
        update();
    }

    /**
     * @param label the label to display adjacent to the node
     */
    public void setLabel(String label) {
        if (this.label != null && this.label.equals(label))
            return;

        this.label = label;
        update();
    }

}
