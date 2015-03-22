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
     * Constructor.
     */
    public KomodoObjectBean() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return the absolute path
     */
    public String getPath() {
        return this.path;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     */
    public void accept(KObjectBeanVisitor visitor) {
	    visitor.visit(this);
	}
}
