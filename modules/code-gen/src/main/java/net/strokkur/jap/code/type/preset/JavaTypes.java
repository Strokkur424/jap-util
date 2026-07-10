/*
 * This file is part of code-gen, licensed under the MIT License.
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
package net.strokkur.jap.code.type.preset;

import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.type.CodeTypes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public interface JavaTypes extends ConvertToClassType {

  JavaTypes OBJECT = create(Object.class);
  JavaTypes STRING = create(String.class);
  JavaTypes NUMBER = create(Number.class);

  JavaTypes INTEGER = create(Integer.class);
  JavaTypes LONG = create(Long.class);
  JavaTypes FLOAT = create(Float.class);
  JavaTypes DOUBLE = create(Double.class);

  JavaTypes LIST = create(List.class);
  JavaTypes RANDOM = create(Random.class);

  JavaTypes OBJECTS = create(Objects.class);
  JavaTypes COLLECTIONS = create(Collections.class);
  JavaTypes ARRAYS = create(Arrays.class);

  JavaTypes SYSTEM = create(System.class);

  JavaTypes NULL_POINTER_EXCEPTION = create(NullPointerException.class);
  JavaTypes ILLEGAL_STATE_EXCEPTION = create(IllegalStateException.class);
  JavaTypes RUNTIME_EXCEPTION = create(RuntimeException.class);

  static JavaTypes create(Class<?> type) {
    return () -> CodeTypes.ofClass(type.getName());
  }
}
