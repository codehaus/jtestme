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

import static com.thoughtworks.testme.config.ApplicationConfiguration.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import java.util.logging.Logger;

public class TestMeDatabase implements Serializable {
	private static final long serialVersionUID = 2943334745974160293L;

	private int testListsSize = 0;
	private Map<String, List<String>> mappings = null;

	private transient String fileName;

	// private static final transient Logger log = Logger.getLogger(TestMeDatabase.class.getCanonicalName());

	public TestMeDatabase() {
		this.fileName = getDatabaseFileName();
		construct();
	}

	public TestMeDatabase(String fileName) {
		if (fileName == null) {
			this.fileName = getDatabaseFileName();
		} else {
			this.fileName = fileName;
		}
		construct();
	}

	/**
	 * Adds this mapping to a synchronized list of mappings, keyed by the Class name (all canonical names)
	 * 
	 * @param a
	 *            mapping of Class to TestCase
	 */
	public void add(String classFileName, String testName) {
		addTestToListOfTestsIfUnique(getListOfTestsForClassFile(classFileName), testName);
	}

	public void deleteDatabase() {
		File file = new File(fileName);
		file.delete();
	}

	// private static final transient Logger log = Logger.getLogger(TestMeDatabase.class.getCanonicalName());
	
	public String getFileName() {
		return fileName;
	}

	public int getNumberOfMappings() {
		return getMappings().size();
	}

	public List<String> getTestsForClassFile(String classFileName) {
		return getMappings().get(classFileName);
	}

	public void remove(String classFileName) {
		if (getMappings().containsKey(classFileName)) {
			// clear out all tests for this class file name
			List<String> tests = getMappings().get(classFileName);
			synchronized (tests) {
				tests.clear(); // gc String entries
			}
			tests = null; // gc ArrayList value

			// remove this class file name from the mappings
			getMappings().remove(classFileName);
		}
	}

	/**
	 * Write the data to the database (the file name was specified in the constructor)
	 * 
	 * @throws IOException
	 */
	synchronized public void save() throws IOException {
		// TODO jgraham 24-Jul-2007: synchronize?
		ByteArrayOutputStream byteArrayOutputStream = writeObjectToByteStream();

		File file = new File(fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(byteArrayOutputStream.toByteArray());
		fileOutputStream.close();
	}

	boolean isDatabaseFileUsable() {
		if (fileName == null) {
			return false;
		}
		File file = new File(fileName);
		return file.exists() && file.canRead() && file.canWrite() && file.isFile();
	}

	/**
	 * Obtaining an iterator for this map requires you to manually synchronize to avoid non-deterministic behaviour.
	 * 
	 * @return a synchronized map with unsynchronized iterator
	 * @see Collections.synchronizedMap()
	 */
	Map<String, List<String>> getMappings() {
		return mappings;
	}

	int getSize() {
		return testListsSize + (mappings.size() * 64); // add some overhead for the maps and list structures
	}

	private void addTestToListOfTestsIfUnique(List<String> tests, String test) {
		if (test == null) {
			return;
		}

		synchronized (tests) {
			if (tests.contains(test)) {
				// log.info(test + " already seen for this classfilename.");
				return;
			} else {
				// log.info(test + " first time for this classfilename. TestCases were = " + tests);
				tests.add(test);
				// log.info("TestCases now = " + tests);
				testListsSize += test.length();
			}
		}
	}

	private void construct() {
		mappings = Collections.synchronizedMap(new HashMap<String, List<String>>());
		if (isDatabaseFileUsable()) {
			try {
				// load up existing file contents
				TestMeDatabase existing = load(fileName);
				mappings.putAll(existing.mappings);
				testListsSize = existing.testListsSize;
			} catch (FileNotFoundException e) {
				deleteDatabase(); // illogical in an if exists() condition, but resets things
			} catch (IOException e) {
				deleteDatabase(); // reset
			}
		}
	}

	/**
	 * Adds a Map entry for this class file name and creates an empty List for this class file name, if the first reference for the class file name.
	 * 
	 * @param classFileName
	 * @return a list of TestCase class names for the given class file name
	 */
	private List<String> getListOfTestsForClassFile(String classFileName) {
		List<String> tests = null;

		if (mappings.containsKey(classFileName)) {
			// log.info(classFileName + " already seen");
			tests = mappings.get(classFileName);
		}

		if (tests == null) {
			// log.info(classFileName + " first time");
			tests = Collections.synchronizedList(new ArrayList<String>());
			mappings.put(classFileName, tests);
		}

		return tests;
	}

	private ByteArrayOutputStream writeObjectToByteStream() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.getSize());
		ObjectOutputStream objectOutputStreams = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStreams.writeObject(this);
		objectOutputStreams.close();
		return byteArrayOutputStream;
	}

	synchronized private static TestMeDatabase load(String fileName) throws FileNotFoundException, IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			return null;
		}

		// TODO jgraham 24-Jul-2007: synchronize?
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(readBytesFromFile(file));
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		TestMeDatabase database;
		try {
			database = (TestMeDatabase) objectInputStream.readObject();
		} catch (ClassNotFoundException e) {
			throw new Error("Unable to deserialize class", e);
		}
		objectInputStream.close();

		return database;
	}

	private static byte[] readBytesFromFile(File file) throws FileNotFoundException, IOException {
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] fileByteArray = readBytesFromStream(fileInputStream);
		fileInputStream.close();
		return fileByteArray;
	}

	static byte[] readBytesFromStream(InputStream inputStreams) throws IOException {
		if (inputStreams == null) {
			return null;
		}
		byte[] oldBuf = null, buf = null;
		int oldBufSize = 0;
		int curPtr = 0;
		int avail = inputStreams.available();
		do {
			buf = new byte[oldBufSize + avail];
			if (oldBufSize > 0) {
				System.arraycopy(oldBuf, 0, buf, 0, oldBufSize);
			}
			oldBuf = buf;
			oldBufSize = buf.length;
			int readBytes = inputStreams.read(buf, curPtr, avail);
			assert readBytes == avail;
			curPtr += readBytes;
			avail = inputStreams.available();
		} while (avail > 0);

		inputStreams.close();

		return buf;
	}
}
