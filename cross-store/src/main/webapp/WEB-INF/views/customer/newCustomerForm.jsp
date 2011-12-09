<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Spring Data MongoDB/MySQL Cross-Store</title>
    <link rel="stylesheet" href="../resources/css/main.css" type="text/css"></link>
    <link rel="stylesheet" href="../resources/css/colors.css" type="text/css"></link>
    <link rel="stylesheet" href="../resources/css/local.css" type="text/css"></link>
</head>
<body>
<div id="page">
    <div id="header">
        <div id="name-and-company">
            <div id='site-name'>
                <a href="../index" title="Spring Data Cross-Store MySQL/MongoDB">Spring Data Cross-Store MySQL/MongoDB</a>
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
            <div id="customerForm">
                <div>
                    <h3>New Customer</h3>
                </div>
                <div>
                    <spring:hasBindErrors name="customer">
                        <div class="error">
                            <spring:bind path="customer.*">
                                <c:forEach items="${status.errorMessages}" var="error">
                                    <c:out value="${error}"/><br/>
                                </c:forEach>
                            </spring:bind>
                        </div>
                    </spring:hasBindErrors>
                    <form:form modelAttribute="customer">
                        <fieldset>
                            <legend>Add new Customer</legend>
                            <div>
                                <div>
                                    <label for="firstName">First Name</label>
                                </div>
                                <div>
                                    <p><form:input path="firstName"/></p>
                                </div>
                            </div>
                            <div>
                                <div>
                                    <label for="lastName">Last Name</label>
                                </div>
                                <div>
                                    <p><form:input path="lastName"/></p>
                                </div>
                            </div>
                            <div>
                                <p>
                                    <button type="submit" id="submit">Submit</button>
                                </p>
                            </div>
                        </fieldset>
                    </form:form>
                </div>
            </div>
            <div>
                <hr/>
                <a href="../index">Home</a> <a href="../customer">Customer List</a>
            </div>
        </div>
    </div>
</div>
<DIV class = "cloudEnvironment">
    ${host}:${port}
</DIV>

</body>
</html>
