package tv.newtv.cboxtv;

import android.text.TextUtils;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example local unit test, which will buildExecutor on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String time = "12:00:00";
        String current = dateFormat.format(now);
        System.out.println(formatToSeconds(time));
        System.out.println(formatToSeconds(current));

    }

    public static int formatToSeconds(String timeFormat) {
        if (timeFormat == null) {
            return 0;
        }
        String[] times = timeFormat.split(":");
        int result = 0;
        for (int index = 0; index < 3; index++) {
            if (times.length >= index + 1) {
                String value = times[index];
                if (!TextUtils.isEmpty(value)) {
                    switch (index) {
                        case 0:
                            result += 3600 * Integer.parseInt(value);
                            break;
                        case 1:
                            result += 60 * Integer.parseInt(value);
                            break;
                        default:
                            result += Integer.parseInt(value);
                            break;
                    }
                }
            }
        }
        return result;
    }


}