package ru.zubkoff.sber.hw8.cache.provider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Supplier;

public interface CacheProvider {

  Object evaluateIfAbsent(Method method, List<Object> keyArgs, Supplier<Object> resultEvaluation);
  
}
