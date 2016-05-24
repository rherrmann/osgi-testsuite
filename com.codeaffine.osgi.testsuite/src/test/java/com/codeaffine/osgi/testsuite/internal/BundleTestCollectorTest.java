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
package com.codeaffine.osgi.testsuite.internal;

import static com.codeaffine.osgi.testsuite.ClassnameFilters.DEFAULT_CLASSNAME_FILTERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;


public class BundleTestCollectorTest {

  private BundleContext bundleContext;

  @Test
  public void testCollectWithNonExistingBundle() {
    String nonExistingBundle =  "no.bundle";
    BundleTestCollector collector = new BundleTestCollector( bundleContext, nonExistingBundle, DEFAULT_CLASSNAME_FILTERS );

    try {
      collector.collect();
      fail();
    } catch( InitializationError expected ) {
      assertTrue( expected.getCauses().get( 0 ).getMessage().contains( nonExistingBundle ) );
    }
  }

  @Test
  public void testCollectWithExistingBundle() throws InitializationError {
    String bundle = createBundle().getSymbolicName();

    BundleTestCollector collector = new BundleTestCollector( bundleContext, bundle, DEFAULT_CLASSNAME_FILTERS );
    Class<?>[] classes = collector.collect();

    assertEquals( 0, classes.length );
  }

  @Before
  public void setUp() {
    bundleContext = mock( BundleContext.class );
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

}
