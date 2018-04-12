package ${moudlePackageNameForActivity};

import com.ljy.devring.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import ${presenterPackageName}.${pageName}Presenter;
import ${iviewPackageName}.I${pageName}View;
import ${imodelPackageName}.I${pageName}Model;
import ${modelPackageName}.${pageName}Model;


@Module
public class ${pageName}ActivityModule {
    private I${pageName}View mIView;

    public ${pageName}ActivityModule(I${pageName}View iView) {
        mIView = iView;
    }

    @ActivityScope
    @Provides
    I${pageName}View i${pageName}View(){
        return mIView;
    }

    @ActivityScope
    @Provides
    I${pageName}Model i${pageName}Model(){
        return new ${pageName}Model();
    }

    @ActivityScope
    @Provides
    ${pageName}Presenter provide${pageName}Presenter(I${pageName}View iView, I${pageName}Model iModel) {
        return new ${pageName}Presenter(iView, iModel);
    }
}