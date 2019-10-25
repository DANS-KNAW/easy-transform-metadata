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

import scala.xml.Node

object XmlWrapper {

//  private val prolog = <?xml version="1.0" encoding="UTF-8"?>
  private val xml =
    <bag:bagmetadata
        xmlns:bag="http://easy.dans.knaw.nl/bag/metadata/bagmetadata/"
        xmlns:files="http://easy.dans.knaw.nl/schemas/bag/metadata/files/"
        xmlns:ddm="http://easy.dans.knaw.nl/schemas/md/ddm/"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://easy.dans.knaw.nl/bag/metadata/bagmetadata/ https://easy.dans.knaw.nl/schemas/bag/metadata/bagmetadata.xsd/">
    </bag:bagmetadata>

  def wrap(datasetXml: Node, filesXml: Node): Node = {
    xml.copy(child = xml.child ++ datasetXml ++ filesXml)
  }
}
