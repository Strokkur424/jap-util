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

import net.strokkur.jap.source.SourceMapProcessor;
import net.strokkur.jap.source.classmodel.SourceField;
import net.strokkur.jap.source.classmodel.SourceInterface;
import net.strokkur.jap.source.classmodel.SourceRecord;
import net.strokkur.jap.source.classmodel.SourceRecordComponent;

import javax.lang.model.type.DeclaredType;
import java.util.List;

public class JavaxRecord extends JavaxClassLike implements SourceRecord {
  public JavaxRecord(SourceMapProcessor processor, DeclaredType type) {
    super(processor, type);
  }

  @Override
  public List<SourceRecordComponent> components() {
    return element.map(e ->
      e.getRecordComponents().stream()
        .map(comp -> new SourceRecordComponent(
          ElementUtil.mirrorsToAnnotations(processor, comp.getAnnotationMirrors()),
          ElementUtil.mapType(processor, comp.asType()),
          comp.getSimpleName().toString()
        ))
        .toList()
    );
  }

  @Override
  public List<SourceInterface> implementsClasses() {
    return interfaces();
  }

  @Override
  public List<SourceField> staticFields() {
    return allFields();
  }
}
