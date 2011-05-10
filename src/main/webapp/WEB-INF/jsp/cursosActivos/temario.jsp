<%-- 
    Document   : verContenido
    Created on : May 9, 2011, 2:17:44 PM
    Author     : jdmr
--%>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <c:choose>
        <c:when test="${contenidos != null}" >
            <div class="list">
                <table id="<portlet:namespace />contenidos">
                    <tbody>
                        <c:forEach items="${contenidos}" var="contenido">
                            <portlet:renderURL var="verContenido" >
                                <portlet:param name="action" value="verContenido" />
                                <portlet:param name="salonId" value="${salon.id}" />
                                <portlet:param name="contenidoId" value="${contenido.entryId}" />
                            </portlet:renderURL>
                            <tr>
                                <td><a href="${verContenido}">${contenido.title}</a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <script type="text/javascript">
                highlightTableRows("<portlet:namespace />contenidos")
            </script>
        </c:when>
        <c:otherwise>
            <p><liferay-ui:message key="cursosActivos.vacio" /></p>
        </c:otherwise>
    </c:choose>
</div>