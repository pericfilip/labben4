package com.company;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

@DisplayName("Testing the program")
public class TollFeeCalculatorTest {
    private final ByteArrayOutputStream errorContent = new ByteArrayOutputStream();

    @Test
    @DisplayName("Testing the if free or paid") // this test will allways pass!
    void checkTollFreeWork() {
        LocalDateTime[] testDatum = new LocalDateTime[5];
        testDatum[0] = LocalDateTime.of(2020, 9, 26, 10, 15); //Lördag
        testDatum[1] = LocalDateTime.of(2020, 9, 27, 10, 15); //Söndag
        testDatum[2] = LocalDateTime.of(2020, 7, 1, 10, 15); //Juli månad
        testDatum[3] = LocalDateTime.of(2020, 9, 23, 10, 15); //Onsdag
        testDatum[4] = LocalDateTime.of(2020, 9, 24, 10, 15); //Torsdag

        for(int i = 0; i < testDatum.length; i++) {
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
    @DisplayName("Testing the correct dates with correct pricing") //1
    void testCorrectFeeTime() {
        LocalDateTime testDate1 = LocalDateTime.parse("2020-10-02 15:20", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        assertEquals(13, TollFeeCalculator.getTollFeePerPassing(testDate1));
        LocalDateTime testDate2 = LocalDateTime.parse("2020-10-02 09:29", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        assertEquals(8, TollFeeCalculator.getTollFeePerPassing(testDate2));
    }

    @Test
    @DisplayName("Testing passing within an hour") //2
    void testMultiblePassings() {
        LocalDateTime[] testDate = new LocalDateTime[2];
        LocalDateTime one = LocalDateTime.parse("2020-06-02 15:29", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime two = LocalDateTime.parse("2020-06-02 15:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        testDate[0] = one;
        testDate[1] = two;
        assertEquals(18, TollFeeCalculator.getTotalFeeCost(testDate), "Wrong fee for passings in an hour");
    }

    @Test
    @DisplayName("Test if file is empty") //3
    void testIfFileIsNotFound (){
        System.setErr(new PrintStream(errorContent));
        File emptyFile = new File("testData/emptyFile.txt");
        boolean r;
        try {
            r = emptyFile.createNewFile();
        } catch (IOException e){
            System.err.println("File could not be created" + emptyFile);
        }

        new TollFeeCalculator(emptyFile.getAbsolutePath());
        assertEquals("File was empty", errorContent.toString().trim());
    }

    @Test
    @DisplayName("Test if dates not in order") //4
    void testDateNotOrder(){
        LocalDateTime[] testDate = new LocalDateTime[2];
        LocalDateTime one = LocalDateTime.parse("2020-06-02 15:29", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime two = LocalDateTime.parse("2020-06-02 15:10", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        testDate [0] = one;
        testDate [1] = two;
        assertEquals(13, TollFeeCalculator.getTotalFeeCost(testDate));
    }

    @Test
    @DisplayName("test if singel value in file") //5
    void testIfSingleValueInFile(){
        System.setErr(new PrintStream(errorContent));
        File singelValueFile = new File("testData/singelValueFile.txt");
        boolean r;
        try{
            r = singelValueFile.createNewFile();
        } catch (IOException e){
            System.err.println("File could not be created" + singelValueFile);
        }

        try{
            FileWriter writer = new FileWriter(singelValueFile);
            writer.write("2020-10-01 10:34");
            writer.close();
        } catch (IOException e){
            System.err.println("Could not write to file"+ singelValueFile);
        }

        new TollFeeCalculator(singelValueFile.getAbsolutePath());
        assertEquals("", errorContent.toString().trim());
    }

    @Test
    @DisplayName("Test parsing Errors") //6
    void testParsing() {
        System.setErr(new PrintStream(errorContent));
        File parsingWrongFile = new File("testData/parsingWrongFile.txt");
        boolean r;
        try {
            r = parsingWrongFile.createNewFile();
        } catch(IOException e){
            System.err.println("Could not be created");
        }

        try {
            FileWriter writer = new FileWriter(parsingWrongFile);
            writer.write("sfsafdsfd23213");
            writer.close();
        } catch (IOException e) {
            System.err.println("Could not write to file");
        }

        new TollFeeCalculator(parsingWrongFile.getAbsolutePath());
        assertEquals("test", errorContent.toString().trim());
    }

    @Test
    @DisplayName("check single day fee") //7
    void singelDayfeeCheck() {
        LocalDateTime date = LocalDateTime.parse("2020-10-05 09:36", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime date2 = LocalDateTime.parse("2020-10-10 09:39", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime[] dateArray = new LocalDateTime[2];
        dateArray[0] = date;
        dateArray[1] = date2;
        assertEquals(8, TollFeeCalculator.getTotalFeeCost(dateArray));
    }

    @Test
    @DisplayName("Test min value") //8
    void testMinValue() {
        LocalDateTime[] dateArray = new LocalDateTime[1];
        LocalDateTime date = LocalDateTime.of(2020, 10, 02, 06, 30);
        dateArray[0] = date;
        assertEquals(13, TollFeeCalculator.getTotalFeeCost(dateArray));
    }

    @Test
    @DisplayName("Test max Value") //9
    void testMaxValue() {
        LocalDateTime[] dateArray = new LocalDateTime[7];
        LocalDateTime date1 = LocalDateTime.of(2020, 10, 02, 6, 30);
        LocalDateTime date2 = LocalDateTime.of(2020, 10, 02, 7, 31);
        LocalDateTime date3 = LocalDateTime.of(2020, 10, 02, 8, 32);
        LocalDateTime date4 = LocalDateTime.of(2020, 10, 02, 9, 33);
        LocalDateTime date5 = LocalDateTime.of(2020, 10, 02, 10, 34);
        LocalDateTime date6 = LocalDateTime.of(2020, 10, 02, 11, 35);
        LocalDateTime date7 = LocalDateTime.of(2020, 10, 02, 12, 36);
        dateArray[0] = date1;
        dateArray[1] = date2;
        dateArray[2] = date3;
        dateArray[3] = date4;
        dateArray[4] = date5;
        dateArray[5] = date6;
        dateArray[6] = date7;
        assertEquals(60, TollFeeCalculator.getTotalFeeCost(dateArray));
    }

    @Test
    @DisplayName("Test if time before 06:30 is skipped") //10
    void testIfFeeBeforeItShould() {
        LocalDateTime date1 = LocalDateTime.of(2020, 10, 02, 5, 55);
        LocalDateTime date2 = LocalDateTime.of(2020, 10, 02, 6, 31); //this date is the only one that should count
        LocalDateTime date3 = LocalDateTime.of(2020, 10, 02, 6, 56);
        LocalDateTime[] dateArray = new LocalDateTime[3];
        dateArray[0] = date1;
        dateArray[1] = date2;
        dateArray[2] = date3;
        assertEquals(13, TollFeeCalculator.getTotalFeeCost(dateArray));
    }

}







