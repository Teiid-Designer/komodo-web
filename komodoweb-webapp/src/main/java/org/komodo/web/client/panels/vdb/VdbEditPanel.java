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
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

/**
 * Vdb Editing Panel
 */
@Dependent
@Templated("VdbEditPanel.html")
public class VdbEditPanel extends Composite implements SelectionHandler<KomodoObjectBean[]>, Constants {

    private static final Logger LOGGER = Logger.getLogger(VdbEditPanel.class.getName());

    @Inject @DataField("vdb-diagram-panel")
    private VerticalPanel vdbDiagramPanel;

    @Inject @DataField("selection-property-panel")
    private VerticalPanel selectionPropertyPanel;

    @Inject
    private VdbEditor editor;

    @Inject
    private Label propertyLabel;

    private Integer scrollPanelWidth = 1024;

    private Integer scrollPanelHeight = 768;

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        ScrollPanel scroller = new ScrollPanel(editor);
        editor.addSelectionHandler(this);

        scroller.setTitle(VDB_EDIT_SCROLLPANEL);
        scroller.setWidth(scrollPanelWidth + Unit.PX.getType());
        scroller.setHeight(scrollPanelHeight + Unit.PX.getType());

        Style style = scroller.getElement().getStyle();
        style.setBorderWidth(1, Unit.PX);
        style.setBorderStyle(BorderStyle.SOLID);

        vdbDiagramPanel.add(scroller);

        selectionPropertyPanel.add(propertyLabel);

    }

    protected void setContent(KomodoObjectBean kObject) {
        editor.setContent(kObject);
    }

    @Override
    public void onSelection(SelectionEvent<KomodoObjectBean[]> event) {
        Object obj = event.getSelectedItem();
        if (obj instanceof KomodoObjectBean[]) {
            KomodoObjectBean[] selection = (KomodoObjectBean[]) obj;

            if (selection.length > 0) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < selection.length; ++i) {
                    buffer.append(selection[i].getName());
                    if (i < (selection.length - 1))
                        buffer.append(COMMA)
                                 .append(SPACE);
                }

                propertyLabel.setText(buffer.toString());
                return;
            }
        }

        // No selection datta so clear the propertyLabel
        propertyLabel.setText(EMPTY_STRING);
    }
}