<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 8/2/13
  Time: 12:40 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="janus.actas.Acta" %>
<html>
    <head>
        <meta name="layout" content="main">

        <script src="${resource(dir: 'js/jquery/plugins/ckeditor', file: 'ckeditor.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

        <title>Acta</title>

        <style>
        .tituloChevere {
            font-size : 20px !important;
        }

        .titulo {
            font-size : 20px;
        }

        .bold {
            font-weight : bold;
        }

        .span1 {
            width : 95px !important;
        }

        .editable {
            background : url(${resource(dir:'images', file:'edit.gif')}) right no-repeat rgba(245, 245, 245, 0.5);
            border     : solid 1px #efefef;
            padding    : 3px 20px 0 3px;
            margin     : 10px 0;
            width      : auto;
        }

        .numero {
            width : 35px !important;
        }

        .seccion {
            background    : rgba(50, 100, 150, 0.3);
            padding       : 5px;
            margin-bottom : 10px;
        }

        .seccion .span9 {
            margin : 0 !important;
        }

        .lvl2 {
            margin-left : 85px !important;
        }

        .lblSeccion {
            width : 900px;
        }

        .contParrafo {
            width : 875px;
        }

        .tablas {
            margin     : 0 0 10px 120px !important;
            background : rgba(100, 100, 100, 0.4);
            width      : 900px;
        }

        .tablas .table {
            margin-bottom : 0 !important;
        }

        th {
            vertical-align : middle !important;
        }

        .tal {
            text-align : left !important;
        }
        </style>
    </head>

    <body>

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

        <div class="tituloTree">
            Creación de Acta
        </div>

        <div class="row" style="margin-bottom: 15px;">
            <div class="span9" role="navigation">
                <div class="btn-group">
                    <g:if test="${actaInstance.contrato}">
                        <g:link controller="contrato" action="verContrato" params="[contrato: params.contrato]" class="btn" title="Regresar al contrato">
                            <i class="icon-arrow-left"></i>
                            Contrato
                        </g:link>
                        <g:link action="form" class="btn" params="[contrato: actaInstance.contrato]">
                            <i class="icon-file"></i>
                            Planillas
                        </g:link>
                    </g:if>
                </div>

                <div class="btn-group">
                    <a href="#" id="btnSave" class="btn btn-success"><i class="icon-save"></i> Guardar</a>
                </div>

            </div>

        </div>

        <g:form class="form-horizontal" name="frmSave-Acta" action="save">
            <g:hiddenField name="id" value="${actaInstance?.id}"/>
            <g:hiddenField id="contrato" name="contrato.id" value="${actaInstance?.contrato?.id}"/>
            <g:hiddenField id="txtDescripcion" name="descripcion" value="${actaInstance?.descripcion}"/>

            <div class="titulo bold">
                Acta de  <g:textField name="nombre" maxlength="20" class=" required input-small" value="${actaInstance?.nombre ?: 'recepción'}"/>
                <g:select name="tipo" from="${actaInstance.constraints.tipo.inList}" class=" required input-medium" value="${actaInstance?.tipo}" valueMessagePrefix="acta.tipo"/>
                N. <g:textField name="numero" maxlength="20" class=" required input-small" value="${actaInstance?.numero}"/>
            </div>

            <div class="tituloChevere">
                Datos Generales
            </div>

            <g:set var="garantias" value="${janus.pac.Garantia.findAllByContrato(actaInstance.contrato)}"/>
            <g:set var="obra" value="${actaInstance.contrato.oferta.concurso.obra}"/>
            <g:set var="fisc" value="${janus.ejecucion.Planilla.findAllByContrato(actaInstance.contrato, [sort: "id", order: "desc"]).first().fiscalizador}"/>
            <div class="well">
                <div class='row'>
                    <div class="bold span1">Contrato N.</div>

                    <div class="span10">${actaInstance.contrato.codigo}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Garantías N.</div>

                    <div class="span10">
                        <g:each in="${garantias}" var="gar" status="i">
                            ${gar.tipoDocumentoGarantia.descripcion} N. ${gar.codigo} - ${gar.aseguradora.nombre} ${i < garantias.size() - 1 ? "," : ""}
                        </g:each>
                    </div>
                </div>

                <div class='row'>
                    <div class="bold span1">Objeto</div>

                    <div class="span10">${actaInstance.contrato.objeto}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Lugar</div>

                    <div class="span10">${obra.sitio}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Ubicación</div>

                    <div class="span10">Parroquia ${obra.parroquia.nombre} - Cantón ${obra.parroquia.canton.nombre}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Monto $.</div>

                    <div class="span10">${actaInstance.contrato.monto}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Contratista</div>

                    <div class="span10">${actaInstance.contrato.oferta.proveedor.nombre}</div>
                </div>

                <div class='row'>
                    <div class="bold span1">Fiscalizador</div>

                    <div class="span10">${fisc.titulo} ${fisc.nombre} ${fisc.apellido}</div>
                </div>

            </div> %{-- well contrato --}%

            <div class="editable ui-corner-all" id="descripcion" contenteditable="true">
                ${actaInstance.descripcion}
            </div>

            <a href="#" class="btn btn-primary btn-small" style="margin-bottom: 10px;" id="btnAddSeccion">
                <i class="icon-plus"></i> Agregar sección
            </a>

            <div id="secciones"></div>

        </g:form>

        <div class="modal hide fade" id="modal">
            <div class="modal-header" id="modalHeader">
                <button type="button" class="close darker" data-dismiss="modal">
                    <i class="icon-remove-circle"></i>
                </button>

                <h3 id="modalTitle"></h3>
            </div>

            <div class="modal-body" id="modalBody">
            </div>

            <div class="modal-footer" id="modalFooter">
            </div>
        </div>

        <script type="text/javascript">
            var secciones = 1;

            function getSecciones() {
                var str = "";
                $(".seccion").each(function () {
                    var $sec = $(this);
                    var num = $sec.data("numero");
                    var titulo = $sec.data("titulo");
                    if (str != "") {
                        str += "&";
                    }
                    str += "seccion=" + num + "**" + titulo;
                    var parrafos = getParrafos($sec);
                    str += parrafos != "" ? "&" + parrafos : "";
                });
                return str;
            }

            function getParrafos($seccion) {
                var str = "";
                $seccion.find(".parrafo").each(function () {
                    var $p = $(this);
                    var num = $p.data("numero");
                    var cont = $p.data("contenido");
                    if (str != "") {
                        str += "&";
                    }
                    str += "parrafos" + $seccion.data("numero") + "=" + num + "**" + cont;
                });
                return str;
            }

            function numerosSecciones() {
                var cont = 1;

                $(".seccion").each(function () {
                    var $sec = $(this);
                    var $titulo = $sec.find(".tituloSeccion");
                    var $num = $titulo.find(".numero");
                    var num = $.trim($num.text());
                    num = $.trim(str_replace(".-", "", num));

                    if (parseInt(num) != cont) {
                        num = cont;
                        $num.text(num + ".-");
                    }

                    var titulo = CKEDITOR.instances["seccion_" + num].getData();

                    $sec.data({
                        numero : num,
                        titulo : titulo
                    });
                    numerosParrafos($sec);
                    cont++;
                });
            }

            function tipoTabla(tipo, $div) {
                var $tabla = $("<table class='table table-bordered table-condensed'></table>");
                var $thead = $("<thead></thead>").appendTo($tabla);
                var $tr = $("<tr></tr>").appendTo($thead);
                var $cont = $("<div class='span8 tablas lvl2 ui-corner-all'></div>");

                switch (tipo) {
                    case "RBR": //Rubros (4.1),
                        $("<th>N.</th>").appendTo($tr);
                        $("<th>Descripción del rubro</th>").appendTo($tr);
                        $("<th>U.</th>").appendTo($tr);
                        $("<th>Precio unitario</th>").appendTo($tr);
                        $("<th>Volumen contratado</th>").appendTo($tr);
                        $("<th>Cantidad total ejecutada</th>").appendTo($tr);
                        $("<th>Valor total ejecutado</th>").appendTo($tr);
                        break;
                    case  "DTP": //Detalle planillas (4.2),
                        $("<th rowspan='2'>Fecha</th>").appendTo($tr);
                        $("<th rowspan='2'>N. planilla</th>").appendTo($tr);
                        $("<th rowspan='2'>Periodo</th>").appendTo($tr);
                        $("<th rowspan='2'>Valor</th>").appendTo($tr);
                        $("<th colspan='2'>Descuentos</th>").appendTo($tr);
                        var $tr2 = $("<tr></tr>").appendTo($thead);
                        $("<th>Anticipo</th>").appendTo($tr2);
                        $("<th>Multas</th>").appendTo($tr2);
                        break;
                    case "OAD": // Obras adicionales (4.3)
                        $("<th>Valor de obras adicionales contractuales</th>").appendTo($tr);
                        $("<th>%</th>").appendTo($tr);
                        $("<th>Valor de obras adicionales costo + porcentaje</th>").appendTo($tr);
                        $("<th>%</th>").appendTo($tr);
                        $("<th>Memorando de autorización</th>").appendTo($tr);
                        $("<th>Memorando a Dir. Financiera</th>").appendTo($tr);
                        $("<th>Memorando a partida presupuestaria</th>").appendTo($tr);
                        $("<th>% total</th>").appendTo($tr);
                        break;
                    case "OCP": //costo y porcentaje (4.4),
                        $("<th>Fecha</th>").appendTo($tr);
                        $("<th>N. planilla</th>").appendTo($tr);
                        $("<th>Periodo</th>").appendTo($tr);
                        $("<th>Valor neto</th>").appendTo($tr);
                        $("<th>%</th>").appendTo($tr);
                        $("<th>Valor total</th>").appendTo($tr);
                        break;
                    case "RRP": //resumen reajuste precios (4.5),
                        $("<th>Fecha</th>").appendTo($tr);
                        $("<th>N. planilla</th>").appendTo($tr);
                        $("<th>Periodo</th>").appendTo($tr);
                        $("<th>Valor</th>").appendTo($tr);
                        break;
                    case "RGV": //resumen general valores (4.6),
                        $("<th class='tal'>Total valor de liquidación de obra</th>").appendTo($tr);
                        var $tr2 = $("<tr></tr>").appendTo($thead);
                        $("<th class='tal'>Total valor de reajuste de precios</th>").appendTo($tr2);
                        var $tr3 = $("<tr></tr>").appendTo($thead);
                        $("<th class='tal'>Total valor de la inversión</th>").appendTo($tr3);
                        break;
                    case "DTA": //detalle ampliaciones (5.5)
                        $("<th>N.</th>").appendTo($tr);
                        $("<th>N. de días</th>").appendTo($tr);
                        $("<th>Trámite</th>").appendTo($tr);
                        $("<th>Fecha</th>").appendTo($tr);
                        $("<th>Motivo</th>").appendTo($tr);
                        $("<th>Observaciones</th>").appendTo($tr);
                        break;
                    case "DTS": //detalle suspensiones (5.6),
                        $("<th>N.</th>").appendTo($tr);
                        $("<th>N. de días</th>").appendTo($tr);
                        $("<th>Periodo</th>").appendTo($tr);
                        $("<th>Trámite</th>").appendTo($tr);
                        $("<th>Fecha</th>").appendTo($tr);
                        $("<th>Motivo</th>").appendTo($tr);
                        break;
                    case  "RPR": //resumen reajuste (8.1)
                        $("<th>N.</th>").appendTo($tr);
                        $("<th>Periodo</th>").appendTo($tr);
                        $("<th>Valor provisional</th>").appendTo($tr);
                        $("<th>Valor definitivo</th>").appendTo($tr);
                        $("<th>Diferencia</th>").appendTo($tr);
                        break;
                }

                $cont.html($tabla);
                $cont.appendTo($div);
            }

            function numerosParrafos($seccion) {
                var cont = 1;

                var numSec = $seccion.data("numero");

                $seccion.find(".parrafo").each(function () {
                    var $par = $(this);

                    var $titulo = $par.find(".tituloParrafo");
                    var $num = $titulo.find(".numero");
                    var num = $.trim($num.text());
                    num = $.trim(str_replace(".-", "", num));
                    var parts = num.split(".");
                    num = parts[1];

                    if (parseInt(num) != cont || parseInt(parts[0]) != numSec) {
                        num = cont;
                        $num.text(numSec + "." + num + ".-");
                    }

                    var contenido = CKEDITOR.instances["parrafo_" + numSec + "_" + num].getData();

                    $par.data({
                        numero    : num,
                        contenido : contenido
                    });
                    cont++;
                });

            }

            function addParrafo(data, num, $div) {
                var $parr = $("<div class='parrafo'></div>");
                var $titulo = $("<div class='row tituloParrafo '></div>");
                $("<div class='span1 numero lvl2 bold'>" + num + "." + data.numero + ".-</div>").appendTo($titulo);
                $("<div class='span9 contParrafo editable ui-corner-all' id='parrafo_" + num + "_" + data.numero + "' contenteditable='true'>" + data.contenido + "</div>").appendTo($titulo);
                if (data.tipoTabla) {
                    tipoTabla(data.tipoTabla, $titulo);
                }

                var $btnEliminarParrafo = $('<a href="#" class="btn btn-delete btn-mini" style="margin-left: 10px;"><i class="icon-minus"></i> Eliminar párrafo</a>');

                $btnEliminarParrafo.click(function () {
                    var $del = $(this).parents(".parrafo");
                    $.box({
                        imageClass : "box_info",
                        text       : "Está seguro de eliminar este párrafo? Esta acción no se puede deshacer...",
                        title      : "Confirmación",
                        iconClose  : false,
                        dialog     : {
                            resizable     : false,
                            draggable     : false,
                            closeOnEscape : false,
                            buttons       : {
                                "Aceptar"  : function () {
                                    $del.remove();
                                    numerosParrafos($div.parents(".seccion"));
                                },
                                "Cancelar" : function () {
                                }
                            }
                        }
                    });
                    return false;
                });

                $titulo.append($btnEliminarParrafo).appendTo($parr);
                $parr.appendTo($div);

                $parr.data({
                    numero    : data.numero,
                    contenido : data.contenido,
                    tipoTabla : data.tipoTabla
                });

            }

            function addSeccion(data) {
                var $seccion = $("<div class='seccion ui-corner-all'></div>");
                var $titulo = $("<div class='row tituloSeccion'></div>");
                $("<div class='span1 numero lvl1 bold'>" + data.numero + ".-</div>").appendTo($titulo);
                $("<div class='span9 lblSeccion editable bold ui-corner-all' id='seccion_" + data.numero + "' contenteditable='true'>" + data.titulo + "</div>").appendTo($titulo);
                var $btnAddParrafo = $('<a href="#" class="btn btn-show btn-mini pull-right" style="margin-left: 10px;"><i class="icon-plus"></i> Agregar párrafo</a>');
                var $btnEliminarSeccion = $('<a href="#" class="btn btn-danger btn-mini pull-right" style="margin-left: 10px;"><i class="icon-minus"></i> Eliminar sección</a>');

                $btnAddParrafo.click(function () {
                    return false;
                });
                $btnEliminarSeccion.click(function () {
                    var $del = $(this).parents(".seccion");
                    $.box({
                        imageClass : "box_info",
                        text       : "Está seguro de eliminar esta sección? Se eliminarán también sus párrafos y esta acción no se puede deshacer...",
                        title      : "Confirmación",
                        iconClose  : false,
                        dialog     : {
                            resizable     : false,
                            draggable     : false,
                            closeOnEscape : false,
                            buttons       : {
                                "Aceptar"  : function () {
                                    $del.remove();
                                    numerosSecciones();
                                },
                                "Cancelar" : function () {
                                }
                            }
                        }
                    });

                    return false;
                });

                $titulo.append($btnAddParrafo).append($btnEliminarSeccion);
                $titulo.appendTo($seccion);

                if (!data.parrafos) {
                    data.parrafos = [];
                }

                $seccion.data({
                    numero : data.numero,
                    titulo : data.titulo
                });

                if (data.parrafos.length > 0) {
                    var $parr = $("<div class='row parrafos'></div>");
                    for (var i = 0; i < data.parrafos.length; i++) {
                        addParrafo(data.parrafos[i], data.numero, $parr);
                    }
                    $parr.appendTo($seccion);
                }

                $("#secciones").append($seccion);

                secciones++;
            }

            function initSecciones() {
                var secciones = ${secciones};
                for (var i = 0; i < secciones.length; i++) {
                    addSeccion(secciones[i]);
                }
            }

            function doSave() {
                if ($("#frmSave-Acta").valid()) {
                    $.box({
                        imageClass : "box_info",
                        text       : "Por favor espere",
                        title      : "Espere...",
                        iconClose  : false,
                        dialog     : {
                            resizable     : false,
                            draggable     : false,
                            closeOnEscape : false,
                            buttons       : null
                        }
                    });

                    $("#txtDescripcion").val(CKEDITOR.instances.descripcion.getData());
                    $("#frmSave-Acta").submit();
//                    var data = $("#frmSave-Acta").serialize();
//                    data += "&descripcion=" + CKEDITOR.instances.descripcion.getData();
//                    var secc = getSecciones();
//                    data += secc != "" ? "&" + secc : "";
//                    console.log(data);
                }
            }

            $(function () {

                initSecciones();

                $("#btnAddSeccion").click(function () {
                    var id = "${actaInstance.id}";
                    if (id == "") {
                        $.box({
                            imageClass : "box_info",
                            text       : "Por favor guarde el acta para insertar secciones y párrafos",
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
                    } else {
                        $.ajax({
                            type    : "POST",
                            data    : {
                                acta   : "${actaInstance.id}",
                                numero : secciones
                            },
                            url     : "${createLink(controller: 'seccion', action:'form_ext_ajax')}",
                            success : function (msg) {
                                var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                                var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                                btnSave.click(function () {
                                    submitForm(btnSave);
                                    return false;
                                });

                                $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                                $("#modalTitle").html("Crear Acta");
                                $("#modalBody").html(msg);
                                $("#modalFooter").html("").append(btnOk).append(btnSave);
                                $("#modal").modal("show");
                            }
                        });
                    }
                    return false;
                });

                $("#btnSave").click(function () {
                    doSave();
                    return false;
                });

                $("#frmSave-Acta").validate({
                    errorClass    : "label label-important",
                    submitHandler : function (form) {
                        $(".btn-success").replaceWith(spinner);
                        form.submit();
                    }
                });

                $("input").keyup(function (ev) {
                    if (ev.keyCode == 13) {
                        submitForm($(".btn-success"));
                    }
                });
            });
        </script>

    </body>
</html>