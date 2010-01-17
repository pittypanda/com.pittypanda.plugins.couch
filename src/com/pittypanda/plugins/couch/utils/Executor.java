package com.pittypanda.plugins.couch.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Executor {
  public static boolean run(String command) {
    Runtime   run      = Runtime.getRuntime();
    
    System.out.print("Running: ");
    System.out.println(command);
    
    try {
      String line;
      Process runshell = run.exec(command);

      DataInputStream ls = new DataInputStream(runshell.getInputStream());
      BufferedReader br = new BufferedReader(new InputStreamReader(ls));

      while ((line = br.readLine()) != null) {
        System.out.println(line);
      }

      ls = new DataInputStream(runshell.getErrorStream());
      br = new BufferedReader(new InputStreamReader(ls));

      while ((line = br.readLine()) != null) { System.out.println(line); }

      runshell.waitFor();
      
      return (runshell.exitValue() == 0) ? true: false;
    } catch (InterruptedException e) {
      System.out.println("InterruptedExecption");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IOExecption");
      e.printStackTrace();
    } finally {
      System.out.println(" -- done.");
    }
    
    return false;
  }

}
