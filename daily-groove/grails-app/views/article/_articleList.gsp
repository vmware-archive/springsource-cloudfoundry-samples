<div class="articleList">

	<g:each in="${articles}" var="article">
		<g:set var="onSuccess"><g:if test="${action=='read'}">$('${action?:'show'}${article.id}').hide()</g:if><g:else></g:else></g:set>	
		<div><g:remoteLink url="[action:action ?: 'show', id:article.id]" 
			 			   id="${(action?:'show') + article.id}" 
						   onSuccess="${onSuccess}"
						   update="contentPane">${article.title}</g:remoteLink></div>
	</g:each>
	
</div>