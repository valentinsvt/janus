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
        <g:select name="buscador" from="${['cdgo': 'Codigo', 'nmbr': 'Nombre', 'tipo': 'Tipo', 'cntn': 'Cantón', 'parr': 'Parroquia'
                , 'cmnd': 'Comunidad', /*'ofig': 'Of. Ingreso', 'ofsl': 'Of. Salida'*/
                /* , 'mmsl': 'Memo Salida', 'frpl': 'F. Polinómica',*/ 'cntr': 'Núm. Contrato', 'cnts': 'Contratista']}" value="${params.buscador}"
                  optionKey="key" optionValue="value" id="buscador_con" style="width: 150px"/>
        %{--<b style="margin-left: 10px">Estado: </b>--}%
        %{--<g:select name="estado" from="${['1':'Todas', '2':'Ingresadas', '3':'Registradas']}" optionKey="key"--}%
        %{--optionValue="value" id="estado_reg" value="${params.estado}" style="width: 150px"/>--}%
        <b>Criterio:</b>
        <g:textField name="criterio" style="width: 250px; margin-right: 10px" value="${params.criterio}" id="criterio_con"/>
        <a href="#" class="btn  " id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>

            <a href="#" class="btn hide" id="imprimir" >
                <i class="icon-print"></i>
                Imprimir
            </a>

        <a href="#" class="btn hide" id="excel" >
            <i class="icon-print"></i>
            Excel
        </a>

        %{--<a href="#" class="btn" id="regresar">--}%
        %{--<i class="icon-arrow-left"></i>--}%
        %{--Regresar--}%
        %{--</a>--}%
    </div>
</div>

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
            <th style="width: 80px">
                Estado
            </th>
        </tr>
    </thead>
    <tbody id="tabla_material">

        <g:if test="${params.buscador != 'undefined'}">

            <g:each in="${obras}" var="fila" status="j">
                <tr class="obra_row">
                    <td>
                        %{--<g:link controller="reportesPlanillas" action="reporteAvance" id="${fila.id}">--}%
                            %{--${fila.obra_cod}--}%
                        %{--</g:link>--}%
                        ${fila.obracdgo}
                    </td>
                    <td>${fila.obranmbr}</td>
                    <td>${fila.cntnnmbr} - ${fila.parrnmbr} - ${fila.cmndnmbr}</td>
                    <td>${fila.cntrcdgo}</td>
                    <td>${fila.prvenmbr}</td>
                    <td><g:formatNumber number="${fila.cntrmnto}" maxFractionDigits="2" minFractionDigits="2" format="##,##0.##" locale="ec"/></td>
                    <td><g:formatDate date="${fila.cntrfcsb}" format="dd-MM-yyyy"/></td>
                    <td><g:formatNumber number="${fila.cntrplzo}" maxFractionDigits="0" minFractionDigits="0"/> días</td>
                    <td><g:formatNumber number="${(fila.av_economico) * 100}" maxFractionDigits="2" minFractionDigits="2"/>%</td>
                    <td><g:formatNumber number="${fila.av_fisico}" maxFractionDigits="2" minFractionDigits="2"/></td>
                    <td>
                        <g:set var="estado" value=""/>
                        <g:if test="${fila.obrafcin}">
                            <g:set var="estado" value="Iniciada el ${fila.obrafcin?.format('dd-MM-yyyy')}"/>
                            <g:if test="${fila.cntrfcfs}">
                                <g:set var="estado" value="Finalizada el ${fila.cntrfcfs?.format('dd-MM-yyyy')}"/>
                            </g:if>
                        </g:if>
                        ${estado}
                        %{--inicio: ${fila.inicio}<br/>--}%
                        %{--rec_con: ${fila.recepcion_contratista}<br/>--}%
                        %{--rec_fis: ${fila.recepcion_fisc}--}%
                    </td>
                </tr>
            </g:each>
        </g:if>
    </tbody>
</table>

<script type="text/javascript">

    var checkeados = [];

    $("#buscar").click(function () {

        var datos = "si=${"si"}&buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val()
//        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes5',action:'tablaAvance')}",
            data     : datos,
            success  : function (msg) {
//                clearInterval(interval)
                $("#detalle").html(msg)
                $("#imprimir").removeClass("hide");
                $("#excel").removeClass("hide");

            }
        });
    });

    $("#regresar").click(function () {
        location.href = "${g.createLink(controller: 'reportes', action: 'index')}"
    });

    $("#imprimir").click(function () {
        location.href = "${g.createLink(controller: 'reportes5', action:'reporteAvance' )}?buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val()
    });

    $("#excel").click(function () {

    location.href = "${g.createLink(controller: 'reportes4', action:'reporteExcelContratadas' )}?buscador=" + $("#buscador_con").val() + "&criterio=" + $("#criterio_con").val()

    });


</script>