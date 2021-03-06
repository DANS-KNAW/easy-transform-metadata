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
import java.nio.file.Paths

import nl.knaw.dans.easy.transform.AccessRights.AccessRights
import nl.knaw.dans.lib.encode.PathEncoding

import scala.xml.transform.{ RewriteRule, RuleTransformer }
import scala.xml.{ Elem, Node, XML }

object XmlTransformation {

  private val accessibleToRightsMap = Map(
    AccessRights.OPEN_ACCESS -> AccessibleToRights.ANONYMOUS,
    AccessRights.OPEN_ACCESS_FOR_REGISTERED_USERS -> AccessibleToRights.KNOWN,
    AccessRights.REQUEST_PERMISSION -> AccessibleToRights.RESTRICTED_REQUEST,
    AccessRights.NO_ACCESS -> AccessibleToRights.NONE)

  def enrichFilesXml(bagId: BagId, filesXml: Node, datasetXml: Node, downloadUrl: URI): Node = {
    val dctermsNameSpace = getDctermsNamespace(filesXml)
    val accessRights = getAccessRights(datasetXml)
    val rule = new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "file" => enrichFileElement(bagId, elem, dctermsNameSpace, accessRights, downloadUrl)
        case _ => n
      }
    }
    new RuleTransformer(rule).transform(filesXml).head
  }

  private def enrichFileElement(bagId: BagId, file: Elem, dcNameSpace: String, accessRights: AccessRights, downloadUrl: URI): Elem = {
    val accessibleToRights = file \\ "accessibleToRights"
    val visibleToRights = file \\ "visibleToRights"
    var enriched = file

    if (accessibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getAccessibleToRightsElement(accessRights))

    if (visibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getVisibleToRightsElement())

    addDownloadUrl(bagId, enriched, downloadUrl, dcNameSpace)
  }

  private def getAccessibleToRightsElement(accessRights: AccessRights): Elem = {
    <accessibleToRights>{accessibleToRightsMap(accessRights)}</accessibleToRights>
  }

  private def getVisibleToRightsElement(): Elem = {
    <visibleToRights>{VisibleToRights.ANONYMOUS}</visibleToRights>
  }

  private def addDownloadUrl(bagId: BagId, file: Elem, downloadUrl: URI, dcNameSpace: String) = {
    val escapedFilePath = Paths.get((file \ "@filepath").text).escapePath
    val downloadPath = downloadUrl.resolve(s"$bagId/$escapedFilePath")
    val sourceElement = XML.loadString(s"""<$dcNameSpace:source>$downloadPath</$dcNameSpace:source>""")
    file.copy(child = file.child ++ sourceElement)
  }

  private def getDctermsNamespace(filesXml: Node): String = {
    Option(filesXml.scope.getPrefix("http://purl.org/dc/terms/")).getOrElse("dcterms")
  }

  private def getAccessRights(datasetXml: Node): AccessRights = {
    AccessRights.withName((datasetXml \ "profile" \ "accessRights").text)
  }
}
