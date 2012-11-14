<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Rubros
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
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

        <div class="span12 btn-group" role="navigation">
            <a href="#" class="btn  " id="btn_lista">
                <i class="icon-file"></i>
                Lista
            </a>
            <a href="${g.createLink(action: 'rubroPrincipal')}" class="btn btn-ajax btn-new">
                <i class="icon-file"></i>
                Nuevo
            </a>
            <a href="#" class="btn btn-ajax btn-new" id="guardar">
                <i class="icon-file"></i>
                Guardar
            </a>
            <a href="${g.createLink(action: 'rubroPrincipal')}" class="btn btn-ajax btn-new">
                <i class="icon-file"></i>
                Cancelar
            </a>
            <a href="#" class="btn btn-ajax btn-new">
                <i class="icon-file"></i>
                Borrar
            </a>
            <a href="#" class="btn btn-ajax btn-new" id="calcular">
                <i class="icon-table"></i>
                Calcular
            </a>
        </div>


        <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px">

            <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;">
                <g:form class="frmRubro" action="save">
                    <input type="hidden" id="rubro__id" name="rubro.id" value="${rubro?.id}">

                    <p class="css-vertical-text">Rubro</p>

                    <div class="linea" style="height: 100px;"></div>

                    <div class="row-fluid">
                        <div class="span2">
                            Código
                            <input type="text" name="rubro.codigo" class="span24" value="${rubro?.codigo}">
                        </div>

                        <div class="span6">
                            Descripción
                            <input type="text" name="rubro.nombre" class="span72" value="${rubro?.nombre}">
                        </div>

                        <div class="span1" style="border: 0px solid black;height: 45px;padding-top: 18px">

                            <div class="btn-group" data-toggle="buttons-checkbox">
                                <button type="button" id="rubro_registro" class="btn btn-info ${(rubro?.registro == 'R') ? 'active registrado' : ""}" style="font-size: 10px">Registrado</button>
                            </div>
                            <input type="hidden" id="registrado" name="rubro.registro" value="${rubro?.registro}">

                        </div>

                        <div class="span2">
                            Fecha registro
                            <elm:datepicker name="rubro.fechaReg" class="span24" value="${rubro?.fechaRegistro}" disabled="true" id="fechaReg"/>
                        </div>

                    </div>
                    <div class="row-fluid">
                <div class="span2"  >
                    Clase
                    <g:select name="rubro.grupo.id" id="selClase" from="${grupos}" class="span12" optionKey="id" optionValue="descripcion"
                              value="${rubro?.departamento?.subgrupo?.grupo?.id}" noSelection="['': '--Seleccione--']"/>
                    </div>
                    <div class="span2">
                        Grupo
                        <g:if test="${rubro?.departamento?.subgrupo?.id}">
                            <g:select id="selGrupo" name="rubro.suggrupoItem.id" from="${janus.SubgrupoItems.findAllByGrupo(rubro?.departamento?.subgrupo?.grupo)}"
                                      class="span12" optionKey="id" optionValue="descripcion" value="${rubro?.departamento?.subgrupo?.id}" noSelection="['': '--Seleccione--']"/>
                        </g:if>
                        <g:else>
                            <select id="selGrupo" class="span12"></select>
                        </g:else>
                    </div>

                    <div class="span3">
                        Sub grupo
                        <g:if test="${rubro?.departamento?.id}">
                            <g:select name="rubro.departamento.id" id="selSubgrupo" from="${janus.DepartamentoItem.findAllBySubgrupo(rubro?.departamento?.subgrupo)}"
                                      class="span12" optionKey="id" optionValue="descripcion" value="${rubro?.departamento?.id}"/>
                        </g:if>
                        <g:else>
                            <select id="selSubgrupo" class="span12"></select>
                        </g:else>
                    </div>

                    <div class="span3">
                        Unidad
                        <g:select name="rubro.unidad.id" from="${janus.Unidad.list()}" class="span12" optionKey="id" optionValue="descripcion" value="${rubro?.unidad?.id}"/>
                    </div>
                </g:form>

                %{--<div class="span2"  >--}%
                %{--Rendimiento--}%
                %{--<input type="text" name="rubro.rendimiento" class="span24">--}%
                %{--</div>--}%

            </div>

            </div>

            <div style="border-bottom: 1px solid black;padding-left: 50px;margin-top: 10px;position: relative;">
                <p class="css-vertical-text">Items</p>

                <div class="linea" style="height: 100px;"></div>

                <div class="row-fluid">
                    <div class="span3">
                        <div style="height: 40px;float: left;width: 100px">Lista de precios</div>

                        <div class="btn-group span7" data-toggle="buttons-radio" style="float: right;">
                            <button type="button" class="btn btn-info active tipoPrecio" id="C">Civiles</button>
                            <button type="button" class="btn btn-info tipoPrecio" id="V">Viales</button>
                        </div>
                    </div>

                    <div class="span4">
                        Ciudad
                        <g:select name="item.ciudad.id" from="${janus.Lugar.findAllByTipo('C')}" optionKey="id" optionValue="descripcion" class="span10" id="ciudad"/>
                    </div>

                    <div class="span2">
                        Fecha
                        <elm:datepicker name="item.fecha" class="span8" id="fecha_precios"/>
                    </div>

                    <div class="span2">
                        <a class="btn btn-small btn-warning " href="#" rel="tooltip" title="Copiar " id="btn_copiarComp">
                            Copiar composición
                        </a>
                    </div>

                </div>

                <div class="row-fluid" style="margin-bottom: 5px">
                    <div class="span2">
                        Código
                        <input type="text" name="item.codigo" id="cdgo_buscar" class="span24">
                        <input type="hidden" id="item_id">
                    </div>

                    <div class="span6">
                        Descripción
                        <input type="text" name="item.descripcion" id="item_desc" class="span72">
                    </div>

                    <div class="span1">
                        Unidad
                        <input type="text" name="item.unidad" id="item_unidad" class="span8">
                    </div>

                    <div class="span1">
                        Cantidad
                        <input type="text" name="item.cantidad" class="span10" id="item_cantidad" value="1" style="text-align: right">
                    </div>

                    <div class="span1">
                        Rendimiento
                        <input type="text" name="item.rendimiento" class="span10" id="item_rendimiento" value="1" style="text-align: right">
                    </div>

                    <div class="span1" style="border: 0px solid black;height: 45px;padding-top: 22px">
                        <a class="btn btn-small btn-primary btn-ajax" href="#" rel="tooltip" title="Agregar" id="btn_agregarItem">
                            <i class="icon-plus"></i>
                        </a>
                    </div>

                </div>
            </div>
            <g:if test="${rubro}">
                <g:set var="items" value="${janus.Rubro.findAllByRubro(rubro)}"></g:set>
            </g:if>
            <div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;height: 500px;overflow-y: auto;">
                <p class="css-vertical-text">Composición</p>

                <div class="linea" style="height: 485px;"></div>
                <table class="table table-bordered table-striped table-condensed table-hover" style="margin-top: 10px;">
                    <thead>
                        <tr>
                            <th style="width: 80px;">
                                Código
                            </th>
                            <th style="width: 600px;">
                                Descripción Equipo
                            </th>
                            <th style="width: 80px;">
                                Cantidad
                            </th>
                            <th class="col_tarifa" style="display: none;">Tarifa</th>
                            <th class="col_hora" style="display: none;">C.Hora</th>
                            <th class="col_rend" style="width: 50px">Rendimiento</th>
                            <th class="col_total" style="display: none;">C.Total</th>
                            <th style="width: 40px" class="col_delete"></th>
                        </tr>
                    </thead>
                    <tbody id="tabla_equipo">
                        <g:each in="${items}" var="rub" status="i">
                            <g:if test="${rub.item.departamento.subgrupo.grupo.id == 3}">
                                <tr class="item_row" id="${rub.id}">
                                    <td class="cdgo">${rub.item.codigo}</td>
                                    <td>${rub.item.nombre}</td>
                                    <td style="text-align: right" class="cant">
                                        <g:formatNumber number="${rub.cantidad}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/>
                                    </td>

                                    <td class="col_tarifa" style="display: none;"></td>
                                    <td class="col_hora" style="display: none;"></td>
                                    <td class="col_rend rend" style="width: 50px;text-align: right">
                                        <g:formatNumber number="${rub.rendimiento}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/>
                                    </td>
                                    <td class="col_total" style="display: none;"></td>
                                    <td style="width: 40px;text-align: center" class="col_delete">
                                        <a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="${rub.id}">
                                            <i class="icon-trash"></i></a>
                                    </td>
                                </tr>
                            </g:if>
                        </g:each>
                    </tbody>
                </table>
                <table class="table table-bordered table-striped table-condensed table-hover">
                    <thead>
                        <tr>
                            <th style="width: 80px;">
                                Código
                            </th>
                            <th style="width: 600px;">
                                Descripción Mano de obra
                            </th>
                            <th style="width: 80px">
                                Cantidad
                            </th>

                            <th class="col_jornal" style="display: none;">Jornal</th>
                            <th class="col_hora" style="display: none;">C.Hora</th>
                            <th class="col_rend" style="width: 50px">Rendimiento</th>
                            <th class="col_total" style="display: none;">C.Total</th>
                            <th style="width: 40px" class="col_delete"></th>
                        </tr>
                    </thead>
                    <tbody id="tabla_mano">
                        <g:each in="${items}" var="rub" status="i">
                            <g:if test="${rub.item.departamento.subgrupo.grupo.id == 2}">
                                <tr class="item_row" id="${rub.id}">
                                    <td class="cdgo">${rub.item.codigo}</td>
                                    <td>${rub.item.nombre}</td>
                                    <td style="text-align: right" class="cant">
                                        <g:formatNumber number="${rub.cantidad}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/>
                                    </td>

                                    <td class="col_jornal" style="display: none;"></td>
                                    <td class="col_hora" style="display: none;"></td>
                                    <td class="col_rend rend" style="width: 50px;text-align: right">
                                        <g:formatNumber number="${rub.rendimiento}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/>
                                    </td>
                                    <td class="col_total" style="display: none;"></td>
                                    <td style="width: 40px;text-align: center" class="col_delete">
                                        <a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="${rub.id}">
                                            <i class="icon-trash"></i></a>
                                    </td>
                                </tr>
                            </g:if>
                        </g:each>
                    </tbody>
                </table>
                <table class="table table-bordered table-striped table-condensed table-hover">
                    <thead>
                        <tr>
                            <th style="width: 80px;">
                                Código
                            </th>
                            <th style="width: 600px;">
                                Descripción Material
                            </th>
                            <th style="width: 60px" class="col_unidad">
                                Unidad
                            </th>
                            <th style="width: 80px">
                                Cantidad
                            </th>
                            <th style="width: 40px" class="col_delete"></th>
                            <th class="col_precioUnit" style="display: none;">Unitario</th>
                            <th class="col_vacio" style="width: 55px;display: none"></th>
                            <th class="col_vacio" style="width: 55px;display: none"></th>
                            <th class="col_total" style="display: none;">C.Total</th>
                        </tr>
                    </thead>
                    <tbody id="tabla_material">
                        <g:each in="${items}" var="rub" status="i">
                            <g:if test="${rub.item.departamento.subgrupo.grupo.id == 1}">
                                <tr class="item_row" id="${rub.id}">
                                    <td class="cdgo">${rub.item.codigo}</td>
                                    <td>${rub.item.nombre}</td>
                                    <td style="width: 60px !important;text-align: center" class="col_unidad">${rub.item.unidad.codigo}</td>
                                    <td style="text-align: right" class="cant">
                                        <g:formatNumber number="${rub.cantidad}" format="##,#####0" minFractionDigits="5" maxFractionDigits="7"/>
                                    </td>
                                    <td class="col_precioUnit" style="display: none;"></td>
                                    <td class="col_vacio" style="width: 50px;display: none"></td>
                                    <td class="col_vacio" style="width: 50px;display: none"></td>
                                    <td class="col_total" style="display: none;"></td>
                                    <td style="width: 40px;text-align: center" class="col_delete">
                                        <a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="${rub.id}">
                                            <i class="icon-trash"></i></a>
                                    </td>
                                </tr>
                            </g:if>
                        </g:each>
                    </tbody>
                </table>
            </div>

        </div>

        <div class="modal grande hide fade " id="modal-rubro" style=";overflow: hidden;">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <bsc:buscador name="rubro.buscador.id" value="" accion="buscaRubro" controlador="rubro" campos="${campos}" label="Rubro" tipo="lista"/>
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>
        <script type="text/javascript">
            function enviarItem() {
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
                $.ajax({type : "POST", url : "${g.createLink(controller: 'rubro',action:'buscaItem')}",
                    data     : data,
                    success  : function (msg) {
                        $("#spinner").hide();
                        $("#buscarDialog").show();
                        $(".contenidoBuscador").html(msg).show("slide");
                    }
                });

            }

            function enviarCopiar() {
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
                $.ajax({type : "POST", url : "${g.createLink(controller: 'rubro',action:'buscaRubroComp')}",
                    data     : data,
                    success  : function (msg) {
                        $("#spinner").hide();
                        $("#buscarDialog").show();
                        $(".contenidoBuscador").html(msg).show("slide");
                    }
                });
            }
            $(function () {

                <g:if test="${!rubro?.departamento?.subgrupo?.grupo?.id}">
                $("#selClase").val("");
                </g:if>

                $("#selClase").change(function () {
                    var clase = $(this).val();
                    var $subgrupo = $("<select id='selSubgrupo' class='span12'></select>");
                    $("#selSubgrupo").replaceWith($subgrupo);
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'gruposPorClase')}",
                        data    : {
                            id : clase
                        },
                        success : function (msg) {
                            $("#selGrupo").replaceWith(msg);
                        }
                    });
                });
                $("#selGrupo").change(function () {
                    var grupo = $(this).val();
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'subgruposPorGrupo')}",
                        data    : {
                            id : grupo
                        },
                        success : function (msg) {
                            $("#selSubgrupo").replaceWith(msg);
                        }
                    });
                });

                $(".tipoPrecio").click(function () {
                    if (!$(this).hasClass("active")) {
                        var tipo = $(this).attr("id");
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'ciudadesPorTipo')}",
                            data    : {
                                id : tipo
                            },
                            success : function (msg) {
                                $("#ciudad").replaceWith(msg);
                            }
                        });
                    }
                });

                $("#calcular").click(function () {
                    if ($(this).hasClass("active")) {
                        $(this).removeClass("active")
                        $(".col_delete").show()
                        $(".col_unidad").show()
                        $(".col_tarifa").hide()
                        $(".col_hora").hide()
                        $(".col_total").hide()
                        $(".col_jornal").hide()
                        $(".col_precioUnit").hide()
                        $(".col_vacio").hide()
                    } else {
                        $(this).addClass("active")
                        var fecha = $("#fecha_precios").val()
                        if (fecha.length < 8) {
                            $.box({
                                imageClass : "box_info",
                                text       : "Seleccione una fecha para determinar la lista de precios",
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
                            $(this).removeClass("active")
                        } else {
                            var items = $(".item_row")
                            if (items.size() < 1) {
                                $.box({
                                    imageClass : "box_info",
                                    text       : "Añada items a la composición del rubro antes de calcular los precios",
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
                                $(this).removeClass("active")
                            } else {
                                var tipo = "C"
                                if ($("#V").hasClass("active"))
                                    tipo = "V"
                                var datos = "fecha=" + $("#fecha_precios").val() + "&ciudad=" + $("#ciudad").val() + "&tipo=" + tipo + "&ids="
                                $.each(items, function () {
                                    datos += $(this).attr("id") + "#"
                                });

                                $.ajax({type : "POST", url : "${g.createLink(controller: 'rubro',action:'getPrecios')}",
                                    data     : datos,
                                    success  : function (msg) {
                                        console.log(msg)

                                    }
                                });

                                $(".col_delete").hide()
                                $(".col_unidad").hide()
                                $(".col_tarifa").show()
                                $(".col_hora").show()
                                $(".col_total").show()
                                $(".col_jornal").show()
                                $(".col_precioUnit").show()
                                $(".col_vacio").show()
                            }
                        }
                    }
                });

                $("#btn_copiarComp").click(function () {
                    if ($("#rubro__id").val() * 1 > 0) {
                        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                        $("#modalTitle").html("Lista de rubros");
                        $("#modalFooter").html("").append(btnOk);
                        $(".contenidoBuscador").html("")
                        $("#modal-rubro").modal("show");
                        $("#buscarDialog").unbind("click")
                        $("#buscarDialog").bind("click", enviarCopiar)
                    } else {
                        $.box({
                            imageClass : "box_info",
                            text       : "Primero guarde el rubro o seleccione uno para editar",
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

                $(".borrarItem").click(function () {
                    var tr = $(this).parent().parent()
                    if (confirm("Esta seguro de eliminar este registro? Esta acción es irreversible")) {
                        $.ajax({type : "POST", url : "${g.createLink(controller: 'rubro',action:'eliminarRubroDetalle')}",
                            data     : "id=" + $(this).attr("iden"),
                            success  : function (msg) {
                                if (msg == "Registro eliminado") {
                                    tr.remove()
                                }

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
                                        }
                                    }
                                });

                            }
                        });
                    }

                });

                $("#cdgo_buscar").focus(function () {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    $("#modalTitle").html("Lista de items");
                    $("#modalFooter").html("").append(btnOk);
                    $(".contenidoBuscador").html("")
                    $("#modal-rubro").modal("show");
                    $("#buscarDialog").unbind("click")
                    $("#buscarDialog").bind("click", enviarItem)
                });
                $("#btn_lista").click(function () {
                    var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
                    $("#modalTitle").html("Lista de rubros");
//        $("#modalBody").html($("#buscador_rubro").html());
                    $("#modalFooter").html("").append(btnOk);
                    $(".contenidoBuscador").html("")
                    $("#modal-rubro").modal("show");
                    $("#buscarDialog").unbind("click")
                    $("#buscarDialog").bind("click", enviar)

                }); //click btn new
                $("#rubro_registro").click(function () {
                    if ($(this).hasClass("active")) {
                        if (confirm("Esta seguro de desregistrar este rubro?")) {
                            $("#registrado").val("N")
                            $("#fechaReg").val("")
                        }
                    } else {
                        if (confirm("Esta seguro de registrar este rubro?")) {
                            $("#registrado").val("R")
                            var fecha = new Date()
                            $("#fechaReg").val(fecha.toString("dd/mm/yyyy"))
                        }
                    }
                });

                $("#guardar").click(function () {
                    $(".frmRubro").submit()
                });

                <g:if test="${rubro}">
                $("#btn_agregarItem").click(function () {
                    var cant = $("#item_cantidad").val()
                    if (cant == "")
                        cant = 0
                    if (isNaN(cant))
                        cant = 0
                    var rend = $("#item_rendimiento").val()
                    if (isNaN(rend))
                        rend = 1
                    if ($("#item_id").val() * 1 > 0) {
                        if (cant > 0) {
                            var data = "rubro=${rubro.id}&item=" + $("#item_id").val() + "&cantidad=" + cant + "&rendimiento=" + rend
                            $.ajax({type : "POST", url : "${g.createLink(controller: 'rubro',action:'addItem')}",
                                data     : data,
                                success  : function (msg) {
                                    var tr = $("<tr class='item_row'>")
                                    var td = $("<td>")
                                    var band = true
                                    var parts = msg.split(";")
                                    tr.attr("id", parts[1])
                                    var a
                                    td.html($("#cdgo_buscar").val())
                                    tr.append(td)
                                    td = $("<td>")
                                    td.html($("#item_desc").val())
                                    tr.append(td)

                                    if (parts[0] == "1") {
                                        $("#tabla_material").children().find(".cdgo").each(function () {
                                            if ($(this).html() == $("#cdgo_buscar").val()) {
                                                var tdCant = $(this).parent().find(".cant")
                                                tdCant.html(number_format(tdCant.html() * 1 + $("#item_cantidad").val() * 1, 5, ".", ""))
                                                band = false
                                            }
                                        });
                                        if (band) {
                                            td = $("<td style='text-align: center' class='col_unidad'>")
                                            td.html($("#item_unidad").val())
                                            tr.append(td)
                                            td = $("<td style='text-align: right' class='cant'>")
                                            td.html(number_format($("#item_cantidad").val(), 5, ".", ""))
                                            tr.append(td)

                                            td = $('<td class="col_precioUnit" style="display: none;"></td>');
                                            tr.append(td)
                                            td = $('<td class="col_vacio" style="width: 40px;display: none"></td>');
                                            tr.append(td)
                                            td = $('<td class="col_vacio" style="width: 40px;display: none"></td>');
                                            tr.append(td)
                                            td = $('<td class="col_total" style="display: none;"></td>');
                                            tr.append(td)
                                            td = $('<td  style="width: 40px;text-align: center" class="col_delete">')
                                            a = $('<a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="' + parts[1] + '"><i class="icon-trash"></i></a>')
                                            td.append(a)
                                            tr.append(td)
                                            $("#tabla_material").append(tr)
                                        }

                                    } else {
                                        if (parts[0] == "2") {

                                            $("#tabla_mano").children().find(".cdgo").each(function () {
                                                if ($(this).html() == $("#cdgo_buscar").val()) {
                                                    var tdCant = $(this).parent().find(".cant")
                                                    var tdRend = $(this).parent().find(".rend")
                                                    tdCant.html(number_format(tdCant.html() * 1 + $("#item_cantidad").val() * 1, 5, ".", ""))
                                                    tdRend.html(number_format($("#item_rendimiento").val() * 1, 5, ".", ""))
                                                    band = false
                                                }
                                            });
                                            if (band) {
                                                td = $("<td style='text-align: right' class='cant'>")
                                                td.html(number_format($("#item_cantidad").val(), 5, ".", ""))
                                                tr.append(td)
                                                td = $('<td class="col_jornal" style="display: none;"></td>');
                                                tr.append(td)
                                                td = $('<td class="col_hora" style="display: none;"></td>');
                                                tr.append(td)
                                                td = $("<td style='text-align: right' class='col_rend rend'>")
                                                td.html(number_format($("#item_rendimiento").val(), 5, ".", ""))
                                                tr.append(td)
                                                td = $('<td class="col_total" style="display: none;"></td>');
                                                tr.append(td)
                                                td = $('<td  style="width: 40px;text-align: center" class="col_delete">')
                                                a = $('<a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="' + parts[1] + '"><i class="icon-trash"></i></a>')
                                                td.append(a)
                                                tr.append(td)
                                                $("#tabla_mano").append(tr)
                                            }

                                        } else {
                                            $("#tabla_equipo").children().find(".cdgo").each(function () {
                                                if ($(this).html() == $("#cdgo_buscar").val()) {
                                                    var tdCant = $(this).parent().find(".cant")
                                                    var tdRend = $(this).parent().find(".rend")
                                                    tdCant.html(number_format(tdCant.html() * 1 + $("#item_cantidad").val() * 1, 5, ".", ""))
                                                    tdRend.html(number_format(tdRend.html() * 1 + $("#item_rendimiento").val() * 1, 5, ".", ""))
                                                    band = false
                                                }
                                            });

                                            if (band) {
                                                td = $("<td style='text-align: right'>")
                                                td.html(number_format(1, 5, ".", ""))
                                                tr.append(td)
                                                td = $('<td class="col_tarifa" style="display: none;"></td>');
                                                tr.append(td)
                                                td = $('<td class="col_hora" style="display: none;"></td>');
                                                tr.append(td)
                                                td = $("<td style='text-align: right' class='col_rend'>")
                                                td.html(number_format($("#item_rendimiento").val(), 5, ".", ""))
                                                tr.append(td)
                                                td = $('<td class="col_total" style="display: none;"></td>');
                                                tr.append(td)
                                                td = $('<td  style="width: 40px;text-align: center" class="col_delete">')
                                                a = $('<a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="' + parts[1] + '"><i class="icon-trash"></i></a>')
                                                td.append(a)
                                                tr.append(td)
                                                $("#tabla_equipo").append(tr)
                                            }
                                        }
                                    }
                                    if (a) {
                                        a.bind("click", function () {
                                            var tr = $(this).parent().parent()
                                            if (confirm("Esta seguro de eliminar este registro? Esta acción es irreversible")) {
                                                $.ajax({type : "POST", url : "${g.createLink(controller: 'rubro',action:'eliminarRubroDetalle')}",
                                                    data     : "id=" + $(this).attr("iden"),
                                                    success  : function (msg) {
                                                        if (msg == "Registro eliminado") {
                                                            tr.remove()
                                                        }

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
                                                                }
                                                            }
                                                        });

                                                    }
                                                });
                                            }

                                        });
                                    }

                                    $("#item_desc").val("")
                                    $("#item_id").val("")
                                    $("#item_cantidad").val("1")
                                    $("#cdgo_buscar").val("")
                                    $("#cdgo_unidad").val("")
                                    $("#item_rendimiento").val("1")
                                }
                            });
                        } else {
                            $.box({
                                imageClass : "box_info",
                                text       : "La cantidad debe ser un número positivo",
                                title      : "Alerta",
                                iconClose  : false,
                                dialog     : {
                                    resizable : false,
                                    draggable : false,
                                    buttons   : {
                                        "Aceptar" : function () {
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        $.box({
                            imageClass : "box_info",
                            text       : "Seleccione un item",
                            title      : "Alerta",
                            iconClose  : false,
                            dialog     : {
                                resizable : false,
                                draggable : false,
                                buttons   : {
                                    "Aceptar" : function () {
                                    }
                                }
                            }
                        });
                    }
                });
                </g:if>
                <g:else>
                $("#btn_agregarItem").click(function () {
                    $.box({
                        imageClass : "box_info",
                        text       : "Primero guarde el rubro o seleccione uno para editar",
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

                });
                </g:else>
            });
        </script>

    </body>
</html>
