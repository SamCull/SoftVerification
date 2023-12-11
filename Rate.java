package cm;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Rate {
    private CarParkKind kind;
    private BigDecimal hourlyNormalRate;
    private BigDecimal hourlyReducedRate;
    private List<Period> reduced = new ArrayList<>();
    private List<Period> normal = new ArrayList<>();

    public Rate(CarParkKind kind, BigDecimal normalRate, BigDecimal reducedRate, List<Period> normalPeriods, List<Period> reducedPeriods) {
        if (reducedPeriods == null || normalPeriods == null) {
            throw new IllegalArgumentException("periods cannot be null");
        }
        if (normalRate == null || reducedRate == null) {
            throw new IllegalArgumentException("The rates cannot be null");
        }
        if (normalRate.compareTo(BigDecimal.ZERO) < 0 || reducedRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("A rate cannot be negative");
        }
        if (normalRate.compareTo(reducedRate) <= 0) {
            throw new IllegalArgumentException("The normal rate cannot be less or equal to the reduced rate");
        }
        if (!isValidPeriods(reducedPeriods) || !isValidPeriods(normalPeriods)) {
            throw new IllegalArgumentException("The periods are not valid individually");
        }
        if (!isValidPeriods(reducedPeriods, normalPeriods)) {
            throw new IllegalArgumentException("The periods overlap");
        }

        this.kind = kind;
        this.hourlyNormalRate = normalRate;
        this.hourlyReducedRate = reducedRate;
        this.reduced = reducedPeriods;
        this.normal = normalPeriods;
    }

    public BigDecimal calculate(Period periodStay) {
        RateCalculationStrategy rateCalculationStrategy = new DefaultRateCalculationStrategy();
        ReductionStrategy reductionStrategy = new VisitorReductionStrategy();

        BigDecimal totalCost = rateCalculationStrategy.calculate(hourlyNormalRate, hourlyReducedRate, normal, reduced, periodStay);
        return reductionStrategy.applyReduction(totalCost, kind);
    }

    private boolean isValidPeriods(List<Period> periods1, List<Period> periods2) {
        // Check if periods1 and periods2 overlap
        for (Period period1 : periods1) {
            for (Period period2 : periods2) {
                if (period1.overlaps(period2)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValidPeriods(List<Period> list) {
        boolean isValid = true;
        if (list.size() >= 2) {
            int lastIndex = list.size() - 1;
            for (int i = 0; i < lastIndex && isValid; i++) {
                isValid = isValidPeriod(list.get(i), list.subList(i + 1, lastIndex + 1));
            }
        }
        return isValid;
    }

    private boolean isValidPeriod(Period period, List<Period> list) {
        boolean isValid = true;
        int i = 0;
        while (i < list.size() && isValid) {
            isValid = !period.overlaps(list.get(i));
            i++;
        }
        return isValid;
    }

    boolean overlapsWithExistingPeriod(Period newPeriod, List<Period> list, int currentIndex) {
        for (int i = 0; i < list.size(); i++) {
            if (i != currentIndex && newPeriod.overlaps(list.get(i))) {
                return true;
            }
        }
        return false;
    }

    // Inner classes for strategies
    private interface RateCalculationStrategy {
        BigDecimal calculate(BigDecimal hourlyNormalRate, BigDecimal hourlyReducedRate, List<Period> normal, List<Period> reduced, Period periodStay);
    }

    private interface ReductionStrategy {
        BigDecimal applyReduction(BigDecimal totalCost, CarParkKind kind);
    }

    private class DefaultRateCalculationStrategy implements RateCalculationStrategy {
        @Override
        public BigDecimal calculate(BigDecimal hourlyNormalRate, BigDecimal hourlyReducedRate, List<Period> normal, List<Period> reduced, Period periodStay) {
            int normalRateHours = periodStay.occurences(normal);
            int reducedRateHours = periodStay.occurences(reduced);

            return hourlyNormalRate.multiply(BigDecimal.valueOf(normalRateHours))
                    .add(hourlyReducedRate.multiply(BigDecimal.valueOf(reducedRateHours)));
        }
    }

    private class VisitorReductionStrategy implements ReductionStrategy {
        @Override
        public BigDecimal applyReduction(BigDecimal totalCost, CarParkKind kind) {
            // The reduction logic for the VISITOR kind
            // ...

            return totalCost; // Placeholder, implement according to your logic
        }
    }

    // Additional inner classes for other strategies...
}
