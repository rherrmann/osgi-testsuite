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
package com.codeaffine.osgi.testuite;

import static com.codeaffine.osgi.testuite.BundleTestSuite.ClassnameFilters.DEFAULT_CLASSNAME_FILTERS;
import static java.lang.String.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

/**
 * The BundleTestSuite test runner can be used to run all tests within a given number of OSGi
 * bundles.
 * <p>
 * To use it, annotate a class with <code>@RunWith(BundleTestSuite.class)</code> and
 * <code>@TestBundles({"bundle.1", ...})</code>.  When you run this class, it will run all the
 * tests named with a <code>Test</code> postfix in all the bundles.
 * </p>
 * <p>
 * Example:
 * <pre>
 *   @RunWith( BundleTestSuite.class )
 *   @TestBundles( { "org.example.bundle1", "org.example.bundle2" } )
 *   public class MasterTestSuite {
 *   }
 * </pre>
 * </p>
 *
 * <p>
 * An <code>InitializationError</code> is thrown if the <code>@TestBundles</code> annotation is
 * missing or if it list bundles that cannot be found.
 * </p>
 *
 * <p>
 * A filtering mechanism for selecting test cases to run is provided by the
 * <code>@ClassnameFilters({"filterExpression", ...})</code> annotation. It
 *  uses regular expressions to match test name patterns.
 * </p>
 * <p>
 * Example:
 * <pre>
 *   @RunWith( BundleTestSuite.class )
 *   @TestBundles( { "org.example.bundle1", "org.example.bundle2" } )
 *   @ClassnameFilters( { ".*IntegrationTest", "!.*FooIntegrationTest" } )
 *   public class IntegrationTestSuite {
 *   }
 * </pre>
 * The example runs all test cases in bundle1 and bundle2 that are named with the postfix <code>IntegrationTest</code>
 * but exclude those with the postfix <code>FooIntegrationTest</code>.
 * </p>
 *
 * @see RunWith
 * @see TestBundles
 * @since 1.0
 */
public class BundleTestSuite extends Suite {

  /**
   * The <code>TestBundles</code> annotation specifies the bundles to be scanned for test classes
   * when a class annotated with <code>@RunWith(BundleTestSuite.class)</code> is run.
   *
   * <p>A test class is identified by its name. All public classes whose names end with 'Test' are
   * considered test classes.</p>
   */
  @Retention( RetentionPolicy.RUNTIME )
  @Target( ElementType.TYPE )
  public @interface TestBundles {

    /**
     * @return the synblic names of the bundles that should be scanned for tests
     */
    String[] value();
  }

  /**
   * The <code>ClassnameFilters</code> annotation specifies a set of regex expressions for all test
   * classes (ie. their qualified names) to be included in a test run. When the annotation
   * is missing, all test classes that matches the expression <code>.*Test</code> in all referred
   * bundles and packages will be run.
   *
   * <p>Use a leading <code>!</code> for an exclusion expression. Exclusion expressions can be use
   * to prevent certain Tests from execution as in 'run all unit tests (<code>".*Test"</code>) but
   * not the integration tests (<code>"!.*ITest"</code>)'.
   * </p>
   * @since 1.1
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  public @interface ClassnameFilters {
    static final String[] DEFAULT_CLASSNAME_FILTERS = new String[] { ".*Test" };

    public String[] value() default { ".*Test" };
  }


  public BundleTestSuite( Class<?> type ) throws InitializationError {
    super( type, getTestClasses( type ) );
  }

  @Override
  protected void runChild( Runner runner, RunNotifier notifier ) {
    super.runChild( runner, notifier );
  }

  private static Class<?>[] getTestClasses( Class<?> type ) throws InitializationError {
    TestBundles bundlesAnnotation = type.getAnnotation( TestBundles.class );
    checkAnnotationExists( type, bundlesAnnotation );
    return new TestCollector( bundlesAnnotation.value(), readFilterPatterns( type ) ).collect();
  }

  private static void checkAnnotationExists( Class<?> type, TestBundles testBundles )
    throws InitializationError
  {
    if( testBundles == null ) {
      String msg = format( "Class '%s' must have a TestBundles annotation", type.getName() );
      throw new InitializationError( msg );
    }
  }

  private static String[] readFilterPatterns( Class<?> type ) {
    String[] result = DEFAULT_CLASSNAME_FILTERS;
    if( type.getAnnotation( ClassnameFilters.class ) != null ) {
      result = type.getAnnotation( ClassnameFilters.class ).value();
    }
    checkPatternSyntax( result );
    return result;
  }

  private static void checkPatternSyntax( String[] result ) {
    for( String pattern : result ) {
      Pattern.compile( pattern );
    }
  }
}