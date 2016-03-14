<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.zookeeper.ZkUtilsImpl"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.SortedSet"%>
<%@page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Welcome To Schema Updater Tool</title>
<%
	String strCtx = request.getContextPath();
%>
</head>
<script language="javascript" type="text/javascript"
	src="js/jquery-1.3.2.min.js"></script>
<script language="javascript" type="text/javascript"
	src="js/jquery-ui-1.7.2.custom.min.js"></script>
<script>
	function getSchemas() {
		try{
		var nib = document.getElementById("baseNib").value;
		var xmlhttp;	
		if (window.XMLHttpRequest) {
			// code for IE7+, Firefox, Chrome, Opera, Safari
			xmlhttp =new XMLHttpRequest();
		} else if (window.ActiveXObject) {
			// code for IE6, IE5
			xmlhttp =new ActiveXObject("Microsoft.XMLHTTP");
		} 
		xmlhttp.open("GET", "<%=strCtx%>/listschemas?time=" + (new Date).getTime()
				+ "&env=" + nib, true);
		xmlhttp.send();
		var htmlBuilder = "<SELECT ID=\"schema\" name=\"schema\" >";
		xmlhttp.onreadystatechange = function() {
			if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
				var ajaxResponse = xmlhttp.responseText; // response from the ajax call	
				if (ajaxResponse == null || ajaxResponse == ""
						|| ajaxResponse == "[]"
						|| typeof ajaxResponse == undefined) {
					document.getElementById("schema").outerHTML = "<input type=\"text\" name=\"schema\" ID=\"schema\"></input>";	
					return;
				} else {
					if (ajaxResponse.indexOf(",") != -1) {
						var schemas = ajaxResponse.substr(1).split(",");
						{
							var len = schemas.length;
							for (var i = 0; i < len - 1; i++) {
								htmlBuilder = htmlBuilder
										+ "<OPTION value=\"" + schemas[i]
								+ "\">"
										+ schemas[i] + "</OPTION>";
							}
							htmlBuilder = htmlBuilder + "<OPTION value=\""
									+ schemas[len - 1].split("]")[0] + "\">"
									+ schemas[len - 1].split("]")[0]
									+ "</OPTION>";
						}
					} else {
						htmlBuilder = htmlBuilder + "<OPTION value=\""
								+ schemas.substring(1, schemas.length) + "\">"
								+ schemas.substring(1, schemas.length)
								+ "</OPTION>";
					}

					htmlBuilder = htmlBuilder + "</SELECT>";
					document.getElementById("schema").outerHTML = htmlBuilder;
				}
			}
			else if(xmlhttp.readyState == 4 && xmlhttp.status == 1000){
			alert("Exception while fetching the schemas list from Zookeper or base Environment do not have schemas in Zookeeper node");	
			}
		};
		}catch(err){alert(err)}
		}
	
	
	
	function controlAjax(){
		var base = document.getElementById("baseNib").value;
		if(base.toLowerCase().indexOf("nib") > -1){
			getSchemas();
		}else{
			document.getElementById("schema").outerHTML = "<input type=\"text\" name=\"schema\" ID=\"schema\"></input>";
		}
		
	}
</script>

<body>

<div id="page" style='margin: 0% 0% 0% 10%;' >
	<form name="main" action="<%=strCtx%>/history?msg=Looks Like This Browser is not supported by this Tool" method="get">
	
		<table><tr><td><a href="index.jsp">Home </a></td><td></td><td></td><td><a href="jsp/documentation.jsp">Documentation </a></td></tr></table>
			<div id="table" style='background-color: yellow; border: 4px solid black; height: 70%;width: 700px; table-layout: fixed;margin: 5% 20% 5% 10%; padding: 4% 2% 3% 4%;'>
				<table>
					<tr>
						<td>Base Environment Name *:</td>
						<td><select id="baseNib" name="baseNib" size="1"
							onchange="controlAjax();">
								<option value="dummy">-Select-</option>
								<option value="client">Client</option>
								<%
									SortedSet nibList = new TreeSet(ZkUtilsImpl.getInstance().listAllNIBS());
									Iterator nibItr = nibList.iterator();
									while (nibItr.hasNext()) {
										String nib = (String) nibItr.next();
										out.println("<option value=\"" + nib + "\">" + nib
												+ "</option>");
									}
								%>
						</select></td>
					</tr>
					<tr>
						<td>Target Nib Name *:</td>
						<td><select id="targetNib" name="targetNib" size="1">
								<option value="dummy">-Select-</option>
								<%
									Iterator nibItr2 = nibList.iterator();
									while (nibItr2.hasNext()) {
										String nib2 = (String) nibItr2.next();
										out.println("<option value=\"" + nib2 + "\">" + nib2
												+ "</option>");
									}
								%>
						</select></td>
					</tr>
					<tr></tr>
					<tr></tr>
					<tr>
						<td>Platform:</td>
						<td><input type="radio" value="novus" name="platform"
							checked="checked">Novus<input type="radio" value="prism"
							name="platform">Prism</td>
					</tr>
					<tr>
						<td>Database Resource</td>
						<td><input type="text" name="resource" ID="resource" ></input></td>
					</tr>
					<tr>
						<td>Database Schema *</td>
						<td><input type="text" name="schema" ID="schema"
							></input></td>
					</tr>
					<tr></tr>
					<tr><td>E Mail:</td><td><input type="text" name="email" ID="email"></input></td></tr>
					<tr><td>You will receive e-mail shortly </td><td>on completion of Request</td></tr>
					<tr>

						<td style='padding: 8% 0% 0% 0%'><input type="submit"
							name="action" value="GenerateChangeLog" onclick="form.action='generatechangelog'"></td>
						<td></td>
						<td style='padding: 8% 0% 0% 0%'><input type="submit"
							name="action" value="History" onclick="form.action='history'"></td>
					</tr>
				</table>
			</div>
	</form>
	<h5 style='color: black'>
		<%=(null != request.getAttribute("msg")) ? request
					.getAttribute("msg") : ""%></h5>
	</div>
</body>
</html>