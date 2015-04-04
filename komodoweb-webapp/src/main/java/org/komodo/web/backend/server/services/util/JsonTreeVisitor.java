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
package org.komodo.web.backend.server.services.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.komodo.spi.repository.KomodoType;
import org.komodo.web.backend.server.services.KomodoService;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KObjectBeanVisitor;
import org.komodo.web.share.beans.KomodoObjectBean;
import org.komodo.web.share.beans.KomodoObjectPropertyBean;
import org.komodo.web.share.exceptions.KomodoUiException;
import org.modeshape.sequencer.teiid.lexicon.VdbLexicon;

/**
 *
 */
public class JsonTreeVisitor implements KObjectBeanVisitor<JsonTreeData>, Constants {

    private static final Logger LOGGER = Logger.getLogger(JsonTreeVisitor.class.getName());

    private class TreeVisitorContext implements VisitorContext<JsonTreeData> {

        private JsonTreeData parent;

        @Override
        public JsonTreeData getParent() {
            return this.parent;
        }

        @Override
        public void setParent(JsonTreeData parent) {
            this.parent = parent;
        }

    }

    private final KomodoService service;

    private JsonTreeData rootData;

    /**
     * Create new instance
     *
     * @param service komodo service
     */
    public JsonTreeVisitor(KomodoService service) {
        this.service = service;
    }

    /**
     * @param parent the parent to assign to the context
     * @return new context for this visitor
     */
    public TreeVisitorContext createContext(JsonTreeData parent) {
        TreeVisitorContext context = new TreeVisitorContext();
        context.setParent(parent);
        return context;
    }

    /**
     * @return the json definition of the tree data
     */
    public String getDefinition() {
        if (rootData == null)
            return EMPTY_STRING;

        return rootData.toDefinition();
    }

    private JsonTreeData createTreeData(KomodoObjectBean kObject, String name) {
        JsonTreeData data = new JsonTreeData(kObject);
        if (rootData == null) {
            // This is the first data since root data has not yet been assigned
            this.rootData = data;
        }

        data.setHasChildren(kObject.hasChildren());

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

        return data;
    }

    private void addChild(JsonTreeData child, VisitorContext<JsonTreeData> context) {
        JsonTreeData parent = context.getParent();
        if (parent == null)
            return;

        parent.addChild(child);
    }

    private void create(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        JsonTreeData jsonTreeData = createTreeData(kObject, kObject.getName());

        addChild(jsonTreeData, context);

        TreeVisitorContext childContext = createContext(jsonTreeData);
        visitChildren(kObject, childContext);
    }

    private void visitChildren(final KomodoObjectBean kObject, final VisitorContext<JsonTreeData> context) {
        if (! kObject.hasChildren())
            return;

        try {
            List<KomodoObjectBean> children = service.getKomodoNodes(kObject.getPath());
            for (KomodoObjectBean child : children) {
                child.accept(this, context);
            }
        } catch (KomodoUiException ex) {
            LOGGER.log(Level.SEVERE, "Cannot retrieve node children", ex); //$NON-NLS-1$
            return;
        }
    }

    private void virtualDatabase(KomodoObjectBean kObject) {
        KomodoObjectPropertyBean nameProperty = kObject.getProperty(VdbLexicon.Vdb.NAME);

        String vdbName = kObject.getName();
        if (nameProperty != null) {
            Object value = nameProperty.getValue();
            if (value != null)
                vdbName = value.toString();
        }

        JsonTreeData vdbNode = createTreeData(kObject, vdbName);

        TreeVisitorContext context = createContext(vdbNode);
        visitChildren(kObject, context);
    }

    private void model(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void modelSource(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void translator(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void vdbImport(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void dataRole(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void permission(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void condition(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void view(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void parameter(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void resultSet(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void dataTypeResultSet(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void mask(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void accesspattern(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void column(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void resultSetColumn(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void usedDefinedFunction(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void uniqueConstraint(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void table(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void storedProcedure(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void virtualProcedure(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void statementOption(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void schema(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void pushdownFunction(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void index(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void foreignKey(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void primaryKey(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void vdbEntry(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void ddlSchema(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void teiidSqlSchema(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    private void vdbSchema(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
        create(kObject, context);
    }

    @Override
    public void visit(KomodoObjectBean kObject, VisitorContext<JsonTreeData> context) {
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
            case TSQL_SCHEMA:
                teiidSqlSchema(kObject, context);
                break;
            case DDL_SCHEMA:
                ddlSchema(kObject, context);
                break;
            case VDB_SCHEMA:
                vdbSchema(kObject, context);
                break;
            case TEIID:
                return;
            case UNKNOWN:
            default:
                visitChildren(kObject, context);
                break;
        }
    }
}
