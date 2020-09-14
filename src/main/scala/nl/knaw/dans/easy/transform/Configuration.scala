/**
 * Copyright (C) 2019 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy.transform

import java.net.URI

import better.files.File
import better.files.File.root
import nl.knaw.dans.easy.transform.Command.{ configuration, logger }
import nl.knaw.dans.easy.transform.bagstore.BagStoreConfig
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import org.apache.commons.configuration.PropertiesConfiguration

case class Configuration(version: String,
                         bagStoreConfig: BagStoreConfig,
                         downloadURL: URI,
                        )

object Configuration extends DebugEnhancedLogging {

  def apply(home: File): Configuration = {
    val cfgPath = Seq(
      root / "etc" / "opt" / "dans.knaw.nl" / "easy-transform-metadata",
      home / "cfg")
      .find(_.exists)
      .getOrElse { throw new IllegalStateException("No configuration directory found") }
    val properties = new PropertiesConfiguration() {
      setDelimiterParsingDisabled(true)
      load((cfgPath / "application.properties").toJava)
    }
    val version = (home / "bin" / "version").contentAsString.stripLineEnd
    val agent = properties.getString("http.agent",s"easy-validate-dans-bag/$version")
    logger.info(s"setting http.agent to $agent")
    System.setProperty("http.agent", agent)

    new Configuration(
      version,
      bagStoreConfig = BagStoreConfig(
        baseURL = new URI(properties.getString("bagstore.baseurl")),
        connectTimeout = properties.getLong("bagstore.timeout_ms.connect"),
        readTimeout = properties.getLong("bagstore.timeout_ms.read"),
      ),
      downloadURL = new URI(properties.getString("download.baseurl")),
    )
  }
}
