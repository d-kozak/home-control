package pt.ipp.estg.home.control.utils;

import java.util.Calendar;

public class Log {

    public static void message(String szMessage) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        int nDay = calendar.get(Calendar.HOUR_OF_DAY);
        int nMinute = calendar.get(Calendar.MINUTE);
        int nMillis = calendar.get(Calendar.MILLISECOND);

        String szDate = "[" + nDay + ":" + nMinute + ":" + nMillis + "]";

        System.out.println(szDate + " " + szMessage.replace("\n", ""));
    }
}
