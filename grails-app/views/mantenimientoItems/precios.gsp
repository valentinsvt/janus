<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 11/6/12
  Time: 3:01 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Precios y mantenimiento de items</title>

        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree', file: 'jquery.jstree.js')}"></script>
        %{--<script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jstree/_lib', file: 'jquery.cookie.js')}"></script>--}%

        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.css')}" rel="stylesheet"/>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.customThemes.css')}" rel="stylesheet"/>

        <style type="text/css">
        .btn-group {
            margin-bottom : 10px;
        }

        #tree, #info {
            float      : left;
            overflow-y : auto;
            height     : 720px;
            width      : 500px;
            background : #CEE2E8;
            border     : solid 2px #6AA8BA;
        }

        #info {
            margin-left  : 15px;
            width        : 617px;
            height       : 690px;
            border-color : #DC6816;
            background   : #F2DFD2;
            padding      : 15px;
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

        <div class="span12 btn-group" data-toggle="buttons-radio">
            <a href="#" id="1" class="btn btn-info toggle active">
                <i class="icon-briefcase"></i>
                Materiales <!--grpo--><!--sbgr -> Grupo--><!--dprt -> Subgrupo--><!--item-->
            </a>
            <a href="#" id="2" class="btn btn-info toggle">
                <i class="icon-group"></i>
                Mano de obra
            </a>
            <a href="#" id="3" class="btn btn-info toggle">
                <i class="icon-truck"></i>
                Equipos
            </a>
        </div>

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

            <div class="span12">
                <div class="btn-group pull-left" data-toggle="buttons-checkbox">
                    <a href="#" id="all" class="btn toggleTipo">
                        Ver todas las listas
                    </a>
                    <a href="#" id="ignore" class="btn toggleTipo">
                        Ignorar lugar
                    </a>
                </div>

                <div class="btn-group pull-left">
                    <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                        <span id="spFecha">
                            Todas las fechas
                        </span>
                        <span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a href="#" class="fecha" id="todasFechas" data-fecha='false'>
                                Todas las fechas
                            </a>
                        </li>
                        <li>
                            <a href="#" class="fecha" id="fechaIgual" data-fecha='true'>
                                Fecha igual
                            </a>
                        </li>
                        <li>
                            <a href="#" class="fecha" id="fechaMenor" data-fecha='true'>
                                Hasta la fecha
                            </a>
                        </li>
                    </ul>
                </div>

                <div class="pull-left hide" id="divFecha">
                    <elm:datepicker name="fecha" class="input-small"/>
                </div>
            </div>

            <div id="tree" class="ui-corner-all"></div>

            <div id="info" class="ui-corner-all"></div>
        </div>

        <div class="modal hide fade" id="modal-tree">
            <div class="modal-header" id="modalHeader">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

        <script type="text/javascript">

            $.jGrowl.defaults.closerTemplate = '<div>[ cerrar todo ]</div>';

            var btn = $("<a href='#' class='btn' id='btnSearch'><i class='icon-zoom-in'></i> Buscar</a>");
            var urlSp = "${resource(dir: 'images', file: 'spinner.gif')}";
            var sp = $('<span class="add-on" id="btnSearch"><img src="' + urlSp + '"/></span>');

            var current = "1";
            var showLugar = {
                all    : false,
                ignore : false,
                fecha  : "all"
            };

            var icons = {
                edit   : "${resource(dir: 'images/tree', file: 'edit.png')}",
                delete : "${resource(dir: 'images/tree', file: 'delete.gif')}",

                grupo_material : "${resource(dir: 'images/tree', file: 'grupo_material.png')}",
                grupo_manoObra : "${resource(dir: 'images/tree', file: 'grupo_manoObra.png')}",
                grupo_equipo   : "${resource(dir: 'images/tree', file: 'grupo_equipo.png')}",

                subgrupo_material : "${resource(dir: 'images/tree', file: 'subgrupo_material.png')}",
                subgrupo_manoObra : "${resource(dir: 'images/tree', file: 'subgrupo_manoObra.png')}",
                subgrupo_equipo   : "${resource(dir: 'images/tree', file: 'subgrupo_equipo.png')}",

                departamento_material : "${resource(dir: 'images/tree', file: 'departamento_material.png')}",
                departamento_manoObra : "${resource(dir: 'images/tree', file: 'departamento_manoObra.png')}",
                departamento_equipo   : "${resource(dir: 'images/tree', file: 'departamento_equipo.png')}",

                item_material : "${resource(dir: 'images/tree', file: 'item_material.png')}",
                item_manoObra : "${resource(dir: 'images/tree', file: 'item_manoObra.png')}",
                item_equipo   : "${resource(dir: 'images/tree', file: 'item_equipo.png')}",

                lugar_c   : "${resource(dir: 'images/tree', file: 'lugar_c.png')}",
                lugar_v   : "${resource(dir: 'images/tree', file: 'lugar_v.png')}",
                lugar_all : "${resource(dir: 'images/tree', file: 'lugar_all.png')}"
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

            function initTree(tipo) {
                var id, rel, label;
                switch (tipo) {
                    case "1":
                        id = "materiales_1";
                        rel = "grupo_material";
                        label = "Materiales";
                        break;
                    case "2":
                        id = "manoObra_2";
                        rel = "grupo_manoObra";
                        label = "Mano de obra";
                        break;
                    case "3":
                        id = "equipos_3";
                        rel = "grupo_equipo";
                        label = "Equipos";
                        break;
                }
                $("#tree").bind("loaded.jstree",
                        function (event, data) {
                            $("#loading").hide();
                            $("#treeArea").show();
                        }).jstree({
                            "core"      : {
                                "initially_open" : [ id ]
                            },
                            "plugins"   : ["themes", "html_data", "json_data", "ui", "types", "contextmenu", "search", "crrm"/*, "wholerow"*/],
                            "html_data" : {
                                "data" : "<ul type='root'><li id='" + id + "' class='root hasChildren jstree-closed' rel='" + rel + "' ><a href='#' class='label_arbol'>" + label + "</a></ul>",
                                "ajax" : {
                                    "type"  : "POST",
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
                                        return {
                                            id      : id,
                                            tipo    : tipo,
                                            precios : true,
                                            all     : showLugar.all,
                                            ignore  : showLugar.ignore
                                        }
                                    },
                                    success : function (data) {

                                    },
                                    error   : function (data) {
                                        ////console.log("error");
                                        ////console.log(data);
                                    }
                                }
                            },
                            "types"     : {
                                "valid_children" : [ "grupo_material", "grupo_manoObra", "grupo_equipo"  ],
                                "types"          : {
                                    "grupo_material" : {
                                        "icon"           : {
                                            "image" : icons.grupo_material
                                        },
                                        "valid_children" : [ "subgrupo_material" ]
                                    },
                                    "grupo_manoObra" : {
                                        "icon"           : {
                                            "image" : icons.grupo_manoObra
                                        },
                                        "valid_children" : [ "subgrupo_manoObra" ]
                                    },
                                    "grupo_equipo"   : {
                                        "icon"           : {
                                            "image" : icons.grupo_equipo
                                        },
                                        "valid_children" : [ "subgrupo_equipo" ]
                                    },

                                    "subgrupo_manoObra" : {
                                        "icon"           : {
                                            "image" : icons.subgrupo_manoObra
                                        },
                                        "valid_children" : [ "departamento_manoObra" ]
                                    },
                                    "subgrupo_material" : {
                                        "icon"           : {
                                            "image" : icons.subgrupo_material
                                        },
                                        "valid_children" : [ "departamento_material" ]
                                    },
                                    "subgrupo_equipo"   : {
                                        "icon"           : {
                                            "image" : icons.subgrupo_equipo
                                        },
                                        "valid_children" : [ "departamento_equipo" ]
                                    },

                                    "departamento_manoObra" : {
                                        "icon"           : {
                                            "image" : icons.departamento_manoObra
                                        },
                                        "valid_children" : [ "item_manoObra" ]
                                    },
                                    "departamento_material" : {
                                        "icon"           : {
                                            "image" : icons.departamento_material
                                        },
                                        "valid_children" : [ "item_material" ]
                                    },
                                    "departamento_equipo"   : {
                                        "icon"           : {
                                            "image" : icons.departamento_equipo
                                        },
                                        "valid_children" : [ "item_equipo" ]
                                    },

                                    "item_manoObra" : {
                                        "icon"           : {
                                            "image" : icons.item_manoObra
                                        },
                                        "valid_children" : [ "lugar_c, lugar_v, lugar_all" ]
                                    },
                                    "item_material" : {
                                        "icon"           : {
                                            "image" : icons.item_material
                                        },
                                        "valid_children" : [ "lugar_c, lugar_v, lugar_all" ]
                                    },
                                    "item_equipo"   : {
                                        "icon"           : {
                                            "image" : icons.item_equipo
                                        },
                                        "valid_children" : [ "lugar_c, lugar_v, lugar_all" ]
                                    },

                                    "lugar_c"   : {
                                        "icon"           : {
                                            "image" : icons.lugar_c
                                        },
                                        "valid_children" : [ "" ]
                                    },
                                    "lugar_v"   : {
                                        "icon"           : {
                                            "image" : icons.lugar_v
                                        },
                                        "valid_children" : [ "" ]
                                    },
                                    "lugar_all" : {
                                        "icon"           : {
                                            "image" : icons.lugar_all
                                        },
                                        "valid_children" : [ "" ]
                                    }
                                }
                            },
                            "themes"    : {
                                "theme" : "default"
                            },
                            "search"    : {
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
                            "ui"        : {
                                "select_limit" : 1
                            }
                        }).bind("search.jstree",function (e, data) {
                            var cant = data.rslt.nodes.length;
                            var search = data.rslt.str;
                            $("#cantRes").html("<b>" + cant + "</b> resultado" + (cant == 1 ? "" : "s"));

                            var container = $('#tree'), scrollTo = $('.jstree-search').first();
                            container.animate({
                                scrollTop : scrollTo.offset().top - container.offset().top + container.scrollTop()
                            }, 2000);
                        }).bind("select_node.jstree", function (NODE, REF_NODE) {
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
                                case "grupo":
                                    url = "${createLink(action:'showGr_ajax')}";
                                    break;
                                case "subgrupo":
                                    url = "${createLink(action:'showSg_ajax')}";
                                    break;
                                case "departamento":
                                    url = "${createLink(action:'showDp_ajax')}";
                                    break;
                                case "item":
                                    url = "${createLink(action:'showIt_ajax')}";
                                    break;
                                case "lugar":
                                    url = "${createLink(action:'showLg_ajax')}";
                                    nodeId = parts[1] + "_" + parts[2];
                                    break;
                            }

                            if (url != "") {
                                $.ajax({
                                    type    : "POST",
                                    url     : url,
                                    data    : {
                                        id     : nodeId,
                                        all    : showLugar.all,
                                        ignore : showLugar.ignore
                                    },
                                    success : function (msg) {
                                        $("#info").html(msg);
                                    }
                                });
                            }
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

                $(".toggle").click(function () {
                    var tipo = $(this).attr("id");
                    if (tipo != current) {
//                        console.log(tipo);
                        current = tipo;
                        initTree(current);
                        $("#info").html("");
                    }
                });
                $(".toggleTipo").click(function () {
                    var tipo = $(this).attr("id");
                    if (!$(this).hasClass("active")) {
                        showLugar[tipo] = true;
                    } else {
                        showLugar[tipo] = false;
                    }
                    initTree(current);
                    $("#info").html("");
                });

                $(".fecha").click(function () {
                    var id = $(this).attr("id");
                    var text = $.trim($(this).text());
                    var fecha = $(this).data("fecha");

                    $("#spFecha").text(text);
                    if (fecha) {
                        var hoy = new Date();
                        $("#divFecha").show();
                        $("#fecha").datepicker("setDate", hoy);
                        showLugar.fecha = hoy.getDate() + "-" + (hoy.getMonth() + 1) + "-" + hoy.getFullYear()
                        console.log(showLugar);
                    } else {
                        $("#divFecha").hide();
                    }
                });

                initTree("1");
                $("#info").html("");

                $("#btnSearch").click(function () {
                    doSearch();
                });
                $("#search").keyup(function (ev) {
                    if (ev.keyCode == 13) {
                        doSearch();
                    }
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
                                cache[ term ] = data;
                                response(data);
                            }
                        });

                    }
                });

            });
        </script>

    </body>
</html>
