<div style="margin-bottom: 1em;"><a href="${resource(dir: '/')}">< Your timeline</a></div>
<h2>Messages tagged with '${tag}'</h2>
<div id="messages">
    <g:render template="statusMessages" collection="${statusMessages}" var="statusMessage"/>
</div>
<div>
    <a id="moreLink" href="${createLink(action: 'getMessagesForTag', id: tag)}">More...</a>
    <span id="moreSpinner" style="display:none;">
        <img src="${resource(dir:'images',file:'spinner.gif')}" alt="code:'spinner.alt',default:'Loading...')}" />
    </span>
</div>
