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

class XmlTransformationSpec extends TestSupportFixture with BeforeAndAfterEach {

  private val xmlTransformation = new XmlTransformation()
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

  "enrichFilesXml" should "leave the first file element as it was" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val origSizeFirstFileElement = (filesXml \ "file").head.child.size
    val firstFileElement = (xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file").head
    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
    firstFileElement.child.size shouldBe origSizeFirstFileElement
  }

  it should "add accessibleToRights element with value 'ANONYMOUS' to the second file element, when dataset accessRights is OPEN_ACCESS" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val secondFileElement = (xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (secondFileElement \ "accessibleToRights").text shouldBe "ANONYMOUS"
  }

  it should "add accessibleToRights element with value 'RESTRICTED_REQUEST' to the second file element, when dataset accessRights is REQUEST_PERMISSION" in {
    val filesXml = XML.loadFile(files_request)
    val datasetXml = XML.loadFile(dataset_request)
    val secondFileElement = (xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (secondFileElement \ "accessibleToRights").text shouldBe "RESTRICTED_REQUEST"
  }

  it should "add accessibleToRights element with value 'NONE' to the second file element, when dataset accessRights is NO_ACCESS" in {
    val filesXml = XML.loadFile(files_no)
    val datasetXml = XML.loadFile(dataset_no)
    val secondFileElement = (xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (secondFileElement \ "accessibleToRights").text shouldBe "NONE"
  }

  it should "add visibleToRights element with value 'ANONYMOUS' to the second file element" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val secondFileElement = (xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
  }

  it should "replace value of filepath attribute with a download path, for all file elements" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val firstFileElement = (xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file").head
    val secondFileElement = (xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    val filepathAttribute_1 = (firstFileElement \ "@filepath").text
    val filepathAttribute_2 = (secondFileElement \ "@filepath").text
    filepathAttribute_1 shouldBe "https://download/location/data/path/to/file.txt"
    filepathAttribute_2 shouldBe "https://download/location/data/quicksort.hs"
  }

  it should "return as many nodes in the output xml as was in the original xml" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val origSize = filesXml.child.size
    xmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl).child.size shouldBe origSize
  }
}
