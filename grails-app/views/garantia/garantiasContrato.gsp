<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 1/16/13
  Time: 11:10 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Garantías contrato</title>

    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.livequery.min.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href='${resource(dir: "js/jquery/plugins/box/css", file: "jquery.luz.box.css")}' rel='stylesheet' type='text/css'>

    <style type="text/css">
    .selected, .selected td {
        background : #B8D5DD !important;
    }

    .Renovada {
        background : #e5e5e5;
    }

    .Vigente {

    }

    .error {
        color : #aa1b17;
    }
    </style>

</head>

<body>

<div class="row" style="margin-bottom: 10px;">
    <div class="span9 btn-group" role="navigation">
        <g:link controller="contrato" action="verContrato" params="[contrato: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
            <i class="icon-double-angle-left"></i>
            Contrato
        </g:link>
        <a href="#" id="btnReporte" class="btn"><i class="icon-print"></i>
            Reporte
        </a>
        <a href="#" id="btnReporteGeneral" class="btn"><i class="icon-print"></i>
            Reporte General Garantías
        </a>
        <a href="#" id="btnReporteVenceran" class="btn"><i class="icon-print"></i>
            Reporte Grt. que vencerán
        </a>
        <a href="#" id="btnReporteDevueltas" class="btn"><i class="icon-print"></i>
            Reporte Grt. Devueltas
        </a>
        <a href="#" id="btnReporteVencidas" class="btn"><i class="icon-print"></i>
            Reporte Grt. Vencidas
        </a>

    </div>
</div>

<div style="border-bottom: 1px solid black;padding-left: 50px;margin-top: 10px;position: relative;">
    <p class="css-vertical-text">Contrato</p>

    <div class="linea" style="height: 100px;"></div>

    <div class="row">
        <div class="span1">
            N. contrato
        </div>

        <div class="span2">
            <span class="span2 uneditable-input">${contrato?.codigo}</span>
        </div>

        <div class="span1">
            Suscripción
        </div>

        <div class="span2">
            <span class="span2 uneditable-input">${contrato?.fechaSubscripcion}</span>
        </div>

        <div class="span1">
            Tipo Contrato
        </div>

        <div class="span2">
            <span class="span2 uneditable-input">${contrato?.tipoContrato?.descripcion}</span>
        </div>

        <div class="span1">
            Monto
        </div>

        <div class="span1">
            <span class="span2 uneditable-input">${contrato?.monto}</span>
        </div>
    </div>

    <div class="row">
        <div class="span1">
            Proyecto
        </div>

        <div class="span8">
            <span class="span8 uneditable-input">${contrato?.oferta?.concurso?.obra?.descripcion}</span>
        </div>

        <div class="span1">
            Fecha Inicio
        </div>

        <div class="span1">
            <span class="span1 uneditable-input">${contrato?.fechaInicio}</span>
        </div>
    </div>

    <div class="row">
        <div class="span1">
            Contratista
        </div>

        <div class="span5">
            <span class="span5 uneditable-input">${contrato?.oferta?.proveedor?.nombre}</span>
        </div>

        <div class="span1">
            Memo Distrib.
        </div>

        <div class="span2">
            <span class="span2 uneditable-input">${contrato?.memo}</span>
        </div>

        <div class="span1">
            Fecha Fin
        </div>

        <div class="span1">
            <span class="span1 uneditable-input">${contrato?.fechaFin}</span>
        </div>
    </div>
</div> <!-- Contrato -->

<form id="frmGarantia">
    <div style="border-bottom: 1px solid black;padding-left: 50px;margin-top: 10px;position: relative;">
        <p class="css-vertical-text">Garantía</p>

        <div class="linea" style="height: 100px;"></div>

        <div class="row">
            <div class="span1">
                Tipo
            </div>

            <div class="span4">
                <g:select name="tipoGarantia" from="${janus.pac.TipoGarantia.list([sort: 'descripcion'])}" class="span4 required" optionKey="id" optionValue="descripcion"/>
            </div>

            <div class="span1">
                N. Garantía
            </div>

            <div class="span2">
                <g:textField name="codigo" class="span2 required allCaps"/>
            </div>

            <div class="span1">
                Grnt. Original
            </div>

            <div class="span2">
                <g:textField name="padre" class="span2 allCaps" readonly="true"/>
            </div>
        </div>

        <div class="row">
            <div class="span1">
                Aseguradora
            </div>

            <div class="span4">
                <g:textField name="aseguradoraTxt" class="span4 required"/>
                <g:hiddenField name="aseguradora" id="aseguradora" class="span4 required"/>
            </div>

            <div class="span1">
                Documento
            </div>

            <div class="span2">
                <g:select name="tipoDocumentoGarantia" from="${janus.pac.TipoDocumentoGarantia.list([sort: 'descripcion'])}" class="span2 required" optionKey="id" optionValue="descripcion"/>
            </div>

            <div class="span1">
                Monto
            </div>

            <div class="span2 input-append">
                <g:textField name="monto" class="span2 required number"/>
                <div class="btn-group">
                    <button class="btn dropdown-toggle" data-toggle="dropdown">
                        <span id="monedaSelected" data-id="${janus.pac.Moneda.findByCodigo("USD")?.id}" data-nombre="${janus.pac.Moneda.findByCodigo("USD")?.codigo}">
                            ${janus.pac.Moneda.findByCodigo("USD")?.codigo}
                        </span>
                        <span class="caret"></span>
                    </button>
                    <ul class="dropdown-menu">
                        <g:each in="${janus.pac.Moneda.list([sort: 'codigo'])}" var="moneda">
                            <li id="mn-${moneda.id}" data-id="${moneda.id}" data-nombre="${moneda.codigo}" class="monedas ${moneda.codigo == 'USD' ? 'selected' : ''}">
                                <a>
                                    <g:if test="${moneda.codigo == 'USD'}">
                                        <i class="icon-chevron-right" id="marcaMoneda"></i>
                                    </g:if>
                                    ${moneda.codigo}
                                </a>
                            </li>
                        </g:each>
                    </ul>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="span1">
                Observaciones
            </div>
            <div class="span10">
                <g:textField name="observaciones" class="span10"/>
            </div>

        </div>

        <div class="row">
            <div class="span1">
                Emisión
            </div>

            <div class="span2">
                <elm:datepicker name="fechaInicio" class="span2 required" onClose="updateDias"/>
            </div>

            <div class="span1">
                Vencimiento
            </div>

            <div class="span2">
                <elm:datepicker name="fechaFinalizacion" class="span2 required" onClose="updateDias"/>
            </div>

            <div class="span1" style="width: 8px;">
                Días
            </div>

            <div class="span1">
                <g:textField name="diasGarantizados" class="span1 required" readonly="true"/>
            </div>

            %{--<div class="span1 estado hide" style="width: 12px;">--}%
            %{--Estado--}%
            %{--</div>--}%

            %{--<div class="span2 estado sel hide">--}%
            %{--<g:select name="estado" from="${estadosGarantia}" optionKey="codigo" class="input-medium" optionValue="descripcion"/>--}%
            %{--</div>--}%

            <div class="span2">
                <div class="btn-toolbar" id="btnsAdd">
                    <div class="btn-group">
                        <a href="#" id="btnAdd" class="btn btn-success" rel="tooltip" title="Agregar">
                            <i class="icon-plus icon-large"></i>
                        </a>
                    </div>
                </div>

                <div class="btn-toolbar hide" id="btnsEdit">
                    <div class="btn-group">
                        <a href="#" id="btnNew" class="btn" rel="tooltip" title="Nuevo">
                            <i class="icon-file icon-large"></i>
                        </a>
                        <g:hiddenField name="id"/>
                        <a href="#" id="btnSave" class="btn" rel="tooltip" title="Guardar">
                            <i class="icon-save icon-large"></i>
                        </a>
                        <a href="#" id="btnRenew" class="btn" rel="tooltip" title="Renovar">
                            <i class="icon-retweet icon-large"></i>
                        </a>
                        <a href="#" id="btnDelete" class="btn" rel="tooltip" title="Eliminar">
                            <i class="icon-trash icon-large"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div> <!-- nueva garantia -->
</form>

<div style="border-bottom: 1px solid black;padding-left: 50px;margin-top: 10px;position: relative; min-height: 150px;">
    <p class="css-vertical-text">Garantías</p>

    <div class="linea" style="height: 100px;"></div>

    <table class="table table-bordered table-condensed table-hover table-stripped">
        <thead>
        <tr>
            <th>Tipo</th>
            <th>N. Garantía</th>
            %{--<th>Nro</th>--}%
            <th>Aseguradora</th>
            <th>Documento</th>
            <th>Estado</th>
            <th>Emisión</th>
            <th>Vencimiento</th>
            <th>Monto</th>
            <th style="width: 55px;">Acciones</th>
            %{--<th>Fecha</th>--}%
        </tr>
        </thead>
        <tbody id="tbGarantias">

        </tbody>
    </table>

</div> <!-- garantias -->


<div class="modal grandote hide fade" id="modal-busqueda" style="overflow: hidden">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">x</button>

        <h3 id="modalTitle_busqueda">Aseguradoras</h3>
    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="aseguradoras" value="" accion="buscaAseguradora" controlador="garantia" campos="${campos}" label="Garantía" tipo="lista"/>
    </div>

    <div class="modal-footer" id="modalFooter_busqueda">

    </div>

</div>



<div class="modal large hide fade" id="modal-presupuesto">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle-presupuesto"></h3>
    </div>

    <div class="modal-body" id="modalBody-presupuesto">
    </div>

    <div class="modal-footer" id="modalFooter-presupuesto">
    </div>
</div>


<script type="text/javascript">

    var $frm = $("#frmGarantia"), $monedaSelected = $("#monedaSelected"), $body = $("#tbGarantias");

    function daydiff(first, second) {
        return (second - first) / (1000 * 60 * 60 * 24)
    }

    function updateDias() {
        var ini = $("#fechaInicio").datepicker("getDate");
        var fin = $("#fechaFinalizacion").datepicker("getDate");
        if (ini && fin) {
            var dif = daydiff(ini, fin);
            if (dif < 0) {
                dif = 0;
            }
            $("#diasGarantizados").val(dif);
        }
        if (ini) {
            $("#fechaFinalizacion").datepicker("option", "minDate", ini.add(1).days());
        }
        if (fin) {
            $("#fechaInicio").datepicker("option", "maxDate", fin.add(-1).days());
        }
    }

    function rowsIniciales() {
        var g = ${garantias};
        for (var i = 0; i < g.length; i++) {
            addRow(g[i], "last");
        }
    }

    function reset() {
        $(".selected").removeClass("selected");
        $frm.find("input").val("");
        $("#tipoGarantia option:first").attr('selected', 'selected');
        $("#tipoDocumentoGarantia option:first").attr('selected', 'selected');
    }

    function loadForm(data) {
        $.each(data, function (k, v) {
            if (k == "moneda") {
                var id = v;
                var $s = $("#mn-" + id);
                var nombre = $s.data("nombre");
                $monedaSelected.data({id : id, nombre : nombre}).text(nombre);
                $s.children("a").prepend($("#marcaMoneda"));
            } else {
                $("#" + k).val(v);
            }
        });
    }

    function estado() {
//                console.log("ASDF");
//                $(".estado").show();
        %{--$.ajax({--}%
        %{--type    : "POST",--}%
        %{--url     : "${createLink(action:'estados')}",--}%
        %{--data    : {--}%
        %{--},--}%
        %{--success : function (msg) {--}%
        %{--$(".estado.sel").html(msg);--}%
        %{--}--}%
        %{--});--}%
    }

    function botones(tipo) {
        var $add = $("#btnsAdd"), $edit = $("#btnsEdit");
        switch (tipo.toLowerCase()) {
            case "edit":
                $add.hide();
                $edit.show();
                break;
            case "create":
                reset();
                $edit.hide();
                $add.show();
                break;
        }
        estado();
    }

    function clicks($tr) {
//                var editables = [1, 2];  //vigente - pedido de cobro
        $tr.dblclick(function (ev) {
            if ($(this).hasClass("1") || $(this).hasClass("2")) {
                padreCod = $tr.data("codigo");
                $(".selected").removeClass("selected");
                $(this).addClass("selected");
                loadForm($(this).data());
                botones("edit");
            }
        });
    }

    function updateRow(data) {
//                ////console.log("update row");
        var $tr = $("#" + data.id);
        $.each(data, function (k, v) {
            if (k == "monto") {
                $tr.find(".monto").text(number_format(data.monto, 2, ".", ",") + " " + data.monedaTxt)
            } else {
                $tr.find("." + k).text(v);
            }
        });
        botonesRow(data.estadoCdgo, data.id, $tr.find(".acciones"));
        $tr.attr("title", data.observaciones);
    }

    function cambiarEstado(id, estado, title) {

        $.box({
            imageClass : false,
            input      : "<textarea style='width:260px; height: 70px;'></textarea>",
            type       : "prompt",
            title      : title,
            text       : "Observaciones",
            iconClose  : false,
            dialog     : {
                buttons : {
                    "Guardar"  : function (r) {
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'cambiarEstado')}",
                            data    : {
                                id     : id,
                                estado : estado,
                                obs    : r
                            },
                            success : function (msg) {
                                if (msg != "NO") {
                                    updateRow($.parseJSON(msg));
                                }
                            }
                        });
                    },
                    "Cancelar" : function () {
                    }
                }
            }
        });
    }

    function botonesRow(estado, id, $td) {
        $td.empty();
        switch (estado) {
            case "1": // Vigente
                var $devolver = $("<a href='#' class='btn btn-mini' title='Devolver'><i class='icon icon-reply'></i></a>");
                var $pedirCobro = $("<a href='#' class='btn btn-mini' title='Pedir Cobro'><i class='icon icon-money'></i></a>");

                $devolver.click(function () {
                    cambiarEstado(id, 3, "Devolver garantía");
                });
                $pedirCobro.click(function () {
                    cambiarEstado(id, 2, "Pedir cobro de garantía");
                });

                $td.append($devolver).append($pedirCobro);
                break;
            case "2": // Pedido de cobro
                var $efectivizar = $("<a href='#' class='btn btn-mini' title='Efectivizar'><i class='icon icon-archive'></i></a>");
                $efectivizar.click(function () {
                    cambiarEstado(id, 4, "Efectivizar garantía");
                });
                $td.append($efectivizar);
                break;
            case  "3" : // Devuelta
                break;
            case "4" : //Efectivizada
                break;
            case "5" : //Renovada
                break;
        }
    }

    function addRow(data, position) {
//                ////console.log("add row");
        var $tr = $("<tr></tr>").data(data).attr("id", data.id).addClass(data.estadoTxt).addClass(data.estadoCdgo).attr('etdo',data.estadoTxt);
        //Tipo  #   aseguradora     docu        estado      emision     vencimiento     monto
        var $tipo = $("<td class='tipoGarantiaTxt'></td>").text(data.tipoGarantiaTxt);
        var $num = $("<td class='codigo'></td>").text(data.codigo);
        var $aseguradora = $("<td class='aseguradoraTxt'></td>").text(data.aseguradoraTxt);
        var $doc = $("<td class='tipoDocumentoGarantiaTxt'></td>").text(data.tipoDocumentoGarantiaTxt);
        var $estado = $("<td class='estadoTxt'></td>").text(data.estadoTxt);
        var $emision = $("<td class='fechaInicio'></td>").text(data.fechaInicio);
        var $vencimiento = $("<td class='fechaFinalizacion'></td>").text(data.fechaFinalizacion);
        var $monto = $("<td class='monto'></td>").text(number_format(data.monto, 2, ".", ",") + " " + data.monedaTxt);

        var $acc = $("<td class='acciones'></td>");
        botonesRow(data.estadoCdgo, data.id, $acc);

        $tr.append($tipo).append($num).append($aseguradora).append($doc).append($estado).append($emision).append($vencimiento).append($monto).append($acc);

        $tr.attr("title", data.observaciones);
        clicks($tr);
        switch (position.toLowerCase()) {
            case "first":
                $body.prepend($tr);
                break;
            case "last":
                $body.append($tr);
                break;
            case "selected":
                var $padre = $("tr.selected");
                $padre.after($tr);
                break;
        }
        reset();
    }
    var padreCod;
    $(function () {
        rowsIniciales();

        $('[rel=tooltip]').tooltip();

        $(".monedas").click(function () {
            var id = $(this).data("id");
            var nombre = $(this).data("nombre");
            $monedaSelected.data({id : id, nombre : nombre}).text(nombre);
            $(this).children("a").prepend($("#marcaMoneda"));
        });

        $("#aseguradoraTxt").focus(function () {
            $("#modal-busqueda").modal("show");
        });

        $("#btnReporte").click(function () {
            location.href = "${createLink(controller: 'pdf',action: 'pdfLink')}?url=${createLink(controller: 'reportes',action: 'garantiasContrato')}/${contrato?.id}"
        });

        $("#btnReporteGeneral").click(function () {
            location.href = "${createLink(controller: 'reportes3',action: 'reporteGarantias')}"
        });

        $("#btnReporteVenceran").click(function () {
            $.ajax({
                type: "POST",
                url: "${createLink(controller: 'garantia', action: 'fechas_ajax')}",
                success: function (msg) {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-print"></i> Imprimir</a>');

                    btnSave.click(function () {
                        location.href = "${g.createLink(controller: 'reportes3', action:'reporteGarantiasVenceran' )}?desde=" + $("#fechaDesde").val() + "&hasta=" + $("#fechaHasta").val()
                        $("#modal-presupuesto").modal("hide");
                    });

                    $("#modalTitle-presupuesto").html("Garantías que vencerán para el período:");
                    $("#modalBody-presupuesto").html(msg);
                    $("#modalFooter-presupuesto").html("").append(btnOk).append(btnSave);
                    $("#modal-presupuesto").modal("show");
                }
            });
            return false;
        });

        $("#btnReporteDevueltas").click(function () {
            location.href = "${g.createLink(controller: 'reportes3', action:'reporteGarantiasDevueltas')}"
        });

        $("#btnReporteVencidas").click(function () {
            location.href = "${g.createLink(controller: 'reportes3', action:'reporteGarantiasVencidas')}"
        });


        $frm.validate();

        $("#btnAdd, #btnSave, #btnRenew").click(function () {
            var btn = $(this).attr("id");
            if (btn == "btnRenew") {
                $("#tipoGarantia").val($("tr.selected").data("tipoGarantia"));
            }
            if ($frm.valid()) {
                var data = {
                    contrato                 : ${contrato?.id},
                    tipoGarantiaTxt          : $("#tipoGarantia option:selected").text(),
                    tipoGarantia             : $("#tipoGarantia").val(),
                    codigo                   : $("#codigo").val(),
                    aseguradoraTxt           : $("#aseguradoraTxt").val(),
                    aseguradora              : $("#aseguradora").val(),
                    tipoDocumentoGarantiaTxt : $("#tipoDocumentoGarantia option:selected").text(),
                    tipoDocumentoGarantia    : $("#tipoDocumentoGarantia").val(),
                    monto                    : $("#monto").val(),
                    monedaTxt                : $monedaSelected.data("nombre"),
                    moneda                   : $monedaSelected.data("id"),
                    fechaInicio              : $("#fechaInicio").val(),
                    fechaFinalizacion        : $("#fechaFinalizacion").val(),
                    diasGarantizados         : $("#diasGarantizados").val(),
                    estado                   : 1,
                    estadoTxt                : 'Vigente',
                    estadoCdgo               : 1,
                    tipo                     : "add",
                    observaciones            : $("#observaciones").val()
                };
                var continua = true;
                if (btn == "btnAdd") {
                    $body.children("tr").each(function () {
//                                console.log('..etdo......', $(this).attr("etdo").toString())
                        if ($(this).data("tipoGarantia").toString() == data.tipoGarantia.toString() &&
                                $(this).attr("etdo").toString() == "Vigente" ) {
                            continua = false;
                        }
                    });
                } else if (btn == "btnEdit") {
                    data.id = $("#id").val();
                    data.tipo = "edit";
                } else if (btn == "btnRenew") {
                    data.id = $("#id").val();
                    data.tipo = "renew";
                    data.padre = padreCod;
                }
                if (continua) {
//                            ////console.log(data);
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'addGarantiaContrato')}",
                        data    : data,
                        success : function (msg) {
                            var parts = msg.split("_");
                            if (parts[0] == "OK") {
                                if (btn == "btnAdd") {
                                    data.id = parts[1];
                                    addRow(data, "first");
                                } else if (btn == "btnEdit") {
                                    updateRow(data);
                                } else if (btn == "btnRenew") {
                                    $("tr.selected").addClass("Renovada").data({
                                        estadoTxt : "Renovada",
                                        estado    : 6
                                    }).find(".estadoTxt").text("Renovada");

                                    data.id = parts[1];
                                    addRow(data, "selected");
                                    botones("create");
//                                            ////console.log(data);
                                }
                                reset();
                            } else {
//                                        ////console.log("ERROR!!!!");
                            }
                        }
                    });
                } else {
                    $.box({
                        imageClass : "box_info",
                        title      : "Alerta",
                        text       : "Solamente puede ingresar una garantía de cada tipo",
                        iconClose  : false,
                        dialog     : {
                            resizable     : false,
                            draggable     : false,
                            closeOnEscape : false,
                            buttons       : {
                                "Aceptar" : function () {
                                }
                            }
                        }
                    });
                }
            }
        }); //btn add / save / renew

        $("#btnNew").click(function () {
            botones("create");
        }); //btn new

        $("#btnDelete").click(function () {
            $.box({
                imageClass : "box_info",
                title      : "Alerta",
                text       : "Está seguro de querer eliminar esta garantía? Esta acción no puede deshacerse",
                iconClose  : false,
                dialog     : {
                    resizable     : false,
                    draggable     : false,
                    closeOnEscape : false,
                    buttons       : {
                        "Aceptar"  : function () {
                            $.ajax({
                                type    : "POST",
                                url     : "${createLink(action:'deleteGarantia')}",
                                data    : {
                                    id : $("tr.selected").attr("id")
                                },
                                success : function (msg) {
                                    if (msg == "OK") {
                                        $("tr.selected").remove();
                                        reset();
                                    } else {
                                        $.box({
                                            imageClass : "box_info",
                                            title      : "Alerta",
                                            text       : msg,
                                            iconClose  : false,
                                            dialog     : {
                                                resizable     : false,
                                                draggable     : false,
                                                closeOnEscape : false,
                                                buttons       : {
                                                    "Aceptar" : function () {
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        },
                        "Cancelar" : function () {
                        }
                    }
                }
            });
        }); //btn delete

    });
</script>

</body>
</html>