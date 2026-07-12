package net.strokkur.jap.code.documentation;

import java.util.List;
import java.util.SequencedCollection;

public class DiscardingDocumentationRenderer extends AbstractDocumentationRenderer {
  @Override
  public SequencedCollection<String> getLines() {
    return List.of();
  }

  @Override
  public void visit(CodeDocumentation.PlainText value) {

  }

  @Override
  public void visit(CodeDocumentation.Meta value) {

  }

  @Override
  public void visit(CodeDocumentation.MethodReferenceMeta value) {

  }

  @Override
  public void visit(CodeDocumentation.ClassReferenceMeta value) {

  }

  @Override
  public void visit(CodeDocumentation.Header value) {

  }

  @Override
  public void visit(CodeDocumentation.Newline value) {

  }

  @Override
  public void visit(CodeDocumentation.Linebreak value) {

  }

  @Override
  public void visit(CodeDocumentation.InlineCode value) {

  }

  @Override
  public void visit(CodeDocumentation.CodeBlock value) {

  }

  @Override
  public void visit(CodeDocumentation.Url value) {

  }

  @Override
  public void visit(CodeDocumentation.ClassReference value) {

  }

  @Override
  public void visit(CodeDocumentation.MethodReference value) {

  }
}
