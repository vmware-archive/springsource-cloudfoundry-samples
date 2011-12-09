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
            <h1>
                Welcome to a very simple demo of the cross-store features
                provided by the <a href="http://www.springsource.org/spring-data/mongodb">Spring Data Document MongoDB</a> project
            </h1>
            <h2>Start by browsing the <a href="customer">Customer List</a></h2>
            <p>The customer data is stored in a MySQL database and the survey data is stored using MongoDB. This is all
                accomplished by using the "cross-store" feature.</p>
            <p>You can see the data stored in the databases <a href="dump">here</a></p>
            <p>Here is the code used in the Customer class. Take a look at the surveyInfo field. It is annotated with
                @RelatedDocument so it will be stored in a MongoDB collection named after the entity class.
            </p>
					<pre>
@Entity
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String firstName;
	
	private String lastName;
	
	@RelatedDocument
	private SurveyInfo surveyInfo;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getId() {
		return id;
	}

	public SurveyInfo getSurveyInfo() {
		if (surveyInfo == null) {
			surveyInfo = new SurveyInfo();
		}
		return surveyInfo;
	}
	 
	public void setSurveyInfo(SurveyInfo surveyInfo) {
		this.surveyInfo = surveyInfo;
	}

}
					</pre>
        </div>
    </div>
</div>
<DIV class = "cloudEnvironment">
    ${host}:${port}
</DIV>

</body>
</html>
