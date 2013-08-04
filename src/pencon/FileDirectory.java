package pencon;

import java.io.*;
import javax.swing.*;
import java.util.*;

class FileDirectory{

/*directory deleting*/
	public boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success)
	                return false;
	        }
	    }

	    // The directory is now empty so delete it
	    return dir.delete();
	}

/*creating directory*/
	public boolean makeDirs(String dir){
            return new File(dir).mkdirs();
	}

/*creating file in append mode*/
	public void makeFileAppend(String path, String text, boolean append)throws IOException{
                PrintWriter pw = new PrintWriter(new FileWriter(new File(path),append));
		pw.println(text);
		pw.close();
	}
/*creating file utf-8 format*/
	public void makeFileUtf(String filePath, String text)throws IOException{
		PrintWriter pw = new PrintWriter(filePath, "utf8");
		pw.println(text);
		pw.close();
	}
        
/*checking file or directory are exist or not*/
        public boolean isExist(String path){
            return new File(path).exists();
        }

/*read a file & return a string*/
        public String getFileString(String path, String newLine) throws IOException
	{
		String text = "";
                String str;
                BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(path), "UTF8"));
                while( (str=br.readLine())!=null )
                        text += (str+newLine);
                br.close();
		return text;
	}
   
/*read a file & return a vector*/
        public Vector getFileVector(String path) throws IOException
	{
		Vector lines = new Vector();
                String str;
                BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(path), "UTF8"));
                while( (str=br.readLine())!=null )
                        lines.add(str);
                br.close();
		return lines;
	}
        
/*get all subdirectory & file name */
        String[] getDirectoryChildName(String path){
            return new File(path).list();
        }

/*get the numbers of subdirecotries & files*/
        int getDirectoryChildNumber(String path){
            return new File(path).list().length;
        }
        
/*add file*/
    long add(String destPath, String fileName){
        JFileChooser jfc = new JFileChooser();
        jfc.setApproveButtonText("Add");
        jfc.setDialogTitle("Add");
        
        //file add button clicked.
        int ret = jfc.showDialog(jfc, null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            try {
                copyFolder(new File(jfc.getSelectedFile().getPath()), new File(destPath+"\\"+fileName));
            }
            catch (Exception ex) {
                //Logger.getLogger(SearchPage.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "File doesn't exist.\nPlease select a file.", "File not fuond !!", JOptionPane.ERROR_MESSAGE);
            }
        }
        return jfc.getSelectedFile().length();
    }
    
/*select file*/
    String selectFile(String operation, String forWhat){
        JFileChooser jfc = new JFileChooser();
        jfc.setApproveButtonText(operation);
        jfc.setDialogTitle(operation+" "+forWhat);
        
        //file add button clicked.
        int ret = jfc.showDialog(jfc, null);
        if (ret == JFileChooser.APPROVE_OPTION) {
            //checking fileSize
            long fileSize = jfc.getSelectedFile().length(); //fileSize in Bytes
            return jfc.getSelectedFile().getPath();
        }
        return "";
    }
    
    /* copy folder, sub-folder & its components
     * replace if file or folder exists
     * does not delete previous files & folders if exist
   **/ 
   public void copyFolder(File source, File destination) throws IOException{
        if(source.isDirectory()){
            //if directory not exists, create it
            if(!destination.exists()){
               destination.mkdir();
               System.out.println("Directory copied from "+ source + "  to " + destination);
            }
            
            //list all the directory contents
            String files[] = source.list();

            for (String file : files) {
               //construct the source and destination file structure
               File sourceFile = new File(source, file);
               File destinationFile = new File(destination, file);
               //recursive copy
               copyFolder(sourceFile,destinationFile);
            }
        }
        else{
            //if file, then copy it
            //Use bytes stream to support all file types
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(destination);

            // Transfer bytes from in to out
            byte[] buffer = new byte[1024];
            int length;
            //copy the file content in bytes
            while ((length = in.read(buffer)) > 0){
               out.write(buffer, 0, length);
            }

            in.close();
            out.close();
            System.out.println("File copied from " + source + " to " + destination);
    	}
    }
   
/*rename file/folder*/
   boolean rename(String oldName, String newName){
       return new File(oldName).renameTo(new File(newName));
   }
}
