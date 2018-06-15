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

        <script src="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.css')}" rel="stylesheet"/>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.customThemes.css')}" rel="stylesheet"/>

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
            min-height : 25px;
        }

        .numero {
            width : 35px !important;
        }

        .seccion {
            background    : rgba(50, 100, 150, 0.3);
            padding       : 5px;
            margin-bottom : 10px;
            position      : relative;
        }

        .botones {
            width    : 31px;
            position : absolute;
            right    : -35px;
            top      : 0;
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

        .warning {
            font-size : 15px;
        }

        .strongWarning {
            font-size   : 18px;
            font-weight : bold;
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

        <div class="row" style="margin-bottom: 15px;">
            <div class="span9" role="navigation">
                <div class="btn-group">
                    <g:if test="${actaInstance.contrato}">
                        <g:link controller="contrato" action="verContrato" id="${actaInstance.contratoId}" class="btn" title="Regresar al contrato">
                            <i class="icon-arrow-left"></i>
                            Contrato
                        </g:link>
                        <g:link controller="planilla" action="list" class="btn" id="${actaInstance.contratoId}">
                            <i class="icon-file"></i>
                            Planillas
                        </g:link>
                    </g:if>
                </div>

                <div class="btn-group">
                    <g:if test="${!actaInstance?.id || editable}">
                        <a href="#" id="btnSave" class="btn btn-success"><i class="icon-save"></i> Guardar</a>
                    </g:if>
                    <g:if test="${actaInstance?.id}">
                    %{--<g:link controller="reportesPlanillas" class="btn" action="actaRecepcion" id="${actaInstance.id}"><i class="icon-print"></i> Imprimir</g:link>--}%
                        <a href="#" class="btn" id="btnPrint"><i class="icon-print"></i> Imprimir</a>
                        <g:if test="${editable}">
                            <a href="#" class="btn " id="btnRegistro"><i class="icon-lock"></i> Registrar</a>
                        </g:if>

                        <a href="#" class="btn text-info" id="btnPrintCmpl"><i class="icon-print text-info"></i> Impr. Complementario</a>
                    </g:if>
                </div>

            </div>

        </div>
        <g:form class="form-horizontal" name="frmSave-Acta" action="save">
            <g:if test="${editable}">
                <g:hiddenField name="id" value="${actaInstance?.id}"/>
                <g:hiddenField id="contrato" name="contrato.id" value="${actaInstance?.contrato?.id}"/>
                <g:hiddenField id="txtDescripcion" name="descripcion" value="${actaInstance?.descripcion}"/>
            </g:if>

            <div class="titulo bold">
                Acta de
                <g:if test="${editable}">
                    <g:textField name="nombre" maxlength="20" class=" required input-small" value="${actaInstance?.nombre ?: actaProv ? actaProv.nombre : 'recepción'}"/>
                </g:if>
                <g:else>
                    ${actaInstance?.nombre}
                </g:else>
                <g:if test="${editable}">
                    <g:select name="tipo" from="${tipos}" class=" required input-medium" value="${actaInstance?.tipo}" valueMessagePrefix="acta.tipo"/>
                </g:if>
                <g:else>
                    <g:message code="acta.tipo.${actaInstance.tipo}"/>
                </g:else>
                N.
                <g:if test="${editable}">
                    <g:textField name="numero" maxlength="20" class=" required allCaps input-medium" style="width: 170px;" value="${actaInstance?.numero}"/>
                </g:if>
                <g:else>
                    ${actaInstance.numero}
                </g:else>
                efectuada el
                <g:if test="${editable}">
                    %{--<elm:datepicker name="fecha" class=" input-small required" value="${actaInstance?.fecha}"--}%
                                    %{--minDate="new Date(${contrato.fechaSubscripcion.format('yyyy')},${contrato.fechaSubscripcion.format('MM').toInteger() - 1},${contrato.fechaSubscripcion.format('dd')},0,0,0,0)"--}%
                                    %{--maxDate="new Date()"/>--}%
                    <elm:datepicker name="fecha" class=" input-small required" value="${actaInstance?.fecha}"
                                    minDate="new Date(${contrato.fechaPedidoRecepcionFiscalizador.format('yyyy')},${contrato.fechaPedidoRecepcionFiscalizador.format('MM').toInteger() - 1},
                                    ${contrato.fechaPedidoRecepcionFiscalizador.format('dd')},0,0,0,0)"
                                    maxDate=""/>



                </g:if>
                <g:else>
                    <g:formatDate date="${actaInstance.fecha}" format="dd-MM-yyyy"/>
                    y registrada el
                    <g:formatDate date="${actaInstance.fechaRegistro}" format="dd-MM-yyyy"/>
                </g:else>

                <span style="margin-left:100px"> Insertar espacios Extra:
                    <g:textField name="espacios" maxlength="1" class="input-medium" style="width: 30px;" value="${actaInstance?.espacios}"/>
                </span>

            </div>

            <g:if test="${!actaInstance.id && actaInstance.tipo == 'D'}">
                <div class="alert alert-info" style="margin-top: 15px;">
                    Está creando el acta definitiva. Una vez ingresados el número y la fecha y guardada el acta se copiarán las secciones y los párrafos del acta provisional.
                </div>
            </g:if>

            <div class="tituloChevere">
                Datos Generales: ${actaInstance?.tipo == 'D' ? 'Acta de Recepción Definitiva' : 'Acta de Recepción Provisional'}
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

            <div class="${editable ? 'editable' : ''} ui-corner-all" id="descripcion" ${editable ? 'contenteditable="true"' : ''}>
                ${actaInstance.descripcion}
            </div>

            <g:if test="${editable && actaInstance.id}">
                <a href="#" class="btn btn-primary btn-small" style="margin-bottom: 10px;" id="btnAddSeccion">
                    <i class="icon-plus"></i> Agregar sección
                </a>
            </g:if>

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
            var $btnSaveActa = $("#btnSave");

            $.jGrowl.defaults.closerTemplate = '<div>[ cerrar todo ]</div>';

            function log(msg, error) {
                var sticky = false;
                var theme = "success";
                if (error) {
                    sticky = true;
                    theme = "error";
                }
                $.jGrowl(msg, {
                    speed      : 'slow',
                    sticky     : sticky,
                    theme      : theme,
                    themeState : ''
                });

//                console.log(error, message);
//                if (error) {
//                    $.box({
//                        imageClass : "box_info",
//                        text       : error,
//                        title      : "Error",
//                        iconClose  : false,
//                        dialog     : {
//                            resizable     : false,
//                            draggable     : false,
//                            closeOnEscape : false,
//                            buttons       : {
//                                "Aceptar" : function () {
//                                }
//                            }
//                        }
//                    });
//                }
            }

            function submitFormSeccion(btn) {
                var $form = $("#frmSave");
                if ($form.valid()) {
                    btn.replaceWith(spinner);
                    var url = $form.attr("action");
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : $form.serialize(),
                        success : function (msg) {
                            if (msg.startsWith("NO")) {
                                var p = msg.split("_");
                                log(p[1], true);
                            } else {
                                var $sec = addSeccion($.parseJSON(msg), true);
                                if ($sec) {
                                    $("#modal").modal("hide");
                                    $('html, body').animate({
                                        scrollTop : $sec.offset().top
                                    }, 2000);
                                }
                                log("Elemento creado existosamente", false);
                            }
                        }
                    });
                }
            }

            function submitFormParrafo(btn, num, $div, add, $replace) {
                var $form = $("#frmSave");
                if ($form.valid()) {
                    btn.replaceWith(spinner);

                    var url = $form.attr("action");
                    $.ajax({
                        type    : "POST",
                        url     : url,
                        data    : $form.serialize(),
                        success : function (msg) {
                            if (msg.startsWith("NO")) {
                                var p = msg.split("_");
                                log(p[1], true);
                            } else {
                                var $sec;
                                if (add) {
                                    $sec = addParrafo($.parseJSON(msg), num, $div);
                                    if ($sec) {
                                        $('html, body').animate({
                                            scrollTop : $div.parents(".seccion").offset().top
                                        }, 2000);
                                    }
                                    log("Elemento creado existosamente", false);
                                } else {
                                    $sec = addParrafo($.parseJSON(msg), num, $div, $replace);
                                }
                                $("#modal").modal("hide");
                                editable($sec.find(".editable"));
                            }
                            location.reload();
                        }
                    });
                }
            }

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

                    var titulo = CKEDITOR.instances["seccion_" + $sec.data("id")].getData();

                    $sec.data({
                        numero : num,
                        titulo : titulo
                    });
                    numerosParrafos($sec);
                    cont++;
                });
            }

            function numerosSeccionesBajar() {

                var cont = 1;

                $(".seccion").each(function () {
                    var $sec = $(this);
                    var $titulo = $sec.find(".tituloSeccion");
                    var $num = $titulo.find(".numero");
                    var num = $.trim($num.text());
                    num = $.trim(str_replace(".-", "", num));
                    console.log("num " + num)

                    if (parseInt(num) != cont) {
                        num = cont;
                        console.log("num " + num)
                        $num.text(num + ".-");
                    }

                    var titulo = CKEDITOR.instances["seccion_" + $sec.data("id")].getData();

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
//                console.log("numero par " + numSec )
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

                    var contenido = CKEDITOR.instances["parrafo_" + $par.data("id")].getData();

                    $par.data({
                        numero    : num,
                        contenido : contenido
                    });
                    cont++;
                });

            }

            function addParrafo(data, num, $div, $replace, isEditable) {
                if (data.contenido == "null" || data.contenido == null) {
                    data.contenido = "";
                }

                var clase = "editable", contentEditable = "contenteditable='true'";
                if (!isEditable) {
                    clase = "";
                    contentEditable = "";
                }

                var $parr = $("<div class='parrafo'></div>");
                var $titulo = $("<div class='row tituloParrafo '></div>");
                $("<div class='span1 numero lvl2 bold'>" + num + "." + data.numero + ".-</div>").appendTo($titulo);
                var $edit = $("<div class='span9 contParrafo " + clase + " ui-corner-all' id='parrafo_" + data.id + "' " + contentEditable + ">" + data.contenido + "</div>").appendTo($titulo);
                if (data.tipoTabla) {
                    tipoTabla(data.tipoTabla, $titulo);
                }
                var $btnTabla = $('<a href="#" class="btn btn-mini" style="margin-left: 10px;">Modificar tabla</a>');
                var $btnEliminarParrafo = $('<a href="#" class="btn btn-delete btn-mini" style="margin-left: 10px;"><i class="icon-minus"></i> Eliminar párrafo</a>');

                $btnTabla.click(function () {
                    $.ajax({
                        type    : "POST",
                        data    : {
                            id : data.id
                        },
                        url     : "${createLink(controller: 'parrafo', action:'form_ext_ajax')}",
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitFormParrafo(btnSave, data.numero, $parr, false, $parr);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Modificar Párrafo");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal").modal("show");
                        }
                    });
                    return false;
                });

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

                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(controller: 'parrafo', action: 'delete_ext')}",
                                        data    : {
                                            id : data.id
                                        },
                                        success : function (msg) {
                                            var p = msg.split("_");
                                            if (p[0] == "OK") {
                                                log(p[1], false);
                                                $del.remove();
                                                numerosParrafos($div.parents(".seccion"));
                                            } else {
                                                log(p[1], true);
                                            }
                                        }
                                    });
                                },
                                "Cancelar" : function () {
                                }
                            }
                        }
                    });
                    return false;
                });

                if (isEditable) {
                    $titulo.append($btnEliminarParrafo).append($btnTabla);
                }
                $titulo.appendTo($parr);
                if ($replace) {
                    $replace.replaceWith($parr);
                } else {
                    $parr.appendTo($div);
                }

                $parr.data({
                    id        : data.id,
                    numero    : data.numero,
                    contenido : data.contenido,
                    tipoTabla : data.tipoTabla
                });
                return $parr;
            }

            function addSeccion(data, isEditable) {
//                console.log(data)
                var $seccion = $("<div class='seccion ui-corner-all'></div>");
                var $titulo = $("<div class='row tituloSeccion'></div>");
                $("<div class='span1 numero lvl1 bold'>" + data.numero + ".-</div>").appendTo($titulo);

                var clase = "editable", contentEditable = "contenteditable='true'";
                if (!isEditable) {
                    clase = "";
                    contentEditable = "";
                }

                var $edit = $("<div class='span9 lblSeccion " + clase + " ui-corner-all' id='seccion_" + data.id + "' " + contentEditable + ">" + data.titulo + "</div>").appendTo($titulo);
                var $btnAddParrafo = $('<a href="#" class="btn btn-show btn-mini pull-right" style="margin-left: 10px;"><i class="icon-plus"></i> Agregar párrafo</a>');
                var $btnEliminarSeccion = $('<a href="#" class="btn btn-danger btn-mini pull-right" style="margin-left: 10px;"><i class="icon-minus"></i> Eliminar sección</a>');

                var $btnSubir = $('<a href="#" class="btn btn-bajar btn-mini"><i class="icon-arrow-up"></i></a>');
//                var $btnBajar = $('<a href="#" class="btn btn-bajar btn-mini"><i class="icon-arrow-down"></i></a>');

                var $botones = $("<div class='botones'></div>");
//                $botones.append($btnSubir).append($btnBajar);
                $botones.append($btnSubir);

                $btnSubir.click(function () {
                    var $prev = $seccion.prev();

                    if ($prev) {
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(controller:'seccion', action: 'updateNumeros')}",
                            data    : {
                                acta   : "${actaInstance.id}",
                                id     : data.id,
                                numero : $seccion.data("numero") - 1
                            },
                            success : function (msg) {
                                var p = msg.split("_");
                                if (p[0] == "OK") {
                                    $prev.before($seccion);
                                    numerosSecciones();
                                } else {
                                    log(p[1], true);
                                }
                            }
                        });
                    }
                    return false;
                });
                %{--$btnBajar.click(function () {--}%
                    %{--var $next = $seccion.next();--}%
                    %{--if ($next) {--}%
                        %{--$.ajax({--}%
                            %{--type    : "POST",--}%
                            %{--url     : "${createLink(controller:'seccion', action: 'updateNumeros')}",--}%
                            %{--data    : {--}%
                                %{--acta   : "${actaInstance.id}",--}%
                                %{--id     : data.id,--}%
                                %{--numero : $seccion.data("numero") + 1--}%
                            %{--},--}%
                            %{--success : function (msg) {--}%
                                %{--var p = msg.split("_");--}%
                                %{--if (p[0] == "OK") {--}%
                                    %{--$next.after($seccion);--}%
                                    %{--numerosSeccionesBajar();--}%
                                %{--} else {--}%
                                    %{--log(p[1], true);--}%
                                %{--}--}%
                            %{--}--}%
                        %{--});--}%
                    %{--}--}%
                    %{--return false;--}%
                %{--});--}%

                if (!data.parrafos) {
                    data.parrafos = [];
                }

                var parrafos = data.parrafos.length;

                var $parr = $("<div class='row parrafos'></div>");

                $btnAddParrafo.click(function () {
                    $.ajax({
                        type    : "POST",
                        data    : {
                            seccion : data.id,
                            numero  : parrafos + 1
                        },
                        url     : "${createLink(controller: 'parrafo', action:'form_ext_ajax')}",
                        success : function (msg) {
                            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                            var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                            btnSave.click(function () {
                                submitFormParrafo(btnSave, data.numero, $parr, true);
                                return false;
                            });

                            $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                            $("#modalTitle").html("Crear Párrafo");
                            $("#modalBody").html(msg);
                            $("#modalFooter").html("").append(btnOk).append(btnSave);
                            $("#modal").modal("show");
                        }
                    });
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
                                    $.ajax({
                                        type    : "POST",
                                        url     : "${createLink(controller: 'seccion', action: 'delete_ext')}",
                                        data    : {
                                            id : data.id
                                        },
                                        success : function (msg) {
                                            var p = msg.split("_");
                                            if (p[0] == "OK") {
                                                log(p[1], false);
                                                $del.remove();
                                                numerosSecciones();
                                            } else {
                                                log(p[1], true);
                                            }
                                        }
                                    });
                                },
                                "Cancelar" : function () {
                                }
                            }
                        }
                    });

                    return false;
                });

                if (isEditable) {
                    $titulo.append($btnAddParrafo).append($btnEliminarSeccion);
                }
                $titulo.appendTo($seccion);

                $seccion.data({
                    id     : data.id,
                    numero : data.numero,
                    titulo : data.titulo
                });
                var parrafosAdded = [];
                if (parrafos > 0) {
                    for (var i = 0; i < data.parrafos.length; i++) {
                        var $par = addParrafo(data.parrafos[i], data.numero, $parr, false, isEditable);
                        parrafosAdded.push($par)
                    }
                }

                $parr.appendTo($seccion);

                $("#secciones").append($seccion);
                if (isEditable) {
                    $seccion.append($botones);
                }

                secciones++;
                if (isEditable) {
                    editable($edit);
                    for (var i = 0; i < parrafosAdded.length; i++) {
                        editable(parrafosAdded[i].find(".editable"));
                    }
                }
                return $seccion;
            }

            function initSecciones() {
                var secciones = ${secciones};
                for (var i = 0; i < secciones.length; i++) {
                    addSeccion(secciones[i], ${editable});
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
                    var data = $("#frmSave-Acta").serialize();
                    $("#frmSave-Acta").submit();
                    numerosSecciones();
//                    var data = $("#frmSave-Acta").serialize();
//                    data += "&descripcion=" + CKEDITOR.instances.descripcion.getData();
//                    var secc = getSecciones();
//                    data += secc != "" ? "&" + secc : "";
//                    console.log(data);
                }
            }

            function editable($elm) {
                var id = $elm.attr("id");
                var p = id.split("_");
                CKEDITOR.config.toolbar_descripcion = [
                    ['Undo', 'Redo'],
                    ['Bold', 'Italic', 'Underline'],
                    ['Subscript', 'Superscript'],
                    ['NumberedList', 'BulletedList'],
                    ['Outdent', 'Indent']
                ];
                CKEDITOR.config.toolbar_seccion = [
                    ['Undo', 'Redo'],
                    ['Bold', 'Italic', 'Underline'],
                    ['Subscript', 'Superscript']
                ];
                CKEDITOR.config.toolbar_parrafo = [
                    ['Undo', 'Redo'],
                    ['Bold', 'Italic', 'Underline'],
                    ['Subscript', 'Superscript'],
                    ['NumberedList', 'BulletedList'],
                    ['Outdent', 'Indent']
                ];

                try {
                    CKEDITOR.inline(id, {
                        toolbar : p[0],
                        on      : {
                            blur : function (event) {
                                var data = event.editor.getData();
                                var url, datos;
                                switch (p[0]) {
                                    case "descripcion":
                                        url = "${createLink(controller: 'acta', action: 'updateDescripcion')}";
                                        datos = {
                                            id          : "${actaInstance.id}",
                                            descripcion : data
                                        };
                                        break;
                                    case "seccion":
                                        url = "${createLink(controller: 'seccion', action: 'save_ext')}";
                                        datos = {
                                            id     : $elm.parents(".seccion").data("id"),
                                            titulo : data
                                        };
                                        break;
                                    case "parrafo":
                                        url = "${createLink(controller: 'parrafo', action: 'save_ext')}";
                                        datos = {
                                            id        : $elm.parents(".parrafo").data("id"),
                                            contenido : data
                                        };
                                        break;
                                }
                                <g:if test="${actaInstance.id}">
                                $.ajax({
                                    type    : "POST",
                                    url     : url,
                                    data    : datos,
                                    success : function (msg) {
                                        var p = msg.split("_");
                                        if (p[0] == "NO") {
                                            log(p[1], p[0] == "NO");
                                        }
                                    }
                                });
                                </g:if>
                            }
                        }
                    });
                } catch (e) {
//                    console.log(e);
                }
            }

            $(function () {

                CKEDITOR.disableAutoInline = true;

                initSecciones();

                $('[rel=tooltip]').tooltip();

                $(".editable").each(function () {
                    editable($(this));
                });

//                CKEDITOR.inline('editable', {
//                    on : {
//                        blur : function (event) {
//                            var data = event.editor.getData();
//                            console.log(data);
//                        }
//                    }
//                });

                $("#btnPrint").click(function () {
                    var url = "${createLink(controller: 'pdf', action: 'pdfLink')}?url=${createLink(controller:
                      'reportesPlanillas', action: 'actaRecepcion', id:actaInstance.id)}";
//                    console.log(url);
                    location.href = url;
                });

                $("#btnPrintCmpl").click(function () {
                    var url = "${createLink(controller: 'pdf', action: 'pdfLink')}?url=${createLink(controller:
                      'reportesPlanillas', action: 'actaRecepcionTotl', id:actaInstance.id)}";
//                    console.log(url);
                    location.href = url;
                });

                $("#btnRegistro").click(function () {
                    $.box({
                        imageClass : "box_info",
                        text       : "<span class='warning'>Por favor verifique que el acta que va a registrar sea la que fue firmada, " +
                                     "pues <span class='strongWarning'>una vez registrada el acta NO se puede modificar y el registro es definitivo y NO se puede deshacer.</span>",
                        title      : "Alerta",
                        iconClose  : false,
                        dialog     : {
                            width         : 450,
                            resizable     : false,
                            draggable     : false,
                            closeOnEscape : false,
                            buttons       : {
                                "Registrar" : function () {
                                    location.href = "${createLink(action:'registrar', id:actaInstance.id)}";
                                },
                                "Cancelar"  : function () {
                                }
                            }
                        }
                    });
                });

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
                                var btnSave = $('<a href="#" id="btnSaveSeccion" class="btn btn-success"><i class="icon-save"></i> Guardar</a>');

                                btnSave.click(function () {
                                    submitFormSeccion(btnSave);
                                    return false;
                                });

                                $("#modalHeader").removeClass("btn-edit btn-show btn-delete");
                                $("#modalTitle").html("Crear Sección");
                                $("#modalBody").html(msg);
                                $("#modalFooter").html("").append(btnOk).append(btnSave);
                                $("#modal").modal("show");
                                $("#titulo").focus();
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

//                $("input").keyup(function (ev) {
//                    if (ev.keyCode == 13) {
//                        submitForm($(".btn-success"));
//                    }
//                });
            });
        </script>

    </body>
</html>