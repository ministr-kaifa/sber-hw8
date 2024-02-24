package ru.zubkoff.sber.hw8.cache.provider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HashMapCacheProvider implements CacheProvider {
  private final Map<Method, Map<List<Object>, Object>> cacheMap;

  @Override
  public Object evaluateIfAbsent(Method method, List<Object> keyArgs, Supplier<Object> resultEvaluation) {
    return cacheMap.computeIfAbsent(method, newCachedMethod -> new HashMap<>())
      .computeIfAbsent(List.of(keyArgs), firstlyCalledArgs -> resultEvaluation.get());
  }

  public HashMapCacheProvider() {
    cacheMap = new HashMap<>();
  }
}
