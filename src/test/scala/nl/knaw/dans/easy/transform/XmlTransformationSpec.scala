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

  "enrichFilesXml" should "leave in the first file element accessibleToRights and visibleToRights elements as they were" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val firstFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file").head
    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
  }

  it should "add a new <source> element in file elements" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val origSizeFirstFileElement = (filesXml \ "file").head.child.size
    val firstFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file").head
    val sourceElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
    sourceElement.child.size shouldBe 1
    firstFileElement.child.size shouldBe origSizeFirstFileElement + 1
  }

  it should "give download path as value for the new <source> element in all file elements" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val sourceElement_1 = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
    val sourceElement_2 = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file" \ "source") (1)
    sourceElement_1.text shouldBe "https://download/location/data/path/to/file%2Etxt"
    sourceElement_2.text shouldBe "https://download/location/data/quicksort%2Ehs"
  }

  it should "construct the download path correctly also when there are spaces in the filepath" in {
    val filesXml = XML.loadFile(files_request)
    val datasetXml = XML.loadFile(dataset_request)
    val sourceElement_1 = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file" \ "source").head
    val sourceElement_2 = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file" \ "source") (1)
    sourceElement_1.text shouldBe "https://download/location/data/path%20to%20file%2Etxt"
    sourceElement_2.text shouldBe "https://download/location/data/quicksort%2Ehs"
  }

  it should "add visibleToRights element with value 'ANONYMOUS' to the second file element" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val secondFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
  }

  it should "add accessibleToRights element with value 'ANONYMOUS' to the second file element, when dataset accessRights is OPEN_ACCESS" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val firstFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file").head
    val secondFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
    (secondFileElement \ "accessibleToRights").text shouldBe "ANONYMOUS"
    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
  }

  it should "add accessibleToRights element with value 'RESTRICTED_REQUEST' to the second file element, when dataset accessRights is REQUEST_PERMISSION" in {
    val filesXml = XML.loadFile(files_request)
    val datasetXml = XML.loadFile(dataset_request)
    val firstFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file").head
    val secondFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
    (secondFileElement \ "accessibleToRights").text shouldBe "RESTRICTED_REQUEST"
    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
  }

  it should "add accessibleToRights element with value 'NONE' to the second file element, when dataset accessRights is NO_ACCESS" in {
    val filesXml = XML.loadFile(files_no)
    val datasetXml = XML.loadFile(dataset_no)
    val firstFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file").head
    val secondFileElement = (XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl) \ "file") (1)
    (firstFileElement \ "accessibleToRights").text shouldBe "NONE"
    (firstFileElement \ "visibleToRights").text shouldBe "RESTRICTED_REQUEST"
    (secondFileElement \ "accessibleToRights").text shouldBe "NONE"
    (secondFileElement \ "visibleToRights").text shouldBe "ANONYMOUS"
  }

  it should "return as many nodes in the output xml as was in the original xml" in {
    val filesXml = XML.loadFile(files_open)
    val datasetXml = XML.loadFile(dataset_open)
    val origSize = filesXml.child.size
    XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl).child.size shouldBe origSize
  }
}
