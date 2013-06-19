<%@ page import="janus.pac.ParametroEvaluacion" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Parametro Evaluaciones
        </title>

        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree', file: 'jquery.jstree.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree/_lib', file: 'jquery.cookie.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

        <link href="${resource(dir: 'css', file: 'tree.css')}" rel="stylesheet"/>

        <style type="text/css">
        .lbl {
            font-weight : bold;
        }

        .inputError {
            border : solid 1px #995157 !important;
        }

        .selected, .selected td {
            background : #B5CBD1 !important;
        }

        .orden {

        }

        .numero {
            text-align : right !important;
        }

        .nivel1, .nivel1 td {
            background : #FFD8AD !important;
        }

        .nivel2, .nivel2 td {
            background : #ADE5FF !important;
        }

        .nivel3, .nivel3 td {
            background : #D6FFAD !important;
        }
        </style>

    </head>

    <body>

        <div class="tituloTree">
            Parámetros de evaluación de <span style="font-weight: bold; font-style: italic;">${concurso.objeto}</span>
        </div>

        <g:if test="${flash.message}">
            <div class="row">
                <div class="span12">
                    <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </div>
            </div>
        </g:if>

        <div class="row">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="concurso" action="list" class="btn">
                    <i class="icon-caret-left"></i>
                    Regresar
                </g:link>
            </div>

            <div class="span3" id="busqueda-ParametroEvaluacion"></div>
        </div>

        <div id="list-ParametroEvaluacion" role="main" style="margin-top: 10px;">
            <div id="treeArea" class="hide">
                <div class="ui-corner-all" id="tree">
                    <ul>

                        <li rel="root" id="root" class="root">
                            <a href="">Parámetros de evaluación</a>
                            <ul>
                                <g:each in="${parametroEvaluacionInstanceList}" var="par1">
                                    <g:set var="par1Hijos" value="${janus.pac.ParametroEvaluacion.findAllByPadre(par1, [sort: 'orden'])}"/>
                                    <li id="${par1.id}" rel="categoria" data-asig="${par1Hijos.sum { it.puntaje }}" data-total="${par1.puntaje}">
                                        <a href="">
                                            ${par1.descripcion}
                                            %{--(asignados <span class="asig">${par1Hijos.sum { it.puntaje }}</span> ${par1Hijos.size() > 0 ? 'de' : ''} <span class="total">${par1.puntaje}</span> puntos)--}%
                                        </a>
                                        <ul>
                                            <g:each in="${par1Hijos}" var="par2">
                                                <g:set var="par2Hijos" value="${janus.pac.ParametroEvaluacion.findAllByPadre(par2, [sort: 'orden'])}"/>
                                                <li id="${par2.id}" rel="node1" data-asig="${par2Hijos.sum { it.puntaje }}" data-total="${par2.puntaje}">
                                                    <a href="">
                                                        ${par2.descripcion}
                                                        %{--(asignados <span class="asig">${par2Hijos.sum { it.puntaje }}</span> ${par2Hijos.size() > 0 ? 'de' : ''} <span class="total">${par2.puntaje}</span> puntos)--}%
                                                    </a>
                                                    <ul>
                                                        <g:each in="${par2Hijos}" var="par3">
                                                            <li id="${par3.id}" rel="node2" data-total="${par3.puntaje}">
                                                                <a href="">
                                                                    ${par3.descripcion}
                                                                    %{--(asignados <span class="total">${par3.puntaje}</span> puntos)--}%
                                                                </a>
                                                            </li>
                                                        </g:each>
                                                    </ul>
                                                </li>
                                            </g:each>
                                        </ul>
                                    </li>
                                </g:each>
                            </ul>
                        </li>

                    </ul>
                </div>

                <div id="info" class="ui-corner-all"></div>
            </div>
        </div>

        <div class="modal long hide fade" id="modal-Form">
            <div class="modal-header" id="modalHeader">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
                <div class="well">

                    <div class="alert alert-error hide" id="alert">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        <h5 id="ttlAlert">Se han encontrado los siguientes errores:</h5>
                        <span id="msgAlert"></span>
                    </div>

                    <div class="row">
                        <div class="span2 lbl">Padre</div>

                        <div class="span3 lbl">Parámetro</div>

                        <div class="span2 lbl">Puntaje</div>

                        <div class="span2 lbl">Mínimo</div>
                    </div>

                    <div class="row">
                        <div class="span2" id="divPadre">
                            Ninguno
                        </div>

                        <div class="span3">
                            <g:textArea name="parametro" class="span3"/>
                        </div>

                        <div class="span2">
                            <g:textField name="puntaje" class="span2"/>
                            <span class="help-block">Puntaje máximo a obtener (<span id="spMax"></span>)</span>
                        </div>

                        <div class="span2">
                            <g:textField name="minimo" class="span2"/>
                            <span class="help-block">Puntaje inferior descalifica automáticamente la oferta.</span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

        <div class="modal hide fade" id="modal-del">
            <div class="modal-header" id="modalHeader-del">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle-del"></h3>
            </div>

            <div class="modal-body" id="modalBody-del">
                Está seguro de querer eliminar este parámetro?
            </div>

            <div class="modal-footer" id="modalFooter-del">
            </div>
        </div>

        <script type="text/javascript">
            var $par = $("#parametro"), $pnt = $("#puntaje"), $min = $("#minimo"), $tbody = $("#tbdPar"), $tree = $("#tree");

            var icons = {
                root      : "${resource(dir:'images/tree', file: 'arrow_r.png')}",
                categoria : "${resource(dir:'images/tree', file: 'group_pr.png')}",
                node1     : "${resource(dir:'images/tree', file:'item1_pr.png')}",
                node2     : "${resource(dir:'images/tree', file:'item2_pr.png')}",

                add    : "${resource(dir:'images/tree', file:'plus_16.png')}",
                remove : "${resource(dir:'images/tree', file:'minus_16.png')}"
            };

            function validarNum(ev) {
                /*
                 48-57      -> numeros
                 96-105     -> teclado numerico
                 188        -> , (coma)
                 190        -> . (punto) teclado
                 110        -> . (punto) teclado numerico
                 8          -> backspace
                 46         -> delete
                 9          -> tab
                 37         -> flecha izq
                 39         -> flecha der
                 */
                return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                        (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                        ev.keyCode == 190 || ev.keyCode == 110 ||
                        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                        ev.keyCode == 37 || ev.keyCode == 39);
            }

            function reset() {
                $par.val("");
                $pnt.val("");
                $min.val("");
            }

            function updateInfo() {
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action: 'updateInfo')}",
                    data    : {
                        id : ${concurso.id}
                    },
                    success : function (msg) {
                        $("#info").html(msg);
                    }
                });
            }

            function createContextmenu(node) {
                var nodeId = node.attr("id");
                var nodeRel = node.attr("rel");
                var nodeText = $.trim(node.children("a").text());

                var tot = node.data("total");
                var asg = node.data("asig");
                var dsp = tot - asg;

                var menuItems = {};

                var nodeHasChildren = node.children("ul").size() > 0;

//                ////console.log(nodeRel, dsp);

                if ((nodeRel != "node2" && dsp > 0) || isNaN(dsp)) {
                    var cant = node.children("ul").children("li").size();
                    var ord = cant + 1;
                    menuItems.addChild = {
                        label            : "Agregar Parámetro",
                        separator_before : false, // Insert a separator before the item
                        separator_after  : false, // Insert a separator after the item
                        icon             : icons.add,
                        action           : function (obj) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            if (nodeRel != "root") {
                                $("#divPadre").text(nodeText);
                                $("#spMax").text(dsp + " puntos disponibles de un total de " + tot);
                                $("#puntaje").attr("max", dsp);
                            } else {
                                $("#divPadre").text("Ninguno");
                            }

                            btnSave.click(function () {
                                var c = 0;
                                var data = {
                                    id  : "ni_" + $tbody.children("tr").size() + 1,
                                    par : $.trim($par.val()),
                                    pnt : $.trim($pnt.val()),
                                    min : $.trim($min.val()),
                                    ord : ord,
                                    cnc : ${concurso.id}
                                };
                                if (nodeRel != "root") {
                                    data.pdr = nodeId;
                                }
                                var msg = "";
                                if (data.par == "") {
                                    c++;
                                    msg += "<li class='err_parametro'>Ingrese el parámetro</li>";
                                    $par.addClass("inputError");
                                }
                                if (data.pnt == "") {
                                    c++;
                                    msg += "<li class='err_puntaje'>Ingrese el puntaje</li>";
                                    $pnt.addClass("inputError");
                                }
                                if (parseFloat(data.pnt) > $pnt.attr("max")) {
                                    c++;
                                    msg += "<li class='err_puntaje'>Ingrese un puntaje inferior a " + dsp + "</li>";
                                    $pnt.addClass("inputError");
                                }
                                if (data.min == "") {
                                    data.min = 0;
                                    $min.val(0);
                                }
                                if (data.min > data.pnt) {
                                    c++;
                                    msg += "<li class='err_minimo'>Ingrese un valor inferior a " + data.pnt + "</li>";
                                    $pnt.addClass("inputError");
                                }

                                if (msg == "") {
                                    btnSave.replaceWith(spinner);
//                                    ////console.log(data);
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'addParametro')}",
                                        data    : data,
                                        success : function (msg) {
                                            //                                            ////console.log(msg);
                                            var parts = msg.split("_");
                                            if (parts[0] == "OK") {
                                                $tree.jstree("create_node", node, ord, {
                                                    attr : {
                                                        id           : parts[1],
                                                        "data-total" : data.pnt,
                                                        "data-asig"  : 0
                                                    },
                                                    data : data.par
                                                });
                                                $tree.jstree("open_node", node);
                                                var asig = parseFloat(node.data("asig"));
                                                if (isNaN(asig)) {
                                                    asig = 0;
                                                }
                                                asig += parseFloat(data.pnt);
                                                node.data("asig", asig);
                                                reset();
                                                updateInfo();
                                                $("#modal-Form").modal("hide");
                                            }
                                        }
                                    });
                                } else {
                                    $("#ttlAlert").text("Se encontr" + (c == 1 ? "ó el siguiente error" : "aron los siguientes errores"));
                                    $("#msgAlert").html("<ul>" + msg + "</ul>");
                                    $("#alert").show();
                                }

//                            return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Parámetro");

                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal-Form").modal("show");
                        }
                    };
                }
                if (!nodeHasChildren && nodeRel != "root") {
                    menuItems.removeChild = {
                        label            : "Eliminar Parámetro",
                        separator_before : false, // Insert a separator before the item
                        separator_after  : false, // Insert a separator after the item
                        icon             : icons.remove,
                        action           : function (obj) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnDelete = $('<a href="#"  class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

                            var nodeParent = node.parent().parent();
                            var tot = parseFloat(node.data("total"));
                            var padreAsig = parseFloat(nodeParent.data("asig"));
                            var nT = padreAsig - tot;

                            btnDelete.click(function () {
                                btnDelete.replaceWith(spinner);
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(action:'removeParametro')}",
                                    data    : {
                                        id : nodeId
                                    },
                                    success : function (msg) {
                                        $("#tree").jstree('delete_node', node);
                                        $("#modal-del").modal("hide");
                                        nodeParent.data("asig", nT);
                                        updateInfo();
                                    }
                                });
//                            return false;
                            });

                            $("#modalHeader-del").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle-del").html("Eliminar Parámetro");

                            $("#modalFooter-del").html("").append(btnOk).append(btnDelete);
                            $("#modal-del").modal("show");
                        }
                    };
                }
//                ////console.log(menuItems);
                return menuItems;
            }

            $(function () {
                reset();
                updateInfo();
                $('[rel=tooltip]').tooltip();

                $("#puntaje, #minimo").keydown(function (ev) {
                    if (ev.keyCode == 190 || ev.keyCode == 110) {
                        var val = $(this).val();
                        if (val.length == 0) {
                            $(this).val("0");
                        }
                        return val.indexOf(".") == -1;
                    } else {
                        return validarNum(ev);
                    }
                });
                $("#puntaje, #parametro").keyup(function () {
                    if ($.trim($(this).val()) != "" && $(this).hasClass("inputError")) {
                        if ($(this).attr("id") == "puntaje") {
                            if (parseFloat($(this).val()) <= parseFloat($(this).attr("max"))) {
                                $(this).removeClass("inputError");
                                $(".err_" + $(this).attr("id")).remove();
                                if ($("#msgAlert").find("li").size() == 0) {
                                    $("#alert").hide();
                                }
                            }
                        } else {
                            $(this).removeClass("inputError");
                            $(".err_" + $(this).attr("id")).remove();
                            if ($("#msgAlert").find("li").size() == 0) {
                                $("#alert").hide();
                            }
                        }
                    }
                });

                $tree.bind("loaded.jstree",
                        function (event, data) {
                            $("#loading").hide();
                            $("#treeArea").show();
                        }).jstree({
//                            "core"        : {
//                                "initially_open" : [ id ]
//                            },
                            "plugins"     : ["themes", "html_data", "json_data", "ui", "types", "contextmenu", "search", "cookies", "crrm"/*, "wholerow"*/],
//                            "html_data"   : {
//                                "data" : "<ul type='root'><li id='root' class='root hasChildren jstree-closed' rel='root' ><a href='#' class='label_arbol'>Parámetros de evaluación</a></ul>"
//                            },
                            "types"       : {
                                "valid_children" : [ ""  ],
                                "types"          : {
                                    "root"      : {
                                        "icon"           : {
                                            "image" : icons.root
                                        },
                                        "valid_children" : [ "categoria" ]
                                    },
                                    "categoria" : {
                                        "icon"           : {
                                            "image" : icons.categoria
                                        },
                                        "valid_children" : [ "node1" ]
                                    },
                                    "node1"     : {
                                        "icon"           : {
                                            "image" : icons.node1
                                        },
                                        "valid_children" : [ "node2" ]
                                    },
                                    "node2"     : {
                                        "icon"           : {
                                            "image" : icons.node2
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
                                    "type"   : "POST",
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
                        })

            });

        </script>

    </body>
</html>
