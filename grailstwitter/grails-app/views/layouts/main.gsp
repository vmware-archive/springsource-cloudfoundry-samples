<html>
    <head>
        <title><g:layoutTitle default="Grails Twitter" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="stylesheet" href="${resource(dir:'css',file:'grailstwitter.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <r:require modules="blueprint"/>
        <g:layoutHead />
        <r:layoutResources/>
    </head>
    <body>

        <div class="container">
            <div class="span-2">&nbsp;</div>
            <div id="grailsLogo" class="span-6">
                <a href="http://grails.org"><img src="${resource(dir:'images',file:'grails_logo.png')}" alt="Grails" border="0" /></a>
            </div>
            <div class="span-8">
                <g:form url='[controller: "searchable", action: "index"]' id="searchableForm" name="searchableForm" method="get">
                    <g:textField name="q" value="${params.q}"/> <input type="submit" value="Search" />
                </g:form>
            </div>
            
            <sec:ifLoggedIn>
                <div id="userInfoBox" class="span-6">
                    <strong>Welcome <sec:username/></strong><br/>
                    <g:link controller="logout">Log out</g:link><br/>
                </div>
                <div class="span-2 last">&nbsp;</div>
            </sec:ifLoggedIn>
            <sec:ifNotLoggedIn>
                <div class="span-8 last">&nbsp;</div>
            </sec:ifNotLoggedIn>
            <hr class="span-24"/>
            <div class="span-2">&nbsp;</div>
            <div class="span-20">
                <g:layoutBody />
            </div>
            <div class="span-2 last">&nbsp;</div>
        </div>

        <r:layoutResources/>
    </body>
</html>
