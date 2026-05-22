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
package net.strokkur.jap.code.documentation;

import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.type.CodeTypes;
import net.strokkur.jap.code.util.TestTypes;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static net.strokkur.jap.code.documentation.CodeDocumentation.classReference;
import static net.strokkur.jap.code.documentation.CodeDocumentation.combine;
import static net.strokkur.jap.code.documentation.CodeDocumentation.combineLines;
import static net.strokkur.jap.code.documentation.CodeDocumentation.methodReference;
import static net.strokkur.jap.code.documentation.CodeDocumentation.text;

class JavadocStarVisitorTests extends CommonDocumentationRendererTests {
  @Test
  void testJavaStarJavadocsClass() {
    // language=java
    final String expected = """
      /**
       * A class holding the Brigadier source tree generated from
       * {@link com.example.CommandClass} using <a href="https://commands.strokkur.net">StrokkCommands</a>
       *
       * @author Strokkur24 - StrokkCommands
       * @version 2.0.0
       * @see #create() creating the LiteralCommandNode
       * @see #register(io.papermc.paper.command.brigadier.Commands) registering the LiteralCommandNode
       */""";
    checkOutput(expected, classJavadoc(), StarJavadocRenderer::new);
  }

  @Test
  void testJavaStarJavadocsCreate() {
    // language=java
    final String expected = """
      /**
       * A method for creating a Brigadier command node which denotes the declared command
       * in {@link com.example.CommandClass}. You can either retrieve the unregistered node with this method
       * or register it directly with {@link #register(io.papermc.paper.command.brigadier.Commands)}.
       */""";
    checkOutput(expected, createJd(), StarJavadocRenderer::new);
  }

  @Test
  void testJavaStarJavadocsRegister() {
    // language=java
    final String expected = """
      /**
       * Shortcut for registering the command node returned from
       * {@link #create()}. This method uses the provided aliases
       * and description from the original source file.
       *
       * <h3>Registering the command</h3>
       *
       * This method can safely be called either in your plugin bootstrapper's
       * {@link io.papermc.paper.plugin.bootstrap.PluginBootstrap#bootstrap(io.papermc.paper.plugin.bootstrap.BootstrapContext)} or your main
       * class' {@link org.bukkit.plugin.java.JavaPlugin#onLoad()} or {@link org.bukkit.plugin.java.JavaPlugin#onEnable()}
       * methods.
       * <p>
       * You need to call it inside of a lifecycle event. General information can be found on the
       * <a href="https://docs.papermc.io/paper/dev/lifecycle/">PaperMC Lifecycle API docs page</a>.
       * <p>
       * The general use case might look like this (example given inside the {@code onEnable} method):
       * <pre>{@code
       * this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
       *     final Commands commands = event.registrar();
       *     EntitiesCommandBrigadier.register(commands);
       * }
       * }</pre>
       */""";
    checkOutput(expected, registerJavadoc(), StarJavadocRenderer::new);
  }

  @Test
  void testJavaStarJavadocsRegisterWithContext() {
    // language=java
    final String expected = """
      /**
       * Shortcut for registering the command node returned from
       * {@link #create()}. This method uses the provided aliases
       * and description from the original source file.
       *
       * <h3>Registering the command</h3>
       *
       * This method can safely be called either in your plugin bootstrapper's
       * {@link PluginBootstrap#bootstrap(BootstrapContext)} or your main
       * class' {@link JavaPlugin#onLoad()} or {@link JavaPlugin#onEnable()}
       * methods.
       * <p>
       * You need to call it inside of a lifecycle event. General information can be found on the
       * <a href="https://docs.papermc.io/paper/dev/lifecycle/">PaperMC Lifecycle API docs page</a>.
       * <p>
       * The general use case might look like this (example given inside the {@code onEnable} method):
       * <pre>{@code
       * this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
       *     final Commands commands = event.registrar();
       *     EntitiesCommandBrigadier.register(commands);
       * }
       * }</pre>
       */""";
    checkOutput(expected, registerJavadoc(), () -> new StarJavadocRenderer(AbstractDocumentationRenderer.createContext(null,
      Set.of(
        TestTypes.PLUGIN_BOOTSTRAP,
        TestTypes.BOOTSTRAP_CONTEXT,
        TestTypes.JAVA_PLUGIN
      )
    )));
  }

  @Test
  void testJavaStarJavadocsCtor() {
    // language=java
    final String expected = """
      /**
       * The constructor is not accessible. There is no need for an instance
       * to be created, as no state is stored and all methods are static.
       *
       * @throws IllegalAccessException always
       */""";
    checkOutput(expected, ctorJd(), StarJavadocRenderer::new);
  }

  @Test
  void testNamedClassReference() {
    // language=java
    final String expected = """
      /**
       * This {@link ProcessEnvironment environment} does not help me
       * at all.
       */""";
    checkOutput(expected, combineLines(
      combine(text("This "), classReference(CodeTypes.ofClass("java.lang.ProcessEnvironment"), "environment"), text(" does not help me")),
      text("at all.")
    ), StarJavadocRenderer::new);
  }

  @Test
  void testNamedMethodReference() {
    // language=java
    final String expected = """
      /**
       * Use the {@link #builder() builder} for quick access.
       */""";
    checkOutput(
      expected,
      combine(
        text("Use the "),
        methodReference(CodeMethod.builder("builder"), "builder"),
        text(" for quick access.")
      ),
      StarJavadocRenderer::new
    );
  }
}
