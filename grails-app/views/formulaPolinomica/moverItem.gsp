<%@ page import="janus.ItemsFormulaPolinomica" %>
<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 17/04/17
  Time: 13:53
--%>
<div>
    <g:hiddenField name="nameNodo" class="idNodo" value="${nodo}"/>

    <div class="row">
        <div class="span">
            Mover Item al coeficiente:
            <g:select name="nombreCof" from="${cof?.numero}" class="span4 idCoefi" cf="${cof?.id}"/>
        </div>
    </div>
</div>