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
package org.komodo.web.client.dialogs;

public enum UiEventType {
	REPO_TREE_LOAD_OK,
	REPO_TREE_LOAD_ERROR,
	KOBJECT_SELECTED,
	VDB_CREATE,
	VDB_CREATE_CONFIRM_OK,
	VDB_CREATE_CONFIRM_CANCEL,
	VDB_DELETE,
	VDB_DELETE_CONFIRM_OK,
	VDB_DELETE_CONFIRM_CANCEL,
	REPO_SHOW_VDBS,
	REPO_HIDE_VDBS,
	REPO_SHOW_CONNECTIONS,
	REPO_HIDE_CONNECTIONS,
	REPO_SHOW_DATASOURCES,
	REPO_HIDE_DATASOURCES,
	REPO_SHOW_VIEWS,
	REPO_HIDE_VIEWS;
}
