package com.jl.city_picker.db;

public class DBConfig {
    //上一个版本数据库（判断删除）
    public static final String DB_NAME_Old = "china_cities_v0.db";
    //最新的数据库名称
    public static final String DB_NAME_New = "china_cities_v1.db";

    //数据库 表名
    public static final String TABLE_NAME = "cities";

    public static final String COLUMN_C_NAME = "c_name";
    public static final String COLUMN_C_PROVINCE = "c_province";
    public static final String COLUMN_C_PINYIN = "c_pinyin";
    public static final String COLUMN_C_CODE = "c_code";
}
