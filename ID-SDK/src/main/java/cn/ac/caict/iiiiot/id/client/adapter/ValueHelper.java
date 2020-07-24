package cn.ac.caict.iiiiot.id.client.adapter;

import cn.ac.caict.iiiiot.id.client.utils.Util;

public class ValueHelper {

    private static ValueHelper valueHelper;
    private ValueHelper(){

    }

    public static ValueHelper getInstance() {
        if(valueHelper ==null){
            synchronized (ValueHelper.class){
                if(valueHelper ==null){
                    valueHelper = new ValueHelper();
                }
            }
        }
        return valueHelper;
    }

    public String extraPrefix(String identifier) {
        String prefix;
        int separator = identifier.indexOf("/");
        if (separator != -1) {
            if (Util.startsWithCaseInsensitive(identifier, "0.NA/")) {
                //前缀
                prefix = identifier;
            } else {
                prefix = identifier.substring(0, separator);
            }
        } else {
            //前缀
            prefix = identifier;
        }
        return prefix;
    }

}
