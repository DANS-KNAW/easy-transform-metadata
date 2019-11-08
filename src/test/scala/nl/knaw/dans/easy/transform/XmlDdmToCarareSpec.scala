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

import java.io.{ StringReader, StringWriter }
import java.net.URI

import better.files.File
import javax.xml.XMLConstants
import javax.xml.transform.stream.{ StreamResult, StreamSource }
import javax.xml.transform.{ Source, Transformer, TransformerFactory }
import javax.xml.validation.{ Schema, SchemaFactory, Validator }
import nl.knaw.dans.easy.transform.fixture.TestSupportFixture
import org.scalatest.BeforeAndAfterEach

import scala.xml.{ Node, PrettyPrinter, XML }

class XmlDdmToCarareSpec extends TestSupportFixture with BeforeAndAfterEach {

  private val dataset = (metadataDir / "metadata_PAN/dataset.xml").toJava
  private val file = (metadataDir / "metadata_PAN/files.xml").toJava
  private val downloadUrl = new URI("https://download/location/")
  private val ddmToCarareXSL = "src/main/assembly/dist/res/pan_ddm_carare.xsl"
  private val carareXSD = "src/main/resources/carare-v2.0.6.xsd"

  val factory: TransformerFactory = TransformerFactory.newInstance()
  val xslt = new StreamSource(File(ddmToCarareXSL).toJava)
  val transformer: Transformer = factory.newTransformer(xslt)
  private lazy val prettyPrinter = new PrettyPrinter(160, 2)

  override def beforeEach(): Unit = {
    super.beforeEach()
    metadataDir.clear()
    File(getClass.getResource("/metadata/").toURI).copyTo(metadataDir)
  }

  "transform" should "produce an XML-file in Carare format, and it should validate against Carare schema 2.0.6" in {
    val datasetXml = XML.loadFile(dataset)
    val filesXml = XML.loadFile(file)

    val output = new StringWriter()
    val upgradedFilesXml = XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl)
    val wrappedXml = XmlWrapper.wrap(datasetXml, upgradedFilesXml)
    val input: Source = new StreamSource(new StringReader(wrappedXml.toString()))
    transformer.transform(input, new StreamResult(output))
    val result = XML.loadString(output.toString)
    Console.err.println(prettyPrinter.format(result))
    validate(result, File(carareXSD))
  }

  private def validate(xmlFile: Node, xsdFile: File): Unit = {
    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema: Schema = schemaFactory.newSchema(xsdFile.toJava)
    val validator: Validator = schema.newValidator()
    val xml = new StreamSource(new StringReader(xmlFile.toString()))
    validator.validate(xml)
  }
}
