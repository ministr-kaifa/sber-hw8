package ru.zubkoff.sber.hw8.serialization.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class SerializationUtils {

  public static <T> void serialize(Path saveTo, T object) throws IOException {
    try(FileOutputStream fos = new FileOutputStream(saveTo.toString());) {
      serialize(fos, object);
    }
  }

  public static Object deserialize(Path loadFrom) throws IOException, ClassNotFoundException {
    try(FileInputStream fis = new FileInputStream(loadFrom.toString());) {
      return deserialize(fis);
    }
  }

  public static <T> byte[] serialize(T object) throws IOException {
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      serialize(bos, object);
      return bos.toByteArray();
    }
  }

  public static Object deserialize(byte[] bytes) throws ClassNotFoundException, IOException {
    try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);) {
      return deserialize(bis);
    }
  }

  public static Object deserialize(InputStream in) throws IOException, ClassNotFoundException {
    try (ObjectInputStream oin = new ObjectInputStream(in)) {
      return oin.readObject();
    }
  }

  public static <T> void serialize(OutputStream out, T object) throws IOException {
    try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
      oout.writeObject(object);
    }
  }

}
