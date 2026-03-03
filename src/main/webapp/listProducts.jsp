<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title><%= request.getAttribute("title") %></title>
</head>
<body>
<h1><%= request.getAttribute("title") %></h1>

<ul>
<%
    String[] products = (String[]) request.getAttribute("products");
    if (products != null) {
        for (String p : products) {
%>
        <li><%= p %></li>
<%
        }
    }
%>
</ul>
</body>
</html>
