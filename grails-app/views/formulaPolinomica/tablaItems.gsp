<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 10/04/17
  Time: 12:00
--%>

<div class="contenedorTabla" style="width: 650px">
    <table class="table table-condensed table-bordered table-striped table-hover" id="tblDisponibles">
        <thead>
        <tr>
            <th>Item</th>
            <th>Descripción</th>
            ${tipo == 'c' ? '<th>Precio unitario</th>' : ''}
            <th>Aporte</th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${rows}" var="r">
            <tr data-item="${r.iid}" data-codigo="${r.codigo}" data-nombre="${r.item}" data-valor="${r.aporte ?: 0}" data-precio="${r.precio ?: 0}" data-grupo="${r.grupo}">
                <td>
                    ${r.codigo}
                </td>
                <td>
                    ${r.item}
                </td>
                <g:if test="${tipo == 'c'}">
                    <td class="numero">
                        <g:formatNumber number="${r.precio ?: 0}" maxFractionDigits="5" minFractionDigits="5" locale='ec'/>
                    </td>
                </g:if>
                <td class="numero">
                    <g:formatNumber number="${r.aporte ?: 0}" maxFractionDigits="5" minFractionDigits="5" locale='ec'/>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>
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
        var nombreOk = true;
        if ($.trim($row.attr("nombre")) == "") {
            nombreOk = false;
        }
        $("#spanCoef").text($.trim($row.attr("numero")) + ": " + $.trim($row.attr("nombre"))).parent().data("nombreOk", nombreOk);
    }

    function updateTotal(val) {
        $("#spanSuma").text("(" + number_format(val, 6, ".", ",") + ")").data("total", val);
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
            %{--////console.log("${tipo}", index, "${tipo}" == 'p', index == 0, "${tipo}" == 'p' && index == 0);--}%
//                    if (index) { //el primero (p01) de la formula no es seleccionable (el de cuadrilla tipo si es)
            if ("${tipo}" == 'p' && index == 0) { //el primero (p01) de la formula no es seleccionable (el de cuadrilla tipo si es)
//                        ////console.log("true");
                $seleccionados.removeClass("selected editable");
                $parent.children("a, .jstree-grid-cell").addClass("editable parent");
            } else {
//                        ////console.log("false");
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

    function updateSumaTotal() {
        var total = 0;

        $("#tree").children("ul").children("li").each(function () {
            var val = $(this).attr("valor");
            val = val.replace(",", ".");
            val = parseFloat(val);
            total += val;
        });
        $("#spanTotal").text(number_format(total, 3, ".", "")).data("valor", total);
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

                    var cantNombre = 0;

                    var $spans = $("#tree").find("span:contains('" + indiceNombre + "')");
                    $spans.each(function () {
                        var t = $.trim($(this).text());
                        if (t == indiceNombre) {
                            cantNombre++;
                        }
                    });

                    if (indiceNombre == nodeText) {
                        cantNombre = 0;
                    }

                    if (cantNombre == 0) {
                        if (valor != "") {
                            btnSave.replaceWith(spinner);
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
//                                            valor = number_format(valor,)
                                        node.attr("nombre", indiceNombre).trigger("change_node.jstree");
                                        node.attr("valor", valor).trigger("change_node.jstree");
                                        $("#modal-formula").modal("hide");
                                        updateSumaTotal();
                                    }
                                }
                            });
                        } else {
                        }
                    } else {
                        $("#modal-formula").modal("hide");
                        $.box({
                            imageClass : "box_info",
                            text       : "No puede ingresar dos coeficientes con el mismo nombre",
                            title      : "Alerta",
                            iconClose  : false,
                            dialog     : {
                                resizable     : false,
                                draggable     : false,
                                closeOnEscape : false,
                                buttons       : {
                                    "Aceptar" : function () {
                                    }
                                }
                            }
                        });
                    }
                });

                menuItems.editar = {
                    label            : "Editar",
                    separator_before : false,
                    separator_after  : false,
                    icon             : icons.edit,
                    action           : function (obj) {
                        <g:if test="${obra?.liquidacion==1 || obra?.estado!='R' || obra?.codigo[-1..-2] != 'OF'}">
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
                        </g:if>
                        <g:else>
                        alert("No puede modificar los coeficientes de una obra ya registrada");
                        </g:else>
                    }
                };

                break;
            case "it":
                var nodeCod = node.attr("numero");
                var nodeDes = node.attr("nombre");
                var nodeValor = node.attr("valor");
                var nodeItem = node.attr("item");

                var nodePrecio = node.attr("precio");
                var nodeGrupo = node.attr("grupo");

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
                        <g:if test="${obra?.liquidacion==1 || obra?.estado!='R' || obra?.codigo[-1..-2] != 'OF'}">
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
                                                    var totalInit = parseFloat($("#spanTotal").data("valor"));
                                                    $("#tree").jstree('delete_node', $("#" + nodeStrId));
                                                    var tr = $("<tr>");
                                                    var tdItem = $("<td>").append(nodeCod);
                                                    var tdDesc = $("<td>").append(nodeDes);
                                                    var tdApor = $("<td class='numero'>").append(number_format(nodeValor, 5, '.', ''));
                                                    var tdPrec = $("<td class='numero'>").append(number_format(nodePrecio, 5, '.', ''));

                                                    tr.append(tdItem).append(tdDesc);
//                                                            if (nodeGrupo.toString() == "2") {
                                                    if ("${params.tipo}" == "c") {
                                                        tr.append(tdPrec);
                                                    }
                                                    tr.append(tdApor);
                                                    tr.data({
                                                        valor  : nodeValor,
                                                        nombre : nodeDes,
                                                        codigo : nodeCod,
                                                        item   : nodeItem,
                                                        precio : nodePrecio,
                                                        grupo  : nodeGrupo
                                                    });
                                                    tr.click(function () {
                                                        clickTr(tr);
                                                    });
                                                    $("#tblDisponibles").children("tbody").prepend(tr);
                                                    tr.show("pulsate");
                                                    parent.attr("valor", number_format(msgParts[1], 3, '.', '')).trigger("change_node.jstree");
//                                                    console.log( $("#spanTotal"),nodeValor,msg)
                                                    totalInit -= parseFloat(nodeValor);
                                                    $("#spanTotal").text(number_format(totalInit, 3, ".", "")).data("valor", totalInit);
                                                    if (parent.children("ul").length == 0) {
                                                        parent.attr("nombre", "").trigger("change_node.jstree");
                                                    }
                                                }
                                            }
                                        });
                                    },
                                    "Cancelar" : function () {
                                    }
                                }
                            }
                        });

                        </g:if>
                        <g:else>
                        $.box({
                            imageClass : "box_info",
                            text       : "No puede modificar los coeficientes de una obra ya registrada",
                            title      : "Alerta",
                            iconClose  : false,
                            dialog     : {
                                resizable     : false,
                                draggable     : false,
                                closeOnEscape : false,
                                buttons       : {
                                    "Aceptar" : function () {
                                    }
                                }
                            }
                        });
                        </g:else>

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

        $("#btnRegresar").click(function () {
            var url = $(this).attr("href");
            var total = parseFloat($("#spanTotal").data("valor"));

//                    console.log(total, Math.abs(total - 1), Math.abs(total - 1) > 0.0001);

            var liCont = 0;
            var liEq = 0;
            $("#tree").find("li[rel=fp]").each(function () {
                var liNombre = $.trim($(this).attr("nombre"));
                var liValor = parseFloat($(this).attr("valor"));
                var liUl = $(this).children("ul").length;
                var liNextNombre = $.trim($(this).next().attr("nombre"));
                if ((liValor > 0 && liNombre == "") || (liUl > 0 && liNombre == "")) {
                    liCont++;
                }
                if (liNombre != "" && liNombre == liNextNombre) {
                    liEq++;
                }
            });
            if (liCont > 0) {
                $.box({
                    imageClass : "box_info",
                    text       : "Seleccione un nombre para todos los coeficientes con items.",
                    title      : "Alerta",
                    iconClose  : false,
                    dialog     : {
                        resizable     : false,
                        draggable     : false,
                        closeOnEscape : false,
                        buttons       : {
                            "Aceptar" : function () {
                            }
                        }
                    }
                });
                return false;
            }
            if (liEq > 0) {
                $.box({
                    imageClass : "box_info",
                    text       : "Seleccione un nombre único para cada coeficiente con items.",
                    title      : "Alerta",
                    iconClose  : false,
                    dialog     : {
                        resizable     : false,
                        draggable     : false,
                        closeOnEscape : false,
                        buttons       : {
                            "Aceptar" : function () {
                            }
                        }
                    }
                });
                return false;
            }

            var tipo = "${tipo}";
            if (Math.abs(total - 1) <= 0.0001) {
                return true;
            }
            var msg = "La fórmula polinómica no suma 1. ¿Está seguro de querer salir de esta página?";
            if (tipo == "c") {
                msg = "La cuadrilla tipo no suma 1. ¿Está seguro de querer salir de esta página?";
            }
            $.box({
                imageClass : "box_info",
                text       : msg,
                title      : "Confirme",
                iconClose  : false,
                dialog     : {
                    resizable     : false,
                    draggable     : false,
                    closeOnEscape : false,
                    buttons       : {
                        "Salir"                  : function () {
                            location.href = url;
                            return false;
                        },
                        "Continuar en la página" : function () {
                            return false;
                        }
                    }
                }
            });
            return false;
        });

        $("#btnRemoveSelection").click(function () {
            if (!$(this).hasClass("disabled")) {
                $tabla.children("tbody").children("tr.selected").removeClass("selected");
                $("#btnRemoveSelection").addClass("disabled");
                updateTotal(0);
                $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
            }
            return false;
        });

        $("#btnBorrar").click(function () {
            $(this).replaceWith(spinner);
            $.ajax({
                async   : false,
                type    : "POST",
                url     : "${createLink(action:'borrarFP')}",
                data    : {
                    obra : ${obra?.id}
                },
                success : function (msg) {
                    $.ajax({
                        async   : false,
                        type    : "POST",
                        url     : "${createLink(action:'insertarVolumenesItem')}",
                        data    : {
                            obra : ${obra?.id}
                        },
                        success : function (msg) {
                            location.reload(true);
                        }
                    });
                }
            });
            return false;
        });

        $("#btnEliminar").click(function () {
            $(this).replaceWith(spinner);
            $.ajax({
                async   : false,
                type    : "POST",
                url     : "${createLink(action:'borrarFP')}",
                data    : {
                    obra : ${obra?.id}
                },
                success : function (msg) {
                    location.reload(true);
                }
            });
            return false;
        });


        $("#btnAgregarItems").click(function () {
            var $btn = $(this);
            if (!$btn.hasClass("disabled")) {
                if ($(this).data("nombreOk")) {
                    $btn.hide().after(spinner);
                    var $target = $("a.selected").parent();

                    var total = parseFloat($target.attr("valor"));

                    var rows2add = [];
                    var dataAdd = {
                        formula : $target.attr("id"),
                        items   : []
                    };

                    var numero = $target.attr("numero");
                    var msg = "";

                    $tabla.children("tbody").children("tr.selected").each(function () {
                        var data = $(this).data();
                        if ($.trim(numero.toLowerCase()) == "px" && total + parseFloat(data.valor) > 0.2) {
                            msg += "<li>No se puede agregar " + data.nombre + " pues el valor de px no puede superar 0.20</li>";
                        } else {
                            rows2add.push({add : {attr : {item : data.item, numero : data.codigo, nombre : data.nombre, valor : data.valor, precio : data.precio, grupo : data.grupo}, data : "   "}, remove : $(this)});
                            total += parseFloat(data.valor);
                            dataAdd.items.push(data.item + "_" + data.valor);
                        }
                    });

                    //                        console.log(dataAdd, msg);
                    if (msg != "") {
                        $("#divError").html("<ul>" + msg + "</ul>").show("pulsate", 2000, function () {
                            setTimeout(function () {
                                $("#divError").hide("blind");
                            }, 5000);
                        });
                        //                            $tabla.children("tbody").children("tr.selected").removeClass(".selected");
                        //                            $("#btnRemoveSelection, #btnAgregarItems").addClass("disabled");
                        $btn.show();
                        spinner.remove();
                    } else {
                        $.ajax({                                    async   : false,
                            type    : "POST",
                            url     : "${createLink(action:'addItemFormula')}",
                            data    : dataAdd,
                            success : function (msg) {
                                //                                ////console.log(msg);
                                var msgParts = msg.split("_");
                                if (msgParts[0] == "OK") {

                                    var totalInit = parseFloat($("#spanTotal").data("valor"));

                                    var insertados = {};
                                    var inserted = msgParts[1].split(",");
                                    for (var i = 0; i < inserted.length; i++) {
                                        var j = inserted[i];
                                        if (j != "") {
                                            var p = j.split(":");
                                            insertados[p[0]] = p[1];
                                        }
                                    }

                                    //                                    ////console.log("insertados", insertados);
                                    for (i = 0; i < rows2add.length; i++) {
                                        var it = rows2add[i];
                                        var add = it.add;
                                        var rem = it.remove;

                                        add.attr.id = "it_" + insertados[add.attr.item];
                                        totalInit += parseFloat(add.attr.valor);
                                        //                                        ////console.log(add.attr.item, add);

                                        $tree.jstree("create_node", $target, "first", add);
                                        if (!$target.hasClass("jstree-open")) {
                                            $('#tree').jstree("open_node", $target);
                                        }
                                        rem.remove();
                                    }
                                    $("#spanTotal").text(number_format(totalInit, 3, ".", "")).data("valor", totalInit);
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
                } else {
                    $.box({
                        imageClass : "box_info",
                        title      : "Alerta",
                        text       : "Por favor seleccione el nombre del índice antes de agregar ítems.",
                        iconClose  : false,
                        dialog     : {
                            resizable     : false,
                            draggable     : false,
                            closeOnEscape : false,
                            buttons       : {
                                "Aceptar" : function () {
                                }
                            }
                        }
                    });
                }
            }

            return false;
        });

        $tabla.children("tbody").children("tr").click(function () {
            clickTr($(this));
        });

        $(".modal").draggable({
            handle : ".modal-header"
        });

        $("#creaIndice").click(function () {
            if (confirm("¿Crear un nuevo Indice INEC?. \nSe deberá luego solicitar al INEC su calificación.\n" +
                    "¿Continuar?")) {
                $.ajax({
                    type    : "POST",
                    url :  "${createLink(action:'creaIndice')}",
                    success : function (msg) {
                        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                        var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                        btnSave.click(function () {
                            if ($("#frmSave-Indice").valid()) {
                                btnSave.replaceWith(spinner);
                                $.ajax({
                                    type    : "POST",
                                    url     : "${createLink(controller: 'indice', action:'grabar')}",
                                    data    : $("#frmSave-Indice").serialize(),
                                    success : function (msg) {
                                        if(msg == 'ok'){
                                            $("#modal-indice").modal("hide");
                                        }
                                    }
                                });
                                return false;
                                location.reload(true);
                            }
                        });

                        $("#modalTitle-Indc").html("Crear Índices   INEC");
                        $("#modalBody-Indc").html(msg);
                        $("#modalFooter-Indc").html("").append(btnOk).append(btnSave);
                        $("#modal-indice").modal("show");
                    }
                });
                return false;
            }
        });



    })

</script>