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
package org.komodo.web.share.beans;

import java.util.HashSet;
import java.util.Set;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.komodo.spi.repository.KomodoType;
import org.komodo.web.share.beans.KObjectBeanVisitor.VisitorContext;

/**
 * A data bean for returning KomodoObject info
 *
 * @author mdrillin@redhat.com
 */
@Portable
@Bindable
public class KomodoObjectBean {

    private String name;
    private String path;
    private KomodoType type;
    private boolean hasChildren = false;
    private boolean isVirtual = false;
    private Set<KomodoObjectPropertyBean> properties = new HashSet<KomodoObjectPropertyBean>();

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the absolute path
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @param path the path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the type
     */
	public KomodoType getType() {
		return type;
	}

	/**
	 * @param type the type
	 */
	public void setType(KomodoType type) {
		this.type = type;
	}

	/**
	 * @return hasChildren
	 */
	public boolean hasChildren() {
		return hasChildren;
	}

	/**
	 * @param hasChildren has children flag
	 */
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	
	/**
	 * @return isVirtual
	 */
	public boolean isVirtual() {
		return isVirtual;
	}

	/**
	 * @param isVirtual is virtual flag
	 */
	public void setIsVirtual(boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	/**
     * @return the properties
     */
    public Set<KomodoObjectPropertyBean> getProperties() {
        return this.properties;
    }

    /**
     * @param name the name of the property to get
     * @return the property with the given name
     */
    public KomodoObjectPropertyBean getProperty(String name) {
        for (KomodoObjectPropertyBean property : getProperties()) {
            if (property.getName().equals(name))
                return property;
        }

        return null;
    }

    /**
     * @param property the property
     */
    public void add(KomodoObjectPropertyBean property) {
        properties.add(property);
    }

	/**
     * Accepts an <code>KObjectBeanVisitor</code>. Calls the appropriate
     * <code>KObjectBeanVisitor</code> <code>visit</code> method.
     *
     * @param visitor The KObjectBeanVisitor to be accepted.
     * @param context Visitor context
     */
    public void accept(KObjectBeanVisitor visitor, VisitorContext context) {
	    visitor.visit(this, context);
	}

    @Override
    public String toString() {
        return this.path;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.hasChildren ? 1231 : 1237);
        result = prime * result + (this.isVirtual ? 1231 : 1237);
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
        result = prime * result + ((this.properties == null) ? 0 : this.properties.hashCode());
        result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KomodoObjectBean other = (KomodoObjectBean)obj;
        if (this.hasChildren != other.hasChildren)
            return false;
        if (this.isVirtual != other.isVirtual)
            return false;
        if (this.name == null) {
            if (other.name != null)
                return false;
        } else if (!this.name.equals(other.name))
            return false;
        if (this.path == null) {
            if (other.path != null)
                return false;
        } else if (!this.path.equals(other.path))
            return false;
        if (this.properties == null) {
            if (other.properties != null)
                return false;
        } else if (!this.properties.equals(other.properties))
            return false;
        if (this.type != other.type)
            return false;
        return true;
    }

}
