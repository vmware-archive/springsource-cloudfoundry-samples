<h1>What Are You Doing?</h1>
<div class="updateStatusForm">
    <g:form action="updateStatus" name="updateStatusForm">
        <g:textArea name="message" value=""/><br/>
        <g:submitButton name="Update Status"/>
     <span id="spinner" style="display:none;">
         <img src="${resource(dir:'images',file:'spinner.gif')}" alt="code:'spinner.alt',default:'Loading...')}" />
     </span>
    </g:form>
</div>
<div id="messages">
    <g:render template="statusMessages" collection="${statusMessages}" var="statusMessage"/>
</div>
<div>
    <a id="moreLink" href="${createLink(action: 'getMessagesForUser')}">More...</a>
    <span id="moreSpinner" style="display:none;">
        <img src="${resource(dir:'images',file:'spinner.gif')}" alt="code:'spinner.alt',default:'Loading...')}" />
    </span>
</div>
