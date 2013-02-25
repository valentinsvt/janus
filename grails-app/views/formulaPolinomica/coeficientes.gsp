<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Ajuste de la fórmula polinómica y cuadrilla tipo
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree', file: 'jquery.jstree.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree', file: 'jstreegrid.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree/_lib', file: 'jquery.cookie.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.dataTables.min.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/css', file: 'jquery.dataTables.css')}" rel="stylesheet">

        <link href="${resource(dir: 'css', file: 'tree.css')}" rel="stylesheet"/>

        <style type="text/css">
        #tree {
            width      : 100%;
            background : none;
            border     : none;
        }

        .area {
            /*width      : 400px;*/
            height : 750px;
            /*background : #fffaf0;*/
            /*display    : none;*/
        }

        .left, .right {
            height     : 750px;
            float      : left;
            overflow-x : hidden;
            overflow-y : auto;
            border     : 1px solid #E2CBA1;
        }

        .left {
            width : 465px;
            /*background : #8a2be2;*/
        }

        .right {
            width       : 685px;
            margin-left : 15px;
            /*background  : #6a5acd;*/
        }

        .jstree-grid-cell {
            cursor : pointer;
        }

        .editable {
            background : #98A8B5 !important;
        }

        .selected {
            background : #A4CCEA !important;
        }

        .hovered {
            background : #C4E5FF;
        }

        .table-hover tbody tr:hover td,
        .table-hover tbody tr:hover th {
            background-color : #C4E5FF !important;
            cursor           : pointer;
        }

        table.dataTable tr.odd.selected td.sorting_1, table.dataTable tr.even.selected td.sorting_1 {
            background : #88AFCC !important;
        }

        .jstree-apple a {
            border-radius : 0 !important;
        }

        </style>

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

        <div class="tituloTree">
            Coeficientes de la fórmula polinómica de la obra: ${obra.descripcion + " (" + obra.codigo + ")"}
        </div>

        <div class="btn-toolbar" style="margin-top: 15px;">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn " title="Regresar a la obra">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </a>
            </div>

            <div class="btn-group" data-toggle="buttons-radio">
                <g:link action="coeficientes" id="${obra.id}" params="[tipo: 'p']" class="btn btn-info ${tipo == 'p' ? 'active' : ''} btn-tab">
                    <i class="icon-cogs"></i>
                    Fórmula polinómica
                </g:link>
                <g:link action="coeficientes" id="${obra.id}" params="[tipo: 'c']" class="btn btn-info  ${tipo == 'c' ? 'active' : ''} btn-tab">
                    <i class="icon-group"></i>
                    Cuadrilla Tipo
                </g:link>
            </div>

        </div>

        <div id="list-grupo" class="span12" role="main" style="margin-top: 5px;margin-left: 0;">

            <div class="area ui-corner-all" id="formula">

                <div id="formulaLeft" class="left ui-corner-left">
                    <div id="tree"></div>
                </div>

                <div id="formulaRight" class="right ui-corner-right">
                    <div id="rightContents" class="hide">
                        <div class="btn-toolbar" style="margin-left: 10px; margin-bottom:0;">
                            <div class="btn-group">
                                <a href="#" id="btnAgregarItems" class="btn btn-success disabled">
                                    <i class="icon-plus"></i>
                                    Agregar a <span id="spanCoef"></span> <span id="spanSuma" data-total="0"></span>
                                </a>
                                <a href="#" id="btnRemoveSelection" class="btn disabled">
                                    Quitar selección
                                </a>
                            </div>
                        </div>
                    </div>

                    <table class="table table-condensed table-bordered table-hover" id="tblDisponibles">
                        <thead>
                            <tr>
                                <th>Item</th>
                                <th>Descripción</th>
                                <th>Aporte</th>
                            </tr>
                        </thead>
                        <tbody>
                            <g:each in="${rows}" var="r">
                                <tr data-item="${r.iid}" data-codigo="${r.codigo}" data-nombre="${r.item}" data-valor="${r.aporte ?: 0}">
                                    <td>
                                        ${r.codigo}
                                    </td>
                                    <td>
                                        ${r.item}
                                    </td>
                                    <td class="numero">
                                        <g:formatNumber number="${r.aporte ?: 0}" maxFractionDigits="3" minFractionDigits="3" format="##,###0" locale='ec'/>
                                    </td>
                                </tr>
                            </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="modal hide fade" id="modal-formula">
            <div class="modal-header" id="modalHeader-formula">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle-formula"></h3>
            </div>

            <div class="modal-body" id="modalBody-formula">
            </div>

            <div class="modal-footer" id="modalFooter-formula">
            </div>
        </div>

        <script type="text/javascript">

            var $tree = $("#tree");
            var $tabla = $("#tblDisponibles");

            var icons = {
                edit   : "${resource(dir: 'images/tree', file: 'edit.png')}",
                delete : "${resource(dir: 'images/tree', file: 'delete.gif')}",

                fp : "${resource(dir: 'images/tree', file: 'boxes.png')}",
                it : "${resource(dir: 'images/tree', file: 'box.png')}"
            };

            function updateCoef($row) {
                $("#spanCoef").text($.trim($row.attr("numero")) + ": " + $.trim($row.attr("nombre")));
            }
            function updateTotal(val) {
                $("#spanSuma").text("(" + number_format(val, 3, ".", ",") + ")").data("total", val);
            }

            function treeSelection($item) {
                var $parent = $item.parent();
                var strId = $parent.attr("id");
                var parts = strId.split("_");

                var tipo = parts[0];
                var index = $parent.index();
                var numero = $parent.attr("numero");

                var $seleccionados = $("a.selected, div.selected, a.editable, div.editable");

                if (tipo == 'fp') {
                    //padres
                    %{--console.log("${tipo}", index, "${tipo}" == 'p', index == 0, "${tipo}" == 'p' && index == 0);--}%
//                    if (index) { //el primero (p01) de la formula no es seleccionable (el de cuadrilla tipo si es)
                    if ("${tipo}" == 'p' && index == 0) { //el primero (p01) de la formula no es seleccionable (el de cuadrilla tipo si es)
//                        console.log("true");
                        $seleccionados.removeClass("selected editable");
                        $parent.children("a, .jstree-grid-cell").addClass("editable parent");
                    } else {
//                        console.log("false");
                        $seleccionados.removeClass("selected editable");
                        $parent.children("a, .jstree-grid-cell").addClass("selected editable parent");
                        updateCoef($item.parents("li"));
                    }
                } else if (tipo == 'it') {
                    //hijos AQUI
                    $seleccionados.removeClass("selected editable");
                    $parent.children("a, .jstree-grid-cell").addClass("editable child");
                    var $upper = $parent.parent().parent();
                    if ($upper.index() > 0) {
                        $seleccionados.removeClass("selected");
                        $upper.children("a, .jstree-grid-cell").addClass("selected editable parent");
                        updateCoef($upper);
                    } else {
                        $seleccionados.removeClass("selected");
                        $upper.children("a, .jstree-grid-cell").addClass("editable parent");
                    }
                }
            }

            function treeNodeEvents($items) {
                $items.bind({
                    mouseenter : function (e) {
                        var $parent = $(this).parent();
                        $parent.children("a, .jstree-grid-cell").addClass("hovered");
                    },
                    mouseleave : function (e) {
                        $(".hovered").removeClass("hovered");
                    },
                    click      : function (e) {
                        treeSelection($(this));
                    }
                });
            }

            function createContextmenu(node) {
                var parent = node.parent().parent();

                var nodeStrId = node.attr("id");
                var nodeText = $.trim(node.attr("nombre"));

                var parentStrId = parent.attr("id");
                var parentText = $.trim(parent.attr("nombre"));

                var nodeTipo = node.attr("rel");

                var parentTipo = parent.attr("rel");

                var parts = nodeStrId.split("_");
                var nodeId = parts[1];

                parts = parentStrId.split("_");
                var parentId = parts[1];

                var nodeHasChildren = node.hasClass("hasChildren");
                var cantChildren = node.children("ul").children().size();

                var menuItems = {}, lbl = "", item = "";

                var num = $.trim(node.attr("numero"));
                var hijos = node.children("ul").length;

                switch (nodeTipo) {
                    case "fp":
                        var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                        var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                        btnSave.click(function () {
                            var indice = $("#indice").val();
                            var valor = $.trim($("#valor").val());

                            var indiceNombre = $("#indice option:selected").text();

                            if (valor != "") {
                                btnSave.replaceWith(spinner);
//                                console.log("SI!!");
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action: 'guardarGrupo')}",
                                    data    : {
                                        id     : nodeId,
                                        indice : indice,
                                        valor  : valor
                                    },
                                    success : function (msg) {
                                        if (msg == "OK") {
                                            node.attr("nombre", indiceNombre).trigger("change_node.jstree");
                                            node.attr("valor", valor).trigger("change_node.jstree");
                                            $("#modal-formula").modal("hide");
                                        }
                                    }
                                });
                            } else {
//                                console.log("NO");
                            }
                        });

                        menuItems.editar = {
                            label            : "Editar",
                            separator_before : false,
                            separator_after  : false,
                            icon             : icons.edit,
                            action           : function (obj) {
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action: 'editarGrupo')}",
                                    data    : {
                                        id : nodeId
                                    },
                                    success : function (msg) {
                                        $("#modalTitle-formula").html("Editar grupo");
                                        $("#modalBody-formula").html(msg);
                                        $("#modalFooter-formula").html("").append(btnCancel).append(btnSave);
                                        $("#modal-formula").modal("show");
                                    }
                                });
                            }
                        };
                        %{--if (hijos == 0 && num != "p01" && num != "p02" && num != "px" && num != "c01") {--}%
                            %{--menuItems.eliminar = {--}%
                                %{--label            : "Eliminar",--}%
                                %{--separator_before : false,--}%
                                %{--separator_after  : false,--}%
                                %{--icon             : icons.delete,--}%
                                %{--action           : function (obj) {--}%
                                    %{--$.box({--}%
                                        %{--imageClass : "box_info",--}%
                                        %{--text       : "Está seguro de eliminar el coeficiente " + num + " " + nodeText + "?",--}%
                                        %{--title      : "Confirmación",--}%
                                        %{--iconClose  : false,--}%
                                        %{--dialog     : {--}%
                                            %{--resizable     : false,--}%
                                            %{--draggable     : false,--}%
                                            %{--closeOnEscape : false,--}%
                                            %{--buttons       : {--}%
                                                %{--"Aceptar"  : function () {--}%
                                                    %{--$.ajax({--}%
                                                        %{--type    : "POST",--}%
                                                        %{--url     : "${createLink(action:'delCoefFormula')}",--}%
                                                        %{--data    : {--}%
                                                            %{--obra : "${obra.id}",--}%
                                                            %{--id   : nodeId--}%
                                                        %{--},--}%
                                                        %{--success : function (msg) {--}%
                                                            %{--$("#tree").jstree('delete_node', $("#" + nodeStrId));--}%
                                                        %{--}--}%
                                                    %{--});--}%
                                                %{--},--}%
                                                %{--"Cancelar" : function () {--}%
                                                %{--}--}%
                                            %{--}--}%
                                        %{--}--}%
                                    %{--});--}%
                                %{--}--}%
                            %{--};--}%
                        %{--}--}%
                        break;
                    case "it":
                        var nodeCod = node.attr("numero");
                        var nodeDes = node.attr("nombre");
                        var nodeValor = node.attr("valor");
                        var nodeItem = node.attr("item");

                        /*** Selecciona el nodo y su padre ***/
                        var $seleccionados = $("a.selected, div.selected, a.editable, div.editable");
                        $seleccionados.removeClass("selected editable");
                        node.children("a, .jstree-grid-cell").addClass("editable child");
                        $seleccionados.removeClass("selected");
                        node.parent().parent().children("a, .jstree-grid-cell").addClass("selected editable parent");
                        /*** Fin Selecciona el nodo y su padre ***/

                        menuItems.delete = {
                            label            : "Eliminar",
                            separator_before : false,
                            separator_after  : false,
                            icon             : icons.delete,
                            action           : function (obj) {
                                $.box({
                                    imageClass : "box_info",
                                    text       : "Está seguro de eliminar " + nodeText + " del grupo " + parentText + "?",
                                    title      : "Confirmación",
                                    iconClose  : false,
                                    dialog     : {
                                        resizable     : false,
                                        draggable     : false,
                                        closeOnEscape : false,
                                        buttons       : {
                                            "Aceptar"  : function () {
                                                $.ajax({
                                                    type    : "POST",
                                                    url     : "${createLink(action:'delItemFormula')}",
                                                    data    : {
                                                        tipo : nodeTipo,
                                                        id   : nodeId
                                                    },
                                                    success : function (msg) {
                                                        var msgParts = msg.split("_");
                                                        if (msgParts[0] == "OK") {
                                                            $("#tree").jstree('delete_node', $("#" + nodeStrId));
                                                            var tr = $("<tr>");
                                                            var tdItem = $("<td>").append(nodeCod);
                                                            var tdDesc = $("<td>").append(nodeDes);
                                                            var tdApor = $("<td class='numero'>").append(number_format(nodeValor, 3, '.', ''));
                                                            tr.append(tdItem).append(tdDesc).append(tdApor);
                                                            tr.data({
                                                                valor  : nodeValor,
                                                                nombre : nodeDes,
                                                                codigo : nodeCod,
                                                                item   : nodeItem
                                                            });
                                                            tr.click(function () {
                                                                clickTr(tr);
                                                            });
                                                            $("#tblDisponibles").children("tbody").prepend(tr);
                                                            tr.show("pulsate");
                                                            parent.attr("valor", msgParts[1]).trigger("change_node.jstree");
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
                        };
                        break;
                }

                return menuItems;
            }

            function clickTr($tr) {
                var $sps = $("#spanSuma");
                var total = parseFloat($sps.data("total"));

                if ($tr.hasClass("selected")) {
                    $tr.removeClass("selected");
                    total -= parseFloat($tr.data("valor"));
                } else {
                    $tr.addClass("selected");
                    total += parseFloat($tr.data("valor"));
                }
                if ($tabla.children("tbody").children("tr.selected").length > 0) {
                    $("#btnRemoveSelection, #btnAgregarItems").removeClass("disabled");
                } else {
                    $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
                }
                updateTotal(total);
            }

            $(function () {

                $("#btnRemoveSelection").click(function () {
                    if (!$(this).hasClass("disabled")) {
                        $tabla.children("tbody").children("tr.selected").removeClass("selected");
                        $("#btnRemoveSelection").addClass("disabled");
                        updateTotal(0);
                        $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
                    }
                    return false;
                });

                $("#btnAgregarItems").click(function () {
                    var $btn = $(this);
                    if (!$btn.hasClass("disabled")) {
                        $btn.after(spinner).hide();
                        var $target = $("a.selected").parent();

                        var total = parseFloat($target.attr("valor"));

                        var rows2add = [];
                        var dataAdd = {
                            formula : $target.attr("id"),
                            items   : []
                        };

                        $tabla.children("tbody").children("tr.selected").each(function () {
                            var data = $(this).data();
                            rows2add.push({add : {attr : {item : data.item, numero : data.codigo, nombre : data.nombre, valor : data.valor}, data : "   "}, remove : $(this)});
                            total += parseFloat(data.valor);
                            dataAdd.items.push(data.item + "_" + data.valor);
                        });

//                        console.log(dataAdd);

                        $.ajax({
                            async   : false,
                            type    : "POST",
                            url     : "${createLink(action:'addItemFormula')}",
                            data    : dataAdd,
                            success : function (msg) {
//                                console.log(msg);
                                var msgParts = msg.split("_");
                                if (msgParts[0] == "OK") {
                                    var insertados = {};
                                    var inserted = msgParts[1].split(",");
                                    for (var i = 0; i < inserted.length; i++) {
                                        var j = inserted[i];
                                        if (j != "") {
                                            var p = j.split(":");
                                            insertados[p[0]] = p[1];
                                        }
                                    }

//                                    console.log("insertados", insertados);
                                    for (i = 0; i < rows2add.length; i++) {
                                        var it = rows2add[i];
                                        var add = it.add;
                                        var rem = it.remove;

                                        add.attr.id = "it_" + insertados[add.attr.item];
//                                        console.log(add.attr.item, add);

                                        $tree.jstree("create_node", $target, "first", add);
                                        if (!$target.hasClass("jstree-open")) {
                                            $('#tree').jstree("open_node", $target);
                                        }
                                        rem.remove();
                                    }
                                }
                            }
                        });

                        $target.find("li").children("a, .jstree-grid-cell").unbind("hover").unbind("click");
                        treeNodeEvents($target.find("li").children("a, .jstree-grid-cell"));

                        $target.attr("valor", number_format(total, 3, ".", ",")).trigger("change_node.jstree");
                        $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
                        updateTotal(0);
                        $btn.show();
                        spinner.remove();
                    }
                    return false;
                });

                $tabla.dataTable({
                    sScrollY        : "655px",
                    bPaginate       : false,
                    bScrollCollapse : true,
                    bFilter         : false,
                    oLanguage       : {
                        sZeroRecords : "No se encontraron datos",
                        sInfo        : "",
                        sInfoEmpty   : ""
                    }
                }).children("tbody").children("tr").click(function () {
                            clickTr($(this));
//                            var $sps = $("#spanSuma");
//                            var total = parseFloat($sps.data("total"));
//
//                            if ($(this).hasClass("selected")) {
//                                $(this).removeClass("selected");
//                                total -= parseFloat($(this).data("valor"));
//                            } else {
//                                $(this).addClass("selected");
//                                total += parseFloat($(this).data("valor"));
//                            }
//                            if ($tabla.children("tbody").children("tr.selected").length > 0) {
//                                $("#btnRemoveSelection, #btnAgregarItems").removeClass("disabled");
//                            } else {
//                                $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
//                            }
//                            updateTotal(total);
                        });

                $tree.bind("loaded.jstree",
                        function (event, data) {
                            var $first = $tree.children("ul").first().children("li").eq(1);
                            $first.children("a, .jstree-grid-cell").addClass("selected");
                            updateCoef($first);
                            updateTotal(0);

                            treeNodeEvents($("#tree").find("a"));
                            treeNodeEvents($(".jstree-grid-cell"));

                            $("#rightContents").show();
                        }).jstree({
                            plugins     : ["themes", "json_data", "grid", "types", "contextmenu", "search", "crrm", "cookies", "types" ],
                            json_data   : {data : ${json.toString()}},
                            themes      : {
                                theme : "apple"
                            },
                            contextmenu : {
                                items : createContextmenu
                            },
                            types       : {
                                valid_children : [ "fp", "it"],
                                types          : {
                                    fp : {
                                        icon           : {
                                            image : icons.fp
                                        },
                                        valid_children : ["it"]
                                    },
                                    it : {
                                        icon           : {
                                            image : icons.it
                                        },
                                        valid_children : [""]
                                    }
                                }
                            },
                            grid        : {
                                columns : [
                                    {
                                        header : "Coef.",
                                        value  : "numero",
                                        title  : "numero",
                                        width  : 80
                                    },
                                    {
                                        header : "Nombre del Indice",
                                        value  : "nombre",
                                        title  : "nombre",
                                        width  : 300
                                    },
                                    {
                                        header : "Valor",
                                        value  : "valor",
                                        title  : "valor",
                                        width  : 70
                                    }
                                ]
                            }
                        });

            });
        </script>

    </body>
</html>