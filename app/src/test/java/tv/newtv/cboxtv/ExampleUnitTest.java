package tv.newtv.cboxtv;

import android.os.CountDownTimer;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Example local unit test, which will buildExecutor on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() throws Exception {
        CountDownTimer downTimer = new CountDownTimer(5000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println(millisUntilFinished);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public static class User {
        public String age;
        public String name;
    }

    public static class Filter {
        public String filterAge;
        public String filterName;

        public Filter(String field, String value) {
            filterName = field;
            filterAge = value;
        }
    }


}