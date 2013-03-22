<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Volumenes de obra
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
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

        <div class="tituloTree">
            Volumen de obra de: ${obra.descripcion + " (" + obra.codigo + ")"}
        </div>

        <div class="span12 btn-group" role="navigation" style="margin-left: 0px;">
            %{--<a href="#" class="btn  " id="btn_lista">--}%
            %{--<i class="icon-file"></i>--}%
            %{--Lista--}%
            %{--</a>--}%
            %{--<a href="${g.createLink(action: 'rubroPrincipal')}" class="btn btn-ajax btn-new">--}%
            %{--<i class="icon-file"></i>--}%
            %{--Nuevo--}%
            %{--</a>--}%
            %{--<a href="#" class="btn btn-ajax btn-new" id="guardar">--}%
            %{--<i class="icon-file"></i>--}%
            %{--Guardar--}%
            %{--</a>--}%
            %{--<a href="${g.createLink(action: 'rubroPrincipal')}" class="btn btn-ajax btn-new">--}%
            %{--<i class="icon-file"></i>--}%
            %{--Cancelar--}%
            %{--</a>--}%
            %{--<a href="#" class="btn btn-ajax btn-new">--}%
            %{--<i class="icon-file"></i>--}%
            %{--Borrar--}%
            %{--</a>--}%
            <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn btn-ajax btn-new" id="atras" title="Regresar a la obra">
                <i class="icon-arrow-left"></i>
                Regresar
            </a>
            <a href="#" class="btn btn-ajax btn-new" id="calcular" title="Calcular precios">
                <i class="icon-table"></i>
                Calcular
            </a>
            %{--<a href="#" class="btn btn-ajax btn-new" id="transporte" title="Transporte">--}%
            %{--<i class="icon-truck"></i>--}%
            %{--Transporte--}%
            %{--</a>--}%
            %{--<a href="#" class="btn btn-ajax btn-new" id="imprimir" title="Imprimir">--}%
            %{--<i class="icon-print"></i>--}%
            %{--Imprimir--}%
            %{--</a>--}%
        </div>

        <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">
            <div class="borde_abajo" style="padding-left: 45px;position: relative;">
                <div class="linea" style="height: 98%;"></div>

                <div class="row-fluid" style="margin-left: 0px">
                    <div class="span3">
                        <b>Memo:</b> ${obra?.memoCantidadObra}
                    </div>

                    <div class="span3">
                        <b>Ubicación:</b> ${obra?.parroquia?.nombre}
                    </div>

                    <div class="span2">
                        <b style="">Dist. peso:</b> ${obra?.distanciaPeso}
                    </div>

                    <div class="span2">
                        <b>Dist. volúmen:</b> ${obra?.distanciaVolumen}
                    </div>
                </div>

                <div class="row-fluid" style="margin-left: 0px">
                    <div class="span4">
                        <b>Subpresupuesto:</b>
                        <span id="sp">
                            <g:select name="subpresupuesto" from="${janus.SubPresupuesto.list([order: 'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 300px;;font-size: 10px" id="subPres"/>
                        </span>
                        <a href="#" class="btn" id="btnCrearSP" title="Crear subpresupuesto" style="margin-top: -10px;">
                            <i class="icon-plus"></i>
                        </a>
                    </div>

                    <div class="span1" style="margin-left: 0px; width: 100px;">
                        <b>Código</b>
                        <input type="text" style="width: 100px;;font-size: 10px" id="item_codigo">
                        <input type="hidden" style="width: 60px" id="item_id">
                    </div>

                    <div class="span4" style="margin-left: 15px;">
                        <b>Rubro</b>
                        <input type="text" style="width: 350px;font-size: 10px" id="item_nombre" disabled="true">

                    </div>

                    <div class="span2" style="margin-left: 20px; width: 100px;">
                        <b>Cantidad</b>
                        <input type="text" style="width: 100px;text-align: right" id="item_cantidad" value="1">
                    </div>

                    <div class="span1" style="margin-left: 20px; width: 60px;">
                        <b>Orden</b>
                        <input type="text" style="width: 30px;text-align: right" id="item_orden" value="${(volumenes?.size() > 0) ? volumenes.size() + 1 : 1}">
                    </div>

                    <div class="span1" style="margin-left: 15px;padding-top:30px">
                        <input type="hidden" value="" id="vol_id">
                        <g:if test="${obra?.estado != 'R'}">
                            <a href="#" class="btn btn-primary" title="agregar" style="margin-top: -10px" id="item_agregar">
                                <i class="icon-plus"></i>
                            </a>
                        </g:if>
                    </div>
                </div>
            </div>

            <div class="borde_abajo" style="position: relative;float: left;width: 95%;padding-left: 45px">
                <p class="css-vertical-text">Composición</p>

                <div class="linea" style="height: 98%;"></div>

                <div style="width: 99.7%;height: 600px;overflow-y: auto;float: right;" id="detalle"></div>

                <div style="width: 99.7%;height: 30px;overflow-y: auto;float: right;text-align: right" id="total">
                    <b>TOTAL:</b>

                    <div id="divTotal" style="width: 150px;float: right;height: 30px;font-weight: bold;font-size: 12px;margin-right: 20px"></div>
                </div>
            </div>
        </div>

        <div class="modal grande hide fade" id="modal-rubro" style="overflow: hidden;">
            <div class="modal-header btn-info">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <bsc:buscador name="rubro.buscador.id" value="" accion="buscaRubro" controlador="volumenObra" campos="${campos}" label="Rubro" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

        <div class="modal hide fade" id="modal-SubPresupuesto">
            <div class="modal-header" id="modalHeader">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle-sp"></h3>
            </div>

            <div class="modal-body" id="modalBody-sp">
            </div>

            <div class="modal-footer" id="modalFooter-sp">
            </div>
        </div>

        <script type="text/javascript">
            function loading(div) {
                y = 0;
                $("#" + div).html("<div class='tituloChevere' id='loading'>Sistema Janus - Cargando, Espere por favor</div>")
                var interval = setInterval(function () {
                    if (y == 30) {
                        $("#detalle").html("<div class='tituloChevere' id='loading'>Cargando, Espere por favor</div>")
                        y = 0
                    }
                    $("#loading").append(".");
                    y++
                }, 500);
                return interval
            }
            function cargarTabla() {
                var interval = loading("detalle")
                var datos = ""
                if ($("#subPres_desc").val() * 1 > 0) {
                    datos = "obra=${obra.id}&sub=" + $("#subPres_desc").val()
                } else {
                    datos = "obra=${obra.id}"
                }
                $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'tabla')}",
                    data     : datos,
                    success  : function (msg) {
                        clearInterval(interval)
                        $("#detalle").html(msg)
                    }
                });
            }
            $(function () {
                %{--$("#detalle").html("<img src='${resource(dir: 'images',file: 'loadingText.gif')}' width='300px' height='45px'>")--}%

                cargarTabla();
                $("#vol_id").val("")
                $("#calcular").click(function () {
                    if ($(this).hasClass("active")) {
                        $(this).removeClass("active")
                        $(".col_delete").show()
                        $(".col_precio").hide()
                        $(".col_total").hide()
                        $("#divTotal").html("")
                    } else {
                        $(this).addClass("active")
                        $(".col_delete").hide()
                        $(".col_precio").show()
                        $(".col_total").show()
                        var total = 0
                        $(".total").each(function () {
                            total += parseFloat(str_replace(",", "", $(this).html()))
                        })
                        $("#divTotal").html(number_format(total, 2, ".", " "))
                    }
                });

                $("#item_codigo").dblclick(function () {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    $("#modalTitle").html("Lista de rubros");
                    $("#modalFooter").html("").append(btnOk);
                    $("#modal-rubro").modal("show");
                    $("#buscarDialog").unbind("click")
                    $("#buscarDialog").bind("click", enviar)

                });

                $("#item_codigo").blur(function () {
//            console.log($("#item_id").val()=="")
                    if ($("#item_id").val() == "" && $("#item_codigo").val() != "") {
                        $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'buscarRubroCodigo')}",
                            data     : "codigo=" + $("#item_codigo").val(),
                            success  : function (msg) {
                                if (msg != "-1") {
//                            console.log("msg "+msg)
                                    var parts = msg.split("&&")
                                    $("#item_id").val(parts[0])
                                    $("#item_nombre").val(parts[2])
                                } else {
                                    $("#item_id").val("")
                                    $("#item_nombre").val("")
                                }
                            }
                        });
                    }
                });
                $("#item_codigo").keydown(function (ev) {

                    if (ev.keyCode * 1 != 9 && (ev.keyCode * 1 < 37 || ev.keyCode * 1 > 40)) {

                        $("#item_id").val("")
                        $("#item_nombre").val("")

                    } else {
//                console.log("no reset")
                    }

                });

                $("#btnCrearSP").click(function () {
                    var $btnOrig = $(this).clone(true);
                    $(this).replaceWith(spinner);
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller:"subPresupuesto",action:'form_ajax')}",
                        success : function (msg) {
                            $("#modalBody-sp").html(msg);

                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            var $frm = $("#frmSave-SubPresupuesto");

                            btnSave.click(function () {
                                spinner.replaceWith($btnOrig);
                                if ($frm.valid()) {
                                    btnSave.replaceWith(spinner);
                                }
                                var data = $frm.serialize();

                                $.ajax({
                                    type    : "POST",
                                    url     : $frm.attr("action"),
                                    data    : data,
                                    success : function (msg) {
                                        if (msg != "NO") {
                                            $("#sp").html(msg);
                                            $("#modal-SubPresupuesto").modal("hide");
                                        }
                                    }
                                });

                                return false;
                            });

                            btnOk.click(function () {
                                spinner.replaceWith($btnOrig);
                            });

                            $("#modalHeader-sp").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle-sp").html("Crear Sub Presupuesto");
                            $("#modalFooter-sp").html("").append(btnOk).append(btnSave);
                            $("#modal-SubPresupuesto").modal("show");

                            $("#volob").val("1");
                        }
                    });
                    return false;
                });

                $("#item_agregar").click(function () {
                    $("#calcular").removeClass("active")
                    $(".col_delete").show()
                    $(".col_precio").hide()
                    $(".col_total").hide()
                    $("#divTotal").html("")
                    var cantidad = $("#item_cantidad").val()
                    cantidad = str_replace(",", "", cantidad)
                    var orden = $("#item_orden").val()
                    var rubro = $("#item_id").val()
                    var sub = $("#subPres").val()
                    if (isNaN(cantidad))
                        cantidad = 0
                    if (isNaN(orden))
                        orden = 0
                    var msn = ""
                    if (cantidad * 1 < 0.00001 || orden * 1 < 1) {
                        msn = "La cantidad  y el orden deben ser números positivos mayores a 0"
                    }
                    if (rubro * 1 < 1)
                        msn = "seleccione un rubro"

                    if (msn.length == 0) {
                        var datos = "rubro=" + rubro + "&cantidad=" + cantidad + "&orden=" + orden + "&sub=" + sub + "&obra=${obra.id}"
                        if ($("#vol_id").val() * 1 > 0)
                            datos += "&id=" + $("#vol_id").val()
                        $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'addItem')}",
                            data     : datos,
                            success  : function (msg) {
                                $("#detalle").html(msg)
                                $("#vol_id").val("")
                                $("#item_codigo").val("")
                                $("#item_id").val("")
                                $("#item_nombre").val("")
                                $("#item_cantidad").val("1")
                                $("#item_orden").val($("#item_orden").val() * 1 + 1)
                            }
                        });
                    } else {
                        $.box({
                            imageClass : "box_info",
                            text       : msn,
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
                    }

                });

            });
        </script>
    </body>
</html>