package prolog.core;

import prolog.kernel.*;
import prolog.logic.*;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.URL;
import java.io.FilenameFilter;
import java.io.File;
import java.util.Arrays;

/**
 * Provides built-ins for GUI programs. Called though Reflection from Prolog.
 */
public class GuiBuiltins implements Stateful {

    public static int defX = 240;
    public static int defY = 300;
    public static int defRows = 16;
    public static int defCols = 24;
    public static int gapX = 2;
    public static int gapY = 2;

    public static String defaultFontName = "Default";
    public static int defaultFontSize = 12;
    public static int defaultFontStyle = Font.PLAIN;
    public static Color defaultFgColor = null;
    public static Color defaultBgColor = null;

    private static Font defaultFont = defaultFont();

    private static Font defaultFont() {
        return new Font(defaultFontName, defaultFontSize, defaultFontSize);
    }

    public static void setColors(Component C) {
        to_default_fg(C);
        to_default_bg(C);
    }

    public static void setFonts(Component C) {
        to_default_font(C);
    }

    public static void setLooks(Component C) {
        setFonts(C);
        setColors(C);
    }

    public GuiBuiltins() {
    }

    public static void stopComponent(Component C) {
        if (C instanceof JinniButton) {
            JinniButton B = (JinniButton) C;
            B.stop();
        }
    }

    /*
     public static Font getDefaultFont() {
     //Prolog.dump("df="+defaultFont);
     return defaultFont;
     }
     */
    public static void set_font_name(String name) {
        if (!name.equals(defaultFontName)) {
            defaultFontName = name;
            defaultFont = new Font(name, defaultFontStyle, defaultFontSize);
        }
    }

    public static void set_font_size(int size) {
        defaultFontSize = size;
        defaultFont = new Font(defaultFontName, defaultFontStyle, size);
    }

    public static void inc_font_size(int size) {
        size += defaultFontSize;
        set_font_size(size);
    }

    public static void set_font_style(String s) {
        int style = defaultFontStyle;
        if (null != s) {
            switch (s) {
                case "plain":
                    style = Font.PLAIN;
                    break;
                case "bold":
                    style = Font.BOLD;
                    break;
                case "italic":
                    style = Font.ITALIC;
                    break;
            }
        }
        if (defaultFontStyle != style) {
            defaultFontStyle = style;
            defaultFont = new Font(defaultFontName, style, defaultFontSize);
        }
    }

    public static void to_default_font(Component C) {
        C.setFont(defaultFont);
    }

    public static void to_default_fg(Component C) {
        if (null == defaultFgColor) {
            return;
        }
        C.setForeground(defaultFgColor);
    }

    public static void to_default_bg(Component C) {
        if (null == defaultBgColor) {
            return;
        }
        C.setBackground(defaultBgColor);
    }

    /*
     public static Color get_fg_color() {
     return defaultFgColor;
     }

     public static Color get_bg_color() {
     return defaultBgColor;
     }
     */
    public static void set_fg_color(double r, double g, double b) {
        defaultFgColor = new_color(r, g, b);
    }

    public static void set_bg_color(double r, double g, double b) {
        defaultBgColor = new_color(r, g, b);
    }

    public static LayoutManager to_layout(String name, int x, int y) {
        LayoutManager M;

        switch (name) {
            case "grid":
                M = new GridLayout(x, y, gapX, gapY);
                break;
            case "border":
                M = new BorderLayout();
                break;
            case "card":
                M = new CardLayout();
                break;
            case "flow":
                M = new FlowLayout();
                break;
            default:
                JavaIO.warnmes("unknown layout: " + name);
                M = new FlowLayout();
                break;
        }
        return M;
    }

    public static JinniFrame new_frame(String title, String layout,
            int x, int y, int kind) {
        LayoutManager L = GuiBuiltins.to_layout(layout, x, y); // more work to decode grid - etc.
        return new JinniFrame(title, L, kind);
    }

    /**
     * new_button(JinniContainer,Name,Action,Button): creates a Button with
     * label Name and attaches to it an action Action
     *
     * @param C
     * @param name
     * @param M
     * @return
     */
    public static JinniButton new_button(Container C, String name, Machine M) {
        JinniButton JB = new JinniButton(name, M);
        C.add(JB);
        return JB;
    }

    /**
     * new_label(JinniContainer,TextToBeDisplayed,Label): creates a label with
     * centered text
     *
     * @param C
     * @param name
     * @return
     */
    public static Label new_label(Container C, String name) {
        Label L = new Label(name);
        L.setAlignment(Label.CENTER);
        C.add(L);
        return L;
    }

    /*
     set_label: directly through Reflection
     */
    public static String new_file_dialog(int mode) {
        return new_file_dialog(mode, "pl");
    }

    public static String new_file_dialog(int mode, String filter) {
        JinniFrame C = new JinniFrame("File Dialog");

        FileDialog D;
        if (0 == mode) {
            D = new JinniFileDialog(C, "Load", FileDialog.LOAD, filter);
        } else {
            D = new JinniFileDialog(C, "Save", FileDialog.SAVE, filter);
        }
        GuiBuiltins.setLooks(D);
        //D.show();
        D.setVisible(true);
        String fname = D.getFile();
        if (null == fname) {
            return null;
        }
        String dname = D.getDirectory();
        if (null == dname) {
            return null;
        }
        String result = dname + fname;
        D.dispose();
        C.dispose();
        return result;
    }

    public static JinniPanel new_panel(Container C, String layout, int x, int y) {
        LayoutManager L = GuiBuiltins.to_layout(layout, x, y);
        JinniPanel P = new JinniPanel(L);
        C.add(P);
        return P;
    }

    /**
     * new_text ARGS: 1=Parent Container 2=initial text content 3=rows 4=cols
     * 5=returned handles
     *
     * @param C
     * @param oldText
     * @param rows
     * @param cols
     * @return
     */
    public static JinniText new_text(Container C, String oldText, int rows, int cols) {
        JinniText T = new JinniText(oldText);
        if (rows > 0 && cols > 0) {
            T.setRows(rows);
            T.setColumns(cols);
        }
        C.add(T);
        return T;
    }

    /* : in Prolog + Reflection
     get_text(JinniText,Answer):  collects
     the cpntent of thext area to new constant Answer
     */
    /*
     in Prolog:
     add_text
     set_text
     get_text
     clear_text
     */
    public static Color new_color(double r, double g, double b) {
        if (r > 1 || r < 0) {
            JavaIO.warnmes("new_color arg 1 should be in 0..1->" + r);
        }
        if (g > 1 || g < 0) {
            JavaIO.warnmes("new_color arg 2 should be in 0..1->" + g);
        }
        if (b > 1 || b < 0) {
            JavaIO.warnmes("new_color arg 3 should be in 0..1->" + b);
        }
        int R = (int) (r * 255.0);
        int G = (int) (g * 255.0);
        int B = (int) (b * 255.0);
        Color C = new Color(R, G, B);
        return C;
    }

    //  set_fg,set_bg,set_color : in Prolog 
    public static void set_direction(Container C, String direction) {
        if (C instanceof JinniFrame) {
            ((JinniFrame) C).setDirection(direction);
        } else {
            ((JinniPanel) C).setDirection(direction);
        }
    }

    public static void destroy(Component C) {
        //C.dispose();
        if (C instanceof Container) {
            ((Container) C).removeAll();
        }
        C.removeNotify();
    }

    public static void set_layout(Container C, String layoutName, int x, int y) {
        //C.removeAll();
        LayoutManager L = to_layout(layoutName, x, y);
        C.setLayout(L);
    }

    public static void show(Container C) {
        C.validate();
        C.setVisible(true);
    }

    public static void resize(Component C, int h, int v) {
        C.setSize(h, v);
    }

    public static void move(Component C, int hpos, int vpos) {
        C.setLocation(hpos, vpos);
    }

    /**
     * detects if applet and gets applet container
     *
     * @return
     */
    public static Applet get_applet() {
        return (Applet) PrologApplet.applet;
    }

    public static String get_applet_host() {
        return get_applet().getCodeBase().getHost();
    }

    public static JinniImagePanel new_image(Container C, String src,
            int width, int height) {
        JinniImagePanel P = new JinniImagePanel(src, width, height);
        C.add(P);
        return P;
    }

    public static class JinniFrame extends Frame {

        private int kind;
        private String direction;

        JinniFrame(String title, LayoutManager L, int kind) {
            super(title);
            setLayout(L); // hgap=10,vgap=10
            this.kind = kind;
            this.direction = null;
            setLooks();
            setSize(GuiBuiltins.defX, GuiBuiltins.defY); // reasonable initial default size 
            this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        }

        JinniFrame(String title) {
            super(title);
            this.kind = 1;
            this.direction = null;
            setLooks();
        }

        private void setLooks() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        @Override
        public boolean action(Event evt, Object arg) {
            return handleRunnable(evt);
        }

        public boolean handleRunnable(Event evt) {
            //$$ Interact.warnmes("handleRunnable TARGET: "+evt.target);
            if (evt.target instanceof Runnable) {
                ((Runnable) evt.target).run();
            } else {
                Interact.warnmes("UNEXPECTED  TARGET: " + evt.target);
                return false;
            }
            return true;
        }

        @Override
        public void processEvent(AWTEvent event) {
            //Interact.println("Frame event:"+event);

            if (this.kind > 0 && event.getID() == WindowEvent.WINDOW_CLOSING) {
                cleanUp();
            }
            super.processEvent(event);
        }

        private void cleanUp() {
            Arrays.stream(getComponents()).parallel()
                    .forEach(C -> {
                        if (C instanceof JinniButton) {
                            ((JinniButton) C).stop();
                        }
                    });
            dispose();
            removeAll();
        }

        @Override
        public Component add(Component C) {
            if (this.getLayout() instanceof BorderLayout) {
                //JavaIO.println("adding to: "+direction);
                return super.add(direction, C);
            } else {
                //JavaIO.println("not adding "+C+" to: "+direction+"<="+this);
                return super.add(C);
            }
        }
    }


    /*
     Examples of Prolog GUI components - add more !
     */
    /**
     * Button with attached Prolog action. Runs action when Button pushed.
     */
    public static class JinniButton extends Button implements Runnable {//$$, ActionListener {

        private Machine M;

        JinniButton(String name, Machine M) {
            super(name);
            this.M = M;
            setLooks();
        }

        private void setLooks() {
            GuiBuiltins.setLooks(this);
        }

        /**
         * Passes action to Prolog when Button is pushed
         */
        @Override
        public void run() {
            ask();
        }

        synchronized private void ask() {
            int answer = 0;
            try {
                if (null != M) {
                    answer = M.ask();
                }
            } catch (PrologException e) {
                // ok, handled in ask.
            }
            if (0 == answer) {
                JavaIO.warnmes("the engine attached to a Prolog Button died");
            }
        }

        synchronized public void stop() {
            if (null != M) {
                M.removeObject(this);
                M.stop();
                M = null;
            }
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            stop();
        }
    }

    public static class JinniPanel extends Panel {

        private String direction;

        JinniPanel(LayoutManager L) {
            super();
            setLayout(L);
            setLooks();
        }

        private void setLooks() {
            GuiBuiltins.setLooks(this);
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        @Override
        public Component add(Component C) {
            if (this.getLayout() instanceof BorderLayout) {
                //JavaIO.println("adding to: "+direction);
                return super.add(direction, C);
            } else {
                //JavaIO.println("not adding "+C+" to: "+direction+"<="+this);
                return super.add(C);
            }
        }

    }

    public static class JinniText extends TextArea implements TextSink {

        JinniText() {
            setLooks();
        }

        JinniText(String oldText) {
            super(oldText, GuiBuiltins.defRows, GuiBuiltins.defCols, SCROLLBARS_VERTICAL_ONLY);
            validate();
        }

        private void setLooks() {
            GuiBuiltins.setLooks(this);
        }

        /*
         public void appendText(String s) { // add_text in Prolog
         append_text(s);
         }
         */
        @Override
        public void append_text(String s) {
            super.append(s);
        }

        public void appendNL() {
            append_text("\n");
        }

        public void appendCode(int c) {
            append_text("" + (char) c);
        }

        @Override
        public void processKeyEvent(KeyEvent e) {
            GuiBuiltins.setColors(this);
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
        }
    }

    public static class JinniImagePanel extends Canvas {

        private Image image;
        private int width;
        private int height;

        JinniImagePanel(String sourceName, int width, int height) {
            //this.sourceName=sourceName;
            this.width = width;
            this.height = height;
            if (null != PrologApplet.applet) {
                Applet applet = (Applet) PrologApplet.applet;
                URL url = applet.getCodeBase();
                image = applet.getImage(url, sourceName);
            } else {
                image = Toolkit.getDefaultToolkit().getImage(sourceName);
            }
            setLooks();
        }

        private void setLooks() {
            GuiBuiltins.setLooks(this);
        }

        // see also (inherited) ImageObserver 
        @Override
        public void paint(Graphics g) {
            if (width <= 0 || height <= 0) {
                width = image.getWidth(this);
                height = image.getHeight(this);
            }
            setSize(width, height);
            g.drawImage(image, 0, 0, width, height, this);
        }
    }

    /**
     * File filters do not function on Windows - known Java bug
     */
    public static class JinniFileDialog extends FileDialog {

        JinniFileDialog(JinniFrame F, String name, int mode, String filter) {
            super(F, name, mode);
            setLooks();
        }

        private void setLooks() {
            GuiBuiltins.setLooks(this);
        }
    }
}
