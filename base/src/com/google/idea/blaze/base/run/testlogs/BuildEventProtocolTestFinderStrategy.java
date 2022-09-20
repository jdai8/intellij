/*
 * Copyright 2017 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.run.testlogs;

import com.google.idea.blaze.base.command.buildresult.BuildEventProtocolOutputReader;
import com.google.idea.blaze.base.command.buildresult.BuildEventStreamProvider;
import com.google.idea.blaze.base.command.buildresult.BuildEventStreamProvider.BuildEventStreamException;
import com.google.idea.blaze.base.io.InputStreamProvider;
import com.intellij.openapi.diagnostic.Logger;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

/**
 * A strategy for locating results from a single 'blaze test' invocation (e.g. output XML files).
 *
 * <p>Parses the output BEP proto either from a bepOutputFile written by blaze or from the
 * BuildEventStream to locate the test XML files.
 */
public final class BuildEventProtocolTestFinderStrategy implements BlazeTestResultFinderStrategy {

  private static final Logger logger =
      Logger.getInstance(BuildEventProtocolTestFinderStrategy.class);

  private final File outputFile;
  private final BuildEventStreamProvider streamProvider;
  private final boolean useLocalFileForTestResults;

  public BuildEventProtocolTestFinderStrategy(File bepOutputFile) {
    this(bepOutputFile, null, true);
  }

  public BuildEventProtocolTestFinderStrategy(BuildEventStreamProvider streamProvider) {
    this(null, streamProvider, false);
  }

  private BuildEventProtocolTestFinderStrategy(
      File outputFile, BuildEventStreamProvider streamProvider, boolean useLocalFile) {
    this.outputFile = outputFile;
    this.streamProvider = streamProvider;
    this.useLocalFileForTestResults = useLocalFile;
  }

  @Override
  public BlazeTestResults findTestResults() {
    try {
      if (useLocalFileForTestResults) {
        return BuildEventProtocolOutputReader.parseTestResults(
            new BufferedInputStream(InputStreamProvider.getInstance().forFile(outputFile)));
      } else {
        return BuildEventProtocolOutputReader.parseTestResults(streamProvider);
      }
    } catch (IOException | BuildEventStreamException e) {
      logger.warn(e.getMessage());
      return BlazeTestResults.NO_RESULTS;
    } finally {
      if (useLocalFileForTestResults && !outputFile.delete()) {
        logger.warn("Could not delete BEP output file: " + outputFile);
      }
    }
  }

  @Override
  public void deleteTemporaryOutputXmlFiles() {}
}
