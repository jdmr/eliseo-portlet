<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "examen.edita.titulo",request.getAttribute("examen"),false) %></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="actualizaExamen"/>
    </portlet:actionURL>

    <form:form name="examenForm" commandName="examen" method="post" action="${actionUrl}" >
        <form:hidden path="id" />
        <form:hidden path="version" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="codigo"><liferay-ui:message key="examen.codigo" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="codigo" maxlength="32"/><br/>
                            <form:errors cssClass="errors" path="codigo" cssStyle="color:red;" />
                        </td>
                    </tr>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="nombre"><liferay-ui:message key="examen.nombre" /></label>
                        </td>
                        <td valign="top" class="value">
                            <form:input path="nombre" maxlength="128"/><br/>
                            <form:errors cssClass="errors" path="nombre" cssStyle="color:red;" />
                        </td>
                    </tr>

                </tbody>
            </table>
        </div>
        <portlet:renderURL var="verExamen" >
            <portlet:param name="action" value="verExamen" />
            <portlet:param name="examenId" value="${examen.id}" />
        </portlet:renderURL>
        <div class="nav">
            <span class="menuButtonn"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='curso.actualiza' />"/></span>
            <span class="menuButton"><a class="cancel" href="${verCurso}"><liferay-ui:message key="curso.cancela" /></a></span>
        </div>
    </form:form>
    <script type="text/javascript">
        document.examenForm.codigo.focus();
    </script>
</div>

