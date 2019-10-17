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

import java.io.{ OutputStreamWriter, Writer }
import java.util.UUID

import better.files.File
import javax.xml.transform.stream.StreamSource
import javax.xml.transform.{ Transformer, TransformerFactory }
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import resource.{ ManagedResource, managed }

import scala.util.{ Failure, Success }

object Command extends App with DebugEnhancedLogging {
  val configuration = Configuration(File(System.getProperty("app.home")))
  val commandLine: CommandLineOptions = new CommandLineOptions(args, configuration) {
    verify()
  }
  val app = new EasyTransformMetadataApp(configuration)

  lazy val singleBagId: Option[BagId] = commandLine.bagId.toOption
  lazy val multipleBagIds: Iterator[BagId] = commandLine.list()
    .lineIterator.map(UUID.fromString)

  lazy val transformer: Option[Transformer] = commandLine.transform.toOption
    .map(xsltFile => {
      val factory = TransformerFactory.newInstance()
      val xslt = new StreamSource(xsltFile.toJava)
      factory.newTransformer(xslt)
    })

  def fileOutput(bagId: BagId): Option[ManagedResource[Writer]] = {
    commandLine.output.toOption
      .map(dir => (dir / s"output-$bagId.xml").createFileIfNotExists())
      .map(file => managed(file.newFileWriter(append = false)))
  }

  lazy val consoleOutput: ManagedResource[Writer] = managed(Console.out)
    .flatMap(ps => managed(new OutputStreamWriter(ps)))

  def process(bagId: BagId, output: Writer): Unit = {
    app.processDataset(bagId, transformer, output)
    match {
      case Success(_) =>
      case Failure(e) =>
        logger.error(e.getMessage, e)
        Console.err.println(s"FAILED: ${ e.getMessage }")
    }
  }

  for (bagId <- singleBagId.map(Iterator(_)) getOrElse multipleBagIds;
       output <- fileOutput(bagId) getOrElse consoleOutput)
    process(bagId, output)
}
