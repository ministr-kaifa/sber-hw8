package ru.zubkoff.sber.hw8.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import ru.zubkoff.sber.hw8.cache.provider.CacheProvider;

public class CachedInvocationHandler implements MethodInterceptor {

  private final CacheProvider memoryCacheProvider;
  private final CacheProvider fileCacheProvider;
  private final CacheProvider zipCacheProvider;
  private final Object proxied;

  public CachedInvocationHandler(Object proxied, CacheProvider memoryCacheProvider, 
                                 CacheProvider fileCacheProvider, CacheProvider zipCacheProvider) {
    this.memoryCacheProvider = memoryCacheProvider;
    this.fileCacheProvider = fileCacheProvider;
    this.zipCacheProvider = zipCacheProvider;
    this.proxied = proxied;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    if (method.isAnnotationPresent(Cached.class)) {
      var cacheInfo = method.getAnnotation(Cached.class);
      List<Object> keyArgs = filterArgs(args, cacheInfo.identityBy());
      if (cacheInfo.persistenceType() == PersistenceType.MEMORY) {
        return cachedInvoke(memoryCacheProvider, obj, method, args, keyArgs, cacheInfo.maxListSize());
      } else if (cacheInfo.persistenceType() == PersistenceType.FILE && cacheInfo.zip()) {
        return cachedInvoke(zipCacheProvider, obj, method, args, keyArgs, cacheInfo.maxListSize());
      } else if (cacheInfo.persistenceType() == PersistenceType.FILE && !cacheInfo.zip()) {
        return cachedInvoke(fileCacheProvider, obj, method, args, keyArgs, cacheInfo.maxListSize());
      } else {
        throw new RuntimeException("unknown persistenceType");
      }
    } else {
      return method.invoke(obj, args);
    }
  }

  private Object cachedInvoke(CacheProvider cacheProvider, Object obj, Method method, Object[] fullArgs,
                                     List<Object> keyArgs, int maxListSize) {
    if (List.class.isAssignableFrom(method.getReturnType().getClass()) && maxListSize > 0) {
      return cacheProvider.evaluateIfAbsent(method, keyArgs,
          () -> trimToMaxSize((List) invoke(method, fullArgs), maxListSize));
    } else {
      return cacheProvider.evaluateIfAbsent(method, keyArgs, () -> invoke(method, fullArgs));
    }
  }

  private Object invoke(Method method, Object[] args) {
    try {
      return method.invoke(proxied, args);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static <T> List<T> trimToMaxSize(List<T> list, int maxSize) {
    return list.subList(0, Math.min(list.size(), maxSize));
  }

  private static List<Object> filterArgs(Object[] args, Class<?>[] targetTypes) {

    if (targetTypes.length == 0) {
      return List.of(args);
    }

    var filtered = new ArrayList<Object>();
    try {
      for (int i = 0, j = 0; i < args.length && j < targetTypes.length; i++) {
        if (targetTypes[j].isAssignableFrom(args[i].getClass())) {
          filtered.add(args[i]);
          j++;
        }
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new RuntimeException("invalid identityBy");
    }
    
    return filtered;

  }

}
