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
package com.thoughtworks.testme.aspect;

import java.io.IOException;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.aspectj.lang.reflect.SourceLocation;
import com.thoughtworks.testme.db.TestMeDatabase;

public class TestCoveringAClassObserverHelper {
	private static final TestMeDatabase db = new TestMeDatabase();
	private static final Logger log = Logger.getLogger(TestMeDatabase.class.getCanonicalName());;

	// nobody else can instantiate me
	private TestCoveringAClassObserverHelper() {
	}

	// only save the database as the JVM is exiting
	static {
		log.fine("Adding shutdown hook");
		TestCoveringAClassObserverHelper me = new TestCoveringAClassObserverHelper();
		TestMeShutdownHook shutdownHook = me.new TestMeShutdownHook();
		Runtime.getRuntime().addShutdownHook(shutdownHook);
		log.fine("Shutdown hook added");
	}

	/**
	 * Record the TestCase that initialized a class (even if the class is a super-type of, or called by, a directly tested class). The file name of the source code for the class is the key used for
	 * this record (e.g. "com/example/YourClass.java"). The path of the class file name is relative to some source root (e.g. "YourProject/src/").
	 * 
	 * @param SourceLocation
	 *            of the weaved join point
	 * @param TestCase
	 *            that is currently executing
	 */
	static void recordLink(SourceLocation location, TestCase testCase) {
		String classFileNameUnqualified = location.getFileName(); // Xerox code doesn't include any path info - grrrr.
		String classFileDirectory = location.getWithinType().getPackage().getName().replace('.', '/'); // use the package to derive a path
		String classFileName = classFileDirectory + "/" + classFileNameUnqualified;

		String testName = testCase.getClass().getCanonicalName();

		log.fine("Class source file: " + classFileName + " covered by test case: " + testName);
		db.add(classFileName, testName);
	}

	/**
	 * Save the database to a file
	 * 
	 * @throws absolutely
	 *             nothing so as to not mess anything else up
	 */
	static void saveDatabase() {
		try {
			db.save();
		} catch (IOException e) {
			e.printStackTrace(); // just log it, don't panic and blow up the application's test run 
		}
	}

	static TestMeDatabase getDb() {
		return db;
	}

	private class TestMeShutdownHook extends Thread {
		@Override
		public void run() {
			saveDatabase();
		}
	}
}
