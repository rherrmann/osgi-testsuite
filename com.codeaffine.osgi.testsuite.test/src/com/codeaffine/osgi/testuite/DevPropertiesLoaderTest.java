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
import static org.mockito.Matchers.anyString;
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
  public void testLoadWithoutConfigurationAreaProperty() {
    Properties properties = loadDevProperties();

    assertEquals( 0, properties.size() );
  }

  @Test
  public void testLoadWithNoneConfigurationArea() {
    when( bundleContext.getProperty( anyString() ) ).thenReturn( "@none" );

    Properties properties = loadDevProperties();

    assertEquals( 0, properties.size() );
  }

  @Test
  public void testLoadWithExistingConfigurationArea() throws IOException {
    File directory = tempFolder.getRoot();
    setConfigurationArea( directory );

    Properties properties = loadDevProperties();

    assertEquals( 0, properties.size() );
  }

  @Test
  public void testLoadWithDevPropertiesInConfigurationArea() throws IOException {
    File directory = tempFolder.getRoot();
    Properties properties = new Properties();
    properties.put( "foo", "bar" );
    storeDevProperties( directory, properties );
    setConfigurationArea( directory );

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

  private void setConfigurationArea( File directory ) throws IOException {
    String configurationArea = "file:///" + directory.getCanonicalPath();
    when( bundleContext.getProperty( anyString() ) ).thenReturn( configurationArea );
  }

  private static void storeDevProperties( File directory, Properties properties ) throws IOException
  {
    File file = new File( directory, "dev.properties" );
    OutputStream outputStream = new FileOutputStream( file );
    properties.store( outputStream, "" );
    outputStream.close();
  }
}
