<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 8/28/13
  Time: 10:36 AM
--%>




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
        <g:select name="buscador" from="${['contrato':'N° Contrato', 'cdgo': 'Garantía', 'nmrv': 'Renovación', 'tpgr': 'Tipo de Garantía', 'tdgr': 'Documento',
                'aseguradora':'Aseguradora', 'cont': 'Contratista', 'etdo':'Estado', 'mnto': 'Monto', 'mnda':'Moneda', 'fcin': 'Emisión', 'fcfn': 'Vencimiento',
                'dias':'Días']}" value="${params.buscador}"
                  optionKey="key" optionValue="value" id="buscador_gar" style="width: 150px"/>
        <b>Fecha: </b>
        <g:set var="fechas" value="${['fcin','fcfn']}" />

        <g:if test="${fechas.contains(params.buscador)}">
            <elm:datepicker name="fecha" id="fecha_gar"  value="${params.fecha}"/>
            <b>Criterio: </b>
            <g:textField name="criterio" id="criterio_gar" readonly="readonly" style="width: 250px; margin-right: 10px" value="${params.criterio}" />
        </g:if>
        <g:else>
            <elm:datepicker name="fecha" id="fecha_gar" disabled="disabled"  value="${params.fecha}"/>
            <b>Criterio: </b>
            <g:textField name="criterio" id="criterio_gar" style="width: 250px; margin-right: 10px" value="${params.criterio}" />
        </g:else>

        <a href="#" class="btn  " id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>

    </div>

</div>

%{--<div style="width: 1000px; height: 600px; overflow-y:hidden; overflow-x: auto" >--}%

<div class="row-fluid"  style="width: 99.7%;height: 500px;overflow-y: auto;float: right;">
    <div class="span12">


    <div style="width: 1800px; height: 500px;">
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>

                <th style="width: 100px;">
                    N° Contrato
                </th>
                <th style="width: 60px;">
                    N° Garantía
                </th>
                <th style="width: 60px">
                    Renovación
                </th>
                <th style="width: 300px">
                    Tipo de Garantía
                </th>
                <th style="width: 180px">
                    Documento
                </th>
                <th style="width: 280px">
                    Aseguradora
                </th>
                <th style="width: 350px">
                    Contratista
                </th>
                <th style="width: 80px">
                    Estado
                </th>
                <th style="width: 250px">
                    Monto
                </th>
                <th style="width: 100px">
                    Moneda
                </th>
                <th style="width: 150px">
                    Emisión
                </th>
                <th style="width: 150px">
                    Vencimiento
                </th>
                <th style="width: 100px">
                    Días
                </th>
            </tr>
            </thead>


            <tbody id="tabla_material">

            %{--<g:if test="${params.criterio || params.fecha}">--}%
            %{--<g:if test="${params.buscador != 'undefined'}">--}%


                <g:each in="${res}" var="cont" status="j">
                    <tr class="obra_row" id="${cont.id}">
                        <td>${cont.codigocontrato}</td>
                        <td>${cont.codigo}</td>
                        <td>${cont.renovacion}</td>
                        <td>${cont.tipogarantia}</td>
                        <td>${cont.documento} </td>
                        <td>${cont.aseguradora}</td>
                        <td>${cont.contratista}</td>
                        <td style="text-align: center">${cont.estado}</td>
                        <td>${cont.monto}</td>
                        <td>${cont.moneda}</td>
                        <td><g:formatDate date="${cont.emision}" format="dd-MM-yyyy"/></td>
                        <td><g:formatDate date="${cont.vencimiento}" format="dd-MM-yyyy"/></td>
                        <td>${cont.dias}</td>
                    </tr>
                </g:each>
            %{--</g:if>--}%
            </tbody>
        </table>

    </div>
</div>
</div>


<script type="text/javascript">

    var checkeados = []

    $("#buscar").click(function(){

        var datos = "si=${"si"}&buscador=" + $("#buscador_gar").val() + "&criterio=" + $("#criterio_gar").val() + "&fecha=" + $("#fecha_gar").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaGarantias')}",
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
        location.href="${g.createLink(controller: 'reportes4', action:'reporteGarantias' )}?buscador=" + $("#buscador_gar").val() + "&criterio=" + $("#criterio_gar").val()
    });

    $("#excel").click(function () {
        location.href="${g.createLink(controller: 'reportes4', action:'reporteExcelGarantias' )}?buscador=" + $("#buscador_gar").val() + "&criterio=" + $("#criterio_gar").val()
    });

    $("#buscador_gar").change(function () {
        if($(this).val() == 'fcin' || $(this).val() == 'fcfn'){
            $("#fecha_gar").removeAttr("disabled")
            $("#criterio_gar").attr("readonly", true).val("")
        }
        else {
            $("#fecha_gar").attr("disabled", true).val("")
            $("#criterio_gar").attr("readonly", false).val("")
        }
    })

</script>