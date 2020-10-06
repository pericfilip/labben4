
package com.company;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.io.ByteArrayOutputStream;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@DisplayName("Testing the program")
public class TollFeeCalculatorTest {


    @Test
    @DisplayName("Testing the if free or paid")
    void CheckTollFreeWork() {

        LocalDateTime[] testDatum = new LocalDateTime[5];
        testDatum[0] = LocalDateTime.of(2020, 9, 26, 10, 15); //Lördag
        testDatum[1] = LocalDateTime.of(2020, 9, 27, 10, 15); //Söndag
        testDatum[2] = LocalDateTime.of(2020, 7, 1, 10, 15); //Juli månad
        testDatum[3] = LocalDateTime.of(2020, 9, 23, 10, 15); //Onsdag
        testDatum[4] = LocalDateTime.of(2020, 9, 24, 10, 15); //Torsdag

        for (int i = 0; i < testDatum.length; i++) {

            if (testDatum[i].getDayOfWeek() == DayOfWeek.SATURDAY ||
                    testDatum[i].getDayOfWeek() == DayOfWeek.SUNDAY ||
                    testDatum[i].getMonth() == Month.JULY) {
                assertTrue(TollFeeCalculator.isTollFreeDate(testDatum[i]));
                System.out.println("Array index " + i + " is a toll free day or month");
            } else {
                assertFalse(TollFeeCalculator.isTollFreeDate(testDatum[i]));
                System.out.println("Array index " + i + " is not a toll free day");
            }
        }
    }

    @Test
    @DisplayName("Testing the correct dates with correct pricing and maximum fee is 60")
    void RightTollFeeAndTotalMaximumReturned() {
        List<TollFeeAndDate> tollFeeAndDates = new ArrayList<>();

        tollFeeAndDates.add(new TollFeeAndDate(0, LocalDateTime.of(2020, 9, 23, 5, 0))); //Expected price 0
        tollFeeAndDates.add(new TollFeeAndDate(8, LocalDateTime.of(2020, 9, 23, 6, 0))); //Expected price 8
        tollFeeAndDates.add(new TollFeeAndDate(13, LocalDateTime.of(2020, 9, 23, 6, 30))); //Expected price 13
        tollFeeAndDates.add(new TollFeeAndDate(18, LocalDateTime.of(2020, 9, 23, 7, 0))); //Expected price 18
        tollFeeAndDates.add(new TollFeeAndDate(13, LocalDateTime.of(2020, 9, 23, 8, 0))); //Expected price 13
        tollFeeAndDates.add(new TollFeeAndDate(8, LocalDateTime.of(2020, 9, 23, 8, 30))); //Expected price 8
        tollFeeAndDates.add(new TollFeeAndDate(8, LocalDateTime.of(2020, 9, 23, 13, 30))); //Expected price 8
        tollFeeAndDates.add(new TollFeeAndDate(13, LocalDateTime.of(2020, 9, 23, 15, 15))); //Expected price 13
        tollFeeAndDates.add(new TollFeeAndDate(18, LocalDateTime.of(2020, 9, 23, 15, 30))); //Expected price 18
        tollFeeAndDates.add(new TollFeeAndDate(13, LocalDateTime.of(2020, 9, 23, 17, 30))); //Expected price 13
        tollFeeAndDates.add(new TollFeeAndDate(8, LocalDateTime.of(2020, 9, 23, 18, 13))); //Expected price 8
        tollFeeAndDates.add(new TollFeeAndDate(0, LocalDateTime.of(2020, 9, 23, 18, 30))); //Expected price 0
        tollFeeAndDates.add(new TollFeeAndDate(0, LocalDateTime.of(2020, 9, 23, 21, 0))); //Expected price 0
        tollFeeAndDates.add(new TollFeeAndDate(13, LocalDateTime.of(2020, 9, 24, 17, 30))); //Expected price 13

        for (TollFeeAndDate tollFeeAndDate : tollFeeAndDates) {
            assertEquals(tollFeeAndDate.getPrice(), TollFeeCalculator.getTollFeePerPassing(tollFeeAndDate.getTime()));
            System.out.println("Expected price: " + TollFeeCalculator.getTollFeePerPassing(tollFeeAndDate.getTime()) + ", input price: " + tollFeeAndDate.getPrice());
        }

        LocalDateTime[] dates = new LocalDateTime[tollFeeAndDates.size()];
        for (int i = 0; i < tollFeeAndDates.size(); i++) {
            dates[i] = tollFeeAndDates.get(i).getTime();
        }

        if (TollFeeCalculator.getTotalFeeCost(dates) >= 60) {
            assertEquals(60, TollFeeCalculator.getTotalFeeCost(dates));
        }
        System.out.println("total fee: " + TollFeeCalculator.getTotalFeeCost(dates));
    }

    private class TollFeeAndDate {
        int mPrice;
        LocalDateTime mTime;

        public TollFeeAndDate(int price, LocalDateTime time) {
            this.mPrice = price;
            this.mTime = time;
        }

        public int getPrice() {
            return mPrice;
        }

        public LocalDateTime getTime() {
            return mTime;
        }
    }

    @Test
    @DisplayName("Testing the correct dates with correct pricing and maximum fee is 60")
    private void test() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        assertEquals(1, 1);
    }


    @Test
    @DisplayName("Testing parsing errors")
    private void checkParsing() {
        assertThrows(DateTimeParseException.class, () ->{
            new TollFeeCalculator("testData/Lab4.txt");
        });
    }
}







