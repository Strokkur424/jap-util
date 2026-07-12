jap-util
========

Common utilities for Java annotation processors, with a focus on (cross JVM language) code generation and intelligent
parsing.

# Modules

## code-gen

The code-gen module features a highly advanced way to generate Java source code fragments or full Java files with typed
library methods. Over are the days of manually writing strings and hoping the generated code compiles and looks somewhat
decent.

This module does not handle compile-time safety of the individual Java types; it is still very well possible to use a
method or class, which does not exist. It simply provides a very convenient and powerful way to represent Java code, in
Java code.

An example? Consider the following Java class:

```java
package net.strokkur.test;

import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Random;

/**
 * A very simple example class to showcase the code gen feature.
 */
@NullMarked
public final class TargetClass {
  private final Random random = new Random();
  private final List<String> welcomeMessages;

  public TargetClass(List<String> welcomeMessages) {
    this.welcomeMessages = welcomeMessages;
  }

  /**
   * Greet the console!
   */
  public void greet() {
    final String message = welcomeMessages.get(random.nextInt(welcomeMessages.size()));
    System.out.println(message);
  }
}
```

Generally, a Java class consists of a few things:

- The package declaration
- Imports
- The class itself
- Javadocs (both on the class, methods, fields, and more)
- Annotations (on the class, fields, and everywhere else)
- Fields
- Constructors
- Methods

The individual parts furthermore consist of their own parts, such as a constructor consisting of parameters and
statements, visibility modifiers, annotations, or even `throws` declarations.

Java is a pretty complex language. So how are these individual parts represented with jap-util's code-gen?

### Model representation

The general model of a class is represented by the `CodeClass` class. For building a class, you use the builder, like
so:

```java
// The fqn of the class you are generating
CodeClassType type = CodeTypes.ofClass("net.strokkur.test.TestClass");
// Create the class itself
CodeClass built = CodeClass.builder().build();
```

The builder has methods for adding documentation (`CodeDocumentation`), modifiers (`Modifiers.PUBLIC`,
`Modifiers.FINAL`), annotations (`CodeAnnotation`), fields (`CodeField`), constructors (`CodeConstructor`), and methods
(`CodeMethod`).

For example, to just represent the following class:

```java
/**
 * A very simple example class to showcase the code gen feature.
 */
@NullMarked
public final class TargetClass {
}
```

...you can use the following code:
<!-- @formatter:off -->
```java
CodeClassType type = CodeTypes.ofClass("net.strokkur.test.TestClass");
CodeClassBuilder builder = CodeClass.builder();

builder.setDocumentation(CodeDocumentation.text(
  "A very simple example class to showcase the code gen feature."
));

// Shortcut of `.addAnnotations(CodeAnnotation.of(JSpecifyTypes.NULL_MARKED))`
builder.addAnnotations(JSpecifyTypes.NULL_MARKED);

// The order you declare these does not matter; they are sorted on print.
builder.addModifiers(Modifiers.PUBLIC, Modifiers.FINAL);
```
<!-- @formatter:on -->

#### Representing fields

Fields can be added to the `CodeClassBuilder` with the `.addFields` method. A field is constructed using the
`CodeField.builder(...)` builder method. You do not need to call
`.toField()` (the "build" method) if using it in the `.addFields` method, as that is done automatically.

When building fields, you need to specify the type and the name. Afterward, modifiers, an optional initializer, and
annotations become settable.

For example, this Java class:

```java
class TargetClass {
  private final Random random = new Random();
  private final List<String> welcomeMessages;
}
```

Can be represented with this code:
<!-- @formatter:off -->
```java
CodeClassType type = CodeTypes.ofClass("net.strokkur.test.TestClass");
CodeClassBuilder builder = CodeClass.builder();

// Whoa, what's happening here? Let's decipher it.
builder.addFields(
  // We first create a new field builder for the Random built-in type, named "random"
  CodeField.builder(JavaTypes.RANDOM, "random")
    // We add the `private final` modifiers
    .addModifiers(Modifiers.PRIVATE, Modifiers.FINAL)
    // We add an initializer. This is the part that sets the expression. In this case,
    // we take the Random built-in type again, and create a constructor call out of it
    // with .ctor(). This is a short-cut to calling the much longer
    // `Expressions.ctorInvocation(JavaTypes.RANDOM)`.
    .setInitializer(JavaTypes.RANDOM.ctor())
);

// The `.addFields` method allows for defining multiple fields at the same time, but you can also
// call the methods multiple times on the same class builder.

builder.addFields(
  // The `.typed` method allows one to add generic type information to a class type.
  // This is equivalent to List<String>.
  CodeField.builder(JavaTypes.LIST.typed(JavaTypes.STRING), "welcomeMessages")
    .addModifiers(Modifiers.PRIVATE, Modifiers.FINAL)
)
```
<!-- @formatter:on -->

#### Representing constructors

For constructors, you can use the `.addConstructor` method. This class has two overloads. One takes in
`CodeConstructor` objects, whilst the other provides a `Consumer<ConstructorBuilder>`. This utility overload is added
because the `CodeConstructor` requires information about the type of the class it is a constructor for, which would be
pretty repetitive to provide, since you'd need to pass in the same type as you have for the class builder. Instead, you
can get a constructor builder for the current class and extend that. Much more convenient!

To represent the following constructor...

```java
public TargetClass(List<String> welcomeMessages) {
  this.welcomeMessages = welcomeMessages;
}
```

You can use this code:

<!-- @formatter:off -->
```java
builder.addConstructor(ctor -> ctor
  .addModifiers(Modifiers.PUBLIC)
  // This method adds a single parameter. A parameter always consists of a type and name. Optionally, it may
  // also contain annotations.
  .addParameter(JavaTypes.LIST.typed(JavaTypes.STRING), "welcomeMessages")
  // The code block references the "content" of a method or conmstructor. Code blocks
  // consists of multiple statements.
  .setCodeBlock(
    // A statement can also just be an expression. In this case, we are declaring an "assign" expression,
    // with `this.welcomeMessages` as the target and the variable `welcomeMessages` as the expression source.
    Expressions.thisExpr().chainField("welcomeMessages").assign(Expressions.variable("welcomeMessages"))
  )
)
```
<!-- @formatter:oN -->

#### Representing methods

Methods and constructors are very similar. The only difference is that methods have a return value, whilst constructors
are bound to their containing class type. They share most of their methods, however.

For example, the following method:

```java
/**
 * Greet the console!
 */
public void greet() {
  final String message = welcomeMessages.get(random.nextInt(welcomeMessages.size()));
  System.out.println(message);
}
```

Can be added to a class as such:
<!-- @formatter:off -->
```java
// Add a new method named `greet`
builder.addMethods(CodeMethod.builder("greet")
  .addModifiers(Modifiers.PUBLIC)
  .setDocumentation(CodeDocumentation.text("Greet the console!"))
  .setCodeBlock(
    // The variable declaration (final) statement declares a new variable with a specific type and name
    // and then, optionally, assigns a expression to it.
    Statements.variableDeclarationFinal(
      JavaTypes.STRING, // the type of the variable
      "message", // the name of the variable
      // Expressions.fieldAccess represents a field. `.chainMethod` uses the previous
      // expression as a base to call another method.
      Expressions.fieldAccess("welcomeMessages").chainMethod("get")
        // This .addParameters call references the latest .chainMethod, adding a parameter
        // expression to the get(...) method for evaluation. The expression of this parameter
        // is once again a method call: `random.nextInt`.
        .addParameters(Expressions.fieldAccess("random").chainMethod("nextInt")
          // this `random.nextInt` call itself also has a parameter: `welcomeMessages.size()`
          .addParameters(Expressions.fieldAccess("welcomeMessages").chainMethod("size"))
        )
    ),
    // You can define multiple statements in a code block method call.
    // In this case, we are just calling System.out.println(message).
    JavaTypes.SYSTEM.chainField("out").chainMethod("println").addParameters(Expressions.variable("message"))
  )
)
```
<!-- @formatter:on -->

### Programming concepts

Instead of taking in an actual object (like `CodeField`), almost all library methods instead use `ConvertTo`
interfaces (`ConvertToField`). These are implemented anywhere it makes sense. For fields, only the `CodeField` and
`CodeFieldBuilder` classes implement the `ConvertToField` interface, but other types, such as `ConvertToClassType` might
be more used. For example, the `JavaTypes`, `JSpecifyTypes`, and any of your custom class-types presets can implement
the `ConvertToClassType` interface and benefit from readable code.

You can do the same for almost all types, which may allow you to simply your own logic a lot if you end up requiring
some more powerful object presets.

### Rendering a class

To render a class, including the import declarations, you can use the `CodeGenUtil` class. If you are building an
annotation processor (AP), you can even implement `CodeGenProcessor` and finally pass an instance of it to the
`CodeGenUtil` constructor, which will allow you to use the `printJavaFile(...)` method to instantly create a Java file
out of your `CodeClass`. Otherwise, you can use `createJavaFile(CodeClass)` to get a raw `String` with the full Java
file contents.

Imports are fetched from the `CodeClass` itself. Over are the days of manually "predicting" the imports you might use!
You can actually do this manually yourself, with the following code:

<!-- @formatter:off -->
```java
final CodeClass codeClass = ...;
final ImportGatheringVisitor importVisitor = new ImportGatheringVisitor();

// The `.accept` method takes in a Visitor and gives back the result. A visitor basically
// goes through your entire class tree recursively and computes some expression. In this case,
// it just retrieves all classes you have referenced.
final Set<CodeClassType> imports = codeClass.accept(importVisitor);
// You need to clean the return expression before, since it will also include references to the current class
// or `java.lang` types, which do not need to be imported.
imports.removeIf(type -> CodePackage.isRedundantImport(codeClass.classType().codePackage(), type.codePackage()));
```
<!-- @formatter:on -->

The class itself is rendered using the `JavaSourcePrintingVisitor`. This class takes in a
`Supplier<AbstractDocumentationRenderer>`. The reason being that the documentation visitor does not return a value;
instead it just adds the rendered parts into a field of type `StringBuilder`. A special method on the
`AbstractDocumentationRender` then takes the value of that field and adds extra stuff, such as the actual documentation
comment syntax `/** ... */` or `///`.

`CodeGenUtil` automatically picks the most fitting documentation renderer based on the current language version the
compiler is using. If the code is compiled for Java 25+, Markdown Javadoc comments will be used, otherwise the standard
Javadoc comments `/** ... */` are used.

### Implementing your own visitor

Implementing your own visitor is as simple as implementing the `CodeVisitor<R>` interface. The `R` generic type here is
the return value of the method. The individual AST elements of the class model do not do any "Delegating" logic; i.e., a
method will not call the visitor for its parameters or return value, meaning you need to do this logic completely
yourself inside the individual `accept(...)` methods.
