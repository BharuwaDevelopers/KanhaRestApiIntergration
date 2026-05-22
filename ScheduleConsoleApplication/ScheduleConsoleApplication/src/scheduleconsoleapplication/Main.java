package scheduleconsoleapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    

    public static void main(String[] args) {
        Timer timer = new Timer();
        TimerTask dailyTaskSalesDivision_23_00PM = new TimerTask() {
            @Override
            public void run() {
                System.out.println("UCDF Plant Running 23:00 PM task at: " + new Date());
                try {
                    farmerCollectionApiForUCDFPlant();
                } catch (Exception e) {
                }
            }
        };
        timer.scheduleAtFixedRate(dailyTaskSalesDivision_23_00PM, getTimeAt_23_00PM(), 24L * 60 * 60 * 1000);
        
        TimerTask dailyTaskSalesDivision_23_50PM = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Nainital Plant Running 23:50 PM task at: " + new Date());
                try {
                    farmerCollectionApiForNainitalPlant();
                } catch (Exception e) {
                }
            }
        };
        timer.scheduleAtFixedRate(dailyTaskSalesDivision_23_50PM, getTimeAt_23_50PM(), 24L * 60 * 60 * 1000);
        
//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//        TimerTask taskEveryOneHour = new TimerTask() {
//            @Override
//            public void run() {
//                // System.out.println("Running task at: Every one hour");
//                System.out.println("Running task FTp Send at: every 60 Min ");
//                System.out.println("Running task at: " + new Date());
//                try {
//                    reciveInvoiceDataFromFtpServer();
//                } catch (Exception e) {
//
//                }
//            }
//        };
//        // Schedule 1: Run every one hours, starting now
//        timer.scheduleAtFixedRate(taskEveryOneHour, 0, 1 * 60 * 60 * 1000);
        // timer.scheduleAtFixedRate(task, 0, 30 * 60 * 1000); // 30 minutes in milliseconds

        //---drcr
        //---------- 12:55 AM--------------------------------------------
        // Schedule 2: Run daily at 12:55 AM
//        TimerTask dailyTask12_55 = new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("Running 12:55 AM task at: " + new Date());
//                try {
//                    reciveInvoiceDataFromFtpServer();
//                } catch (Exception e) {
//                }
//            }
//        };
//        timer.scheduleAtFixedRate(dailyTask12_55, getTimeAt12_55AM(), 24 * 60 * 60 * 1000);
//
//        TimerTask dailyTask11_30 = new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("Running 11:30 AM task at: " + new Date());
//                try {
//                    reciveInvoiceDataFromFtpServer();
//                } catch (Exception e) {
//                }
//            }
//        };
//        timer.scheduleAtFixedRate(dailyTask11_30, getTimeAt11_30AM(), 24 * 60 * 60 * 1000);
//
//
//        TimerTask dailyTask1AM = new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("Running  sales report 1:00 AM task at: " + new Date());
//                try {
//                    ReportSalesReportSendEmail();
//                } catch (Exception e) {
//                    e.printStackTrace(); // Log the exception
//                }
//            }
//        };
//        timer.scheduleAtFixedRate(dailyTask1AM, getTimeAt1AM(), 24L * 60 * 60 * 1000); // Repeat every 24 hours
//        
        
        /*
        TimerTask dailyTask4_30AM = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Running 4:30 AM task at: " + new Date());
                try {
                    ReportDairyCustmerBlanceReportSendEmail();
                } catch (Exception e) {
                }
            }
        };
        timer.scheduleAtFixedRate(dailyTask4_30AM, getTimeAt4_30AM(), 24 * 60 * 60 * 1000);
*/

     
     
      

        // every date 9:00 AM
     
      
        
        
        // every date 22:00 PM
       
        
        keepAlive();
    }

    private static Date getTimeAt11_30AM() {
        Calendar next = new GregorianCalendar();
        next.set(Calendar.HOUR_OF_DAY, 11);
        next.set(Calendar.MINUTE, 30);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        // If 12:55 AM has already passed today, schedule for tomorrow
        if (next.before(Calendar.getInstance())) {
            next.add(Calendar.DATE, 1);
        }
        return next.getTime(); // => Tomorrow at 12:55 AM
    }

    private static Date getTimeAt_9_00AM() {
       // Calendar next = new GregorianCalendar();
       Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, 9);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        // If 12:55 AM has already passed today, schedule for tomorrow
        if (next.before(Calendar.getInstance())) {
            next.add(Calendar.DATE, 1);
        }
        return next.getTime(); // => Tomorrow at 12:55 AM
    }

    private static Date getTimeAt12_55AM() {
        Calendar next = new GregorianCalendar();
        next.set(Calendar.HOUR_OF_DAY, 0);
        next.set(Calendar.MINUTE, 55);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        // If 12:55 AM has already passed today, schedule for tomorrow
        if (next.before(Calendar.getInstance())) {
            next.add(Calendar.DATE, 1);
        }
        return next.getTime(); // => Tomorrow at 12:55 AM
    }
    
    private static Date getTimeAt_23_00PM() {
       // Calendar next = new GregorianCalendar();
       Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, 23);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        // If 12:55 AM has already passed today, schedule for tomorrow
        if (next.before(Calendar.getInstance())) {
            next.add(Calendar.DATE, 1);
        }
        return next.getTime(); // => Tomorrow at 12:55 AM
    }
    
    private static Date getTimeAt_23_50PM() {
       // Calendar next = new GregorianCalendar();
       Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, 23);
        next.set(Calendar.MINUTE, 50);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        // If 12:55 AM has already passed today, schedule for tomorrow
        if (next.before(Calendar.getInstance())) {
            next.add(Calendar.DATE, 1);
        }
        return next.getTime(); // => Tomorrow at 12:55 AM
    }
    

//    private static Date getTimeAt4_30AM() {
//        Calendar next = new GregorianCalendar();
//        next.set(Calendar.HOUR_OF_DAY, 4);
//        next.set(Calendar.MINUTE, 30);
//        next.set(Calendar.SECOND, 0);
//        next.set(Calendar.MILLISECOND, 0);
//
//        // If 4:30 AM has already passed today, schedule for tomorrow
//        if (next.before(Calendar.getInstance())) {
//            next.add(Calendar.DATE, 1);
//        }
//        return next.getTime(); // => Next occurrence of 4:30 AM
//    }
    
    private static Date getTimeAt4_30AM() {
    Calendar next = Calendar.getInstance();
    next.set(Calendar.HOUR_OF_DAY, 4);
    next.set(Calendar.MINUTE, 30);
    next.set(Calendar.SECOND, 0);
    next.set(Calendar.MILLISECOND, 0);

    if (next.before(Calendar.getInstance())) {
        next.add(Calendar.DATE, 1);
    }
    return next.getTime();
}


//    private static Date getTimeAt1AM() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 1); // Set to 1:00 AM
//        calendar.set(Calendar.MINUTE, 0); // Set minutes to 0
//        calendar.set(Calendar.SECOND, 0); // Set seconds to 0
//        calendar.set(Calendar.MILLISECOND, 0); // Set milliseconds to 0
//        return calendar.getTime(); // Return the Date object
//    }
    
    private static Date getTimeAt1AM() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 1);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);

    if (cal.getTime().before(new Date())) {
        cal.add(Calendar.DATE, 1);
    }
    return cal.getTime();
}


    
    public static void farmerCollectionApiForUCDFPlant() throws Exception {
        //  String url = " http://127.0.0.1:7101/WebServiceApp/resources/GenJasper?UnitCode=13001";
        String url = "http://141.148.193.253:9073/KanhaIntegrationApiPrd/resources/FarmerCollection?unitCode=80001&empCode=E-001";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setConnectTimeout(60000);  // 1 minute to establish connection
        con.setReadTimeout(120000); 
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String status = response.toString();
        System.out.println("response----" + status);
        // JOptionPane.showMessageDialog(null,"programme run status--"+status);
    }
    
    
    public static void farmerCollectionApiForNainitalPlant() throws Exception {
        //  String url = " http://127.0.0.1:7101/WebServiceApp/resources/GenJasper?UnitCode=13001";
        String url = "http://141.148.193.253:9073/KanhaIntegrationApiPrd/resources/FarmerCollection?unitCode=70001&empCode=E-001";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setConnectTimeout(60000);  // 1 minute to establish connection
        con.setReadTimeout(120000); 
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        String status = response.toString();
        System.out.println("response----" + status);
        // JOptionPane.showMessageDialog(null,"programme run status--"+status);
    }
    
    public static void WriteErrorLogToFile(String errorMsg) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            //System.out.println(formatter.format(date));
            // String file = "/home/oracle/API_ErrorLogs/ErrorLog.txt";
            //String file = "/home/lenovo/Desktop/testing/ErrorLog.txt";
            String file = "/u01/app/backup/schedule/text.txt";
            File myObj = new File(file);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
                try {
                    FileWriter myWriter = new FileWriter(file);
                    myWriter.write(formatter.format(date) + "--" + errorMsg);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            } else {
                System.out.println("File already exists.");
                try {
                    FileWriter myWriter = new FileWriter(file);
                    myWriter.write(formatter.format(date) + "--" + errorMsg);
                    myWriter.close();
                    System.out.println("Successfully wrote to the file.");
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    private static void keepAlive() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException ignored) {}
    }
}
