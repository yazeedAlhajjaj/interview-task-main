package com.progressoft;

import com.progressoft.tools.ScoringSummary;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ScoringSummaryImplementation implements ScoringSummary {

  private final List<BigDecimal> values;
  private BigDecimal mean;
  private int size;
  private BigDecimal variance;
  private BigDecimal min;
  private BigDecimal max;

  public ScoringSummaryImplementation(List<BigDecimal> values) {
    this.values = values;
  }

  @Override
  public BigDecimal mean() {
    if (mean==null) {
      BigDecimal reduce =
          values.stream().map(Objects::requireNonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
      size = values.size();
      mean =
          reduce
              .divide(new BigDecimal(size), MathContext.DECIMAL32)
              .setScale(0, RoundingMode.HALF_EVEN);
      mean = mean.setScale(2, RoundingMode.HALF_EVEN);
    }
    return mean;
  }

  @Override
  public BigDecimal standardDeviation() {
    if (variance == null) {
      variance = variance();
    }
    return BigDecimal.valueOf(Math.sqrt(variance.doubleValue()))
        .setScale(2, RoundingMode.HALF_EVEN);
  }

  @Override
  public BigDecimal variance() {
    if (variance==null) {
      if (mean == null) {
        mean = mean();
      }
      BigDecimal sum = BigDecimal.ZERO;
      for (BigDecimal x : values) {
        BigDecimal subtract = x.subtract(mean);
        sum = sum.add(subtract.multiply(subtract));
      }
      variance =
          sum.divide(BigDecimal.valueOf(size), MathContext.DECIMAL32)
              .setScale(0, RoundingMode.HALF_EVEN);
      variance = variance.setScale(2, RoundingMode.HALF_EVEN);
    }
    return variance;
  }

  @Override
  public BigDecimal median() {
    values.sort(BigDecimal::compareTo);
    BigDecimal median;
    if (size % 2 == 0) {
      median =
          (values.get(size / 2).add(values.get((size / 2) - 1)))
              .divide(new BigDecimal(2), MathContext.DECIMAL32);
    } else {
      median = values.get(size / 2);
    }
    median = median.setScale(2, RoundingMode.HALF_EVEN);
    return median;
  }

  @Override
  public BigDecimal min() {
    if (min == null) {

      Optional<BigDecimal> minV = values.stream().min(BigDecimal::compareTo);
      min = minV.map(x -> x.setScale(2, RoundingMode.HALF_EVEN)).orElse(null);
    }
    return min;
  }

  @Override
  public BigDecimal max() {
    if (max == null) {
      Optional<BigDecimal> maxV = values.stream().max(BigDecimal::compareTo);
      max = maxV.map(x -> x.setScale(2, RoundingMode.HALF_EVEN)).orElse(null);
    }
    return max;
  }
}
