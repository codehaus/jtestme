/*
 * Copyright (c) 2007, ThoughtWorks, Inc.	http://www.thoughtworks.com/
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributor: Joshua Graham mailto:jagraham@computer.org
 * Contributor: 
 * 
 */
package com.thoughtworks.testme.config;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ApplicationConfiguration {
	private static final String BUNDLE_NAME = "jtestme";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	public static String getDatabaseFileName() {
		return getProperty("database.fileName");
	}

	public static String getProperty(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
