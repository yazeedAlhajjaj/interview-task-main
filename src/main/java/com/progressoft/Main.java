package com.progressoft;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
  public static void main(String[] args) throws IOException {
    NormalizerImplementation nI = new NormalizerImplementation();
    Path source = Paths.get(args[0]);
    Path dest = Paths.get(args[1]);
    String col = args[2];
    String method=args[3];
    if (method.equals("min-max")) {
      nI.minMaxScaling(source, dest, col);
    }else if (method.equals("z-score")){
      nI.zscore(source, dest, col);
    }
  }
}
