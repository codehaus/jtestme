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
package com.thoughtworks.testme;

import java.io.File;
import junit.framework.TestCase;
import com.thoughtworks.testme.db.TestMeDatabase;

public class TestMeTest extends TestCase {
	private static final String TEST_DATABASE = "TestMeDatabaseTest.db";
	private TestMeDatabase db;

	@Override
	protected void setUp() throws Exception {
		db = new TestMeDatabase(TEST_DATABASE);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		db.deleteDatabase();
		super.tearDown();
	}

	public void testResetCommandRemovesDatabaseFile() throws Exception {
		new TestMe(TEST_DATABASE).run("--reset");
		
		File file = new File(TEST_DATABASE);
		assertFalse(file.exists());
	}

	public void testClassCommandListsCorrectMappings() throws Exception {
		String classFileName1;
		String classFileName2;

		classFileName1 = "java.util.Date"; // need a real class - although "Date" is debatable ;-)
		db.add(classFileName1, "__#Test1!__"); // don't need a real test
		db.add(classFileName1, "__#Test2!__");
		db.add(classFileName1, "__#Test3!__");

		classFileName2 = "java.lang.Integer"; // need a real class
		db.add(classFileName2, "__#Test1!__"); // don't need a real test
		db.add(classFileName2, "__#Test2!__");
		db.add(classFileName2, "__#Test3!__");

		db.save();
		String doc = new TestMe(TEST_DATABASE).run("--classfile", classFileName1);
		
		assertEquals("__#Test1!__ __#Test2!__ __#Test3!__", doc);
	}
}
