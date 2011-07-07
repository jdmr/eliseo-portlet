<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "examen.ver.titulo",request.getAttribute("examen"),false) %></h1>
    <portlet:renderURL var="verCurso" >
        <portlet:param name="action" value="ver" />
        <portlet:param name="cursoId" value="${examen.curso.id}" />
    </portlet:renderURL>
    <portlet:renderURL var="editaExamen" >
        <portlet:param name="action" value="editaExamen" />
        <portlet:param name="examenId" value="${examen.id}" />
    </portlet:renderURL>
    <portlet:actionURL var="eliminaExamen" >
        <portlet:param name="action" value="eliminaExamen" />
        <portlet:param name="examenId" value="${examen.id}" />
    </portlet:actionURL>

    <div class="dialog">
        <table>
            <tbody>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="codigo"><liferay-ui:message key="examen.codigo" /></label>
                    </td>
                    <td valign="top" class="value">${examen.codigo}</td>
                </tr>

                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="nombre"><liferay-ui:message key="examen.nombre" /></label>
                    </td>
                    <td valign="top" class="value">${examen.nombre}</td>
                </tr>


            </tbody>
        </table>
    </div>
    <div class="nav">
        <span class="menuButton"><a class="edit" href="${editaExamen}"><liferay-ui:message key="examen.edita" /></a></span>
        <span class="menuButton"><a class="delete" href="${eliminaExamen}" onclick="return confirm('<%= LanguageUtil.format(pageContext, "examen.elimina.confirmacion",request.getAttribute("examen"),false) %>');"><liferay-ui:message key="examen.elimina" /></a></span>
        <span class="menuButton"><a class="edit" href="${verCurso}"><liferay-ui:message key="curso.ver" /></a></span>
    </div>
</div>

