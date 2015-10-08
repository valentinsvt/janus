<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            P.A.C.
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
        <style type="text/css">
        td {
            font-size : 10px !important;
        }

        th {
            font-size : 11px !important;
        }
        </style>
    </head>

    <body>
        <div class="span12">
            <g:if test="${flash.message}">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </g:if>
        </div>


        %{--<div class="span12 btn-group" role="navigation" style="margin-left: 0px;">--}%


        %{--<a href="#" class="btn btn-ajax btn-new" id="calcular" title="Calcular precios">--}%
        %{--<i class="icon-table"></i>--}%
        %{--Calcular--}%
        %{--</a>--}%

        %{--</div>--}%
        <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
            <div class="borde_abajo" style="padding-left: 45px;position: relative;">
                <p class="css-vertical-text">P.A.C.</p>

                <div class="linea" style="height: 98%;"></div>

                <div class="row-fluid" style="margin-left: 0px">
                    <div class="span1">
                        <b>Año:</b>
                        <g:select name="anio" from="${janus.pac.Anio.list([sort: 'anio'])}" id="item_anio" optionValue="anio" optionKey="id" value="${actual.id}" style="width: 80px;font-size: 14px; font-weight: bold "></g:select>
                    </div>

                    <div class="span3">
                        <b>Partida presupuestaria:</b>
                        <input type="text" style="width: 190px;;font-size: 12px" id="item_presupuesto">
                        <input type="hidden" style="width: 60px" id="item_prsp">
                        <a href="#" class="btn btn-warning" title="Crear nueva partida" style="margin-top: -10px" id="item_agregar_prsp">
                            <i class="icon-edit"></i>
                        </a>
                    </div>

                    <div class="span2">
                        <b>Techo:</b>
                        <input type="text" id="techo" disabled="true" style="width: 120px;;text-align: right">
                    </div>

                    <div class="span2">
                        <b>Comprometido:</b>
                        <input type="text" id="usado" disabled="true" style="width: 120px;;text-align: right">
                    </div>

                    <div class="span2">
                        <b>Disponible:</b>
                        <input type="text" id="disponible" disabled="true" style="width: 120px;text-align: right">
                    </div>
                </div>

                <div class="row-fluid" style="margin-left: 0px">
                    <div class="span4">
                        <b>Requirente:</b>
                        <input type="text" id="item_req" style="width: 250px; font-size: 12px;">
                    </div>

                    <div class="span3">
                        <b>Memorando:</b>
                        <input class="allCaps" type="text" id="item_memo" style="width: 156px; font-size: 12px;">
                    </div>

                    <div class="span4">
                        <b>Coordinación:</b>
                        <input type="hidden" id="item_id">
                        <g:select name="presupuesto.id" from="${janus.Departamento.list([sort: 'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 250px;;font-size: 12px" id="item_depto"></g:select>
                    </div>

                </div>

                <div class="row-fluid" style="margin-left: 0px">
                    <div class="span4">
                        <b>Tipo procedimiento:</b>
                        <g:select name="tipoProcedimiento.id" from="${janus.pac.TipoProcedimiento.list([sort: 'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 213px;;font-size: 12px" id="item_tipoProc"></g:select>
                    </div>

                    <div class="span3">
                        <b>Código C.P.:</b>
                        <input type="text" style="width: 154px;;font-size: 12px" id="item_codigo">
                        <input type="hidden" style="width: 60px" id="item_cpac">
                    </div>

                    <div class="span5">
                        <b>Tipo compra:</b>
                        <g:select name="tipo" from="${janus.pac.TipoCompra.list([sort: 'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 120px;;font-size: 12px; margin-left: 6px;" id="item_tipo"></g:select>
                    </div>
                </div>

                <div class="row-fluid" style="margin-left: 0px">

                    <div class="span4" style="width: 400px;">
                        <b>Descripción:</b>
                        <input type="text" style="width: 400px;font-size: 12px" id="item_desc">

                    </div>
                    %{--<div class="span2" style="width: 100px;">--}%
                    %{--<b>Tipo compra:</b>--}%
                    %{--<g:select name="tipo" from="${janus.pac.TipoCompra.list()}" optionKey="id" optionValue="descripcion" style="width: 100px;;font-size: 12px" id="item_tipo"></g:select>--}%
                    %{--</div>--}%
                    <div class="span2">
                        <b>Cantidad:</b>
                        <input type="text" style="width: 100px;text-align: right" id="item_cantidad" value="1">
                    </div>

                    <div class="span2" style="margin-left: -40px;">
                        <b>Costo unitario:</b>
                        <input type="text" style="width: 123px;text-align: right" id="item_precio" value="1">
                    </div>

                    <div class="span1" style="margin-left: -10px;">
                        <b>Unidad:</b>
                        <g:select name="unidad.id" from="${janus.pac.UnidadIncop.list()}" id="item_unidad" optionKey="id" optionValue="codigo" style="width: 60px;font-size: 12px"></g:select>
                    </div>

                    <div class="span2">
                        <b>Cuatrimestre:</b>

                        <div class="btn-group" data-toggle="buttons-checkbox">
                            <button type="button" id="item_c1" class="btn btn-info " style="font-size: 12px">C.1</button>
                            <button type="button" id="item_c2" class="btn btn-info " style="font-size: 12px">C.2</button>
                            <button type="button" id="item_c3" class="btn btn-info " style="font-size: 12px">C.3</button>
                        </div>
                    </div>

                    <div class="span1" style="margin-left: 12px;padding-top:30px; width:90px;">
                        <input type="hidden" value="" id="vol_id">
                        <a href="#" class="btn btn-primary" title="agregar" style="margin-top: -10px" id="item_agregar">
                            <i class="icon-plus"></i>
                        </a>
                        <g:link action="formUploadPac" class="btn" title="Subir Excel" style="margin-top: -10px" id="btnUpload">
                            <i class="icon-cloud-upload"></i>
                        </g:link>
                    </div>
                </div>
            </div>

            <div class="borde_abajo" style="position: relative;float: left;width: 95%;padding-left: 45px">
                <p class="css-vertical-text">Detalle</p>

                <div class="linea" style="height: 98%;"></div>

                <div style="width: 99.7%;height: 580px;overflow-y: auto;float: right;" id="detalle"></div>

            </div>
        </div>

        <div class="modal grande hide fade" id="modal-ccp" style="overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <bsc:buscador name="pac.buscador.id" value="" accion="buscaCpac" controlador="pac" campos="${campos}" label="cpac" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

        <div class="modal hide fade" id="modal-presupuesto">
            <div class="modal-header btn-warning">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle-presupuesto"></h3>
            </div>

            <div class="modal-body" id="modalBody-presupuesto">
            </div>

            <div class="modal-footer" id="modalFooter-presupuesto">
            </div>
        </div>

        <script type="text/javascript">
            function cargarTecho() {
                if ($("#item_prsp").val() * 1 > 0) {
                    $.ajax({type : "POST", url : "${g.createLink(controller: 'pac',action:'cargarTecho')}",
                        data     : "id=" + $("#item_prsp").val() + "&anio=" + $("#item_anio").val() + "&pac_id=" + $("#item_id").val(),
                        success  : function (msg) {
                            var parts = msg.split(";")
                            $("#techo").val(number_format(parts[0], 2, ".", ""))
                            $("#usado").val(number_format(parts[1], 2, ".", ""))
                            var dis = parts[0] - parts[1]
                            if ($("#item_id").val() * 1 > 1) {
                                var act = $("#item_cantidad").val() * $("#item_precio").val()
                                if (isNaN(act) || act == "")
                                    act = 0
                                dis += act
                            }

                            $("#disponible").val(number_format(dis, 2, ".", ""))
                        }
                    });
                }
            }
            function enviarPrsp() {
                var data = "";
                $("#buscarDialog").hide();
                $("#spinner").show();
                $(".crit").each(function () {
                    data += "&campos=" + $(this).attr("campo");
                    data += "&operadores=" + $(this).attr("operador");
                    data += "&criterios=" + $(this).attr("criterio");
                });
                if (data.length < 2) {
                    data = "tc=" + $("#tipoCampo").val() + "&campos=" + $("#campo :selected").val() + "&operadores=" + $("#operador :selected").val() + "&criterios=" + $("#criterio").val()
                }
                data += "&ordenado=" + $("#campoOrdn :selected").val() + "&orden=" + $("#orden :selected").val();
                $.ajax({type : "POST", url : "${g.createLink(controller: 'pac',action:'buscaPrsp')}",
                    data     : data,
                    success  : function (msg) {
                        $("#spinner").hide();
                        $("#buscarDialog").show();
                        $(".contenidoBuscador").html(msg).show("slide");
                    }
                });
            }
            function cargarTabla() {

                var datos = ""
                if ($("#item_depto").val() * 1 > 0) {
                    datos = "dpto=" + $("#item_depto").val() + "&anio=" + $("#item_anio").val()
                } else {
                    datos = "anio=" + $("#item_anio").val()
                }
                $.ajax({type : "POST", url : "${g.createLink(controller: 'pac',action:'tabla')}",
                    data     : datos,
                    success  : function (msg) {
                        $("#detalle").html(msg)
                    }
                });
            }
            $(function () {
                $("#item_agregar_prsp").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'form_ajax',controller: 'presupuesto')}",
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                            btnSave.click(function () {
                                if ($("#frmSave-presupuestoInstance").valid()) {
                                    btnSave.replaceWith(spinner);
                                }
                                $.ajax({type : "POST", url : "${g.createLink(controller: 'presupuesto',action:'saveAjax')}",
                                    data     : $("#frmSave-presupuestoInstance").serialize(),
                                    success  : function (msg) {

                                        var parts = msg.split("&")
                                        $("#item_prsp").val(parts[0])
                                        $("#item_presupuesto").val(parts[1])
                                        $("#item_presupuesto").attr("title", parts[2])
                                        $("#modal-presupuesto").modal("hide");
                                        cargarTecho()
                                    }
                                });

                                return false;
                            });

                            $("#modalTitle-presupuesto").html("Crear Presupuesto");
                            $("#modalBody-presupuesto").html(msg);
                            $("#modalFooter-presupuesto").html("").append(btnOk).append(btnSave);
                            $("#modal-presupuesto").modal("show");
                        }
                    });
                    return false;
                });

                $("#item_codigo").dblclick(function () {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    $("#modalTitle").html("Código compras públicas");
                    $("#modalFooter").html("").append(btnOk);
                    $(".contenidoBuscador").html("")
                    $("#modal-ccp").modal("show");
                    $("#buscarDialog").unbind("click")
                    $("#buscarDialog").bind("click", enviar)

                });

                $("#item_presupuesto").dblclick(function () {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    $("#modalTitle").html("Partidas presupuestarias");
                    $("#modalFooter").html("").append(btnOk);
                    $(".contenidoBuscador").html("")
                    $("#modal-ccp").modal("show");
                    $("#buscarDialog").unbind("click")
                    $("#buscarDialog").bind("click", enviarPrsp)

                });
                cargarTabla()
                $("#item_agregar").click(function () {
                    var dpto = $("#item_depto").val()
                    var anio = $("#item_anio").val()
                    var prsp = $("#item_prsp").val()

                    var cpac = $("#item_cpac").val()
                    var tipo = $("#item_tipo").val()
                    var desc = $("#item_desc").val()
                    var cant = $("#item_cantidad").val()
                    var req = $("#item_req").val()
                    var memo = $("#item_memo").val()
                    var tipoP = $("#item_tipoProc").val()
                    cant = str_replace(",", "", cant)
                    if (isNaN(cant))
                        cant = 0
                    var costo = $("#item_precio").val()
                    var costoAnt = $("#item_precio").attr("valAnt")
                    costo = str_replace(",", "", costo)
                    if (isNaN(costo))
                        costo = 0
                    costoAnt = str_replace(",", "", costoAnt)
                    if (isNaN(costoAnt))
                        costoAnt = 0
                    costoAnt = costoAnt * 1
                    var unidad = $("#item_unidad").val()
                    var c1 = $("#item_c1")
                    var c2 = $("#item_c2")
                    var c3 = $("#item_c3")
                    if (c1.hasClass("active"))
                        c1 = "S"
                    else
                        c1 = ""
                    if (c2.hasClass("active"))
                        c2 = "S"
                    else
                        c2 = ""
                    if (c3.hasClass("active"))
                        c3 = "S"
                    else
                        c3 = ""
                    var msg = ""
                    if (req.trim() == "") {
                        msg += "<br>Error: Ingrese el nombre de la persona requiriente"

                    }
                    if (memo.trim() == "") {
                        msg += "<br>Error: Ingrese el numero del momorando de referencia"
                    }
                    if (costo * 1 == 0 || cant * 1 == 0) {
                        msg += "<br>Error: El costo y la cantidad deben ser números positivos"
                    }
                    if (desc.trim() == "") {
                        msg += "<br>Error: Ingrese una descripción"

                    }
                    if (prsp * 1 < 1) {
                        msg += "<br>Error: Escoja una partida presupuestaria"

                    }
                    if (cpac * 1 < 1) {
                        msg += "<br>Error en el Código CP: Escoja un código de compras públicas (CPC) válido"

                    }
                    var disponible = $("#disponible").val()
                    if (disponible == "" || isNaN(disponible))
                        disponible = 0
                    else
                        disponible = disponible * 1

                    if (costo * cant > disponible + costoAnt) {
                        msg += "<br>Error: El valor total del P.A.C. (costo*cantidad) $" + (costo * cant) + " no se puede ser superior a: $" + (disponible + costoAnt)
                    }
                    if (msg !== "") {

                        $.box({
                            imageClass : "box_info",
                            text       : msg,
                            title      : "Alerta",
                            iconClose  : false,
                            dialog     : {
                                resizable : false,
                                draggable : false,
                                buttons   : {
                                    "Aceptar" : function () {
                                    }
                                },
                                width     : 500
                            }
                        });
                    } else {
                        $.ajax({type : "POST", url : "${g.createLink(controller: 'pac',action:'regPac')}",
                            data     : {
                                "departamento.id"      : dpto,
                                "anio.id"              : anio,
                                "presupuesto.id"       : prsp,
                                "cpp.id"               : cpac,
                                "tipoCompra.id"        : tipo,
                                "descripcion"          : desc,
                                "cantidad"             : cant,
                                "costo"                : costo,
                                "unidad.id"            : unidad,
                                "requiriente"          : req,
                                "memo"                 : memo,
                                "tipoProcedimiento.id" : tipoP,
                                c1                     : c1,
                                c2                     : c2,
                                c3                     : c3,
                                id                     : $("#item_id").val()
                            },
                            success  : function (msg) {
                                $("#item_id").val("")
                                $("#item_cpac").val("")
                                $("#item_tipo").val()
                                $("#item_desc").val("")
                                $("#item_cantidad").val("1")
                                $("#item_precio").val("1")
                                $("#item_unidad").val()
                                $("#item_c1").removeClass("active")
                                $("#item_c2").removeClass("active")
                                $("#item_c3").removeClass("active")
                                $("#item_codigo").val("").attr("title", "")
                                $("#item_presupuesto").val("").attr("title", "")
                                $("#item_prsp").val("")
                                $("#item_req").val("")
                                $("#item_memo").val("")
                                $("#techo").val("")
                                $("#usado").val("")
                                $("#disponible").val("")
                                cargarTabla()
                            }
                        });
                    }

                });

                $("#item_depto").change(function () {
                    cargarTabla()
                })
                $("#item_anio").change(function () {
                    cargarTabla()
                    cargarTecho()
                })
            });
        </script>
    </body>
</html>