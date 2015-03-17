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
import javax.inject.Inject;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

@Dependent
@Templated("./VdbPanel.html")
public class VdbPanel extends Composite {
    
    @Inject @DataField("label-komodoprops-name")
    protected Label labelKObjectName;
    
    // VDB Edit panel
    @Inject @DataField("vdb-edit")
    private VdbEditPanel vdbEditPanel;
    
    // VDB Test panel
    @Inject @DataField("vdb-test")
    private VdbTestPanel vdbTestPanel;
    
    // VDB DDL panel
    @Inject @DataField("vdb-ddl")
    private VdbDdlPanel vdbDdlPanel;
    
    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
    	labelKObjectName.setText("Name");
    }
    
    public void setKObject(KomodoObjectBean kObj) {
        labelKObjectName.setText("Komodo VDB: " + kObj.getName());
        String vdbPath = kObj.getPath();

        vdbEditPanel.setContent(kObj);
        vdbDdlPanel.setVdbPath(vdbPath);
    }
    
}