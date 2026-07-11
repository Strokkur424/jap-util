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
package net.strokkur.jap.code.test.java;

import org.junit.jupiter.api.Test;

import static net.strokkur.jap.code.expression.Expressions.variable;

class BooleanExpressionGenTests extends AbstractGenTest {

  @Test
  void testNot() {
    checkCode("left", variable("left"));
    checkCode("!left", variable("left").not());
    checkCode("left", variable("left").not().not());
  }

  @Test
  void testAnd() {
    checkCode("left && right", variable("left")
      .and(variable("right"))
    );
    checkCode("left && middle && right", variable("left")
      .and(variable("middle"))
      .and(variable("right"))
    );
  }

  @Test
  void testOr() {
    checkCode("left || right", variable("left")
      .or(variable("right"))
    );
    checkCode("left || middle || right", variable("left")
      .or(variable("middle"))
      .or(variable("right"))
    );
  }

  @Test
  void testAndOr() {
    checkCode("(left || middle) && right", variable("left")
      .or(variable("middle"))
      .and(variable("right"))
    );

    checkCode("left || middle && right", variable("left")
      .or(variable("middle").and(variable("right")))
    );

    checkCode("left && (middle || right)", variable("left")
      .and(variable("middle").or(variable("right")))
    );
    checkCode("left && middle || right", variable("left")
      .and(variable("middle"))
      .or(variable("right"))
    );
  }

  @Test
  void testComplexNot() {
    checkCode("!(left || right)", variable("left")
      .or(variable("right"))
      .not()
    );
    checkCode("!(left && right)", variable("left")
      .and(variable("right"))
      .not()
    );
  }

  @Test
  void testComplex() {
    checkCode("!((left || middle) && !right)",
      variable("left").or(variable("middle"))
        .and(variable("right").not())
        .not()
    );
  }
}
