package lz.mylife.cal;

import java.util.HashMap;
import java.util.Map;

import lz.mylife.R;

/**
 * Created by cussyou on 2016-06-17.
 */
public class YahooWeatherCode {
    private static  boolean inited = false;
    public static Map<String, Integer> weatherCodes = new HashMap<String, Integer>();
    public static void init() {
        if(inited) {
            return;
        }
        weatherCodes.put("0", R.string.code_0);
        weatherCodes.put("1", R.string.code_1);
        weatherCodes.put("2", R.string.code_2);
        weatherCodes.put("3", R.string.code_3);
        weatherCodes.put("4", R.string.code_4);
        weatherCodes.put("5", R.string.code_5);
        weatherCodes.put("6", R.string.code_6);
        weatherCodes.put("7", R.string.code_7);
        weatherCodes.put("8", R.string.code_8);
        weatherCodes.put("9", R.string.code_9);
        weatherCodes.put("10", R.string.code_10);
        weatherCodes.put("11", R.string.code_11);
        weatherCodes.put("12", R.string.code_12);
        weatherCodes.put("13", R.string.code_13);
        weatherCodes.put("14", R.string.code_14);
        weatherCodes.put("15", R.string.code_15);
        weatherCodes.put("16", R.string.code_16);
        weatherCodes.put("17", R.string.code_17);
        weatherCodes.put("18", R.string.code_18);
        weatherCodes.put("19", R.string.code_19);
        weatherCodes.put("20", R.string.code_20);
        weatherCodes.put("21", R.string.code_21);
        weatherCodes.put("22", R.string.code_22);
        weatherCodes.put("23", R.string.code_23);
        weatherCodes.put("24", R.string.code_24);
        weatherCodes.put("25", R.string.code_25);
        weatherCodes.put("26", R.string.code_26);
        weatherCodes.put("27", R.string.code_27);
        weatherCodes.put("28", R.string.code_28);
        weatherCodes.put("29", R.string.code_29);
        weatherCodes.put("30", R.string.code_30);
        weatherCodes.put("31", R.string.code_31);
        weatherCodes.put("32", R.string.code_32);
        weatherCodes.put("33", R.string.code_33);
        weatherCodes.put("34", R.string.code_34);
        weatherCodes.put("35", R.string.code_35);
        weatherCodes.put("36", R.string.code_36);
        weatherCodes.put("37", R.string.code_37);
        weatherCodes.put("38", R.string.code_38);
        weatherCodes.put("39", R.string.code_39);
        weatherCodes.put("40", R.string.code_40);
        weatherCodes.put("41", R.string.code_41);
        weatherCodes.put("42", R.string.code_42);
        weatherCodes.put("43", R.string.code_43);
        weatherCodes.put("44", R.string.code_44);
        weatherCodes.put("45", R.string.code_45);
        weatherCodes.put("46", R.string.code_46);
        weatherCodes.put("47", R.string.code_47);
        weatherCodes.put("3200", R.string.code_3200);
        inited = true;
    }
}
