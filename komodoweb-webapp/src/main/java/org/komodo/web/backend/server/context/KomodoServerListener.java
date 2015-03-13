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
package org.komodo.web.backend.server.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.komodo.core.KEngine;
import org.komodo.spi.KException;
import org.komodo.utils.KLog;

/**
 *
 */
public class KomodoServerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Nothing required
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        KEngine kEngine = KEngine.getInstance();
        try {
            KLog.getLogger().info("Shutting down Komodo Engine"); //$NON-NLS-1$
            kEngine.shutdown();
        } catch (KException e) {
            KLog.getLogger().error("Komodo Engine shutdown failure", e); //$NON-NLS-1$
        }
    }

}
