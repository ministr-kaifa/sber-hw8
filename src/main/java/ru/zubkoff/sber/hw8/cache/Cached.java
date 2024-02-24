package ru.zubkoff.sber.hw8.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cached {

  /**
   * if PersistenceType.FILE then result value and args must be serializable
   */
  PersistenceType persistenceType() default PersistenceType.MEMORY;

  String fileNamePrefix() default "";
  
  boolean zip() default false;
  
  Class<?>[] identityBy() default {};
  
  int maxListSize() default 0;
}
