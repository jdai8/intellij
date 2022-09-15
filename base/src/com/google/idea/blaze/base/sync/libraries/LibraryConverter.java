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

import com.google.common.collect.ImmutableSet;
import com.google.idea.blaze.base.model.BlazeLibrary;
import com.google.idea.blaze.base.model.BlazeLibraryModelModifier;
import com.google.idea.blaze.base.model.LibraryKey;
import com.google.idea.blaze.base.sync.workspace.ArtifactLocationDecoder;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.Library.ModifiableModel;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

/**
 * Helps convert from {@link BlazeLibrary} or {@link Library} to {@link BlazeLibraryModelModifier}
 */
public interface LibraryConverter {
  ExtensionPointName<LibraryConverter> EP_NAME =
      ExtensionPointName.create("com.google.idea.blaze.LibraryConverter");

  static Optional<LibraryConverter> getFirstAvailableLibraryConverter() {
    return Arrays.stream(EP_NAME.getExtensions()).filter(ep -> ep.isEnabled()).findFirst();
  }

  static ModifiableModel getLibraryModifiableModel(
      IdeModifiableModelsProvider modelsProvider, LibraryKey libraryKey) {
    String libraryName = libraryKey.getIntelliJLibraryName();
    Library library = modelsProvider.getLibraryByName(libraryName);
    boolean libraryExists = library != null;
    if (!libraryExists) {
      library = modelsProvider.createLibrary(libraryName);
    }
    return modelsProvider.getModifiableLibraryModel(library);
  }

  boolean isEnabled();

  ImmutableSet<String> toLibraryNames(Collection<BlazeLibrary> libraries);

  ImmutableSet<BlazeLibraryModelModifier> getOrCreateLibraryModelModifiers(
      Project project,
      ArtifactLocationDecoder artifactLocationDecoder,
      IdeModifiableModelsProvider modelsProvider,
      Collection<BlazeLibrary> libraries);

  BlazeLibraryModelModifier getOrCreateLibraryModelModifier(
      Project project,
      ArtifactLocationDecoder artifactLocationDecoder,
      IdeModifiableModelsProvider modelsProvider,
      BlazeLibrary blazeLibrary);
}
