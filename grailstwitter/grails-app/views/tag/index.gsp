<h3>Tags</h3>
<div id="tagsSpinner" style="display:none;">
    <img src="${resource(dir:'images',file:'spinner.gif')}" alt="code:'spinner.alt',default:'Loading...')}" />
</div>
<ul>
<g:each in="${ tags }" var="tag">
  <li><div><g:link controller="status" action="tag" id="${tag.key}">${ tag.key }</g:link></div><div class="tagCount">${ tag.value }</div></li>
</g:each>
</ul>
