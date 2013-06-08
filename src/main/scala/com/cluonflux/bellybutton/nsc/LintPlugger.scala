package com.cluonflux.bellybutton.nsc

import scala.collection.JavaConverters._

import scala.tools.nsc.plugins.{PluginComponent, Plugin}
import scala.tools.nsc.{Global, Phase}
import com.cluonflux.bellybutton.api.LintPlugin

class LintPlugger(val global: Global) extends Plugin {
  import global._

  val name = "Belly Button"
  val components = List(Component)
  val description = "Loads Lint libraries from the classpath so they can flame on your code"

  private object Component extends PluginComponent {
    val global = LintPlugger.this.global
    val phaseName = LintPlugger.this.name
    val runsAfter = List("refchecks")

    def newPhase(prev: Phase) = new LintyPhase(prev)

    class LintyPhase(prev: Phase) extends StdPhase(prev) {
      val plugins: Seq[LintPlugin] = {
        // TODO which classloader to use here?
        val cl = getClass.getClassLoader

        var enabled = Set[String]()
        var disabled = Set[String]()

        settings.pluginOptions.value.foreach {
          case enable(x)  => enabled += x
          case disable(x) => disabled += x
        }

        val urls = cl.getResources("/META-INF/services/com.cluonflux.bellybutton.LintPlugin").asScala
        for (url <- urls) {
          val (mSrc, classNames) = try {
            val is = url.openStream()
            val source = scala.io.Source.fromInputStream(is, "UTF-8")
            val names = source.getLines().filterNot(_.startsWith("#"))
            (Some(source), names.toSeq)
          } catch {
            case e: Exception =>
              reporter.echo(s"Unable to load LintPlugin from $url: ${e.getMessage}")
              e.printStackTrace()
              (None, Nil)
          }

          mSrc match {
            case None =>

            case Some(src) =>
              try {
                for (classname <- classNames) yield {
                  try {
                    val clazz = cl.loadClass(classname).asInstanceOf[Class[LintPlugin]]
                    val plugin = clazz.newInstance()

                    val shorty = plugin.shortName
                    val enabledByDefault = plugin.enabledByDefault

                  }
                }
              } finally {
                src.close()
              }
          }


        }
      }

      def apply(unit: global.CompilationUnit) {

      }
    }

    val enable = """^bellybutton:+(.*?)$""".r
    val disable = """^bellybutton:-(.*?)$""".r
  }

}

