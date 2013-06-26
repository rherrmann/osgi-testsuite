# Automated OSGi Test Suite

The OSGi Test Suite runs all JUnit tests in a given list of bundles.

## Example
````Java
@RunWith( BundleTestSuite.class )
@TestBundles( { "org.example.bundle1", "org.example.bundle2" } )
public class MasterTestSuite {
}
````

## Lincense
The code is published under the terms of the [Eclipse Public License, version 1.0](http://www.eclipse.org/legal/epl-v10.html).
