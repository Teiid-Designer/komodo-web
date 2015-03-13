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
import org.komodo.web.share.Constants;
import com.github.gwtd3.api.Colors;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Transform;
import com.github.gwtd3.api.svg.Symbol;
import com.github.gwtd3.api.svg.Symbol.Type;
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

    private Selection svg;
    private Symbol symbols;
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

        // Construct the symbol generator
        symbols = D3.svg().symbol();

        // Create the svg canvas by selecting the 'div' of this panel and
        // appending an 'svg' div and inside that a 'g' div
        svg = D3.select(this)
                    .append(SVG_ELEMENT)
                    .append(GROUP_ELEMENT);

        addSymbol();
        addSymbol();
        addSymbol();
    }

    protected void addSymbol() {
        symbols.type(Type.values()[Random.nextInt(Type.values().length)]);
        symbols.size(Random.nextInt(2500) + 25);

        svg.append(SVG_PATH)
                .classed(css.css(), true)
                .attr(SVG_TRANSFORM,
                        Transform
                                .parse("")
                                .translate(Random.nextInt(width),
                                        Random.nextInt(height)).toString())
                .attr("d", symbols.generate(1.0))
                .style("fill",
                        Colors.rgb(Random.nextInt(255), Random.nextInt(255),
                                Random.nextInt(255)).toHexaString());
    }
}
