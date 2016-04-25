package org.zywx.wbpalmstar.plugin.wheelpickview;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;

import java.util.HashMap;

public class EUEXWheelPickView extends EUExBase{

    public static final String TAG = "EUEXWheelPickView";

    private int x;
    private int y;
    private int height;
    private int width;
    private String color;

    private ACWheelPickView mView;

    public EUEXWheelPickView(Context context, EBrowserView inParent) {
        super(context, inParent);
    }


    public void close(String params[]) {
        if (mView == null) {
            return;
        }
        mBrwView.removeViewFromCurrentWindow(mView);
        mView = null;
    }

    public void open(String[] params) {
        if (params == null || params.length < 1) {
            errorCallback(0, 0, "error params!");
            return;
        }

        String str = params[0];
        JSONObject jsonObject;
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        try {
            jsonObject = new JSONObject(str);
            boolean isValid = jsonObject.has("src") && jsonObject.has("select");
            if(!isValid) {
                errorCallback(0, 0, "error params!");
                throw new JSONException("invalid params");
            }
            x = jsonObject.optInt("x", 0);
            //view的高度
            int heightPx = EUExUtil.dipToPixels(250);
            y = jsonObject.optInt("y", dm.heightPixels - heightPx);
            width = jsonObject.optInt("width", dm.widthPixels);
            height = jsonObject.optInt("height", heightPx);
            if (width > dm.widthPixels || width < 0) {
                width = dm.widthPixels;
            }
            if(height < 0) {
                height = 500;
            }
            paramMap.put("x", x);
            paramMap.put("y",y);
            paramMap.put("width", width);
            paramMap.put("height", height);
            String src = jsonObject.getString("src");
            if(!TextUtils.isEmpty(src)) {
                src = BUtility.makeRealPath(
                        BUtility.makeUrl(mBrwView.getCurrentUrl(), src),
                        mBrwView.getCurrentWidget().m_widgetPath,
                        mBrwView.getCurrentWidget().m_wgtType);

            }
            paramMap.put("src", src);
            JSONArray selectArray = jsonObject.optJSONArray("select");
            //对数组进行参数验证
            if (selectArray == null) {
                selectArray = new JSONArray("[0,0]");
            }
            int arrayLen = selectArray.length();
            for (int i = 0; i < arrayLen; i ++) {
                if(selectArray.getInt(i) < 0) {
                    selectArray.put(i, 0);
                    Log.i(TAG, "Invalid value for \"select\"");
                }
            }
            paramMap.put("select", selectArray);
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
            return;
        }

        try {
            if (mView != null) {
                return;
            }
            mView = new ACWheelPickView(mContext, this, paramMap);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    width, height);
            addView2CurrentWindow(mView, lp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addView2CurrentWindow(final View child,
            RelativeLayout.LayoutParams parms) {
        int w = parms.width;
        int h = parms.height;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(w, h);
        lp.gravity = Gravity.TOP|Gravity.LEFT;
        lp.leftMargin = x;
        lp.topMargin = y;
        adptLayoutParams(parms, lp);
        mBrwView.addViewToCurrentWindow(child, lp);

        mBrwView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //点击视图外部会关闭
                    DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                    float eventX = event.getX();
                    float eventY = event.getY();
                    boolean isInside = (eventX >= child.getX() && eventX <= child.getX() + child.getWidth()) &&
                            (eventY >= child.getY() && eventY <= child.getY() + child.getHeight());
                    if (!isInside) {
                        close(null);
                    }
                }
                return false;
            }
        });
    }

    @Override
    protected boolean clean() {
        close(null);
        return false;
    }
}
