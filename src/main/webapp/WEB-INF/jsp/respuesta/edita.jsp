<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "respuesta.edita.titulo",request.getAttribute("pregunta"),false) %></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="actualizaRespuesta"/>
    </portlet:actionURL>

    <form:form name="respuestaForm" commandName="respuesta" method="post" action="${actionUrl}" >
        <form:hidden path="id" />
        <form:hidden path="version" />
        <input type="hidden" name="preguntaId" value="${pregunta.id}" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="texto"><liferay-ui:message key="respuesta.texto" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:textarea path="opcion.texto" />
                            <form:errors cssClass="errors" path="opcion.texto" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="todas"><liferay-ui:message key="respuesta.esCorrecta" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:checkbox path="esCorrecta" />
                            <form:errors cssClass="errors" path="esCorrecta" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="esParecida"><liferay-ui:message key="respuesta.esParecida" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:checkbox path="esParecida" />
                            <form:errors cssClass="errors" path="esParecida" cssStyle="color:red;" />
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
            <span class="menuButton"><a class="cancel" href="${verExamen}"><liferay-ui:message key="pregunta.cancela" /></a></span>
        </div>
    </form:form>
    <script type="text/javascript">
        document.preguntaForm.texto.focus();
    </script>
</div>

