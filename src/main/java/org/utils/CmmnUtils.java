package org.utils;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CmmnUtils {
    public static void sleep(long millis){
        try{
            Thread.currentThread().sleep(millis);
        }catch (Exception e){
            Logger.log(e.getMessage());
        }
    }

    public static long random(int range){
        if(range==0){
            return range;
        }
        return System.currentTimeMillis()%range;
    }

    public static boolean isEmpty(String str){
        return str==null || str.length()==0;
    }

    public static String list2Str(List<String> list){
        if(list == null || list.size() == 0){
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for(String s : list){
            builder.append(",").append(s);
        }
        return builder.toString().substring(1);
    }

    public static List<String> str2List(String str){
        List<String> list = new LinkedList<>();
        if(CmmnUtils.isEmpty(str)){
            return list;
        }
        String[] ids = str.split(",");
        for (String id : ids) {
            id = id.trim();
            if (id == null || id.length()==0) {
                continue;
            }
            if(!list.contains(id)) {
                list.add(id);
            }
        }
        return list;
    }

    public static String expressConvert(String name, String express){
        String s = express.replace("%", "/100.0");
        return name+s;
    }

    public static String prettyDouble(Double v){
        DecimalFormat decimalFormat = new DecimalFormat("###################.##");
        return decimalFormat.format(v);
    }

    public static Integer asInteger(String s){
        if(CmmnUtils.isEmpty(s)){
            return null;
        }
        try{
            return Integer.parseInt(s);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Double asDouble(String s){
        if(CmmnUtils.isEmpty(s)){
            return null;
        }
        try{
            return Double.parseDouble(s);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Long asLong(String s) {
        if(CmmnUtils.isEmpty(s)){
            return null;
        }
        try{
            return Long.parseLong(s);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Date asDate(String strDate){
        try {
            return new Date(Long.parseLong(strDate));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
