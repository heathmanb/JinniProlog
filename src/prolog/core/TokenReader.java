package prolog.core;

import prolog.kernel.*;
import prolog.logic.*;
import java.io.*;
import static java.lang.Math.floor;
import static prolog.kernel.JavaIO.string2reader;
import static prolog.kernel.JavaIO.url_or_file;
import static prolog.logic.Interact.errmes;

/**
 * Reads chars from char streams using the current default encoding
 */
public final class TokenReader extends PrologReader {

    /**
     *
     * @param fname
     * @return
     * @throws ExistenceException
     */
    public static PrologReader toTokenReader(String fname) throws ExistenceException {
        return new TokenReader(url_or_file(fname));
    }

    /**
     *
     * @param cs
     * @return
     * @throws ExistenceException
     */
    public static PrologReader string2TokenReader(String cs) throws ExistenceException {
        Reader stream = string2reader(cs);
        return new TokenReader(stream);
    }

    /**
     *
     * @param stream
     */
    public TokenReader(InputStream stream) {
        super(stream);
        init();
    }

    /**
     *
     * @param reader
     */
    public TokenReader(Reader reader) {
        super(reader);
        init();
    }

    /**
     *
     */
    public void init() {
        tokenizer = new StreamTokenizer(this);
        tokenizer.parseNumbers();
        tokenizer.eolIsSignificant(true);
        tokenizer.ordinaryChar('.');
        tokenizer.ordinaryChar('/');
        tokenizer.ordinaryChar('\"');
        tokenizer.ordinaryChar('\'');
        tokenizer.ordinaryChar('-');
        tokenizer.wordChars('_', '_');
    }

    private StreamTokenizer tokenizer;

    /**
     *
     * @param M
     * @return
     * @throws PrologException
     */
    @Override
    public int get(Machine M) throws PrologException {
        if (null == tokenizer) {
            return 0;
        }

        int c = StreamTokenizer.TT_EOF;
        try {
            c = tokenizer.nextToken();
        } catch (IOException e) {
            errmes("tokenizer error", e);
        }

        int t;
        switch (c) {
            case StreamTokenizer.TT_WORD:
                t = M.prolog.atomTable.newFunctor(tokenizer.sval, 0); //better not internalize!
                break;

            case StreamTokenizer.TT_NUMBER:
                if (tokenizer.nval == floor(tokenizer.nval)) {
                    t = M.termReader.putInt((int) tokenizer.nval);
                } else {
                    t = M.termReader.putFloat(tokenizer.nval);
                }
                break;

            case StreamTokenizer.TT_EOL:
                t = M.prolog.atomTable.newFunctor("" + (char) 10, 0);
                break;

            case StreamTokenizer.TT_EOF: {
                tokenizer = null;
                t = 0;
            }
            break;

            default: {
                String s = "" + (char) c;
                t = M.prolog.atomTable.newFunctor(s, 0);
            }

        }
        //Prolog.dump("<<"+c+">>"+M.termToString(t));
        return t;
    }

    /**
     *
     * @param M
     */
    @Override
    public void stop(Machine M) {
        super.stop(M);
        tokenizer = null;
    }
}
