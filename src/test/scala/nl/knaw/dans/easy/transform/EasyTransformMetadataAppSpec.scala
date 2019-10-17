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
import java.util.UUID

import nl.knaw.dans.easy.transform.bagstore.BagStoreConfig
import nl.knaw.dans.easy.transform.fixture.TestSupportFixture
import okhttp3.HttpUrl
import okhttp3.mockwebserver.{ MockResponse, MockWebServer }

class EasyTransformMetadataAppSpec extends TestSupportFixture {

  // configure the mock server
  private val server = new MockWebServer
  server.start()
  private val test_server = "/test_server/"
  private val baseUrl: HttpUrl = server.url(test_server)

  private val app = new EasyTransformMetadataApp(Configuration("1.0.0", BagStoreConfig(baseUrl.uri(), 0l, 0l), new URI("")))
  private val bagId: BagId = UUID.fromString("0000000-0000-0000-0000-000000000001")

  "loadDatasetXml" should "fetch the xml-file from a correct location" in {
    server.enqueue(new MockResponse().setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ddm:DDM>-</ddm:DDM>"))
    val datasetXml = app.loadDatasetXml(bagId)
    server.takeRequest.getRequestLine shouldBe s"GET ${ test_server }bags/$bagId/metadata/dataset.xml HTTP/1.1"
  }

  "loadFilesXml" should "fetch the xml-file from a correct location" in {
    server.enqueue(new MockResponse().setBody("<?xml version=\"1.0\" encoding=\"UTF-8\"?><files>-</files>"))
    val filesXml = app.loadFilesXml(bagId)
    server.takeRequest.getRequestLine shouldBe s"GET ${ test_server }bags/$bagId/metadata/files.xml HTTP/1.1"
  }
}
