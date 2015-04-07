/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.komodo.web.client.widgets;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.beans.KomodoObjectBean;
import com.google.gwt.user.client.Window;

/**
 *
 */
public class KObjectExecutor {

    private static final Logger LOGGER = Logger.getLogger(KObjectExecutor.class.getName());

    /**
     * Execute the operation on the komodo object at the given path
     *
     * @param kObjectPath komodo object path
     * @param operation the operation
     */
    public void executeOperation(final String kObjectPath, final KObjectOperation operation) {
        KomodoRpcService.get().getKomodoNode(kObjectPath, new IRpcServiceInvocationHandler<KomodoObjectBean>() {
            @Override
            public void onReturn(KomodoObjectBean kObject) {
                operation.execute(kObject);
            }

            @Override
            public void onError(Throwable error) {
                String msg = "Unable to perform operation on " + kObjectPath + " due to the error: " + error.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$
                Window.alert(msg);
                LOGGER.log(Level.SEVERE, msg, error);
            }
        });
    }
}
