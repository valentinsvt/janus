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
    <title>Registro y mantenimiento de items</title>

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

    <link href="${resource(dir: 'css', file: 'tree.css')}" rel="stylesheet"/>

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

        grupo : "${resource(dir: 'images/tree', file: 'grupo_material.png')}",

        subgrupo : "${resource(dir: 'images/tree', file: 'subgrupo_material.png')}",

        departamento : "${resource(dir: 'images/tree', file: 'departamento_material.png')}"

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

        }
        if(url!= "") {
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
                    url       : "${createLink(action:'formSg_ajax')}",
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
                break;

            case "subgrupo":
                menuItems.editar = createUpdate({
                    action    : "update",
                    label     : "Editar Grupo",
                    icon      : icons.edit,
                    sepBefore : false,
                    sepAfter  : false,
                    url       : "${createLink(action:'formSg_ajax')}",
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
                    url       : "${createLink(action:'formSg_ajax')}",
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
                    url       : "${createLink(action:'formDp_ajax')}",
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
                break;
            case "departamento":

                menuItems.editar = createUpdate({
                    action    : "update",
                    label     : "Editar subgrupo",
                    icon      : icons.edit,
                    sepBefore : false,
                    sepAfter  : false,
                    url       : "${createLink(action:'formDp_ajax')}",
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
                    url       : "${createLink(action:'formDp_ajax')}",
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
                break;

        }

        return menuItems;
    }

    function initTree() {
        var id, rel, label;
        id = "root";
        rel = "root";
        label = "Dirección Solicitante";




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
                                return {id : id, tipo: tipo}
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
                }) ;


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
