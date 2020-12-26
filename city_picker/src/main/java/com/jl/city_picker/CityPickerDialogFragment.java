package com.jl.city_picker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jl.city_picker.adapter.CityListAdapter;
import com.jl.city_picker.adapter.InnerListener;
import com.jl.city_picker.adapter.OnPickListener;
import com.jl.city_picker.adapter.decoration.DividerItemDecoration;
import com.jl.city_picker.adapter.decoration.SectionItemDecoration;
import com.jl.city_picker.db.DBManager;
import com.jl.city_picker.model.City;
import com.jl.city_picker.model.HotCity;
import com.jl.city_picker.model.LocateState;
import com.jl.city_picker.model.LocatedCity;
import com.jl.city_picker.util.ScreenUtil;
import com.jl.city_picker.widget.SideIndexBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Bro0cL
 * @Date: 2018/2/6 20:50
 */
public class CityPickerDialogFragment extends DialogFragment implements TextWatcher,
        View.OnClickListener, SideIndexBar.OnIndexTouchedChangedListener, InnerListener {
    private View mContentView;
    private RecyclerView mRecyclerView;
    //数据为空时显示
    private View mEmptyView;
    //索引选中后显示当前选中拼音提示
    private TextView mOverlayTextView;
    private SideIndexBar mIndexBar;
    //搜索框
    private EditText mSearchBox;
    //取消搜索按钮
    private TextView mCancelBtn;
    //删除搜索框内容的图标
    private ImageView mClearAllBtn;
    private LinearLayoutManager mLayoutManager;
    private CityListAdapter mAdapter;
    private List<City> mAllCities;
    private List<HotCity> mHotCities;
    private List<City> mResults;
    //索引条数据
    private List<String> mIndexItems;
    //数据库管理
    private DBManager dbManager;
    private int height;
    private int width;

    private boolean enableAnim = false;
    private int mAnimStyle = R.style.DefaultCityPickerAnimation;
    private LocatedCity mLocatedCity;
    //是否启用定位城市
    private Boolean isEnabledLocatedCity = true;
    //是否启用热门城市
    private Boolean isEnabledHotCity = true;
    //是否启用热门城市的测试数据
    private Boolean isEnabledTestHotCity = true;
    private int locateState;
    private OnPickListener mOnPickListener;

    /**
     * 获取实例
     *
     * @param enable 是否启用动画效果
     * @return
     */
    public static CityPickerDialogFragment newInstance(boolean enable) {
        final CityPickerDialogFragment fragment = new CityPickerDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("cp_enable_anim", enable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.CityPickerStyle);
    }

    /**
     * 是否启用定位城市
     */
    public void isEnabledLocatedCity(@NonNull Boolean isEnabledLocatedCity) {
        this.isEnabledLocatedCity = isEnabledLocatedCity;
    }

    /**
     * 是否启用定位城市
     */
    public void isEnabledHotCity(@NonNull Boolean isEnabledHotCity) {
        this.isEnabledHotCity = isEnabledHotCity;
    }

    /**
     * 是否启用热门城市的测试数据
     */
    public void isEnabledTestHotCity(@NonNull Boolean isEnabledTestHotCity) {
        this.isEnabledTestHotCity = isEnabledTestHotCity;
    }

    public void setLocatedCity(LocatedCity location) {
        mLocatedCity = location;
    }

    public void setHotCities(List<HotCity> data) {
        if (this.mHotCities != null) {
            this.mHotCities.clear();
        } else {
            this.mHotCities = new ArrayList<>();
        }
        if (data != null && !data.isEmpty()) {
            this.mHotCities = data;
        }
    }

    public void setAllCities(List<City> data) {
        if (this.mAllCities != null) {
            this.mAllCities.clear();
        } else {
            this.mAllCities = new ArrayList<>();
        }
        if (data != null && !data.isEmpty()) {
            this.mAllCities.addAll(data);
        }
    }

    @SuppressLint("ResourceType")
    public void setAnimationStyle(@StyleRes int resId) {
        this.mAnimStyle = resId <= 0 ? mAnimStyle : resId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.cp_dialog_city_picker, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initViews();
    }

    private void initViews() {
        mRecyclerView = mContentView.findViewById(R.id.cp_city_recyclerview);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SectionItemDecoration(getActivity(), mAllCities), 0);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()), 1);
        mAdapter = new CityListAdapter(getActivity(), mAllCities, mHotCities, locateState, isEnabledLocatedCity);
        mAdapter.autoLocate(true);
        mAdapter.setInnerListener(this);
        mAdapter.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //确保定位城市能正常刷新
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mAdapter.refreshLocationItem();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }
        });

        mEmptyView = mContentView.findViewById(R.id.cp_empty_view);
        mOverlayTextView = mContentView.findViewById(R.id.cp_overlay);
        mIndexBar = mContentView.findViewById(R.id.cp_side_index_bar);
        mIndexBar.setIndexItems(mIndexItems);
        mIndexBar.setNavigationBarHeight(ScreenUtil.getNavigationBarHeight(getActivity()));
        mIndexBar.setOverlayTextView(mOverlayTextView).setOnIndexChangedListener(this);
        mSearchBox = mContentView.findViewById(R.id.cp_search_box);
        mSearchBox.addTextChangedListener(this);
        mSearchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoftKeyboard(getDialog());
            }
            return false;
        });
        mCancelBtn = mContentView.findViewById(R.id.cp_cancel);
        mClearAllBtn = mContentView.findViewById(R.id.cp_clear_all);
        mCancelBtn.setOnClickListener(this);
        mClearAllBtn.setOnClickListener(this);
    }

    private void initData() {
        Bundle args = getArguments();
        if (args != null) {
            enableAnim = args.getBoolean("cp_enable_anim");
        }
        if (dbManager == null) {
            dbManager = new DBManager(getActivity());
        }
        //是否启用了热门城市
        if (isEnabledHotCity) {
            //初始化热门城市
            if (mHotCities == null) {
                mHotCities = new ArrayList<>();
            }
            //是否启用热门城市的测试数据
            if (mHotCities.isEmpty() && isEnabledTestHotCity) {
                mHotCities.add(new HotCity("北京", "北京", "101010100"));
                mHotCities.add(new HotCity("上海", "上海", "101020100"));
                mHotCities.add(new HotCity("广州", "广东", "101280101"));
                mHotCities.add(new HotCity("深圳", "广东", "101280601"));
                mHotCities.add(new HotCity("天津", "天津", "101030100"));
                mHotCities.add(new HotCity("杭州", "浙江", "101210101"));
                mHotCities.add(new HotCity("南京", "江苏", "101190101"));
                mHotCities.add(new HotCity("成都", "四川", "101270101"));
                mHotCities.add(new HotCity("武汉", "湖北", "101200101"));
            }
        }
        //初始化定位城市，默认为空时会自动回调定位
        if (mLocatedCity == null) {
            mLocatedCity = new LocatedCity(getString(R.string.cp_locating), "未知", "0");
            locateState = LocateState.LOCATING;
        } else {
            locateState = LocateState.SUCCESS;
        }
        if (mAllCities == null) {
            mAllCities = new ArrayList<>();
        }
        if (mAllCities.size() <= 0) {
            mAllCities.clear();

            mAllCities.addAll(dbManager.getAllCities());
        }
        //启用热门城市
        if (isEnabledHotCity) {
            mAllCities.add(0, new HotCity(AppConstant.HotCityName, "未知", "0"));
        }
        //启用定位城市
        if (isEnabledLocatedCity) {
            mAllCities.add(0, mLocatedCity);
        }
        mResults = mAllCities;
        //索引数据修改
        if (mIndexItems == null) {
            mIndexItems = new ArrayList<>();
            mIndexItems.addAll(Arrays.asList(AppConstant.DEFAULT_INDEX_ITEMS));
        }
        //启用热门城市
        if (isEnabledHotCity) {
            mIndexItems.add(0, AppConstant.HotCityNameIndexItems);
        }
        //启用定位城市
        if (isEnabledLocatedCity) {
            mIndexItems.add(0, AppConstant.LocatedCityNameIndexItems);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            //返回键监听
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (mOnPickListener != null) {
                    mOnPickListener.onCancel();
                }
            }
            return false;
        });
        measure();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(width, height - ScreenUtil.getStatusBarHeight(getActivity()));
            if (enableAnim) {
                window.setWindowAnimations(mAnimStyle);
            }
        }
    }

    //测量宽高
    private void measure() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(dm);
            height = dm.heightPixels;
            width = dm.widthPixels;
        } else {
            DisplayMetrics dm = getResources().getDisplayMetrics();
            height = dm.heightPixels;
            width = dm.widthPixels;
        }
    }

    /**
     * 搜索框监听
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String keyword = s.toString();
        if (TextUtils.isEmpty(keyword)) {
            mClearAllBtn.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mResults = mAllCities;
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            mAdapter.updateData(mResults);
        } else {
            mClearAllBtn.setVisibility(View.VISIBLE);
            //开始数据库查找
            mResults = dbManager.searchCity(keyword);
            ((SectionItemDecoration) (mRecyclerView.getItemDecorationAt(0))).setData(mResults);
            if (mResults == null || mResults.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                mAdapter.updateData(mResults);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cp_cancel) {
            dismiss();
            if (mOnPickListener != null) {
                mOnPickListener.onCancel();
            }
        } else if (id == R.id.cp_clear_all) {
            hideSoftKeyboard(getDialog());
            mSearchBox.setText("");
        }
    }


    @Override
    public void onIndexChanged(String index, int position) {
        //滚动RecyclerView到索引位置
        mAdapter.scrollToSection(index);
    }

    public void locationChanged(LocatedCity location, int state) {
        mAdapter.updateLocateState(location, state);
    }

    @Override
    public void dismiss(int position, City data) {
        dismiss();
        if (mOnPickListener != null) {
            mOnPickListener.onPick(position, data);
        }
    }

    @Override
    public void locate() {
        if (mOnPickListener != null) {
            mOnPickListener.onLocate();
        }
    }

    public void setOnPickListener(OnPickListener listener) {
        this.mOnPickListener = listener;
    }

    /**
     * 自动关闭软键盘
     */
    public static void hideSoftKeyboard(Dialog dialog) {
        if (dialog != null) {
            View view = dialog.getCurrentFocus();
            if (view == null) {
                Window window = dialog.getWindow();
                if (window != null) {
                    View decorView = window.getDecorView();
                    View focusView = decorView.findViewWithTag("keyboardTagView");
                    if (focusView == null) {
                        view = new EditText(window.getContext());
                        view.setTag("keyboardTagView");
                        ((ViewGroup) decorView).addView(view, 0, 0);
                    } else {
                        view = focusView;
                    }
                    view.requestFocus();
                }
            }
            if (view != null) {
                Activity activity = dialog.getOwnerActivity();
                if (activity != null) {
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                    }
                }
            }
        }
    }
}
