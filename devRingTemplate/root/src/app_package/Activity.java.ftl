package ${ativityPackageName};

import android.os.Bundle;

import ${ativityPackageName}.base.BaseActivity;

import ${iviewPackageName}.I${pageName}View;
import ${presenterPackageName}.${pageName}Presenter;

import ${packageName}.R;


public class ${pageName}Activity extends BaseActivity<${pageName}Presenter> implements I${pageName}View {

    @Override
    protected int getContentLayout() {
        return R.layout.${activityLayoutName};
    }

    @Override
    protected void initView(Bundle bundle) {
        
    }

    @Override
    protected void initData(Bundle bundle) {

    }

    @Override
    protected void initEvent() {

    }

}
