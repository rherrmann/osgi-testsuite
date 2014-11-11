# Automated OSGi Test Suite [![Build Status](https://travis-ci.org/rherrmann/osgi-testsuite.png)](https://travis-ci.org/rherrmann/osgi-testsuite)

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

A mechanism for selection of test classes to run is provided by the `@ClassnameFilters({"filterExpression", ...})` annotation. It
uses regular expressions to match test name patterns.

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



## Download & Integration
The p2 repository

> `http://rherrmann.github.io/osgi-testsuite/repository`

contains a feature with a single bundle. Add this to your target platform.

In order to use the BundleTestSuite, Require-Bundle or Package-Import the bundle/package com.codeaffine.osgi.testsuite.
In addition an implementation of the OSGi specification [Release 4, Version 4.3](http://www.osgi.org/Release4/Download) (tested with Eclipse Equinox 3.7) and JUnit 4.8 or later is required.

## Lincense
The code is published under the terms of the [Eclipse Public License, version 1.0](http://www.eclipse.org/legal/epl-v10.html).
