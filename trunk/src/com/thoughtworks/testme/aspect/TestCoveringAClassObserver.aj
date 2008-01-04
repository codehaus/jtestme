/*
 * Copyright (c) 2007, ThoughtWorks, Inc. http://www.thoughtworks.com/ All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 * 
 * Contributor: Joshua Graham mailto:jagraham@computer.org
 * Contributor: Gianny Damour
 * 
 */
package com.thoughtworks.testme.aspect;

import junit.framework.TestCase;
import static com.thoughtworks.testme.aspect.TestCoveringAClassObserverHelper.*;

public aspect TestCoveringAClassObserver {
	pointcut allTestMethods(TestCase test): 
		execution(public void TestCase+.test*()) &&  // advise execution of any JUnit test methods
      target(test)                                 // and capture that TestCase instance
	;

	before(TestCase test):
		cflow(allTestMethods(test)) &&					              	     	// within the control flow of the allTestMethods advice
		(execution(!private * *(*)) || execution(!private * *())) &&		// when executing any non-private method
		(execution(* !TestCase+.*()) || execution(* !TestCase+.*(*))) &&	// that is not in the test case class
		within (!com.thoughtworks.testme..*)							      	// and not within any JTestMe class
		{
			recordLink(thisJoinPointStaticPart.getSourceLocation(), test);
		}
	;
}
