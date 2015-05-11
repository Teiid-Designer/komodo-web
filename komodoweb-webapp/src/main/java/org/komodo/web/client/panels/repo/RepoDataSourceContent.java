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
package org.komodo.web.client.panels.repo;

import javax.enterprise.context.Dependent;

import org.gwtbootstrap3.client.ui.PanelCollapse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Accordion for display of data source content
 */
@Dependent
public class RepoDataSourceContent extends Composite {
    
    interface RepoDataSourceContentBinder extends UiBinder<Widget, RepoDataSourceContent> { }
    private static RepoDataSourceContentBinder uiBinder = GWT.create(RepoDataSourceContentBinder.class);
 
    @UiField
    PanelCollapse collapseDataSource;

    /**
     * Constructor
     */
    public RepoDataSourceContent() {
        // Init the dashboard from the UI Binder template
        initWidget(uiBinder.createAndBindUi(this));
    }
    
}