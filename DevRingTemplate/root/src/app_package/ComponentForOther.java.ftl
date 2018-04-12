package ${componentPackageNameForOther};

import dagger.Component;
import ${moudlePackageNameForOther}.${pageName}Module;

<#if needActivity && needFragment>
import ${ativityPackageName}.${pageName}Activity;
import ${fragmentPackageName}.${pageName}Fragment;
<#elseif needActivity>
import ${ativityPackageName}.${pageName}Activity;   
<#elseif needFragment>
import ${fragmentPackageName}.${pageName}Fragment;
</#if>


@Component(modules = ${pageName}Module.class)
public interface ${pageName}Component {
  <#if needActivity && needFragment>
	void inject(${pageName}Activity activity);
	void inject(${pageName}Fragment fragment);
  <#elseif needActivity || needFragment>
    void inject(<#if needFragment>${pageName}Fragment fragment<#else>${pageName}Activity activity</#if>);
  </#if>
}