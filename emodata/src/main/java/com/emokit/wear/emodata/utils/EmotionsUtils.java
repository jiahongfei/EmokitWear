package com.emokit.wear.emodata.utils;

import android.content.Context;

import com.emokit.wear.emodata.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取24种情绪，或者7种情绪
 * Created by jiahongfei on 15/11/23.
 */
public class EmotionsUtils {

    private static final HashMap<String, List<String>> emotions24HashMap
            = new HashMap<String, List<String>>();
    private static final HashMap<String, List<String>> emotions7HashMap
            = new HashMap<String, List<String>>();
    private static final HashMap<String, List<String>> emotions5HashMap
            = new HashMap<String, List<String>>();

    public static boolean isZh(Context context) {
//        Locale locale = context.getResources().getConfiguration().locale;
//        if (locale.getLanguage().startsWith("zh")) {
//            return true;
//        }
//        return false;
        return true;
    }

    /**
     * 获取24种情绪
     * @return
     */
    public static Map<String, List<String>> getEmotions24(Context context){
        if(null != emotions24HashMap && emotions24HashMap.size() > 0){
            return emotions24HashMap;
        }
        String[] emotions = context.getResources().getStringArray(R.array.emotion_24);
        for (int i = 0; i < emotions.length ;i++){
            String split = ";";
            if(isZh(context)){
                split = "；";
            }
            String[] tmpEmotions = emotions[i].split(split);
            List<String> tmpEmotionsList =  Arrays.asList(tmpEmotions)
                    .subList(1, tmpEmotions.length);
            emotions24HashMap.put(tmpEmotions[0], tmpEmotionsList);
        }

        return emotions24HashMap;
    }

    /**
     * 获取7种情绪
     * @return
     */
    public static Map<String, List<String>> getEmotions7(Context context){
        if(null != emotions7HashMap && emotions7HashMap.size() > 0){
            return emotions7HashMap;
        }
        String[] emotions = context.getResources().getStringArray(R.array.emotion_7);
        for (int i = 0; i < emotions.length ;i++){
            String split = ";";
            if(isZh(context)){
                split = "；";
            }
            String[] tmpEmotions = emotions[i].split(split);
            List<String> tmpEmotionsList =  Arrays.asList(tmpEmotions)
                    .subList(1, tmpEmotions.length);
            emotions7HashMap.put(tmpEmotions[0], tmpEmotionsList);
        }

        return emotions7HashMap;
    }

    /**
     * 获取5种情绪
     * @return
     */
    public static Map<String, List<String>> getEmotions5(Context context){
        if(null != emotions5HashMap && emotions5HashMap.size() > 0){
            return emotions5HashMap;
        }
        String[] emotions = context.getResources().getStringArray(R.array.emotion_5);
        for (int i = 0; i < emotions.length ;i++){
            String split = ";";
            if(isZh(context)){
                split = "；";
            }
            String[] tmpEmotions = emotions[i].split(split);
            List<String> tmpEmotionsList =  Arrays.asList(tmpEmotions)
                    .subList(1, tmpEmotions.length);
            emotions5HashMap.put(tmpEmotions[0], tmpEmotionsList);
        }

        return emotions5HashMap;
    }


    /**
     * 根据情绪返回的rc_main字段返回结果从24种情绪中找到结果
     * @return
     */
    public static List<String> getEmotions24ByEmoKey(Context context, String emoKey){

        if(null == emotions24HashMap || emotions24HashMap.size() <= 0){
            getEmotions24(context);
        }

        List<String> resultList = new ArrayList<String>();
        for (String keyString :
                emotions24HashMap.keySet()) {
            if(keyString.equals(emoKey)){
                resultList.addAll(emotions24HashMap.get(keyString));
            }
        }
        return resultList;
    }

    /**
     * 根据情绪返回的rc_main字段返回结果从7种情绪中找到结果
     * @return
     */
    public static List<String> getEmotions7ByEmoKey(Context context, String emoKey){

        if(null == emotions7HashMap || emotions7HashMap.size() <= 0){
            getEmotions7(context);
        }

        List<String> resultList = new ArrayList<String>();
        for (String keyString :
                emotions7HashMap.keySet()) {
            if(keyString.equals(emoKey)){
                resultList.addAll(emotions7HashMap.get(keyString));
            }
        }
        return resultList;
    }

    /**
     * 根据情绪返回的rc_main字段返回结果从5种情绪中找到结果
     * @return
     */
    public static List<String> getEmotions5ByEmoKey(Context context, String emoKey){

        if(null == emotions5HashMap || emotions5HashMap.size() <= 0){
            getEmotions5(context);
        }

        List<String> resultList = new ArrayList<String>();
        for (String keyString :
                emotions5HashMap.keySet()) {
            if(keyString.equals(emoKey)){
                resultList.addAll(emotions5HashMap.get(keyString));
            }
        }
        return resultList;
    }
}
