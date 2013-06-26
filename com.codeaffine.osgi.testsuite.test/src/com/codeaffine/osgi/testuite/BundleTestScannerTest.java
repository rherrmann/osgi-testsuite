/*******************************************************************************
 * Copyright (c) 2012, 2013 Rüdiger Herrmann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 ******************************************************************************/
package com.codeaffine.osgi.testuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

public class BundleTestScannerTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private BundleContext bundleContext;

  @Test
  public void testScanWithNonExistingBundle() {
    String nonExistingBundle = "no.bundle";
    BundleTestScanner scanner = new BundleTestScanner( bundleContext, nonExistingBundle );

    try {
      scanner.scan();
      fail();
    } catch( InitializationError expected ) {
      assertTrue( expected.getCauses().get( 0 ).getMessage().contains( nonExistingBundle ) );
    }
  }

  @Test
  public void testScanWithEmptyBundle() throws InitializationError {
    Bundle bundle = createBundle();

    BundleTestScanner scanner = new BundleTestScanner( bundleContext, bundle.getSymbolicName() );
    Class<?>[] classes = scanner.scan();

    assertEquals( 0, classes.length );
  }

  @Test
  public void testScanWithTestBundle() throws Exception {
    Bundle bundle = createBundle();
    addTestResource( bundle, FooTest.class.getName(), FooTest.class );

    BundleTestScanner scanner = new BundleTestScanner( bundleContext, bundle.getSymbolicName() );
    Class<?>[] classes = scanner.scan();

    assertEquals( 1, classes.length );
    assertEquals( FooTest.class, classes[ 0 ] );
  }

  @Test
  public void testScanWithTestBundleUnderDevelopment() throws Exception {
    Bundle bundle = createBundle();
    addTestResource( bundle, "bin/" + FooTest.class.getName(), FooTest.class );
    createDevProperties( bundle, "bin" );

    BundleTestScanner scanner = new BundleTestScanner( bundleContext, bundle.getSymbolicName() );
    Class<?>[] classes = scanner.scan();

    assertEquals( 1, classes.length );
    assertEquals( FooTest.class, classes[ 0 ] );
  }

  @Before
  public void setUp() {
    bundleContext = mock( BundleContext.class );
    if( bundleContext == null ) {
      throw new RuntimeException( "BAD" );
    }
    when( bundleContext.getBundles() ).thenReturn( new Bundle[ 0 ] );
  }

  private Bundle createBundle() {
    Bundle result = mock( Bundle.class );
    when( result.getSymbolicName() ).thenReturn( "bundle.symbolic.name" );
    BundleWiring bundleWiring = mock( BundleWiring.class );
    when( result.adapt( BundleWiring.class ) ).thenReturn( bundleWiring );
    when( bundleContext.getBundles() ).thenReturn( new Bundle[] { result } );
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

  private void createDevProperties( Bundle bundle, String binFolder ) throws IOException {
    File directory = tempFolder.getRoot();
    storeDevPropertiesFile( directory, bundle, binFolder );
    String configurationArea = "file:///" + directory.getCanonicalPath();
    when( bundleContext.getProperty( "osgi.configuration.area" ) ).thenReturn( configurationArea );
  }

  private static void storeDevPropertiesFile( File directory, Bundle bundle, String binFolder )
    throws IOException
  {
    Properties properties = new Properties();
    properties.put( bundle.getSymbolicName(), binFolder );
    File file = new File( directory, "dev.properties" );
    OutputStream outputStream = new FileOutputStream( file );
    properties.store( outputStream, "" );
    outputStream.close();
  }

  private static class FooTest {
  }

}
