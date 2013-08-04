package pencon;

import java.io.*;

public class PenDriveDetection
{
    String[] letters = new String[26];
    File[] drives = new File[letters.length];
    boolean[] isDrive = new boolean[letters.length];

    PenDriveDetection()
    {
        //declare variable
        int I = 0;
        
        //letters initialization for drive name
        for(char ch='A'; ch<='Z'; ch++,I++)
            letters[I] = String.valueOf(ch);
        
        //initialze for checking which drive is recently inserted
        for ( I = 0; I < letters.length; I++ ){
            drives[I] = new File(letters[I]+":");
            isDrive[I] = drives[I].canRead();        
        }

        System.out.println("DetectPenDrive => Waiting for devices....");
        //System.out.println("PenDriveDetection => Drive "+getInsertedPenDriveName()+" has been plugged in");
    }
    
    /**/
    File getInsertedPenDriveName(){
        //continuous checking for recently inserted drive
        while(true)
            for (int I = 0; I < letters.length; I++ ){
                if ( drives[I].canRead()!=isDrive[I] && drives[I].canRead()==true )
                    return drives[I];
                isDrive[I] = drives[I].canRead();
            }
    }
    
    /**/
    int getContestDurationFromPenDrive(){
        EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
        String timeFilePath = SystemInformation.penDrive.toString()+"\\Info\\Time";
        try{
            return Integer.valueOf(encryptionDecryption.readFileDecrypt(timeFilePath, encryptionDecryption.getEncryptValue(timeFilePath)));
        }catch(Exception exception){
            System.out.println("ContestInformation => "+exception);
            return -1;
        }
    }
    
}
