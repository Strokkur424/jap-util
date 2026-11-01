import org.jspecify.annotations.NullMarked;

@NullMarked
module net.strokkur.internal.processor {
  requires org.jspecify;
  requires net.strokkur.internal.annotations;
  requires com.google.auto.service;
  requires java.compiler;
  requires net.strokkur.jap.source;
}
