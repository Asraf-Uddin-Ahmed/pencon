/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pencon;

import java.io.*;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
/**
 *
 * @author RATUL
 */
public class EncryptionDecryption {
    
    /*get encrypt value from path*/
    int getEncryptValue(String path){
        int value = 0;
        for(int I=path.length()-1; I>=0&&path.charAt(I)!='\\'; I--){
            value += path.charAt(I);
            value %= 128;
        }
        return value;
    }
    
    /*get encrypt text*/
    String encrypt(int value, String text){
        if( text==null )
            return null;
        
        String encryptText = "";
        for(int I=0; I<text.length(); I++)
            encryptText += (char)((text.charAt(I)+value));
        return encryptText;
    }
    
    /*get decrypt text*/
    String decrypt(int value, String text){
        if( text==null )
            return null;
        
        String decryptText = "";
        for(int I=0; I<text.length(); I++)
            decryptText += (char)((text.charAt(I))-value);
        return decryptText;
    }
    
    /*write encrypted file*/
    void writeFileEncrypt(String path, String text, int value){
        FileDirectory fd = new FileDirectory();
        try{
            fd.makeFileUtf(path, encrypt(value, text));
        }catch(IOException ioe){
            System.out.println("ProblemSet => "+ioe);
        }
    }
    
    /*get decrypt file*/
    String readFileDecrypt(String path, int value){
        FileDirectory fd = new FileDirectory();
        String text = null;
        try{
            text = fd.getFileString(path, "");
        }catch(IOException ioe){
            System.out.println("ProblemSet => "+ioe);
        }
        return decrypt(value, text);
    }
    
    /*make a encrypted copy of file*/
    /*void makeEncryptedFile(String sourcePath, String destPath) throws IOException{
        int keyValue = getEncryptValue(destPath);
        //System.out.println(destPath+" -> "+keyValue);
        FileDirectory fileDirectory = new FileDirectory();
        
        String line = null;
        BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(sourcePath), "UTF8"));
        if( (line=br.readLine())!=null )
            fileDirectory.makeFileAppend(destPath, encrypt(keyValue, line), false);
        while( (line=br.readLine())!=null ){
            fileDirectory.makeFileAppend(destPath, encrypt(keyValue, line), true);
        }
        br.close();
    }
    
    /*make a decrypted copy of file*/
    /*void makeDecryptedFile(String sourcePath, String destPath) throws IOException{
        int keyValue = getEncryptValue(sourcePath);
        //System.out.println(destPath+" -> "+keyValue);
        FileDirectory fileDirectory = new FileDirectory();
        
        String line = null;
        BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(sourcePath), "UTF8"));
        if( (line=br.readLine())!=null )
            fileDirectory.makeFileAppend(destPath, encrypt(keyValue, line), false);
        while( (line=br.readLine())!=null ){
            fileDirectory.makeFileAppend(destPath, decrypt(keyValue, line), true);
        }
        br.close();
    }
    
    
    /*make encrypted/decrypted file*/
    void makeFastestEncryptDecryptFile(String inFilePath, String outFilePath, boolean isEncrypt) throws Exception{
        FileInputStream inFile = new FileInputStream(inFilePath);
        FileOutputStream outFile = new FileOutputStream(outFilePath);
        
        //password needs to generates a secret key.
        // mainFile <encryption> encryptFile <decryption> decryptFile(mainFile)
        //middle one is common. So value of encrypted file name is a password.
        String password = null;
        int encryptDecryptValue;
        if( isEncrypt==true){
            password = String.valueOf(getEncryptValue(outFilePath));
            encryptDecryptValue = Cipher.ENCRYPT_MODE;
        }
        else{
            password = String.valueOf(getEncryptValue(inFilePath));
            encryptDecryptValue = Cipher.DECRYPT_MODE;
        }
        
        //to create a key based on a password. The password is passed as a character array
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey passwordKey = keyFactory.generateSecret(keySpec);
        
        //for selecting algorithm
        //PBE = hashing + symmetric encryption.  
        //A 64 bit random number (the salt) is added to the password and 
        //hashed using a Message Digest Algorithm (MD5 in this example.).
        //The number of times the password is hashed is determined by the interation count.  
        //Adding a random number and hashing multiple times enlarges the key space.
        byte[] salt = new byte[8];
        int iterations = 100;
        PBEParameterSpec parameterSpec = new PBEParameterSpec(salt, iterations);
        
        // Create the cipher
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        //initialize it for encryption/decryption.
        cipher.init(encryptDecryptValue, passwordKey, parameterSpec);
        
        // Read the file and encrypt/decrypt its bytes.
        byte[] input = new byte[64];
        int bytesRead;
        while ((bytesRead = inFile.read(input)) != -1){
         byte[] output = cipher.update(input, 0, bytesRead);
         if (output != null) 
             outFile.write(output);
        }

        byte[] output = cipher.doFinal();
        if (output != null) 
          outFile.write(output);

        inFile.close();
        outFile.flush();
        outFile.close();
    }
}
