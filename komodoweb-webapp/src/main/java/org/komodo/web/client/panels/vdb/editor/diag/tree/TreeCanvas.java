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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import org.komodo.web.client.panels.vdb.editor.diag.DiagramCss;
import org.komodo.web.share.Constants;
import com.github.gwtd3.api.Coords;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.arrays.ForEachCallback;
import com.github.gwtd3.api.core.EnteringSelection;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Transition;
import com.github.gwtd3.api.core.UpdateSelection;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.functions.KeyFunction;
import com.github.gwtd3.api.layout.HierarchicalLayout.Node;
import com.github.gwtd3.api.layout.Link;
import com.github.gwtd3.api.layout.Tree;
import com.github.gwtd3.api.svg.Diagonal;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class TreeCanvas implements Constants {

    private static class JSTreeNode extends Node {
        protected JSTreeNode() {
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

    private class Click implements DatumFunction<Void> {
        @Override
        public Void apply(Element context, Value d, int index) {
            JSTreeNode node = d.<JSTreeNode> as();
            if (node.children() != null) {
                node.setAttr(JS_NO_CHILDREN, node.children());
                node.setAttr(JS_CHILDREN, null);
            } else {
                node.setAttr(JS_CHILDREN, node.getObjAttr(JS_NO_CHILDREN));
                node.setAttr(JS_NO_CHILDREN, null);
            }
            update(node);
            return null;
        }
    }

    private class Collapse implements ForEachCallback<Void> {
        @Override
        public Void forEach(Object thisArg, Value element, int index,
                Array<?> array) {
            JSTreeNode datum = element.<JSTreeNode> as();
            Array<Node> children = datum.children();
            if (children != null) {
                datum.setAttr(JS_NO_CHILDREN, children);
                datum.getObjAttr(JS_NO_CHILDREN).<Array<Node>> cast()
                        .forEach(this);
                datum.setAttr(JS_CHILDREN, null);
            }
            return null;
        }
    }

    private static final int TOP_MARGIN = 100;

    private static final int DEPTH_HEIGHT = 100;

    private static final int TRANSITION_DURATION = 750;

    private static final int IMAGE_X = -16;

    private static final int IMAGE_Y = -40;

    private Logger logger = Logger.getLogger("TreeCanvas");

    private final Widget parent;

    private final Selection svg;

    private final Tree layout;

    private final Diagonal pathDataGenerator;

    private final DiagramCss css;

    private int parentWidth;

    private int parentHeight;

    private TreeData rootData;

    private final AtomicInteger intGenerator;

    private JSTreeNode root;

    private final DatumFunction<String> childStatusCallback = new DatumFunction<String>() {
        @Override
        public String apply(Element context, Value jsNode, int index) {
            JavaScriptObject object = jsNode.<JSTreeNode> as().getObjAttr(JS_NO_CHILDREN);
            Value hasChildrenValue = jsNode.getProperty(HAS_CHILDREN);
            boolean hasChildren = hasChildrenValue.asBoolean();

            return (object != null || hasChildren) ? "lightsteelblue" : "#fff";
        }
    };

    /**
     * @param widget the widget to place the canvas on
     * @param width the initial width of the widget
     * @param height the initial height of the widget
     * @param css the css settings to be observed by the diagram
     */
    public TreeCanvas(Widget widget, int width, int height, DiagramCss css) {
        this.parent = widget;
        this.parentWidth = width;
        this.parentHeight = height;
        this.css = css;
        this.intGenerator = new AtomicInteger(Integer.MIN_VALUE);

        this.layout = D3.layout().tree().size(width, height);

        /*
         * Diagonal generator
         * Projection determines the location of the source and target points
         * of the diagonal line to be drawn.
         */
        this.pathDataGenerator = D3.svg().diagonal().projection(new DatumFunction<Array<Double>>() {
            @Override
            public Array<Double> apply(Element element, Value jsNode, int elementIndex) {
                JSTreeNode treeNode = jsNode.<JSTreeNode> as();
                return Array.fromDoubles(treeNode.x(), treeNode.y());
            }
        });

        /*
         * Create the svg canvas by selecting the 'div' of the widget and
         * appending an 'svg' div and inside that a 'g' div
         */
        this.svg = D3.select(parent)
                    .append(SVG_ELEMENT)
                    .append(GROUP_ELEMENT);
    }

    /**
     * Set the root data object
     *
     * @param rootData the root data
     */
    public void setRootData(TreeData rootData) {
        this.rootData = rootData;
    }

    /*
     * Updates all the new, updated and removed nodes
     *
     * The source is the node at the "top" of the section
     * being explanded/collapsed. Normally, this would be root
     * but when a node is clicked on to be expanded then it
     * would be the clicked node.
     */
    private void update(final JSTreeNode source) {
        if (root == null)
            return;

        /*
         * The layout converts the hierarchy of data nodes
         * presented by root into an array of tree layout nodes
         */
        Array<Node> nodes = layout.nodes(root).reverse();

        /*
         * Set the y location of the nodes based on the depth
         * of each node in the hierarchy
         */
        nodes.forEach(new ForEachCallback<Void>() {
            @Override
            public Void forEach(Object thisArg, Value jsNode, int index, Array<?> array) {
                JSTreeNode treeNode = jsNode.<JSTreeNode> as();
                double value = (treeNode.depth() * DEPTH_HEIGHT) + TOP_MARGIN;
                treeNode.setAttr(HTML_Y, value);
                return null;
            }
        });

        /*
         * Select all existing nodes
         */
        String nodeSelector = GROUP_ELEMENT + DOT + css.node();
        Selection nodeSelection = svg.selectAll(nodeSelector);

        /*
         * Map and append all new nodes from the data array
         * to the node selection
         */
        UpdateSelection updateNodeSelection = nodeSelection.data(nodes,
                new KeyFunction<Integer>() {
                    @Override
                    public Integer map(Element context, Array<?> newDataArray,
                                                    Value jsNode, int index) {
                        /*
                         * jsNode has the properties gathered from the key/values
                         * in the json data presented to the layout initially.
                         */
                        JSTreeNode treeNode = jsNode.<JSTreeNode> as();
                        Value idValue = jsNode.getProperty(ID);
                        int id = idValue.asInt();
                        return ((treeNode.id() == -1) ? treeNode.id(id) : treeNode.id());
                    }
                });

        /*
         * Selection of new nodes being added to layout
         */
        EnteringSelection enterNodes = updateNodeSelection.enter();

        /*
         * Add group element to enter nodes
         */
        Selection enterNodesGroup = enterNodes.append(GROUP_ELEMENT);

        /*
         * Adds the click handler to new nodes
         */
        enterNodesGroup.attr(CSS_CLASS, css.node())
                                   .attr(SVG_TRANSFORM, SVG_TRANSLATE + OPEN_BRACKET +
                                                                                 source.getNumAttr(HTML_X0) + COMMA +
                                                                                 source.getNumAttr(HTML_Y0) + CLOSE_BRACKET)
                                   .on(HTML_CLICK, new Click());

        /*
         * Append the label to each new node
         */
        Selection txtSelection = enterNodesGroup.append(HTML_TEXT);
        
        /*
         * text-anchor="middle" will anchor the text using the centre
         * of the label at the location specified by the x attribute
         *
         * Note: the y and dy attributes locate the text using the
         *          bottom-left of the text block
         */
        txtSelection.attr(HTML_TEXT_ANCHOR, MIDDLE);
        txtSelection.style(CSS_FILL_OPACITY, 1);
        txtSelection.attr(HTML_DY, IMAGE_Y - 5);

        txtSelection.text(new DatumFunction<String>() {
            @Override
            public String apply(Element context, Value jsNode, int index) {
                Value labelValue = jsNode.getProperty(NAME);
                String label = labelValue.asString();
                return label;

            }
        });

        /*
         * Append the image to each new node
         */
        Selection imgSelection = enterNodesGroup.append(HTML_IMAGE);

        /*
         * Centre the icon above the circle
         */
        imgSelection.attr(HTML_X, IMAGE_X);
        imgSelection.attr(HTML_Y, IMAGE_Y);

        imgSelection.attr(HTML_XLINK_REF, new DatumFunction<String>() {
            @Override
            public String apply(Element context, Value jsNode, int index) {
                Value iconValue = jsNode.getProperty(ICON);
                String imgHref = iconValue.asString();
                return imgHref;
            }
        });
        imgSelection.attr(HTML_WIDTH, new DatumFunction<String>() {
            @Override
            public String apply(Element context, Value jsNode, int index) {
                Value iconValue = jsNode.getProperty(ICON_WIDTH);
                String imgWidth = iconValue.asString();
                return imgWidth;
            }
        });
        imgSelection.attr(HTML_HEIGHT, new DatumFunction<String>() {
            @Override
            public String apply(Element context, Value jsNode, int index) {
                Value iconValue = jsNode.getProperty(ICON_HEIGHT);
                String imgHeight = iconValue.asString();
                return imgHeight;
            }
        });

        /*
         * Add children indicator circles below the image 
         */
        enterNodesGroup.append(SVG_CIRCLE)
                                   .attr(HTML_RADIUS, 1e-6)
                                   .style(CSS_FILL, childStatusCallback);

        /*
         * Animate new nodes, ie. child nodes being displayed after expanding parent, 
         * using a transition. This will move the new nodes from parent nodes to their
         * final destination.
         */
        Transition nodeUpdate = updateNodeSelection.transition()
                                                        .duration(TRANSITION_DURATION)
                                                        .attr(SVG_TRANSFORM, new DatumFunction<String>() {
                                                            @Override
                                                            public String apply(Element context, Value jsNode, int index) {
                                                                JSTreeNode treeNode = jsNode.<JSTreeNode> as();
                                                                return SVG_TRANSLATE + OPEN_BRACKET +
                                                                            treeNode.x() + COMMA +
                                                                            treeNode.y() + CLOSE_BRACKET;
                                                            }
                                                        });

        /*
         * All circles currently being updated have their radius enlarged to their
         * destination visible size.
         */
        nodeUpdate.select(SVG_CIRCLE).attr(HTML_RADIUS, 4.5).style(CSS_FILL, childStatusCallback);

        /*
         * Animate the removal of nodes being removed from the diagram
         * when a parent node is contracted.
         */
        Transition nodeExit = updateNodeSelection.exit().transition()
                                                    .duration(TRANSITION_DURATION)
                                                    .attr(SVG_TRANSFORM, new DatumFunction<String>() {
                                                        @Override
                                                        public String apply(Element context, Value d, int index) {
                                                            return SVG_TRANSLATE + OPEN_BRACKET +
                                                                        source.x() + COMMA +
                                                                        source.y() + CLOSE_BRACKET;
                                                        }
                                                    }).remove();

        /*
         * Make the removed nodes circles too small to be visible
         */
        nodeExit.select(SVG_CIRCLE).attr(HTML_RADIUS, 1e-6);

        /*
         * Update the locations of all remaining nodes based on where
         * the layout has now located them
         */
        nodes.forEach(new ForEachCallback<Void>() {
            @Override
            public Void forEach(Object thisArg, Value jsNode, int index, Array<?> array) {
                JSTreeNode treeNode = jsNode.<JSTreeNode> as();
                treeNode.setAttr(HTML_X0, treeNode.x());
                treeNode.setAttr(HTML_Y0, treeNode.y());
                return null;
            }
        });

        /*
         * Determine the array of links given the
         * nodes provided to the layout
         */
        Array<Link> links = layout.links(nodes);

        /*
         * Update link paths for all new node locations
         * Select all the svg links on the page that have a valid target
         * in the links data. Links with an invalid target are those
         * where the child has been collapsed away.
         */
        UpdateSelection link = svg.selectAll(SVG_PATH + DOT + css.link())
                                                 .data(links, new KeyFunction<Integer>() {
                                                     @Override
                                                     public Integer map(Element context, Array<?> newDataArray,
                                                                        Value jsNode, int index) {
                                                         return jsNode.<Link> as().target().<JSTreeNode> cast().id();
                                                     }
                                                 });

        /*
         * For all links not yet drawn, build an svg path using the
         * co-ordinates of the source, ie. parent.
         */
        link.enter().insert(SVG_ELEMENT + COLON + SVG_PATH, GROUP_ELEMENT)
                         .attr(CSS_CLASS, css.link())
                         .attr(SVG_DATA_ELEMENT, new DatumFunction<String>() {
                                                                          @Override
                                                                          public String apply(Element context, Value jsNode, int index) {
                                                                              Coords o = Coords.create(source.getNumAttr(HTML_X0),
                                                                                                                      source.getNumAttr(HTML_Y0));
                                                                              /*
                                                                               * Use the path data generator to create a link that at
                                                                               * this point goes from source to source
                                                                               */
                                                                              return pathDataGenerator.generate(Link.create(o, o));
                                                                          }
                                                                        });

        /*
         * Starts an animtion of the link's svg data element using the path generator
         */
        link.transition().duration(TRANSITION_DURATION)
                                .attr(SVG_DATA_ELEMENT, pathDataGenerator);

        /*
         * Removal of invalid links where their targets have been collapsed away
         * Animate their removal by regenerating the link back to pointing their
         * source and target to the source, ie. parent.
         */
        link.exit().transition().duration(TRANSITION_DURATION)
                .attr(SVG_DATA_ELEMENT, new DatumFunction<String>() {
                                                                    @Override
                                                                    public String apply(Element context, Value jsNode, int index) {
                                                                        Coords o = Coords.create(source.x(), source.y());
                                                                        return pathDataGenerator.generate(Link.create(o, o));
                                                                    }
                                                                }).remove();
    }

    /**
     * Update the tree diagram by parsing the data and passing
     * the root node into the internal update process. 
     */
    public void update() {
        if(this.rootData == null)
            return;

        String definition = this.rootData.toDefinition();
        logger.severe("Definition: " + definition);

        /*
         * Parse the json of the root data. As more children
         * are added asynchronously to the root data's
         * descendants, new nodes will be added and displayed.
         */
        JSONValue jsonValue = JSONParser.parseLenient(definition);
        if (jsonValue == null)
            return;

        JSONObject jsonObject = jsonValue.isObject();
        if (jsonObject == null)
            return;

       /*
        * Casts the js object created by the parser to a tree node.
        */
        JavaScriptObject jsRoot = jsonObject.getJavaScriptObject();
        root = jsRoot.<JSTreeNode>cast();

        /* Set the initial location of the root node */
        root.setAttr(HTML_X0, (parentWidth - 20) / 2);
        root.setAttr(HTML_Y0, TOP_MARGIN);

//        if (root.children() != null) {
//            root.children().forEach(new Collapse());
//        }

        update(root);
    }

    /**
     * @return a unique id
     */
    int createId() {
        return intGenerator.incrementAndGet();
    }
}
