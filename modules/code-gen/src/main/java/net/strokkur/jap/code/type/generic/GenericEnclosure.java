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
package net.strokkur.jap.code.type.generic;

import net.strokkur.jap.code.type.CodeType;

public sealed interface GenericEnclosure {

  CodeType encloses();

  /// `Type`
  static GenericEnclosure typeEnclosure(CodeType encloses) {
    return new TypeEnclosure(encloses);
  }

  /// `? extends Type`
  static GenericEnclosure extendsEnclosure(CodeType encloses) {
    return new ExtendsEnclosure(encloses);
  }

  /// `? super Type`
  static GenericEnclosure superEnclosure(CodeType encloses) {
    return new SuperEnclosure(encloses);
  }

  record ExtendsEnclosure(
    CodeType encloses
  ) implements GenericEnclosure {
  }

  record SuperEnclosure(
    CodeType encloses
  ) implements GenericEnclosure {
  }

  record TypeEnclosure(
    CodeType encloses
  ) implements GenericEnclosure {
  }
}
