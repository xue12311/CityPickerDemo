package com.jl.city_picker.adapter;


import com.jl.city_picker.model.City;

/**
 * 城市选择监听接口
 */
public interface OnPickListener {
    void onPick(int position, City city);

    void onLocate();

    void onCancel();
}
