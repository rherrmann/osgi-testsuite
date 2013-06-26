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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.osgi.framework.BundleContext;


class DevPropertiesLoader {
  private final BundleContext bundleContext;
  private final Properties properties;

  DevPropertiesLoader( BundleContext bundleContext ) {
    this.bundleContext = bundleContext;
    this.properties = new Properties();
  }

  Properties load() {
    File configurationArea = getConfigurationArea();
    if( configurationArea != null ) {
      File file = new File( configurationArea, "dev.properties" );
      load( file );
    }
    return properties;
  }

  private void load( File file ) {
    InputStream inputStream = openFile( file );
    if( inputStream != null ) {
      try {
        properties.load( inputStream );
        inputStream.close();
      } catch( IOException ioe ) {
        throw new RuntimeException( ioe );
      }
    }
  }

  private File getConfigurationArea() {
    File result = null;
    if( bundleContext != null ) {
      String configurationArea = bundleContext.getProperty( "osgi.configuration.area" );
      URL url = toUrl( configurationArea );
      if( url != null && url.getProtocol().equals( "file" ) ) {
        result = new File( toUri( url ) );
      }
    }
    return result;
  }

  private static FileInputStream openFile( File file ) {
    FileInputStream result;
    try {
      result = new FileInputStream( file );
    } catch( FileNotFoundException fnfe ) {
      result = null;
    }
    return result;
  }

  private static URL toUrl( String string ) {
    URL result;
    try {
      result = new URL( string );
    } catch( MalformedURLException mue ) {
      result = null;
    }
    return result;
  }

  private static URI toUri( URL url ) {
    try {
      return url.toURI();
    } catch( URISyntaxException use ) {
      throw new RuntimeException( use );
    }
  }

}
