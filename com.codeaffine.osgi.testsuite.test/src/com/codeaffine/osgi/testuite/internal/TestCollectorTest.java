package com.codeaffine.osgi.testuite.internal;

import static com.codeaffine.osgi.testuite.BundleTestSuite.ClassnameFilters.DEFAULT_CLASSNAME_FILTERS;
import static com.codeaffine.osgi.testuite.BundleTestSuite.NoMatchPolicy.FAIL;
import static com.codeaffine.osgi.testuite.BundleTestSuite.NoMatchPolicy.IGNORE;
import static com.codeaffine.osgi.testuite.BundleTestSuite.NoMatchPolicy.WARN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.InitializationError;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.wiring.BundleWiring;

import com.codeaffine.osgi.testuite.BundleTestSuite.TestBundles;

public class TestCollectorTest {

  private static final String BUNDLE_SYMBOLIC_NAME = "bundle.symbolic.name";

  private BundleContext bundleContext;
  private ByteArrayOutputStream outputStream;

  @Test
  public void testCollectWithDefaultNoMatchPolicy() throws InitializationError {
    TestBundles testBundles = NoMatchTestSuite_DefaultPolicy.class.getAnnotation( TestBundles.class );

    Class<?>[] classes = collect( testBundles );

    assertEquals( 0, classes.length );
    assertPrintStreamIsEmpty();
  }

  @Test
  public void testCollectWithIgnoreNoMatchPolicy() throws InitializationError {
    TestBundles testBundles = NoMatchTestSuite_IgnorePolicy.class.getAnnotation( TestBundles.class );

    Class<?>[] classes = collect( testBundles );

    assertEquals( 0, classes.length );
    assertPrintStreamIsEmpty();
  }

  @Test
  public void testCollectWithWarnNoMatchPolicy() throws InitializationError {
    TestBundles testBundles = NoMatchTestSuite_WarnPolicy.class.getAnnotation( TestBundles.class );

    Class<?>[] classes = collect( testBundles );

    assertEquals( 0, classes.length );
    assertPrintStreamIsNotEmpty();
  }

  @Test
  public void testCollectWithFailNoMatchPolicy() {
    TestBundles testBundles = NoMatchTestSuite_FailPolicy.class.getAnnotation( TestBundles.class );

    try {
      collect( testBundles );
      fail();
    } catch( InitializationError expected ) {
      assertPrintStreamIsEmpty();
    }

  }

  @Before
  public void setUp() {
    bundleContext = createBundleContext();
    outputStream = new ByteArrayOutputStream();
  }

  private static BundleContext createBundleContext() {
    BundleContext result = mock( BundleContext.class );
    Bundle bundle = mock( Bundle.class );
    when( bundle.getSymbolicName() ).thenReturn( BUNDLE_SYMBOLIC_NAME );
    BundleWiring bundleWiring = mock( BundleWiring.class );
    when( bundle.adapt( BundleWiring.class ) ).thenReturn( bundleWiring );
    when( result.getBundles() ).thenReturn( new Bundle[] { bundle } );
    return result;
  }

  private void assertPrintStreamIsEmpty() {
    assertEquals( 0, outputStream.toByteArray().length );
  }

  private void assertPrintStreamIsNotEmpty() {
    assertTrue( outputStream.toByteArray().length > 0 );
  }

  private Class<?>[] collect( TestBundles testBundles ) throws InitializationError {
    PrintStream printStream = new PrintStream( outputStream, true );
    return new TestCollector( printStream, bundleContext, testBundles, DEFAULT_CLASSNAME_FILTERS ).collect();
  }

  @TestBundles(value = BUNDLE_SYMBOLIC_NAME)
  private static class NoMatchTestSuite_DefaultPolicy {
  }

  @TestBundles(value = BUNDLE_SYMBOLIC_NAME, noMatchPolicy = IGNORE)
  private static class NoMatchTestSuite_IgnorePolicy {
  }

  @TestBundles(value = BUNDLE_SYMBOLIC_NAME, noMatchPolicy = WARN)
  private static class NoMatchTestSuite_WarnPolicy {
  }

  @TestBundles(value = BUNDLE_SYMBOLIC_NAME, noMatchPolicy = FAIL)
  private static class NoMatchTestSuite_FailPolicy {
  }

}
