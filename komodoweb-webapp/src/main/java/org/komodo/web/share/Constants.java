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
package org.komodo.web.share;

import org.komodo.spi.constants.StringConstants;

/**
 * Application constants
 * @author mdrillin@redhat.com
 */
@SuppressWarnings( {"javadoc", "nls"} )
public interface Constants extends StringConstants {

    String OK = "OK";
    String SELECT_STAR_FROM = "SELECT * FROM";
    String LIMIT_10 = "LIMIT 10";
    String NONE = "NONE";
    String OK_BUTTON_TXT = "Ok";
    String CANCEL_BUTTON_TXT = "Cancel";

    String NEW_VDB_BASENAME = "NewVDB";

    String KOMODO_WORKSPACE_SCREEN = "KomodoWorkspaceScreen";
    String UNKNOWN = "[unknown]";
    
    String CONFIRMATION_DIALOG = "ConfirmationDialog";
    String CONFIRMATION_DIALOG_TYPE_KEY = "ConfirmDialog_TypeKey";
    String CONFIRMATION_DIALOG_ARG_KEY = "ConfirmDialog_ArgKey";

    String CONFIRMATION_DIALOG_DELETE_VDB = "ConfirmDeleteVDB";
    String CONFIRMATION_DIALOG_CREATE_VDB = "ConfirmCreateVDB";

    String ADD_VIEW_SRC_DIALOG = "AddViewSrcDialog";
    String ADD_VIEW_SRC_AVAILABLE_SRCS = "AddViewAvailSrcs";
    String UPLOAD_DRIVER_DIALOG = "UploadDriverDialog";

    int DATASOURCES_TABLE_PAGE_SIZE = 15;
    int DATASOURCE_TYPES_TABLE_PAGE_SIZE = 15;
    int VDBS_TABLE_PAGE_SIZE = 15;
    int VDB_MODELS_TABLE_PAGE_SIZE = 15;
    int QUERY_RESULTS_TABLE_PAGE_SIZE = 15;
    int QUERY_COLUMNS_TABLE_PAGE_SIZE = 6;

    String FROM_SCREEN = "from-screen";

    String JNDI_PREFIX = "java:/";
    String JBOSS_JNDI_PREFIX = "java:jboss/datasources/";
    String MODESHAPE_JNDI_PREFIX = "java:/datasources/";
    String SERVICE_NAME_KEY = "service-name";
    String SERVICE_VIEW_NAME = "SvcView";
    String CLONE_SERVICE_KEY = "clone-service";
    String DELETE_SERVICE_KEY = "delete-service";

    String VDB_PROP_KEY_REST_AUTOGEN = "{http://teiid.org/rest}auto-generate";
    String VDB_PROP_KEY_DATASERVICE_VIEWNAME = "data-service-view";

    String OPENSHIFT_HOST_PREFIX = "[OPENSHIFT]";

    String DYNAMIC_VDB_SUFFIX = "-vdb.xml";
    String STATUS_ACTIVE = "ACTIVE";
    String STATUS_INACTIVE = "INACTIVE";
    String STATUS_LOADING = "LOADING";
    String STATUS_UNKNOWN = "Unknown";

    String VDB = "VDB";
    String VIEW_MODEL = "VIEW_MODEL";
    String SOURCE_MODEL = "SOURCE_MODEL";
    String TABLE = "TABLE";
    String VIEW = "VIEW";
    String COLUMN = "COLUMN";
    String PARAMETER = "PARAMETER";
    String PROCEDURE = "PROCEDURE";
    String VIRTUAL_PROCEDURE = "VIRTUAL_PROCEDURE";
    String UNKNOWN_TYPE = "UNKNOWN";

    /*
     * d3 diagram processing
     */
    String SVG_ELEMENT = "svg";
    String GROUP_ELEMENT = "g";
    String SVG_PATH = "path";
    String SVG_TRANSFORM = "transform";
    String SVG_DATA_ELEMENT = "d";

    String CSS_CLASS = "class";
    String CSS_FILL_OPACITY = "fill-opacity";

    String HTML_XLINK_REF = "xlink:href";
    String HTML_X = "x";
    String HTML_Y = "y";
    String HTML_WIDTH = "width";
    String HTML_HEIGHT = "height";
    String HTML_IMAGE = "image";
    String HTML_TEXT = "text";
    String HTML_DY = "dy";
    String HTML_TEXT_ANCHOR = "text-anchor";

    String MIDDLE = "middle";
    String NODE = "node";

    /* Vdb Editor */
    String VDB_EDIT_SCROLLPANEL = "vdb-edit-scrollpanel";
    String VDB_EDITOR = "vdb-editor";
}
