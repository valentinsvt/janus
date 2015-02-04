<%@ page import="janus.pac.Asignacion" %>
<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Asignaciones
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}"
            type="text/javascript"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}"
            type="text/javascript"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}"
          rel="stylesheet" type="text/css"/>
    <style type="text/css">
    td {
        font-size: 10px !important;
    }

    th {
        font-size: 11px !important;
    }

    .dato {
        margin-top: 5px !important;
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

<div class="tituloTree" style="width: 800px;">
    Asignación de techos anuales a partidas presupuestarias
</div>

<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px">

    <div id="create-Asignacion" style="border-bottom: 1px solid black;width: 900px;margin-bottom: 10px">
        <g:form class="form-horizontal frm_asgn" name="frmSave-Asignacion" action="save">
            <g:hiddenField name="id" value="${asignacionInstance?.id}"/>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse" style="width: 110px;">
                        Partida
                    </span>
                </div>

                <div class="controls" style="width: 1000px; margin-left: 140px;">

                    <input class="span3" type="text" style="width: 300px;;font-size: 12px" id="item_presupuesto">

                    <input type="hidden" id="item_prsp" name="prespuesto.id">
                    %{--<br>--}%
                    <input class="span4" type="text" style="width: 400px;;font-size: 12px;margin-top: 0px;"
                           id="item_desc" disabled="true">
                    <a href="#" class="btn btn-warning" title="Crear nueva partida" id="item_agregar_prsp">
                        <i class="icon-edit"></i>
                        Nueva partida
                    </a>
                    <a href="#" class="btn btn-warning" title="Crear nueva partida" id="prsp_editar">
                        <i class="icon-edit"></i>
                        Editar
                    </a>

                    <div class="span2 dato" style="width: 100px;">Fuente:</div> <input class="span4 dato" type="text"
                                                                                       style="width: 360px;;font-size: 12px;margin-top: 0px;"
                                                                                       id="item_fuente"
                                                                                       disabled="true"> <br>

                    <div class="span2 dato" style="width: 100px;">Programa:</div> <input class="span4 dato" type="text"
                                                                                         style="width: 510px;;font-size: 12px;margin-top: 0px;"
                                                                                         id="item_prog"
                                                                                         disabled="true">  <br>

                    <div class="span2 dato" style="width: 100px;">Subprograma:</div><input class="span4 dato"
                                                                                           type="text"
                                                                                           style="width: 510px;;font-size: 12px;margin-top: 0px;"
                                                                                           id="item_spro"
                                                                                           disabled="true">   <br>

                    <div class="span2 dato" style="width: 100px;">Proyecto:</div><input class="span4 dato" type="text"
                                                                                        style="width: 510px;;font-size: 12px;margin-top: 0px;"
                                                                                        id="item_proy"
                                                                                        disabled="true">   <br>

                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse" style="width: 120px;">
                        Año
                    </span>
                </div>

                <div class="controls" style="width: 120px;">
                    <g:select id="anio" name="anio.id" from="${janus.pac.Anio.list()}" optionKey="id" optionValue="anio"
                              class="many-to-one required" value="${actual.id}" style="width: 100px;"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>
                </div>

            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse" style="width: 120px;">
                        Valor
                    </span>
                </div>

                <div class="controls">
                    <g:field type="number" name="valor" id="valor" class=" required" value="0.00"
                             style="width: 150px;"/>
                    <span class="mandatory">*</span>

                    <p class="help-block ui-helper-hidden"></p>

                    <span style="margin-left: 400px;">
                        <a href="#" id="guardar" class="btn btn-primary">Guardar</a>
                    </span>
                </div>
            </div>

        </g:form>
    </div>

    <div id="list-Asignacion" style="width: 1100px;">

    </div>
</div>


<div class="modal grande hide fade" id="modal-ccp" style="overflow: hidden;">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle"></h3>
    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="pac.buscador.id" value="" accion="buscaPrsp" controlador="asignacion" campos="${campos}"
                      label="cpac" tipo="lista"/>
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>

<div class="modal large hide fade" id="modal-presupuesto">
    <div class="modal-header btn-warning">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle-presupuesto"></h3>
    </div>

    <div class="modal-body" id="modalBody-presupuesto">
    </div>

    <div class="modal-footer" id="modalFooter-presupuesto">
    </div>
</div>
<script type="text/javascript">
    function cargarTecho() {
        if ($("#item_prsp").val() * 1 > 0) {
            $.ajax({type: "POST", url: "${g.createLink(controller: 'asignacion',action:'cargarTecho')}",
                data: "id=" + $("#item_prsp").val() + "&anio=" + $("#anio").val(),
                success: function (msg) {
                    $("#valor").val(number_format(msg, 2, ".", ""))
                }
            });
        } else {
            $.box({
                imageClass: "box_info",
                text: "Por favor escoja una partida presupuestaria, dando doble click en el campo de texto",
                title: "Alerta",
                iconClose: false,
                dialog: {
                    resizable: false,
                    draggable: false,
                    buttons: {
                        "Aceptar": function () {
                        }
                    },
                    width: 500
                }
            });
        }

    }

    $("#prsp_editar").click(function () {
        $.ajax({
            type: "POST",
            data: {
                id: $("#item_prsp").val()
            },
            url: "${createLink(action:'form_ajax',controller: 'presupuesto')}",
            success: function (msg) {
                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                btnSave.click(function () {
                    if ($("#frmSave-presupuestoInstance").valid()) {
                        btnSave.replaceWith(spinner);
                    }
                    $.ajax({type: "POST", url: "${g.createLink(controller: 'presupuesto',action:'saveAjax')}",
                        data: $("#frmSave-presupuestoInstance").serialize(),
                        success: function (msg) {
//                            ////console.log(msg)
                            var parts = msg.split("&")
                            $("#item_prsp").val(parts[0])
                            $("#item_presupuesto").val(parts[1])
                            $("#item_presupuesto").attr("title", parts[2])
                            $("#item_desc").val(parts[2])
                            $("#item_fuente").val(parts[3])
                            $("#item_prog").val(parts[4])
                            $("#item_spro").val(parts[5])
                            $("#item_proy").val(parts[6])
                            $("#modal-presupuesto").modal("hide");
                        }
                    });

                    return false;
                });

                $("#modalTitle-presupuesto").html("Crear Presupuesto");
                $("#modalBody-presupuesto").html(msg);
                $("#modalFooter-presupuesto").html("").append(btnOk).append(btnSave);
                $("#modal-presupuesto").modal("show");
            }
        });
        return false;
    });

    $("#guardar").click(function () {
        var msn = ""
        var valor = $("#valor").val()
        if ($("#item_prsp").val() * 1 < 1) {
            msn += "<br>Error: Escoja una partida presupuestaria, dando doble click en el campo de texto"
        }
        if (isNaN(valor)) {
            msn += "<br>Error: El valor debe ser un número positivo"
        } else {
            if (valor * 1 < 0) {
                msn += "<br>Error: El valor debe ser un número positivo"
            }
        }
        if (msn == "")
            $(".frm_asgn").submit()
        else {
            $.box({
                imageClass: "box_info",
                text: msn,
                title: "Errores",
                iconClose: false,
                dialog: {
                    resizable: false,
                    draggable: false,
                    buttons: {
                        "Aceptar": function () {
                        }
                    },
                    width: 500
                }
            });
        }

    })


    $("#anio").change(cargarTecho)
    $("#item_agregar_prsp").click(function () {
        $.ajax({
            type: "POST",
            url: "${createLink(action:'form_ajax',controller: 'presupuesto')}",
            success: function (msg) {
                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                btnSave.click(function () {
                    if ($("#frmSave-presupuestoInstance").valid()) {
                        btnSave.replaceWith(spinner);
                    }
                    $.ajax({type: "POST", url: "${g.createLink(controller: 'presupuesto',action:'saveAjax')}",
                        data: $("#frmSave-presupuestoInstance").serialize(),
                        success: function (msg) {
//                            ////console.log(msg)
                            var parts = msg.split("&")
                            $("#item_prsp").val(parts[0])
                            $("#item_presupuesto").val(parts[1])
                            $("#item_presupuesto").attr("title", parts[2])
                            $("#item_desc").val(parts[2])
                            $("#item_fuente").val(parts[3])
                            $("#item_prog").val(parts[4])
                            $("#item_spro").val(parts[5])
                            $("#item_proy").val(parts[6])
                            $("#modal-presupuesto").modal("hide");
                        }
                    });

                    return false;
                });

                $("#modalTitle-presupuesto").html("Crear Presupuesto");
                $("#modalBody-presupuesto").html(msg);
                $("#modalFooter-presupuesto").html("").append(btnOk).append(btnSave);
                $("#modal-presupuesto").modal("show");
            }
        });
        return false;
    });
    $("#item_presupuesto").dblclick(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle").html("Partidas presupuestarias");
        $("#modalFooter").html("").append(btnOk);
        $(".contenidoBuscador").html("")
        $("#modal-ccp").modal("show");

    });
    %{--list-Asignacion--}%
    $.ajax({type: "POST", url: "${g.createLink(controller: 'asignacion', action:'tabla')}",
        data: "",
        success: function (msg) {
            $("#list-Asignacion").html(msg)
        }
    });


</script>

</body>
</html>

