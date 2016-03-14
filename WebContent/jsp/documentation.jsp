<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Documentation</title>
</head>
<body>
<a href="../index.jsp">Home </a>
<pre>
<h2>

1. First DropDown is for base environment it fetches all NIB environment from Zookeeper dynamically and Client is statically added.

2. Second DropDown is for target environment dynamically fetches all NIB environment from Zookeeper.

3. Third Radio Button is for Platform Novus or Prism.

4. Forth Text Field is for Database Resource helps in case of multiple database in client environment.
 
5. Fifth Field is Database Schema most required field.

6. GenerateChangeLog Button will generate the change log file on local file system where the tool is running(currently getting path from Constant.CHANGELOG_FILE_DIR which is /tmp/ ). we can see the all changelog in History.
its recommended to keep minimum files in history.

7. History Button will list out the previously generated change log files available on the file system. We can download the file by clicking on it. We can Delete the file by Delete Button on rightmost.

 

8. This Tool Assumes target always would be NIB hosted on single Oracle Database.

9. Resource Field is optional if base environment a NIB environment (tool assumes all schemas are on single database so it go ahead with the schema name only)

10. Giving Wrong schema would not stop Liquibase to compare it will compare and will generate empty change log file. Liquibase has no way to identify whether schema is available or not.

11. Do grants access after applying the changes.

</h2>
</pre>




</body>
</html>