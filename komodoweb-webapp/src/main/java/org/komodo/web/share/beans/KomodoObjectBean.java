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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

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
    private String type;
    private boolean hasChildren = false;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean hasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

}
