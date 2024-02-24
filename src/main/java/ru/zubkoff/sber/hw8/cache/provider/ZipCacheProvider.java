package ru.zubkoff.sber.hw8.cache.provider;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import ru.zubkoff.sber.hw8.serialization.utils.SerializationUtils;

public class ZipCacheProvider implements CacheProvider {

  private final Path cacheFilesFolder;

  public ZipCacheProvider(Path cacheFilesFolder) {
    this.cacheFilesFolder = cacheFilesFolder;
  }

  @Override
  public Object evaluateIfAbsent(Method method, List<Object> keyArgs, Supplier<Object> resultEvaluation) {
    var cacheFileName = generateCachedMethodDataFileName(method);
    var cacheArchiveFilePath = cacheFilesFolder.resolve(cacheFileName + ".zip");

    Map<List<Object>, Object> methodCacheMap;
    try {
      methodCacheMap = getCacheFromArchive(cacheArchiveFilePath, cacheFileName);
    } catch (NoSuchFileException e) {
      createCacheArchive(cacheArchiveFilePath, cacheFileName);
      methodCacheMap = new HashMap<>();
    }

    var result = methodCacheMap.computeIfAbsent(keyArgs, args -> resultEvaluation.get());
    persistCacheToArchive(methodCacheMap, cacheArchiveFilePath, cacheFileName);
    return result;

  }

  public static String generateCachedMethodDataFileName(Method method) {
    return method.getDeclaringClass().getCanonicalName() + "#" + method.getName() + ".cache";
  }

  private static void createCacheArchive(Path archiveFile, String cacheFileName) {
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archiveFile.toFile()));) {
      ZipEntry e = new ZipEntry(cacheFileName);
      out.putNextEntry(e);
      out.closeEntry();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Map<List<Object>, Object> getCacheFromArchive(Path archiveFile, String cacheFileName) throws NoSuchFileException {
    try (ZipFile archive = new ZipFile(archiveFile.toString());) {
      ZipEntry cacheFileEntry = new ZipEntry(cacheFileName);
      var cacheFileIos = archive.getInputStream(cacheFileEntry);
      var result = (Map<List<Object>, Object>) SerializationUtils.deserialize(cacheFileIos);
      cacheFileIos.close();
      return result;
    } catch (NoSuchFileException e) {
      throw e;
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static void persistCacheToArchive(Map<List<Object>, Object> methodCacheMap, Path archiveFile,
                                            String cacheFileName) {
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archiveFile.toFile()));) {
      out.putNextEntry(new ZipEntry(cacheFileName));
      SerializationUtils.serialize(out, methodCacheMap);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
