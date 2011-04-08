<#import "/spring.ftl" as spring />
<#assign url><@spring.url relativeUrl="${servletPath}/env.json"/></#assign>
"environment" : {
      "resource" : "${baseUrl}${url}",
      "system" : {
<#if system?? && system?size!=0>
	<#list system?keys as key>
			"${key}" : "${system[key]}"<#if key_index != system?size-1>,</#if>
	</#list>	
</#if>
      },
      "host" : {
<#if host?? && host?size!=0>
	<#list host?keys as key>
			"${key}" : "${host[key]}"<#if key_index != host?size-1>,</#if>
	</#list>	
</#if>
      }
   }