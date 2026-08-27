package net.strokkur.jap.code.statement;

import net.strokkur.jap.code.classmodel.CodeBlock;
import net.strokkur.jap.code.type.CodeClassType;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record TryStatement(
  CodeBlock tryBlock,
  List<CatchStatement> catchStatements,
  @Nullable CodeBlock finallyBlock
) implements CodeStatement {
  public record CatchStatement(
    List<CodeClassType> exceptionTypes,
    String catchName,
    CodeBlock catchBlock
  ) {}
}
