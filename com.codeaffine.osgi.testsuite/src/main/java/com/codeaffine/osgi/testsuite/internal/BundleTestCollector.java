/*******************************************************************************
 * Copyright (c) 2012, 2015 Rüdiger Herrmann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rüdiger Herrmann - initial API and implementation
 *    Frank Appel - ClassnameFilters
 ******************************************************************************/
package com.codeaffine.osgi.testsuite.internal;

import java.util.Properties;

import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BundleTestCollector {

  private final ClassnameFilter classnameFilter;
  private final String bundleSymbolicName;
  private final BundleContext bundleContext;
  private final Properties devProperties;

  public BundleTestCollector( BundleContext bundleContext,
                              String bundleSymbolicName,
                              String[] filterPatterns )
  {
    this.bundleContext = bundleContext;
    this.bundleSymbolicName = bundleSymbolicName;
    this.classnameFilter = new ClassnameFilter( filterPatterns );
    this.devProperties = new DevPropertiesLoader( bundleContext ).load();
  }

  public Class<?>[] collect() throws InitializationError {
    return new ClassPathScanner( getBundle(), devProperties, classnameFilter ).scan();
  }

  private Bundle getBundle() throws InitializationError {
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
}