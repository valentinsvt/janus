<%@ page import="janus.utilitarios.reportesService" %>
<%
    def reportesServ = grailsApplication.classLoader.loadClass('janus.utilitarios.reportesService').newInstance()
%>

<g:if test="${flash.message}">
    <div class="span12" style="height: 35px;margin-bottom: 10px; margin-left: -25px">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status" style="text-align: center">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>


<div class="row-fluid">
    <div class="span12">

        <b>Buscar Por:</b>
        <elm:select name="buscador" from = "${reportesServ.obrasAvance()}" value="${params.buscador}"
                    optionKey="campo" optionValue="nombre" optionClass="operador" id="buscador_con" style="width: 240px" />
        <b>Operación:</b>
        <span id="selOpt"></span>
        <b style="margin-left: 20px">Criterio: </b>
        <g:textField name="criterio" style="width: 160px; margin-right: 10px" value="${params.criterio}" id="criterio_con"/>
        <a href="#" class="btn" id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>
        <a href="#" class="btn" id="imprimir" >
            <i class="icon-print"></i>
            Imprimir
        </a>
        <a href="#" class="btn" id="excel" >
            <i class="icon-print"></i>
            Excel
        </a>
        <a href="#" class="btn btn-info" id="imprimirGrafico" >
            <i class="icon-print"></i>
            Gráfico
        </a>
    </div>
</div>

<div class="row-fluid"  style="width: 99.7%;height: 600px;overflow-y: auto;float: right;">
    <div class="span12">

<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr>
        <th style="width: 80px;">
            Código
        </th>
        <th style="width: 250px;">
            Nombre
        </th>
        <th style="width: 250px">
            Cantón-Parroquia-Comunidad
        </th>
        <th style="width: 80px">
            Núm. Contrato
        </th>
        <th style="width: 150px">
            Contratista
        </th>
        <th style="width: 80px">
            Monto
        </th>
        <th style="width: 80px">
            Fecha suscripción
        </th>
        <th style="width: 80px">
            Plazo
        </th>
        <th style="width: 80px">
            % avance económico
        </th>
        <th style="width: 80px">
            Avance físico
        </th>
    </tr>

    </thead>
    <tbody id="tabla_material">
        <g:each in="${obras}" var="fila" status="j">
            <tr class="obra_row">
                <td>${fila.obracdgo}</td>
                <td>${fila.obranmbr}</td>
                <td>${fila.cntnnmbr} - ${fila.parrnmbr} - ${fila.cmndnmbr}</td>
                <td>${fila.cntrcdgo}</td>
                <td>${fila.prvenmbr}</td>
                <td><g:formatNumber number="${fila.cntrmnto}" maxFractionDigits="2" minFractionDigits="2" format="##,##0.##" locale="ec"/></td>
                <td><g:formatDate date="${fila.cntrfcsb}" format="dd-MM-yyyy"/></td>
                <td><g:formatNumber number="${fila.cntrplzo}" maxFractionDigits="0" minFractionDigits="0"/> días</td>
                <td><g:formatNumber number="${(fila.av_economico) * 100}" maxFractionDigits="2" minFractionDigits="2"/>%</td>
                <td><g:formatNumber number="${fila.av_fisico}" maxFractionDigits="2" minFractionDigits="2"/></td>
            </tr>
        </g:each>
    </tbody>
</table>

    </div>
</div>

<script type="text/javascript">

    var checkeados = [];

    $("#buscar").click(function () {

        var datos = "si=${"si"}&buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val() +
                "&operador=" + $("#oprd").val()
        //        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes5',action:'tablaAvance')}",
            data     : datos,
            success  : function (msg) {
                $("#detalle").html(msg);
            }
        });
    });

    $("#regresar").click(function () {
        location.href = "${g.createLink(controller: 'reportes', action: 'index')}"
    });

    $("#imprimir").click(function () {
        location.href = "${g.createLink(controller: 'reportes5', action:'reporteAvance' )}?buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val() + "&operador=" + $("#oprd").val()
    });

    $("#excel").click(function () {
        location.href = "${g.createLink(controller: 'reportes5', action:'reporteExcelAvance' )}?buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val() + "&operador=" + $("#oprd").val()
    });

    $("#imprimirGrafico").click(function () {
        location.href = "${g.createLink(controller: 'reportes6', action:'graficoAvance' )}?buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val() + "&operador=" + $("#oprd").val()
    })

    $("#buscador_con").change(function(){
        var anterior = "${params.operador}"
        var opciones = $(this).find("option:selected").attr("class").split(",");
        poneOperadores(opciones);
        /* regresa a la opción seleccionada */
        $("#oprd option[value=" + anterior + "]").prop('selected', true);
    });


    function poneOperadores (opcn) {
        var $sel = $("<select name='operador' id='oprd' style='width: 160px'}>");
        for(var i=0; i<opcn.length; i++) {
            var opt = opcn[i].split(":");
            var $opt = $("<option value='"+opt[0]+"'>"+opt[1]+"</option>");
            $sel.append($opt);
        }
        $("#selOpt").html($sel);
    };

    /* inicializa el select de oprd con la primea opción de busacdor */
    $( document ).ready(function() {
        $("#buscador_con").change();
    });

</script>