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
package com.codeaffine.osgi.testuite;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

public class ClassPathScannerTest {

  private Bundle bundle;
  private Properties devProperties;
  private ClassnameFilter classnameFilter;

  @Test
  public void testScanWithoutDevProperties() throws Exception {
    addTestResource( bundle, FooTest.class.getName(), FooTest.class );

    ClassPathScanner scanner = new ClassPathScanner( bundle, devProperties, classnameFilter );
    Class<?>[] classes = scanner.scan();

    assertArrayEquals( new Class[] { FooTest.class }, classes );
  }

  @Test
  public void testScanWithNonMatchingDevProperties() throws Exception {
    addTestResource( bundle, FooTest.class.getName(), FooTest.class );
    devProperties.setProperty( "some.bogus.bundle.name", "bin" );

    ClassPathScanner scanner = new ClassPathScanner( bundle, devProperties, classnameFilter );
    Class<?>[] classes = scanner.scan();

    assertArrayEquals( new Class[] { FooTest.class }, classes );
  }

  @Test
  public void testScanWithBundleSpecificClassPathRoot() throws Exception {
    addTestResource( bundle, "bin/" + FooTest.class.getName(), FooTest.class );
    devProperties.setProperty( bundle.getSymbolicName(), "bin" );

    ClassPathScanner scanner = new ClassPathScanner( bundle, devProperties, classnameFilter );
    Class<?>[] classes = scanner.scan();

    assertArrayEquals( new Class[] { FooTest.class }, classes );
  }

  @Test
  public void testScanWithGeneralClassPathRoot() throws Exception {
    addTestResource( bundle, "bin/" + FooTest.class.getName(), FooTest.class );
    devProperties.setProperty( "*", "bin" );

    ClassPathScanner scanner = new ClassPathScanner( bundle, devProperties, classnameFilter );
    Class<?>[] classes = scanner.scan();

    assertArrayEquals( new Class[] { FooTest.class }, classes );
  }

  @Test
  public void testScanWithNonMatchingFilter() throws Exception {
    addTestResource( bundle, "bin/" + Foo.class.getName(), Foo.class );
//    devProperties.setProperty( "*", "bin" );

    ClassPathScanner scanner = new ClassPathScanner( bundle, devProperties, classnameFilter );
    Class<?>[] classes = scanner.scan();

    assertArrayEquals( new Class[  0 ], classes );
  }

  @Test
  public void testScanWithNonExistingClass() throws ClassNotFoundException {
    addTestResource( bundle, FooTest.class.getName(), FooTest.class );
    ClassNotFoundException classNotFoundException = new ClassNotFoundException();
    when( bundle.loadClass( anyString() ) ).thenThrow( classNotFoundException );
    ClassPathScanner scanner = new ClassPathScanner( bundle, devProperties, classnameFilter );

    try {
      scanner.scan();
      fail();
    } catch( InitializationError expected ) {
      assertEquals( classNotFoundException, expected.getCauses().get( 0 ) );
    }
  }

  @Before
  public void setUp() {
    bundle = createBundle();
    devProperties = new Properties();
    classnameFilter = mockClassnameFilter();
  }

  private static Bundle createBundle() {
    Bundle result = mock( Bundle.class );
    when( result.getSymbolicName() ).thenReturn( "bundle.symbolic.name" );
    BundleWiring bundleWiring = mock( BundleWiring.class );
    when( result.adapt( BundleWiring.class ) ).thenReturn( bundleWiring );
    return result;
  }

  private static ClassnameFilter mockClassnameFilter() {
    ClassnameFilter result = mock( ClassnameFilter.class );
    when( result.accept( FooTest.class.getName() ) ).thenReturn( true );
    return result;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static void addTestResource( Bundle bundle, String resourceName, Class clazz )
    throws ClassNotFoundException
  {
    BundleWiring bundleWiring = bundle.adapt( BundleWiring.class );
    when( bundleWiring.listResources( anyString(), anyString(), anyInt() ) )
      .thenReturn( Arrays.asList( resourceName ) );
    when( bundle.loadClass( clazz.getName() ) ).thenReturn( clazz );
  }

  private static class Foo {
  }

  private static class FooTest {
  }
}