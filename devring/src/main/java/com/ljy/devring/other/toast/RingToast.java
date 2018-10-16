package com.ljy.devring.other.toast;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ljy.devring.util.DensityUtil;


/**
 * author : HJQ
 * github : https://github.com/getActivity/ToastUtils
 * time   : 2018/09/01
 * desc   : Toast工具类
 * modify : ljy
 */
public class RingToast {

    private static IToastStyle mToastStyle;
    private static Toast mToast;
    private static Context mContext;

    /**
     * 初始化
     *
     * @param context 应用的上下文
     */
    public static void init(Context context) {
        //如果这个上下文不是全局的上下文，就自动换成全局的上下文
        if (context != context.getApplicationContext()) {
            context = context.getApplicationContext();
        }
        mContext = context;

        if (mToastStyle != null) {
            mToast = new XToast(context);
            mToast.setGravity(mToastStyle.getGravity(), mToastStyle.getXOffset(), mToastStyle.getYOffset());

            View customView = mToastStyle.getCustomToastView();
            TextView textView;

            if (customView != null) {
                mToast.setView(customView);
                textView = ((XToast) mToast).getTextView();
            } else {
                textView = new TextView(context);
            }
            textView.setTextColor(mToastStyle.getTextColor());
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.sp2px(context, mToastStyle.getTextSize()));
            textView.setPadding(DensityUtil.dp2px(context, mToastStyle.getPaddingLeft()), DensityUtil.dp2px(context, mToastStyle.getPaddingTop()), DensityUtil.dp2px(context,
                    mToastStyle.getPaddingRight()), DensityUtil.dp2px(context, mToastStyle.getPaddingBottom()));
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(mToastStyle.getBackgroundColor());
            drawable.setCornerRadius(DensityUtil.dp2px(context, mToastStyle.getCornerRadius()));
            //setBackground API版本兼容
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textView.setBackground(drawable);
            } else {
                textView.setBackgroundDrawable(drawable);
            }
            if (mToastStyle.getMaxLines() > 0) {
                textView.setMaxLines(mToastStyle.getMaxLines());
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }

            if (customView == null) {
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mToast.setView(textView);
            }
        } else {
            mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }

    }

    /**
     * 显示一个对象的吐司
     *
     * @param object 对象
     */
    public static void show(Object object) {
        show(object != null ? object.toString() : "null");
    }

    /**
     * 显示一个吐司
     *
     * @param id 如果传入的是正确的string id就显示对应字符串
     *           如果不是则显示一个整数的string
     */
    public static void show(int id) {

        //吐司工具类还没有被初始化，必须要先调用init方法进行初始化
        if (mToast == null) {
            throw new IllegalStateException("ToastUtils has not been initialized");
        }

        try {
            //如果这是一个资源id
            show(mToast.getView().getContext().getResources().getText(id));
        } catch (Resources.NotFoundException ignored) {
            //如果这是一个int类型
            show(String.valueOf(id));
        }
    }

    /**
     * 显示一个吐司
     *
     * @param text 需要显示的文本
     */
    public static void show(CharSequence text) {

        //吐司工具类还没有被初始化，必须要先调用init方法进行初始化
        if (mToast == null) {
            throw new IllegalStateException("ToastUtils has not been initialized");
        }

        if (text == null || text.equals("")) return;

        //如果显示的文字超过了10个就显示长吐司，否则显示短吐司
        if (text.length() > 20) {
            mToast.setDuration(Toast.LENGTH_LONG);
        } else {
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        try {
            //判断是否在主线程中执行
            if (Looper.myLooper() == Looper.getMainLooper()) {
                mToast.setText(text);
                mToast.show();
            } else {
                //在子线程中显示处理
                Looper.prepare();
                mToast.setText(text);
                mToast.show();
                Looper.loop();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 获取当前Toast对象
     */
    public static Toast getToast() {
        return mToast;
    }

    /**
     * 给当前Toast设置新的布局
     */
    public static void setView(View view) {
        if (view == null) {
            throw new IllegalArgumentException("Views cannot be empty");
        }
        mToast.setView(view);
    }

    /**
     * 初始化Toast样式
     *
     * @param style 样式实现类
     */
    public static void initStyle(IToastStyle style) {
        if (style != null) {
            RingToast.mToastStyle = style;
            //如果吐司已经创建，就重新初始化吐司
            if (mToast != null) {
                //取消原有吐司的显示
                mToast.cancel();
                //重新初始化吐司类
                init(mContext);
            }
        }
    }

}
