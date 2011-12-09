<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Spring Data MongoDB/MySQL Cross-Store</title>
    <link rel="stylesheet" href="resources/css/main.css" type="text/css"></link>
    <link rel="stylesheet" href="resources/css/colors.css" type="text/css"></link>
    <link rel="stylesheet" href="resources/css/local.css" type="text/css"></link>
</head>
<body>
<div id="page">
    <div id="header">
        <div id="name-and-company">
            <div id='site-name'>
                <a href="index" title="Spring Data Cross-Store MySQL/MongoDB">Spring Data Cross-Store MySQL/MongoDB</a>
            </div>
            <div id='company-name'>
                <a href="http://www.springsource.com" title="SpringSource">SpringSource Home</a>
            </div>
        </div>
        <!-- /name-and-company -->
    </div>
    <!-- /header -->
    <div id="container">
        <div id="content" class="no-side-nav">
            <h1>Database Info</h1>
            <h4>SQL: <c:out value="${sqlinfo}"/></h4>
            <p>
                <c:out value="${sqldata}"/>
            </p>
            <h4>Mongo: <c:out value="${mongoinfo}"/></h4>
            <p>
                <c:out value="${mongodata}"/>
            </p>
            <a href="index">Home</a>
        </div>
    </div>
</div>

<DIV class = "cloudEnvironment">
    ${host}:${port}
</DIV>



</body>
</html>
