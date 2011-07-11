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
    <portlet:renderURL var="nuevaPregunta" >
        <portlet:param name="action" value="nuevaPregunta" />
        <portlet:param name="examenId" value="${examen.id}" />
    </portlet:renderURL>

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
    <div class="list">
        <table>
            <thead>
                <tr>
                    <th><liferay-ui:message key="pregunta.texto" /></th>
                    <th><liferay-ui:message key="pregunta.todas" /></th>
                    <th><liferay-ui:message key="pregunta.esMultiple" /></th>
                    <th><liferay-ui:message key="pregunta.puntos" /></th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${preguntas}" var="pregunta" >
                    <portlet:renderURL var="editaPregunta" >
                        <portlet:param name="action" value="editaPregunta" />
                        <portlet:param name="examenId" value="${examen.id}" />
                        <portlet:param name="preguntaId" value="${pregunta.id}" />
                    </portlet:renderURL>
                    <portlet:actionURL var="eliminaPregunta" >
                        <portlet:param name="action" value="eliminaPregunta" />
                        <portlet:param name="examenId" value="${examen.id}" />
                        <portlet:param name="preguntaId" value="${pregunta.id}" />
                    </portlet:actionURL>
                    <tr>
                        <td>${pregunta.texto}</td>
                        <td style="width:100px;"><input type="checkbox" disabled="true" <c:if test="${pregunta.todas}">checked="checked"</c:if> /></td>
                        <td style="width:120px;"><input type="checkbox" disabled="true" <c:if test="${pregunta.esMultiple}">checked="checked"</c:if> /></td>
                        <td style="width:60px;">${pregunta.puntos}</td>
                        <td style="width:80px;"><a class="edit" href="${editaPregunta}">EDITAR</a></td>
                        <td style="width:80px;"><a class="delete" href="${eliminaPregunta}">ELIMINAR</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="nav">
        <span class="menuButton"><a class="edit" href="${nuevaPregunta}"><liferay-ui:message key="examen.nuevaPregunta" /></a></span>
        <span class="menuButton"><a class="edit" href="${editaExamen}"><liferay-ui:message key="examen.edita" /></a></span>
        <span class="menuButton"><a class="delete" href="${eliminaExamen}" onclick="return confirm('<%= LanguageUtil.format(pageContext, "examen.elimina.confirmacion",request.getAttribute("examen"),false) %>');"><liferay-ui:message key="examen.elimina" /></a></span>
        <span class="menuButton"><a class="edit" href="${verCurso}"><liferay-ui:message key="curso.ver" /></a></span>
    </div>
</div>

