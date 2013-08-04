/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.util.Vector;

/**
 *
 * @author RATUL
 */
public class MemoryUsage implements Runnable{
    
    Thread threadMemorySize;
    private Vector mainTaskHistories = new Vector();
    
    /*maximum memory size of process Main.exe given in KB*/
    public int maximumMemoryUsage(){
        int maxMemoryUsage = 0;
        for(Object mainTaskHistory : mainTaskHistories){
            String history = mainTaskHistory.toString();
            //parse string categorywise
            String categories[] = history.split(",", 5);
            //take memory usage by process Main.exe
            String memoryHistory = categories[categories.length-1];
            //numerical string conversion only
            memoryHistory = memoryHistory.replaceAll("\"", "").replaceAll(",", "").replaceAll("K", "").replaceAll(" ", "");
            //convert into int data type
            int memoryUsage = 0;
            try{
                memoryUsage = Integer.valueOf(memoryHistory);
                //take max value of memory usage
                maxMemoryUsage = memoryUsage>maxMemoryUsage ? memoryUsage : maxMemoryUsage;
                System.out.println("maxMemoryUsage() => "+memoryUsage+" "+maxMemoryUsage+" in KB");
            }catch(NumberFormatException numberFormatException){
                System.out.println("maxMemoryUsage() => "+numberFormatException);
            }
        } 
        return maxMemoryUsage;
    }
    
    public MemoryUsage() {
        threadMemorySize = new Thread(this);
        threadMemorySize.start();
    }
    
    
    public void run(){
        //print memory size
        System.out.println("<MemorySize>");
        try{
            String status = null;
            BatchFileExecution ebf = new BatchFileExecution();
            //for XP
            while( (status=ebf.inputString("tasklist /nh /fo csv /fi \"imagename eq Main.exe\""))!=null ){
                System.out.println("MemoryUsage => "+status);
                //for WIN 7
                if( status.charAt(0)=='I' )
                    break;
                mainTaskHistories.add(status);
            }
        }catch(Exception e){
            System.out.println("MemoryUsage => "+e);
        }
        threadMemorySize.stop();
    }
}
