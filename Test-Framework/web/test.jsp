<%-- 
    Document   : test
    Created on : 28 mars 2023, 13:32:04
    Author     : aris
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
        
        <%= 
           ((double) request.getAttribute("huhu"))
        %>
    </body>
</html>
