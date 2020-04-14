package com.example.demo;

import java.io.*;
/** 
public class CSVParser {

    private String file;
    private BufferedReader reader;

    public CSVParser(String file) {
       this.file = file;
       File csv = new File(file);
       try{
            reader = new BufferedReader(new FileReader(csv));
        } catch (Exception e) {
            reader = null;
        }
    }

    public String getHeader() {
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] cols = line.split(cvsSplitBy);
            System.out.println("Coulmn 4= " + cols[4] + " , Column 5=" + cols[5]);
        }
    }
}
*/