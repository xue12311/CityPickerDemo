package com.jl.city_picker.model;

import android.text.TextUtils;
import com.jl.city_picker.AppConstant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author Bro0cL on 2016/1/26.
 * 城市 实体类
 */
public class City {
    //城市名称
    private String name;
    //所属省份
    private String province;
    //城市拼音
    private String pinyin;
    //城市码（暂时未使用）
    private String code;

    public City(String name, String province, String pinyin, String code) {
        this.name = name;
        this.province = province;
        this.pinyin = pinyin;
        this.code = code;
    }

    /***
     * 获取悬浮栏文本，（#、定位、热门 需要特殊处理）
     * @return
     */
    public String getSection() {
        if (TextUtils.isEmpty(pinyin)) {
            return "#";
        } else {
            String c = pinyin.substring(0, 1);
            Pattern p = Pattern.compile("[a-zA-Z]");
            Matcher m = p.matcher(c);
            if (m.matches()) {
                return c.toUpperCase();
                //在添加定位和热门数据时设置的section就是‘定’、’热‘开头
            } else if (TextUtils.equals(c, AppConstant.LocatedCityFirstWordName) || TextUtils.equals(c, AppConstant.HotCityFirstWordName)) {
                return pinyin;
            } else {
                return "#";
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
