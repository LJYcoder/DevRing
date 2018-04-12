package ${componentPackageNameForFragment};

import com.ljy.devring.di.scope.FragmentScope;
import dagger.Component;

import ${moudlePackageNameForFragment}.${pageName}Module;

<#if needActivity && needFragment>
import ${ativityPackageName}.${pageName}Activity;
import ${fragmentPackageName}.${pageName}Fragment;
<#elseif needActivity>
import ${ativityPackageName}.${pageName}Activity;   
<#elseif needFragment>
import ${fragmentPackageName}.${pageName}Fragment;
</#if>


@FragmentScope
@Component(modules = ${pageName}FragmentModule.class)
public interface ${pageName}FragmentComponent {
  <#if needActivity && needFragment>
	void inject(${pageName}Activity activity);
	void inject(${pageName}Fragment fragment);
  <#elseif needActivity || needFragment>
    void inject(<#if needFragment>${pageName}Fragment fragment<#else>${pageName}Activity activity</#if>);
  </#if>
}