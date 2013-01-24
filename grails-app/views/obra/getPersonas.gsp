<div class="span1">Inspección</div>

%{--<div class="span3"><g:select name="inspector.id" class="inspector required" from="${janus.Persona?.list()}" value="${obra?.inspector?.nombre + " " + obra?.inspector?.apellido}" optionValue="nombre" optionKey="id"/></div>--}%
<div class="span3"><g:select name="inspector.id" class="inspector required" from="${personasRolInsp}" optionKey="id" optionValue="${{it.nombre+' '+it.apellido}}" /></div>

<div class="span1">Revisión</div>

%{--<div class="span3"><g:select name="revisor.id" class="revisor required" from="${janus.Persona?.list()}" value="${obra?.revisor?.id}" optionValue="nombre" optionKey="id"/></div>--}%
<div class="span3"><g:select name="revisor.id" class="revisor required" from="${personasRolRevi}" optionKey="id" optionValue="${{it.nombre+' '+it.apellido}}" /></div>

<div class="span1">Responsable</div>

<div class="span1"><g:select name="responsableObra.id" class="responsableObra required" from="${personasRolResp}" optionKey="id" optionValue="${{it.nombre+' '+it.apellido}}"/></div>