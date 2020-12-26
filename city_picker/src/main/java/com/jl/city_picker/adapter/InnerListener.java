package com.jl.city_picker.adapter;


import com.jl.city_picker.model.City;

public interface InnerListener {
    /**
     * 选中城市
     * @param position 选择位置
     * @param city 城市
     */
    void dismiss(int position, City city);

    /**
     * 定位
     */
    void locate();
}
