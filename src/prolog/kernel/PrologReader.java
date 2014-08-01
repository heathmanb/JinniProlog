package prolog.kernel;

import prolog.logic.*;
import java.io.*;
import static java.lang.Character.isWhitespace;
import static prolog.kernel.JavaIO.getStdInput;
import static prolog.kernel.JavaIO.println;
import static prolog.kernel.JavaIO.println;
import static prolog.kernel.Top.get_verbosity;
import static prolog.logic.Defs.INPUT_INT;
import static prolog.logic.Interact.warnmes;

class GenericReader extends LineNumberReader {

    public GenericReader(InputStream inputStream) {
        //super(new InputStreamReader(inputStream));
        super(tryEncoding(inputStream, Top.defaultEncoding));
    }

    public GenericReader(Reader reader) {
        super(reader);
    }

    public GenericReader() {
        super(getStdInput());
    }

    private static InputStreamReader tryEncoding(InputStream inputStream, String encoding) {
        int min_verbosity = 5;
        try {
            if (get_verbosity() >= min_verbosity) {
                println(
                        "old default char encoding=" + new InputStreamReader(inputStream).getEncoding());
            }
            InputStreamReader reader = new InputStreamReader(inputStream, encoding);
            if (get_verbosity() >= min_verbosity) {
                println("new default char encoding=" + reader.getEncoding());
            }
            return reader;
        } catch (UnsupportedEncodingException e) { // use default if it fails
            warnmes("unable to use character encoding: " + encoding);
            return new InputStreamReader(inputStream);
        }
    }

}

/**
 * Reads chars from byte streams using the current default encoding
 */
public class PrologReader extends GenericReader implements Stateful {

    /**
     *
     */
    public static final int EOF = -1;
    private static final char NL = JavaIO.XNL;

    /**
     *
     * @param inputStream
     */
    public PrologReader(InputStream inputStream) {
        super(inputStream);
    }

    /**
     *
     * @param reader
     */
    public PrologReader(Reader reader) {
        super(reader);
    }

    @Override
    public int read() {
        try {
            return super.read();
        } catch (EOFException eof) {
            atEOF();
            return EOF;
        } catch (IOException e) {
            //JavaIO.errmes("error in read(): "+this,e);
            atEOF();
            return EOF;
        }
    }

    /**
     *
     * @return
     */
    public final String readln() {
        try {
            return readLine();
        } catch (EOFException eof) {
            return atEOF();
        } catch (IOException e) {
            //JavaIO.errmes("error in readln(): "+this,e);
            return atEOF();
        }
    }

    /**
     *
     * @param M
     * @return
     * @throws PrologException
     */
    public int get(Machine M) throws PrologException { // M not really used here
        int ires = read();
        if (EOF == ires) {
            atEOF();
            return 0;
        } else {
            return INPUT_INT(ires);
        }
    }

    /**
     *
     * @param M
     */
    public void stop(Machine M) {
        fclose();
        M.removeObject(this);
    }

    /**
     *
     */
    public void fclose() {
        //if(this!=M.stdInput())
        try {
            //JavaIO.println("trying to close"+this);
            close();
        } catch (IOException e) {
            //JavaIO.println("problem closing"+e);
        }
    }

    /**
     *
     * @return
     */
    public String atEOF() {
        fclose();
        return null;
    }

    /**
     *
     * @return
     */
    public String nextClauseString() {
        int last = ' ';
        int next = read();
        if (EOF == next) {
            return atEOF();
        }
        boolean inBlocComment = false;
        boolean inLineComment = false;
        boolean inSingleQuoted = false;
        boolean inDoubleQuoted = false;
        boolean hasContent = false;
        StringBuilder s = new StringBuilder();

        for (;;) {
            last = next;
            next = read();

            if (EOF == next) {
                atEOF();
                break;
            } else if ('.' == last
                    && !inLineComment && !inBlocComment
                    && !inSingleQuoted && !inDoubleQuoted) {
                if (isWhitespace((char) next)) {
                    break;
                } else if ('.' == next) {
                    s.append((char) last);
                    s.append((char) next);
                    last = next;
                    next = read();
                    //Prolog.dump("!!!:"+(char)last+(char)next);
                    continue;
                }

            } else if ('\'' == last
                    && !inDoubleQuoted && !inBlocComment && !inLineComment) {
                inSingleQuoted = !inSingleQuoted;
            } else if ('\"' == last && !inSingleQuoted && !inBlocComment && !inLineComment) {
                inDoubleQuoted = !inDoubleQuoted;
            } else if ('/' == last && '*' == next
                    && !inSingleQuoted && !inDoubleQuoted && !inLineComment) {
                if (inBlocComment) {
                    return readError("embedded bloc comments not allowed");
                }
                inBlocComment = true;
        //last=curr;
                //curr=read();
            } else if ('*' == last && '/' == next
                    && !inSingleQuoted && !inDoubleQuoted && !inLineComment) {
                if (!inBlocComment) {
                    return readError("bloc comment ends but never begins: ");
                }
                inBlocComment = false;
                last = next;
                next = read();
                continue;
            } // $$ possible bug
            else if ('%' == last && !inBlocComment && !inSingleQuoted && !inDoubleQuoted) {
                inLineComment = true;
            } else if (NL == last && !inBlocComment && !inSingleQuoted && !inDoubleQuoted) {
                inLineComment = false;
                s.append((char) last);
                continue;
            } else if ('\r' == last) {
                if (!inBlocComment && !inSingleQuoted && !inDoubleQuoted) {
                    inLineComment = false; //$$ fix to 1-line comments bug ???
                }
                last = ' ';
            } else {
                //
            }

            if (inBlocComment || inLineComment) {
                continue;
            }
            if (!isWhitespace((char) last)) {
                hasContent = true;
            }
            s.append((char) last);
        }
        if (0 == s.length() || !hasContent) {
            return null;
        }

        return s.toString().trim();
    }

    /**
     *
     * @param message
     * @return
     */
    public String readError(String message) {
        atEOF();
        warnmes(message);
        return null;
    }
}
