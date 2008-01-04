REM Copyright (c) 2007, ThoughtWorks, Inc.	http://www.thoughtworks.com/
REM All rights reserved.
REM 
REM Licensed under the Apache License, Version 2.0 (the "License");
REM you may not use this file except in compliance with the License.
REM You may obtain a copy of the License at
REM
REM     http://www.apache.org/licenses/LICENSE-2.0
REM
REM Unless required by applicable law or agreed to in writing, software
REM distributed under the License is distributed on an "AS IS" BASIS,
REM WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM See the License for the specific language governing permissions and
REM limitations under the License.
REM 
REM Contributor: Joshua Graham mailto:jagraham@computer.org
REM Contributor: 

@echo off
setlocal ENABLEDELAYEDEXPANSION
set JTESTME_HOME=c:\dev\PROJECTS\GeekNight\trunk\JTestMe\dist
set CLASSPATH=.;c:\dev\_lib\junit.jar;%JTESTME_HOME%\jtestme.jar;%JTESTME_HOME%\aspecjtweaver.jar;%JTESTME_HOME%;

rem This captures stdout and puts it into an environment variable (like backticks on Unix)
FOR /F "usebackq delims=" %%t IN (`gettests --classfile %1`) DO @set test_cases=!test_cases!%%t

java org.junit.runner.JUnitCore %test_cases%
