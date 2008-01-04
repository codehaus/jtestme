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
 * ******************************************************************************
 * SourceLocationImpl copied from AspectJ source (perhaps we'll Mock it instead)
 * Copyright (c) 1999-2001 Xerox Corporation, 
 *               2002 Palo Alto Research Center, Incorporated (PARC).
 * ******************************************************************************
 *               
 */
package com.thoughtworks.testme.aspect;

import java.util.List;
import junit.framework.TestCase;
import org.aspectj.lang.reflect.SourceLocation;

public class TestCoveringAClassObserverHelperTest extends TestCase {

	public void testRecordLink() {
		String classFileShortName = "Integer.java";
		String classFileName = "java/lang/" + classFileShortName;
		SourceLocationImpl location = new SourceLocationImpl(Integer.class, classFileShortName, 1);
		TestCoveringAClassObserverHelper.recordLink(location, this);
		String testName = this.getClass().getCanonicalName();
		List<String> testsForClassFile = TestCoveringAClassObserverHelper.getDb().getTestsForClassFile(classFileName);
		assertEquals(testName, testsForClassFile.get(0));
		TestCoveringAClassObserverHelper.getDb().remove(classFileName);
	}

	// basically copied from Xerox's code, but theirs is a non-public class
	private class SourceLocationImpl implements SourceLocation {
		Class<?> withinType;
		String fileName;
		int line;

		SourceLocationImpl(Class<?> withinType, String fileName, int line) {
			this.withinType = withinType;
			this.fileName = fileName;
			this.line = line;
		}

		public Class<?> getWithinType() {
			return withinType;
		}

		public String getFileName() {
			return fileName;
		}

		public int getLine() {
			return line;
		}

		public int getColumn() {
			return -1;
		}
	}
}
