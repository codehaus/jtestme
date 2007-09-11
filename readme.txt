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
JTestMe dynamically defines smoke test suites for Java applications - dramatically improving the continuous integration cycle time.

When a test runs, any application Classes (actually, their source file names) which are executed are recorded into a repository. Super-classes and classes that are not directly called by tests are included. When you change any application code, the repository is inspected to determine which tests should be run.

These tests can then be run whenever you save a file, as an pre-commit build test suite, or as an smoke test suite for a Continuous Integration tool like CruiseControl. Rather than running the whole test suite, or pre-defined test suites, only the tests that need to be run to exercise the changed code are the ones that are run, dramatically reducing the feedback cycle for most applications.

Once the smoke test suite has passed, the full unit, integration, and functional test suites would be executed as is current practice, in order to fully assure the behaviour of the application (as some changes are beyond the view of the executing code, which you can manage by including known cases in manually defined smoke tests).

We looked at perhaps extending code-coverage tools like Cobertura and Emma but these tools don't appear to provide the calling context to capture the running test names, and also are not as pervasive as JUnit.

ProTest is a similar tool with more functionality. https://sourceforge.net/projects/protest Fellow ThoughtWorkers created it. Let's see if we can merge the ideas.

There is a Google/SourceForge Project called "Testar" which has similar goals, but approaches the solution in a different way - it also does not appear to work with the JUnit Ant task, JUnit4, or 1.5 classes. We are open to merging projects if suitable.

Initial Committers:
* Joshua Graham (ThoughtWorks)
* Stacy Curl (ThoughtWorks)
* Gianny Damour (ThoughtWorks)

Maturity: Alpha (currently working, test-driven code - available for the perusal of the Despots if required for approval)

Dependencies:
* AspectJ (including load time weaving)

To do:
* Ant tasks
* JUnit runner integration
* JUnit4 annotation awareness
* CruiseControl "modificationset" integration
* Classification of tests (unit, integration, functional) and finer control of smoke test suite definition
* Eclipse plugin
* IntelliJ IDEA plugin
* Continuous Testing plugin integration (http://www.pag.csail.mit.edu/continuoustesting/)
* Integration to other advanced testing tools which manipulate the ordering of tests to reduce feedback delay
* Non JUnit-based runner integration
* Use a 3rd-party persistence framework if the captured data gets more sophisticated
