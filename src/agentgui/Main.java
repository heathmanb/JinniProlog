package agentgui;

import static agentgui.VFrame.startGUI;

/**
 *
 * @author Brad
 */
public class Main {

    /**
     *
     */
    public static VFrame vframe;
  
    /**
     *
     * @param title
     * @return
     */
    synchronized public static InnerFrame new_inner_frame(String title) {
	InnerFrame iframe=new InnerFrame(title);
	vframe.addInnerFrame(iframe);
	return iframe;
  }
  
    /**
     *
     */
    public static void startgui() {
	startGUI(new VFrame());
  }
  
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
	  startgui();
  }
}


