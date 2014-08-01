package agentgui;

import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import jgui.Start;

import java.awt.event.*;
import java.awt.*;
import static java.awt.Toolkit.getDefaultToolkit;
import static java.lang.System.exit;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.KeyStroke.getKeyStroke;
import static javax.swing.KeyStroke.getKeyStroke;
import static jgui.Start.invokeLater;

/*
 * InternalFrameDemo.java requires:
 *   MyInternalFrame.java
 */

/**
 *
 * @author Brad
 */

public class VFrame extends JFrame 
implements ActionListener {

    /**
     *
     */
    protected JDesktopPane desktop;

    /**
     *
     */
    public VFrame() {
      this("VDesktop",80);
    }
    
    /**
     * Makes default virtual desktop on given percentage of screen.
     * @param name
     * @param percentOfScreen
     */
    public VFrame(String name,int percentOfScreen) {
        super(name);
        int p=percentOfScreen;
        if(p<10) {
            p=10;
        } else if(p>100) {
            p=100;
        }

        Dimension screenSize = getDefaultToolkit().getScreenSize();
        int w=screenSize.width;
        int h=screenSize.height;
        int dx=(w*p/100);
        int x=(w-dx)/2;
        int dy=(h*p/100);
        int y=(h-dy)/2;
        //System.out.println("x="+x+",y="+y+",dx="+dx+",dy="+dy);
        setBounds(x, y,dx,dy);
      
        desktop = new JDesktopPane();
        
        createControlButtons(); 
        
        setContentPane(desktop);
        
        JMenuBar mbar=createMenuBar();
        if(null!=mbar) {
            setJMenuBar(mbar);
        }

        //Make dragging a little faster but perhaps uglier.
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    }

   /*
   public void createControlButtons1() {
       InnerFrame frame = ControlButtons.toInnerFrame(this);
       desktop.add(frame);
       try {
           frame.setSelected(true);
       } 
       catch (java.beans.PropertyVetoException e) {}
   }
   */

    /**
     *
     */
    
    
   public void createControlButtons() {
   }
   
    /**
     *
     * @return
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Set up the lone menu.
        JMenu menu = new JMenu("Actions");
        menu.setMnemonic(KeyEvent.VK_M);
        menuBar.add(menu);


        JMenuItem menuItem = new JMenuItem("PrologConsole");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.setAccelerator(getKeyStroke(
                KeyEvent.VK_P, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("PrologConsole");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("PrologIDE");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.setAccelerator(getKeyStroke(
                KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("PrologIDE");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("DisplayAgent");
        menuItem.setMnemonic(KeyEvent.VK_D);
        menuItem.setAccelerator(getKeyStroke(
                KeyEvent.VK_D, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("DisplayAgent");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("NewDesktop");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.setAccelerator(getKeyStroke(
                KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("NewDesktop");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Quit");
        menuItem.setMnemonic(KeyEvent.VK_Q);
        menuItem.setAccelerator(getKeyStroke(
                KeyEvent.VK_Q, ActionEvent.ALT_MASK));
        menuItem.setActionCommand("Quit");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        return menuBar;
    }

    //React to menu selections.
    public void actionPerformed(ActionEvent e) {
    	String cmd=e.getActionCommand();
    	if (null != cmd) switch (cmd) {
            case "PrologConsole":{
                Runnable R=new PrologIDE(this,"PrologConsole","new_console");
                    invokeLater(R);
                    break;
                }
            case "PrologIDE":{
                Runnable R=new PrologIDE(this,"PrologIDE","new_ide");
                    invokeLater(R);
                    break;
                }
            case "DisplayAgent":{
                Runnable R=new AgentDisplay(this);
                    invokeLater(R);
                    break;
                }
            case "NewDesktop":
                startGUI(new VFrame());
                break;
            case "Quit":
                quit();
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param frame
     */
    synchronized public void addInnerFrame(final InnerFrame frame) {
      //prolog.kernel.Machine.sleep_ms(50);
        invokeLater(() -> {
            addInnerFrame0(frame);
        });
    }
    
    private void addInnerFrame0(InnerFrame frame) {
        desktop.add(frame);
        try {
            frame.setSelected(true);
        } 
        catch (java.beans.PropertyVetoException e) {}
    }
    
    
    //Quit the application.

    /**
     *
     */
        protected void quit() {
        exit(0);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowVFrame(VFrame frame) {
        //Make sure we have nice window decorations.
        setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        //VFrame frame = new VFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Display the window.
        frame.setVisible(true);
        
        Main.vframe=frame;
    }

    /**
     *
     * @param vframe
     */
    synchronized public static void startGUI(final VFrame vframe) {
        invokeLater(() -> {
            createAndShowVFrame(vframe);
        });
    }
}



