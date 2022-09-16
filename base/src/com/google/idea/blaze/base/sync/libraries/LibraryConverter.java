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

import com.google.common.collect.ImmutableSet;
import com.google.idea.blaze.base.model.BlazeLibrary;
import com.google.idea.blaze.base.model.BlazeLibraryModelModifier;
import com.google.idea.blaze.base.model.LibraryKey;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.Library.ModifiableModel;
import java.util.Collection;
import java.util.Optional;

/**
 * Helps convert from {@link BlazeLibrary} or {@link Library} to {@link BlazeLibraryModelModifier}
 */
public abstract class LibraryConverter {
  static LibraryConverter getInstance(Project project) {
    return ServiceManager.getService(project, LibraryConverter.class);
  }

  protected static ModifiableModel getLibraryModifiableModelProvider(
      IdeModifiableModelsProvider modelsProvider, LibraryKey libraryKey) {
    String libraryName = libraryKey.getIntelliJLibraryName();
    Library library = modelsProvider.getLibraryByName(libraryName);
    boolean libraryExists = library != null;
    if (!libraryExists) {
      library = modelsProvider.createLibrary(libraryName);
    }
    return modelsProvider.getModifiableLibraryModel(library);
  }

  public ImmutableSet<String> toLibraryNames(Collection<BlazeLibrary> libraries) {
    return libraries.stream()
        .map(library -> library.key.getIntelliJLibraryName())
        .collect(toImmutableSet());
  }

  public ImmutableSet<BlazeLibraryModelModifier> getOrCreateLibraryModelModifiers(
      IdeModifiableModelsProvider modelsProvider, Collection<BlazeLibrary> libraries) {
    return libraries.stream()
        .map(blazeLibrary -> getOrCreateLibraryModelModifier(modelsProvider, blazeLibrary))
        .collect(toImmutableSet());
  }

  public BlazeLibraryModelModifier getOrCreateLibraryModelModifier(
      IdeModifiableModelsProvider modelsProvider, BlazeLibrary blazeLibrary) {
    return blazeLibrary.toModifier(
        getLibraryModifiableModelProvider(modelsProvider, blazeLibrary.key));
  }

  public Optional<BlazeLibraryModelModifier> getOrCreateLibraryModelModifier(
      IdeModifiableModelsProvider modelsProvider, Library library) {
    return Optional.empty();
  }
}
