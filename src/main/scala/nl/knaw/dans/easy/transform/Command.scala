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

import java.io.{ FileWriter, OutputStreamWriter, PrintStream, Writer }
import java.util.UUID

import better.files.File
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.{ Transformer, TransformerFactory }
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import resource.{ ManagedResource, managed }

import scala.language.reflectiveCalls

object Command extends App with DebugEnhancedLogging {
  val configuration = Configuration(File(System.getProperty("app.home")))
  val commandLine: CommandLineOptions = new CommandLineOptions(args, configuration) {
    verify()
  }
  val app = new EasyTransformMetadataApp(configuration)

  lazy val singleDatasetId: Option[DatasetId] = commandLine.datasetId.toOption
  lazy val multipleDatasetIds: Iterator[DatasetId] = commandLine.list()
    .lineIterator.map(UUID.fromString)

  // TODO this is Java code to use the transformer somehow, somewhere
  //      Source text = new StreamSource(new File("input.xml"));
  //      transformer.transform(text, new StreamResult(new File("output.xml")));
  lazy val transformer: Option[Transformer] = commandLine.transform.toOption
    .map(xsltFile => {
      val factory = TransformerFactory.newInstance()
      val xslt = new StreamSource(xsltFile.toJava)
      factory.newTransformer(xslt)
    })

  lazy val fileOutput: Option[ManagedResource[Writer]] = commandLine.output.toOption
    .map(file => managed(file.newFileWriter(append = true)))
  lazy val consoleOutput: ManagedResource[Writer] = managed(Console.out)
    .flatMap(ps => managed(new OutputStreamWriter(ps)))

  for (output <- fileOutput getOrElse consoleOutput;
       datasetId <- singleDatasetId.map(Iterator(_)) getOrElse multipleDatasetIds) {
    app.processDataset(datasetId, transformer, output)
  }
}
