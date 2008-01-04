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

import java.util.List;
import com.thoughtworks.testme.db.TestMeDatabase;

public class TestMe {
	TestMeDatabase db;
	private String databaseFileName = null;

	public static void main(String[] args) {
		System.out.print(new TestMe().run(args));
		return;
	}

	public TestMe() {
	}

	/**
	 * Use the passed file name rather than the one specified in the properties file
	 * @param databaseFileName
	 */
	public TestMe(String databaseFileName) {
		this.databaseFileName = databaseFileName;
	}

	/**
	 * <b>Usage:</b><br/>
	 *  --reset<br/>
	 *  --classfile classfilename
	 * @param classfilename
	 *            is the name of the class source file (relative to project root)
	 * @param filename
	 *            is a valid path to a JTestMe database file
	 * @return
	 */
	String run(String... args) {
		String result = "";

		if (args.length > 0) {
			db = new TestMeDatabase(databaseFileName); // uses default file name if null

			if ("--reset".equalsIgnoreCase(args[0])) {
				int recordCount = db.getNumberOfMappings();
				db.deleteDatabase();
				result = String.format("Reset database %s, removed mappings for %d class source files", db.getFileName(), recordCount);
			} else if ("--classfile".equalsIgnoreCase(args[0])) {
				String classFileName = args[1];

				List<String> testsForClass = db.getTestsForClassFile(classFileName);
				if (testsForClass != null) {
					for (String testName : testsForClass) {
						result = result.concat(testName + " ");
					}
				}
			}
		}
		return result.trim();
	}
}
