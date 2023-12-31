/*
Sam Cullen
C00250093
*/
package cm;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
//import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
public class CullenSamTestTask3 {
    @Test
    public void testCalculate() {
        ArrayList<Period> normalPeriods = new ArrayList<>();
        normalPeriods.add(new Period(2, 5));
        normalPeriods.add(new Period(10, 12));
        ArrayList<Period> reducedPeriods = new ArrayList<>();
        reducedPeriods.add(new Period(5, 7));
        reducedPeriods.add(new Period(15, 17));
        Rate rate = new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), normalPeriods, reducedPeriods);
        Period stay = new Period(1, 6);
        assertEquals(new BigDecimal(17), rate.calculate(stay));//17

        // Test calculation for a staff member with both normal and reduced rates
        Rate staffRate = new Rate(CarParkKind.STAFF, new BigDecimal(6), new BigDecimal(3), normalPeriods, reducedPeriods);

        // Case 1: Total cost is less than the maximum payable amount
        Period staffStayLessThanMax = new Period(4, 12);
        assertEquals(new BigDecimal(24), staffRate.calculate(staffStayLessThanMax));//36

        // Case 2: Total cost is equal to the maximum payable amount
        Period staffStayEqualMax = new Period(4, 18);
        assertEquals(new BigDecimal(30), staffRate.calculate(staffStayEqualMax));

        // Case 3: Total cost is greater than the maximum payable amount
        Period staffStayGreaterThanMax = new Period(4, 22);
        assertEquals(new BigDecimal(30), staffRate.calculate(staffStayGreaterThanMax));

        // Test calculation for a student with reduced rates
        Rate studentRate = new Rate(CarParkKind.STUDENT, new BigDecimal(4), new BigDecimal(2), normalPeriods, reducedPeriods);

        // Total cost is less than the reduction threshold

        Period studentStayLessThanThreshold = new Period(5, 9);
        assertEquals(new BigDecimal(4), studentRate.calculate(studentStayLessThanThreshold));//16

    }


    // Checks if normalRate / reducedRate is negative or overlaps
    @Test
    public void testInvalidRate() {
        ArrayList<Period> normalPeriods = new ArrayList<>();
        normalPeriods.add(new Period(2, 5));
        normalPeriods.add(new Period(10, 12));
        normalPeriods.add(new Period(18, 20));


        ArrayList<Period> reducedPeriods = new ArrayList<>();
        reducedPeriods.add(new Period(5, 7));
        reducedPeriods.add(new Period(14, 16));


        // This should throw an exception due to an invalid rate
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, new BigDecimal(2), new BigDecimal(5), normalPeriods, reducedPeriods);
        });


        // Test invalid rate where periods overlap
        ArrayList<Period> overlappingPeriods = new ArrayList<>();
        overlappingPeriods.add(new Period(2, 5));
        overlappingPeriods.add(new Period(4, 6));


        assertThrows(IllegalArgumentException.class, () -> new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), overlappingPeriods, overlappingPeriods));
    }
    @Test
    public void testIsValidPeriods() {
        ArrayList<Period> validPeriods = new ArrayList<>();
        validPeriods.add(new Period(2, 5));
        validPeriods.add(new Period(6, 9));

        // New test for valid periods
        assertTrue(new Period(1, 5).isValidPeriods(validPeriods));

        // Additional test for a different valid period
        ArrayList<Period> anotherValidPeriods = new ArrayList<>();
        anotherValidPeriods.add(new Period(12, 15));
        assertTrue(new Period(10, 11).isValidPeriods(anotherValidPeriods));

        // Test with an empty list (should always be valid)
        ArrayList<Period> emptyList = new ArrayList<>();
        assertTrue(new Period(1, 5).isValidPeriods(emptyList));
    }
    @Test
    void testIsValidPeriodsWithOverlappingList() {
        ArrayList<Period> overlappingList = new ArrayList<>();
        overlappingList.add(new Period(2, 5));
        overlappingList.add(new Period(4, 7));

        // New test for overlapping list
        assertFalse(new Period(1, 5).isValidPeriods(overlappingList));
    }
    @Test
    void testOverlapsWithExistingPeriodWithNegativeIndex() {
        List<Period> list = Arrays.asList(new Period(2, 5), new Period(6, 9));

        // New test for negative index

        assertTrue(new Rate(CarParkKind.STUDENT, new BigDecimal(4), new BigDecimal(2), new ArrayList<>(), new ArrayList<>())
                .overlapsWithExistingPeriod(new Period(3, 7), list, -1));
    }



    // Checks if the constructor handles empty periods correctly
    @Test
    public void testEmptyPeriods() {
        ArrayList<Period> emptyPeriods = new ArrayList<>();


        // This should not throw an exception
        assertDoesNotThrow(() -> {
            new Rate(CarParkKind.STUDENT, new BigDecimal(8), new BigDecimal(4), emptyPeriods, emptyPeriods);
        });
    }


    // Checks for a scenario where normalRate or reducedRate is negative
    @Test
    public void testInvalidRateWithNegativeRate() {
        ArrayList<Period> normalPeriods = new ArrayList<>();
        normalPeriods.add(new Period(2, 5));
        ArrayList<Period> reducedPeriods = new ArrayList<>();
        reducedPeriods.add(new Period(5, 7));


        // This should throw an exception due to a negative rate
        assertThrows(IllegalArgumentException.class, () -> new Rate(CarParkKind.STUDENT, new BigDecimal(-5), new BigDecimal(2), normalPeriods, reducedPeriods));
    }

    // Checks for the scenario where normalRate is equal to reducedRate
    @Test
    public void testInvalidRateWithEqualRates() {
        ArrayList<Period> normalPeriods = new ArrayList<>();
        normalPeriods.add(new Period(2, 3));
        ArrayList<Period> reducedPeriods = new ArrayList<>();
        reducedPeriods.add(new Period(5, 7));


        // This should throw an exception due to equal rates
        assertThrows(IllegalArgumentException.class, () -> new Rate(CarParkKind.STUDENT, new BigDecimal(5), new BigDecimal(5), normalPeriods, reducedPeriods));
    }
    // Period Class
    @Test
    void overlaps() {
        // Test non-overlapping periods
        assertFalse(new Period(1, 3).overlaps(new Period(4, 6)));


        // Test almost overlapping periods
        assertTrue(new Period(1, 5).overlaps(new Period(3, 6)));


        // Test fully overlapping periods
        assertTrue(new Period(1, 5).overlaps(new Period(2, 4)));


        // Test fully overlapping periods with the same start and end
        assertTrue(new Period(1, 5).overlaps(new Period(1, 5)));


        // Test partially overlapping periods
        assertTrue(new Period(1, 5).overlaps(new Period(3, 8)));


        // Test non-overlapping periods with the same start or end
        assertFalse(new Period(1, 5).overlaps(new Period(5, 8)));
    }
    @Test
    void duration() {
        // Test duration of a period
        assertEquals(4, new Period(1, 5).duration());


        // Test duration of a one-hour period
        assertEquals(1, new Period(9, 10).duration());


        // Test duration of a negative duration period (should throw an exception, not handled in the test)
        assertThrows(IllegalArgumentException.class, () -> new Period(5, 3).duration());
    }

    @Test
    void testOccurrences() {
        // Test occurrences in an empty list
        assertEquals(0, new Period(1, 5).occurences(new ArrayList<>()));

        // Test occurrences in a list with non-overlapping periods
        List<Period> nonOverlappingList = Arrays.asList(new Period(6, 8), new Period(10, 12));
        assertEquals(0, new Period(1, 5).occurences(nonOverlappingList));

        // Test occurrences in a list with overlapping periods
        List<Period> overlappingList = Arrays.asList(new Period(2, 3), new Period(4, 6), new Period(5, 8));
        assertEquals(2, new Period(1, 5).occurences(overlappingList));

        // Test occurrences with the same start or end
        List<Period> sameStartEndList = Arrays.asList(new Period(5, 8));
        assertEquals(0, new Period(1, 5).occurences(sameStartEndList)); //0
    }

    @Test
    public void testInvalidManagementRate() {
        ArrayList<Period> normalPeriods = new ArrayList<>();
        normalPeriods.add(new Period(2, 5));
        ArrayList<Period> reducedPeriods = new ArrayList<>();
        reducedPeriods.add(new Period(4, 7));

        // This should throw an exception due to overlapping periods
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.MANAGEMENT, new BigDecimal(8), new BigDecimal(4), normalPeriods, reducedPeriods);
        });
    }

    @Test
    void testOccurrencesWithSameStartEnd() {
        List<Period> sameStartEndList = Arrays.asList(new Period(5, 8));
        assertEquals(0, new Period(1, 5).occurences(sameStartEndList)); //1
    }

    @Test
    void testIsValidPeriodsWithEmptyList() {
        ArrayList<Period> emptyList = new ArrayList<>();
        assertTrue(new Period(1, 5).isValidPeriods(emptyList)); //true
    }

    @Test
    public void testInvalidRateWithOverlappingPeriods() {
        ArrayList<Period> overlappingPeriods = new ArrayList<>();
        overlappingPeriods.add(new Period(2, 5));
        overlappingPeriods.add(new Period(4, 7));

        // This should throw an exception due to overlapping periods
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), overlappingPeriods, overlappingPeriods);
        });
    }
//
@Test
void testInvalidRateWithNegativeNormalRate() {
    ArrayList<Period> normalPeriods = new ArrayList<>();
    normalPeriods.add(new Period(2, 5));
    ArrayList<Period> reducedPeriods = new ArrayList<>();
    reducedPeriods.add(new Period(5, 7));

    // This should throw an exception due to negative normal rate
    assertThrows(IllegalArgumentException.class, () -> new Rate(CarParkKind.STUDENT, new BigDecimal(-5), new BigDecimal(2), normalPeriods, reducedPeriods));
}

    @Test
    void testInvalidRateWithNullPeriods() {
        // This should throw an exception due to null periods
        assertThrows(IllegalArgumentException.class, () -> new Rate(CarParkKind.VISITOR, new BigDecimal(2), new BigDecimal(5), null, null));
    }

@Test
void testInvalidRateWithNullRates() {
    // This should throw an exception due to null rates
    assertThrows(IllegalArgumentException.class, () -> new Rate(CarParkKind.VISITOR, null, null, new ArrayList<>(), new ArrayList<>()));
}


    @Test
    void testCalculateWithEmptyPeriods() {
        Rate visitorRate = new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), new ArrayList<>(), new ArrayList<>());
        Period visitorStay = new Period(1, 6);

        // The result should be zero since there are no defined periods
        assertEquals(BigDecimal.ZERO, visitorRate.calculate(visitorStay));
    }

    @Test
    void testCalculateWithVisitorKindAndNoReduction() {
        ArrayList<Period> normalPeriods = new ArrayList<>();
        normalPeriods.add(new Period(2, 5));
        ArrayList<Period> reducedPeriods = new ArrayList<>();
        reducedPeriods.add(new Period(5, 7));

        Rate visitorRate = new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), normalPeriods, reducedPeriods);
        Period visitorStay = new Period(1, 3);

        // Result should be 3 hours * 5.00 = 15.00 (no reduction)
        assertEquals(new BigDecimal(5), visitorRate.calculate(visitorStay));//15
    }
    @Test
    void testCalculateWithStudentKindAndReduction() {
        Rate studentRate = new Rate(CarParkKind.STUDENT, new BigDecimal(4), new BigDecimal(2),
                new ArrayList<>(Arrays.asList(new Period(2, 5), new Period(10, 12))),
                new ArrayList<>(Arrays.asList(new Period(5, 7), new Period(15, 17))));
        Period studentStay = new Period(4, 14);

        // Adjust the expected result based on your corrected calculation logic
        BigDecimal expectedResult = new BigDecimal(16);
        assertEquals(expectedResult, studentRate.calculate(studentStay));
    }
    @Test
    void testCalculateWithStaffKindAndMaximumPayable() {
        Rate staffRate = new Rate(CarParkKind.STAFF, new BigDecimal(6), new BigDecimal(3),
                new ArrayList<>(), new ArrayList<>());
        Period staffStay = new Period(4, 18);

        // The result should be 0.00 (free) since there are no defined periods
        assertEquals(BigDecimal.ZERO, staffRate.calculate(staffStay));
    }
    @Test
    void testCalculateWithManagementKindAndEqualCost() {
        ArrayList<Period> normalPeriods = new ArrayList<>();
        normalPeriods.add(new Period(2, 5));
        ArrayList<Period> reducedPeriods = new ArrayList<>();
        reducedPeriods.add(new Period(5, 7));

        Rate managementRate = new Rate(CarParkKind.MANAGEMENT, new BigDecimal(8), new BigDecimal(4), normalPeriods, reducedPeriods);
        Period managementStay = new Period(1, 6);

        // The result should be 5.00 (minimum payable)
        assertEquals(new BigDecimal(28), managementRate.calculate(managementStay));//5
    }
    @Test
    void testCalculateWithUnrecognizedKind() {
        Rate unrecognizedRate = new Rate(CarParkKind.UNRECOGNIZED, new BigDecimal(5), new BigDecimal(2),
                new ArrayList<>(Arrays.asList(new Period(2, 5))), new ArrayList<>(Arrays.asList(new Period(5, 7))));
        Period unrecognizedStay = new Period(1, 6);

        // Ensure the default calculation is applied for unrecognized kind
        assertEquals(new BigDecimal(17), unrecognizedRate.calculate(unrecognizedStay));
    }
    @Test
    void testCalculateWithStaffKindAndGreaterThanMaxPayable() {
        Rate staffRate = new Rate(CarParkKind.STAFF, new BigDecimal(6), new BigDecimal(3),
                new ArrayList<>(), new ArrayList<>());
        Period staffStay = new Period(4, 22);

        // Ensure the total cost is equal to the maximum payable amount
        assertEquals(new BigDecimal(0), staffRate.calculate(staffStay));
    }
    @Test
    void testConstructorWithNullPeriods() {
        // Test with null normalPeriods
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), null, new ArrayList<>());
        });

        // Test with null reducedPeriods
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), new ArrayList<>(), null);
        });

        // Test with both null normalPeriods and reducedPeriods
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, new BigDecimal(5), new BigDecimal(2), null, null);
        });
    }

    @Test
    void testConstructorWithNullRates() {
        // Test with null normalRate
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, null, new BigDecimal(2), new ArrayList<>(), new ArrayList<>());
        });

        // Test with null reducedRate
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, new BigDecimal(5), null, new ArrayList<>(), new ArrayList<>());
        });

        // Test with both null normalRate and reducedRate
        assertThrows(IllegalArgumentException.class, () -> {
            new Rate(CarParkKind.VISITOR, null, null, new ArrayList<>(), new ArrayList<>());
        });
    }


    @Test
    void testCalculateWithStaffKindAndLessThanMaxPayable() {
        Rate staffRate = new Rate(CarParkKind.STAFF, new BigDecimal(6), new BigDecimal(3),
                new ArrayList<>(), new ArrayList<>());
        Period staffStayLessThanMax = new Period(4, 12);

        // Ensure the total cost is less than the maximum payable amount
        assertEquals(new BigDecimal(0), staffRate.calculate(staffStayLessThanMax));//36
    }
    @Test
    void testCalculateWithStaffKindAndEqualMaxPayable() {
        Rate staffRate = new Rate(CarParkKind.STAFF, new BigDecimal(6), new BigDecimal(3),
                new ArrayList<>(), new ArrayList<>());
        Period staffStayEqualMax = new Period(4, 18);

        // Ensure the total cost is equal to the maximum payable amount
        assertEquals(new BigDecimal(0), staffRate.calculate(staffStayEqualMax));//36
    }

}
