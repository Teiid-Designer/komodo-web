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
package org.komodo.web.backend.server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.jboss.errai.bus.server.annotations.Service;
import org.komodo.core.KEngine;
import org.komodo.relational.vdb.Vdb;
import org.komodo.relational.vdb.internal.VdbImpl;
import org.komodo.relational.workspace.WorkspaceManager;
import org.komodo.repository.LocalRepository;
import org.komodo.spi.KException;
import org.komodo.spi.repository.KomodoObject;
import org.komodo.spi.repository.Repository;
import org.komodo.spi.repository.RepositoryClient.State;
import org.komodo.spi.repository.RepositoryObserver;
import org.komodo.utils.KLog;
import org.komodo.web.backend.server.services.util.Utils;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.exceptions.KomodoUiException;
import org.komodo.web.share.services.IKomodoService;

/**
 * Concrete implementation of the Komodo service.  This service is used to interact with the Komodo instance
 *
 * @author mdrillin@redhat.com
 */
@Service
public class KomodoService implements IKomodoService {

	private static KEngine kEngine;

	private WorkspaceManager wsManager;

	private Utils utils;

    /**
     * Constructor.
     */
    public KomodoService() {
    }

    private WorkspaceManager getWorkspaceManager() {
        if (wsManager == null) {
            Repository repository = kEngine.getDefaultRepository();
            wsManager = WorkspaceManager.getInstance(repository);
        }

        return wsManager;
    }

    private Utils getUtils() {
        if (utils == null)
            utils = Utils.getInstance();

        return utils;
    }

    @Override
    public void startKEngine( ) throws KomodoUiException {
    	// If KEngine already started, return
    	if(isKEngineStarted()) return;

		/*
		 * Ensure Logging on Modeshape Engine is set to a sane level By default,
		 * it seems to revert to TRACE or ALL.
		 */
		try {
			KLog.getLogger().setLevel(Level.INFO);
		} catch (Exception e) {
			throw new KomodoUiException(e);
		}

		kEngine = KEngine.getInstance();
		final Repository defaultRepo = kEngine.getDefaultRepository();

		// Latch for awaiting the start of the default repository
		final CountDownLatch updateLatch = new CountDownLatch(1);

		// Observer attached to the default repository for listening for the change of state
		RepositoryObserver stateObserver = new RepositoryObserver() {

			@Override
			public void eventOccurred() {
				updateLatch.countDown();
			}
		};
		defaultRepo.addObserver(stateObserver);

		// Start KEngine
		try {
			kEngine.start();
		} catch (KException e) {
			throw new KomodoUiException(e);
		}

		// Block the thread until the latch has counted down or timeout has been reached
		boolean localRepoWaiting = true;
		try {
			localRepoWaiting = updateLatch.await(3, TimeUnit.MINUTES);
		     if (! localRepoWaiting)
		         throw new Exception("Timeout while waiting for Komodo Engine to start."); //$NON-NLS-1$

		} catch (Exception e) {
			throw new KomodoUiException(e);
		}
    	
    	//loadTestWorkspace();
    }

    @Override
    public void shutdownKEngine( ) throws KomodoUiException {
    	if(kEngine!=null) {
        	// Stop KEngine
        	try {
    			kEngine.shutdown();
    		} catch (KException e) {
    			throw new KomodoUiException(e);
    		}
    	}
    	return;
    }
    
    private static boolean isKEngineStarted() {
    	boolean isStarted = false;
    	
    	if(kEngine!=null) {
    		State engineState = kEngine.getState();
    		if(engineState==State.STARTED) {
    			isStarted=true;
    		}
    	}
    	
    	return isStarted;
    }
    
    @Override
    public List<KomodoObjectBean> getKomodoNodes(final String kObjPath) throws KomodoUiException {
    	if(!isKEngineStarted()) {
    		startKEngine();
    	}

    	List<KomodoObjectBean> result = new ArrayList<KomodoObjectBean>();
    	
  		Repository repo = kEngine.getDefaultRepository();
  		
  		// If kObjPath is null, get the root Vdbs
  		
    	KomodoObject[] children = null;
		try {
			if(kObjPath==null) {
				children = getWorkspaceManager().findVdbs(null);
			} else {
				KomodoObject kObj = repo.getFromWorkspace(null, kObjPath);
				children = kObj.getChildren(null);
			}
		} catch (KException e) {
			throw new KomodoUiException(e);
		}
		if(children!=null && children.length>0) {
			for(KomodoObject child : children) {
				result.add(getUtils().createKomodoObjectBean(child));
			}
		}

		return result;
	}

	@Override
    public KomodoObjectBean createVdb(final String vdbName) throws KomodoUiException {
		if(!isKEngineStarted()) {
			startKEngine();
		}

		KomodoObjectBean result = null;

		Vdb newVdb;
		try {
			newVdb = getWorkspaceManager().createVdb(null, null, vdbName, "extPath"); //$NON-NLS-1$
			result = getUtils().createKomodoObjectBean(newVdb);
		} catch (KException ex) {
			throw new KomodoUiException(ex);
		}

		return result;
	}

	@Override
    public List<KomodoObjectBean> deleteVdb(final String vdbName) throws KomodoUiException {
		if(!isKEngineStarted()) {
			startKEngine();
		}

		try {
			KomodoObject[] vdbs = getWorkspaceManager().findVdbs(null);
			for(KomodoObject vdb : vdbs) {
				String kVdbName = vdb.getName(null);
				if(vdbName.equals(kVdbName)) {
				    getWorkspaceManager().delete(null, vdb);
					break;
				}
			}
		} catch (KException ex) {
			throw new KomodoUiException(ex);
		}

		return getKomodoNodes(null);
	}

	@Override
    public String getVdbDDL(final String vdbPath) throws KomodoUiException {
		if(!isKEngineStarted()) {
			startKEngine();
		}

		// If kObjPath is null, get the root Vdbs

		String vdbDdl = null;
		try {
			Vdb[] vdbs = getWorkspaceManager().findVdbs(null);
			for(Vdb vdb : vdbs) {
				String thePath = vdb.getAbsolutePath();
				if(thePath.equals(vdbPath)) {
					vdbDdl = ((VdbImpl)vdb).export(null);
				}
			}
		} catch (KException e) {
			throw new KomodoUiException(e);
		}

		return vdbDdl;
	}

	protected static LocalRepository _repo = null;

	protected static LocalRepositoryObserver _repoObserver = null;

	protected static class LocalRepositoryObserver implements RepositoryObserver {

		private CountDownLatch latch;

		public LocalRepositoryObserver() {
			resetLatch();
		}

		public void resetLatch() {
			latch = new CountDownLatch(1);
		}

		/**
		 * @return the latch
		 */
		 public CountDownLatch getLatch() {
			 return this.latch;
		 }

		 @Override
		 public void eventOccurred() {
			 latch.countDown();
		 }
	}

}
