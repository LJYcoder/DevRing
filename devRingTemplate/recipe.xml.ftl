<?xml version="1.0"?>
<recipe>
<#if needActivity>
    <merge from="root/AndroidManifest.xml.ftl"
           to="${escapeXmlAttribute(manifestOut)}/AndroidManifest.xml" />
</#if>

<#if needActivity && generateActivityLayout>
    <instantiate from="root/res/layout/simple.xml.ftl"
                 to="${escapeXmlAttribute(resOut)}/layout/${activityLayoutName}.xml" />
</#if>

<#if needFragment && generateFragmentLayout>
    <instantiate from="root/res/layout/simple.xml.ftl"
                 to="${escapeXmlAttribute(resOut)}/layout/${fragmentLayoutName}.xml" />
</#if>

<#if needActivity>
    <instantiate from="root/src/app_package/Activity.java.ftl"
                   to="${projectOut}/src/main/java/${slashedPackageName(ativityPackageName)}/${pageName}Activity.java" />
    <open file="${projectOut}/src/main/java/${slashedPackageName(ativityPackageName)}/${pageName}Activity.java" />
</#if>

<#if needFragment>
    <instantiate from="root/src/app_package/Fragment.java.ftl"
                   to="${projectOut}/src/main/java/${slashedPackageName(fragmentPackageName)}/${pageName}Fragment.java" />
    <open file="${projectOut}/src/main/java/${slashedPackageName(fragmentPackageName)}/${pageName}Fragment.java" />
</#if>

<#if needIView>
    <instantiate from="root/src/app_package/IView.java.ftl"
                   to="${projectOut}/src/main/java/${slashedPackageName(iviewPackageName)}/I${pageName}View.java" />
</#if>

<#if needIModel>
    <instantiate from="root/src/app_package/IModel.java.ftl"
                   to="${projectOut}/src/main/java/${slashedPackageName(imodelPackageName)}/I${pageName}Model.java" />
</#if>

<#if needPresenter>
    <instantiate from="root/src/app_package/Presenter.java.ftl"
                   to="${projectOut}/src/main/java/${slashedPackageName(presenterPackageName)}/${pageName}Presenter.java" />
    <open file="${projectOut}/src/main/java/${slashedPackageName(presenterPackageName)}/${pageName}Presenter.java" />
</#if>

<#if needModel>
    <instantiate from="root/src/app_package/Model.java.ftl"
                   to="${projectOut}/src/main/java/${slashedPackageName(modelPackageName)}/${pageName}Model.java" />
</#if>

<#if needDagger>
    <#if needDaggerActivity>
      <instantiate from="root/src/app_package/ComponentForActivity.java.ftl"
                     to="${projectOut}/src/main/java/${slashedPackageName(componentPackageNameForActivity)}/${pageName}ActivityComponent.java" />
      <instantiate from="root/src/app_package/ModuleForActivity.java.ftl"
                     to="${projectOut}/src/main/java/${slashedPackageName(moudlePackageNameForActivity)}/${pageName}ActivityModule.java" />
    </#if>

    <#if needDaggerFragment>
      <instantiate from="root/src/app_package/ComponentForFragment.java.ftl"
                     to="${projectOut}/src/main/java/${slashedPackageName(componentPackageNameForFragment)}/${pageName}FragmentComponent.java" />
      <instantiate from="root/src/app_package/ModuleForFragment.java.ftl"
                     to="${projectOut}/src/main/java/${slashedPackageName(moudlePackageNameForFragment)}/${pageName}FragmentModule.java" />
    </#if>

    <#if needDaggerOther>
      <instantiate from="root/src/app_package/ComponentForOther.java.ftl"
                     to="${projectOut}/src/main/java/${slashedPackageName(componentPackageNameForOther)}/${pageName}Component.java" />
      <instantiate from="root/src/app_package/ModuleForOther.java.ftl"
                     to="${projectOut}/src/main/java/${slashedPackageName(moudlePackageNameForOther)}/${pageName}Module.java" />
    </#if>
</#if>

</recipe>
