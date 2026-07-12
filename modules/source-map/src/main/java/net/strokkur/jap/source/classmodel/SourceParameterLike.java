package net.strokkur.jap.source.classmodel;

import net.strokkur.jap.source.annotation.AnnotationsHolder;
import net.strokkur.jap.source.annotation.SourceAnnotation;
import net.strokkur.jap.source.type.SourceType;

import java.util.List;

public interface SourceParameterLike  extends AnnotationsHolder {

  List<SourceAnnotation> annotations();

  SourceType type();

  String name();
}
