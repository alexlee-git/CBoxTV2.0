package tv.newtv.cboxtv;

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
        User user = new User();
        user.age = "11";
        user.name = "admin";

        List<Filter> filters = new ArrayList<>();
        filters.add(new Filter("age", "11"));
        filters.add(new Filter("name", "mali"));

        check : for (Field field : user.getClass().getDeclaredFields()) {
            for (Filter filter : filters) {
                System.out.println("check field=" + field.getName() + " value="+field.get(user));
                System.out.println("filter field=" + filter.filterName + " value="+filter.filterAge);
                if (filter.filterName.equals(field.getName()) && filter.filterAge.equals(field.get
                        (user).toString())) {
                    System.out.println("the same with user");
                    break check;
                } else {
                    System.out.println("not same with user");
                }
            }
        }
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