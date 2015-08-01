package com.codeaffine.osgi.testuite.internal;

import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.runners.model.InitializationError;
import org.osgi.framework.BundleContext;

import com.codeaffine.osgi.testuite.BundleTestSuite.NoMatchPolicy;
import com.codeaffine.osgi.testuite.BundleTestSuite.TestBundles;


public class TestCollector {
  private final PrintStream printStream;
  private final BundleContext bundleContext;
  private final TestBundles testBundlesAnnotation;
  private final String[] classNameFilters;

  public TestCollector( PrintStream printStream,
                        BundleContext bundleContext,
                        TestBundles testBundlesAnnotation,
                        String...classNameFilters )
  {
    this.printStream = printStream;
    this.bundleContext = bundleContext;
    this.testBundlesAnnotation = testBundlesAnnotation;
    this.classNameFilters = classNameFilters;
  }

  public Class<?>[] collect() throws InitializationError {
    Collection<Class<?>> allTestClasses = new ArrayList<Class<?>>();
    for( String bundleSymbolicName : testBundlesAnnotation.value() ) {
      Class<?>[] testClasses = collect( bundleSymbolicName );
      allTestClasses.addAll( asList( testClasses ) );
    }
    return allTestClasses.toArray( new Class<?>[ allTestClasses.size() ] );
  }

  private Class<?>[] collect( String bundleSymbolicName ) throws InitializationError {
    Class<?>[] testClasses = new BundleTestCollector( bundleContext, bundleSymbolicName, classNameFilters ).collect();
    handleEmptyTestBundlePolicy( bundleSymbolicName, testClasses );
    return testClasses;
  }

  private void handleEmptyTestBundlePolicy( String bundleSymbolicName, Class<?>[] testClasses )
    throws InitializationError
  {
    if( testClasses.length == 0 ) {
      NoMatchPolicy policy = testBundlesAnnotation.noMatchPolicy();
      switch( policy ) {
        case IGNORE:
          break;
        case WARN:
          printStream.println( getNoMatchMessage( bundleSymbolicName ) );
          break;
        case FAIL:
          throw new InitializationError( getNoMatchMessage( bundleSymbolicName ) );
        default:
          throw new UnsupportedOperationException( "Unsupported NoMatchPolicy: " + policy );
      }
    }
  }

  private static String getNoMatchMessage( String bundleSymbolicName ) {
    return format( "No matching tests found in bundle: %s", bundleSymbolicName );
  }
}
