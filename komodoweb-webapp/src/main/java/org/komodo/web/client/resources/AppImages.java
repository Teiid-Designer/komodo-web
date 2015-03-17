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
package org.komodo.web.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * GWT managed images for Workbench
 */
public interface AppImages extends ClientBundle {

    @Source("images/teiid_user_logo.png")
    ImageResource ufUserLogo();
    
    @Source("images/filterConnections.png")
    ImageResource filterConnectionsImage();
    
    @Source("images/filterViews.png")
    ImageResource filterViewsImage();
    
    @Source("images/filterVdbs.png")
    ImageResource filterVdbsImage();
    
    @Source("images/relational/VDB-edit.png")
    ImageResource editVdbIconImage();
    
    @Source("images/add_16x16.gif")
    ImageResource addIconImage();

    @Source("images/delete_16x16.gif")
    ImageResource removeIconImage();
    
    @Source("images/spinner_16.gif")
    ImageResource spinnner16x16Image();
    
    @Source("images/spinner_24.gif")
    ImageResource spinnner24x24Image();
    
    @Source("images/Error_16x16.png")
    ImageResource errorIcon16x16Image();
    
    @Source("images/Error_32x32.png")
    ImageResource errorIcon32x32Image();
    
    @Source("images/Warning_16x16.png")
    ImageResource warningIcon16x16Image();
    
    @Source("images/Warning_32x32.png")
    ImageResource warningIcon32x32Image();
    
    @Source("images/Ok_16x16.png")
    ImageResource okIcon16x16Image();
    
    @Source("images/Ok_32x32.png")
    ImageResource okIcon32x32Image();
    
    @Source("images/JoinInner.png")
    ImageResource joinInner_Image();
        
    @Source("images/JoinFullOuter.png")
    ImageResource joinFullOuter_Image();
    
    @Source("images/JoinLeftOuter.png")
    ImageResource joinLeftOuter_Image();
    
    @Source("images/JoinRightOuter.png")
    ImageResource joinRightOuter_Image();
    
    // -----------------------------------
    // Model and VDB Images
    // -----------------------------------
    @Source("images/VDB.gif")
    ImageResource vdb_Image();
    
    @Source("images/ModelSource.gif")
    ImageResource modelSource_Image();
    
    @Source("images/ModelView.gif")
    ImageResource modelView_Image();

    // -----------------------------------
    // Relational Images
    // -----------------------------------
    
    @Source("images/relational/Schema.gif")
    ImageResource relSchema_Image();
    @Source("images/relational/TableSource.png")
    ImageResource relTableSource_Image();
    @Source("images/relational/TableView.png")
    ImageResource relTableView_Image();
    @Source("images/relational/Column.gif")
    ImageResource relColumn_Image();
    @Source("images/relational/ProcedureSource.png")
    ImageResource relProcedureSource_Image();
    @Source("images/relational/ProcedureView.png")
    ImageResource relProcedureView_Image();
    @Source("images/relational/Parameter_in.gif")
    ImageResource relParameterIn_Image();
    @Source("images/relational/Parameter_inout.gif")
    ImageResource relParameterInOut_Image();
    @Source("images/relational/Parameter_out.gif")
    ImageResource relParameterOut_Image();
    @Source("images/relational/Parameter_return.gif")
    ImageResource relParameterReturn_Image();
    @Source("images/relational/Parameter_unknown.gif")
    ImageResource relParameterUnknown_Image();
    @Source("images/relational/Parameter.gif")
    ImageResource relParameter_Image();
    @Source("images/relational/PrimaryKey.gif")
    ImageResource relPK_Image();
    @Source("images/relational/ForeignKey.gif")
    ImageResource relFK_Image();
    @Source("images/relational/UniqueConstraint.gif")
    ImageResource relUC_Image();

    // -----------------------------------
    // Diagramming Images
    // -----------------------------------
    @Source ("images/diagramming/vdb.png")
    ImageResource diagVdb_Image();

}
