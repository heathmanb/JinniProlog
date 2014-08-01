package prolog.core;

import com.sun.glass.events.WindowEvent;
import java.applet.*;
import java.awt.*;
import java.util.Arrays;
import static java.util.Arrays.stream;
import static prolog.core.GuiBuiltins.stopComponent;
import prolog.kernel.*;
import static prolog.kernel.JavaIO.pathOf;
import static prolog.kernel.Top.initProlog;
import prolog.logic.*;
import static prolog.logic.Interact.errmes;
import static prolog.logic.Interact.warnmes;
import static prolog.logic.Prolog.dump;

/**
 * Provides an Applet wrapper for the Prolog GUI
 */
public class PrologApplet extends Applet implements Stateful {

    /**
     *
     */
    public static Applet applet;

    /**
     *
     * @return
     */
    public static String getAppletHome() {
        String appletURL = applet.getCodeBase().toString();
        return pathOf(appletURL);
    }

    /**
     * Used to initialize applet
     */
    @Override
    public void init() {
        JavaIO.isApplet = true;
        PrologApplet.applet = this;
        Interact.USER_PATH = new ObjectQueue(Interact.applet_user_path);
        //Prolog.dump("changed to applet path: "+Main.USER_PATH);
        String root = getParameter("root");
        String command = getParameter("command");
        if (null == root || root.length() == 0) {
            root = Top.ZIPSTORE;
        } else {
            Top.ZIPSTORE = root; // usually "prolog.jar", defaults to prolog.zip otherwise
        }
        if (null == command || command.length() == 0) {
            command = "applet_console";
        }

        String[] argv = new String[2];
        argv[0] = root;
        argv[1] = command;
        //Machine M=
        try {
            initProlog(argv);
        } catch (Throwable e) {
            errmes("ireecoverable Prolog error", e);
            destroy();
        }
    }

    @Override
    public void start() {
        dump("starting...");
    }

    @Override
    public void stop() {
        dump("stopping...");
    }

    @Override
    public void destroy() {
        dump("destroying...");
        super.destroy();
    }

    @Override
    public boolean action(Event evt, Object arg) {
        if (evt.target instanceof Runnable) {
            ((Runnable) evt.target).run();
        } else {
            warnmes("UNEXPECTED  TARGET: " + evt.target);
            return false;
        }
        return true;
    }

    @Override
    public void processEvent(AWTEvent event) {
        if ((event.getID() & WindowEvent.CLOSE) > 0) {
            stream(getComponents()).forEach(
                    C -> stopComponent(C));
            removeAll();
            destroy();
        }
        super.processEvent(event);
    }
}
