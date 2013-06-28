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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.osgi.framework.BundleContext;

public class DevPropertiesLoaderTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  private BundleContext bundleContext;

  @Test
  public void testLoadWithoutOsgiDevProperty() {
    Properties properties = loadDevProperties();

    assertEquals( 0, properties.size() );
  }

  @Test
  public void testLoadWithEmptyOsgiDevProperty() {
    when( bundleContext.getProperty( "osgi.dev" ) ).thenReturn( "" );

    Properties properties = loadDevProperties();

    assertEquals( 0, properties.size() );
  }

  @Test
  public void testLoadWithNonExistingDevPropertiesFile() throws IOException {
    setOsgiDevProperty();
    DevPropertiesLoader loader = new DevPropertiesLoader( bundleContext );

    try {
      loader.load();
      fail();
    } catch( RuntimeException expected ) {
      assertTrue( expected.getCause() instanceof IOException );
    }
  }

  @Test
  public void testLoadWithDevProperties() throws IOException {
    Properties properties = new Properties();
    properties.put( "foo", "bar" );
    storeDevProperties( properties );
    setOsgiDevProperty();

    Properties loadedProperties = loadDevProperties();

    assertEquals( properties, loadedProperties );
  }

  @Test
  public void testLoadWithNullBundleContext() {
    DevPropertiesLoader loader = new DevPropertiesLoader( null );

    Properties properties = loader.load();

    assertEquals( 0, properties.size() );
  }

  @Before
  public void setUp() {
    bundleContext = mock( BundleContext.class );
  }

  private Properties loadDevProperties() {
    DevPropertiesLoader loader = new DevPropertiesLoader( bundleContext );
    return loader.load();
  }

  private void setOsgiDevProperty() throws IOException {
    File file = new File( tempFolder.getRoot(), "dev.properties" );
    when( bundleContext.getProperty( "osgi.dev" ) ).thenReturn( file.toURI().toURL().toString() );
  }

  private void storeDevProperties( Properties properties ) throws IOException
  {
    File file = new File( tempFolder.getRoot(), "dev.properties" );
    OutputStream outputStream = new FileOutputStream( file );
    properties.store( outputStream, "" );
    outputStream.close();
  }
}
