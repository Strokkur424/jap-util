/*
 * This file is part of code-gen, licensed under the MIT License.
 *
 * Copyright (c) 2025 Strokkur24
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
package net.strokkur.jap.code.test.documentation;

import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.documentation.AbstractDocumentationRenderer;
import net.strokkur.jap.code.documentation.MarkdownJavadocRenderer;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.type.preset.JavaTypes;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.documentation.CodeDocumentation.blank;
import static net.strokkur.jap.code.documentation.CodeDocumentation.classReference;
import static net.strokkur.jap.code.documentation.CodeDocumentation.combine;
import static net.strokkur.jap.code.documentation.CodeDocumentation.combineLines;
import static net.strokkur.jap.code.documentation.CodeDocumentation.methodReference;
import static net.strokkur.jap.code.documentation.CodeDocumentation.newline;
import static net.strokkur.jap.code.documentation.CodeDocumentation.see;
import static net.strokkur.jap.code.documentation.CodeDocumentation.text;

class JavadocMarkdownVisitorTests extends CommonDocumentationRendererTests {
  @Test
  void testJavaMarkdownJavadocsClass() {
    // language=java
    final String expected = """
      /// A class holding the Brigadier source tree generated from
      /// [com.example.CommandClass] using [StrokkCommands](https://commands.strokkur.net)
      ///
      /// @author Strokkur24 - StrokkCommands
      /// @version 2.0.0
      /// @see #create() creating the LiteralCommandNode
      /// @see #register(io.papermc.paper.command.brigadier.Commands) registering the LiteralCommandNode""";
    checkOutput(expected, classJavadoc(), MarkdownJavadocRenderer::new);
  }

  @Test
  void testJavaMarkdownJavadocsCreate() {
    // language=java
    final String expected = """
      /// A method for creating a Brigadier command node which denotes the declared command
      /// in [com.example.CommandClass]. You can either retrieve the unregistered node with this method
      /// or register it directly with [#register(io.papermc.paper.command.brigadier.Commands)].""";
    checkOutput(expected, createJd(), MarkdownJavadocRenderer::new);
  }

  @Test
  void testJavaMarkdownJavadocsRegister() {
    // language=java
    final String expected = """
      /// Shortcut for registering the command node returned from
      /// [#create()]. This method uses the provided aliases
      /// and description from the original source file.
      ///
      /// ### Registering the command
      ///
      /// This method can safely be called either in your plugin bootstrapper's
      /// [PluginBootstrap#bootstrap(BootstrapContext)] or your main
      /// class' [JavaPlugin#onLoad()] or [JavaPlugin#onEnable()]
      /// methods.
      ///
      /// You need to call it inside of a lifecycle event. General information can be found on the
      /// [PaperMC Lifecycle API docs page](https://docs.papermc.io/paper/dev/lifecycle/).
      ///
      /// The general use case might look like this (example given inside the `onEnable` method):
      /// ```
      /// this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
      ///     final Commands commands = event.registrar();
      ///     EntitiesCommandBrigadier.register(commands);
      /// }
      /// ```""";
    checkOutput(expected, registerJavadoc(), () -> new MarkdownJavadocRenderer(AbstractDocumentationRenderer.createContext(null,
      Set.of(
        TestTypes.PLUGIN_BOOTSTRAP,
        TestTypes.BOOTSTRAP_CONTEXT,
        TestTypes.JAVA_PLUGIN
      )
    )));
  }

  @Test
  void testJavaMarkdownJavadocsCtor() {
    // language=java
    final String expected = """
      /// The constructor is not accessible. There is no need for an instance
      /// to be created, as no state is stored and all methods are static.
      ///
      /// @throws IllegalAccessException always""";
    checkOutput(expected, ctorJd(), MarkdownJavadocRenderer::new);
  }

  @Test
  void testNamedClassReference() {
    // language=java
    final String expected = """
      /// This [environment][ProcessEnvironment] does not help me
      /// at all.""";
    checkOutput(expected, combineLines(
      combine(text("This "), classReference(CodeTypes.ofClass("java.lang.ProcessEnvironment"), "environment"), text(" does not help me")),
      text("at all.")
    ), MarkdownJavadocRenderer::new);
  }

  @Test
  void testNamedMethodReference() {
    // language=java
    final String expected = """
      /// Use the [builder][#builder()] for quick access.""";
    checkOutput(
      expected,
      combine(
        text("Use the "),
        methodReference(CodeMethod.builder("builder"), "builder"),
        text(" for quick access.")
      ),
      MarkdownJavadocRenderer::new
    );
  }

  @Test
  void testMiscCodeTypes() {
    // language=java
    final String expected = """
      /// This is text with a
      /// new line.
      ///
      /// @see String#concat(String)""";
    checkOutput(
      expected,
      combineLines(
        combine(text("This is text with a"), newline(), text("new line.")),
        blank(),
        see(CodeMethod.builder("concat").addParameters(CodeParameterDefinition.of(JavaTypes.STRING, "")),
          null,
          JavaTypes.STRING
        )
      ),
      MarkdownJavadocRenderer::new
    );
  }
}
