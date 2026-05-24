import org.jspecify.annotations.NullMarked;

@NullMarked
module net.strokkur.jap.code {
  requires static org.jetbrains.annotations;
  requires static org.jspecify;
  requires java.compiler;

  exports net.strokkur.jap.code;
  exports net.strokkur.jap.code.annotations;
  exports net.strokkur.jap.code.classmodel;
  exports net.strokkur.jap.code.classmodel.builder;
  exports net.strokkur.jap.code.convert;
  exports net.strokkur.jap.code.documentation;
  exports net.strokkur.jap.code.expression;
  exports net.strokkur.jap.code.expression.builder;
  exports net.strokkur.jap.code.expression.simple;
  exports net.strokkur.jap.code.expression.source;
  exports net.strokkur.jap.code.statement;
  exports net.strokkur.jap.code.type;
  exports net.strokkur.jap.code.type.generic;
  exports net.strokkur.jap.code.type.preset;
  exports net.strokkur.jap.code.util;
  exports net.strokkur.jap.code.visitor;
  exports net.strokkur.jap.code.visitor.source;
}
