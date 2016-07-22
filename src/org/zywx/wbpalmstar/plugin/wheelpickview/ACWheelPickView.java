package org.zywx.wbpalmstar.plugin.wheelpickview;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.plugin.wheelpickview.PickerView.OnWheelChangedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ACWheelPickView extends FrameLayout {
    private static final String TAG = "ACWheelPickView";
    private EUEXWheelPickView mUexBaseObj;
    private Button mCompleteBttn;
    private Button mCancelBttn;
    private PickerView mProvinceView;
    private PickerView mCityView;
    private PickerView mDistrictView;
    private List<AreaInfo> mProvinceDate = new ArrayList<AreaInfo>();
    private HashMap<String, List<AreaInfo>> mCityDate = new HashMap<String, List<AreaInfo>>();
    private HashMap<String, List<AreaInfo>> mDistrictDate = new HashMap<String, List<AreaInfo>>();
    private AreaListAdapter<List<AreaInfo>> mProvinceAdapter = new AreaListAdapter<List<AreaInfo>>();
    private AreaListAdapter<List<AreaInfo>> mCityAdapter = new AreaListAdapter<List<AreaInfo>>();
    private AreaListAdapter<List<AreaInfo>> mDistrictAdapter = new AreaListAdapter<List<AreaInfo>>();
    private Context mContext;

    private static final String FIRST_LEVEL_NAME = "name";
    private static final String SECOND_LEVEL_Array = "sub";
    private static final String SECOND_LEVEL_NAME = "name";
    private static final String Third_LEVEL_NAME = "sub";

    private int firstLevelIndex = 0;
    private int secondLevelIndex = 0;
    private int thirdLevelIndex = 0;



    private Map<String, Object> params;

    public ACWheelPickView(Context context, EUEXWheelPickView base, Map<String, Object> params) {
        super(context);
        this.mContext = context;
        this.mUexBaseObj = base;
        this.params = params;
        initView();
    }

    private void initView() {
        CRes.init(mContext);
        LayoutInflater.from(mContext).inflate(CRes.plugin_wheelpickview_layout, this, true);
        mCancelBttn = (Button) findViewById(CRes.plugin_wheelpickview_cancel);
        mCancelBttn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mUexBaseObj.close(null);
            }
        });
        mCompleteBttn = (Button) findViewById(CRes.plugin_wheelpickview_complete);
        mCompleteBttn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUexBaseObj != null) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        JSONArray values = new JSONArray();
                        values.put(mProvinceView.getAreaInfoName());
                        values.put(mCityView.getAreaInfoName());
                        JSONArray indexes = new JSONArray();
                        indexes.put(firstLevelIndex);
                        indexes.put(secondLevelIndex);
                        if (!TextUtils.isEmpty(mDistrictView.getAreaInfoName())) {
                            values.put(mDistrictView.getAreaInfoName());
                            indexes.put(thirdLevelIndex);
                        }

                        jsonObject.put("data", values);
                        jsonObject.put("index", indexes);

                        String js = EUEXWheelPickView.SCRIPT_HEADER
                                + "if("
                                + WheelPickViewUtil.AREAPICKERVIEW_FUN_ON_CONFIRMCLICK
                                + "){"
                                + WheelPickViewUtil.AREAPICKERVIEW_FUN_ON_CONFIRMCLICK
                                + "('" + jsonObject.toString() + "');}";
                        mUexBaseObj.onCallback(js);
                        mUexBaseObj.close(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mProvinceView = (PickerView) findViewById(CRes.plugin_wheelpickview_province);
        mCityView = (PickerView) findViewById(CRes.plugin_wheelpickview_city);
        mDistrictView = (PickerView) findViewById(CRes.plugin_wheelpickview_district);
        initAreaData();
    }

    private void initAreaData(){
        String str = WheelPickViewUtil.readData(mContext, params.get("src").toString());
        try {
            JSONArray array = new JSONArray(str);
            int len = array.length();
            for (int i = 0; i < len; i ++) {
                JSONObject firstLevel = array.getJSONObject(i);
                AreaInfo areaInfo = new AreaInfo();
                String firstLevelId = "F" + i;
                areaInfo.setId(firstLevelId);
                areaInfo.setName(firstLevel.getString(FIRST_LEVEL_NAME));
                mProvinceDate.add(areaInfo);
                JSONArray secondLevelArray = firstLevel.getJSONArray(SECOND_LEVEL_Array);
                //解析获取第二类的数据
                int secondArrayLen = secondLevelArray.length();
                List<AreaInfo> secondList = new ArrayList<AreaInfo>();
                for (int j = 0; j < secondArrayLen; j ++) {
                    JSONObject secondLevel = secondLevelArray.getJSONObject(j);
                    AreaInfo secondArea = new AreaInfo();
                    String secondLevelId = firstLevelId + "S" + j;
                    secondArea.setId(secondLevelId);
                    secondArea.setName(secondLevel.getString(SECOND_LEVEL_NAME));
                    secondList.add(secondArea);
                    //解析获取第三类的数据
                    if (secondLevel.has(Third_LEVEL_NAME)) {
                        JSONArray thirdLevel = secondLevel.getJSONArray(Third_LEVEL_NAME);
                        List<AreaInfo> thirdList = new ArrayList<AreaInfo>();
                        for (int k = 0; k < thirdLevel.length(); k ++) {
                            AreaInfo thirdArea = new AreaInfo();
                            thirdArea.setId("T" + k);
                            thirdArea.setName(thirdLevel.getString(k));
                            thirdList.add(thirdArea);
                        }
                        mDistrictDate.put(secondLevelId, thirdList);
                    } else {
                        mDistrictView.setVisibility(View.GONE);
                    }

                }
                mCityDate.put(firstLevelId, secondList);
            }
            JSONArray jsonArray = (JSONArray)params.get("select");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("ACWheelPickView", "exception:" + e.getMessage());
            return;
        }
        JSONArray jsonArray = (JSONArray)params.get("select");

        mProvinceAdapter = new AreaListAdapter<List<AreaInfo>>(mProvinceDate);
        mProvinceView.setAdapter(mProvinceAdapter);
        mProvinceView.setCyclic(false);
        firstLevelIndex = jsonArray.optInt(0, 0);
        if (firstLevelIndex >= mProvinceDate.size()) {
            Log.i(TAG, "invalid firstLevel index:" + firstLevelIndex);
            firstLevelIndex = 0;
        }
        mProvinceView.setCurrentItem(firstLevelIndex);
        //获取默认的第二级的数据
        final String firstLevelId = mProvinceDate.get(firstLevelIndex).getId();
        setCityAdapter(firstLevelId);
        secondLevelIndex = jsonArray.optInt(1, 0);
        List<AreaInfo> cityAreaInfos = mCityDate.get(firstLevelId); //首先获取第二类的value;
        if (secondLevelIndex >= cityAreaInfos.size()) {
            Log.i(TAG, "invalid second index:" + secondLevelIndex);
            secondLevelIndex = 0;
        }
        mCityView.setCurrentItem(secondLevelIndex);

        //设置第三级的默认数据
        final String secondLevelId = cityAreaInfos.get(secondLevelIndex).getId();
        setDistrictAdapter(secondLevelId);
        thirdLevelIndex = jsonArray.optInt(2, 0);
        List<AreaInfo> districtAreaInfos = mDistrictDate.get(secondLevelId);
        //第三级有可能不存在
        if (districtAreaInfos == null || thirdLevelIndex >= districtAreaInfos.size()) {
            Log.i(TAG, "invalid third index:" + thirdLevelIndex);
            thirdLevelIndex = 0;
        }

        mDistrictView.setCurrentItem(thirdLevelIndex);

        mProvinceView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(PickerView wheel, int oldValue, int newValue) {
                firstLevelIndex = newValue;
                setCityAdapter(mProvinceAdapter.getAreaInfoId(newValue));
                secondLevelIndex = 0;
                mCityView.setCurrentItem(secondLevelIndex);
                setDistrictAdapter(mCityDate
                        .get(mProvinceAdapter.getAreaInfoId(newValue)).get(0)
                        .getId());
                thirdLevelIndex = 0;
                mDistrictView.setCurrentItem(thirdLevelIndex);
            }
        });


        mCityView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(PickerView wheel, int oldValue, int newValue) {
                secondLevelIndex = newValue;
                setDistrictAdapter(mCityAdapter.getAreaInfoId(newValue));
                thirdLevelIndex = 0;
                mDistrictView.setCurrentItem(thirdLevelIndex);
            }
        });

        mDistrictView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(PickerView wheel, int oldValue, int newValue) {
                thirdLevelIndex = newValue;
            }
        });
    }

    private void setCityAdapter(String infoId) {
        List<AreaInfo> cityAreaInfos = mCityDate.get(infoId);
        mCityAdapter = new AreaListAdapter<List<AreaInfo>>(cityAreaInfos);
        mCityView.setAdapter(mCityAdapter);


    }

    private void setDistrictAdapter(String infoId) {
        List<AreaInfo> districtAreaInfos = mDistrictDate.get(infoId);
        if (districtAreaInfos == null || districtAreaInfos.size() == 0) {
            districtAreaInfos = new ArrayList<AreaInfo>();
            AreaInfo areaInfo = new AreaInfo();
            areaInfo.setName("");
            areaInfo.setId("");
            districtAreaInfos.add(areaInfo);
        }
        mDistrictAdapter = new AreaListAdapter<List<AreaInfo>>(
                districtAreaInfos);
        mDistrictView.setAdapter(mDistrictAdapter);
    }
}
