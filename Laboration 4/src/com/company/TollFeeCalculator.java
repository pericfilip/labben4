package com.company;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TollFeeCalculator {

    public TollFeeCalculator(String inputFile) {
        try {
            Scanner sc = new Scanner(new File(inputFile));
            String[] dateStrings = sc.nextLine().split(", ");
            List<LocalDateTime> localDateTimes = new ArrayList<>(); //Skapar en lista istället för array för att kunna anväda mig av en annan storts forloop för enklare error hantering
            for(String date : dateStrings){//för varje string i dateStrings så kör den loopen och lägger till den i listan
                 try{
                    localDateTimes.add(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));//lägger till i listan
                }catch (Exception e){
                    continue;//om error hoppar den över elementet kan vara om där råkat tillkomma en en bokstav
                }
            }
            LocalDateTime[] dates;// Skapar en to array av typen LocalDateTime
            dates = localDateTimes.toArray(new LocalDateTime[localDateTimes.size()]); //konverterat listan till en array
            System.out.println("The total fee for the inputfile is: " + getTotalFeeCost(dates));  // fattades ": ", dvs mellanrum, bugg?
            sc.close(); // Skannern stängdes aldrig
        } catch(FileNotFoundException e) { //Tar bort IO för att få fram alla möjliga exceptions istället, då slipper man använda fler catch!
            System.err.println("Could not read file " + inputFile);
            System.out.println(e.getMessage());
        }


    }

    public static int getTotalFeeCost(LocalDateTime[] dates) {
        int totalFee = 0;
        LocalDateTime intervalStart = dates[0];
        for(LocalDateTime date: dates) {
            System.out.println(date.toString());
            long diffInMinutes = intervalStart.until(date, ChronoUnit.MINUTES);
            if(totalFee == 0 || diffInMinutes > 60) { //totalFee == 0 då det inget startvärde finns
                totalFee += getTollFeePerPassing(date);
            } else if(getTollFeePerPassing(intervalStart) < getTollFeePerPassing(date)){ //om för lite betalas, else if statement
                totalFee += getTollFeePerPassing(date) - getTollFeePerPassing(intervalStart); //nuvarande minus start för mellanskillnad
            }
            intervalStart = date; // fanns tidigare i if satsen. intervallet börjar på givet datum oavsett?
        }
        return Math.min(totalFee, 60); // math.min ska det vara då om priset överstiger 60 så skrivs 60 ut. annars totalFee
    }

    public static int getTollFeePerPassing(LocalDateTime date) {
        if (isTollFreeDate(date)) return 0;
        int hour = date.getHour();
        int minute = date.getMinute();
        if (hour == 6 && minute >= 0 && minute <= 29) return 8;
        if (hour == 6 && minute <= 29) return 8;
        else if (hour == 6) return 13;
        else if (hour == 7) return 18;
        else if (hour == 8 && minute <= 29) return 13; // jag räknar hela tidtabelle som en bugg då en hel del ändrig krävdes
        else if (hour >= 8 && hour < 15) return 8;
        else if (hour == 15 && minute <= 29) return 13;
        else if (hour == 15 || hour == 16) return 18;
        else if (hour == 17) return 13;
        else if (hour == 18 && minute <= 29) return 8;
        else return 0;
    }

    public static boolean isTollFreeDate(LocalDateTime date) {
        return date.getDayOfWeek().getValue() == 6 || date.getDayOfWeek().getValue() == 7 || date.getMonth().getValue() == 7;
    }

    public static void main(String[] args) {
        new TollFeeCalculator("testData/Lab4.txt");

    }
}

