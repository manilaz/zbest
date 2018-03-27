package com.zbest.raft.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhangbin on 2018/3/27.
 */
public class PropertiesUtil {


    public static final Map<String,Properties> PropertiesMap = new HashMap<String, Properties>();

    static {

        for (PropertiesEnum e : PropertiesEnum.values()){
            BufferedInputStream inputStream ;
            try {
                inputStream = new BufferedInputStream(new FileInputStream(e.getPath()));
                Properties properties = new Properties();
                properties.load(inputStream);
                PropertiesMap.put(e.getName(),properties);

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }


    public static String getValue(PropertiesEnum e,String key){
        Properties properties = PropertiesMap.get(e.getName());

        if(properties != null){
            return properties.getProperty(key);
        }
        return null;
    }

}
