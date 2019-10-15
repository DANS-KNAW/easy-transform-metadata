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
package nl.knaw.dans.easy.transform.bagstore

import java.net.{ URI, URL }

import nl.knaw.dans.easy.transform.{ Configuration, DatasetId, HttpStatusException }
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import scalaj.http.BaseHttp

import scala.language.postfixOps
import scala.util.{ Failure, Success, Try }
import scala.xml.{ Elem, XML }

abstract class BagStore(configuration: Configuration) extends DebugEnhancedLogging {

  val baseUrl: URI = configuration.bagStoreConfig.baseURL
  object Http extends BaseHttp(userAgent = s"easy-transform-metadata/${ configuration.version }")

  def loadDatasetXml(bagId: DatasetId): Try[Elem] = {
    loadXml(new URL(baseUrl.toString + s"bags/$bagId/metadata/dataset.xml"))
  }

  def loadFilesXml(bagId: DatasetId): Try[Elem] = {
    loadXml(new URL(baseUrl.toString + s"bags/$bagId/metadata/files.xml"))
  }

  private def loadXml(url: URL): Try[Elem] = {
    for {
      response <- Try { Http(url.toString).method("GET").asString }
      _ <- if (response.isSuccess) Success(())
           else Failure(HttpStatusException(url.toString, response))
    } yield XML.loadString(response.body)
  }
}
