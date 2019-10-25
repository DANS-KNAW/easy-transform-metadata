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

import nl.knaw.dans.easy.transform.AccessRights.AccessRights

import scala.xml.transform.{ RewriteRule, RuleTransformer }
import scala.xml.{ Elem, Node }

object XmlTransformation {

  private val accessibleToRightsMap = Map(
    AccessRights.OPEN_ACCESS -> AccessibleToRights.ANONYMOUS,
    AccessRights.REQUEST_PERMISSION -> AccessibleToRights.RESTRICTED_REQUEST,
    AccessRights.NO_ACCESS -> AccessibleToRights.NONE)

  def enrichFilesXml(filesXml: Node, datasetXml: Node, downloadUrl: URI): Node = {
    val rule = new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "file" => enrichFileElement(elem, datasetXml, downloadUrl)
        case _ => n
      }
    }
    new RuleTransformer(rule).transform(filesXml).head
  }

  private def enrichFileElement(file: Elem, datasetXml: Node, downloadUrl: URI): Elem = {
    val accessibleToRights = file \\ "accessibleToRights"
    val visibleToRights = file \\ "visibleToRights"
    var enriched = file

    if (accessibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getAccessibleToRightsElement(datasetXml))

    if (visibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getVisibleToRightsElement())

    addDownloadUrl(enriched, downloadUrl)
  }

  private def getAccessibleToRightsElement(datasetXml: Node): Elem = {
    val accessRights: AccessRights = AccessRights.withName((datasetXml \\ "accessRights").text)
    <accessibleToRights>{accessibleToRightsMap(accessRights)}</accessibleToRights>
  }

  private def getVisibleToRightsElement(): Elem = {
    <visibleToRights>{VisibleToRights.ANONYMOUS}</visibleToRights>
  }

  private def addDownloadUrl(file: Elem, downloadUrl: URI) = {
    file.copy(child = file.child ++ <dcterms:source>{downloadUrl.resolve((file \ "@filepath").text)}</dcterms:source>)
  }
}
