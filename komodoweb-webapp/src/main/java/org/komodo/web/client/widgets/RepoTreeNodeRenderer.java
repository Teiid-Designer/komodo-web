package org.komodo.web.client.widgets;

import org.komodo.web.client.resources.AppResource;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class RepoTreeNodeRenderer extends HorizontalPanel {

	public RepoTreeNodeRenderer(KomodoObjectBean kObj) {
		super();
		init(kObj);
	}
	
	private void init(KomodoObjectBean kObj) {
		
		// TODO: type info will change
		Image theImage = null;
		if(kObj.getType().equals(Constants.VIEW_MODEL)) {
			theImage = new Image(AppResource.INSTANCE.images().viewModel_Image());
		} else if(kObj.getType().equals(Constants.SOURCE_MODEL)) {
			theImage = new Image(AppResource.INSTANCE.images().physicalModel_Image());
		} else if(kObj.getType().equals(Constants.VDB)) {
			theImage = new Image(AppResource.INSTANCE.images().vdb_Image());
		} else if(kObj.getType().equals(Constants.TABLE)) {
			theImage = new Image(AppResource.INSTANCE.images().relTable_Image());
		} else if(kObj.getType().equals(Constants.VIEW)) {
			theImage = new Image(AppResource.INSTANCE.images().relTable_Image());
		} else if(kObj.getType().equals(Constants.COLUMN)) {
			theImage = new Image(AppResource.INSTANCE.images().relColumn_Image());
		} else if(kObj.getType().equals(Constants.PROCEDURE)) {
			theImage = new Image(AppResource.INSTANCE.images().relProcedure_Image());
		} else if(kObj.getType().equals(Constants.PARAMETER)) {
			theImage = new Image(AppResource.INSTANCE.images().relParameterIn_Image());
		} else if(kObj.getType().equals(Constants.VIRTUAL_PROCEDURE)) {
			theImage = new Image(AppResource.INSTANCE.images().relProcedure_Image());
		} else if(kObj.getType().equals(Constants.UNKNOWN_TYPE)) {
			theImage = new Image(AppResource.INSTANCE.images().relFK_Image());
		}
		
		// Add the type image
		add(theImage);
		
		add(new Label(kObj.getName()));
	}
}
