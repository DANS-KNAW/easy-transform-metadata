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
import scala.xml.{ Attribute, Elem, Node, Null }

class XmlTransformation() {

  private val accessibleToRightsMap = Map(OPEN_ACCESS -> ANONYMOUS, REQUEST_PERMISSION -> RESTRICTED_REQUEST, NO_ACCESS -> "NONE")

  def enrichFilesXml(xml: Node, accessRights: AccessRights, downloadUrl: URI): Node = {
    val rule = new RewriteRule {
      override def transform(n: Node): Seq[Node] = n match {
        case elem: Elem if elem.label == "file" => enrichFileElement(elem, accessRights, downloadUrl)
        case _ => n
      }
    }
    new RuleTransformer(rule).transform(xml).head
  }

  private def enrichFileElement(file: Elem, accessRights: AccessRights, downloadUrl: URI): Elem = {
    val accessibleToRights = file \\ "accessibleToRights"
    val visibleToRights = file \\ "visibleToRights"
    var enriched = file

    if (accessibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getAccessibleToRightsElement(accessRights))

    if (visibleToRights.isEmpty)
      enriched = enriched.copy(child = enriched.child ++ getVisibleToRightsElement())

    replaceFilePathWithDownloadUrl(enriched, downloadUrl)
  }

  private def getAccessibleToRightsElement(accessRights: AccessRights): Elem = {
    <accessibleToRights>{accessibleToRightsMap(accessRights.toString)}</accessibleToRights>
  }

  private def getVisibleToRightsElement(): Elem = {
    <visibleToRights>ANONYMOUS</visibleToRights>
  }

  private def replaceFilePathWithDownloadUrl(file: Elem, downloadUrl: URI) = {
    val downloadLocation = downloadUrl.toString.stripSuffix("/") concat "/"
    file % Attribute(null, "filepath", downloadLocation + (file \ "@filepath").text, Null)
  }
}
