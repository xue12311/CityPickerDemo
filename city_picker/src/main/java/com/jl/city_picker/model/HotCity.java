package com.jl.city_picker.model;

import com.jl.city_picker.AppConstant;

/**
 * 热门城市 实体类
 */
public class HotCity extends City {

    public HotCity(String name, String province, String code) {
        super(name, province, AppConstant.HotCityName, code);
    }
}
