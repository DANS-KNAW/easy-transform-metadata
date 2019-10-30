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

import java.io.{ OutputStreamWriter, StringReader, StringWriter, Writer }
import java.net.URI

import better.files.File
import javax.xml.XMLConstants
import javax.xml.transform.{ Result, Source, TransformerFactory }
import javax.xml.transform.stream.{ StreamResult, StreamSource }
import javax.xml.validation.{ Schema, SchemaFactory, Validator }
import nl.knaw.dans.easy.transform.fixture.TestSupportFixture
import org.scalatest.BeforeAndAfterEach
import resource.{ ManagedResource, managed }

import scala.util.Try
import scala.xml.{ Node, PrettyPrinter, XML }

class XmlDdmToCarareSpec extends TestSupportFixture with BeforeAndAfterEach {

  private val dataset_open = (metadataDir / "metadata_OPEN_ACCESS/dataset.xml").toJava
  private val files_open = (metadataDir / "metadata_OPEN_ACCESS/files.xml").toJava
  private val bagmetadataSchema = "src/main/resources/bagmetadata.xsd"
  private val downloadUrl = new URI("https://download/location/")
  private val ddmToCarareXSL = "src/main/resources/ddm_carare.xsl"
  private val carareXSD = "src/main/resources/carare-v2.0.1.xsd"

  val factory = TransformerFactory.newInstance()
  val xslt = new StreamSource(File(ddmToCarareXSL).toJava)
  val transformer = factory.newTransformer(xslt)
  private lazy val prettyPrinter = new PrettyPrinter(160, 2)

  override def beforeEach(): Unit = {
    super.beforeEach()
    metadataDir.clear()
    File(getClass.getResource("/metadata/").toURI).copyTo(metadataDir)
  }

  "transform" should "..." in {

    val datasetXml = XML.loadFile(dataset_open)
    val filesXml = XML.loadFile(files_open)

    val output = new StringWriter()
    val upgradedFilesXml = XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl)
    val wrappedXml = XmlWrapper.wrap(datasetXml, upgradedFilesXml)
    val input: Source = new StreamSource(new StringReader(wrappedXml.toString()))
    val resultXml = transformer.transform(input, new StreamResult(output))
    val strResult = output.toString
    Console.err.println(strResult)
    validate(XML.loadString(strResult), File(carareXSD))
  }

//  it should "validate the wrapping XML against bagmetadata schema" in {
//    val datasetXml = XML.loadFile(dataset_open)
//    val filesXml = XML.loadFile(files_open)
//    val result = XmlWrapper.wrap(datasetXml, filesXml)
//
//    validate(result, File(bagmetadataSchema))
//  }
//
  private def validate(xmlFile: Node, xsdFile: File): Unit = {
    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema: Schema = schemaFactory.newSchema(xsdFile.toJava)
    val validator: Validator = schema.newValidator()
    val xml = new StreamSource(new StringReader(xmlFile.toString()))
    validator.validate(xml)
  }
}
