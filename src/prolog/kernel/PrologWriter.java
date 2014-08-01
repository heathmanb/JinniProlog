package prolog.kernel;

import prolog.logic.*;
import java.io.*;
import static prolog.kernel.JavaIO.getStdOutput;
import static prolog.logic.Interact.warnmes;

class GenericWriter extends PrintWriter {

    //

    public GenericWriter(OutputStream f) {
        //super(f,true); // flush automatically on NL
        super(tryEncoding(f, Top.defaultEncoding), true); // flush automatically on NL
    }

    public GenericWriter(String fileName) throws IOException {
        this(new FileOutputStream(fileName));
    }

    public GenericWriter() {
        super(getStdOutput(), true);
    }

    private static OutputStreamWriter tryEncoding(OutputStream outputStream, String encoding) {
        try {
            return new OutputStreamWriter(outputStream, encoding);
        } catch (UnsupportedEncodingException e) { // use default if it fails
            warnmes("failing to use character encoding: " + encoding);
            return new OutputStreamWriter(outputStream);
        }
    }
}

/**
 * Basic Prolog char writer
 */
public class PrologWriter extends GenericWriter implements Stateful {

    /**
     * allows virtualizing output to go to something like a GUI element clearly
     * there's no file in this case - the extender should provide some write and
     * newLine methods that append to a window it could also be used to do
     * nothing at all - and disable any output by implementing a "do nothing"
     * PrologWriter extension
     */

    public TextSink textSink;

    /**
     *
     * @param textSink
     */
    public PrologWriter(TextSink textSink) {
        this.textSink = textSink;
    }

    /**
     *
     * @return
     */
    public TextSink getTextSink() {
        return this.textSink;
    }

    /**
     *
     * @param f
     */
    public PrologWriter(OutputStream f) {
        super(f);
    }

    /**
     *
     * @param fileName
     * @throws IOException
     */
    public PrologWriter(String fileName) throws IOException {
        this(new FileOutputStream(fileName));
    }

    /**
     *
     * @param c
     */
    public final void super_write(int c) {
        if ('\n' == c) {
            super.flush();
        }
        super.write(c);
    }

    @Override
    public void write(int c) {
        //System.err.println(JavaIO.NL+this+"write => <"+(char)c+">");
        if (!JavaIO.showOutput) {
            return;
        }
        super_write(c);
    }

    @Override
    public void flush() {
        if (!JavaIO.showOutput) {
            return;
        }
        super.flush();
    }

    @Override
    public void print(String s) {
        //System.err.println(JavaIO.NL+this+"print => <"+s+">");
        if (!JavaIO.showOutput) {
            return;
        }
        super.print(s);
        if (s.indexOf('\n') > 0) {
            super.flush();
        }
    }

    @Override
    public void println(String s) {
        print(s + JavaIO.NL);
    }

    @Override
    public void println() {
        print("" + JavaIO.NL);
    }

}
