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
                    <thead>
                        <tr>
                            <th><liferay-ui:message key="cursosActivos.tema" /></th>
                            <th style="width: 90px;text-align: center;"><liferay-ui:message key="cursosActivos.tipoContenido" /></th>
                            <th style="width: 150px;text-align: center;"><liferay-ui:message key="cursosActivos.ultimaFecha" /></th>
                            <th style="width: 70px;text-align: center;"><liferay-ui:message key="cursosActivos.estatus" /></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${contenidos}" var="contenido" varStatus="status">
                            <portlet:renderURL var="verContenido" >
                                <portlet:param name="action" value="verContenido" />
                                <portlet:param name="salonId" value="${salon.id}" />
                                <portlet:param name="contenidoId" value="${contenido.id}" />
                            </portlet:renderURL>
                            <tr class="${(status.count % 2) == 0 ? 'odd' : 'even'}">
                                <td><a href="${verContenido}">${contenido.nombre}</a></td>
                                <td style="text-align: center;">${contenido.tipo}</td>
                                <td style="text-align: center;">${contenido.fecha}</td>
                                <td style="text-align: center;">${contenido.estatus}</td>
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