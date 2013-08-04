/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainPage.java
 *
 * Created on Mar 7, 2012, 8:32:41 PM
 */
package pencon;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.io.*;
import javax.swing.JOptionPane;
import java.util.*;

/**
 *
 * @author RATUL
 */
public class MainPage extends javax.swing.JFrame implements Runnable{

    boolean isVerdict = false;
    String mainPath = null;
    String fileName = "Main";
    String inFile = null;
    String extension = "cpp";
    Color foregroundColor = Color.white;
    String judgeOutputPath = null;
    int initialContestDuration = 0;
    int penaltyTimes[];
    
    /** Creates new form MainPage */
    public MainPage() {
        initComponents();
        setLocationRelativeTo(null);
        
        //initialization
        SystemInformation systemInformation = new SystemInformation();
        mainPath = systemInformation.mainPath;
        if( SystemInformation.isJudge==true )
            SystemInformation.penDrive = null;
        
        //select c++ compiler
        jRadioButtonCpp.setSelected(true);
        jRadioButtonCppActionPerformed(null);
        
        //active manual judge
        jCheckBoxManualJudge.setSelected(true);
        jCheckBoxManualJudgeActionPerformed(null);
        
        //inactive editor 
        editable(false);
        
        //problem set combo box
        ProblemSet problemSet = new ProblemSet();
        problemSet.printProblemList(jComboBoxSelectProblem);
        
        //show remaining time
        if( SystemInformation.penDrive!=null ){
            int totalProblem = 0;
            try{
                totalProblem = new FileDirectory().getDirectoryChildNumber(SystemInformation.penDrive.toString()+"\\Contest");
                penaltyTimes = new int[totalProblem];
                new CutCopyFromPenDrive();
                initialContestDuration = new PenDriveDetection().getContestDurationFromPenDrive();
                new RemainingTime(systemInformation.penDrive.toString(), jLabelRemainingTime).startThread();
                //initialize submission time
                systemInformation.totalSubmission = 0;
            }catch(NullPointerException nullPointerException){
                JOptionPane.showMessageDialog(rootPane, "Pen drive does not ready for contest.\nPlease contact with authority.", "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("MainPage => "+nullPointerException);
            }
        }
        
        //set initial info
        jTextPaneEditor.setText("/** PenCon editor.\n * First active editable below this window.\n * code here :\n */\n");
        activeWindow();
        
    }

    
    /* compiling file */
    private boolean compileFile(){
        saveFile();
        
        // determining c++ or java file for compile
        SystemInformation systemInformation = new SystemInformation();
        String batFileName = mainPath+"\\batch\\compile.bat";
        String runFile = null;
        String instruction = null;
        String compilerPath = null;
        if( extension.equals("cpp")==true ){
            runFile = mainPath+"\\src\\"+fileName+".exe";
            compilerPath = systemInformation.cppCompilerPath;
            instruction = "g++ \"" + mainPath+"\\src\\"+fileName+".cpp\" -o \"" + mainPath+"\\src\\"+fileName+".exe\"";
        }
        else{
            runFile = mainPath+"\\src\\"+fileName+".class";
            compilerPath = systemInformation.javaCompilerPath;
            instruction = "javac \""+mainPath+"\\src\\"+fileName+".java\"";
        }
        
        // executing batch
        BatchFileExecution ebf = new BatchFileExecution();
        try{
            ebf.creatingCompileBat(batFileName, compilerPath, instruction);
            if( ebf.isError(batFileName)==true && new FileDirectory().isExist(runFile)==false ){
                JOptionPane.showMessageDialog(rootPane, "Compilation Error\n"+ebf.errorMessage, "Verdict", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            else{
                JOptionPane.showMessageDialog(rootPane, "Compiled :-)", "Successful", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /*run compiled file*/
    private void runFile(String tmpFile){
        try{
            if( tmpFile.equals("")==true ){
                return;
            }
        }catch(NullPointerException npe){
            JOptionPane.showMessageDialog(rootPane, "Sorry, you did not select any problem.", 
                    "Problem Selection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SystemInformation.verdict = null;
        inFile = "\"" + tmpFile + "\"";
        /*System.out.println("runFile() -> "+tmpFile);
        if( new FileDirectory().isExist(tmpFile)==false ){
            JOptionPane.showMessageDialog(rootPane, "Input file not found.\nPlease check the path of input file.", "File Not Found", JOptionPane.ERROR_MESSAGE);
            return ;
        }*/
        
        Thread t = new Thread(this);
        t.start();
    }
    
    /*check output file with supplied file*/
    private void checkFile(){
        String path1 = mainPath+"\\src\\out.txt";
        String path2 = judgeOutputPath;
        FileDirectory fd = new FileDirectory();
        Vector content1 = new Vector();
        Vector content2 = new Vector();
        long size1 = new File(path1).length();
        long size2 = new File(path2).length();
        //System.out.println(new File(path1).length()==new File(path2).length());
        
        //if file sizes are same
        if( size1==size2 ){
            //get content of file in vector
            try{
                content1 = fd.getFileVector(path1);
                content2 = fd.getFileVector(path2);
            }catch(Exception e){
                System.out.println(e);
                JOptionPane.showMessageDialog(rootPane, "Output file not found.\nPlease check the path of output file.", 
                        "File Not Found", JOptionPane.ERROR_MESSAGE);
                return ;
            }
        }
        
        //checking with other
        if( SystemInformation.verdict==null ){
            if( content1.isEmpty()==false && content1.equals(content2)==true )
                SystemInformation.verdict = "AC";
            else
                SystemInformation.verdict = "WA";
        }
    }
    
    /*save file on default path from PenCon editor*/
    boolean saveFile(){
        String sourceFileName = fileName+"."+extension;
        String sourceCode = jTextPaneEditor.getText();
        String filePath = mainPath+"\\src";
        try{
            FileDirectory fd = new FileDirectory();
            fd.deleteDir(new File(filePath));
            fd.makeDirs(filePath);
            fd.makeFileUtf(filePath+"\\"+sourceFileName, sourceCode);
            return true;
        }catch(IOException ioe){
            System.out.println(ioe);
            return false;
        }
    }
    
    /*add file on default path*/
    void addFile(){
        judgeOutputPath = null;
        String sourceFileName = fileName+"."+extension;

        FileDirectory fd = new FileDirectory();
        boolean ans = fd.deleteDir(new File(mainPath+"\\src"));
        System.out.println(mainPath+"\\src delete = "+ans);
        fd.makeDirs(mainPath+"\\src");
        
        long fileSize = fd.add(mainPath+"\\src",sourceFileName);
        if( fileSize>1024*50 ){
            JOptionPane.showMessageDialog(null, "Unable to show selected file.\nFile size is huge."
                    + "\nPlease choose a file less than 50KB.", "Error", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        
        try{
            String text = fd.getFileString(mainPath+"\\src\\"+sourceFileName, "\n");
            jTextPaneEditor.setText(text);
            textColoring();
        }catch(IOException ioe){
            jTextPaneEditor.setText("File adding error :(");
        }
    }
    
    /*save file on user desired path*/
    void saveAs(){
        FileDirectory fd = new FileDirectory();
        String sourceFileName = fileName+"."+extension;
        String sourceCode = null;
        try{
            saveFile();
            sourceCode = fd.getFileString(mainPath+"\\src\\"+sourceFileName, "\n");
            String savePath = fd.selectFile("Save", "As...");
            if( savePath.equals("")==true )
                return;
            fd.makeFileUtf(savePath, sourceCode);
            JOptionPane.showMessageDialog(rootPane, "Saved");
        }catch(IOException ioe){
            System.out.println(ioe);
        }
    }
    
    /*active/inactive PenCon editor*/
    void editable(boolean edit){
        jTextPaneEditor.setEditable(edit);
        jCheckBoxEditable.setSelected(edit);
        jCheckBoxMenuItemEditable.setSelected(edit);
    }
    
    /*window closing message*/
    void closingWindow(){
        int result = JOptionPane.showConfirmDialog(rootPane, "The contest data will be lost.\nDo you want to close it?", 
                "Closing PenCon", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if( result==0 ){
            //contest is running. then delete contest & password folder by using jLabelRemainingTimePropertyChange(evt)
            if( jLabelRemainingTime.getText().isEmpty()==false ){
                SystemInformation systemInformation = new SystemInformation();
                systemInformation.removeContestPasswordFolder();
                System.out.println("MainPage => Contest abort");
            }
            //this.dispose();   //ACTIVE when all processes are demolished
            System.exit(1);     //ACTIVE when any processes are not demolished
        }
    }
    
    /**/
    void textColoring()
    {
        int index = jTextPaneEditor.getCaretPosition();
        //System.out.println("textCpp() -> "+index);
        String text = jTextPaneEditor.getText();
        jTextPaneEditor.setText(null);
        new StyleEditor(jTextPaneEditor, text, foregroundColor);
        jTextPaneEditor.setCaretPosition(index);
    }
    
    /**/
    void activeWindow(){
        SystemInformation userBoundary = new SystemInformation();
        System.out.println("Mainpage => "+userBoundary.isJudge);
        jMenuArrangecontest.setEnabled(userBoundary.isJudge);
        //jButtonOutput.setEnabled(userBoundary.isJudge);
        if( userBoundary.isJudge==true ){
            this.setTitle("PenCon (Judge)");
            jButtonSwitch.setToolTipText("Switch to Contestant");
        }
        else{
            this.setTitle("PenCon (Contestant)");
            jButtonSwitch.setToolTipText("Switch to Judge");
            //System.out.println("MainPage => activeWindow() -> "+SystemInformation.penDrive);
            if( SystemInformation.penDrive!=null )
                jButtonVerdict.setText("Submit");
        }
    }
    
    
    /*return true to dispose time execution window*/
    boolean showVerdict(int timeLimit, int memoryLimit, int memoryUsed){
        System.out.println("MainPage => showVerdict() -> "+SystemInformation.verdict);
        //AUTO judge verdict
        if( isVerdict==true ){
            char problemId = new ProblemSet().getSelectedProblemId(jComboBoxSelectProblem);
            String verdictTitle = "Verdict (Problem " + String.valueOf(problemId) + ")";
            if( SystemInformation.verdict!=null && SystemInformation.verdict.equals("AC")==true ){
                JOptionPane.showMessageDialog(rootPane, "ACCEPTED :-)", verdictTitle, JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            else{
                if( SystemInformation.verdict==null || SystemInformation.verdict.equals("SE")==true )
                    JOptionPane.showMessageDialog(rootPane, "Unable to run.\nPlease try later or contact with authority.", "System Error", JOptionPane.ERROR_MESSAGE);
                else if( SystemInformation.verdict.equals("TLE")==true ) 
                    JOptionPane.showMessageDialog(rootPane, "Time Limit Exceeded\n\nTime Limit : "+timeLimit+" sec", verdictTitle, JOptionPane.ERROR_MESSAGE);
                else if( SystemInformation.verdict.equals("MLE")==true ) 
                    JOptionPane.showMessageDialog(rootPane, "Memory Limit Exceeded\n\nMemory Limit : "+memoryLimit+" KB\nMemory Used : "+memoryUsed+" KB", verdictTitle, JOptionPane.ERROR_MESSAGE);
                else if( SystemInformation.verdict.equals("RTE")==true )
                    JOptionPane.showMessageDialog(null, "Runtime Error", verdictTitle, JOptionPane.ERROR_MESSAGE);
                else if( SystemInformation.verdict.equals("WA")==true ) 
                    JOptionPane.showMessageDialog(rootPane, "Wrong Answer", verdictTitle, JOptionPane.ERROR_MESSAGE);           
                return true;
            }
        }
        //MANUAL judge run time verdict
        //successful source code running return null value
        else if( SystemInformation.verdict!=null ){
            if( SystemInformation.verdict.equals("SE")==true )
                JOptionPane.showMessageDialog(rootPane, "Unable to run.\nPlease try later or contact with authority.", "System Error", JOptionPane.ERROR_MESSAGE);
            else if( SystemInformation.verdict.equals("RTE")==true )
                JOptionPane.showMessageDialog(null, "Runtime Error", "Verdict", JOptionPane.ERROR_MESSAGE);
            else if( SystemInformation.verdict.equals("AC")==true )
                JOptionPane.showMessageDialog(rootPane, "ACCEPTED :-)", "Verdict", JOptionPane.INFORMATION_MESSAGE);
            else if( SystemInformation.verdict.equals("WA")==true ) 
                JOptionPane.showMessageDialog(rootPane, "Wrong Answer", "Verdict", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    /**/
    void saveVerdict(String verdict){
        int remainingDuration = new PenDriveDetection().getContestDurationFromPenDrive();
        System.out.println("MainPage => "+initialContestDuration+" "+remainingDuration);
        //if pen drive not found
        if( remainingDuration<0 ){
            jLabelRemainingTime.setText("Finished");
            return ;
        }
        
        int submissionTimeI = initialContestDuration-remainingDuration;
        char problemId = new ProblemSet().getSelectedProblemId(jComboBoxSelectProblem);
        int problemIdIndex = problemId-'A';
        
        //penalty time calculation
        String penaltyTime = "-";
        //if it is AC once
        if( penaltyTimes[problemIdIndex]!=-1 ){
            if( verdict.equals("AC")==true ){
                penaltyTime = String.valueOf( penaltyTimes[problemIdIndex]+submissionTimeI );
                penaltyTimes[problemIdIndex] = -1;
            }
            //if problem is not AC. after AC it will not work for that -1 is use.
            else {
                penaltyTimes[problemIdIndex] += 20;
            }
        }
        
        //fomating string for file saving
        String problemIdString = String.valueOf(problemId);
        String submissionTimeString = new RemainingTime(null, null).formatTime(submissionTimeI);
        
        String submissionHistory = problemIdString + " " + submissionTimeString + " " + verdict + " " + penaltyTime;
        //saving verdict. encrypt it.
        try{
            String resultFilePath = SystemInformation.penDrive.toString()+"\\Info\\Result.txt";
            EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
            String submissionEncrypt = encryptionDecryption.encrypt(encryptionDecryption.getEncryptValue(resultFilePath), submissionHistory);;
            new FileDirectory().makeFileAppend(resultFilePath, submissionEncrypt, true);
            SystemInformation.totalSubmission++;
        }
        //if pen drive not found
        catch(Exception exception){
            System.out.println("MainPage => "+exception);
            jLabelRemainingTime.setText("Finished");
        }
        System.out.println(submissionHistory);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabelCompiler = new javax.swing.JLabel();
        jRadioButtonCpp = new javax.swing.JRadioButton();
        jRadioButtonJava = new javax.swing.JRadioButton();
        jButtonAddFile = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JSeparator();
        jButtonCompiler = new javax.swing.JButton();
        jButtonRun = new javax.swing.JButton();
        jButtonCheck = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        jCheckBoxManualJudge = new javax.swing.JCheckBox();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jCheckBoxAutoJudge = new javax.swing.JCheckBox();
        jComboBoxSelectProblem = new javax.swing.JComboBox();
        jButtonVerdict = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JSeparator();
        jButtonOutput = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonSaveAs = new javax.swing.JButton();
        jCheckBoxEditable = new javax.swing.JCheckBox();
        jLabelCopyright = new javax.swing.JLabel();
        jLabelSelectProblem = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPaneEditor = new javax.swing.JTextPane();
        jButtonSwitch = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JSeparator();
        jLabelRemainingTime = new javax.swing.JLabel();
        jButtonResult = new javax.swing.JButton();
        jSeparator13 = new javax.swing.JSeparator();
        jMenuBarMainPage = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemAdd = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jMenuItemFontSize = new javax.swing.JMenuItem();
        jMenuBackground = new javax.swing.JMenu();
        jRadioButtonMenuItemWhite = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemBlack = new javax.swing.JRadioButtonMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItemEditable = new javax.swing.JCheckBoxMenuItem();
        jMenuArrangecontest = new javax.swing.JMenu();
        jMenuItemAddNewProblem = new javax.swing.JMenuItem();
        jMenuItemUpdateProblem = new javax.swing.JMenuItem();
        jMenuItemRemoveProblem = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jMenuItemActiveContest = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        jMenuItemRankList = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemUserManual = new javax.swing.JMenuItem();
        jMenuItemCredits = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("PenCon");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabelCompiler.setText("Compiler :");

        buttonGroup1.add(jRadioButtonCpp);
        jRadioButtonCpp.setText("C++");
        jRadioButtonCpp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCppActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButtonJava);
        jRadioButtonJava.setText("Java");
        jRadioButtonJava.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonJavaActionPerformed(evt);
            }
        });

        jButtonAddFile.setText("Add File");
        jButtonAddFile.setToolTipText("Add file to editor");
        jButtonAddFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddFileActionPerformed(evt);
            }
        });

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButtonCompiler.setText("Compile");
        jButtonCompiler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCompilerActionPerformed(evt);
            }
        });

        jButtonRun.setText("Run");
        jButtonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRunActionPerformed(evt);
            }
        });

        jButtonCheck.setText("Check");
        jButtonCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCheckActionPerformed(evt);
            }
        });

        buttonGroup2.add(jCheckBoxManualJudge);
        jCheckBoxManualJudge.setText("Manual Judge");
        jCheckBoxManualJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxManualJudgeActionPerformed(evt);
            }
        });

        jSeparator5.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);

        buttonGroup2.add(jCheckBoxAutoJudge);
        jCheckBoxAutoJudge.setText("Auto Judge");
        jCheckBoxAutoJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAutoJudgeActionPerformed(evt);
            }
        });

        jButtonVerdict.setText("Verdict");
        jButtonVerdict.setToolTipText("Show verdict of a selected problem");
        jButtonVerdict.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonVerdictActionPerformed(evt);
            }
        });

        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jButtonOutput.setText("Output");
        jButtonOutput.setToolTipText("Show output of source code");
        jButtonOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOutputActionPerformed(evt);
            }
        });

        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonSaveAs.setText("Save As...");
        jButtonSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveAsActionPerformed(evt);
            }
        });

        jCheckBoxEditable.setText("Editable");
        jCheckBoxEditable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxEditableActionPerformed(evt);
            }
        });

        jLabelCopyright.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabelCopyright.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelCopyright.setText("Copyright © 2012");

        jLabelSelectProblem.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabelSelectProblem.setText("Select Problem :");

        jTextPaneEditor.setBackground(new java.awt.Color(0, 0, 0));
        jTextPaneEditor.setFont(new java.awt.Font("Vrinda", 0, 14));
        jTextPaneEditor.setForeground(new java.awt.Color(255, 255, 255));
        jTextPaneEditor.setCaretColor(new java.awt.Color(204, 0, 0));
        jTextPaneEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextPaneEditorKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTextPaneEditor);

        jButtonSwitch.setText("Switch");
        jButtonSwitch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSwitchActionPerformed(evt);
            }
        });

        jSeparator12.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jLabelRemainingTime.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabelRemainingTime.setToolTipText("Remaining Time");
        jLabelRemainingTime.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jLabelRemainingTimePropertyChange(evt);
            }
        });

        jButtonResult.setText("Result");
        jButtonResult.setToolTipText("Show submission history");
        jButtonResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonResultActionPerformed(evt);
            }
        });

        jSeparator13.setOrientation(javax.swing.SwingConstants.VERTICAL);

        jMenuFile.setText("File");

        jMenuItemAdd.setText("Add");
        jMenuItemAdd.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemAddMousePressed(evt);
            }
        });
        jMenuFile.add(jMenuItemAdd);
        jMenuFile.add(jSeparator8);

        jMenuItemSave.setText("Save");
        jMenuItemSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemSaveMousePressed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setText("Save As...");
        jMenuItemSaveAs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemSaveAsMousePressed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator10);

        jMenuItemExit.setText("Exit");
        jMenuItemExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemExitMousePressed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBarMainPage.add(jMenuFile);

        jMenuSettings.setText("Settings");

        jMenuItemFontSize.setText("Font Size");
        jMenuItemFontSize.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemFontSizeMousePressed(evt);
            }
        });
        jMenuSettings.add(jMenuItemFontSize);

        jMenuBackground.setText("Background");

        buttonGroup3.add(jRadioButtonMenuItemWhite);
        jRadioButtonMenuItemWhite.setText("White");
        jRadioButtonMenuItemWhite.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItemWhiteItemStateChanged(evt);
            }
        });
        jMenuBackground.add(jRadioButtonMenuItemWhite);

        buttonGroup3.add(jRadioButtonMenuItemBlack);
        jRadioButtonMenuItemBlack.setSelected(true);
        jRadioButtonMenuItemBlack.setText("Black");
        jRadioButtonMenuItemBlack.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonMenuItemBlackItemStateChanged(evt);
            }
        });
        jMenuBackground.add(jRadioButtonMenuItemBlack);

        jMenuSettings.add(jMenuBackground);
        jMenuSettings.add(jSeparator9);

        jCheckBoxMenuItemEditable.setSelected(true);
        jCheckBoxMenuItemEditable.setText("Editable");
        jCheckBoxMenuItemEditable.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBoxMenuItemEditableItemStateChanged(evt);
            }
        });
        jMenuSettings.add(jCheckBoxMenuItemEditable);

        jMenuBarMainPage.add(jMenuSettings);

        jMenuArrangecontest.setText("Arrange_Contest");
        jMenuArrangecontest.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenuArrangecontestMenuSelected(evt);
            }
        });

        jMenuItemAddNewProblem.setText("Add New Problem");
        jMenuItemAddNewProblem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemAddNewProblemMousePressed(evt);
            }
        });
        jMenuArrangecontest.add(jMenuItemAddNewProblem);

        jMenuItemUpdateProblem.setText("Update Problem");
        jMenuItemUpdateProblem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemUpdateProblemMousePressed(evt);
            }
        });
        jMenuArrangecontest.add(jMenuItemUpdateProblem);

        jMenuItemRemoveProblem.setText("Remove Problem");
        jMenuItemRemoveProblem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemRemoveProblemMousePressed(evt);
            }
        });
        jMenuArrangecontest.add(jMenuItemRemoveProblem);
        jMenuArrangecontest.add(jSeparator11);

        jMenuItemActiveContest.setText("Active Contest");
        jMenuItemActiveContest.setToolTipText("Activate contest environment on a pen drive");
        jMenuItemActiveContest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemActiveContestMousePressed(evt);
            }
        });
        jMenuArrangecontest.add(jMenuItemActiveContest);
        jMenuArrangecontest.add(jSeparator14);

        jMenuItemRankList.setText("Ranklist");
        jMenuItemRankList.setToolTipText("Show current ranklist");
        jMenuItemRankList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemRankListMousePressed(evt);
            }
        });
        jMenuArrangecontest.add(jMenuItemRankList);

        jMenuBarMainPage.add(jMenuArrangecontest);

        jMenuHelp.setText("Help");

        jMenuItemUserManual.setText("Instruction");
        jMenuItemUserManual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemUserManualMousePressed(evt);
            }
        });
        jMenuHelp.add(jMenuItemUserManual);

        jMenuItemCredits.setText("Credits");
        jMenuItemCredits.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItemCreditsMousePressed(evt);
            }
        });
        jMenuHelp.add(jMenuItemCredits);

        jMenuBarMainPage.add(jMenuHelp);

        setJMenuBar(jMenuBarMainPage);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabelCompiler, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jRadioButtonJava)
                    .addComponent(jRadioButtonCpp))
                .addGap(43, 43, 43)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jButtonAddFile, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jButtonResult, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jButtonSwitch, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(jSeparator13, javax.swing.GroupLayout.DEFAULT_SIZE, 10, Short.MAX_VALUE)
                .addGap(32, 32, 32)
                .addComponent(jLabelRemainingTime)
                .addGap(61, 61, 61))
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE)
            .addComponent(jSeparator4, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBoxManualJudge)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonCompiler, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRun, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabelSelectProblem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxSelectProblem, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jCheckBoxAutoJudge))
                .addGap(18, 18, 18)
                .addComponent(jButtonVerdict)
                .addGap(38, 38, 38)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButtonOutput)
                .addGap(59, 59, 59))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxEditable)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 649, Short.MAX_VALUE)
                .addComponent(jButtonSaveAs)
                .addGap(26, 26, 26)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 921, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(830, Short.MAX_VALUE)
                .addComponent(jLabelCopyright)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 901, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jButtonAddFile))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelCompiler)
                            .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButtonCpp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButtonJava))
                            .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelRemainingTime)
                            .addComponent(jSeparator12, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSeparator13, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jButtonResult))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jButtonSwitch)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBoxManualJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                            .addComponent(jButtonCompiler)
                            .addComponent(jButtonRun)
                            .addComponent(jButtonCheck)))
                    .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBoxAutoJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jComboBoxSelectProblem, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabelSelectProblem)
                                    .addComponent(jButtonVerdict))
                                .addGap(3, 3, 3))
                            .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonOutput)
                            .addGap(13, 13, 13))))
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 227, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSaveAs)
                    .addComponent(jButtonSave)
                    .addComponent(jCheckBoxEditable))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelCopyright))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

/*ADD*/
private void jButtonAddFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddFileActionPerformed
    try{
        addFile();
    }catch(Exception exception){
        System.out.println("MainPage => add() -> "+exception);
        jTextPaneEditor.setText("File not added :(");
    }
}//GEN-LAST:event_jButtonAddFileActionPerformed

/*COMPILE*/
private void jButtonCompilerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompilerActionPerformed
    compileFile();
}//GEN-LAST:event_jButtonCompilerActionPerformed

/*RUN*/
private void jButtonRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRunActionPerformed
    FileDirectory fd = new FileDirectory();
    String tmpFile = fd.selectFile("Select", "Input File");
    runFile(tmpFile);
}//GEN-LAST:event_jButtonRunActionPerformed

/*CHECK*/
private void jButtonCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCheckActionPerformed
    FileDirectory fd = new FileDirectory();
    judgeOutputPath = fd.selectFile("Select","Judge Output File");
    
    SystemInformation.verdict = null;
    checkFile();
    showVerdict(0, 0, 0);
}//GEN-LAST:event_jButtonCheckActionPerformed

/*c++ selected*/
private void jRadioButtonCppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonCppActionPerformed
    extension = "cpp";
}//GEN-LAST:event_jRadioButtonCppActionPerformed

/*java selected*/
private void jRadioButtonJavaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonJavaActionPerformed
    extension = "java";
}//GEN-LAST:event_jRadioButtonJavaActionPerformed

/*manual judge*/
private void jCheckBoxManualJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxManualJudgeActionPerformed
    //enable for manual check
    jButtonCompiler.setEnabled(true);
    jButtonRun.setEnabled(true);
    jButtonCheck.setEnabled(true);
    
    //disable for auto judge
    jButtonVerdict.setEnabled(false);
    jComboBoxSelectProblem.setEnabled(false);
    jLabelSelectProblem.setEnabled(false);
}//GEN-LAST:event_jCheckBoxManualJudgeActionPerformed

/*auto judge*/
private void jCheckBoxAutoJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAutoJudgeActionPerformed
    //initialize problem list
    new ProblemSet().printProblemList(jComboBoxSelectProblem);
    
    //disable for manual check
    jButtonCompiler.setEnabled(false);
    jButtonRun.setEnabled(false);
    jButtonCheck.setEnabled(false);
    
    //enable for auto judge
    jButtonVerdict.setEnabled(true);
    jComboBoxSelectProblem.setEnabled(true);
    jLabelSelectProblem.setEnabled(true);
}//GEN-LAST:event_jCheckBoxAutoJudgeActionPerformed

/*editable*/
private void jCheckBoxEditableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxEditableActionPerformed
    //System.out.println(jCheckBox3.getSelectedObjects());
    editable(jCheckBoxEditable.getSelectedObjects()!=null);
}//GEN-LAST:event_jCheckBoxEditableActionPerformed

/*save*/
private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
    if( saveFile()==true )
        JOptionPane.showMessageDialog(rootPane, "Saved");
}//GEN-LAST:event_jButtonSaveActionPerformed

/*save as*/
private void jButtonSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveAsActionPerformed
    saveAs();
}//GEN-LAST:event_jButtonSaveAsActionPerformed

/*output*/
private void jButtonOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOutputActionPerformed
    new Output(mainPath+"\\src\\out.txt", judgeOutputPath).setVisible(true);
}//GEN-LAST:event_jButtonOutputActionPerformed

/*add file*/
private void jMenuItemAddMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemAddMousePressed
    addFile();
}//GEN-LAST:event_jMenuItemAddMousePressed

/*save as*/
private void jMenuItemSaveAsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsMousePressed
    saveAs();
}//GEN-LAST:event_jMenuItemSaveAsMousePressed

/*font size*/
private void jMenuItemFontSizeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemFontSizeMousePressed
    String input = JOptionPane.showInputDialog(rootPane, "Font Size : ", jTextPaneEditor.getFont().getSize());
    System.out.println("Font Size = "+input);
    if( input!=null ){
        int size = Integer.parseInt(input);
        Font f = new Font(jTextPaneEditor.getFont().getName(), Font.PLAIN, size);
        jTextPaneEditor.setFont(f);
        jTextPaneEditor.revalidate();
    }
}//GEN-LAST:event_jMenuItemFontSizeMousePressed

/*editable*/
private void jCheckBoxMenuItemEditableItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemEditableItemStateChanged
    editable(jCheckBoxMenuItemEditable.getSelectedObjects()!=null);
}//GEN-LAST:event_jCheckBoxMenuItemEditableItemStateChanged

/*add new problem*/
private void jMenuItemAddNewProblemMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemAddNewProblemMousePressed
    new AddNewProblemPage(this, true).setVisible(true);
    new ProblemSet().printProblemList(jComboBoxSelectProblem);
}//GEN-LAST:event_jMenuItemAddNewProblemMousePressed

/*verdict*/
private void jButtonVerdictActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonVerdictActionPerformed
    //if none problem selected
    if( jComboBoxSelectProblem.getSelectedIndex()==0 )
        return ;
    
    //confirmation message if contest running
    if( jLabelRemainingTime.getText().isEmpty()==false ){
        char problemId = new ProblemSet().getSelectedProblemId(jComboBoxSelectProblem);
        int result = JOptionPane.showConfirmDialog(rootPane, "Do you want to submit problem "+problemId+" ?", 
                    "Submission", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if( result!=0 )
            return ;
    }
    
    //if any problem selected
    SystemInformation systemInformation = new SystemInformation();
    if( compileFile()==true ){
        isVerdict = true;
        judgeOutputPath = systemInformation.infoFolderPath + "\\Output";
        runFile(systemInformation.infoFolderPath + "\\Input");
    }
    //if contest running then save verdict
    else if( jLabelRemainingTime.getText().isEmpty()==false ){
        saveVerdict("CE");
    }
    //System.out.println("###################################");
}//GEN-LAST:event_jButtonVerdictActionPerformed

/*white background*/
private void jRadioButtonMenuItemWhiteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemWhiteItemStateChanged
    if( evt.getStateChange()==ItemEvent.SELECTED ){
        jTextPaneEditor.setBackground(Color.white);
        jTextPaneEditor.setForeground(Color.black);
        jTextPaneEditor.setCaretColor(Color.black);
        foregroundColor = Color.black;
        textColoring();
    }
}//GEN-LAST:event_jRadioButtonMenuItemWhiteItemStateChanged

/*black background*/
private void jRadioButtonMenuItemBlackItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemBlackItemStateChanged
    if( evt.getStateChange()==ItemEvent.SELECTED ){
        jTextPaneEditor.setBackground(Color.black);
        jTextPaneEditor.setForeground(Color.white);
        jTextPaneEditor.setCaretColor(Color.red);
        foregroundColor = Color.white;
        textColoring();
    }
}//GEN-LAST:event_jRadioButtonMenuItemBlackItemStateChanged

/*update problem*/
private void jMenuItemUpdateProblemMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemUpdateProblemMousePressed
    new UpdateProblemPage(this, true).setVisible(true);
    new ProblemSet().printProblemList(jComboBoxSelectProblem);
}//GEN-LAST:event_jMenuItemUpdateProblemMousePressed

/*deleting problem*/
private void jMenuItemRemoveProblemMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemRemoveProblemMousePressed
    new RemoveProblemPage(this, true).setVisible(true);
    new ProblemSet().printProblemList(jComboBoxSelectProblem);
}//GEN-LAST:event_jMenuItemRemoveProblemMousePressed

/*save*/
private void jMenuItemSaveMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemSaveMousePressed
    if( saveFile()==true )
        JOptionPane.showMessageDialog(rootPane, "Saved");
}//GEN-LAST:event_jMenuItemSaveMousePressed

/*user manual*/
private void jMenuItemUserManualMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemUserManualMousePressed
    new UserInstruction(this, true).setVisible(true);
}//GEN-LAST:event_jMenuItemUserManualMousePressed

/*credits*/
private void jMenuItemCreditsMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemCreditsMousePressed
    new Credits(this, true).setVisible(true);
}//GEN-LAST:event_jMenuItemCreditsMousePressed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    closingWindow();
}//GEN-LAST:event_formWindowClosing

/*exit*/
private void jMenuItemExitMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemExitMousePressed
    closingWindow();
}//GEN-LAST:event_jMenuItemExitMousePressed

private void jTextPaneEditorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextPaneEditorKeyReleased
    int inactive[] = {16, 17, 18, 20, 27, 35, 36, 37, 38, 39, 40, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 145, 155};
    int keyCode = evt.getKeyCode();
    System.out.println(evt.getKeyCode());
    
    //if ctrl & alt pressesd then no need to color
    if( evt.isControlDown()==true && keyCode!=86 )
        return ;
    if( evt.isAltDown()==true )
        return ;
    if( evt.isAltGraphDown()==true )
        return ;
    
    //inactive for funtion & some other keys 
    for(int I=0; I<inactive.length; I++)
        if( keyCode==inactive[I] )
            return ;
    
    textColoring();
    
}//GEN-LAST:event_jTextPaneEditorKeyReleased

private void jButtonSwitchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSwitchActionPerformed
    //contest does not running
    int result = -1;
    
    //if contest running
    if( jLabelRemainingTime.getText().isEmpty()==false )
        result = JOptionPane.showConfirmDialog(rootPane, "The contest data will be lost.\nDo you want to switching to judge?", 
                "Switching", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
    //if OK
    System.out.println("MainPage => "+result);
    if( result==0 ){
        SystemInformation systemInformation = new SystemInformation();
        systemInformation.penDrive = null;
        systemInformation.removeContestPasswordFolder();
        System.out.println("MainPage => Contest abort");
    }
    
    //if cancelled by contestant & for normal time
    if( result!=2 ){
        SwitchUser switchUser = new SwitchUser();
        switchUser.setVisible(true);
        this.dispose();
    }
}//GEN-LAST:event_jButtonSwitchActionPerformed

/*creating contest folder when arrange contest selected*/
private void jMenuArrangecontestMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuArrangecontestMenuSelected
    String contestFolderPath = mainPath+"\\Contest";
    FileDirectory fileDirectory = new FileDirectory();
    fileDirectory.makeDirs(contestFolderPath);
    BatchFileExecution executeBatchFile = new BatchFileExecution();
    executeBatchFile.executeCommand("attrib +S +H +R \""+contestFolderPath+"\"");
}//GEN-LAST:event_jMenuArrangecontestMenuSelected

private void jMenuItemActiveContestMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemActiveContestMousePressed
    new WaitForPenDrivePage(this, true).setVisible(true);
}//GEN-LAST:event_jMenuItemActiveContestMousePressed

private void jLabelRemainingTimePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jLabelRemainingTimePropertyChange
    if( jLabelRemainingTime.getText().equals("Finished")==true ){
        System.out.println("MainPage => Contest Over");
        SystemInformation systemInformation = new SystemInformation();
        systemInformation.removeContestPasswordFolder();
    }
}//GEN-LAST:event_jLabelRemainingTimePropertyChange

/*Result*/
private void jButtonResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonResultActionPerformed
    //if contest is not running
    if( jLabelRemainingTime.getText().isEmpty()==true ){
        //if user is a contestant
        if( SystemInformation.isJudge==false ){
            JOptionPane.showMessageDialog(rootPane, "Contest is not running.", "Error", JOptionPane.ERROR_MESSAGE);
            return ;
        }
        //if user is a judge then WaitForPenDrivePage => jButtonYes need to know it is clicked or not
        SystemInformation.isResult = true;
        new WaitForPenDrivePage(this, true).setVisible(true);
        SystemInformation.isResult = false;
    } 
    //if contest is running or judge inserted a pen drive
    try{
        new ResultPage().setVisible(true);
    }catch(Exception exception){
        JOptionPane.showMessageDialog(rootPane, exception, "Error", JOptionPane.ERROR_MESSAGE);
        System.out.println("MainPage => jButtonResultActionPerformed() -> "+exception);
    }
}//GEN-LAST:event_jButtonResultActionPerformed

private void jMenuItemRankListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItemRankListMousePressed
    new RankListPage().setVisible(true);
}//GEN-LAST:event_jMenuItemRankListMousePressed

    public void run(){
        ProblemSet problemSet = new ProblemSet();
        SystemInformation systemInformation = new SystemInformation();
        BatchFileExecution ebf = new BatchFileExecution();
        FileDirectory fileDirectory = new FileDirectory();
        
        //initialize infinity
        int timeLimit = (1<<31)-1;
        int memoryLimit = (1<<31)-1;
        
        //initialize
        char problemId = ' ';
        String problemFolderPath = null;
        String decryptFolderPath = null;
        
        //make command for execution in windows
        String command = null;
        if( extension.equals("cpp")==true )
            command = "";
        else
            command = "\""+systemInformation.javaCompilerPath+"\\java\"";
        
        //if verdict requested then decrypt judge input file
        if( isVerdict==true ){
            //get problem id
            problemId = problemSet.getSelectedProblemId(jComboBoxSelectProblem);
            problemFolderPath = mainPath + "\\Contest\\" + String.valueOf(problemId);
            decryptFolderPath = systemInformation.infoFolderPath;

            //get time limit
            try{
                timeLimit = Integer.valueOf(problemSet.getProblemTimeLimit(problemId));
                memoryLimit = Integer.valueOf(problemSet.getProblemMemoryLimit(problemId));
            }catch(Exception exception){
                System.out.println("MainPage => "+exception);
            }
            System.out.println("MainPage => run() -> "+timeLimit+" "+memoryLimit);
            
            //if judge's input file does not create
            if( problemSet.makeDecryptedJudgeFile(problemFolderPath+"\\Input", decryptFolderPath+"\\Input")==false ){
                //Re-initialize. Main.exe & judge I/O files do not created
                isVerdict = false;
                return ;
            }
        }
        
        /*RUN source code with input file*/
        RunFile runFile = new RunFile(mainPath, fileName, inFile, command);
        TimeAndMemoryExecution timeAndMemoryExecution = new TimeAndMemoryExecution();
        timeAndMemoryExecution.setVisible(true);
        MemoryUsage memoryUsage = new MemoryUsage();
        int counter = 0;
        
        /*TIME*/
        while( runFile.completed==false && timeAndMemoryExecution.isTle==false ){
            //System.out.println(runFile.completed+" "+timeAndMemoryExecution.isTle+" -> "+counter);
            timeAndMemoryExecution.setSecond(counter);
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                System.out.println("MainPage => "+e);
            }
            //for XP. dwwin.exe is created for run time error of Main.exe. to kill this
            if( ebf.executeCommand("TASKKILL /F /IM dwwin.exe")==true ){
                ebf.executeCommand("TASKKILL /F /IM dwwin.exe");
                System.out.println("MainPage => dwwin.exe killed");
            }
            //for WIN 7. werfault.exe is created for run time error of Main.exe. to kill this
            if( ebf.executeCommand("TASKKILL /F /IM werfault.exe")==true ){
                ebf.executeCommand("TASKKILL /F /IM werfault.exe");
                System.out.println("MainPage => werfault.exe killed");
            }
            counter++;
           
            //if tle occured
            if( counter>=timeLimit ){
                if( SystemInformation.verdict==null )
                    SystemInformation.verdict = "TLE";
                timeAndMemoryExecution.isTle = true;
            }
        }
        //stop RunFile thread
        runFile.t.stop();
        
        //deleteing judge input file after run source code with it
        fileDirectory.deleteDir(new File(decryptFolderPath+"\\Input"));
        
        /*MEMORY*/
        int memoryUsed = memoryUsage.maximumMemoryUsage();
        //if memory can not determine because of System Error
        //if( memoryUsed==0 )
          //  SystemInformation.verdict = "SE";
        
        //if mle occured
        if( SystemInformation.verdict==null && memoryUsed>memoryLimit )
            SystemInformation.verdict = "MLE";   
        
        //show used memory
        timeAndMemoryExecution.setMaximumMemoryUsage(memoryUsed);
        
        //kill process Main.exe
        if( ebf.executeCommand("TASKKILL /F /IM Main.exe")==true )
            System.out.println("MainPage => Main.exe killed");
        
        //if all is ok for AC or WA
        if( SystemInformation.verdict==null && isVerdict==true && timeAndMemoryExecution.isTle==false ){
            if( problemSet.makeDecryptedJudgeFile(problemFolderPath+"\\Output", decryptFolderPath+"\\Output")==true )
                checkFile();
            //deleteing judge output file
            fileDirectory.deleteDir(new File(decryptFolderPath+"\\Output"));
        }
        
        /*SHOW VERDICT*/
        if( showVerdict(timeLimit, memoryLimit, memoryUsed)==true )
            timeAndMemoryExecution.dispose();
        
        //SAVE verdict if cotest is running & auto judge is required & system error does not occured
        if( jLabelRemainingTime.getText().isEmpty()==false && isVerdict==true 
                && SystemInformation.verdict!=null && SystemInformation.verdict.equals("SE")==false )
            saveVerdict(SystemInformation.verdict);
        
        //Re-initialize
        isVerdict = false;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainPage().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jButtonAddFile;
    private javax.swing.JButton jButtonCheck;
    private javax.swing.JButton jButtonCompiler;
    private javax.swing.JButton jButtonOutput;
    private javax.swing.JButton jButtonResult;
    private javax.swing.JButton jButtonRun;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSaveAs;
    private javax.swing.JButton jButtonSwitch;
    private javax.swing.JButton jButtonVerdict;
    private javax.swing.JCheckBox jCheckBoxAutoJudge;
    private javax.swing.JCheckBox jCheckBoxEditable;
    private javax.swing.JCheckBox jCheckBoxManualJudge;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemEditable;
    public javax.swing.JComboBox jComboBoxSelectProblem;
    private javax.swing.JLabel jLabelCompiler;
    private javax.swing.JLabel jLabelCopyright;
    private javax.swing.JLabel jLabelRemainingTime;
    private javax.swing.JLabel jLabelSelectProblem;
    private javax.swing.JMenu jMenuArrangecontest;
    private javax.swing.JMenu jMenuBackground;
    private javax.swing.JMenuBar jMenuBarMainPage;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenuItem jMenuItemActiveContest;
    private javax.swing.JMenuItem jMenuItemAdd;
    private javax.swing.JMenuItem jMenuItemAddNewProblem;
    private javax.swing.JMenuItem jMenuItemCredits;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemFontSize;
    private javax.swing.JMenuItem jMenuItemRankList;
    private javax.swing.JMenuItem jMenuItemRemoveProblem;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemUpdateProblem;
    private javax.swing.JMenuItem jMenuItemUserManual;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JRadioButton jRadioButtonCpp;
    private javax.swing.JRadioButton jRadioButtonJava;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemBlack;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemWhite;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JTextPane jTextPaneEditor;
    // End of variables declaration//GEN-END:variables
}
