package com.progressoft;

import com.progressoft.tools.Normalizer;
import com.progressoft.tools.ScoringSummary;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class NormalizerImplementation implements Normalizer {

  @Override
  public ScoringSummary zscore(Path csvPath, Path destPath, String colToStandardize)
      throws IOException {
    return getScoringSummary(csvPath, destPath, colToStandardize, Operation.Z_SCORE);
  }

  @Override
  public ScoringSummary minMaxScaling(Path csvPath, Path destPath, String colToNormalize)
      throws IOException {
    return getScoringSummary(csvPath, destPath, colToNormalize, Operation.MINMAX);
  }

  private ScoringSummary getScoringSummary(
      Path csvPath, Path destPath, String colToStandardize, Operation operation)
      throws IOException {
    handleFileExceptions(csvPath, colToStandardize);
    List<List<String>> csvFile = readCsvFile(csvPath);
    List<BigDecimal> colToModify = getColumn(colToStandardize, csvFile);
    List<String> firstLine = csvFile.get(0);
    int index = firstLine.indexOf(colToStandardize) + 1;
    firstLine.add(index, colToStandardize + operation.getPostFix());
    List<BigDecimal> modifiedCol = calculate(colToModify, operation);
    writeToCsv(destPath, csvFile, index, modifiedCol);
    return new ScoringSummaryImplementation(colToModify);
  }

  private void writeToCsv(
      Path destPath, List<List<String>> csvFile, int index, List<BigDecimal> modifiedCol)
      throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(destPath)) {
      for (int i = 0; i < csvFile.size(); i++) {
        if (i > 0) {
          csvFile.get(i).add(index, String.valueOf(modifiedCol.get(i - 1)));
        }
        int finalI = i;
        csvFile
            .get(i)
            .forEach(
                x -> {
                  try {
                    if (csvFile.get(finalI).indexOf(x) == 0) {
                      writer.write(x);
                    } else {
                      writer.write("," + x);
                    }
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                });
        writer.newLine();
      }
    }
  }

  private List<BigDecimal> calculate(List<BigDecimal> values, Operation operation) {
    UnaryOperator<BigDecimal> function;
    function = operation.getFunction(values);
    return values.stream().map(function).collect(Collectors.toList());
  }

  private List<BigDecimal> getColumn(String colToNormalize, List<List<String>> csvFile) {
    List<BigDecimal> temp = new ArrayList<>();
    int colIndex = csvFile.get(0).indexOf(colToNormalize);
    csvFile.forEach(
        x -> {
          if (!x.contains(colToNormalize)) {
            temp.add(new BigDecimal(x.get(colIndex)));
          }
        });
    return temp;
  }

  private List<List<String>> readCsvFile(Path csvPath) throws IOException {
    List<List<String>> temp = new ArrayList<>();
    try (BufferedReader br = Files.newBufferedReader(csvPath)) {
      String line;
      while ((line = br.readLine()) != null) {
        temp.add(Arrays.stream(line.split(",")).collect(Collectors.toList()));
      }
    }
    return temp;
  }

  private void handleFileExceptions(Path csvPath, String colToModify) throws IOException {
    if (Files.notExists(csvPath)) {
      throw new IllegalArgumentException("source file not found");
    }
    if (!Files.readAllLines(csvPath).get(0).contains(colToModify)) {
      throw new IllegalArgumentException("column " + colToModify + " not found");
    }
  }
}
