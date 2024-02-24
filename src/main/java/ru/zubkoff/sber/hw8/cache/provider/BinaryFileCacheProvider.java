package ru.zubkoff.sber.hw8.cache.provider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ru.zubkoff.sber.hw8.serialization.utils.SerializationUtils;

public class BinaryFileCacheProvider implements CacheProvider {

  private final Path cacheFilesFolder;

  public BinaryFileCacheProvider(Path cacheFilesFolder) {
    this.cacheFilesFolder = cacheFilesFolder;
  }

  @Override
  public Object evaluateIfAbsent(Method method, List<Object> keyArgs, Supplier<Object> resultEvaluation) {
    var cacheFilePath = cacheFilesFolder.resolve(generateCachedMethodDataFileName(method));
    try {
      Map<List<Object>, Object> methodCacheMap;
      try {
        System.out.println(Files.readAllBytes(cacheFilePath));
        methodCacheMap = (Map)SerializationUtils.deserialize(Files.readAllBytes(cacheFilePath));
      } catch (NoSuchFileException e) {
        methodCacheMap = new HashMap<>();
        Files.createFile(cacheFilePath);
      }
      var result = methodCacheMap.computeIfAbsent(keyArgs, args -> resultEvaluation.get());
      SerializationUtils.serialize(cacheFilePath, methodCacheMap);
      return result;
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static String generateCachedMethodDataFileName(Method method) {
    return method.getDeclaringClass().getCanonicalName() + "#" + method.getName() + ".cache";
  }
  
}
