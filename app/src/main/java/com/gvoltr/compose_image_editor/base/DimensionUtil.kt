package com.gvoltr.compose_image_editor.base

import android.content.Context
import android.util.TypedValue
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DimensionUtil @Inject constructor(@ApplicationContext private val context: Context) {

    fun convertDpToPixel(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
    }

    fun convertSpToPixel(sp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
    }
}
