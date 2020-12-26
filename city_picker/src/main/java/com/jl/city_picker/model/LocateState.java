package com.jl.city_picker.model;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 定位状态 实体类
 */
public class LocateState {
    //定位中
    public static final int LOCATING    = 123;
    //定位成功
    public static final int SUCCESS     = 132;
    //定位失败
    public static final int FAILURE     = 321;

    @IntDef({SUCCESS, FAILURE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State{}
}
