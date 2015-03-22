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
package org.komodo.web.client.panels.vdb.editor.diag.tree;

import java.util.List;
import org.komodo.spi.repository.KomodoType;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.beans.KObjectBeanVisitor;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;

/**
 *
 */
public class TreeVisitor implements KObjectBeanVisitor {

    private class Context {

        private TreeData vdb;

        private TreeData parent;

        /**
         * @return the vdb
         */
        public TreeData getVdb() {
            return this.vdb;
        }

        /**
         * @param vdb the vdbNode to set
         */
        public void setVdb(TreeData vdb) {
            this.vdb = vdb;
            tree.setRootData(vdb);
            tree.update();
        }

        /**
         * @return the parent
         */
        public TreeData getParent() {
            return this.parent;
        }

        /**
         * @param parent the parent to set
         */
        public void setParent(TreeData parent) {
            this.parent = parent;
        }

    }

    private final TreeCanvas tree;

    private KomodoRpcService service;

    private Context context;

    /**
     * Create new instance
     *
     * @param tree the canvas on which nodes/links should be added
     */
    public TreeVisitor(TreeCanvas tree) {
        this.tree = tree;
        this.service = KomodoRpcService.get();
        this.context = new Context();
    }

    private TreeData createTreeData(KomodoObjectBean kObject) {
        TreeData data = new TreeData(tree);
        data.setHasChildren(kObject.hasChildren());
        return data;
    }

    private void visitChildren(KomodoObjectBean kObject) {
        IRpcServiceInvocationHandler<List<KomodoObjectBean>> handler = new IRpcServiceInvocationHandler<List<KomodoObjectBean>>() {
            @Override
            public void onReturn(final List<KomodoObjectBean> result) {
                if (result.isEmpty())
                    return;

                for (KomodoObjectBean kObject: result) {
                    kObject.accept(TreeVisitor.this);
                }
            }
            @Override
            public void onError(Throwable error) {
                //TODO
            }
        };

        service.getKomodoNodes(kObject.getPath(), handler);
    }

    private void virtualDatabase(KomodoObjectBean kObject) {
        TreeData vdbNode = createTreeData(kObject);
        vdbNode.setImage(AppResource.INSTANCE.images().diagVdb_Image());
        KomodoObjectPropertyBean nameProperty = kObject.getProperty(VdbLexicon.Vdb.NAME);

        String vdbName = kObject.getName();
        if (nameProperty != null) {
            Object value = nameProperty.getValue();
            if (value != null)
                vdbName = value.toString();
        }
        vdbNode.setLabel(vdbName);

        context.setVdb(vdbNode);
        context.setParent(vdbNode);

        visitChildren(kObject);
    }

    private void model(KomodoObjectBean kObject) {
        TreeData model = createTreeData(kObject);
        model.setImage(AppResource.INSTANCE.images().diagModel_Image());
        model.setLabel(kObject.getName());

        TreeData vdb = context.getParent();
        if (vdb == null)
            return;

        vdb.addChild(model);
        tree.update();
    }

    @Override
    public void visit(KomodoObjectBean kObject) {
        if (kObject == null)
            return;

        KomodoType type = kObject.getType();
        switch (type) {
            case VDB:
                virtualDatabase(kObject);
                break;
            case MODEL:
                model(kObject);
                break;
            case ACCESS_PATTERN:
                break;
            case COLUMN:
                break;
            case DATA_TYPE_RESULT_SET:
                break;
            case FOREIGN_KEY:
                break;
            case INDEX:
                break;
            case PARAMETER:
                break;
            case PRIMARY_KEY:
                break;
            case PUSHDOWN_FUNCTION:
                break;
            case RESULT_SET_COLUMN:
                break;
            case SCHEMA:
                break;
            case STATEMENT_OPTION:
                break;
            case STORED_PROCEDURE:
                break;
            case TABLE:
                break;
            case TABULAR_RESULT_SET:
                break;
            case TEIID:
                break;
            case UNIQUE_CONSTRAINT:
                break;
            case USER_DEFINED_FUNCTION:
                break;
            case VDB_CONDITION:
                break;
            case VDB_DATA_ROLE:
                break;
            case VDB_ENTRY:
                break;
            case VDB_IMPORT:
                break;
            case VDB_MASK:
                break;
            case VDB_MODEL_SOURCE:
                break;
            case VDB_PERMISSION:
                break;
            case VDB_TRANSLATOR:
                break;
            case VIEW:
                break;
            case VIRTUAL_PROCEDURE:
                break;
            case UNKNOWN:
            default:
                visitChildren(kObject);
                break;
        }
    }
}
