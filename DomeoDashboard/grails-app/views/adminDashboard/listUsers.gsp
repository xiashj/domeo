<!DOCTYPE html>
<%-- by Paolo Ciccarese --%>

<html>
  <head>
    <meta name="layout" content="dashboard-admin" />
	<title>:: Users <g:if test="${role!=null}">(with Role: ${role.label})</g:if> List - total# ${usersTotal}</title>
  </head>
  
  <body>
	<g:render template="/adminDashboard/listUsers" />
  </body>
</html>