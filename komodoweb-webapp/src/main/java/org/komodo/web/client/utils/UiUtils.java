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
package org.komodo.web.client.utils;

import org.gwtbootstrap3.client.ui.Label;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.share.CoreConstants;

import com.google.gwt.user.client.ui.Image;


/**
 * Ui Utilities
 * Constants static methods for common ui tasks
 * @author mdrilling
 */
public class UiUtils implements CoreConstants.RelationalType {

	public enum MessageType {
        INFO, 
        WARNING,
        ERROR,
        SUCCESS
    }
	
	/**
	 * Set the style type for alert labels
	 * @param statusLabel the label
	 * @param msgType the message type
	 */
    public static void setMessageStyle(Label statusLabel, MessageType msgType) {
    	statusLabel.removeStyleName("alert-info");
    	statusLabel.removeStyleName("alert-warning");
    	statusLabel.removeStyleName("alert-danger");
    	statusLabel.removeStyleName("alert-success");
    	if(msgType.equals(MessageType.INFO)) {
    		statusLabel.addStyleName("alert-info");
    	} else if(msgType.equals(MessageType.WARNING)) {
    		statusLabel.addStyleName("alert-warning");
    	} else if(msgType.equals(MessageType.ERROR)) {
    		statusLabel.addStyleName("alert-danger");
    	} else if(msgType.equals(MessageType.SUCCESS)) {
    		statusLabel.addStyleName("alert-success");
    	}
    }
    
	public static String getTypeDisplayName(int type) {
		switch(type) {
			case VDB: return "VDB";
			case MODEL: return "Model";
			case TABLE: return "Table";
			case VIEW: return "View";
			case PROCEDURE: return "Procedure";
			case VIRTUAL_PROCEDURE: return "Virtual Procedure";
			case COLUMN: return "Column";
			case PARAMETER: return "Parameter";
			case RESULT_SET: return "Result Set";
			case TRANSLATOR: return "Translator";
			case MODEL_SOURCE: return "Model Source";
			case ENTRY: return "File Entry";
			case VDB_IMPORT: return "Vdb Import";
			case UNKNOWN: return "Unknown";
		}
		return "Unknown";
	}
	
    public static Image getImage(int type, boolean isVirtual) {

    	switch(type) {
    	case VDB: return new Image(AppResource.INSTANCE.images().vdb_Image());
    	case MODEL: 
    		if(isVirtual) {
    			return new Image(AppResource.INSTANCE.images().modelView_Image());
    		} else {
    			return new Image(AppResource.INSTANCE.images().modelSource_Image());
    		}
    	case TABLE: 
    		if(isVirtual) {
    			return new Image(AppResource.INSTANCE.images().relTableView_Image());
    		} else {
    			return new Image(AppResource.INSTANCE.images().relTableSource_Image());
    		}
    	case VIEW: return new Image(AppResource.INSTANCE.images().relTableView_Image());
    	case PROCEDURE: 
    		if(isVirtual) {
    			return new Image(AppResource.INSTANCE.images().relProcedureView_Image());
    		} else {
    			return new Image(AppResource.INSTANCE.images().relProcedureSource_Image());
    		}
    	case VIRTUAL_PROCEDURE: return new Image(AppResource.INSTANCE.images().relProcedureView_Image());
    	case COLUMN: return new Image(AppResource.INSTANCE.images().relColumn_Image());
    	case PARAMETER: return new Image(AppResource.INSTANCE.images().relParameter_Image());
    	case RESULT_SET: return null;
    	case TRANSLATOR: return null;
    	case MODEL_SOURCE: return new Image(AppResource.INSTANCE.images().modelSource_Image());
    	//		case ENTRY: return CorePlugin.getDefault().getImage(VDB_ICON);
    	case VDB_IMPORT: return null;
    	}
    	return null;
    }
        		
}
