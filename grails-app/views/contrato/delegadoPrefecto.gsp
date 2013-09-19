<%@ page import="janus.Persona" %>
<div class="row">
    <div class="span2">
        Delegado del prefecto
    </div>

    <div class="3">
        <g:select name="delegadoPrefecto" from="${Persona.list([sort: 'apellido'])}" optionKey="id" optionValue="${{
            it.apellido + ' ' + it.nombre
        }}" value="${contrato?.delegadoPrefecto?.id}"/>
    </div>
</div>