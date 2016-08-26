package com.wang.baseadapter.animation;

import android.animation.Animator;
import android.view.View;

/**
 * base animation
 */
public interface  BaseAnimation {

    Animator[] getAnimators(View view);

}
