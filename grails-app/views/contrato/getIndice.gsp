<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 01/10/14
  Time: 03:10 PM
--%>

<div class="span2 formato" style="margin-left: -1px">Indices 30 días antes de la presentación de la oferta</div>
<div class="span3">
<g:select name="periodoValidez.id" from="${janus.pac.PeriodoValidez.list([sort: 'fechaFin'])}" class="indiceOferta activo" value="${periodoValidez?.id}" optionValue="descripcion" optionKey="id"/>
</div>
