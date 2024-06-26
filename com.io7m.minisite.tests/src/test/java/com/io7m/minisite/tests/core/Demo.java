/*
 * Copyright © 2017 <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.minisite.tests.core;

import com.io7m.minisite.core.MinBugTrackerConfiguration;
import com.io7m.minisite.core.MinChangesConfiguration;
import com.io7m.minisite.core.MinConfiguration;
import com.io7m.minisite.core.MinSite;
import com.io7m.minisite.core.MinSourcesConfiguration;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.io7m.minisite.tests.XHTMLValidation.validate;

public final class Demo
{
  private Demo()
  {

  }

  public static void main(
    final String[] args)
    throws Exception
  {
    final MinConfiguration c =
      MinConfiguration.builder()
        .setProjectName("com.io7m.r2")
        .setProjectGroupName("com.io7m.r2")
        .setRelease("0.3.0")
        .setCentralReposPath("/com/io7m/r2/")
        .setOverview(Paths.get("src/site/resources/overview.xml"))
        .setFeatures(Paths.get("src/site/resources/features.xml"))
        .setChangelog(MinChangesConfiguration.builder()
                        .setFeedEmail("nobody@example.com")
                        .setFile(Paths.get("README-CHANGES.xml"))
                        .build())
        .setLicense(Paths.get("README-LICENSE.txt"))
        .setSources(
          MinSourcesConfiguration.builder()
            .setSystem("Git")
            .setUri(URI.create("https://www.github.com/io7m-com/r2"))
            .build())
        .setBugTracker(
          MinBugTrackerConfiguration.builder()
            .setSystem("GitHub Issues")
            .setUri(URI.create("https://www.github.com/io7m-com/r2/issues"))
            .build())
        .build();

    final MinSite site = MinSite.create(c);

    final Path directory = Paths.get("/shared-tmp");
    Files.createDirectories(directory);

    final var fileOutput =
      directory.resolve("index.xhtml");
    final var docBuilderFactory =
      DocumentBuilderFactory.newInstance();
    final var docBuilder =
      docBuilderFactory.newDocumentBuilder();
    final var document =
      docBuilder.newDocument();

    document.appendChild(site.document(document));

    final var source = new DOMSource(document);
    try (var output = Files.newBufferedWriter(fileOutput)) {
      final var result = new StreamResult(output);
      final var transformerFactory = TransformerFactory.newInstance();
      final var transformer = transformerFactory.newTransformer();
      transformer.transform(source, result);
      output.flush();
    }

    validate(fileOutput.toFile());
  }
}
