<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %>
<%@ include file="/WEB-INF/jsp/include.jsp" %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/main.css" type="text/css"/>
<div class="Curso">
    <h1><%= LanguageUtil.format(pageContext, "curso.contenido.titulo",request.getAttribute("curso"),false) %></h1>
    <portlet:actionURL var="actionUrl">
        <portlet:param name="action" value="actualizaContenido"/>
    </portlet:actionURL>

    <aui:form action="<%= actionUrl %>" method="post" name="fm" >
        <aui:input type="hidden" name="cursoId" value="${curso.id}" />
        <aui:input type="hidden" name="seleccionados" value="" />
        <div class="dialog">
            <table>
                <tbody>

                    <tr class="prop">
                        <td valign="top" class="name">
                            <label for="codigo"><liferay-ui:message key="curso.contenido" /></label>
                        </td>
                        <td valign="top" class="value">
                <liferay-ui:input-move-boxes
                    leftTitle="current"
                    rightTitle="available"
                    leftBoxName="contenidoSeleccionado"
                    rightBoxName="contenidoDisponible"
                    leftReorder="true"
                    leftList="${seleccionados}"
                    rightList="${disponibles}"
                    />
                </td>
                </tr>

                </tbody>
            </table>
        </div>
        <portlet:renderURL var="verCurso" >
            <portlet:param name="action" value="ver" />
            <portlet:param name="cursoId" value="${curso.id}" />
        </portlet:renderURL>
        <div class="nav">
            <span class="menuButtonn"><input type="submit" name="<portlet:namespace />_crea" class="save" value="<liferay-ui:message key='curso.actualiza' />" onclick='<%= renderResponse.getNamespace() + "saveSelectBoxes();" %>' /></span>
            <span class="menuButton"><a class="cancel" href="${verCurso}"><liferay-ui:message key="curso.cancela" /></a></span>
        </div>                    
    </aui:form>
</div>
<aui:script>
    Liferay.provide(
		window,
		'<portlet:namespace />saveSelectBoxes',
		function() {
                        document.<portlet:namespace />fm.<portlet:namespace />seleccionados.value = Liferay.Util.listSelect(document.<portlet:namespace />fm.<portlet:namespace />contenidoSeleccionado);
			submitForm(document.<portlet:namespace />fm);
		},
		['liferay-util-list-fields']
	);
</aui:script>