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

import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

class BundleTestScanner {

  private static final String DOT_CLASS = ".class";

  private final String[] bundleSymbolicNames;
  private final BundleContext bundleContext;
  private final Set<Class<?>> classes;
  private final Properties properties;

  BundleTestScanner( String... bundleSymbolicNames ) {
    this( getBundleContext(), bundleSymbolicNames );
  }

  BundleTestScanner( BundleContext bundleContext, String... bundleSymbolicNames ) {
    this.bundleContext = bundleContext;
    this.bundleSymbolicNames = bundleSymbolicNames;
    this.classes = new HashSet<Class<?>>();
    this.properties = new DevPropertiesLoader( bundleContext ).load();
  }

  Class<?>[] scan() throws InitializationError {
    for( String bundleSymbolicName : bundleSymbolicNames ) {
      Bundle bundle = findBundle( bundleSymbolicName );
      scan( bundle );
    }
    return classes.toArray( new Class[ classes.size() ] );
  }

  private void scan( Bundle bundle ) throws InitializationError {
    Collection<String> resources = listResources( bundle );
    for( String resource : resources ) {
      String className = toClassName( bundle, resource );
      Class<?> loadedClass = loadClass( bundle, className );
      classes.add( loadedClass );
    }
  }

  private Bundle findBundle( String bundleSymbolicName ) throws InitializationError {
    Bundle result = null;
    Bundle[] bundles = bundleContext.getBundles();
    for( int i = 0; result == null && i < bundles.length; i++ ) {
      if( bundles[ i ].getSymbolicName().equals( bundleSymbolicName ) ) {
        result = bundles[ i ];
      }
    }
    if( result == null ) {
      throw new InitializationError( "Bundle not found: " + bundleSymbolicName );
    }
    return result;
  }

  private Collection<String> listResources( Bundle bundle ) {
    BundleWiring bundleWiring = bundle.adapt( BundleWiring.class );
    int options = BundleWiring.LISTRESOURCES_LOCAL | BundleWiring.LISTRESOURCES_RECURSE;
    String path = getClassesRoot( bundle );
    return bundleWiring.listResources( path, "*Test.class", options );
  }

  private String getClassesRoot( Bundle bundle ) {
    String result = "/";
    String property = properties.getProperty( bundle.getSymbolicName() );
    if( property != null ) {
      result = property + "/";
    }
    return result;
  }

  private static Class<?> loadClass( Bundle bundle, String className )
    throws InitializationError
  {
    try {
      return bundle.loadClass( className );
    } catch( ClassNotFoundException exception ) {
      throw new InitializationError( exception );
    }
  }

  private String toClassName( Bundle bundle, String resource ) {
    String result = resource;
    String path = getClassesRoot( bundle );
    if( result.startsWith( path ) ) {
      result = result.substring( path.length() );
    }
    result = result.replace( '/', '.' );
    return truncateClassExtension( result );
  }

  private static String truncateClassExtension( String string ) {
    String result = string;
    if( result.endsWith( DOT_CLASS ) ) {
      result = result.substring( 0, result.length() - DOT_CLASS.length() );
    }
    return result;
  }

  private static BundleContext getBundleContext() {
    Bundle bundle = FrameworkUtil.getBundle( BundleTestScanner.class );
    return bundle == null ? null : bundle.getBundleContext();
  }
}