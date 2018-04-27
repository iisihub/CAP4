import java.io.*;
import java.util.Date;

import static javafx.scene.input.KeyCode.T;

/**
 * Created by VALLA on 2017/11/22.
 */
public class WriteDummyDataToPCL {

  static FileOutputStream fo = null;

  public void writeToFile(String filePath, StringBuffer data){
    System.out.println("start output data...");
    if("".equals(filePath) || data == null)
      return;
    File targetFile = new File(filePath);
//    FileOutputStream fo = null;
    BufferedOutputStream bo = null;
    BufferedWriter bw = null;
    OutputStreamWriter ow = null;
    try {
      if(!targetFile.exists())
        targetFile.createNewFile();
      fo = new FileOutputStream(targetFile);
//      bw = new BufferedWriter(fo);
      bo = new BufferedOutputStream(fo);
      ow = new OutputStreamWriter(bo, "UTF-8");
      ow.write(data.toString());
      ow.flush();
      System.out.println("output data completed !");
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
//        bw.close();
        ow.close();
        fo.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public StringBuffer gettingData(){
    System.out.println("now getting data...");
    StringBuffer data = new StringBuffer();
    int maxRows = 1000000;
    for(int i = 1 ; i <= maxRows ; i++){
      data.append("now row No." + i);
      if(i != maxRows)
        data.append("\n");
    }
    System.out.println("getting data completed !");
    return data;
  }

  public static void testWrite(){
    System.out.println("test start...");
    long startTime = new Date().getTime();
    WriteDummyDataToPCL obj = new WriteDummyDataToPCL();

    StringBuffer data = obj.gettingData();
    obj.writeToFile("/Users/VALLA/Downloads/PCLtest.txt", data);
    long endTime = new Date().getTime();
    System.out.println("test done!");
    System.out.println("Cost time: " +(endTime - startTime)+ " ms");

  }






  public static void main (String[] args){
    Thread thread1 = new WorkThread();
    System.out.println("thread1 start");
    thread1.start();
    System.out.println("interrupt 開始執行");
    thread1.interrupt();
    try {
      fo.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("interrupt 結束執行");
    System.out.println("thread1 stop");


  }

  private static class WorkThread extends Thread{
    @Override
    public void run() {
      try {
//        for(long i = 0 ; i < Byte.MAX_VALUE && !isInterrupted();){
//          Thread.sleep(999999);
//          System.out.println("thread1 working to " + (++i));
//        }
        WriteDummyDataToPCL.testWrite();
      } catch (Exception e){
        System.out.println("interrupt excute!");
      }
    }
  }

  private class ThreadTimer extends Thread{
    public ThreadTimer(Thread targetThread){

    }

    @Override
    public boolean isInterrupted() {
      return super.isInterrupted();
    }

    @Override
    public void run() {
      for(long i = 0 ; i < Long.MAX_VALUE ; i++){
        System.out.println("thread1 working to " + i);
      }
    }
  }


}
