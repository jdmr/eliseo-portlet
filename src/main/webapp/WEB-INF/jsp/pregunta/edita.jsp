<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "pregunta.edita.titulo",request.getAttribute("examen"),false) %></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="actualizaPregunta"/>
    </portlet:actionURL>

    <form:form name="preguntaForm" commandName="pregunta" method="post" action="${actionUrl}" >
        <form:hidden path="id" />
        <form:hidden path="version" />
        <input type="hidden" name="examenId" value="${examen.id}" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="texto"><liferay-ui:message key="pregunta.texto" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:textarea path="texto" />
                            <form:errors cssClass="errors" path="texto" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="todas"><liferay-ui:message key="pregunta.todas" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:checkbox path="todas" />
                            <form:errors cssClass="errors" path="todas" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="esMultiple"><liferay-ui:message key="pregunta.esMultiple" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:checkbox path="esMultiple" />
                            <form:errors cssClass="errors" path="esMultiple" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="puntos"><liferay-ui:message key="pregunta.puntos" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="puntos" maxlength="10" />
                            <form:errors cssClass="errors" path="puntos" cssStyle="color:red;" />
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <portlet:renderURL var="verExamen" >
            <portlet:param name="action" value="verExamen" />
            <portlet:param name="examenId" value="${examen.id}" />
        </portlet:renderURL>
        <portlet:renderURL var="nuevaRespuesta" >
            <portlet:param name="action" value="nuevaRespuesta" />
            <portlet:param name="examenId" value="${examen.id}" />
            <portlet:param name="preguntaId" value="${pregunta.id}" />
        </portlet:renderURL>
        <div class="nav">
            <span class="menuButton"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='pregunta.actualiza' />"/></span>
            <span class="menuButton"><a class="create" href="${nuevaRespuesta}"><liferay-ui:message key="respuesta.nueva" /></a></span>
            <span class="menuButton"><a class="cancel" href="${verExamen}"><liferay-ui:message key="pregunta.cancela" /></a></span>
        </div>
    </form:form>
    <script type="text/javascript">
        document.preguntaForm.texto.focus();
    </script>
</div>

