/*******************************************************************************
 * Copyright (c) 2012, 2013, 2014 Rüdiger Herrmann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - ClassnameFilters
 ******************************************************************************/
package com.codeaffine.osgi.testuite.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.codeaffine.osgi.testuite.internal.ClassnameFilter;

public class ClassnameFilterTest {

  @Test
  public void testAccept() {
    ClassnameFilter filter = new ClassnameFilter();

    boolean actual = filter.accept( "com.codeaffine.Foo" );

    assertTrue( actual );
  }

  @Test
  public void testAcceptWithMatchingInclusionPattern() {
    ClassnameFilter filter = new ClassnameFilter( ".*Foo" );

    boolean actual = filter.accept( "com.codeaffine.Foo" );

    assertTrue( actual );
  }

  @Test
  public void testAcceptWithNonMatchingInclusionPattern() {
    ClassnameFilter filter = new ClassnameFilter( ".*Foo" );

    boolean actual = filter.accept( "com.codeaffine.Bar" );

    assertFalse( actual );
  }

  @Test
  public void testAcceptWithMatchingExclusionPattern() {
    ClassnameFilter filter = new ClassnameFilter( "!.*Foo" );

    boolean actual = filter.accept( "com.codeaffine.Foo" );

    assertFalse( actual );
  }

  @Test
  public void testAcceptWithNonMatchingExclusionPattern() {
    ClassnameFilter filter = new ClassnameFilter( "!.*Foo" );

    boolean actual = filter.accept( "com.codeaffine.Bar" );

    assertTrue( actual );
  }

  @Test
  public void testAcceptWithMultiplePatterns() {
    ClassnameFilter filter = new ClassnameFilter( ".*", "!.*Foo" );

    boolean excluded = filter.accept( "com.codeaffine.Foo" );
    boolean included = filter.accept( "com.codeaffine.Bar" );

    assertFalse( excluded );
    assertTrue( included );
  }
}