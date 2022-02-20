package com.progressoft;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public enum Operation {
  Z_SCORE("_z") {
    @Override
    public UnaryOperator<BigDecimal> getFunction(List<BigDecimal> values) {
      ScoringSummaryImplementation sc = new ScoringSummaryImplementation(new ArrayList<>(values));
      BigDecimal sd = sc.standardDeviation();
      BigDecimal mean = sc.mean();
      return x ->
          (x.subtract(mean)).divide(sd, MathContext.DECIMAL32).setScale(2, RoundingMode.HALF_EVEN);
    }
  },
  MINMAX("_mm") {
    @Override
    public UnaryOperator<BigDecimal> getFunction(List<BigDecimal> values) {
      ScoringSummaryImplementation sc = new ScoringSummaryImplementation(new ArrayList<>(values));
      BigDecimal max = sc.max();
      BigDecimal min = sc.min();
      return x ->
          (x.subtract(min))
              .divide(max.subtract(min), MathContext.DECIMAL32)
              .setScale(2, RoundingMode.HALF_EVEN);
    }
  },
  ;

  private final String postFix;

  Operation(String postFix) {
    this.postFix = postFix;
  }

  public abstract UnaryOperator<BigDecimal> getFunction(List<BigDecimal> values);

  public String getPostFix() {
    return postFix;
  }
}
