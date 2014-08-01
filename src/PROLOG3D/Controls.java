package prolog3d;

import java.awt.*;
import java.awt.event.*;
import static prolog3d.Prolog3D.pp;

/**
   Adds a control Panel to the 3D display
 */
public class Controls extends Panel implements ActionListener {
  Controls(Prolog3D M,LayoutEngine LG) {
    super(new BorderLayout());
    this.M=M;
    this.LG=LG;
    makeControls();
  }
  
  Prolog3D M;
  LayoutEngine LG;
  TextArea output;
   
    /**
     *
     */
    public void makeControls() {
    this.output=M.output;
  }
  
  public void actionPerformed(ActionEvent e) {
        pp("unknown event="+e);
  }

    /**
     *
     * @param O
     */
    public void print(Object O) {
    this.output.append(O.toString());
  }
}
