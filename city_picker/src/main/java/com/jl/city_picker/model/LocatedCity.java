package com.jl.city_picker.model;

import com.jl.city_picker.AppConstant;

/**
 * 定位城市 实体类
 */
public class LocatedCity extends City {

    public LocatedCity(String name, String province, String code) {
        super(name, province, AppConstant.LocatedCityName, code);
    }
}
