package jgui;

import javax.swing.*;
//import javax.swing.event.*;
//import javax.swing.text.*;
//import javax.swing.filechooser.*;

import java.io.File;
import static jgui.Start.setLooks;

/**
 *
 * @author Brad
 */
public class JinniFileDialog extends JFileChooser  {
  
    /**
     *
     * @param F
     * @param name
     * @param mode
     * @param filter
     */
    public JinniFileDialog(JinniFrame F,String name,int mode,String filter) {
    //super(F,name,mode);
    super("."); //(File)null,(FileSystemView)null);
    this.mode=mode;
    setDialogTitle(name);
    setDialogType(mode);
    //this.filter=filter;
    //setFilenameFilter(this);
        setLooks(this);
    
  }
  private int mode;
  //private String filter;
  
    /**
     *
     * @return
     */
    public String getChoice() {
     int status;
     if(0==mode) {
         status=showOpenDialog(null);
        } else {
         status=showSaveDialog(null);
        }
     File selectedFile = getSelectedFile();
     if(null==selectedFile) {
         return null;
        }
     return selectedFile.getName();
  }
  
    /**
     *
     * @param dir
     * @param name
     * @return
     */
    public boolean accept(File dir, String name) {
    //Prolog.dump("accept called with: "+name);
    //return name.endsWith("."+this.filter);
    return true; // this makes behavior uniform accross platforms
  }
}

