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

import java.io.{ StringReader, Writer }

import javax.xml.transform.stream.{ StreamResult, StreamSource }
import javax.xml.transform.{ Source, Transformer }

import scala.util.Try
import scala.xml.{ Node, PrettyPrinter }

class EasyTransformMetadataApp(configuration: Configuration) {

  // TODO implement
  //  (1) fetch dataset.xml and files.xml from easy-bag-store
  //  (2) enrich files.xml
  //      (a) add <accessibleToRights> and <visibleToRights> entities with value 'ANONYMOUS' whenever they're not there
  //          (also take dcterms:accessRight into account as a backwards compatible replacement for these...)
  //      (b) replace the value in 'filepath' with the download url
  //  (3) combine dataset.xml and files.xml into a single METS xml
  //  (4) if provided, run the transformer to convert the METS xml to the output format and write it to 'output'

  def processDataset(datasetId: DatasetId, transformer: Option[Transformer], output: Writer): Try[Unit] = {
    for {
      datasetXml <- fetchDatasetXml(datasetId)
      filesXml <- fetchFilesXml(datasetId)
      upgradedFilesXml <- enrichFilesXml(filesXml)
      metsXml <- makeMetsXml(datasetXml, upgradedFilesXml)
      _ <- transformer.fold(outputXml(metsXml, output))(t => transform(metsXml, t, output))
    } yield ()
  }

  private def fetchDatasetXml(datasetId: DatasetId): Try[Node] = ???

  private def fetchFilesXml(datasetId: DatasetId): Try[Node] = ???

  private def enrichFilesXml(xml: Node): Try[Node] = ???

  private def makeMetsXml(datasetXml: Node, filesXml: Node): Try[Node] = ???

  private def transform(xml: Node, transformer: Transformer, output: Writer): Try[Unit] = Try {
    val input: Source = new StreamSource(new StringReader(xml.toString()))
    transformer.transform(input, new StreamResult(output))
  }

  private def outputXml(xml: Node, output: Writer): Try[Unit] = Try {
    val xmlOutput = new PrettyPrinter(160, 2).format(xml)
    output.write(xmlOutput)
  }
}
