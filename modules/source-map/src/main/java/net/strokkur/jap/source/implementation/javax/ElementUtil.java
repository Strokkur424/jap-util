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

import com.sun.source.tree.ExpressionTree;
import net.strokkur.jap.code.convert.ConvertToGenericType;
import net.strokkur.jap.code.type.CodeClassType;
import net.strokkur.jap.code.type.CodePackage;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.generic.GenericEnclosure;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.source.SourceMapProcessor;
import net.strokkur.jap.source.annotation.SourceAnnotation;
import net.strokkur.jap.source.annotation.SourceAnnotationParameter;
import net.strokkur.jap.source.classmodel.SourceClassLike;
import net.strokkur.jap.source.implementation.javax.visitor.JavaxAnnotationValueToExpression;
import net.strokkur.jap.source.implementation.javax.visitor.JavaxTreeToExpression;
import net.strokkur.jap.source.type.LazySourceClassLikeType;
import net.strokkur.jap.source.type.SourceArrayType;
import net.strokkur.jap.source.type.SourceType;
import net.strokkur.jap.source.util.Lazy;
import net.strokkur.jap.source.util.LazyExpression;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.Nullable;

import javax.lang.model.AnnotatedConstruct;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ElementUtil {

  @SuppressWarnings({"unchecked", "DataFlowIssue"})
  public static SourceAnnotation mirrorToAnnotation(SourceMapProcessor processor, AnnotatedConstruct annotated, AnnotationMirror element) {
    return new SourceAnnotation(
      Lazy.of(() -> {
        final String fqn = element.getAnnotationType().toString();
        final Class<? extends Annotation> annotationClass;
        try {
          annotationClass = (Class<? extends Annotation>) Class.forName(fqn);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
        return Objects.requireNonNull(annotated.getAnnotation(annotationClass));
      }),
      new JavaxAnnotationInterface(processor, element.getAnnotationType()),
      element.getElementValues().entrySet().stream()
        .map(e -> new SourceAnnotationParameter(
          e.getKey().getSimpleName().toString(),
          e.getValue().getValue(),
          LazyExpression.ofExpression(() -> e.getValue().accept(JavaxAnnotationValueToExpression.VISITOR, null))
        ))
        .toList()
    );
  }

  public static @Nullable Modifiers mapModifier(Modifier elementModifier) {
    try {
      return Modifiers.valueOf(elementModifier.name());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  public static CodeClassType mapTypeToClassType(TypeElement element) {
    final List<String> nameComponents = new ArrayList<>();
    Element current = element;
    do {
      nameComponents.add(current.getSimpleName().toString());
      current = current.getEnclosingElement();
    } while (current instanceof TypeElement);

    final PackageElement pkg = (PackageElement) current;

    return new CodeClassType(
      CodePackage.of(pkg.getQualifiedName().toString()),
      String.join(".", nameComponents.reversed()),
      null
    );
  }

  public static CodeClassType mapDeclared(DeclaredType declared) {
    final String fqn = declared.toString()
      .replaceAll("@[a-zA-Z0-9_.]+(\\([^)]*\\))?\\s*", "");

    if (fqn.contains("<")) {
      final String[] split = fqn.split("<");
      return CodeTypes.ofClassTyped(
        split[0],
        Arrays.stream(split[1].substring(0, split[1].length() - 1).split(","))
          .map(String::strip)
          .map(CodeTypes::generic)
          .toArray(ConvertToGenericType[]::new)
      );
    }
    return CodeTypes.ofClass(fqn);
  }

  public static @Nullable LazyExpression toLazyMirror(@Nullable ExpressionTree tree) {
    if (tree == null) {
      return null;
    }
    //noinspection DataFlowIssue
    return LazyExpression.ofExpression(() -> Objects.requireNonNull(tree.accept(new JavaxTreeToExpression(), null)));
  }

  public static SourceType mapType(SourceMapProcessor processor, TypeMirror mirror) {
    if (mirror.getKind() == TypeKind.ARRAY && mirror instanceof ArrayType array) {
      return new SourceArrayType(mapType(processor, array.getComponentType()));
    }
    if (mirror.getKind() == TypeKind.VOID) {
      return SourceType.VOID;
    }

    if (mirror.getKind().isPrimitive()) {
      return switch (mirror.getKind()) {
        case BYTE -> SourceType.BYTE;
        case BOOLEAN -> SourceType.BOOL;
        case SHORT -> SourceType.SHORT;
        case CHAR -> SourceType.CHAR;
        case INT -> SourceType.INT;
        case LONG -> SourceType.LONG;
        case FLOAT -> SourceType.FLOAT;
        case DOUBLE -> SourceType.DOUBLE;

        // we don't know; this type is not mirrored yet.
        default -> SourceType.UNKNOWN;
      };
    }
    if (mirror.getKind() == TypeKind.DECLARED && mirror instanceof DeclaredType declared) {
      return new LazySourceClassLikeType(
        mapDeclared(declared),
        Lazy.of(() -> {
          final Element element = declared.asElement();
          return getClassLikeFor(processor, (TypeElement) element);
        })
      );
    }

    throw new IllegalArgumentException("Unknown type kind: " + mirror.getKind());
  }

  public static SourceClassLike getClassLikeFor(SourceMapProcessor processor, TypeElement element) {
    return switch (element.getKind()) {
      case CLASS -> new JavaxClass(processor, (DeclaredType) element.asType());
      case RECORD -> new JavaxRecord(processor, (DeclaredType) element.asType());
      case INTERFACE -> new JavaxInterface(processor, (DeclaredType) element.asType());
      case ANNOTATION_TYPE -> new JavaxAnnotationInterface(processor, (DeclaredType) element.asType());
      default -> throw new IllegalArgumentException("This should not happen.");
    };
  }

  public static List<CodeGenericTypeDefinition> mapGenerics(SourceMapProcessor processor, List<? extends TypeParameterElement> parameters) {
    return parameters.stream()
      .map(param -> {
        if (param.getBounds().size() > 2) {
          throw new IllegalArgumentException("Multiple generic bounds are currently not supported.");
        }

        if (param.getBounds().size() == 1) {
          return new CodeGenericTypeDefinition(
            param.getSimpleName().toString(),
            GenericEnclosure.withExtends(mapType(
              processor,
              param.getBounds().getFirst()
            ))
          );
        }

        return new CodeGenericTypeDefinition(param.getSimpleName().toString(), null);
      })
      .toList();
  }

  public static @Unmodifiable List<SourceAnnotation> mapAnnotations(SourceMapProcessor processor, AnnotatedConstruct source) {
    return source.getAnnotationMirrors().stream()
      .map(mirror -> mirrorToAnnotation(processor, source, mirror))
      .toList();
  }

  public static Set<Modifiers> mapModifiers(Set<Modifier> elementModifiers) {
    return elementModifiers.stream()
      .map(ElementUtil::mapModifier)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());
  }

  private ElementUtil() throws IllegalAccessError {
    throw new IllegalAccessError("This class cannot be instantiated.");
  }
}
