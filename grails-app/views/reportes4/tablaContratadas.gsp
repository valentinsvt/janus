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

        <b>Buscar Por: </b>
        <elm:select name="buscador" from = "${reportesServ.obrasContratadas()}" value="${params.buscador}"
                  optionKey="campo" optionValue="nombre" optionClass="operador" id="buscador_con" style="width: 200px" />

        <b>Operación:</b>
        <span id="selOpt"></span>
        <b style="margin-left: 20px">Criterio: </b>
        <g:textField name="criterio" style="width: 160px; margin-right: 10px" value="${params.criterio}" id="criterio_con"/>
        <a href="#" class="btn  " id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>
        <a href="#" class="btn" id="imprimir">
            <i class="icon-print"></i>
            Imprimir
        </a>
        <a href="#" class="btn" id="excel">
            <i class="icon-print"></i>
            Excel
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
        <th style="width: 120px;">
            Tipo
        </th>
        <th style="width: 80px">
            Fecha Reg
        </th>
        <th style="width: 250px">
            Cantón-Parroquia-Comunidad
        </th>
        <th style="width: 100px">
            Valor
        </th>
        <th style="width: 250px">
           Coordinación
        </th>
        <th style="width: 80px">
            Contrato
        </th>


    </tr>
    </thead>


    <tbody id="tabla_material">

    %{--<g:if test="${params.buscador != 'undefined'}">--}%

        <g:each in="${obras}" var="obra" status="j">
            <tr class="obra_row" id="${obra.obra__id}">
                <td>${obra.obracdgo}</td>
                <td>${obra.obranmbr}</td>
                <td>${obra.tpobdscr}</td>
                <td><g:formatDate date="${obra.obrafcha}" format="dd-MM-yyyy"/></td>
                <td>${obra.cntnnmbr} - ${obra.parrnmbr} - ${obra.cmndnmbr}</td>
                <td style="text-align: right">${obra.cntrmnto}</td>
                <td>${obra.dptodscr}</td>
                <td>${obra.cntrcdgo}</td>
            </tr>
        </g:each>
    %{--</g:if>--}%

    </tbody>
</table>

    </div>

</div>


<script type="text/javascript">

    var checkeados = []

    $("#buscar").click(function(){

        var datos = "si=${"si"}&buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val() +
                "&operador=" + $("#oprd").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaContratadas')}",
            data     : datos,
            success  : function (msg) {
                clearInterval(interval)
                $("#detalle").html(msg)
            }
        });
    });



    $("#regresar").click(function () {
        location.href = "${g.createLink(controller: 'reportes', action: 'index')}"
    });


    $("#imprimir").click(function () {
        var busca = $("#buscador_con").val();
        location.href="${g.createLink(controller: 'reportes4', action:'reporteContratadas' )}?buscador=" + busca + "&operador=" + $("#oprd").val() + "&criterio=" + $("#criterio_con").val()
    });

    $("#excel").click(function () {
        location.href="${g.createLink(controller: 'reportes4', action:'reporteExcelContratadas' )}?buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val() + "&operador=" + $("#oprd").val()
    });

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