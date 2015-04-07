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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.komodo.spi.repository.KomodoType;
import org.komodo.web.share.Constants;
import org.komodo.web.share.beans.KomodoObjectBean;

/**
 *
 */
public class JsonTreeData implements Constants {

    private final KomodoObjectBean source;

    private JsonTreeData parent;

    private String label;

    private List<JsonTreeData> children = new ArrayList<JsonTreeData>();

    private boolean hasChildren;

    /**
     * @param source the source bean of this tree data
     */
    public JsonTreeData(KomodoObjectBean source) {
        this.source = source;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.source.getPath();
    }

    /**
     * @return the source
     */
    public KomodoObjectBean getSource() {
        return this.source;
    }

    /**
     * @return the parent
     */
    public JsonTreeData getParent() {
        return this.parent;
    }

    /**
     * @param parent the parent to set
     */
    protected void setParent(JsonTreeData parent) {
        this.parent = parent;
    }

    /**
     * @param child child to add
     */
    public void addChild(JsonTreeData child) {
        if (child.getParent() == this && children.contains(child))
            return;

        children.add(child);
        child.setParent(this);
    }

    /**
     * @return the type of the source object
     */
    public KomodoType getType() {
        return source.getType();
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * @param label the label to display adjacent to the node
     */
    public void setLabel(String label) {
        if (this.label != null && this.label.equals(label))
            return;

        this.label = label;
    }

    /**
     * @return flag indicating if there are children
     */
    public boolean hasChildren() {
        return this.hasChildren;
    }

    /**
     * @param hasChildren known whether we have children
     */
    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    private void quoted(StringBuffer buffer, String value) {
        buffer.append(SPEECH_MARK).append(value).append(SPEECH_MARK);
    }

    private void colon(StringBuffer buffer) {
        buffer.append(SPACE).append(COLON).append(SPACE);
    }

    private void comma(StringBuffer buffer) {
        buffer.append(COMMA);
    }

    private void newline(StringBuffer buffer) {
        buffer.append(NEW_LINE);
    }

    private void property(StringBuffer buffer, String name, String value) {
        quoted(buffer, name);
        colon(buffer);
        quoted(buffer, value);
    }

    /**
     * @return the json definition of this node
     */
    public String toDefinition() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(OPEN_BRACE);
        newline(buffer);

        property(buffer, ID, getId());
        comma(buffer);
        newline(buffer);

        property(buffer, NAME, getLabel());
        comma(buffer);
        newline(buffer);
        
        property(buffer, PARENT, parent != null ? parent.getLabel() : NULL);
        comma(buffer);
        newline(buffer);

        property(buffer, TYPE, getType().getType());
        comma(buffer);
        newline(buffer);

        property(buffer, HAS_CHILDREN, Boolean.toString(hasChildren()));

        if (! children.isEmpty()) {
            comma(buffer);
            newline(buffer);

            quoted(buffer, CHILDREN);
            colon(buffer);
            buffer.append(OPEN_SQUARE_BRACKET);
            newline(buffer);

            Iterator<JsonTreeData> iterator = children.iterator();
            while (iterator.hasNext()) {
                JsonTreeData child = iterator.next();
                buffer.append(child.toDefinition());

                if (iterator.hasNext()) {
                    comma(buffer);
                    newline(buffer);
                }
            }

            buffer.append(CLOSE_SQUARE_BRACKET);
        }

        buffer.append(CLOSE_BRACE);

        return buffer.toString();
    }

    @Override
    public String toString() {
        return toDefinition();
    }
}
