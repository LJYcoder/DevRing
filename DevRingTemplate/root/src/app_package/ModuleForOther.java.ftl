package ${moudlePackageNameForOther};

import dagger.Module;
import dagger.Provides;

import ${iviewPackageName}.I${pageName}View;
import ${imodelPackageName}.I${pageName}Model;
import ${modelPackageName}.${pageName}Model;

@Module
public class ${pageName}Module {
    private I${pageName}View mIView;

    public ${pageName}Module(I${pageName}View iView) {
        mIView = iView;
    }

    @Provides
    I${pageName}View i${pageName}View(){
        return mIView;
    }

    @Provides
    I${pageName}Model i${pageName}Model(){
        return new ${pageName}Model();
    }

    @Provides
    ${pageName}Presenter provide${pageName}Presenter(I${pageName}View iView, I${pageName}Model iModel) {
        return new ${pageName}Presenter(iView, iModel);
    }
}