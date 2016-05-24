package com.codeaffine.osgi.testsuite;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
  String[] DEFAULT_CLASSNAME_FILTERS = new String[] { ".*Test" };

  public String[] value() default { ".*Test" };
}