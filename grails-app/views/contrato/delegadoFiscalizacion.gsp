<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 08/07/15
  Time: 02:53 PM
--%>

<%@ page import="janus.Persona" %>
<div class="row">
    <div class="span2">
        Delegado de fiscalización:
    </div>

    <div class="3">
        <g:select name="delegadoFisc" from="${Persona.list([sort: 'apellido'])}" optionKey="id"
                  optionValue="${{it.apellido + ' ' + it.nombre }}" value="${contrato?.delegadoFiscalizacion?.id}"
                  noSelection="['null': 'No se ha definido aún ...']" style="width:440px;"/>
    </div>
</div>