package com.cluonflux.bellybutton.api

trait LintPlugin {
  /**
   * @return a short, yet hopefully globally unique, name by which your plugin can be enabled/disabled on the command line.
   */
  def shortName: String

  /**
   * Should the plugin be enabled by default when it's in the classpath?
   *
   * If true, the user must set -P:bellybutton:-foo (where "foo" is your `shortName`) to disable.
   * If false, the user must set -P:bellybutton:+foo (where "foo" is your `shortName`) to enable.
   *
   * @return true if the mere presence of this plugin in the classpath should enable it by default.
   */
  def enabledByDefault: Boolean
}
