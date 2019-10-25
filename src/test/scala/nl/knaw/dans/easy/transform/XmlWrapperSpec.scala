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
import nl.knaw.dans.easy.transform.fixture.TestSupportFixture
import org.scalatest.BeforeAndAfterEach

import scala.xml.XML

class XmlWrapperSpec extends TestSupportFixture with BeforeAndAfterEach {

  private val files_open = (metadataDir / "metadata_OPEN_ACCESS/files.xml").toJava
  private val files_request = (metadataDir / "metadata_REQUEST_PERMISSION/files.xml").toJava
  private val files_no = (metadataDir / "metadata_NO_ACCESS/files.xml").toJava
  private val dataset_open = (metadataDir / "metadata_OPEN_ACCESS/dataset.xml").toJava
  private val dataset_request = (metadataDir / "metadata_REQUEST_PERMISSION/dataset.xml").toJava
  private val dataset_no = (metadataDir / "metadata_NO_ACCESS/dataset.xml").toJava
  private val downloadUrl = new URI("https://download/location/")

  override def beforeEach(): Unit = {
    super.beforeEach()
    metadataDir.clear()
    File(getClass.getResource("/metadata/").toURI).copyTo(metadataDir)
  }

  "makeWrappingXml" should "..." in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    XmlWrapper.wrap(datasetXml,filesXml) shouldBe "a"
  }
}

