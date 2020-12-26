package com.jl.citypickerdemo

import android.os.Looper
import com.blankj.utilcode.util.LogUtils
import com.jl.city_picker.CityPicker
import com.jl.city_picker.adapter.OnPickListener
import com.jl.city_picker.model.City
import com.jl.city_picker.model.LocateState
import com.jl.city_picker.model.LocatedCity
import com.jl.citypickerdemo.databinding.ActivityCityPickerDemoBinding
import com.zjx.app_common_library.base.viewbinding.BaseVbActivity
import com.zjx.app_common_library.utils.WeakHandler

/**
 * 城市选择
 */
class CityPickerActivity : BaseVbActivity<ActivityCityPickerDemoBinding>() {
    override fun initView() {
        mViewBinding.sbCityPicker.setOnClickListener {
            CityPicker.from(this)
                .enableAnimation(true)
                .setOnPickListener(object : OnPickListener {
                    override fun onPick(position: Int, city: City?) {
                        mViewBinding.tvMessage.setText(city?.name)
                    }
                    override fun onLocate() {
                        WeakHandler(Looper.getMainLooper())
                            .postDelayed({
                                if ((0..10).random() % 2 == 1) {
                                    CityPicker.from(this@CityPickerActivity)
                                        .locateComplete(
                                            LocatedCity("长沙", "湖南省", "210111"),
                                            LocateState.SUCCESS
                                        )
                                } else {
                                    CityPicker.from(this@CityPickerActivity).locateFail()
                                }
                            }, 3000)
                    }

                    override fun onCancel() {
                        mViewBinding.tvMessage.setText("取消选择")
                    }
                })
                .show()
        }
    }
}