<%-- 
    Document   : test
    Created on : 28 mars 2023, 13:32:04
    Author     : aris
--%>

<%@page import="java.util.Arrays"%>
<%@page import="java.util.Date"%>
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
        
        <%= 
           ((String) request.getAttribute("nom"))
        %>
        
        <%= 
           ((Date) request.getAttribute("date"))
        %>
        
        <%= 
            Arrays.toString((String[]) request.getAttribute("haha"))
        %>
    </body>
</html>
