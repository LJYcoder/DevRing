package ${componentPackageNameForActivity};

import com.ljy.devring.di.scope.ActivityScope;
import dagger.Component;
import ${moudlePackageNameForActivity}.${pageName}Module;

<#if needActivity && needFragment>
import ${ativityPackageName}.${pageName}Activity;
import ${fragmentPackageName}.${pageName}Fragment;
<#elseif needActivity>
import ${ativityPackageName}.${pageName}Activity;   
<#elseif needFragment>
import ${fragmentPackageName}.${pageName}Fragment;
</#if>


@ActivityScope
@Component(modules = ${pageName}ActivityModule.class)
public interface ${pageName}ActivityComponent {
  <#if needActivity && needFragment>
	void inject(${pageName}Activity activity);
	void inject(${pageName}Fragment fragment);
  <#elseif needActivity || needFragment>
    void inject(<#if needFragment>${pageName}Fragment fragment<#else>${pageName}Activity activity</#if>);
  </#if>
}