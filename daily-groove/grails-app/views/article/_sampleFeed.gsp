<g:if test="${sampleFeed}">
    <g:form name="sampleSubscribeForm" controller="article" action="addFeed">
    	<a href="${sampleFeed.url}" onclick="document.getElementById('sampleSubscribeForm').submit(); return false;">${sampleFeed.name}</a>
    	<input type="hidden" name="url" value="${sampleFeed.url}">
    </g:form>
</g:if>
