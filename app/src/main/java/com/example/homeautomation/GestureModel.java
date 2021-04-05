package com.example.homeautomation;

import java.util.HashMap;

public class GestureModel {
    private static final HashMap<Integer, String> videoMap = new HashMap<>();
    private static final HashMap<Integer, String> gestures = new HashMap<>();

    static {
        gestures.put(0, "LightOn");
        gestures.put(1, "LightOff");
        gestures.put(2, "FanOn");
        gestures.put(3, "FanOff");
        gestures.put(4, "FanUp");
        gestures.put(5, "FanDown");
        gestures.put(6, "SetThermo");
        gestures.put(7, "Num0");
        gestures.put(8, "Num1");
        gestures.put(9, "Num2");
        gestures.put(10, "Num3");
        gestures.put(11, "Num4");
        gestures.put(12, "Num5");
        gestures.put(13, "Num6");
        gestures.put(14, "Num7");
        gestures.put(15, "Num8");
        gestures.put(16, "Num9");

        videoMap.put(0, "H-LightOn");
        videoMap.put(1, "H-LightOff");
        videoMap.put(2, "H-FanOn");
        videoMap.put(3, "H-FanOff");
        videoMap.put(4, "H-IncreaseFanSpeed");
        videoMap.put(5, "H-DecreaseFanSpeed");
        videoMap.put(6, "H-SetThermo");
        videoMap.put(7, "H-0");
        videoMap.put(8, "H-1");
        videoMap.put(9, "H-2");
        videoMap.put(10, "H-3");
        videoMap.put(11, "H-4");
        videoMap.put(12, "H-5");
        videoMap.put(13, "H-6");
        videoMap.put(14, "H-7");
        videoMap.put(15, "H-8");
        videoMap.put(16, "H-9");
    }

    public String getVideoMap(Integer key) {
        return videoMap.get(key);
    }
    public String getGestures(Integer key) {
        return gestures.get(key);
    }


}
