package com.github.heyalex

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.core.view.doOnLayout
import com.github.heyalex.behavior.CornerSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior

open class CornerDrawer : FrameLayout {

    @LayoutRes
    protected var headerViewRes: Int = 0

    @LayoutRes
    private var contentViewRes: Int = 0


    @ColorInt
    private var contentColor: Int = 0

    @ColorInt
    private var headerColor: Int = 0

    private val container: FrameLayout
    private lateinit var header: View
    private lateinit var content: View

    private var bottomInset: Int = 0
    private var topInset: Int = 0
    private var isExpanded: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
        : super(context, attrs, defStyleAttr) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerDrawer)

        headerViewRes =
            typedArray.getResourceId(R.styleable.CornerDrawer_header_view, getHeaderStub())

        contentViewRes =
            typedArray.getResourceId(R.styleable.CornerDrawer_content_view, View.NO_ID)

        typedArray.recycle()

        if (headerViewRes != View.NO_ID) {
            header = LayoutInflater.from(context).inflate(headerViewRes, null).apply {
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }
        }

        if (contentViewRes != View.NO_ID) {
            content = LayoutInflater.from(context).inflate(contentViewRes, null)
        }

        container = FrameLayout(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
        super.addView(header)
        super.addView(container)
        addView(content)

        doOnLayout {
            val bottomSheetBehavior = BottomSheetBehavior.from(this) as CornerSheetBehavior
            bottomSheetBehavior.peekHeight = header.height + bottomInset
            bottomSheetBehavior.setHorizontalPeekHeight(header.width, false)
            onStartState()

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    header.alpha = lerp(1f, 0f, 0f, 0.15f, slideOffset)
                    header.visibility = if (slideOffset < 0.5) View.VISIBLE else View.GONE
                    content.visibility = if (slideOffset > 0.2) View.VISIBLE else View.GONE
                    container.alpha = lerp(0f, 1f, 0.2f, 0.8f, slideOffset)
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }
            })
        }
    }

    protected open fun getHeaderStub() = View.NO_ID

    protected open fun onStartState() {
        if (isExpanded) {
//            translationX = 0f
            container.alpha = 1f
            header.alpha = 0f
//            sheetBackground.fillColor = ColorStateList.valueOf(contentColor)
//            sheetBackground.interpolation = 0f
            header.visibility = View.GONE
            content.visibility = View.VISIBLE
            container.translationY = topInset.toFloat()
        } else {
//            translationX = maxTranslationX
            header.visibility = View.VISIBLE
            content.visibility = View.GONE
            container.alpha = 0f
            content.visibility = View.GONE
        }
    }

    override fun addView(child: View?) {
        container.addView(child)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            bottomInset = insets.systemWindowInsetBottom
            topInset = insets.systemWindowInsetTop
        }
        return super.onApplyWindowInsets(insets)
    }

//    override fun onSaveInstanceState(): Parcelable {
//        val superState = super.onSaveInstanceState()
//        superState?.let {
//            val customViewSavedState = CornerDrawerSavedState(superState)
//            val bottomSheetBehavior = BottomSheetBehavior.from(this)
//            customViewSavedState.isExpanded = bottomSheetBehavior.state ==
//                BottomSheetBehavior.STATE_EXPANDED
//            return customViewSavedState
//        }
//        return superState
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable) {
//        val customViewSavedState = state as CornerDrawerSavedState
//        if (customViewSavedState.isExpanded) {
//            isExpanded = true
//            BottomSheetBehavior.from(this).state = BottomSheetBehavior.STATE_EXPANDED
//        }
//        super.onRestoreInstanceState(customViewSavedState.superState)
//    }
//
//    protected class CornerDrawerSavedState : BaseSavedState {
//
//        internal var isExpanded: Boolean = false
//
//        constructor(superState: Parcelable) : super(superState)
//
//        private constructor(source: Parcel) : super(source) {
//            isExpanded = source.readInt() == 1
//        }
//
//        override fun writeToParcel(out: Parcel, flags: Int) {
//            super.writeToParcel(out, flags)
//            out.writeInt(if (isExpanded) 1 else 0)
//        }
//
//        companion object CREATOR : Parcelable.Creator<CornerDrawerSavedState> {
//            override fun createFromParcel(source: Parcel): CornerDrawerSavedState {
//                return CornerDrawerSavedState(source)
//            }
//
//            override fun newArray(size: Int): Array<CornerDrawerSavedState?> {
//                return arrayOfNulls(size)
//            }
//        }
//
//        override fun describeContents(): Int {
//            return 0
//        }
//    }
}