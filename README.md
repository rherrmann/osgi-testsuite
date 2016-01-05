# Automated OSGi Test Suite 
[![Build Status](https://img.shields.io/travis/rherrmann/osgi-testsuite.svg)](https://travis-ci.org/rherrmann/osgi-testsuite)
[![Version](https://img.shields.io/badge/version-1.2-lightgrey.svg)](http://rherrmann.github.io/osgi-testsuite/repository/)
[![EPL licensed](https://img.shields.io/badge/license-EPL-blue.svg)](https://raw.githubusercontent.com/rherrmann/osgi-testsuite/master/LICENSE)

The OSGi Test Suite is a JUnit test runner that lets you specify a list of bundles and runs all tests contained in these bundles. The test runner is provided as a bundle itself so that it can be easily consumed from OSGi projects.


## Usage
Annotate a class with `@RunWith(BundleTestSuite.class)` and `@TestBundles({"bundle.1", ...})`. 
When you run this class, it will run all the tests in all the bundles.
A test class is identified by its name. By default all public classes whose names end with 'Test' are considered test classes.

For example:
````Java
@RunWith( BundleTestSuite.class )
@TestBundles( { "org.example.bundle1", "org.example.bundle2" } )
public class MasterTestSuite {
}
````
The `MasterTestSuite` will execute all '*Test' classes from bundle1 and bundle2.

If the default strategy to identify test classes is not suitable, it can be overridden with the  `@ClassnameFilters({"filterExpression", ...})` annotation. The list of regular expressions is used match test class names that should be included and excluded.

For Example:
````Java
@RunWith( BundleTestSuite.class )
@TestBundles( { "org.example.bundle1", "org.example.bundle2" } )
@ClassnameFilters( { ".*IntegrationTest", "!.*FooIntegrationTest" } )
public class IntegrationTestSuite {
}
````
The example runs all test cases in bundle1 and bundle2 that are named with the postfix `IntegrationTest`
but exclude those with the postfix `FooIntegrationTest`.

Optionally, a _no match policy_ can be defined that determines what happens if no test classes can be found in one or more of the specified bundles. Available choices are `IGNORE` (do nothing), `WARN` (write a message to the console), and `FAIL` (abort test execution by throwing an exception).

For example:
````Java
@RunWith( BundleTestSuite.class )
@TestBundles( value = "org.example.bundle", noMatchPolicy = NoMatchPolicy.FAIL )
public class MasterTestSuite {
}
````

In order to remain backward compatibility with previous versions, the default value is `IGNORE`.

## Download & Integration
The p2 repository

> `http://rherrmann.github.io/osgi-testsuite/repository`

contains a feature with a single bundle. Add this to your target platform.

In order to use the BundleTestSuite, Require-Bundle or Package-Import the bundle/package com.codeaffine.osgi.testsuite.
In addition an implementation of the OSGi specification [Release 5](https://www.osgi.org/developer/downloads/) (tested with Eclipse Equinox 3.8), JUnit 4.8 or later, and a JRE version 1.5 or later is required.


## Lincense
The code is published under the terms of the [Eclipse Public License, version 1.0](http://www.eclipse.org/legal/epl-v10.html).
