package ${fragmentPackageName};

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import ${fragmentPackageName}.base.BaseFragment;
import ${iviewPackageName}.I${pageName}View;
import ${presenterPackageName}.${pageName}Presenter;
import ${packageName}.R;



public class ${pageName}Fragment extends BaseFragment<${pageName}Presenter> implements I${pageName}View{

    @Override
    protected boolean isLazyLoad() {
        return true;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.${fragmentLayoutName};
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {

    }
  
}
