package cm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Rate {
    private CarParkKind kind;
    private BigDecimal hourlyNormalRate;
    private BigDecimal hourlyReducedRate;
    private ArrayList<Period> reduced = new ArrayList<>();
    private ArrayList<Period> normal = new ArrayList<>();

    public Rate(CarParkKind kind, BigDecimal normalRate, BigDecimal reducedRate, ArrayList<Period> normalPeriods, ArrayList<Period> reducedPeriods) {
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
            throw new IllegalArgumentException("The periods overlaps");
        }

        this.kind = kind;
        this.hourlyNormalRate = normalRate;
        this.hourlyReducedRate = reducedRate;
        this.reduced = reducedPeriods;
        this.normal = normalPeriods;
    }

    /**
     * Checks if two collections of periods are valid together
     * @param periods1
     * @param periods2
     * @return true if the two collections of periods are valid together
     */
    /**
     * Checks if two collections of periods are valid together
     *
     * @param periods1
     * @param periods2
     * @return true if the two collections of periods are valid together
     */
    private boolean isValidPeriods(ArrayList<Period> periods1, ArrayList<Period> periods2) {
        Boolean isValid = true;
        int i = 0;
        while (i < periods1.size() && isValid) {
            isValid = isValidPeriod(periods1.get(i), periods2) && !overlapsWithExistingPeriod(periods1.get(i), periods1, i);
            i++;
        }
        return isValid;
    }

    /**
     * Checks if a new period overlaps with any existing periods in the list
     *
     * @param newPeriod
     * @param list
     * @param currentIndex
     * @return true if the new period overlaps with any existing periods
     */
     boolean overlapsWithExistingPeriod(Period newPeriod, List<Period> list, int currentIndex) {
        for (int i = 0; i < list.size(); i++) {
            if (i != currentIndex && newPeriod.overlaps(list.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a collection of periods is valid
     *
     * @param list the collection of periods to check
     * @return true if the periods do not overlap
     */
    private Boolean isValidPeriods(ArrayList<Period> list) {
        Boolean isValid = true;
        if (list.size() >= 2) {
            int lastIndex = list.size() - 1;
            for (int i = 0; i < lastIndex && isValid; i++) {
                isValid = isValidPeriod(list.get(i), list.subList(i + 1, lastIndex + 1));
            }
        }
        return isValid;
    }

    /**
     * Checks if a period is a valid addition to a collection of periods
     *
     * @param period the Period addition
     * @param list   the collection of periods to check
     * @return true if the period does not overlap in the collection of periods
     */
    private Boolean isValidPeriod(Period period, List<Period> list) {
        Boolean isValid = true;
        int i = 0;
        while (i < list.size() && isValid) {
            isValid = !period.overlaps(list.get(i));
            i++;
        }
        return isValid;
    }

    public BigDecimal calculate(Period periodStay) {
        int normalRateHours = periodStay.occurences(normal);
        int reducedRateHours = periodStay.occurences(reduced);

        BigDecimal totalCost = this.hourlyNormalRate.multiply(BigDecimal.valueOf(normalRateHours))
                .add(this.hourlyReducedRate.multiply(BigDecimal.valueOf(reducedRateHours)));

        if (this.kind == CarParkKind.MANAGEMENT) {
            // Apply reduction for MANAGEMENT kind
            return totalCost.max(new BigDecimal(5.00)); // Updated to use max instead of min
        } else {
            // For VISITOR and other kinds, apply reduction based on the kind
            return applyReduction(totalCost);
        }

    }

    private BigDecimal applyReduction(BigDecimal totalCost) {
        switch (this.kind) {
            case VISITOR:
                // The first 10.00 is free, 50% reduction above that
                BigDecimal reductionThreshold = new BigDecimal(10.00);
                BigDecimal freeThreshold = new BigDecimal(0.00);
                BigDecimal fiftyPercent = new BigDecimal(0.50);

                return totalCost.compareTo(reductionThreshold) <= 0 ?
                        totalCost.subtract(freeThreshold) :
                        reductionThreshold.add((totalCost.subtract(reductionThreshold)).multiply(fiftyPercent));
            case STUDENT:
                // 33% reduction on any amount above 5.50
                BigDecimal studentReductionThreshold = new BigDecimal(5.50);
                BigDecimal thirtyThreePercent = new BigDecimal(0.33);

                return totalCost.compareTo(studentReductionThreshold) <= 0 ?
                        totalCost :
                        studentReductionThreshold.add((totalCost.subtract(studentReductionThreshold)).multiply(thirtyThreePercent));
            case STAFF:
                // The maximum payable is 10.00 per day
                BigDecimal staffMaximum = new BigDecimal(10.00);
                return totalCost.min(staffMaximum);

            default:
                return totalCost;

        }
    }
}
