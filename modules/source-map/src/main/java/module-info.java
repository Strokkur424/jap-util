import org.jspecify.annotations.NullMarked;

@NullMarked
module net.strokkur.jap.source {
  requires transitive net.strokkur.jap.code;

  requires static org.jspecify;
  requires static org.jetbrains.annotations;

  requires java.compiler;
  requires jdk.compiler;

  exports net.strokkur.jap.source;
  exports net.strokkur.jap.source.annotation;
  exports net.strokkur.jap.source.classmodel;
  exports net.strokkur.jap.source.type;
  exports net.strokkur.jap.source.util;
  exports net.strokkur.jap.source.visitor;
}
