package com.iyuba.core.me.activity;

/**
 * 编辑个人信息界面
 *
 * @author chentong
 * @version 1.0
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.base.LocationRequest;
import com.iyuba.core.common.protocol.base.LocationResponse;
import com.iyuba.core.common.protocol.message.RequestEditUserInfo;
import com.iyuba.core.common.protocol.message.RequestUserDetailInfo;
import com.iyuba.core.common.protocol.message.ResponseEditUserInfo;
import com.iyuba.core.common.protocol.message.ResponseUserDetailInfo;
import com.iyuba.core.common.sqlite.op.SchoolOp;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.GetLocation;
import com.iyuba.core.common.util.JudgeZodicaAndConstellation;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.lil.user.util.LoginUtil;
import com.iyuba.core.me.adapter.SchoolListAdapter;
import com.iyuba.core.me.sqlite.mode.EditUserInfo;
import com.iyuba.core.me.sqlite.mode.School;
import com.iyuba.core.me.widget.ModifyNameDialog;
import com.iyuba.lib.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import permissions.dispatcher.PermissionUtils;

public class EditUserInfoActivity extends BasisActivity implements OnWheelChangedListener {
    private Context mContext;
    private TextView gender, birthday, zodiac, constellation, occupation, education, school, location;
    private EditText etAffectivestatus, etLookingfor, etInterest, etIntro;
    //	private EditText location;
    private LinearLayout changeImageLayout;
    private ImageView userImage;
    private Button back, save;
    private LinearLayout llName;
    private TextView tvName;
    private String userName;
    private final static int GENDER_DIALOG = 1;// 性别选择
    private final static int DATE_DIALOG = 2;// 日期选择
    private final static int SCHOOL_DIALOG = 3;// 学校选择
    private final static int LOCATION_DIALOG = 4;// 学校选择
    private final static int OCCUPATION_DIALOG = 5;//身份选择
    private final static int EDUCATION_DIALOG = 6;//学历选择
    private Calendar calendar = null;
    private EditUserInfo editUserInfo = new EditUserInfo();
    private CustomDialog waitingDialog;
    private String cityName;
    // school
    private View schoolDialog;
    // location
    private View locationDialog;

    private EditText searchText;
    private Button sure;
    private View clear;
    private ListView schoolList;
    private ArrayList<School> schools = new ArrayList<School>();
    private SchoolListAdapter schoolListAdapter;
    private StringBuffer tempSchool;

    /**
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null
     */
    private JSONObject mJsonObj;

    private Button btnShowChoose;
    /** * 省的WheelView控件*/
    private WheelView mProvince;
    /** * 市的WheelView控件 */
    private WheelView mCity;
    /*** 区的WheelView控件*/
    private WheelView mArea;

    /** * 所有省 */
    private String[] mProvinceDatas;
    /** * key - 省 value - 市s*/
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /*** key - 市 values - 区s*/
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();

    /*** 当前省的名称*/
    private String mCurrentProviceName;
    /** * 当前市的名称*/
    private String mCurrentCityName;
    /*** 当前区的名称*/
    private String mCurrentAreaName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edituserinfo);
        mContext = this;
        CrashApplication.getInstance().addActivity(this);
        waitingDialog = WaittingDialog.showDialog(mContext);
        initWidget();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadInfo();//???
        userName = UserInfoManager.getInstance().getUserName();
    }

    private void initWidget() {

        userImage = (ImageView) findViewById(R.id.iveditPortrait);
        gender = (TextView) findViewById(R.id.editGender);
        birthday = (TextView) findViewById(R.id.editBirthday);
        location = (TextView) findViewById(R.id.editResideLocation);
        etAffectivestatus = (EditText) findViewById(R.id.editAffectivestatus);
        etLookingfor = (EditText) findViewById(R.id.editLookingfor);
        etIntro = (EditText) findViewById(R.id.editIntro);
        etInterest = (EditText) findViewById(R.id.editInterest);
        zodiac = (TextView) findViewById(R.id.editZodiac);
        constellation = (TextView) findViewById(R.id.editConstellation);
        changeImageLayout = (LinearLayout) findViewById(R.id.editPortrait);
        back = (Button) findViewById(R.id.button_back);
        save = (Button) findViewById(R.id.editinfo_save_btn);
        occupation = (TextView) findViewById(R.id.tvOccupation);
        education = (TextView) findViewById(R.id.tvEducation);//学历
        school = (TextView) findViewById(R.id.editSchool);//大学名称

        llName = findViewById(R.id.ll_name);
        tvName = findViewById(R.id.editName);
    }

    private void setText() {
        tvName.setText(userName);
        if (editUserInfo.getEdGender() != null) {
            if (editUserInfo.getEdGender().equals("1")) {
                gender.setText(getResources().getStringArray(R.array.gender)[0]);
            } else if (editUserInfo.getEdGender().equals("2")) {
                gender.setText(getResources().getStringArray(R.array.gender)[1]);
            }
        } else {
            gender.setText(getResources().getStringArray(R.array.gender)[0]);
        }
        birthday.setText(editUserInfo.getBirthday());
        zodiac.setText(editUserInfo.getEdZodiac());
        constellation.setText(editUserInfo.getEdConstellation());
        if (editUserInfo != null && editUserInfo.getEdResideCity() != null &&
                !editUserInfo.getEdResideCity().trim().isEmpty()) {
            location.setText(editUserInfo.getEdResideCity());
        }
        etAffectivestatus.setText(editUserInfo.getEdAffectivestatus());
        etLookingfor.setText(editUserInfo.getEdLookingfor());
        etIntro.setText(editUserInfo.getEdIntro());
        etInterest.setText(editUserInfo.getEdInterest());
        occupation.setText(editUserInfo.getEdOccupation());
        education.setText(editUserInfo.getEdEducation());
        if (editUserInfo != null && editUserInfo.getUniversity() != null && !editUserInfo.getUniversity().trim().isEmpty()) {
            school.setText(editUserInfo.getUniversity());
        }
        GitHubImageLoader.Instace(mContext).setCirclePic(
                String.valueOf(UserInfoManager.getInstance().getUserId()), userImage);
    }

    private void LoadInfo() {
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    waitingDialog.show();
//				GetAddr();
                    handler.sendEmptyMessage(1);
                    break;
                case 1:
                    ExeProtocol.exe(
                            new RequestUserDetailInfo(String.valueOf(UserInfoManager.getInstance().getUserId())),
                            new ProtocolResponse() {

                                @Override
                                public void finish(BaseHttpResponse bhr) {

                                    ResponseUserDetailInfo responseUserDetailInfo = (ResponseUserDetailInfo) bhr;
                                    if (responseUserDetailInfo.result.equals("211")) {
                                        editUserInfo = responseUserDetailInfo.editUserInfo;
                                    }
                                    handler.sendEmptyMessage(2);
                                }

                                @Override
                                public void error() {

                                    handler.sendEmptyMessage(2);
                                    handler.sendEmptyMessage(6);
                                }
                            });
                    break;
                case 2:
                    waitingDialog.dismiss();
                    setText();
                    break;
                case 3:
                    save.setClickable(true);
                    CustomToast.showToast(mContext, R.string.person_info_success);
                    break;
                case 4:
                    save.setClickable(true);
                    CustomToast.showToast(mContext, R.string.person_info_fail);
                    break;
                case 5:
                    setText();
                    break;
                case 6:
                    CustomToast.showToast(mContext, R.string.check_network);
                    break;
                case 7:
                    CustomToast.showToast(mContext, R.string.check_gps);
                    break;
                case 8:
                    schools = new SchoolOp(mContext).findDataByFuzzy(tempSchool
                            .toString());
                    schoolListAdapter = new SchoolListAdapter(mContext, schools);
                    schoolList.setAdapter(schoolListAdapter);
                    schoolListAdapter.notifyDataSetChanged();
                    break;
                case 9:
                    schools = new SchoolOp(mContext).findDataByFuzzy(tempSchool
                            .toString());
                    schoolListAdapter.setData(schools);
                    schoolListAdapter.notifyDataSetChanged();
                    break;
                case 10:
                    location.setText(cityName);
                    break;
                default:
                    break;
            }
        }
    };

    private void setListener() {
        int userId = UserInfoManager.getInstance().getUserId();
        llName.setOnClickListener(v -> new ModifyNameDialog(mContext)
                .setUserId(userId)
                .setOldName(userName)
                .setModifySuccessCallback(newName -> {
                    userName = newName;
                    tvName.setText(newName);

                    //这里原来是设置名称了，修改后不太对，之后看看吧
                })
                .show());

        back.setOnClickListener(v -> onBackPressed());

        changeImageLayout.setOnClickListener(v -> {
            if (!UserInfoManager.getInstance().isLogin()) {
                showNormalDialog();
            } else {
                Intent intent = new Intent(mContext, UpLoadImage.class);
                startActivity(intent);
            }
        });

        gender.setOnClickListener(v -> createDialog(GENDER_DIALOG));
        birthday.setOnClickListener(v -> createDialog(DATE_DIALOG));
        occupation.setOnClickListener(v -> createDialog(OCCUPATION_DIALOG));
        education.setOnClickListener(v -> createDialog(EDUCATION_DIALOG));
        save.setOnClickListener(v -> {

            save.setClickable(false);
            String city = location.getText().toString();

            String editAffectivestatus = etAffectivestatus.getText().toString();
            String editLookingfor = etLookingfor.getText().toString();
            String editIntro = etIntro.getText().toString();
            String editInterest = etInterest.getText().toString();

            city = city.trim();
            String value = "", key = "";
            StringBuffer sb = new StringBuffer("");
            int i;
            if (city.contains(" ")) {
                String[] area = city.split(" ");
                sb.append(editUserInfo.getEdGender()).append(",");
                sb.append(editUserInfo.getEdBirthYear()).append(",");
                sb.append(editUserInfo.getEdBirthMonth()).append(",");
                sb.append(editUserInfo.getEdBirthDay()).append(",");
                sb.append(editUserInfo.getEdConstellation()).append(",");
                sb.append(editUserInfo.getEdZodiac()).append(",");
                sb.append(editAffectivestatus).append(",");
                sb.append(editLookingfor).append(",");
                sb.append(editIntro).append(",");
                sb.append(editInterest).append(",");
                sb.append(occupation.getText()).append(",");
                sb.append(education.getText()).append(",");
                sb.append(school.getText()).append(",");
                for (i = 0; i < area.length; i++) {
                    sb.append(area[i]).append(",");
                }
                if (area.length == 2) {
                    sb.append(" ").append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                value = sb.toString();
//					if (i == 3) {
                key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,affectivestatus,lookingfor,bio,interest,"
                        + "occupation,education,graduateschool,resideprovince,residecity,residedist";
//					} else {
//						key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,graduateschool,resideprovince,residecity";
//					}
            } else {
                sb.append(editUserInfo.getEdGender()).append(",");
                sb.append(editUserInfo.getEdBirthYear()).append(",");
                sb.append(editUserInfo.getEdBirthMonth()).append(",");
                sb.append(editUserInfo.getEdBirthDay()).append(",");
                sb.append(editUserInfo.getEdConstellation()).append(",");
                sb.append(editUserInfo.getEdZodiac()).append(",");
                sb.append(editAffectivestatus).append(",");
                sb.append(editLookingfor).append(",");
                sb.append(editIntro).append(",");
                sb.append(editInterest).append(",");
                sb.append(occupation.getText()).append(",");
                sb.append(education.getText()).append(",");
                sb.append(school.getText()).append(",");
                sb.append(city);
                value = sb.toString();
                key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,affectivestatus,lookingfor,bio,interest,"
                        + "occupation,education,graduateschool,residecity";
            }
            ExeProtocol.exe(
                    new RequestEditUserInfo(String.valueOf(UserInfoManager.getInstance().getUserId()), key, value),
                    new ProtocolResponse() {

                        @Override
                        public void finish(BaseHttpResponse bhr) {

                            ResponseEditUserInfo responseEditUserInfo = (ResponseEditUserInfo) bhr;
                            if (responseEditUserInfo.result.equals("221")) {
                                handler.sendEmptyMessage(3);
                            } else {
                                handler.sendEmptyMessage(4);
                            }
                        }

                        @Override
                        public void error() {

                            handler.sendEmptyMessage(6);
                        }
                    });
        });
        location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                createDialog(LOCATION_DIALOG);
            }
        });
        school.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                createDialog(SCHOOL_DIALOG);
            }
        });
    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(mContext);
        normalDialog.setIcon(R.drawable.iyubi_icon);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("临时用户无法更换头像！");
        normalDialog.setPositiveButton("登录",
                (dialog, which) -> {
                    LoginUtil.startToLogin(mContext);
                });
        normalDialog.setNegativeButton("确定",
                (dialog, which) -> {

                });
        // 显示
        normalDialog.show();
    }

    private void initSchoolDialog(final Dialog dialog) {
        searchText = (EditText) schoolDialog.findViewById(R.id.search_text);
        sure = (Button) schoolDialog.findViewById(R.id.search);
        clear = schoolDialog.findViewById(R.id.clear);
        schoolList = (ListView) schoolDialog.findViewById(R.id.school_list);
        searchText.requestFocus();
        searchText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {


            }

            @Override
            public void afterTextChanged(Editable arg0) {
                tempSchool = new StringBuffer("");
                int size = arg0.length();
                for (int i = 0; i < size; i++) {
                    tempSchool.append(arg0.charAt(i));
                    tempSchool.append('%');
                }
                handler.sendEmptyMessage(9);
            }
        });
        clear.setOnClickListener(v -> {

            searchText.setText("");
            schools.clear();
            tempSchool = new StringBuffer("");
            handler.sendEmptyMessage(8);
        });
        sure.setOnClickListener(v -> {

            school.setText(searchText.getText().toString());
            dialog.dismiss();
        });
        schoolList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                school.setText(schools.get(arg2).school_name);
                dialog.dismiss();
            }
        });
        tempSchool = new StringBuffer("");
        handler.sendEmptyMessage(8);
    }

    private void initLocationDialog(final Dialog dialog) {
        initJsonData();

        mProvince = (WheelView) locationDialog.findViewById(R.id.id_province);
        mCity = (WheelView) locationDialog.findViewById(R.id.id_city);
        mArea = (WheelView) locationDialog.findViewById(R.id.id_area);
        btnShowChoose = (Button) locationDialog.findViewById(R.id.btn_showchoose);

        initDatas();

        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        // 添加change事件
        mProvince.addChangingListener(this);
        // 添加change事件
        mCity.addChangingListener(this);
        // 添加change事件
        mArea.addChangingListener(this);

        btnShowChoose.setOnClickListener(v -> {
            location.setText(mCurrentProviceName + " " + mCurrentCityName + " " + mCurrentAreaName);
            dialog.dismiss();
        });

        mProvince.setVisibleItems(5);
        mCity.setVisibleItems(5);
        mArea.setVisibleItems(5);
        updateCities();
        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mAreaDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[]{""};
            mCurrentAreaName = "";
        } else {
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[0];
        }
        mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
        mArea.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[]{""};
        }
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mCity.setCurrentItem(0);
        updateAreas();
    }

    /**
     * 解析整个Json对象，完成后释放Json对象的内存
     */
    private void initDatas() {
        try {
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            mProvinceDatas = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象
                String province = jsonP.getString("p");// 省名字

                mProvinceDatas[i] = province;

                JSONArray jsonCs = null;
                try {
                    /**
                     * Throws JSONException if the mapping doesn't exist or is
                     * not a JSONArray.
                     */
                    jsonCs = jsonP.getJSONArray("c");
                } catch (Exception e1) {
                    continue;
                }
                String[] mCitiesDatas = new String[jsonCs.length()];
                for (int j = 0; j < jsonCs.length(); j++) {
                    JSONObject jsonCity = jsonCs.getJSONObject(j);
                    String city = jsonCity.getString("n");// 市名字
                    mCitiesDatas[j] = city;
                    JSONArray jsonAreas = null;
                    try {
                        /**
                         * Throws JSONException if the mapping doesn't exist or
                         * is not a JSONArray.
                         */
                        jsonAreas = jsonCity.getJSONArray("a");
                    } catch (Exception e) {
                        continue;
                    }

                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区
                    for (int k = 0; k < jsonAreas.length(); k++) {
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称
                        mAreasDatas[k] = area;
                    }
                    mAreaDatasMap.put(city, mAreasDatas);
                }

                mCitisDatasMap.put(province, mCitiesDatas);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mJsonObj = null;
    }

    /**
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "gbk"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void GetAddr() {
        String latitude;
        String longitude;
        Log.e("地理位置权限", PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION) + "");
        if (PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Pair<Double, Double> location = GetLocation.getLocation(mContext);
            latitude = String.valueOf(location.first);
            longitude = String.valueOf(location.second);
        } else {
            latitude = "0";
            longitude = "0";
        }

        if (latitude.equals("0.0") && longitude.equals("0.0")) {
            handler.sendEmptyMessage(7);
        }
        ExeProtocol.exe(new LocationRequest(latitude, longitude),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {


                        LocationResponse lcr = (LocationResponse) bhr;
                        StringBuffer builder = new StringBuffer();
                        builder.append(lcr.province).append(" ");
                        builder.append(lcr.locality).append(" ");
                        builder.append(lcr.subLocality);
                        cityName = builder.toString();
                        handler.sendEmptyMessage(10);
                    }

                    @Override
                    public void error() {


                    }
                });
    }

    private void createDialog(int id) {
        Dialog dialog = null;
        Builder builder = new AlertDialog.Builder(mContext);
        switch (id) {
            case GENDER_DIALOG:
                int gender = 0;
                try {
                    gender = Integer.valueOf(editUserInfo.getEdGender()) - 1;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                builder.setTitle(R.string.person_info_gender);
                builder.setSingleChoiceItems(R.array.gender, gender,
                        (dialog1, which) -> {
                            if (which == 1) {
                                editUserInfo.setEdGender("2");
                            } else if (which == 0) {
                                editUserInfo.setEdGender("1");
                            }
                            handler.sendEmptyMessage(5);
                            dialog1.dismiss();
                        });
                builder.setNegativeButton(R.string.alert_btn_cancel, null);
                dialog = builder.create();
                break;
            case OCCUPATION_DIALOG:
                builder.setTitle(R.string.person_info_occupation);
                builder.setSingleChoiceItems(R.array.occupation, 0,
                        (dialog12, which) -> {

                            if (which == 0) {
                                editUserInfo.setEdOccupation("学生");
                            } else if (which == 1) {
                                editUserInfo.setEdOccupation("上班族");
                            }
                            handler.sendEmptyMessage(5);
                            dialog12.dismiss();
                        });
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                dialog = builder.create();
                break;
            case EDUCATION_DIALOG:
                builder.setTitle(R.string.person_info_education);
                builder.setSingleChoiceItems(R.array.education, 0,
                        new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 0) {
                                    editUserInfo.setEdEducation("小学");
                                } else if (which == 1) {
                                    editUserInfo.setEdEducation("初中");
                                } else if (which == 2) {
                                    editUserInfo.setEdEducation("高中");
                                } else if (which == 3) {
                                    editUserInfo.setEdEducation("中等专业学校");
                                } else if (which == 4) {
                                    editUserInfo.setEdEducation("大学专科");
                                } else if (which == 5) {
                                    editUserInfo.setEdEducation("本科");
                                } else if (which == 6) {
                                    editUserInfo.setEdEducation("硕士");
                                } else if (which == 7) {
                                    editUserInfo.setEdEducation("博士");
                                }
                                handler.sendEmptyMessage(5);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                dialog = builder.create();
                break;
            case DATE_DIALOG:
                calendar = Calendar.getInstance();
                dialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker dp, int year,
                                                  int month, int dayOfMonth) {
                                editUserInfo.setEdBirthDay(dayOfMonth);
                                editUserInfo.setEdBirthYear(year);
                                editUserInfo.setEdBirthMonth(month + 1);
                                Calendar cal = new GregorianCalendar(year, month,
                                        dayOfMonth);
                                String constellation = JudgeZodicaAndConstellation
                                        .date2Constellation(cal);
                                String zodiac = JudgeZodicaAndConstellation
                                        .date2Zodica(cal);
                                editUserInfo.setEdZodiac(zodiac);
                                editUserInfo.setEdConstellation(constellation);
                                editUserInfo.setBirthday(year + "-" + (month + 1)
                                        + "-" + dayOfMonth);
                                handler.sendEmptyMessage(5);
                            }
                        }, calendar.get(Calendar.YEAR), // 传入年份
                        calendar.get(Calendar.MONTH), // 传入月份
                        calendar.get(Calendar.DAY_OF_MONTH) // 传入天数
                );
                dialog.setTitle(R.string.person_info_birth);
                break;
            case SCHOOL_DIALOG:
                builder.setTitle(R.string.person_info_school);
                LayoutInflater vi = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                schoolDialog = vi.inflate(R.layout.school_dialog, null);
                builder.setView(schoolDialog);
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                dialog = builder.create();
                dialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {

                        schoolListAdapter = null;
                    }
                });
                initSchoolDialog(dialog);
                break;
            case LOCATION_DIALOG:
                builder.setTitle(R.string.person_info_local_now);
                LayoutInflater vInflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                locationDialog = vInflater.inflate(R.layout.location_dialog, null);
                builder.setView(locationDialog);
                builder.setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                dialog = builder.create();
                dialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface arg0) {


                    }
                });
                initLocationDialog(dialog);
                break;
            default:
                break;
        }
        dialog.show();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {

        if (wheel == mProvince) {
            updateCities();
        } else if (wheel == mCity) {
            updateAreas();
        } else if (wheel == mArea) {
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
        }
    }

}
