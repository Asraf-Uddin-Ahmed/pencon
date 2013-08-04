/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author RATUL
 */
public class RemainingTime implements Runnable{
    
    String penDriveTimeFilePath = null;
    JLabel jLabelRemainingTime = null;
    
    public RemainingTime(String penDrive, JLabel jLabel) {
        penDriveTimeFilePath = penDrive+"\\Info\\Time";
        jLabelRemainingTime = jLabel;
    }
    
    /*for starting run() method*/
    void startThread(){
        new Thread(this).start();
    }
    
    /*get current time. decrease by 1 & write into file encryptly*/
    String getTime(){
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        int key = encryptionDecryption.getEncryptValue(penDriveTimeFilePath);
        
        String currentTimeString = encryptionDecryption.readFileDecrypt(penDriveTimeFilePath, key);
        int time = Integer.valueOf(currentTimeString);
        
        String showTimeString = String.valueOf(time-1);
        encryptionDecryption.writeFileEncrypt(penDriveTimeFilePath, showTimeString, key);
        return showTimeString;
    }
    
    /*formating time*/
    String formatTime(int time){
        if( time==0 )
            return "  < 1";
        if( time<0 )
            return "Finished";
        
        int hour = time/60;
        int minuet = time%60;
        String formattedTime = "";
        
        if( hour<10 )
            formattedTime += "0";
        formattedTime += String.valueOf(hour)+":";
        if( minuet<10 )
            formattedTime += "0";
        formattedTime += String.valueOf(minuet);
        
        return formattedTime;
    }
    
    public void run(){
        SystemInformation systemInformation = new SystemInformation();
        while( systemInformation.isJudge==false ){
            String timeString = null;
            try{
                timeString = getTime();
            }
            //if pen drive does not exist suddenly
            catch(Exception exception){
                System.out.println("RemainingTime => "+exception);
                //for this contest & password folder are deleted from MainPage => jLabelRemainingTimePropertyChange(evt)
                jLabelRemainingTime.setText("Finished");
                JOptionPane.showMessageDialog(null, "Pen drive not found.\nPlease contact with authority.", "Error", JOptionPane.ERROR_MESSAGE);
                break;
            }
            
            int time = Integer.valueOf(timeString);
            System.out.println("RemainingTime => "+timeString+" = "+formatTime(time));
            jLabelRemainingTime.setText(formatTime(time));
            if( timeString.equals("-1")==true )
                break;
            
            try{
                //1000 mili sec = 1 sec
                //60*1 sec = 1 min
                //thread sleep for 1 min
                Thread.sleep(60*1000);
            }catch(Exception exception){
                System.out.println("RemainingTime => "+exception);
            }
        }
    }
}
