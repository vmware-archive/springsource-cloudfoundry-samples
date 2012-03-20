<g:applyLayout name="main">
<head>
    <r:require modules="prototype" />
    <g:layoutHead/>
</head>
<body>
    <div class="span-13">
        <g:layoutBody/>
    </div>
    <div class="span-1">&nbsp;</div>
    <div class="span-6 last">
        <div id="following">
            <h3>Following</h3>
            <ul>
                <g:each in="${ currentUser.followed }" var="person">
                    <li>${ person.realName }
                </g:each>
            </ul>
            <div class="searchForm">
                Search for users
                <g:form controller="person" action="search" method="get">
                    <g:textField name="q" value=""/>
                </g:form>
                <p style="font-size: 0.8em;">(use * to search for all users)</p>
            </div>
        </div>
        <div id="tagList">
            <g:include controller="tag"/>
        </div>
    </div>
<r:script>
document.observe("dom:loaded", function() {
	var maxMessages = 30;
    $('moreLink').observe('click', function(event) {
        new Ajax.Updater(
                'messages',
                '${createLink(action: moreMessagesAction, id: moreMessagesId)}',
                {asynchronous:true,
                    parameters:'max=' + maxMessages,
                    onLoading: function() { $('moreSpinner').show(); },
                    onComplete: function(resp) { $('moreSpinner').hide(); }});
        maxMessages += 10;
        event.stop();
    });

    $('updateStatusForm').observe('submit', function(event) {
        new Ajax.Updater(
    	        'messages',
    	        '${createLink(action: 'updateStatus')}',
    	        {asynchronous:true,
        	        onLoading: function() { $('spinner').show(); },
        	        onComplete: function(response) {
            	        if (response.request.success()) {
            	        	document.updateStatusForm.message.value='';
        	            }
        	            $('spinner').hide();
           	        },
           	        parameters:Form.serialize(this)});
       event.stop();
    });

    new Ajax.PeriodicalUpdater(
            {success: 'tagList'},
            '${createLink(controller: "tag", action: "index")}',
            {asynchronous:true, frequency: 5, onFailure: function() { this.stop(); }});
});
</r:script>
</body>
</g:applyLayout>
