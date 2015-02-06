package org.komodo.web.client.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.share.beans.KomodoObjectBean;

import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

public class RepoTreeDataProvider extends AsyncDataProvider<KomodoObjectBean> {

	@Inject
	private KomodoRpcService komodoService;
	
	@Override
	protected void onRangeChanged(HasData<KomodoObjectBean> display) {
	      List<KomodoObjectBean> result = new ArrayList<KomodoObjectBean>();
	      KomodoObjectBean test = new KomodoObjectBean();
	      test.setName("node1");
	      result.add(test);
	      updateRowCount(result.size(), true);
	      updateRowData(0, result);
	      
//		komodoService.getChildren(null, new IRpcServiceInvocationHandler<List<KomodoObjectBean>>() {
//			@Override
//			public void onReturn(final List<KomodoObjectBean> result) {	
//				Window.alert("returned children");
//				updateRowCount(result.size(), true);
//				updateRowData(0, result);
//			}
//			@Override
//			public void onError(Throwable error) {
//				//errorMessage = error.getMessage();
//				//refreshCompleteEvent.fire(new UiEvent(UiEventType.QUERY_RESULT_DISPLAYER_REFRESHED_ERROR));
//			}
//		});
	}

    
}
