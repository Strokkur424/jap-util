import org.jspecify.annotations.NullMarked;

@NullMarked
module net.strokkur.jap.source {
  requires transitive net.strokkur.jap.code;

  requires static org.jspecify;
  requires static org.jetbrains.annotations;

  requires java.compiler;
  requires jdk.compiler;
  requires java.xml;
}
