package net.strokkur.jap.code.test.java;

import net.strokkur.jap.code.classmodel.CodeField;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeRecord;
import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.expression.Expressions;
import net.strokkur.jap.code.statement.Statements;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.generic.CodeGenericTypeDefinition;
import net.strokkur.jap.code.type.preset.JSpecifyTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import net.strokkur.jap.code.util.Modifiers;
import net.strokkur.jap.code.visitor.CodeVisitable;
import org.junit.jupiter.api.Test;

import java.util.Set;

class RecordGenTests extends AbstractGenTest {

  @Test
  void testFullRecord() {
    //language=Java
    final String expectedCode = """
      /// Documentation
      @NullMarked
      public record Triple<L, M, R>(
        L left,
        M middle,
        R right
      ) {
        public static final Triple<String, String, String> A = Triple.of("a");
      
        /// Constructs a Triple with all three same values.
        public static <S> Triple<S, S, S> of(S same) {
          return new Triple<>(same, same, same);
        }
      
        /// Constructs a new Triple
        public Triple {
          Objects.requireNonNull(left);
          Objects.requireNonNull(middle);
          Objects.requireNonNull(right);
        }
      
        @Override
        public String toString() {
          return "noString() :P";
        }
      }
      """;

    final Set<? extends ConvertToClassType> expectedImports = Set.of(
      TestTypes.TRIPLE,
      JavaTypes.STRING,
      JavaTypes.OBJECTS,
      JavaTypes.OVERRIDE,
      JSpecifyTypes.NULL_MARKED
    );

    final CodeVisitable ast = CodeRecord.builder(TestTypes.TRIPLE)
      .setDocumentation(CodeDocumentation.text("Documentation"))
      .addAnnotations(JSpecifyTypes.NULL_MARKED)
      .addModifiers(Modifiers.PUBLIC)
      .addGenericTypes(
        CodeGenericTypeDefinition.of("L"),
        CodeGenericTypeDefinition.of("M"),
        CodeGenericTypeDefinition.of("R")
      )
      .addComponent(CodeTypes.generic("L"), "left")
      .addComponent(CodeTypes.generic("M"), "middle")
      .addComponent(CodeTypes.generic("R"), "right")

      .addFields(CodeField.builder(TestTypes.TRIPLE.typed(JavaTypes.STRING, JavaTypes.STRING, JavaTypes.STRING), "A")
        .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC, Modifiers.FINAL)
        .setInitializer(TestTypes.TRIPLE.chainMethod("of", Expressions.string("a")))
      )

      .withPrimaryConstructor(builder -> builder
        .setDocumentation(CodeDocumentation.text("Constructs a new Triple"))
        .addModifiers(Modifiers.PUBLIC)
        .setCodeBlock(
          JavaTypes.OBJECTS.chainMethod("requireNonNull", Expressions.variable("left")),
          JavaTypes.OBJECTS.chainMethod("requireNonNull", Expressions.variable("middle")),
          JavaTypes.OBJECTS.chainMethod("requireNonNull", Expressions.variable("right"))
        )
      )

      .addMethods(CodeMethod.builder("of")
        .setDocumentation(CodeDocumentation.text("Constructs a Triple with all three same values."))
        .addModifiers(Modifiers.PUBLIC, Modifiers.STATIC)
        .addGenerics(CodeGenericTypeDefinition.of("S"))
        .setReturnType(TestTypes.TRIPLE.typed(CodeTypes.generic("S"), CodeTypes.generic("S"), CodeTypes.generic("S")))
        .addParameter(CodeTypes.generic("S"), "same")
        .setCode(
          Statements.returnStmt(
            TestTypes.TRIPLE.typed().ctor(
              Expressions.variable("same"),
              Expressions.variable("same"),
              Expressions.variable("same")
            )
          )
        )
      )

      .addMethods(CodeMethod.builder("toString")
        .addAnnotations(JavaTypes.OVERRIDE)
        .addModifiers(Modifiers.PUBLIC)
        .setReturnType(JavaTypes.STRING)
        .setCode(Statements.returnStmt(
          Expressions.string("noString() :P")
        ))
      )

      .toRecord();

    check(expectedImports, expectedCode, ast);
  }

  @Test
  void testEmptyRecord() {
    checkCode("""
        record EmptyRecord() {
        }
        """,
      CodeRecord.builder("none.EmptyRecord").toRecord()
    );
  }
}
