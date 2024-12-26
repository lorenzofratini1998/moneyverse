package it.moneyverse.test;

import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public abstract class CriteriaRandomGenerator<T> {

    public abstract T generate();

    protected BoundCriteria randomCriteriaBound(List<BigDecimal> values) {
        BigDecimal minBalance = findMin(values);
        BigDecimal maxBalance = findMax(values);
        BoundCriteria criteria = new BoundCriteria();
        criteria.setLower(
                RandomUtils.randomDecimal(
                                minBalance.doubleValue(),
                                maxBalance.divide(BigDecimal.TWO, RoundingMode.HALF_DOWN).doubleValue())
                        .setScale(2, RoundingMode.HALF_DOWN));
        criteria.setUpper(
                RandomUtils.randomDecimal(
                                maxBalance.divide(BigDecimal.TWO, RoundingMode.HALF_DOWN).doubleValue(),
                                maxBalance.doubleValue())
                        .setScale(2, RoundingMode.HALF_DOWN));
        return criteria;
    }

    private BigDecimal findMin(List<BigDecimal> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("List is empty or contains only null values"));
    }


    private BigDecimal findMax(List<BigDecimal> values) {
        return values.stream()
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("List is empty or contains only null values"));
    }


}
