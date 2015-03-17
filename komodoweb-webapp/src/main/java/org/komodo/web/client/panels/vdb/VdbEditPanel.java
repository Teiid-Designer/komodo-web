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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;

/**
 * Vdb Editing Panel
 */
@Dependent
@Templated("VdbEditPanel.html")
public class VdbEditPanel extends Composite implements Constants {

    @Inject @DataField("vdb-edit-panel")
    private VerticalPanel vdbEditPanel;

    @Inject
    private VdbEditor editor;

    private Integer scrollPanelWidth = 800;

    private Integer scrollPanelHeight = 600;

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        ScrollPanel scroller = new ScrollPanel(editor);
        scroller.setTitle(VDB_EDIT_SCROLLPANEL);
        scroller.setWidth(scrollPanelWidth + Unit.PX.getType());
        scroller.setHeight(scrollPanelHeight + Unit.PX.getType());

        Style style = scroller.getElement().getStyle();
        style.setBorderWidth(1, Unit.PX);
        style.setBorderStyle(BorderStyle.SOLID);

        vdbEditPanel.add(scroller);
    }

    protected void setContent(KomodoObjectBean kObject) {
        editor.setContent(kObject);
    }

}