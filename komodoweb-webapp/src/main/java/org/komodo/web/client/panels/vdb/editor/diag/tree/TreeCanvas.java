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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.komodo.web.client.panels.vdb.editor.diag.DiagramCss;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.github.gwtd3.api.D3;
import com.github.gwtd3.api.arrays.Array;
import com.github.gwtd3.api.behaviour.Zoom;
import com.github.gwtd3.api.behaviour.Zoom.ZoomEventType;
import com.github.gwtd3.api.core.EnteringSelection;
import com.github.gwtd3.api.core.Selection;
import com.github.gwtd3.api.core.Transition;
import com.github.gwtd3.api.core.UpdateSelection;
import com.github.gwtd3.api.core.Value;
import com.github.gwtd3.api.functions.DatumFunction;
import com.github.gwtd3.api.layout.HierarchicalLayout.Node;
import com.github.gwtd3.api.layout.Link;
import com.github.gwtd3.api.layout.Tree;
import com.github.gwtd3.api.svg.Diagonal;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class TreeCanvas extends TreeCanvasUtilities {

    private static final int TOP_MARGIN = 100;

    private static final int DEPTH_HEIGHT = 100;

    private static final int TRANSITION_DURATION = 750;

    private static final int IMAGE_X = -16;

    private static final int IMAGE_Y = -40;

    private final Widget parent;

    private final Selection svg;

    private final Zoom zoom;

    private final Selection svgGroup;

    private final Tree layout;

    private final Diagonal pathDataGenerator;

    private final DiagramCss css;

    private final ZoomListenerCallback zoomListenerCallback;

    private final SelectionListener selectionListener;

    private final ExpandCollapseListener expandCollapseListener;

    private final ChildStatusCallback childStatusCallback;

    private final NodeIdMappingCallback nodeIdMappingCallback;

    private final LinkIdMappingCallback linkIdMappingCallback;

    private final ImageResourceCallback imageResourceCallback;

    private final StringPropertyCallback labelCallback;

    private final YNodeLocationCallback nodeYLocationCallback;

    private final GroupIdCallback groupIdCallback;

    private final NodeEnterTransformCallback nodeEnterTransformCallback;

    private final NodeUpdateLocationCallback nodeUpdateLocationCallback;

    private final CollapseCallback collapseCallback;
    
    private int parentWidth;

    private int parentHeight;

    private String rootObjectPath;

    private TreeNode root;

    private HasSelectionHandlers<KomodoObjectBean[]> selectionHandler;

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

        this.layout = D3.layout().tree().size(width, height);

        /*
         * Diagonal generator
         * Projection determines the location of the source and target points
         * of the diagonal line to be drawn.
         */
        PathProjectionCallback pathProjectionCallback = new PathProjectionCallback();
        this.pathDataGenerator = D3.svg().diagonal().projection(pathProjectionCallback);

        /*
         * Create the svg canvas by selecting the 'div' of the widget and
         * appending an 'svg' div and inside that a 'g' div
         */
        this.svg = D3.select(parent).append(SVG_ELEMENT);

        /*
         * Create the root group element of the svg
         */
        this.svgGroup = this.svg.append(GROUP_ELEMENT);

        /*
         * Create listeners for interactivity of diagram
         */
        this.selectionListener = new SelectionListener(svgGroup);
        this.expandCollapseListener = new ExpandCollapseListener();

        /*
         * Create callbacks for displaying nodes and links
         */
        this.collapseCallback = new CollapseCallback();
        this.childStatusCallback = new ChildStatusCallback();
        this.nodeIdMappingCallback = new NodeIdMappingCallback();
        this.linkIdMappingCallback = new LinkIdMappingCallback();

        this.imageResourceCallback = new ImageResourceCallback();

        this.labelCallback = new StringPropertyCallback(NAME);

        this.nodeYLocationCallback = new YNodeLocationCallback(DEPTH_HEIGHT, TOP_MARGIN);
        this.groupIdCallback = new GroupIdCallback();
        this.nodeEnterTransformCallback = new NodeEnterTransformCallback();
        this.nodeUpdateLocationCallback = new NodeUpdateLocationCallback();

        /*
         * Create zoom behavious and assign the zoom listener callback
         */
        this.zoomListenerCallback = new ZoomListenerCallback(svgGroup);
        zoom = D3.behavior().zoom()
                                          .scaleExtent(Array.fromDoubles(0.1, 3))
                                          .on(ZoomEventType.ZOOM, zoomListenerCallback);

        /*
         * Assign the selection listener to the click event of the svg
         * Call an initial zoom on the svg
         */
        this.svg.on(HTML_CLICK, selectionListener).call(zoom);

    }

    /**
     * @return the parentWidth
     */
    public int getParentWidth() {
        return this.parentWidth;
    }

    /**
     * @return the parentHeight
     */
    public int getParentHeight() {
        return this.parentHeight;
    }

    @Override
    protected DiagramCss css() {
        return this.css;
    }

    /**
     * @param selectionHandler the handler to deal with selections
     */
    public void setSelectionHandler(HasSelectionHandlers<KomodoObjectBean[]> selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    @Override
    protected void fireSelectionEvent() {
        if (selectionHandler == null)
            return;

        final List<KomodoObjectBean> selectedObjects = new ArrayList<KomodoObjectBean>();

        String selectedClass = DOT + css().selected();
        final Selection selection = svgGroup.selectAll(selectedClass);
        if (selection.empty()) {
            SelectionEvent.fire(selectionHandler, selectedObjects.toArray(new KomodoObjectBean[0]));
            return;
        }

        /*
         * The selection contains the rectangles used for displaying the selection
         *  so have to find their accompanying data.
         */
        selection.each(new DatumFunction<Void>() {

            @Override
            public Void apply(Element element, Value jsNode, final int index) {
                Value idValue = jsNode.getProperty(ID);
                String path = idValue.asString();

                KomodoRpcService service = KomodoRpcService.get();
                service.getKomodoNode(path, new IRpcServiceInvocationHandler<KomodoObjectBean>() {

                    @Override
                    public void onReturn(KomodoObjectBean kObject) {
                        selectedObjects.add(kObject);

                        if (index == selection.size() - 1)
                            SelectionEvent.fire(selectionHandler, selectedObjects.toArray(new KomodoObjectBean[0]));
                    }

                    @Override
                    public void onError(Throwable error) {
                        Window.alert("Failed to process the selected nodes: " + error.getMessage()); //$NON-NLS-1$
                        LOGGER.log(Level.SEVERE, "Node selection failure", error); //$NON-NLS-1$
                    }
                });

                return null;
            }
        });
    }

    /**
     * Set the root data object
     *
     * @param kObject the root object
     */
    public void setContent(KomodoObjectBean kObject) {
        this.rootObjectPath = kObject.getPath();
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Setting canvas content to " + kObject.getPath()); //$NON-NLS-1$

        update();
    }

    /*
     * Update all new, existing and outdated links
     */
    private void updateLinks(final TreeNode source, Array<Node> nodes) {
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
        UpdateSelection link = svgGroup.selectAll(SVG_PATH + DOT + css.link())
                                                           .data(links, linkIdMappingCallback);

        /*
         * For all links not yet drawn, build an svg path using the
         * co-ordinates of the source, ie. parent.
         */
        LinkGeneratorCallback linkEnterCallback = new LinkGeneratorCallback(pathDataGenerator,
                                                                                                                               source.getNumAttr(HTML_X0),
                                                                                                                               source.getNumAttr(HTML_Y0));
        link.enter().insert(SVG_ELEMENT + COLON + SVG_PATH, GROUP_ELEMENT)
                         .classed(css.link(), true)
                         .attr(SVG_DATA_ELEMENT, linkEnterCallback);

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
        LinkGeneratorCallback linkExitCallback = new LinkGeneratorCallback(pathDataGenerator,
                                                                                                                      source.x(),
                                                                                                                      source.y());
        link.exit().transition().duration(TRANSITION_DURATION)
                .attr(SVG_DATA_ELEMENT, linkExitCallback).remove();
    }

    /*
     * Add children indicator circles below the image 
     */
    private void addChildrenIndicator(Selection enterNodesGroup) {
        enterNodesGroup.append(SVG_CIRCLE)
                                   .attr(HTML_RADIUS, 1e-6)
                                   .style(CSS_FILL, childStatusCallback)
                                   .on(HTML_CLICK, expandCollapseListener);
    }

    private void setImage(Selection nodesGroup) {
        Selection imgSelection = nodesGroup.select(HTML_IMAGE);
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Setting image on " + imgSelection.size() + " nodes"); //$NON-NLS-1$ //$NON-NLS-2$

        imgSelection.each(imageResourceCallback);
    }

    /*
     * Append the image to each new node
     */
    private void addImage(Selection selection) {
        Selection imgSelection = selection.append(HTML_IMAGE);

        /*
         * Centre the icon above the circle
         */
        imgSelection.attr(HTML_X, IMAGE_X);
        imgSelection.attr(HTML_Y, IMAGE_Y);
        imgSelection.each(imageResourceCallback);
        setImage(selection);
    }

    private void setLabel(Selection selection) {
        Selection txtSelection = selection.select(HTML_TEXT);
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Setting labels on " + txtSelection.size() + " nodes"); //$NON-NLS-1$ //$NON-NLS-2$

        txtSelection.text(labelCallback);
    }

    /*
     * Append the label to each new node
     */
    private void addLabel(Selection enterNodesGroup) {
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
        setLabel(enterNodesGroup);
    }

    @Override
    protected void update(final TreeNode source) {
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
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Setting Y location of each node according to depth"); //$NON-NLS-1$

        nodes.forEach(nodeYLocationCallback);

        /*
         * Select all existing nodes
         */
        String nodeSelector = GROUP_ELEMENT + DOT + css.node();
        Selection nodeSelection = svgGroup.selectAll(nodeSelector);

        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Size of existing node selection: " + nodeSelection.size()); //$NON-NLS-1$

        /*
         * Map and append all new nodes from the data array
         * to the node selection
         */
        UpdateSelection updateNodeSelection = nodeSelection.data(nodes, nodeIdMappingCallback);
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Size of updating node selection: " + updateNodeSelection.size()); //$NON-NLS-1$

        /*
         * Selection of new nodes being added to layout
         */
        EnteringSelection enterNodes = updateNodeSelection.enter();
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Have new nodes? " + ! enterNodes.empty()); //$NON-NLS-1$

        if (! enterNodes.empty()) {
            /*
             * Add group element to enter nodes
             */
            Selection enterNodesGroup = enterNodes.append(GROUP_ELEMENT);

            /*
             * Adds the click handler to new nodes
             */
            enterNodesGroup.classed(css.node(), true)
                                       .attr(SVG_TRANSFORM, SVG_TRANSLATE + OPEN_BRACKET +
                                                                                 source.getNumAttr(HTML_X) + COMMA +
                                                                                 source.getNumAttr(HTML_Y) + CLOSE_BRACKET)
                                       .attr(ID, groupIdCallback)
                                       .on(HTML_CLICK, selectionListener);

            addLabel(enterNodesGroup);

            addImage(enterNodesGroup);

            addChildrenIndicator(enterNodesGroup);
        }

        /*
         * Update the label and image for each displayed node
         */
        setLabel(updateNodeSelection);
        setImage(updateNodeSelection);

        /*
         * Animate new nodes, ie. child nodes being displayed after expanding parent, 
         * using a transition. This will move the new nodes from parent nodes to their
         * final destination.
         */
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Animate transition of expanding nodes"); //$NON-NLS-1$

        Transition nodeUpdate = updateNodeSelection.transition()
                                                        .duration(TRANSITION_DURATION)
                                                        .attr(SVG_TRANSFORM, nodeEnterTransformCallback);

        /*
         * All circles currently being updated have their radius enlarged to their
         * destination visible size.
         */
        nodeUpdate.select(SVG_CIRCLE).attr(HTML_RADIUS, 4.5).style(CSS_FILL, childStatusCallback);

        /*
         * Animate the removal of nodes being removed from the diagram
         * when a parent node is contracted.
         */
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Animate transition of removal of collapsing nodes"); //$NON-NLS-1$

        NodeExitTransformCallback nodeExitTransformCallback = new NodeExitTransformCallback(source);
        Transition nodeExit = updateNodeSelection.exit().transition()
                                                    .duration(TRANSITION_DURATION)
                                                    .attr(SVG_TRANSFORM, nodeExitTransformCallback).remove();

        /*
         * Make the removed nodes circles too small to be visible
         */
        nodeExit.select(SVG_CIRCLE).attr(HTML_RADIUS, 1e-6);

        /*
         * Update the locations of all remaining nodes based on where
         * the layout has now located them
         */
        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Updating locations of all nodes"); //$NON-NLS-1$

        nodes.forEach(nodeUpdateLocationCallback);

        updateLinks(source, nodes);
    }

    /**
     * Update the tree diagram by parsing the data and passing
     * the root node into the internal update process. 
     */
    public void update() {
        if(this.rootObjectPath == null)
            return;

        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Deriving json definition from root object"); //$NON-NLS-1$

        KomodoRpcService service = KomodoRpcService.get();
        service.deriveJsonTree(this.rootObjectPath, new IRpcServiceInvocationHandler<String>() {

            @Override
            public void onReturn(String definition) {
                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.fine("Definition of root object: " + definition); //$NON-NLS-1$

                /*
                 * Parse the json of the root data. As more children
                 * are added asynchronously to the root data's
                 * descendants, new nodes will be added and displayed.
                 */
                JavaScriptObject jsRoot = JsonUtils.safeEval(definition);
                if (jsRoot == null) {
                    String msg = "Failed to safely evaluate json definition for root object"; //$NON-NLS-1$
                    Window.alert(msg);
                    LOGGER.severe(msg);
                    return;
                }

               /*
                * Casts the js object created by the parser to a tree node.
                */
                root = jsRoot.<TreeNode>cast();

                /* Set the initial location of the root node */
                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.fine("Setting location of root object"); //$NON-NLS-1$

                root.setAttr(HTML_X, (parentWidth - 20) / 3);
                root.setAttr(HTML_Y, TOP_MARGIN);

                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.fine("Collapsing root object's grandchildren"); //$NON-NLS-1$

                /* Collapse everything but root initially */
                if (root.children() != null) {
                    root.children().forEach(collapseCallback);
                }

                update(root);
            }

            @Override
            public void onError(Throwable error) {
                Window.alert("An error occurred while drawing diagram. See console for more details."); //$NON-NLS-1$
                LOGGER.log(Level.SEVERE, "Exception from deriving json tree from server", error); //$NON-NLS-1$
            }
        });
    }
}
