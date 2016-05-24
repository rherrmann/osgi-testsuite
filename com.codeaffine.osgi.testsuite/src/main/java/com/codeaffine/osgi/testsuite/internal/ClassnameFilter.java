/*******************************************************************************
 * Copyright (c) 2012, 2015 Rüdiger Herrmann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - ClassnameFilters
 ******************************************************************************/
package com.codeaffine.osgi.testsuite.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ClassnameFilter {
  private final Collection<String> positiveFilters;
  private final Collection<String> negationFilters;

  public ClassnameFilter( String... filterPatterns ) {
    this.positiveFilters = findPositiveFilters( filterPatterns );
    this.negationFilters = findNegationFilters( filterPatterns );
  }

  public boolean accept( String className ) {
    boolean result = false;
    if( !matchesNegationFilters( className )
        && matchesPositiveFilters( className ) ) {
      result = true;
    }
    return result;
  }

  private boolean matchesNegationFilters( String className ) {
    boolean result = false;
    for( Iterator<String> iterator = negationFilters.iterator(); !result && iterator.hasNext(); ) {
      String pattern = iterator.next();
      if( className.matches( pattern ) ) {
        result = true;
      }
    }
    return result;
  }

  private boolean matchesPositiveFilters( String className ) {
    boolean result = positiveFilters.isEmpty();
    for( Iterator<String> iterator = positiveFilters.iterator(); !result && iterator.hasNext(); ) {
      String pattern = iterator.next();
      if( className.matches( pattern ) ) {
        result = true;
      }
    }
    return result;
  }

  private static Collection<String> findPositiveFilters( String[] filterPatterns ) {
    Collection<String> result = new ArrayList<String>();
    for( String pattern : filterPatterns ) {
      if( !pattern.startsWith( "!" ) ) {
        result.add( pattern );
      }
    }
    return result;
  }

  private static Collection<String> findNegationFilters( String[] filterPatterns ) {
    Collection<String> result = new ArrayList<String>();
    for( String pattern : filterPatterns ) {
      if( pattern.startsWith( "!" ) ) {
        result.add( pattern.substring( 1 ) );
      }
    }
    return result;
  }
}