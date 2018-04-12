package ${presenterPackageName};

import ${presenterPackageName}.base.BasePresenter;
import ${iviewPackageName}.I${pageName}View;
import ${imodelPackageName}.I${pageName}Model;

public class ${pageName}Presenter extends BasePresenter<I${pageName}View, I${pageName}Model> {

    public ${pageName}Presenter (I${pageName}View iView, I${pageName}Model iModel) {
        super(iView, iModel);
    }

    
}
