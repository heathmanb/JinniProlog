package prolog.kernel;

import static java.lang.String.valueOf;
import prolog.logic.Interact;

/**
 *
 * @author Brad
 */
public class PWriter extends PrologWriter {

    private boolean trouble = false;

	//private Formatter formatter;

    /**
     *
     * @param textSink
     */
        public PWriter(TextSink textSink) {
        super(textSink);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean checkError() {
        return trouble;
    }

    @Override
    protected void setError() {
        trouble = true;
    }

    @Override
    protected void clearError() {
        trouble = false;
    }

    @Override
    public void write(int c) {
        write("" + (char) c);
    }

    @Override
    public void write(char buf[], int off, int len) {
        write(new String(buf, off, len));
    }

    @Override
    public void write(char buf[]) {
        write(buf, 0, buf.length);
    }

    @Override
    public void write(String s, int off, int len) {
        write(s.substring(off, off + len));
    }

    @Override
    public void write(String s) {
        textSink.append_text(s);
    }

    private void newLine() {
        write(Interact.NL);
    }

    /* Methods that do not terminate lines */
    @Override
    public void print(boolean b) {
        write(b ? "true" : "false");
    }

    @Override
    public void print(char c) {
        write(c);
    }

    @Override
    public void print(int i) {
        write(valueOf(i));
    }

    @Override
    public void print(long l) {
        write(valueOf(l));
    }

    @Override
    public void print(float f) {
        write(valueOf(f));
    }

    @Override
    public void print(double d) {
        write(valueOf(d));
    }

    @Override
    public void print(char s[]) {
        write(s);
    }

    @Override
    public void print(String s) {
        if (s == null) {
            s = "null";
        }
        write(s);
    }

    @Override
    public void print(Object obj) {
        write(valueOf(obj));
    }

    @Override
    public void println() {
        newLine();
    }

    @Override
    public void println(boolean x) {
        print(x);
        println();
    }

    @Override
    public void println(char x) {
        print(x);
        println();
    }

    @Override
    public void println(int x) {
        print(x);
        println();
    }

    @Override
    public void println(long x) {
        print(x);
        println();
    }

    @Override
    public void println(float x) {
        print(x);
        println();
    }

    @Override
    public void println(double x) {
        print(x);
        println();
    }

    @Override
    public void println(char[] x) {
        write(x);
        println();
    }

    @Override
    public void println(String x) {
        print(x);
        println();
    }

    @Override
    public void println(Object x) {
        String s = valueOf(x);
        print(s);
        println();
    }

    /*
    TODO: Dead code.
     public VWriter printf(String format, Object... args) {
     return format(format, args);
     }

     public VWriter printf(Locale l, String format, Object... args) {
     return format(l, format, args);
     }

     public VWriter format(String format, Object... args) {
     if ((formatter == null) || (formatter.locale() != Locale.getDefault()))
     formatter = new Formatter(this);
     formatter.format(Locale.getDefault(), format, args);
     return this;
     }

     public VWriter format(Locale l, String format, Object... args) {
     if ((formatter == null) || (formatter.locale() != l))
     formatter = new Formatter(this, l);
     formatter.format(l, format, args);
     return this;
     }

     public VWriter append(CharSequence csq) {
     if (csq == null)
     write("null");
     else
     write(csq.toString());
     return this;
     }

     public VWriter append(CharSequence csq, int start, int end) {
     CharSequence cs = (csq == null ? "null" : csq);
     write(cs.subSequence(start, end).toString());
     return this;
     }
	
     public PWriter append(char c) {
     write(c);
     return this;
     }
     */
}
