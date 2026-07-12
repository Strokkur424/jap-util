package net.strokkur.jap.code.type.preset;

import net.strokkur.jap.code.convert.ConvertToClassType;
import net.strokkur.jap.code.type.CodeTypes;

public interface JakartaInjectTypes extends ConvertToClassType {

  JakartaInjectTypes INJECT = create("Inject");
  JakartaInjectTypes NAMED = create("Named");
  JakartaInjectTypes PROVIDER = create("Provider");
  JakartaInjectTypes QUALIFIER = create("Qualifier");
  JakartaInjectTypes SCOPE = create("Scope");
  JakartaInjectTypes SINGLETON = create("Singleton");

  static JakartaInjectTypes create(String name) {
    return () -> CodeTypes.ofClass("jarkarta.inject." + name);
  }
}
