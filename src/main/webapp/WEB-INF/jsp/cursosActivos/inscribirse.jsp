<%-- 
    Document   : inscribirse
    Created on : Apr 18, 2011, 5:19:29 PM
    Author     : jdmr
--%>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    ${contenido}
    
    <div class="nav">
        <portlet:renderURL var="contenidoUrl" >
            <portlet:param name="action" value="ver" />
            <portlet:param name="salonId" value="${salon.id}" />
        </portlet:renderURL>
        <span class="menuButton"><a href="${contenidoUrl}">&laquo; <liferay-ui:message key="back" /></a></span>
    </div>
</div>
