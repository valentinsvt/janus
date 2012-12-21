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

        <script src="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/js', file: 'jquery.dataTables.min.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/DataTables-1.9.4/media/css', file: 'jquery.dataTables.css')}" rel="stylesheet">

        <link href="${resource(dir: 'css', file: 'tree.css')}" rel="stylesheet"/>

        <style type="text/css">
        #tree {
            width      : auto;
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
                                        <g:formatNumber number="${r.aporte ?: 0}" maxFractionDigits="3" minFractionDigits="3"/>
                                    </td>
                                </tr>
                            </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <script type="text/javascript">

            function updateCoef($row) {
                $("#spanCoef").text($.trim($row.attr("numero")) + ": " + $.trim($row.attr("nombre")));
            }
            function updateTotal(val) {
                $("#spanSuma").text("(" + number_format(val, 3, ".", ",") + ")").data("total", val);
            }

            function treeSelection($item) {
                $("a.selected, div.selected").removeClass("selected");
                $item.parent().children("a, .jstree-grid-cell").addClass("selected");
                updateCoef($item.parents("li"));
            }

            $(function () {

                var $tree = $("#tree");
                var $tabla = $("#tblDisponibles");

                $("#btnRemoveSelection").click(function () {
                    if (!$(this).hasClass("disabled")) {
                        $tabla.children("tbody").children("tr.selected").removeClass("selected");
                        $("#btnRemoveSelection").addClass("disabled");
                    }
                    return false;
                });

                $("#btnAgregarItems").click(function () {
                    if (!$(this).hasClass("disabled")) {
                        var $target = $("a.selected").parent();

                        var total = parseFloat($target.attr("valor"));

                        var rows2add = [];
                        var dataAdd = {
                            formula : $target.attr("id"),
                            items   : []
                        };

                        $tabla.children("tbody").children("tr.selected").each(function () {
                            var data = $(this).data();
                            rows2add.push({add : {attr : {id : "it_" + data.item, numero : "", nombre : data.nombre, valor : data.valor}, data : "   "}, remove : $(this)});
                            total += parseFloat(data.valor);
                            dataAdd.items.push(data.item);
//                        $tree.jstree("create_node", $target, "first", {attr : {id : "it_" + data.item, numero : "", nombre : data.nombre, valor : data.valor}, data : "   "});
//                        if (!$target.hasClass("jstree-open")) {
//                            $('#tree').jstree("open_node", $target);
//                        }
//                        $(this).remove();
                        });

                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'addItemFormula')}",
                            data    : dataAdd,
                            success : function (msg) {
                                console.log(msg);
                                for (var i = 0; i < rows2add.length; i++) {
                                    var it = rows2add[i];
                                    var add = it.add;
                                    var rem = it.remove;

                                    $tree.jstree("create_node", $target, "first", add);
                                    if (!$target.hasClass("jstree-open")) {
                                        $('#tree').jstree("open_node", $target);
                                    }
                                    rem.remove();
                                }
                            }
                        });

                        $target.attr("valor", number_format(total, 3, ".", ",")).trigger("change_node.jstree");
                        $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
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
                            var $sps = $("#spanSuma");
                            var total = parseFloat($sps.data("total"));

                            if ($(this).hasClass("selected")) {
                                $(this).removeClass("selected");
                                total -= parseFloat($(this).data("valor"));
                            } else {
                                $(this).addClass("selected");
                                total += parseFloat($(this).data("valor"));
                            }
                            if ($tabla.children("tbody").children("tr.selected").length > 0) {
                                $("#btnRemoveSelection, #btnAgregarItems").removeClass("disabled");
                            } else {
                                $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
                            }
                            updateTotal(total);
                        });

                $tree.bind("loaded.jstree",
                        function (event, data) {
                            var $first = $tree.children("ul").first().children("li").first();
                            $first.children("a, .jstree-grid-cell").addClass("selected");
                            updateCoef($first);
                            updateTotal(0);

                            $("#tree").find("a").hover(function () {
                                $(this).addClass("hovered");
                                $(this).siblings(".jstree-grid-cell").addClass("hovered");
                            },function () {
                                $(".hovered").removeClass("hovered");
                            }).click(function () {
                                        treeSelection($(this));
//                                        $("a.selected, div.selected").removeClass("selected");
//                                        $(this).addClass("selected");
//                                        $(this).siblings(".jstree-grid-cell").addClass("selected");
//                                        updateCoef($(this).parents("li"));
                                    });
                            $(".jstree-grid-cell").hover(function () {
                                $(this).addClass("hovered");
                                $(this).siblings(".jstree-grid-cell").addClass("hovered");
                                $(this).siblings("a").addClass("hovered");
                            },function () {
                                $(".hovered").removeClass("hovered");
                            }).click(function () {
                                        treeSelection($(this));
//                                        $("a.selected, div.selected").removeClass("selected");
//                                        $(this).addClass("selected");
//                                        $(this).siblings(".jstree-grid-cell").addClass("selected");
//                                        $(this).siblings("a").addClass("selected");
//                                        updateCoef($(this).parents("li"));
                                    });
                            $("#rightContents").show();
                        }).jstree({
                            plugins   : ["themes", "json_data", "grid", "types", "contextmenu", "search", "crrm" ],
                            json_data : {data : ${json.toString()}},
                            themes    : {
                                theme : "apple"
                            },
                            grid      : {
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
                                        width  : 314
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