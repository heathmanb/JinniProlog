package prolog.core;

import prolog.kernel.*;
import java.io.*;
import java.util.zip.*;

/**
 * Provides zip/unzip compression for various Prolog tasks to save memory and
 * disk space.
 */
public class Zipper {

    public static InputStream zip2stream(String jarname, String fname, boolean quiet) {
        try {
            File JF = new File(jarname);
            if (!JF.exists()) {
                return null;
            }
            ZipFile jf = new ZipFile(JF);
            ZipEntry entry = jf.getEntry(fname);
            if (null == entry) {
                return null;
            }
            return jf.getInputStream(entry);
        } catch (IOException e) {
            if (!quiet) {
                JavaIO.warnmes("error opening zip or jar file component: "
                        + jarname + ":" + fname);
            }
            return null;
        }
    }
}
