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
package com.thoughtworks.testme.db;

import junit.framework.TestCase;

public class TestMeDatabaseTest extends TestCase {
	private static final String TEST_DATABASE = "TestMeDatabaseTest.db";
	private TestMeDatabase db;

	@Override
	protected void setUp() throws Exception {
		db = new TestMeDatabase(TEST_DATABASE);
		db.deleteDatabase();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		db.deleteDatabase();
		super.tearDown();
	}

	public void testAddingAMappingIncreasesSize() throws Exception {
		int size = 0;

		db.add("java/util/Date.java", "test1");
		assertTrue(db.getSize() > size);
		size = db.getSize();

		db.add("java/util/Date.java", "test2");
		assertTrue(db.getSize() > size);
		size = db.getSize();

		db.add("java/lang/Integer.java", "test1");
		assertTrue(db.getSize() > size);
		size = db.getSize();

		db.add("java/lang/Integer.java", "test3");
		assertTrue(db.getSize() > size);
	}

	public void testSaveShouldCreateFile() throws Exception {
		db.save();
		assertTrue(db.isDatabaseFileUsable());
	}

	public void testExistingFileShouldBeLoadedOnNew() throws Exception {
		TestMeDatabase db1 = db;
		db1.add("java/util/Date.java", "test1");
		db1.save();

		TestMeDatabase db2 = new TestMeDatabase(TEST_DATABASE);
		assertEquals(db1.getSize(), db2.getSize());
		assertTrue(db2.getMappings().containsKey("java/util/Date.java"));
		assertEquals("test1", db2.getMappings().get("java/util/Date.java").get(0));
	}

	public void testGetListOfTestsForAClass() throws Exception {
		db.add("java/util/Date.java", "test1");
		db.add("java/util/Date.java", "test2");
		db.add("java/lang/Integer.java", "test1");
		db.add("java/lang/Integer.java", "test3");

		assertEquals("test1", db.getTestsForClassFile("java/util/Date.java").get(0));
		assertEquals("test2", db.getTestsForClassFile("java/util/Date.java").get(1));
	}

	public void testRemoveWithNoTests() throws Exception {
		String classFileName = "test.java";
		db.add(classFileName, null);
		db.remove(classFileName);
		assertFalse(db.getMappings().containsKey(classFileName));
	}

	public void testRemoveWithOneTest() throws Exception {
		String classFileName = "test.java";
		db.add(classFileName, "test1");
		db.remove(classFileName);
		assertFalse(db.getMappings().containsKey(classFileName));
	}

	public void testRemoveWithTwoTests() throws Exception {
		String classFileName = "test.java";
		db.add(classFileName, "test1");
		db.add(classFileName, "test2");
		db.remove(classFileName);
		assertFalse(db.getMappings().containsKey(classFileName));
	}
}
