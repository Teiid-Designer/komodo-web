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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.komodo.spi.repository.KomodoType;
import org.komodo.web.client.resources.AppResource;
import org.komodo.web.client.services.KomodoRpcService;
import org.komodo.web.client.services.rpc.IRpcServiceInvocationHandler;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KObjectBeanVisitor;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 */
public class TreeVisitor implements KObjectBeanVisitor<TreeData>, Constants {

    private static final Logger LOGGER = Logger.getLogger(TreeVisitor.class.getName());

    private class TreeVisitorContext implements VisitorContext<TreeData> {

        private TreeData parent;

        /**
         * @param vdb the vdbNode to set
         */
        public void setVdb(TreeData vdb) {
            tree.setRootData(vdb);
            setParent(parent);
        }

        @Override
        public TreeData getParent() {
            return this.parent;
        }

        @Override
        public void setParent(TreeData parent) {
            this.parent = parent;
        }

    }

    private final TreeCanvas tree;

    private KomodoRpcService service;

    /**
     * Create new instance
     *
     * @param tree the canvas on which nodes/links should be added
     */
    public TreeVisitor(TreeCanvas tree) {
        this.tree = tree;
        this.service = KomodoRpcService.get();
    }

    /**
     * @param parent the parent to assign to the context
     * @return new context for this visitor
     */
    public TreeVisitorContext createContext(TreeData parent) {
        TreeVisitorContext context = new TreeVisitorContext();
        context.setParent(parent);
        return context;
    }

    private TreeData createTreeData(KomodoObjectBean kObject, ImageResource img, String name) {
        TreeData data = new TreeData(tree);
        data.setHasChildren(kObject.hasChildren());
        data.setImage(img);

        if (KomodoType.UNKNOWN == kObject.getType())
            data.setLabel(name);
        else {
            StringBuffer label = new StringBuffer();
            label.append(name)
                 .append(SPACE).append(COLON)
                 .append(OPEN_SQUARE_BRACKET)
                 .append(kObject.getType().getType())
                 .append(CLOSE_SQUARE_BRACKET);
            data.setLabel(label.toString());
        }

        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("Definition of " + kObject.getName() + //$NON-NLS-1$
                                   SPACE + HYPHEN + SPACE + data.toDefinition());

        return data;
    }

    private void addChild(TreeData child, VisitorContext<TreeData> context) {
        TreeData parent = context.getParent();
        if (parent == null)
            return;

        parent.addChild(child);
        tree.update();
    }

    private void create(KomodoObjectBean kObject, VisitorContext<TreeData> context, ImageResource imageResource) {
        TreeData treeData = createTreeData(kObject,
                                                              imageResource,
                                                              kObject.getName());

        addChild(treeData, context);

        TreeVisitorContext childContext = createContext(treeData);
        visitChildren(kObject, childContext);
    }

    private void visitChildren(final KomodoObjectBean kObject, final VisitorContext<TreeData> context) {
        if (! kObject.hasChildren())
            return;

        IRpcServiceInvocationHandler<List<KomodoObjectBean>> handler = new IRpcServiceInvocationHandler<List<KomodoObjectBean>>() {
            @Override
            public void onReturn(final List<KomodoObjectBean> result) {
                if (result.isEmpty())
                    return;

                for (KomodoObjectBean child: result) {
                    child.accept(TreeVisitor.this, context);
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
        KomodoObjectPropertyBean nameProperty = kObject.getProperty(VdbLexicon.Vdb.NAME);

        String vdbName = kObject.getName();
        if (nameProperty != null) {
            Object value = nameProperty.getValue();
            if (value != null)
                vdbName = value.toString();
        }

        TreeData vdbNode = createTreeData(kObject,
                                                                  AppResource.INSTANCE.images().diagVdb_Image(),
                                                                  vdbName);

        TreeVisitorContext context = createContext(vdbNode);
        context.setVdb(vdbNode);

        visitChildren(kObject, context);
    }

    private void model(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagModel_Image());
    }

    private void modelSource(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagSource_Image());
    }

    private void translator(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagTranslator_Image());
    }

    private void vdbImport(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagVdbImport_Image());
    }

    private void dataRole(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagDataRole_Image());
    }

    private void permission(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagPermission_Image());
    }

    private void condition(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagCondition_Image());
    }

    private void view(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagView_Image());
    }

    private void parameter(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagParameter_Image());
    }

    private void resultSet(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagResultSet_Image());
    }

    private void dataTypeResultSet(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagDataTypeResultSet_Image());
    }

    private void mask(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagMask_Image());
    }

    private void accesspattern(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagAccessPattern_Image());
    }

    private void column(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagColumn_Image());
    }

    private void resultSetColumn(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagResultSetColumn_Image());
    }

    private void usedDefinedFunction(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagUsedDefinedFunction_Image());
    }

    private void uniqueConstraint(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagUniqueConstraint_Image());
    }

    private void table(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagTable_Image());
    }

    private void storedProcedure(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagStoredProcedure_Image());
    }

    private void virtualProcedure(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagVirtualProcedure_Image());
    }

    private void statementOption(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagStatementOption_Image());
    }

    private void schema(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagSchema_Image());
    }

    private void pushdownFunction(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagPushdownFunction_Image());
    }

    private void index(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagIndex_Image());
    }

    private void foreignKey(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagForeignKey_Image());
    }

    private void primaryKey(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagPrimaryKey_Image());
    }

    private void vdbEntry(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagVdbEntry_Image());
    }

    private void ddlSchema(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagDdl_Image());
    }

    private void teiidSqlSchema(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagTeiidSql_Image());
    }

    private void vdbSchema(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        create(kObject, context, AppResource.INSTANCE.images().diagVdbSchema_Image());
    }

    @Override
    public void visit(KomodoObjectBean kObject, VisitorContext<TreeData> context) {
        if (kObject == null)
            return;

        KomodoType type = kObject.getType();
        switch (type) {
            case VDB:
                virtualDatabase(kObject);
                break;
            case MODEL:
                model(kObject, context);
                break;
            case PARAMETER:
                parameter(kObject, context);
                break;
            case TABULAR_RESULT_SET:
                resultSet(kObject, context);
                break;
            case VDB_CONDITION:
                condition(kObject, context);
                break;
            case VDB_DATA_ROLE:
                dataRole(kObject, context);
                break;
            case VDB_IMPORT:
                vdbImport(kObject, context);
                break;
            case VDB_MASK:
                mask(kObject, context);
                break;
            case VDB_MODEL_SOURCE:
                modelSource(kObject, context);
                break;
            case VDB_PERMISSION:
                permission(kObject, context);
                break;
            case VDB_TRANSLATOR:
                translator(kObject, context);
                break;
            case VIEW:
                view(kObject, context);
                break;
            case ACCESS_PATTERN:
                accesspattern(kObject, context);
                break;
            case COLUMN:
                column(kObject, context);
                break;
            case DATA_TYPE_RESULT_SET:
                dataTypeResultSet(kObject, context);
                break;
            case RESULT_SET_COLUMN:
                resultSetColumn(kObject, context);
                break;
            case PRIMARY_KEY:
                primaryKey(kObject, context);
                break;
            case FOREIGN_KEY:
                foreignKey(kObject, context);
                break;
            case INDEX:
                index(kObject, context);
                break;
            case PUSHDOWN_FUNCTION:
                pushdownFunction(kObject, context);
                break;
            case SCHEMA:
                schema(kObject, context);
                break;
            case STATEMENT_OPTION:
                statementOption(kObject, context);
                break;
            case STORED_PROCEDURE:
                storedProcedure(kObject, context);
                break;
            case TABLE:
                table(kObject, context);
                break;
            case UNIQUE_CONSTRAINT:
                uniqueConstraint(kObject, context);
                break;
            case USER_DEFINED_FUNCTION:
                usedDefinedFunction(kObject, context);
                break;
            case VDB_ENTRY:
                vdbEntry(kObject, context);
                break;
            case VIRTUAL_PROCEDURE:
                virtualProcedure(kObject, context);
                break;
            case TEIID:
                return; // Not being dealt with in this visitor
            case TSQL_SCHEMA:
                teiidSqlSchema(kObject, context);
                break;
            case DDL_SCHEMA:
                ddlSchema(kObject, context);
                break;
            case VDB_SCHEMA:
                vdbSchema(kObject, context);
                break;
            case UNKNOWN:
            default:
                visitChildren(kObject, context);
                break;
        }
    }
}
