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

  private val dataset = metadataDir / "metadata_PAN/dataset.xml"
  private val file = metadataDir / "metadata_PAN/files.xml"
  private val downloadUrl = new URI("https://download/location/")
  private val ddmToCarareXSL = "src/main/resources/pan_ddm_carare.xsl"
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
    val carareXml = transformToCarare(dataset, file)
    Console.err.println(prettyPrinter.format(carareXml))
    validate(carareXml, File(carareXSD))
  }

  it should "produce a Carare XML-file with a correct structure" in {
    val carareXml = transformToCarare(dataset, file)

    (carareXml \ "carare").head.child should have size 6
    carareXml \ "carare" \ "collectionInformation" should have size 1
    carareXml \ "carare" \ "heritageAssetIdentification" should have size 1
    carareXml \ "carare" \ "digitalResource" should have size 4
  }

  it should "produce a Carare XML-file with a correct id" in {
    val carareXml = transformToCarare(dataset, file)
    val id = carareXml \ "carare" \ "@id"

    id.text shouldBe "10.17026/dans-z74-c65r"
  }

  it should "produce a Carare XML-file with a correct collectionInformation contents" in {
    val carareXml = transformToCarare(dataset, file)
    val collectionInformation = carareXml \ "carare" \ "collectionInformation"

    (collectionInformation \ "title").text  shouldBe "Portable Antiquities of The Netherlands (DANS-PAN)"
    (collectionInformation \ "contacts" \ "organization").text  shouldBe "Data Archiving and Networked Services (DANS)"
    (collectionInformation \ "rights" \ "licence").text  shouldBe "http://creativecommons.org/licenses/by-nc-sa/4.0/"
    (collectionInformation \ "coverage" \ "spatial" \ "locationSet" \ "geopoliticalArea").text  shouldBe "the Netherlands"
  }

  it should "produce a Carare XML-file with a correct heritageAssetIdentification contents" in {
    val carareXml = transformToCarare(dataset, file)
    val heritageAssetIdentification = carareXml \ "carare" \ "heritageAssetIdentification"

    (heritageAssetIdentification \ "recordInformation" \ "id").text  shouldBe "10.17026/dans-z74-c65r"
    (heritageAssetIdentification \ "recordInformation" \ "creation" \ "date").text  shouldBe "2018-12-31"
    (heritageAssetIdentification \ "recordInformation" \ "language").head.text  shouldBe "eng"
    (heritageAssetIdentification \ "recordInformation" \ "language").head.attribute("lang").get.text shouldBe "dcterms:ISO639-2"
    (heritageAssetIdentification \ "appellation" \ "name").head.text  shouldBe "PAN-00009021 - open plain arm ring with single knobbed terminals"
    (heritageAssetIdentification \ "appellation" \ "id").head.text  shouldBe "10.17026/dans-z74-c65r"
    (heritageAssetIdentification \ "description").head.text  shouldBe "This find is registered at Portable Antiquities of the Netherlands with number PAN-00009021"
    (heritageAssetIdentification \ "generalType").head.text  shouldBe "Artefact"
    (heritageAssetIdentification \ "actors" \ "name").head.text  shouldBe "Portable Antiquities of the Netherlands"
    (heritageAssetIdentification \ "actors" \ "actorType").head.text  shouldBe "organization"
    (heritageAssetIdentification \ "actors" \ "roles").head.text  shouldBe "DataCurator"
    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.text  shouldBe "open plain arm ring with single knobbed terminals"
    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.attribute("namespace").get.text  shouldBe "https://data.cultureelerfgoed.nl/term/id/pan/PAN"
    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.attribute("termUID").get.text  shouldBe "https://data.cultureelerfgoed.nl/term/id/pan/01-04-02-01-07-03"
    (heritageAssetIdentification \ "characters" \ "heritageAssetType").head.attribute("term").get.text  shouldBe "open plain arm ring with single knobbed terminals"
    (heritageAssetIdentification \ "characters" \ "temporal" \ "displayDate").head.text  shouldBe "Early Roman Period A"
    (heritageAssetIdentification \ "characters" \ "materials").head.text  shouldBe "metal"
    (heritageAssetIdentification \ "spatial" \ "locationSet" \ "namedLocation").head.text  shouldBe "Zaltbommel"
    (heritageAssetIdentification \ "publicationStatement" \ "publisher").head.text  shouldBe "DANS/KNAW"
    (heritageAssetIdentification \ "rights" \ "copyrightCreditLine").head.text  shouldBe "Vrije Universiteit Amsterdam"
    (heritageAssetIdentification \ "rights" \ "accessRights").head.text  shouldBe "Open Access"
    (heritageAssetIdentification \ "rights" \ "licence").head.text  shouldBe "http://creativecommons.org/licenses/by-nc-sa/4.0/"
    (heritageAssetIdentification \ "rights" \ "europeanaRights").head.text  shouldBe "Creative Commons - Attribution, Non-Commercial, ShareAlike (BY-NC-SA)"
    (heritageAssetIdentification \ "references" \ "appellation" \ "name").head.text  shouldBe "Portable Antiquities of The Netherlands"
    (heritageAssetIdentification \ "references" \ "appellation" \ "id").head.text  shouldBe "https://www.portable-antiquities.nl/pan/#/object/public/9021"
    (heritageAssetIdentification \ "hasRepresentation").head.text  shouldBe "10.17026/dans-z74-c65r/images/PAN-00009021-001.jpg"
  }

  it should "produce a Carare XML-file with a correct digitalResource contents" in {
    val carareXml = transformToCarare(dataset, file)
    val digitalResource = (carareXml \ "carare" \ "digitalResource").head

    (digitalResource \ "recordInformation" \ "id").text  shouldBe "10.17026/dans-z74-c65r/object.xml"
    (digitalResource \ "appellation" \ "name").text  shouldBe "object.xml"
    (digitalResource \ "appellation" \ "id").text  shouldBe "object.xml"
    (digitalResource \ "description").text  shouldBe "Technical description of the object in xml format"
    (digitalResource \ "format").text  shouldBe "text/xml"
    (digitalResource \ "link").text  shouldBe "https://download/location/data/object%2Exml"
    (digitalResource \ "object").text  shouldBe "https://download/location/data/object%2Exml"
    (digitalResource \ "isShownAt").text  shouldBe "https://doi.org/10.17026/dans-z74-c65r"
    (digitalResource \ "rights" \ "accessRights").text  shouldBe "Open Access"
    (digitalResource \ "rights" \ "licence").text  shouldBe "http://creativecommons.org/licenses/by-nc-sa/4.0/"
  }

  private def transformToCarare(dataset: File, file: File): Node = {
    val datasetXml = XML.loadFile(dataset.toJava)
    val filesXml = XML.loadFile(file.toJava)

    val output = new StringWriter()
    val upgradedFilesXml = XmlTransformation.enrichFilesXml(filesXml, datasetXml, downloadUrl)
    val wrappedXml = XmlWrapper.wrap(datasetXml, upgradedFilesXml)
    val input: Source = new StreamSource(new StringReader(wrappedXml.toString()))
    transformer.transform(input, new StreamResult(output))
    val result = XML.loadString(output.toString)
    result
  }

  private def validate(xmlFile: Node, xsdFile: File): Unit = {
    val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
    val schema: Schema = schemaFactory.newSchema(xsdFile.toJava)
    val validator: Validator = schema.newValidator()
    val xml = new StreamSource(new StringReader(xmlFile.toString()))
    validator.validate(xml)
  }
}
