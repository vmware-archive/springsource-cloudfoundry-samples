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
            <div id="customerResults">
                <h2>Customer List:</h2>
                <div id='new-company-link'>
                    <a href="customer/new" title="Add a new customer">Add a new customer</a>
                </div>
                <hr/>
                <table>
                    <thead>
                    <tr>
                        <th>FirstName</th>
                        <th>LastName</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:if test="${not empty customerList}">
                        <c:forEach var="customer" items="${customerList}">
                            <tr>
                                <td>${customer.firstName}</td>
                                <td>${customer.lastName}</td>
                                <td><a href="customer/${customer.id}" title="View customer">View Customer</a></td>
                                <td><a href="customerDelete/${customer.id}" title="Delete customer">Delete Customer</a></td>
                            </tr>
                        </c:forEach>
                    </c:if>
                    <c:if test="${empty customerList}">
                        <tr>
                            <td colspan="2">No Customers found</td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
            <div>
                <hr/>
                <a href="index">Home</a>
            </div>
        </div>
    </div>
</div>
<DIV class = "cloudEnvironment">
    ${host}:${port}
</DIV>

</body>
</html>
