<%@ page import="janus.Persona" %>
<div class="row">
    <div class="span2">
        Delegado del Prefecto:
    </div>

    <div class="3">
        <g:select name="delegadoPrefecto" from="${Persona.list([sort: 'apellido'])}" optionKey="id"
                  optionValue="${{it.apellido + ' ' + it.nombre }}" value="${contrato?.delegadoPrefecto?.id}"
                  noSelection="['null': 'No se ha definido aún ...']" style="width:440px;"/>
    </div>
</div>