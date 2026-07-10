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
