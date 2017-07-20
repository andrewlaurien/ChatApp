package com.andrewlaurien.chatApp.utils;

import android.transition.Slide;
import android.view.Window;

/**
 * Created by andrewlaurienrsocia on 18/07/2017.
 */

public class commonFunc {


    public static void setupWindowAnimations(Window window) {

        Slide slide = new Slide();
        slide.setDuration(1000);
        window.setEnterTransition(slide);

    }

    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }


}
