<div class="tituloTree" style="width: 1200px;">
    Asignaciones del año   <g:select name="anios" id="tabla_anio" from="${janus.pac.Anio.list(sort: 'anio')}" value="${actual.anio}" optionKey="anio" optionValue="anio" style="width: 80px;"></g:select>
</div>
<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr>
        <th width="320px;">Partida</th>
        <th width="90px;">Fuente</th>
        <th width="180px;">Programa</th>
        <th width="180px;">Subprograma</th>
        <th width="220px;">Proyecto</th>
        <th width="30px;">Año</th>
        <th width="80px;">Valor</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${asignaciones}" var="asg">
        <tr>
            <td>${asg.prespuesto.descripcion} (${asg.prespuesto.numero})</td>
            <td>${asg.prespuesto.fuente}</td>
            <td>${asg.prespuesto.programa}</td>
            <td>${asg.prespuesto.subPrograma}</td>
            <td>${asg.prespuesto.proyecto}</td>
            <td style="text-align: center">${asg.anio.anio}</td>
            <td style="text-align: right">
                <g:formatNumber number="${asg.valor}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<script type="text/javascript">
    $("#tabla_anio").change(function(){
        $.ajax({type : "POST", url : "${g.createLink(controller: 'asignacion',action:'tabla')}",
            data     :   "anio="+$("#tabla_anio").val(),
            success  : function (msg) {
                $("#list-Asignacion").html(msg)
            }
        });
    })
</script>