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

import net.strokkur.jap.code.classmodel.CodeClass;
import net.strokkur.jap.code.classmodel.CodeMethod;
import net.strokkur.jap.code.classmodel.CodeParameterDefinition;
import net.strokkur.jap.code.documentation.AbstractDocumentationRenderer;
import net.strokkur.jap.code.documentation.CodeDocumentation;
import net.strokkur.jap.code.test.util.TestTypes;
import net.strokkur.jap.code.type.CodeTypes;

import java.util.function.Supplier;

import static net.strokkur.jap.code.documentation.CodeDocumentation.author;
import static net.strokkur.jap.code.documentation.CodeDocumentation.blank;
import static net.strokkur.jap.code.documentation.CodeDocumentation.classReference;
import static net.strokkur.jap.code.documentation.CodeDocumentation.codeBlock;
import static net.strokkur.jap.code.documentation.CodeDocumentation.combine;
import static net.strokkur.jap.code.documentation.CodeDocumentation.combineLines;
import static net.strokkur.jap.code.documentation.CodeDocumentation.header;
import static net.strokkur.jap.code.documentation.CodeDocumentation.inlineCode;
import static net.strokkur.jap.code.documentation.CodeDocumentation.linebreak;
import static net.strokkur.jap.code.documentation.CodeDocumentation.methodReference;
import static net.strokkur.jap.code.documentation.CodeDocumentation.see;
import static net.strokkur.jap.code.documentation.CodeDocumentation.text;
import static net.strokkur.jap.code.documentation.CodeDocumentation.throwsMeta;
import static net.strokkur.jap.code.documentation.CodeDocumentation.url;
import static net.strokkur.jap.code.documentation.CodeDocumentation.version;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class CommonDocumentationRendererTests {
  void checkOutput(String expected, CodeDocumentation javadoc, Supplier<AbstractDocumentationRenderer> visitorSupplier) {
    final AbstractDocumentationRenderer visitor = visitorSupplier.get();
    javadoc.accept(visitor);
    final String actual = String.join("\n", visitor.getLines());
    assertEquals(expected, actual);
  }

  CodeDocumentation classJavadoc() {
    return combineLines(
      text("A class holding the Brigadier source tree generated from"),
      combine(classReference(sourceClass()), text(" using "), url("StrokkCommands", "https://commands.strokkur.net")),
      blank(),
      author("Strokkur24 - StrokkCommands"),
      version("2.0.0"),
      see(createMethod(), "creating the LiteralCommandNode"),
      see(registerMethod(), "registering the LiteralCommandNode")
    );
  }

  CodeDocumentation registerJavadoc() {
    return combineLines(
      text("Shortcut for registering the command node returned from"),
      combine(methodReference(createMethod()), text(". This method uses the provided aliases")),
      text("and description from the original source file."),
      header("Registering the command", 3),
      text("This method can safely be called either in your plugin bootstrapper's"),
      combine(methodReference(bootstrapMethod(), TestTypes.PLUGIN_BOOTSTRAP), text(" or your main")),
      combine(text("class' "), methodReference(onLoadMethod(), TestTypes.JAVA_PLUGIN), text(" or "), methodReference(onEnableMethod(), TestTypes.JAVA_PLUGIN)),
      text("methods."),
      linebreak(),
      text("You need to call it inside of a lifecycle event. General information can be found on the"),
      combine(url("PaperMC Lifecycle API docs page", "https://docs.papermc.io/paper/dev/lifecycle/"), text(".")),
      linebreak(),
      combine(text("The general use case might look like this (example given inside the "), inlineCode("onEnable"), text(" method):")),
      codeBlock("""
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS.newHandler(event -> {
            final Commands commands = event.registrar();
            EntitiesCommandBrigadier.register(commands);
        }""")
    );
  }

  CodeDocumentation createJd() {
    return combineLines(
      text("A method for creating a Brigadier command node which denotes the declared command"),
      combine(text("in "), classReference(sourceClass()), text(". "), text("You can either retrieve the unregistered node with this method")),
      combine(text("or register it directly with "), methodReference(registerMethod()), text("."))
    );
  }

  CodeDocumentation ctorJd() {
    return combineLines(
      text("The constructor is not accessible. There is no need for an instance"),
      text("to be created, as no state is stored and all methods are static."),
      blank(),
      throwsMeta(CodeTypes.ofClass("java.lang.IllegalAccessException"), "always")
    );
  }

  CodeClass sourceClass() {
    return CodeClass.builder("com.example.CommandClass").build();
  }

  CodeClass targetClass() {
    return CodeClass.builder("com.example.CommandClassBrigadier").build();
  }

  CodeMethod createMethod() {
    return CodeMethod.builder("create").toMethod();
  }

  CodeMethod registerMethod() {
    return CodeMethod.builder("register")
      .addParameters(
        CodeParameterDefinition.of(CodeTypes.ofClass("io.papermc.paper.command.brigadier.Commands"), "commands")
      )
      .toMethod();
  }

  CodeMethod bootstrapMethod() {
    return CodeMethod.builder("bootstrap")
      .addParameters(CodeParameterDefinition.of(
        TestTypes.BOOTSTRAP_CONTEXT, "context"
      ))
      .toMethod();
  }

  CodeMethod onLoadMethod() {
    return CodeMethod.builder("onLoad").toMethod();
  }

  CodeMethod onEnableMethod() {
    return CodeMethod.builder("onEnable").toMethod();
  }
}
