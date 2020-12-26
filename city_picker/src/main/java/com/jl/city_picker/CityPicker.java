package com.jl.city_picker;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jl.city_picker.adapter.OnPickListener;
import com.jl.city_picker.model.City;
import com.jl.city_picker.model.HotCity;
import com.jl.city_picker.model.LocateState;
import com.jl.city_picker.model.LocatedCity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 17:52
 */
public class CityPicker {
    private static final String TAG = "CityPicker";
    private WeakReference<FragmentActivity> mContext;
    private WeakReference<Fragment> mFragment;
    private WeakReference<FragmentManager> mFragmentManager;

    private boolean enableAnim;
    private int mAnimStyle;
    //定位城市
    private LocatedCity mLocation;
    //是否启用定位城市
    private Boolean isEnabledLocatedCity = true;
    //热门城市
    private List<HotCity> mHotCities;
    //是否启用热门城市
    private Boolean isEnabledHotCity = true;
    //是否启用热门城市的测试数据
    private Boolean isEnabledTestHotCity = true;
    //全部城市
    private List<City> mAllCities;
    //监听接口
    private OnPickListener mOnPickListener;

    private CityPicker() {
    }

    private CityPicker(Fragment fragment) {
        this(fragment.getActivity(), fragment);
        mFragmentManager = new WeakReference<>(fragment.getChildFragmentManager());
    }

    private CityPicker(FragmentActivity activity) {
        this(activity, null);
        mFragmentManager = new WeakReference<>(activity.getSupportFragmentManager());
    }

    private CityPicker(FragmentActivity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static CityPicker from(Fragment fragment) {
        return new CityPicker(fragment);
    }

    public static CityPicker from(FragmentActivity activity) {
        return new CityPicker(activity);
    }

    /**
     * 设置动画效果
     *
     * @param animStyle
     * @return
     */
    public CityPicker setAnimationStyle(@StyleRes int animStyle) {
        this.mAnimStyle = animStyle;
        return this;
    }

    /**
     * 设置当前已经定位的城市
     *
     * @param location
     * @return
     */
    public CityPicker setLocatedCity(LocatedCity location) {
        this.mLocation = location;
        return this;
    }

    public CityPicker setHotCities(List<HotCity> data) {
        this.mHotCities = data;
        return this;
    }

    /**
     * 是否启用定位城市
     */
    public CityPicker isEnabledLocatedCity(@NonNull Boolean isEnabledLocatedCity) {
        this.isEnabledLocatedCity = isEnabledLocatedCity;
        return this;
    }

    /**
     * 是否启用热门城市
     */
    public CityPicker isEnabledHotCity(@NonNull Boolean isEnabledHotCity) {
        this.isEnabledHotCity = isEnabledHotCity;
        return this;
    }

    /**
     * 是否启用热门城市的测试数据
     */
    public CityPicker isEnabledTestHotCity(@NonNull Boolean isEnabledTestHotCity) {
        this.isEnabledTestHotCity = isEnabledTestHotCity;
        return this;
    }

    //设置全部城市
    public CityPicker setAllCities(List<City> data) {
        if (this.mAllCities != null) {
            this.mAllCities.clear();
        } else {
            this.mAllCities = new ArrayList<>();
        }
        if (data != null && !data.isEmpty()) {
            this.mAllCities.addAll(data);
        }
        return this;
    }

    /**
     * 启用动画效果，默认为false
     *
     * @param enable
     * @return
     */
    public CityPicker enableAnimation(boolean enable) {
        this.enableAnim = enable;
        return this;
    }

    /**
     * 设置选择结果的监听器
     *
     * @param listener
     * @return
     */
    public CityPicker setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
        return this;
    }

    public void show() {
        FragmentTransaction ft = mFragmentManager.get().beginTransaction();
        final Fragment prev = mFragmentManager.get().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev).commit();
            ft = mFragmentManager.get().beginTransaction();
        }
        ft.addToBackStack(null);
        final CityPickerDialogFragment cityPickerFragment =
                CityPickerDialogFragment.newInstance(enableAnim);
        cityPickerFragment.setAllCities(mAllCities);
        cityPickerFragment.isEnabledLocatedCity(isEnabledLocatedCity);
        cityPickerFragment.setLocatedCity(mLocation);
        cityPickerFragment.isEnabledHotCity(isEnabledHotCity);
        cityPickerFragment.isEnabledTestHotCity(isEnabledTestHotCity);
        cityPickerFragment.setHotCities(mHotCities);
        cityPickerFragment.setAnimationStyle(mAnimStyle);
        cityPickerFragment.setOnPickListener(mOnPickListener);
        cityPickerFragment.show(ft, TAG);
    }

    /**
     * 定位完成
     *
     * @param location
     * @param state
     */
    public void locateComplete(LocatedCity location, @LocateState.State int state) {
        CityPickerDialogFragment fragment = (CityPickerDialogFragment) mFragmentManager.get().findFragmentByTag(TAG);
        if (fragment != null) {
            fragment.locationChanged(location, state);
        }
    }

    /**
     * 定位失败
     */
    public void locateFail() {
        CityPickerDialogFragment fragment = (CityPickerDialogFragment) mFragmentManager.get().findFragmentByTag(TAG);
        if (fragment != null) {
            LocatedCity mLocatedCity = new LocatedCity(fragment.getString(R.string.cp_locate_failed), "未知", "0");
            fragment.locationChanged(mLocatedCity, LocateState.FAILURE);
        }
    }
}
