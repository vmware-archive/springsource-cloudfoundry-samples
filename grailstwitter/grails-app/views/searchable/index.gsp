<html>
<head>
    <meta name="layout" content="main"/>
</head>
<body>
        <g:each var="person" in="${searchResult?.results}">
        <div id="name">
            ${person.realName} <g:link id="${person.id}" action="follow" controller="status">follow</g:link>
            </div>
        </g:each>
</body>
</html>