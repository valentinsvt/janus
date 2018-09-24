<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <title>Grupos de Rubros</title>

    <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree', file: 'jquery.jstree.js')}"></script>
    %{--<script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree/_lib', file: 'jquery.cookie.js')}"></script>--}%

    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'custom-methods.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.css')}" rel="stylesheet"/>
    <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.customThemes.css')}" rel="stylesheet"/>

    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet"/>

    <g:if test="${janus.Parametros.findByEmpresaLike(message(code: 'ambiente2'))}">
        <link href="${resource(dir: 'css', file: 'treeV2.css')}" rel="stylesheet"/>
    </g:if>
    <g:else>
        <link href="${resource(dir: 'css', file: 'tree.css')}" rel="stylesheet"/>
    </g:else>

</head>

<body>

<g:if test="${flash.message}">
    <div class="span12">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>


<div id="loading" style="text-align:center;">
    <img src="${resource(dir: 'images', file: 'spinner_24.gif')}" alt="Cargando..."/>

    <p>Cargando... Por favor espere.</p>
</div>


<div id="treeArea" class="hide">
    <form class="form-search" style="width: 500px;">
        <div class="input-append">
            <input type="text" class="input-medium search-query" id="search"/>
            <a href='#' class='btn' id="btnSearch"><i class='icon-zoom-in'></i> Buscar</a>
        </div>
        <span id="cantRes"></span>
        <input type="button" class="btn pull-right" value="Cerrar todo" onclick="$('#tree').jstree('close_all');">
    </form>

    %{--<div class="btn-group">--}%
    %{--<input type="button" class="btn" value="Cerrar todo" onclick="$('#tree').jstree('close_all');">--}%
    %{--<input type="button" class="btn" value="Abrir todo" onclick="$('#tree').jstree('open_all');">--}%
    %{--</div>--}%

    <div id="tree" class="ui-corner-all"></div>

    <div id="info" class="ui-corner-all"></div>
</div>

<div class="modal longModal hide fade" id="modal-tree">
    <div class="modal-header" id="modalHeader">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>


<div class="modal large hide fade " id="modal-transporte" style="overflow: hidden;">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modal_trans_title">
            Variables de transporte
        </h3>
    </div>

    <div class="modal-body" id="modal_trans_body">
        <div class="row-fluid">

            <div class="span2">
                Lista de precios: MO y Equipos
            </div>

            <div class="span3">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=6')}" optionKey="id"
                    optionValue="descripcion" id="ciudad" style="width: 170px"/>
            </div>

            <div class="span1">
                Fecha
            </div>

            <div class="span2">
                <elm:datepicker name="item.fecha" class="" style="width: 90px;" id="fecha_precios" value="${new java.util.Date()}" format="dd-MM-yyyy"/>
            </div>

            <div class="span2" style="width: 120px;">
                % costos indirectos
            </div>

            <div class="span1">
                <input type="text" style="width: 30px;" id="costo_indi" value="22.5">
            </div>
        </div>
        <hr style="margin: 5px 0 10px 0;"/>

        <div class="row-fluid">
            <div class="span1">
                Volquete
            </div>

            <div class="span3" style="margin-left: 10px;">
                <g:select style="width: 165px;" name="volquetes" from="${volquetes2}" optionKey="id" optionValue="nombre" id="cmb_vol" noSelection="${['-1': 'Seleccione']}" value="${aux.volquete.id}"/>
            </div>

            <div class="span1" style="width: 35px; margin-left: 5px;">
                Costo
            </div>

            <div class="span1" style="margin-left: 5px; width: 90px;">
                <input type="text" style="width: 69px;text-align: right" disabled="" id="costo_volqueta">
            </div>

            <div class="span1" style="margin-left: 5px;">
                Chofer
            </div>

            <div class="span3" style="margin-left: 5px;">
                <g:select style="width: 165px;" name="volquetes" from="${choferes}" optionKey="id" optionValue="nombre" id="cmb_chof" noSelection="${['-1': 'Seleccione']}" value="${aux.chofer.id}"/>
            </div>

            <div class="span1" style="width: 35px;margin-left: 5px;">
                Costo
            </div>

            <div class="span1" style="margin-left: 5px; width: 90px;">
                <input type="text" style="width: 69px;text-align: right" disabled="" id="costo_chofer">
            </div>
        </div>

        <div class="row-fluid" style="border-bottom: 1px solid black;margin-bottom: 10px">
            <div class="span6">
                <b>Distancia peso</b>
                %{--<input type="text" style="width: 50px;" id="dist_peso" value="0.00">--}%
            </div>

            <div class="span5" style="margin-left: 30px;">
                <b>Distancia volumen</b>
                %{--<input type="text" style="width: 50px;" id="dist_vol" value="0.00">--}%
            </div>
        </div>

        <div class="row-fluid">
            <div class="span2">
                Canton
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_p1" value="10.00">
            </div>

            <div class="span4">
                Materiales Petreos Hormigones
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_v1" value="20.00">
            </div>

        </div>

        <div class="row-fluid">
            <div class="span2">
                Especial
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_p2" value="10.00">
            </div>

            <div class="span4">
                Materiales Mejoramiento
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_v2" value="20.00">
            </div>
        </div>

        <div class="row-fluid">
            <div class="span5">

            </div>

            <div class="span4">
                Materiales Carpeta Asfáltica
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_v3" value="20.00">
            </div>
        </div>

        <div class="row-fluid" style="border-bottom: 1px solid black;margin-bottom: 10px">
            <div class="span6">
                <b>Listas de precios</b>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span1">
                Cantón
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=1')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_1"/>
            </div>

            <div class="span3">
                Petreos Hormigones
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=3')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_3"/>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span1">
                Especial
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=2')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_2"/>
            </div>

            <div class="span3">
                Mejoramiento
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=4')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_4"/>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span5"></div>

            <div class="span3">
                Carpeta Asfáltica
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=5')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_5"/>
            </div>
        </div>
    </div>

    <div class="modal-footer" id="modal_trans_footer">
        <g:hiddenField name="nodeId" val=""/>
        <g:hiddenField name="nodeGrupo" val=""/>
        <a href= "#" data-dismiss="modal" class="btn btn-primary" id="print_totales" data-transporte="true"><i class="icon-print"></i> Consolidado</a>
        <a href="#" data-dismiss="modal" class="btn btn-primary btnPrint" data-transporte="si"><i class="icon-print"></i> Con </br>transporte</a>
        <a href="#" data-dismiss="modal" class="btn btn-primary btnPrint" data-transporte="no"><i class="icon-print"></i> Sin </br>transporte</a>
        <a href="#" data-dismiss="modal" class="btn btn-primary btnPrintVae" data-transporte="si"><i class="icon-print"></i> VAE con </br>transporte</a>
        <a href="#" data-dismiss="modal" class="btn btn-primary btnPrintVae" data-transporte="no"><i class="icon-print"></i> VAE sin </br>transporte</a>
        <a href="#" data-dismiss="modal" class="btn" id="btnCancel">Cancelar</a>
    </div>
</div>



<div class="modal large hide fade " id="modal-transporte2" style="overflow: hidden;">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modal_trans_title2">
            Variables de transporte para el Grupo
        </h3>
    </div>

    <div class="modal-body" id="modal_trans_body2">
        <div class="row-fluid">

            <div class="span2">
                Lista de precios: MO y Equipos
            </div>

            <div class="span3">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=6')}" optionKey="id"
                          optionValue="descripcion" id="ciudad" style="width: 170px"/>
            </div>

            <div class="span1">
                Fecha
            </div>

            <div class="span2">
                <elm:datepicker name="item.fecha" class="" style="width: 90px;" id="fecha_precios2" value="${new java.util.Date()}"
                                format="dd-MM-yyyy"/>
            </div>

            <div class="span2" style="width: 120px;">
                % costos indirectos
            </div>

            <div class="span1">
                <input type="text" style="width: 30px;" id="costo_indi2" value="22.5">
            </div>
        </div>
        <hr style="margin: 5px 0 10px 0;"/>

        <div class="row-fluid">
            <div class="span1">
                Volquete
            </div>

            <div class="span3" style="margin-left: 10px;">
                <g:select style="width: 165px;" name="volquetes" from="${volquetes2}" optionKey="id" optionValue="nombre" id="cmb_vol2" noSelection="${['-1': 'Seleccione']}" value="${aux.volquete.id}"/>
            </div>

            <div class="span1" style="width: 35px; margin-left: 5px;">
                Costo
            </div>

            <div class="span1" style="margin-left: 5px; width: 90px;">
                <input type="text" style="width: 69px;text-align: right" disabled="" id="costo_volqueta2">
            </div>

            <div class="span1" style="margin-left: 5px;">
                Chofer
            </div>

            <div class="span3" style="margin-left: 5px;">
                <g:select style="width: 165px;" name="volquetes" from="${choferes}" optionKey="id" optionValue="nombre" id="cmb_chof2" noSelection="${['-1': 'Seleccione']}" value="${aux.chofer.id}"/>
            </div>

            <div class="span1" style="width: 35px;margin-left: 5px;">
                Costo
            </div>

            <div class="span1" style="margin-left: 5px; width: 90px;">
                <input type="text" style="width: 69px;text-align: right" disabled="" id="costo_chofer2">
            </div>
        </div>

        <div class="row-fluid" style="border-bottom: 1px solid black;margin-bottom: 10px">
            <div class="span6">
                <b>Distancia peso</b>
                %{--<input type="text" style="width: 50px;" id="dist_peso" value="0.00">--}%
            </div>

            <div class="span5" style="margin-left: 30px;">
                <b>Distancia volumen</b>
                %{--<input type="text" style="width: 50px;" id="dist_vol" value="0.00">--}%
            </div>
        </div>

        <div class="row-fluid">
            <div class="span2">
                Canton
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_p1g" value="10.00">
            </div>

            <div class="span4">
                Materiales Petreos Hormigones
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_v1g" value="20.00">
            </div>

        </div>

        <div class="row-fluid">
            <div class="span2">
                Especial
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_p2g" value="10.00">
            </div>

            <div class="span4">
                Materiales Mejoramiento
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_v2g" value="20.00">
            </div>
        </div>

        <div class="row-fluid">
            <div class="span5">

            </div>

            <div class="span4">
                Materiales Carpeta Asfáltica
            </div>

            <div class="span3">
                <input type="text" style="width: 50px;" id="dist_v3g" value="20.00">
            </div>
        </div>

        <div class="row-fluid" style="border-bottom: 1px solid black;margin-bottom: 10px">
            <div class="span6">
                <b>Listas de precios</b>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span1">
                Cantón
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=1')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_1g"/>
            </div>

            <div class="span3">
                Petreos Hormigones
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=3')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_3g"/>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span1">
                Especial
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=2')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_2g"/>
            </div>

            <div class="span3">
                Mejoramiento
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=4')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_4g"/>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span5"></div>

            <div class="span3">
                Carpeta Asfáltica
            </div>

            <div class="span4">
                <g:select name="item.ciudad.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=5')}" optionKey="id" optionValue="descripcion" class="span10" id="lista_5g"/>
            </div>
        </div>
    </div>

    <div class="modal-footer" id="modal_trans_footer2">
        <g:hiddenField name="nodeId" val=""/>
        <g:hiddenField name="nodeGrupo" val=""/>
        <a href= "#" data-dismiss="modal" class="btn btn-primary" id="imp_consolidado" data-transporte="true"><i class="icon-print"></i>Consolidado</a>
        <a href= "#" data-dismiss="modal" class="btn btn-primary" id="imp_consolidado_excel" data-transporte="true"><i class="icon-table"></i> Consolidado Excel</a>
        <a href="#" data-dismiss="modal" class="btn" id="btnCancel">Cancelar</a>
    </div>
</div>



<script type="text/javascript">

    $.jGrowl.defaults.closerTemplate = '<div>[ cerrar todo ]</div>';

    var btn = $("<a href='#' class='btn' id='btnSearch'><i class='icon-zoom-in'></i> Buscar</a>");
    var urlSp = "${resource(dir: 'images', file: 'spinner.gif')}";
    var sp = $('<span class="add-on" id="btnSearch"><img src="' + urlSp + '"/></span>');

    var current = "1";

    var icons = {
        edit   : "${resource(dir: 'images/tree', file: 'edit.png')}",
        delete : "${resource(dir: 'images/tree', file: 'delete.gif')}",
        info   : "${resource(dir: 'images/tree', file: 'info.png')}",
        print  : "${resource(dir: 'images/tree', file: 'print.png')}",

        grupo        : "${resource(dir: 'images/tree', file: 'grupo_material.png')}",
        subgrupo     : "${resource(dir: 'images/tree', file: 'subgrupo_material.png')}",
        departamento : "${resource(dir: 'images/tree', file: 'departamento_material.png')}",
        rubro        : "${resource(dir: 'images/tree', file: 'item_material.png')}"
    };

    function log(msg, error) {
        var sticky = false;
        var theme = "success";
        if (error) {
            sticky = true;
            theme = "error";
        }
        $.jGrowl(msg, {
            speed          : 'slow',
            sticky         : sticky,
            theme          : theme,
            closerTemplate : '<div>[ cerrar todos ]</div>',
            themeState     : ''
        });
    }

    function getPrecios($cmb, $txt, $fcha) {
        if ($cmb.val() != "-1") {
//            var datos = "fecha=" + $("#fecha_precios").val() + "&ciudad=" + $("#ciudad").val() + "&ids=" + $cmb.val();
            var datos = "fecha=" + $fcha.val() + "&ciudad=" + $("#ciudad").val() + "&ids=" + $cmb.val();
            $.ajax({
                type    : "POST",
                url     : "${g.createLink(controller: 'rubro',action:'getPreciosTransporte')}",
                data    : datos,
                success : function (msg) {
                    var precios = msg.split("&");
                    for (var i = 0; i < precios.length; i++) {
                        var parts = precios[i].split(";");
                        if (parts.length > 1) {
                            $txt.val(parts[1].trim());
                        }
                    }
                }
            });
        } else {
            $txt.val("0.00");
        }
    }

    function showInfo() {
        var node = $.jstree._focused().get_selected();
        var parent = node.parent().parent();

        var nodeStrId = node.attr("id");
        var nodeText = $.trim(node.children("a").text());

        var nodeRel = node.attr("rel");
        var parts = nodeRel.split("_");
        var nodeNivel = parts[0];
        var nodeTipo = parts[1];

        parts = nodeStrId.split("_");
        var nodeId = parts[1];

        var url = "";

        switch (nodeNivel) {
            case "root":
                break;
            case "grupo":
                url = "${createLink(action:'showGr_ajax')}";
                break;
            case "subgrupo":
                url = "${createLink(action:'showSg_ajax')}";
                break;
            case "departamento":
                url = "${createLink(action:'showDp_ajax')}";
                break;
            case "rubro":
                url = "${createLink(action:'showRb_ajax')}";
                break;
        }
        if (url != "") {
            $.ajax({
                type    : "POST",
                url     : url,
                data    : {
                    id : nodeId
                },
                success : function (msg) {
                    $("#info").html(msg);
                }
            });
        }
    }

    function imprimir(params) {
        $("#nodeId").val(params.id);
        if(params.grupo) {
            $("#nodeGrupo").val(params.grupo);
        } else {
            $("#nodeGrupo").val();
        }
        var obj = {
            label            : params.label,
            separator_before : params.sepBefore, // Insert a separator before the item
            separator_after  : params.sepAfter, // Insert a separator after the item
            icon             : params.icon,
            action           : function (obj) {
                $("#modal-transporte").modal("show");
            }
        };
        return obj;
    }

    function imprimirConsolidado(params) {
        $("#nodeId").val(params.id);
        if(params.grupo) {
            $("#nodeGrupo").val(params.grupo);
        } else {
            $("#nodeGrupo").val();
        }
        var obj = {
            label            : params.label,
            separator_before : params.sepBefore, // Insert a separator before the item
            separator_after  : params.sepAfter, // Insert a separator after the item
            icon             : params.icon,
            action           : function (obj) {
                $("#modal-transporte2").modal("show");
                $("#fecha_precios2").change();
            }
        };
        return obj;
    }

    function createUpdate(params) {
        var obj = {
            label            : params.label,
            separator_before : params.sepBefore, // Insert a separator before the item
            separator_after  : params.sepAfter, // Insert a separator after the item
            icon             : params.icon,
            action           : function (obj) {
                $.ajax({
                    type    : "POST",
                    url     : params.url,
                    data    : params.data,
                    success : function (msg) {
                        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                        var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                        btnSave.click(function () {
                            if ($("#frmSave").valid()) {
                                btnSave.replaceWith(spinner);
                                var url = $("#frmSave").attr("action");
                                $.ajax({
                                    type    : "POST",
                                    url     : url,
                                    data    : $("#frmSave").serialize(),
                                    success : function (msg) {
                                        var parts = msg.split("_");
                                        if (parts[0] == "OK") {
                                            if (params.action == "create") {
                                                if (params.open) {
                                                    $("#" + params.nodeStrId).removeClass("jstree-leaf").addClass("jstree-closed");
                                                    $('#tree').jstree("open_node", $("#" + params.nodeStrId));
                                                }
                                                $('#tree').jstree("create_node", $("#" + params.nodeStrId), params.where, {attr : {id : params.tipo + "_" + parts[2]}, data : parts[3]});
                                                $("#modal-tree").modal("hide");
                                                log(params.log + parts[3] + " creado correctamente");
                                            } else if (params.action == "update") {
                                                $("#tree").jstree('rename_node', $("#" + params.nodeStrId), parts[3]);
                                                $("#modal-tree").modal("hide");
                                                log(params.log + parts[3] + " editado correctamente");
                                                showInfo();
                                            }
                                        } else {
                                            $("#modal-tree").modal("hide");
                                            log("Ha ocurrido el siguiente error: " + parts[1], true);
                                        }
                                    }
                                });
                            }
//                                            $("#frmSave").submit();
                            return false;
                        });
                        if (params.action == "create") {
                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                        } else if (params.action == "update") {
                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-edit");
                        }
                        $("#modalTitle").html(params.title);
                        $("#modalBody").html(msg);
                        $("#modalFooter").html("").append(btnOk).append(btnSave);
                        $("#modal-tree").modal("show");
                    }
                });
            }
        };
        return obj;
    }

    function remove(params) {
        var obj = {
            label            : params.label,
            separator_before : params.sepBefore, // Insert a separator before the item
            separator_after  : params.sepAfter, // Insert a separator after the item
            icon             : params.icon,
            action           : function (obj) {

                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                var btnSave = $('<a href="#"  class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');
                $("#modalHeader").removeClass("btn-edit btn-show btn-delete").addClass("btn-delete");
                $("#modalTitle").html(params.title);
                $("#modalBody").html("<p>Está seguro de querer eliminar este " + params.confirm + "?</p>");
                $("#modalFooter").html("").append(btnOk).append(btnSave);
                $("#modal-tree").modal("show");

                btnSave.click(function () {
                    btnSave.replaceWith(spinner);
                    $.ajax({
                        type    : "POST",
                        url     : params.url,
                        data    : params.data,
                        success : function (msg) {
                            var parts = msg.split("_");
                            if (parts[0] == "OK") {
                                $("#tree").jstree('delete_node', $("#" + params.nodeStrId));
                                $("#modal-tree").modal("hide");
                                log(params.log + " eliminado correctamente");
                                if ($("#" + params.parentStrId).children("ul").children().size() == 0) {
                                    $("#" + params.parentStrId).removeClass("hasChildren");
                                }
                            } else {
                                $("#modal-tree").modal("hide");
                                log("Ha ocurrido un error al eliminar", true);
                            }
                        }
                    });
                    return false;
                });
            }
        };
        return obj;
    }

    function createContextmenu(node) {
        var parent = node.parent().parent();

        var nodeStrId = node.attr("id");
        var nodeText = $.trim(node.children("a").text());

        var parentStrId = parent.attr("id");
        var parentText = $.trim(parent.children("a").text());

        var nodeRel = node.attr("rel");
        var parts = nodeRel.split("_");
        var nodeNivel = parts[0];
        var nodeTipo = parts[1];

        var parentRel = parent.attr("rel");
        parts = nodeRel.split("_");
        var parentNivel = parts[0];
        var parentTipo = parts[1];

        parts = nodeStrId.split("_");
        var nodeId = parts[1];

        parts = parentStrId.split("_");
        var parentId = parts[1];

        var nodeHasChildren = node.hasClass("hasChildren");
        var cantChildren = node.children("ul").children().size();
        nodeHasChildren = nodeHasChildren || cantChildren != 0;

        var menuItems = {}, lbl = "", item = "";

        switch (nodeTipo) {
            case "material":
                lbl = "o material";
                item = "Material";
                break;
            case "manoObra":
                lbl = "a mano de obra";
                item = "Mano de obra";
                break;
            case "equipo":
                lbl = "o equipo";
                item = "Equipo";
                break;
        }

//                ////console.log(nodeNivel);

        switch (nodeNivel) {
            case "root":

                menuItems.crearHijo = createUpdate({
                    action    : "create",
                    label     : "Nuevo Solicitante",
                    icon      : icons.grupo,
//                    icon      : icons[nodeRel],
                    sepBefore : false,
                    sepAfter  : false,
                    url       : "${createLink(action:'formGr_ajax')}",
                    data      : {
                        grupo : nodeId
                    },
                    open      : false,
                    nodeStrId : nodeStrId,
                    where     : "first",
                    tipo      : "sg",
                    log       : "Grupo ",
                    title     : "Nuevo Solicitante"
                });

                break;
            case "grupo":
                menuItems.editar = createUpdate({
                    action    : "update",
                    label     : "Editar Solicitante",
                    icon      : icons.edit,
                    sepBefore : false,
                    sepAfter  : false,
                    url       : "${createLink(action:'formGr_ajax')}",
                    data      : {
                        grupo : parentId,
                        id    : nodeId
                    },
                    open      : false,
                    nodeStrId : nodeStrId,
                    log       : "Grupo ",
                    title     : "Editar Solicitante"
                });
                if (!nodeHasChildren) {
                    menuItems.eliminar = remove({
                        label       : "Eliminar Solicitante",
                        sepBefore   : false,
                        sepAfter    : false,
                        icon        : icons.delete,
                        title       : "Eliminar Solicitante",
                        confirm     : "grupo",
                        url         : "${createLink(action:'deleteGr_ajax')}",
                        data        : {
                            id : nodeId
                        },
                        nodeStrId   : nodeStrId,
                        parentStrId : parentStrId,
                        log         : "Grupo "
                    });
                }
                menuItems.crearHermano = createUpdate({
                    action    : "create",
                    label     : "Nuevo Solicitante",
                    icon      : icons[nodeRel],
                    sepBefore : true,
                    sepAfter  : true,
                    url       : "${createLink(action:'formGr_ajax')}",
                    data      : {
                        grupo : parentId
                    },
                    open      : false,
                    nodeStrId : nodeStrId,
                    where     : "after",
                    tipo      : "sg",
                    log       : "Grupo ",
                    title     : "Nuevo Solicitante"
                });
                menuItems.crearHijo = createUpdate({
                    action    : "create",
                    label     : "Nuevo Grupo",
                    sepBefore : false,
                    sepAfter  : false,
                    icon      : icons.subgrupo,
                    url       : "${createLink(action:'formSg_gr_ajax')}",
                    data      : {
                        grupo : nodeId
                    },
                    open      : true,
                    nodeStrId : nodeStrId,
                    where     : "first",
                    tipo      : "sg",
                    log       : "Subgrupo ",
                    title     : "Nuevo Grupo"
                });
                menuItems.print = imprimirConsolidado({
                    id        : nodeStrId,
                    label     : "Imprimir rubros del grupo",
                    sepBefore : true,
                    sepAfter  : false,
                    icon      : icons.print
                });

                break;

            case "subgrupo":
                menuItems.editar = createUpdate({
                    action    : "update",
                    label     : "Editar Grupo",
                    icon      : icons.edit,
                    sepBefore : false,
                    sepAfter  : false,
                    url       : "${createLink(action:'formSg_gr_ajax')}",
                    data      : {
                        grupo : parentId,
                        id    : nodeId
                    },
                    open      : false,
                    nodeStrId : nodeStrId,
                    log       : "Subgrupo ",
                    title     : "Editar Grupo"
                });
                if (!nodeHasChildren) {
                    menuItems.eliminar = remove({
                        label       : "Eliminar Grupo",
                        sepBefore   : false,
                        sepAfter    : false,
                        icon        : icons.delete,
                        title       : "Eliminar Grupo",
                        confirm     : "grupo",
                        url         : "${createLink(action:'deleteSg_ajax')}",
                        data        : {
                            id : nodeId
                        },
                        nodeStrId   : nodeStrId,
                        parentStrId : parentStrId,
                        log         : "Subgrupo "
                    });
                }
                menuItems.crearHermano = createUpdate({
                    action    : "create",
                    label     : "Nuevo Grupo",
                    icon      : icons[nodeRel],
                    sepBefore : true,
                    sepAfter  : true,
                    url       : "${createLink(action:'formSg_gr_ajax')}",
                    data      : {
                        grupo : parentId
                    },
                    open      : false,
                    nodeStrId : nodeStrId,
                    where     : "after",
                    tipo      : "sg",
                    log       : "Subgrupo ",
                    title     : "Nuevo Grupo"
                });
                menuItems.crearHijo = createUpdate({
                    action    : "create",
                    label     : "Nuevo subgrupo",
                    sepBefore : false,
                    sepAfter  : false,
//                    icon      : icons["departamento_" + nodeTipo],
                    icon      : icons.departamento,
                    url       : "${createLink(action:'formDp_gr_ajax')}",
                    data      : {
                        subgrupo : nodeId
                    },
                    open      : true,
                    nodeStrId : nodeStrId,
                    where     : "first",
                    tipo      : "dp",
                    log       : "Departamento ",
                    title     : "Nuevo subgrupo"
                });
                menuItems.print = imprimir({
                    id        : nodeStrId,
                    label     : "Imprimir rubros subgrupo",
                    sepBefore : true,
                    sepAfter  : false,
                    icon      : icons.print
                });
                break;
            case "departamento":

                menuItems.editar = createUpdate({
                    action    : "update",
                    label     : "Editar subgrupo",
                    icon      : icons.edit,
                    sepBefore : false,
                    sepAfter  : false,
                    url       : "${createLink(action:'formDp_gr_ajax')}",
                    data      : {
                        subgrupo : parentId,
                        id       : nodeId
                    },
                    open      : false,
                    nodeStrId : nodeStrId,
                    log       : "Departamento ",
                    title     : "Editar subgrupo"
                });
                if (!nodeHasChildren) {
                    menuItems.eliminar = remove({
                        label       : "Eliminar subgrupo",
                        sepBefore   : false,
                        sepAfter    : false,
                        icon        : icons.delete,
                        title       : "Eliminar subgrupo",
                        confirm     : "departamento",
                        url         : "${createLink(action:'deleteDp_ajax')}",
                        data        : {
                            id : nodeId
                        },
                        nodeStrId   : nodeStrId,
                        parentStrId : parentStrId,
                        log         : "Departamento "
                    });
                }
                menuItems.crearHermano = createUpdate({
                    action    : "create",
                    label     : "Nuevo subgrupo",
                    sepBefore : true,
                    sepAfter  : true,
                    icon      : icons[nodeRel],
                    url       : "${createLink(action:'formDp_gr_ajax')}",
                    data      : {
                        subgrupo : parentId
                    },
                    open      : false,
                    nodeStrId : nodeStrId,
                    where     : "after",
                    tipo      : "dp",
                    log       : "Departamento ",
                    title     : "Nuevo subgrupo"
                });
                menuItems.print = imprimir({
                    id        : nodeStrId,
                    label     : "Imprimir capítulo",
                    sepBefore : true,
                    sepAfter  : false,
                    icon      : icons.print
                });
                break;
            case "rubro":
                menuItems.print = imprimir({
                    id        : nodeStrId,
                    label     : "Imprimir rubro",
                    sepBefore : true,
                    sepAfter  : false,
                    icon      : icons.print
                });
                break;

        }

        return menuItems;
    }

    function initTree() {
        var id, rel, label;
        id = "root";
        rel = "root";
        label = "Dirección Responsable";

        $("#tree").bind("loaded.jstree",
                function (event, data) {
                    $("#loading").hide();
                    $("#treeArea").show();
                }).jstree({
                    "core"        : {
                        "initially_open" : [ id ]
                    },
                    "plugins"     : ["themes", "html_data", "json_data", "ui", "types", "contextmenu", "search", "crrm"/*, "dnd"/*, "wholerow"*/],
                    "html_data"   : {
                        "data" : "<ul type='root'><li id='" + id + "' class='root hasChildren jstree-closed' rel='" + rel + "' ><a href='#' class='label_arbol'>" + label + "</a></ul>",
                        "ajax" : {
                            "url"   : "${createLink(action: 'loadTreePart')}",
                            "data"  : function (n) {
                                var obj = $(n);
                                var id = obj.attr("id");
                                var parts = id.split("_");
                                id = 0;
                                if (parts.length > 1) {
                                    id = parts[1]
                                }
                                var tipo = obj.attr("rel");
                                return {id : id, tipo : tipo}
                            },
                            success : function (data) {

                            },
                            error   : function (data) {
                                ////////console.log("error");
                                ////////console.log(data);
                            }
                        }
                    },
                    "types"       : {
                        "valid_children" : [ "root"  ],
                        "types"          : {

                            "root" : {

                                "icon"           : {
                                    "image" : icons.root
                                },
                                "valid_children" : [ "grupo" ]
                            },

                            "grupo"        : {
                                "icon"           : {
                                    "image" : icons.grupo
                                },
                                "valid_children" : [ "subgrupo" ]
                            },
                            "subgrupo"     : {
                                "icon"           : {
                                    "image" : icons.subgrupo
                                },
                                "valid_children" : [ "departamento" ]
                            },
                            "departamento" : {
                                "icon"           : {
                                    "image" : icons.departamento
                                },
                                "valid_children" : [ "rubro" ]
                            },
                            "rubro"        : {
                                "icon"           : {
                                    "image" : icons.rubro
                                },
                                "valid_children" : [ "" ]
                            }
                        }
                    },
                    "themes"      : {
                        "theme" : "default"
                    },
                    "search"      : {
                        "case_insensitive" : true,
                        "ajax"             : {
                            "url"    : "${createLink(action:'searchTree_ajax')}",
                            "data"   : function () {
                                return { search : this.data.search.str, tipo : current }
                            },
                            complete : function () {
                                $("#btnSearch").replaceWith(btn);
                                btn.click(function () {
                                    doSearch();
                                });
                            }
                        }
                    },
                    "contextmenu" : {
                        select_node : true,
                        "items"     : createContextmenu
                    }, //contextmenu
                    "ui"          : {
                        "select_limit" : 1
                    }
                }).bind("search.jstree",function (e, data) {
                    var cant = data.rslt.nodes.length;
                    var search = data.rslt.str;
                    $("#cantRes").html("<b>" + cant + "</b> resultado" + (cant == 1 ? "" : "s"));
                    if (cant > 0) {
                        var container = $('#tree'), scrollTo = $('.jstree-search').first();
                        container.animate({
                            scrollTop : scrollTo.offset().top - container.offset().top + container.scrollTop()
                        }, 2000);
                    }
                }).bind("select_node.jstree", function (NODE, REF_NODE) {
                    showInfo();
                });

    }

    function doSearch() {
        var val = $.trim($("#search").val());
        if (val != "") {
            $("#btnSearch").replaceWith(sp);
            $("#tree").jstree("search", val);
        }
    }

    $(function () {
        $("#search").val("");

        initTree();
        $("#fecha_precios").change(function(){
            $("#cmb_vol").change()
            $("#cmb_chof").change()
        })
        $("#fecha_precios2").change(function(){
            $("#cmb_vol2").change()
            $("#cmb_chof2").change()
        })

        $("#cmb_vol").change(function () {
            getPrecios($("#cmb_vol"), $("#costo_volqueta"), $("#fecha_precios"));
        });
        $("#cmb_vol2").change(function () {
            getPrecios($("#cmb_vol2"), $("#costo_volqueta2"), $("#fecha_precios2"));
        });

        $("#cmb_chof").change(function () {
            getPrecios($("#cmb_chof"), $("#costo_chofer"), $("#fecha_precios"));
        });
        $("#cmb_chof2").change(function () {
            getPrecios($("#cmb_chof2"), $("#costo_chofer2"), $("#fecha_precios2"));
        });

        getPrecios($("#cmb_chof"), $("#costo_chofer"), $("#fecha_precios"));
        getPrecios($("#cmb_vol"), $("#costo_volqueta"), $("#fecha_precios"));

        $("#btnSearch").click(function () {
            doSearch();
        });
        $("#search").keyup(function (ev) {
            if (ev.keyCode == 13) {
                doSearch();
            }
        });

        $(".btnPrint").click(function () {

            var dsp0 = $("#dist_p1").val();
            var dsp1 = $("#dist_p2").val();
            var dsv0 = $("#dist_v1").val();
            var dsv1 = $("#dist_v2").val();
            var dsv2 = $("#dist_v3").val();
            var listas = $("#lista_1").val() + "," + $("#lista_2").val() + "," + $("#lista_3").val() + "," + $("#lista_4").val() + "," + $("#lista_5").val() + "," + $("#ciudad").val();
            var volqueta = $("#costo_volqueta").val();
            var chofer = $("#costo_chofer").val();
            var trans = $(this).data("transporte");
            var nodeId = $("#nodeId").val();

            var datos = "dsp0=" + dsp0 + "Wdsp1=" + dsp1 + "Wdsv0=" + dsv0 + "Wdsv1=" + dsv1 + "Wdsv2=" + dsv2 + "Wprvl=" + volqueta + "Wprch=" + chofer + "Wfecha=" + $("#fecha_precios").val() + "Wid=" + nodeId + "Wlugar=" + $("#ciudad").val() + "Wlistas=" + listas + "Wchof=" + $("#cmb_chof").val() + "Wvolq=" + $("#cmb_vol").val() + "Windi=" + $("#costo_indi").val() + "Wtrans=" + trans;
            var url = "${g.createLink(controller: 'reportes3',action: 'imprimirRubros')}?" + datos;
            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url + "&filename=rubros_" + "${new Date().format('ddMMyyyy_hhmm')}" + ".pdf";

            $("#modal-transporte").modal("hide");
            return false;
        });


        $(".btnPrintVae").click(function () {

            var dsp0 = $("#dist_p1").val();
            var dsp1 = $("#dist_p2").val();
            var dsv0 = $("#dist_v1").val();
            var dsv1 = $("#dist_v2").val();
            var dsv2 = $("#dist_v3").val();
            var listas = $("#lista_1").val() + "," + $("#lista_2").val() + "," + $("#lista_3").val() + "," + $("#lista_4").val() + "," + $("#lista_5").val() + "," + $("#ciudad").val();
            var volqueta = $("#costo_volqueta").val();
            var chofer = $("#costo_chofer").val();
            var trans = $(this).data("transporte");
            var nodeId = $("#nodeId").val();

            var datos = "dsp0=" + dsp0 + "Wdsp1=" + dsp1 + "Wdsv0=" + dsv0 + "Wdsv1=" + dsv1 + "Wdsv2=" + dsv2 + "Wprvl=" + volqueta + "Wprch=" + chofer + "Wfecha=" + $("#fecha_precios").val() + "Wid=" + nodeId + "Wlugar=" + $("#ciudad").val() + "Wlistas=" + listas + "Wchof=" + $("#cmb_chof").val() + "Wvolq=" + $("#cmb_vol").val() + "Windi=" + $("#costo_indi").val() + "Wtrans=" + trans;
            var url = "${g.createLink(controller: 'reportes3',action: 'imprimirRubrosVae')}?" + datos;
            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url + "&filename=rubros_" + "${new Date().format('ddMMyyyy_hhmm')}" + ".pdf";

            $("#modal-transporte").modal("hide");
            return false;
        });
        $("#print_totales").click(function () {
            var dsp0 = $("#dist_p1").val();
            var dsp1 = $("#dist_p2").val();
            var dsv0 = $("#dist_v1").val();
            var dsv1 = $("#dist_v2").val();
            var dsv2 = $("#dist_v3").val();
            var listas = $("#lista_1").val() + "," + $("#lista_2").val() + "," + $("#lista_3").val() + "," + $("#lista_4").val() + "," + $("#lista_5").val() + "," + $("#ciudad").val();
            var volqueta = $("#costo_volqueta").val();
            var chofer = $("#costo_chofer").val();
            var trans = $(this).data("transporte");
            var nodeId = $("#nodeId").val();
            var principal = false;

            var datos = "dsp0=" + dsp0 + "Wdsp1=" + dsp1 + "Wdsv0=" + dsv0 + "Wdsv1=" + dsv1 + "Wdsv2=" + dsv2 +
                    "Wprvl=" + volqueta + "Wprch=" + chofer + "Wfecha=" + $("#fecha_precios").val() + "Wid=" + nodeId +
                    "Wlugar=" + $("#ciudad").val() + "Wlistas=" + listas + "Wchof=" + $("#cmb_chof").val() +
                    "Wvolq=" + $("#cmb_vol").val() + "Windi=" + $("#costo_indi").val() + "Wprincipal=" + principal +"Wtrans=" + trans;
            var url = "${g.createLink(controller: 'reportes2',action: 'imprimirRubrosConsolidado')}?" + datos;
            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url + "&filename=rubros_" + "${new Date().format('ddMMyyyy_hhmm')}" + ".pdf";

            $("#modal-transporte").modal("hide");
            return false;
        });


        $("#imp_consolidado").click(function () {
            var dsp0 = $("#dist_p1g").val();
            var dsp1 = $("#dist_p2g").val();
            var dsv0 = $("#dist_v1g").val();
            var dsv1 = $("#dist_v2g").val();
            var dsv2 = $("#dist_v3g").val();
            var lista1 = $("#lista_1g").val();
            var lista2 = $("#lista_2g").val();
            var lista3 = $("#lista_3g").val();
            var lista4 = $("#lista_4g").val();
            var lista5 = $("#lista_5g").val();
            var lista6 = $("#ciudad").val();
            var volqueta = $("#costo_volqueta2").val();
            var chofer = $("#costo_chofer2").val();
            var trans = $(this).data("transporte");
            var nodeId = $("#nodeId").val();
            var principal = true;

            var datos = "dsp0=" + dsp0 + "Wdsp1=" + dsp1 + "Wdsv0=" + dsv0 + "Wdsv1=" + dsv1 + "Wdsv2=" + dsv2 +
                    "Wprvl=" + volqueta + "Wprch=" + chofer + "Wfecha=" + $("#fecha_precios2").val() + "Wid=" + nodeId +
                    "Wlugar=" + $("#ciudad").val() + "Wlista1=" + lista1 + "Wlista2=" + lista2 + "Wlista3=" + lista3 +
                    "Wlista4=" + lista4 + "Wlista5=" + lista5 + "Wlista6=" + lista6 + "Wprincipal=" + principal
                    + "Wchof=" + $("#cmb_chof").val() +
                    "Wvolq=" + $("#cmb_vol").val() + "Windi=" + $("#costo_indi2").val() + "Wtrans=" + trans;
            var url = "${g.createLink(controller: 'reportes2',action: 'imprimirRubrosConsolidado2')}?" + datos;
            location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url + "&filename=rubros_" + "${new Date().format('ddMMyyyy_hhmm')}" + ".pdf";
            $("#modal-transporte2").modal("hide");
            return false;
        });


        $("#imp_consolidado_excel").click(function () {

//            var dsp0 = $("#dist_p1").val();
//            var dsp1 = $("#dist_p2").val();
//            var dsv0 = $("#dist_v1").val();
//            var dsv1 = $("#dist_v2").val();
//            var dsv2 = $("#dist_v3").val();
//            var lista1 = $("#lista_1").val();
//            var lista2 = $("#lista_2").val();
//            var lista3 = $("#lista_3").val();
//            var lista4 = $("#lista_4").val();
//            var lista5 = $("#lista_5").val();
//            var lista6 = $("#ciudad").val();
//            var volqueta = $("#costo_volqueta").val();
//            var chofer = $("#costo_chofer").val();
//            var trans = $(this).data("transporte");
//            var nodeId = $("#nodeId").val();
//            var principal = true;


            var dsp0 = $("#dist_p1g").val();
            var dsp1 = $("#dist_p2g").val();
            var dsv0 = $("#dist_v1g").val();
            var dsv1 = $("#dist_v2g").val();
            var dsv2 = $("#dist_v3g").val();
            var lista1 = $("#lista_1g").val();
            var lista2 = $("#lista_2g").val();
            var lista3 = $("#lista_3g").val();
            var lista4 = $("#lista_4g").val();
            var lista5 = $("#lista_5g").val();
            var lista6 = $("#ciudad").val();
            var volqueta = $("#costo_volqueta2").val();
            var chofer = $("#costo_chofer2").val();
            var trans = $(this).data("transporte");
            var nodeId = $("#nodeId").val();
            var principal = true;



            var datos = "dsp0=" + dsp0 + "&dsp1=" + dsp1 + "&dsv0=" + dsv0 + "&dsv1=" + dsv1 + "&dsv2=" + dsv2 +
                    "&prvl=" + volqueta + "&prch=" + chofer + "&fecha=" + $("#fecha_precios2").val() + "&id=" + nodeId +
                    "&lugar=" + $("#ciudad").val() + "&lista1=" + lista1 + "&lista2=" + lista2 + "&lista3=" + lista3 +
                    "&lista4=" + lista4 + "&lista5=" + lista5 + "&lista6=" + lista6 + "&principal=" + principal
                    + "&chof=" + $("#cmb_chof").val() +
                    "&volq=" + $("#cmb_vol").val() + "&indi=" + $("#costo_indi2").val() + "&trans=" + trans;
           location.href = "${g.createLink(controller: 'reportes2',action: 'consolidadoExcel')}?" + datos;
            $("#modal-transporte2").modal("hide");
            return false;
        });




        var cache = {};
        $("#search").autocomplete({
            minLength : 3,
            source    : function (request, response) {
                var term = request.term;
                if (term in cache) {
                    response(cache[ term ]);
                    return;
                }

                $.ajax({
                    type     : "POST",
                    dataType : 'json',
                    url      : "${createLink(action: 'search_ajax')}",
                    data     : {
                        search : term,
                        tipo   : current
                    },
                    success  : function (data) {
                        $("#search").removeClass("ui-autocomplete-loading-error");
                        cache[ term ] = data;
                        response(data);
                    }, error : function () {
                        $("#search").removeClass("ui-autocomplete-loading").addClass("ui-autocomplete-loading-error");
                    }
                });

            }
        });

    });
</script>

</body>
</html>
