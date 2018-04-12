package ${moudlePackageNameForFragment};

import com.ljy.devring.di.scope.FragmentScope;

import dagger.Module;
import dagger.Provides;

import ${iviewPackageName}.I${pageName}View;
import ${imodelPackageName}.I${pageName}Model;
import ${modelPackageName}.${pageName}Model;


@Module
public class ${pageName}FragmentModule {
    private I${pageName}View mIView;

    public ${pageName}FragmentModule(I${pageName}View iView) {
        mIView = iView;
    }

    @FragmentScope
    @Provides
    I${pageName}View i${pageName}View(){
        return mIView;
    }

    @FragmentScope
    @Provides
    I${pageName}Model i${pageName}Model(){
        return new ${pageName}Model();
    }

    @FragmentScope
    @Provides
    ${pageName}Presenter provide${pageName}Presenter(I${pageName}View iView, I${pageName}Model iModel) {
        return new ${pageName}Presenter(iView, iModel);
    }
}