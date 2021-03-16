package in.alertmeu.a4b.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import in.alertmeu.a4b.R;
import in.alertmeu.a4b.activity.BusinessProfileSettingActivity;
import in.alertmeu.a4b.activity.HomePageActivity;
import in.alertmeu.a4b.activity.ScanActivity;
import in.alertmeu.a4b.utils.AppStatus;


public class TabsFragmentActivity extends FragmentActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor prefEditor;
    private FragmentTabHost mTabHost;
    Button btnActive, btninActive, newAds;
    TextView title;
    ImageView btnHome, btnscanbar;
    private ImageView naviBtn;
    ArrayList<String> mainCatArrayList;
    ArrayList<String> subCatArrayList;
    LinearLayout btnBusinessListC;
    Resources res;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_fragment);
        res = getResources();
        naviBtn = (ImageView) findViewById(R.id.naviBtn);
        title = (TextView) findViewById(R.id.title);
        btnActive = (Button) findViewById(R.id.btnActive);
        btninActive = (Button) findViewById(R.id.btninActive);
        newAds = (Button) findViewById(R.id.newAds);
        btnHome = (ImageView) findViewById(R.id.btnHome);
        btnscanbar = (ImageView) findViewById(R.id.btnscanbar);
        btnBusinessListC=(LinearLayout)findViewById(R.id.btnBusinessListC);
        btnBusinessListC.setBackgroundColor(Color.parseColor("#809E9E9E"));
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        preferences = getSharedPreferences("Prefrence", MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.putInt("n_count", 0);
        prefEditor.commit();
        setBadge();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        if (args != null) {
            mainCatArrayList = (ArrayList<String>) args.getSerializable("mainCat");
            subCatArrayList = (ArrayList<String>) args.getSerializable("subCat");
        }

        Bundle args1 = new Bundle();
        args1.putSerializable("mainCat", (Serializable) mainCatArrayList);
        args1.putSerializable("subCat", (Serializable) subCatArrayList);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator(res.getString(R.string.homePosActAds), null),
                ActiveAdsFragment.class, null);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator(res.getString(R.string.homePosInActAds), null),
                InActiveAdsFragment.class, null);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab3").setIndicator(res.getString(R.string.homePosAddAds), null),
                AddNewAdsFragment.class, args1);

        mTabHost.setCurrentTab(intent.getIntExtra("active", 0));
        mTabHost.setSelected(true);

        String tabs = mTabHost.getCurrentTabTag();
        if (tabs.equals("tab1")) {
           // title.setText(R.string.homePosActAds);
            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF")); //unselected
                TextView tv =  (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setAllCaps(false);
            }
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#D9D9D9")); // selected

        }
        if (tabs.equals("tab2")) {
          //  title.setText(R.string.homePosInActAds);
            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF")); //unselected
                TextView tv =  (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setAllCaps(false);
            }
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#D9D9D9")); // selected

        }
        if (tabs.equals("tab3")) {
         //   title.setText(R.string.homePosAddAds);
            for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF")); //unselected
                TextView tv =  (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                tv.setAllCaps(false);
            }
            mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#D9D9D9")); // selected

        }

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                String tabs = tabId;
               /* if (tabs.equals("tab1")) {
                    title.setText(R.string.homePosActAds);
                }
                if (tabs.equals("tab2")) {
                    title.setText(R.string.homePosInActAds);

                }
                if (tabs.equals("tab3")) {
                    title.setText(R.string.homePosAddAds);

                }*/
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#FFFFFF")); //unselected
                }
                mTabHost.getTabWidget().getChildAt(mTabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#D9D9D9")); // selected
            }
        });


        naviBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    Intent intent = new Intent(TabsFragmentActivity.this, BusinessProfileSettingActivity.class);
                    startActivity(intent);


                } else {

                    Toast.makeText(getApplicationContext(), res.getString(R.string.jpcnc), Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TabsFragmentActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        });
        btnscanbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TabsFragmentActivity.this, ScanActivity.class);
                startActivity(intent);
            }
        });
    }
//count show on app icon

    public void setBadge() {
        String launcherClassName = getLauncherClassName(getApplicationContext());
        if (launcherClassName == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", 0);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", launcherClassName);
        sendBroadcast(intent);
    }

    public String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }
}
