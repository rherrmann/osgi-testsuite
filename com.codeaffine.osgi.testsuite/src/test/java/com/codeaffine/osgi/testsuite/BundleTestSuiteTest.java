/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Rüdiger Herrmann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    Frank Appel - ClassnameFilters
 ******************************************************************************/
package com.codeaffine.osgi.testsuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.util.regex.PatternSyntaxException;

import org.junit.Test;
import org.junit.runners.model.InitializationError;

public class BundleTestSuiteTest {

  @Test
  public void testConstructorSatisfiesJUnitRequirements() throws Exception {
    Constructor<BundleTestSuite> constructor = BundleTestSuite.class.getConstructor( Class.class );

    BundleTestSuite instance = constructor.newInstance( EmptyBundleTestSuite.class );

    assertNotNull( instance );
  }

  @Test
  public void testConstructorWithValidSuite() throws InitializationError {
    BundleTestSuite bundleTestSuite = new BundleTestSuite( EmptyBundleTestSuite.class );

    assertEquals( EmptyBundleTestSuite.class, bundleTestSuite.getTestClass().getJavaClass() );
  }

  @Test
  public void testConstructorWithValidPatternSuite() throws InitializationError {
    BundleTestSuite bundleTestSuite = new BundleTestSuite( ValidPatternTestSuite.class );

    assertEquals( ValidPatternTestSuite.class, bundleTestSuite.getTestClass().getJavaClass() );
  }

  @Test
  public void testConstructorWithInvalidSuite() throws Exception {
    try {
      new BundleTestSuite( InvalidBundleTestSuite.class );
      fail();
    } catch( InitializationError expected ) {
      String message = expected.getCauses().get( 0 ).getMessage();
      assertTrue( message.contains( InvalidBundleTestSuite.class.getName() ) );
    }
  }

  @Test
  public void testConstructorWithInvalidPatternSuite() throws Exception {
    try {
      new BundleTestSuite( InvalidPatternTestSuite.class );
      fail();
    } catch( PatternSyntaxException expected ) {
    }
  }

  public static class InvalidBundleTestSuite {}
  @TestBundles({})
  public static class EmptyBundleTestSuite {}
  @TestBundles( {} )
  @ClassnameFilters( { "*" } )
  public static class InvalidPatternTestSuite {}
  @TestBundles( {} )
  @ClassnameFilters( { ".*Foo" } )
  public static class ValidPatternTestSuite {}
}
