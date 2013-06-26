# Automated OSGi Test Suite

The OSGi Test Suite is a JUnit test runner that runs all tests in a given list of bundles.

## Usage
Annotate a class with `@RunWith(BundleTestSuite.class)` and `@TestBundles({"bundle.1", ...})`. 
When you run this class, it will run all the tests in all the bundles.
A test class is identified by its name. All public classes whose names end with 'Test' are considered test classes.

## Example
````Java
@RunWith( BundleTestSuite.class )
@TestBundles( { "org.example.bundle1", "org.example.bundle2" } )
public class MasterTestSuite {
}
````

## Lincense
The code is published under the terms of the [Eclipse Public License, version 1.0](http://www.eclipse.org/legal/epl-v10.html).
