package ru.zubkoff.sber.hw8.cache;

import net.sf.cglib.proxy.Enhancer;
import ru.zubkoff.sber.hw8.cache.provider.CacheProvider;

public class CacheWrapperFactory {
  private final CacheProvider memoryCacheProvider;
  private final CacheProvider fileCacheProvider;
  private final CacheProvider zipCacheProvider;

  public CacheWrapperFactory(CacheProvider memoryCacheProvider, CacheProvider fileCacheProvider,
                             CacheProvider zipCacheProvider) {
    this.memoryCacheProvider = memoryCacheProvider;
    this.fileCacheProvider = fileCacheProvider;
    this.zipCacheProvider = zipCacheProvider;
  }

  /**
   * Creates new cache proxy wrapper instance
   * @param target object to be wrapped
   * @return new cache proxy wrapper instance
   */
  public <T> T wrap(T target) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(target.getClass());
    enhancer.setCallback(new CachedInvocationHandler(target, memoryCacheProvider, fileCacheProvider, zipCacheProvider));
    return (T)enhancer.create();
  }
}
