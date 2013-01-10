<%@ page import="janus.pac.ParametroEvaluacion" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Parametro Evaluacions
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
                                    <li id="${par1.id}" rel="categoria">
                                        <a href="">${par1.descripcion}</a>
                                        <ul>
                                            <g:each in="${janus.pac.ParametroEvaluacion.findAllByPadre(par1, [sort: 'orden'])}" var="par2">
                                                <li id="${par2.id}" rel="node1">
                                                    <a href="">${par2.descripcion}</a>
                                                    <ul>
                                                        <g:each in="${janus.pac.ParametroEvaluacion.findAllByPadre(par2, [sort: 'orden'])}" var="par3">
                                                            <li id="${par3.id}" rel="node2">
                                                                <a href="">${par3.descripcion}</a>
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
                            <span class="help-block">Puntaje máximo a obtener.</span>
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
//                console.log(node);

                var nodeId = node.attr("id");
                var nodeRel = node.attr("rel");

                var menuItems = {};

                var nodeHasChildren = node.children("ul").size() > 0;

                if (nodeRel != "node2") {
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

                            btnSave.click(function () {
                                btnSave.replaceWith(spinner);
                                var c = 0;
                                var data = {
                                    id  : "ni_" + $tbody.children("tr").size() + 1,
                                    par : $.trim($par.val()),
                                    pnt : $.trim($pnt.val()),
                                    min : $.trim($min.val()),
                                    ord : ord,
                                    cnc : ${concurso.id}
                                };
//                                console.log(nodeId)
                                if (node.attr("rel") != "root") {
                                    data.pdr = nodeId;
                                }

                                var msg = "";
                                if (data.par == "") {
                                    c++;
                                    msg += "<li id='err_parametro'>Ingrese el parámetro</li>";
                                    $par.addClass("inputError");
                                }
                                if (data.pnt == "") {
                                    c++;
                                    msg += "<li id='err_puntaje'>Ingrese el puntaje</li>";
                                    $pnt.addClass("inputError");
                                }
                                if (data.min == "") {
                                    data.min = 0;
                                    $min.val(0);
                                }

                                if (msg == "") {
//                                    console.log(data);
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(action:'addParametro')}",
                                        data    : data,
                                        success : function (msg) {
//                                            console.log(msg);
                                            var parts = msg.split("_");
                                            if (parts[0] == "OK") {
                                                $tree.jstree("create_node", node, ord, {
                                                    attr : {
                                                        id : parts[1]
                                                    },
                                                    data : data.par
                                                });
                                                $tree.jstree("open_node", node);
                                                reset();
                                                updateInfo();
                                            }
                                        }
                                    });
                                } else {
                                    $("#ttlAlert").text("Se encontr" + (c == 1 ? "ó el siguiente error" : "aron los siguientes errores"));
                                    $("#msgAlert").html("<ul>" + msg + "</ul>");
                                    $("#alert").show();
                                }
                                $("#modal-Form").modal("hide");

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
//                console.log(menuItems);
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
                        $(this).removeClass("inputError");
                        $("#err_" + $(this).attr("id")).remove();
                        if ($("#msgAlert").find("li").size() == 0) {
                            $("#alert").hide();
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
