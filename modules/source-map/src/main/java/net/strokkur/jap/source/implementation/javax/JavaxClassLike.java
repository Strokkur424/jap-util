/*
 * This file is part of source-map, licensed under the MIT License.
 *
 * Copyright (c) 2026 Strokkur24
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.strokkur.jap.source.implementation.javax;

import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePackage;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.source.SourceMapProcessor;
import net.strokkur.jap.source.annotation.SourceAnnotation;
import net.strokkur.jap.source.classmodel.SourceClassLike;
import net.strokkur.jap.source.classmodel.SourceField;
import net.strokkur.jap.source.classmodel.SourceInterface;
import net.strokkur.jap.source.classmodel.SourceMethod;
import net.strokkur.jap.source.classmodel.SourceModule;
import net.strokkur.jap.source.classmodel.SourcePackage;
import net.strokkur.jap.source.util.Lazy;
import org.jspecify.annotations.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.ModuleElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.List;
import java.util.Set;

abstract class JavaxClassLike implements SourceClassLike {
  protected final SourceMapProcessor processor;
  protected final DeclaredType type;
  protected final Lazy<TypeElement> element;

  JavaxClassLike(SourceMapProcessor processor, DeclaredType type) {
    this.processor = processor;
    this.type = type;
    this.element = Lazy.of(() -> (TypeElement) type.asElement());
  }

  @Override
  public CodeClassType classType() {
    return ElementUtil.mapTypeToClassType(element.get());
  }

  @Override
  public List<CodeGenericTypeDefinition> genericTypes() {
    return element.map(e -> ElementUtil.mapGenerics(processor, e.getTypeParameters()));
  }

  @Override
  public List<SourceClassLike> nestedClasses() {
    return element.map(e ->
      e.getEnclosedElements().stream()
        .filter(ele -> ele.getKind() == ElementKind.CLASS
          || ele.getKind() == ElementKind.RECORD
          || ele.getKind() == ElementKind.INTERFACE
          || ele.getKind() == ElementKind.ANNOTATION_TYPE
        )
        .map(ele -> ElementUtil.getClassLikeFor(processor, (TypeElement) ele))
        .toList()
    );
  }

  @Override
  public @Nullable SourceClassLike enclosingClass() {
    //noinspection DataFlowIssue
    return element.map(e ->
      e.getEnclosingElement() instanceof TypeElement t
        ? ElementUtil.getClassLikeFor(processor, t)
        : null
    );
  }

  @Override
  public @Nullable SourcePackage sourcePackage() {
    Element maybePackage = element.get();
    do {
      maybePackage = maybePackage.getEnclosingElement();
    } while (maybePackage != null && maybePackage.getKind() != ElementKind.PACKAGE);

    if (maybePackage instanceof PackageElement pkg) {
      return new SourcePackage(
        CodePackage.of(pkg.getQualifiedName().toString()),
        ElementUtil.mirrorsToAnnotations(processor, pkg.getAnnotationMirrors())
      );
    }

    return null;
  }

  @Override
  public @Nullable SourceModule sourceModule() {
    Element maybeModule = element.get();
    do {
      maybeModule = maybeModule.getEnclosingElement();
    } while (maybeModule != null && maybeModule.getKind() != ElementKind.MODULE);

    if (maybeModule instanceof ModuleElement pkg) {
      return new SourceModule(
        pkg.getQualifiedName().toString(),
        ElementUtil.mirrorsToAnnotations(processor, pkg.getAnnotationMirrors())
      );
    }

    return null;
  }

  @Override
  public Set<Modifiers> modifiers() {
    return element.map(e -> ElementUtil.mapModifiers(e.getModifiers()));
  }

  @Override
  public List<SourceMethod> methods() {
    return element.map(e ->
      e.getEnclosedElements().stream()
        .filter(ele -> ele.getKind() == ElementKind.METHOD)
        .map(ExecutableElement.class::cast)
        .map(method -> JavaxUtil.convertMethod(processor, method))
        .toList()
    );
  }

  @Override
  public List<SourceAnnotation> annotations() {
    return element.map(e -> ElementUtil.mirrorsToAnnotations(processor, e.getAnnotationMirrors()));
  }

  //
  // Common utility methods, since these exist in almost all class likes,
  // but generally are named differently.
  //

  protected List<SourceInterface> interfaces() {
    return element.map(e ->
      e.getInterfaces().stream()
        .filter(DeclaredType.class::isInstance)
        .map(DeclaredType.class::cast)
        .map(declared -> (SourceInterface) new JavaxInterface(processor, declared))
        .toList()
    );
  }

  protected List<SourceField> allFields() {
    return element.map(e ->
      e.getEnclosedElements().stream()
        .filter(f -> f.getKind() == ElementKind.FIELD && f instanceof VariableElement)
        .map(VariableElement.class::cast)
        .map(field -> JavaxUtil.convertField(processor, field))
        .toList()
    );
  }
}
