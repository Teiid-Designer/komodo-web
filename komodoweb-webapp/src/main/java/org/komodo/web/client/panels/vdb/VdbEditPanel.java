/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.komodo.web.client.panels.vdb;

import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.client.panels.vdb.editor.VdbEditor;
import org.komodo.web.client.panels.vdb.property.PropertiesPanelFactory;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Vdb Editing Panel
 */
@Dependent
@Templated("VdbEditPanel.html")
public class VdbEditPanel extends Composite implements SelectionHandler<KomodoObjectBean[]>, Constants {

    private static final Logger LOGGER = Logger.getLogger(VdbEditPanel.class.getName());

    private static final Integer DIAGRAM_PANEL_WIDTH = 50; // in em

    private static final Integer EDIT_PANEL_HEIGHT = 40; // in em

    private static final Integer PROPERTY_TITLE_HEIGHT = 2; // in em

    private static final Double BORDER_WIDTH = 0.1; // in em

    @Inject @DataField("vdb-edit-panel")
    private FlowPanel basePanel;

    @Inject
    private VdbEditor editor;

    @Inject
    private FlowPanel objectPropertiesPanel;

    @Inject
    private PropertiesPanelFactory panelFactory;

    private Widget selectionSubPanel;

    private ScrollPanel createDiagramPanel() {
        ScrollPanel scroller = new ScrollPanel(editor);
        editor.addSelectionHandler(this);

        scroller.setTitle(VDB_EDIT_DIAG_SCROLLPANEL);
        scroller.setWidth(DIAGRAM_PANEL_WIDTH + Unit.EM.getType());
        scroller.setHeight(EDIT_PANEL_HEIGHT + Unit.EM.getType());

        Style style = scroller.getElement().getStyle();
        style.setBorderWidth(BORDER_WIDTH, Unit.EM);
        style.setBorderStyle(BorderStyle.SOLID);

        return scroller;
    }

    private Widget createObjectPropertiesPanel() {
        Style objPropPanelStyle = objectPropertiesPanel.getElement().getStyle();

        /*
         * Want to position the properties panel so its always to the right
         * of the diagram panel, even when zoomed in. Float: left fails to
         * work at this point since zoom in enough and the properties
         * panel jumps down below.
         */

        /*
         * Set the position of the properties panel to absolute so that we
         * are now in charge of its location
         */
        objPropPanelStyle.setPosition(Position.ABSOLUTE);

        /*
         * Set its position as being on same level as diagram panel
         */
        objPropPanelStyle.setTop(0, Unit.EM);

        /*
         * Move it to the right of the diagram panel which is the
         * diagram panel width + border width + an extra 2 units
         * to account for the vertical scrollbar width
         */
        objPropPanelStyle.setLeft(DIAGRAM_PANEL_WIDTH + BORDER_WIDTH + 2, Unit.EM);

        /*
         * Set its width and height to appropriate values
         */
        objPropPanelStyle.setWidth(DIAGRAM_PANEL_WIDTH / 2.5, Unit.EM);
        objPropPanelStyle.setHeight(EDIT_PANEL_HEIGHT, Unit.EM);

        /*
         * Set its background colour to a subtle shade that just frames the panel
         */
        objPropPanelStyle.setBackgroundColor("#fAfAfA"); //$NON-NLS-1$

        /*
         * Add the title
         */
        Label propertyTitle = new Label("Property Editor"); //$NON-NLS-1$
        Style titleStyle = propertyTitle.getElement().getStyle();

        /*
         * Centre the title
         * Set its font size and make it bold
         * Set its height as this ensures a value we can know when
         * passing on the remaining content area to the sub panels, ie.
         * SUB_PANEL_HEIGHT = DIAGRAM_PANEL_HEIGHT
         *                                          - PROPERTY_TITLE_HEIGHT + (BORDER_WIDTH * 2)
         */
        titleStyle.setTextAlign(TextAlign.CENTER);
        titleStyle.setFontSize(1, Unit.EM);
        titleStyle.setFontWeight(FontWeight.BOLD);
        titleStyle.setLineHeight(PROPERTY_TITLE_HEIGHT, Unit.EM);
        titleStyle.setHeight(PROPERTY_TITLE_HEIGHT, Unit.EM);
        objectPropertiesPanel.add(propertyTitle);

        return objectPropertiesPanel;
    }

    private void addPropertiesPanel(KomodoObjectBean kObject) {
        selectionSubPanel = panelFactory.create(kObject);
        objectPropertiesPanel.add(selectionSubPanel);
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        basePanel.add(createDiagramPanel());

        panelFactory.setParentDimensions(DIAGRAM_PANEL_WIDTH / 2.5,
                                                             (EDIT_PANEL_HEIGHT - PROPERTY_TITLE_HEIGHT +
                                                             (BORDER_WIDTH * 2)));
        Widget panel = createObjectPropertiesPanel();
        basePanel.add(panel);

        addPropertiesPanel(null);
    }

    protected void setContent(KomodoObjectBean kObject) {
        editor.setContent(kObject);
    }

    @Override
    public void onSelection(SelectionEvent<KomodoObjectBean[]> event) {
        if (selectionSubPanel != null)
            selectionSubPanel.removeFromParent();

        Object obj = event.getSelectedItem();
        if (! (obj instanceof KomodoObjectBean[])) {
            addPropertiesPanel(null);
            return;
        }

        KomodoObjectBean[] selection = (KomodoObjectBean[]) obj;
        if (selection.length == 0 || selection.length > 1) {
            addPropertiesPanel(null);
            return;
        }

        KomodoObjectBean selectedBean = selection[0];
        addPropertiesPanel(selectedBean);
    }
}