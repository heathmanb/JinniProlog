package prolog.core;

import prolog.logic.Fun;
import prolog.logic.Stateful;
import java.util.*;

// meant to do arithmetics on things like 2003/04/01 08:02:00

/**
 *
 * @author Brad
 */
public class PDate extends GregorianCalendar implements Stateful {

    /**
     *
     * @param Date
     * @param Inc
     * @return
     */
    public static Fun inc_date(Fun Date, Fun Inc) {

        PDate P = new PDate(Date);

    //System.out.println(Date+"=>PDate="+P);
        int Y = ((Integer) Inc.getArg(1));
        int M = ((Integer) Inc.getArg(2));
        int D = ((Integer) Inc.getArg(3));
        int H = ((Integer) Inc.getArg(4));
        int N = ((Integer) Inc.getArg(5));
        int S = ((Integer) Inc.getArg(6));

        P.add(Calendar.YEAR, Y);
        P.add(Calendar.MONTH, M);
        P.add(Calendar.DAY_OF_MONTH, D);
        P.add(Calendar.HOUR_OF_DAY, H);
        P.add(Calendar.MINUTE, N);
        P.add(Calendar.SECOND, S);

    //System.out.println("PDate.result=>"+R);
        return P.toFun();
    }

    /**
     *
     * @param date
     * @return
     */
    public static String format_date(Fun date) {
        return new PDate(date).toString();
    }

    /**
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @param hourOfDay
     * @param minute
     * @param sec
     */
    public PDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int sec) {
        super(year, month, dayOfMonth, hourOfDay, minute, sec);
    }

    /**
     *
     * @param date
     */
    public PDate(Fun date) {
        this(
                ((Integer) date.getArg(1)),
                ((Integer) date.getArg(2)),
                ((Integer) date.getArg(3)),
                ((Integer) date.getArg(4)),
                ((Integer) date.getArg(5)),
                ((Integer) date.getArg(6)));
    }

    /**
     *
     * @return
     */
    public Fun toFun() {
        int Y = get(Calendar.YEAR);
        int M = get(Calendar.MONTH);
        int D = get(Calendar.DAY_OF_MONTH);
        int H = get(Calendar.HOUR_OF_DAY);
        int N = get(Calendar.MINUTE);
        int S = get(Calendar.SECOND);

        Fun R = new Fun("date",
                new Integer(Y),
                new Integer(M),
                new Integer(D),
                new Integer(H),
                new Integer(N),
                new Integer(S)
        );

        return R;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(get(Calendar.YEAR));
        b.append("/");
        b.append(get(Calendar.MONTH));
        b.append("/");
        b.append(get(Calendar.DAY_OF_MONTH));
        b.append(" ");
        b.append(get(Calendar.HOUR_OF_DAY));
        b.append(":");
        b.append(get(Calendar.MINUTE));
        b.append(":");
        b.append(get(Calendar.SECOND));
        return b.toString();
    }

}
