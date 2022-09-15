/*
 * Copyright 2022 The Bazel Authors. All rights reserved.
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
package com.google.idea.blaze.base.sync.libraries;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.idea.blaze.base.sync.libraries.LibraryConverter.getLibraryModifiableModel;

import com.google.common.collect.ImmutableSet;
import com.google.idea.blaze.base.model.BlazeLibrary;
import com.google.idea.blaze.base.model.BlazeLibraryModelModifier;
import com.google.idea.blaze.base.sync.workspace.ArtifactLocationDecoder;
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider;
import com.intellij.openapi.project.Project;
import java.util.Collection;

/**
 * The default implementation of {@link LibraryConverter} that make sure there's at least one {@link
 * LibraryConverter} can be used.
 */
public class DefaultLibraryConverter implements LibraryConverter {

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public ImmutableSet<String> toLibraryNames(Collection<BlazeLibrary> libraries) {
    return libraries.stream()
        .map(library -> library.key.getIntelliJLibraryName())
        .collect(toImmutableSet());
  }

  @Override
  public ImmutableSet<BlazeLibraryModelModifier> getOrCreateLibraryModelModifiers(
      Project project,
      ArtifactLocationDecoder artifactLocationDecoder,
      IdeModifiableModelsProvider modelsProvider,
      Collection<BlazeLibrary> libraries) {
    return libraries.stream()
        .map(
            blazeLibrary ->
                getOrCreateLibraryModelModifier(
                    project, artifactLocationDecoder, modelsProvider, blazeLibrary))
        .collect(toImmutableSet());
  }

  @Override
  public BlazeLibraryModelModifier getOrCreateLibraryModelModifier(
      Project project,
      ArtifactLocationDecoder artifactLocationDecoder,
      IdeModifiableModelsProvider modelsProvider,
      BlazeLibrary blazeLibrary) {
    return blazeLibrary.getModelModifier(
        project,
        artifactLocationDecoder,
        getLibraryModifiableModel(modelsProvider, blazeLibrary.key));
  }
}
