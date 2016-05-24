package com.codeaffine.osgi.testsuite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.codeaffine.osgi.testsuite.BundleTestSuite.NoMatchPolicy;

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

  /**
   * @return the policy that determines how bundles without matching tests are treated
   *
   * @see NoMatchPolicy
   * @since 1.2
   */
  NoMatchPolicy noMatchPolicy() default NoMatchPolicy.IGNORE;
}