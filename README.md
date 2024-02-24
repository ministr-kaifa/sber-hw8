SerializationUtils - содержит утилиты для сериализации/десериализации из разных источников(поток, файл, массив байт)

```java
public interface CacheProvider {
  Object evaluateIfAbsent(Method method, List<Object> keyArgs, Supplier<Object> resultEvaluation);
}
```
Описывает работу с провайдерами кеша(BinaryFileCacheProvider, HashMapCacheProvider, ZipCacheProvider). 

method - кешируемый метод. 

keyArgs - аргументы которые считаются ключем(набор определяется в аннотации Cached.identityBy). 

Эти два аргумента выступают ключом а resultEvaluation представляет собой саплаером вызов которого вернет значение. Этот саплаер исполняется только когда представленого значения нет в кеше. После исполнения значение которое вернулось должно записаться в кеш.


```java
public class CacheWrapperFactory {
  private final CacheProvider memoryCacheProvider;
  private final CacheProvider fileCacheProvider;
  private final CacheProvider zipCacheProvider;

  public CacheWrapperFactory(CacheProvider memoryCacheProvider, CacheProvider fileCacheProvider, CacheProvider zipCacheProvider);

  public <T> T wrap(T target);
}
```
Создает кеширующие обертки, вызывая метод wrap оборачиваем target

CachedInvocationHandler - класс прокси создаваемый CacheWrapperFactory при вызове wrap, в который передается от туда конфигурация кеш провайдеров (memoryCacheProvider, fileCacheProvider, zipCacheProvider). В колбеке прокси резолвится необходимый кеш провайдер исходя из аннотации на вызываемом методе. У выбранного провайдера вызывается метод evaluateIfAbsent



