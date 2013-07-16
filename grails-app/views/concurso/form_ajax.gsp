<%@ page import="janus.pac.Concurso" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Lista de Concursos
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.livequery.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
    <style>
    td {
        line-height : 12px !important;
    }

    .row {
        height : 35px;;
    }

    .tab-content {
        padding : 10px;
        border  : solid 1px #555;
    }

    #myTab {
        margin-bottom : 0;
        border        : solid 1px #555;
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

<div class="row" style="margin-bottom: 10px;">
    <div class="span9 btn-group" role="navigation">
        <g:link controller="concurso" action="list" class="btn">
            <i class="icon-angle-left"></i> Regresar
        </g:link>
    %{--<input type="SUBMIT" value="Guardar" class="btn btn-primary">--}%
        <g:if test="${concursoInstance.estado != 'R'}">
            <a href="#" class="btn btn-success" id="btnSave">
                <i class="icon-save"></i> Guardar
            </a>
        </g:if>
        <a href="#" class="btn" id="btnRegi">
            <i class="icon-exchange"></i> Cambiar Estado
        </a>
    </div>
</div>


<div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;height: 150px;margin-bottom: 10px;">
    <p class="css-vertical-text" style="font-size: 30px;margin-left: -7px"><i class="icon-arrow-left active" id="min" style="cursor: pointer" title="Ocultar"></i> <span id="msg" title="Ocultar" style="cursor: pointer">P.A.C.</span>
    </p>

    <div class="linea" style="height: 100%"></div>

    <div class="row " id="mostrar" style="display: none;cursor: pointer">
        <div class="span10">
            <b>Ver P.A.C.</b>
        </div>

    </div>

    <div class="row header">
        <div class="span10">
            <span class="control-label label label-inverse span2" style="width: 135px;">
                Tipo Procedimiento:
            </span>

            <div class="controls span2">
                ${concursoInstance?.pac?.tipoProcedimiento?.descripcion}
            </div>
            <span class="control-label label label-inverse span2" style="width: 100px;">
                Tipo Compra:
            </span>

            <div class="controls span1">
                ${concursoInstance?.pac?.tipoCompra?.descripcion}
            </div>
            <span class="control-label label label-inverse span1">
                Código cp:
            </span>

            <div class="controls span1" title=" ${concursoInstance?.pac?.cpp?.descripcion}">
                ${concursoInstance?.pac?.cpp?.numero}
            </div>

        </div>
    </div>

    <div class="row header">
        <div class="span10">
            <span class="control-label label label-inverse span2" style="width: 135px;">
                Partida:
            </span>

            <div class="controls span7" title="">
                ${concursoInstance?.pac?.presupuesto?.numero} (${concursoInstance?.pac?.presupuesto?.descripcion})
            </div>
        </div>

    </div>

    <div class="row header">
        <div class="span10">
            <span class="control-label label label-inverse span1">
                Descripción:
            </span>

            <div class="controls span8" title="">
                ${concursoInstance?.pac?.descripcion}
            </div>

        </div>

    </div>

    <div class="row header">
        <div class="span10">
            <span class="control-label label label-inverse span1">
                Cantidad:
            </span>

            <div class="controls span1" title="">
                ${concursoInstance?.pac?.cantidad}
            </div>
            <span class="control-label label label-inverse span1">
                Unidad:
            </span>

            <div class="controls span1" title="">
                ${concursoInstance?.pac?.unidad?.descripcion}
            </div>
            <span class="control-label label label-inverse span1">
                Precio U.:
            </span>

            <div class="controls span1" title="">
                ${concursoInstance?.pac?.costo.round(2)}
            </div>
            <span class="control-label label label-inverse span1">
                Total:
            </span>

            <div class="controls span1" title="">
                ${(concursoInstance?.pac?.costo * concursoInstance?.pac?.cantidad).round(2)}
            </div>
        </div>
    </div>
</div>

<div style="border-bottom: 1px solid black;padding-left: 50px;position: relative;height: 590px;margin-bottom: 10px;">
<p class="css-vertical-text">Concurso</p>

<div class="linea" style="height: 100%"></div>

<g:form class="form-horizontal" name="frmSave-Concurso" action="save" id="${concursoInstance?.id}">
<ul class="nav nav-pills ui-corner-top" id="myTab">
    <li class="active"><a href="#datos">Datos concurso</a></li>
    <li><a href="#fechas">Fechas del concurso</a></li>
    <li><a href="#fechas2">Fechas de control del trámite</a></li>
</ul>

<div class="tab-content ui-corner-bottom">
<div class="tab-pane active" id="datos">
    <div class="row">
        <div class="span10">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Objeto
                    </span>
                </div>

                <div class="controls">
                    <g:textArea name="objeto" class="span8" value="${concursoInstance?.objeto}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </div>

        <div class="span5">

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Estado
                    </span>
                </div>

                <div class="controls">
                    %{--
                    <g:textField name="estado" class="" value="${concursoInstance?.estado}"/>
                    --}%
                    %{--<p class="help-block ui-helper-hidden"></p>--}%
                    <g:hiddenField name="estado" value="${concursoInstance?.estado}"/>
                    ${concursoInstance?.estado == 'R' ? 'Registrado' : 'No registrado'}
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Memo de requerimiento
                    </span>
                </div>

                <div class="controls">
                    <g:textField name="memoRequerimiento" value="${concursoInstance?.memoRequerimiento}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Obra requerida
                    </span>
                </div>

                <div class="controls">
                    <input type="hidden" id="obra_id" name="obra.id" value="${concursoInstance?.obra?.id}">
                    <input type="text" id="obra_busqueda" value="${concursoInstance?.obra?.codigo}" title="${concursoInstance?.obra?.nombre}">
                    %{--
                    <g:select id="obra" name="obra.id" from="${janus.Obra.list([sort: 'nombre'])}" optionKey="id" class="many-to-one " value="${concursoInstance?.obra?.id}"
                    --}%
                    %{--noSelection="['null': '']"
                    optionValue="${{ it.descripcion && it.descripcion.size() > 55 ? it.nombre + " " + it.descripcion[0..45] + '...' : it.nombre + " " + it.descripcion }}"/>--}%
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Pac
                    </span>
                </div>

                <div class="controls">
                    <g:select id="pac" name="pac.id" from="${janus.pac.Pac.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.pac?.id}"
                              noSelection="['null': '']" optionValue="${{ it.descripcion && it.descripcion.size() > 55 ? it.descripcion[0..55] + '...' : it.descripcion }}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

        </div> <!-- fin col 1-->

        <div class="span5">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Administración
                    </span>
                </div>

                <div class="controls">
                    %{--
                    <g:select id="administracion" name="administracion.id" from="${janus.Administracion.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.administracion?.id}" noSelection="['null': '']"/>
                    --}%
                    <g:hiddenField name="administracion.id" value="${concursoInstance?.administracion?.id}"/>
                    %{--${concursoInstance?.administracion?.fechaInicio?.format("dd-MM-yyyy")} a--}%
                    %{--${concursoInstance?.administracion?.fechaFin?.format("dd-MM-yyyy")}--}%
                    ${concursoInstance?.administracion?.nombrePrefecto}
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Código
                    </span>
                </div>

                <div class="controls">
                    %{--
                    <g:textField name="codigo" class="" value="${concursoInstance?.codigo}"/>
                    --}%
                    %{--<span class="uneditable-input">${concursoInstance?.codigo}</span>--}%
                    <g:textField name="codigo" value="${concursoInstance?.codigo}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Costo Bases
                    </span>
                </div>

                <div class="controls">
                    <g:field type="number" name="costoBases" class="" value="${fieldValue(bean: concursoInstance, field: 'costoBases')}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Monto adjudicado
                    </span>
                </div>

                <div class="controls">
                    <div class="input-append">
                        <g:field type="number" name="presupuestoReferencial" class="required number" value="${concursoInstance?.presupuestoReferencial ?: 0}" style="text-align: right;width: 180px;"/>
                        <span class="add-on">$</span>
                    </div>

                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </div> <!-- fin col 2-->

        <div class="span10">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Observaciones
                    </span>
                </div>

                <div class="controls">
                    <g:textField name="observaciones" class="span8" value="${concursoInstance?.observaciones}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>
        </div>
    </div>
</div> <!-- fin tab datos -->

<div class="tab-pane" id="fechas">
%{--<div id="cols" style="float: left;">--}%
<div class="row">
    <div class="span5">
        <a  href="#" id="tramites" class="btn btn-primary">
            <i class="icon-search"></i> Ver tramites S.A.D.
        </a>
    </div>
</div>
<div class="row">
    <div class="span5">
        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Publicación
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaPublicacion" class="" value="${concursoInstance?.fechaPublicacion}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Lím. Preguntas
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaLimitePreguntas" class="" value="${concursoInstance?.fechaLimitePreguntas}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Lím. Respuestas
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaLimiteRespuestas" class="" value="${concursoInstance?.fechaLimiteRespuestas}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Lím. Entrega Ofertas
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaLimiteEntregaOfertas" class="" value="${concursoInstance?.fechaLimiteEntregaOfertas}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Apertura Ofertas
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaAperturaOfertas" class="" value="${concursoInstance?.fechaAperturaOfertas}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Sol. Convalidación
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaLimiteSolicitarConvalidacion" class="" value="${concursoInstance?.fechaLimiteSolicitarConvalidacion}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Recibir Convalidación
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaLimiteRespuestaConvalidacion" class="" value="${concursoInstance?.fechaLimiteRespuestaConvalidacion}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Ini. Eval. Oferta
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaInicioEvaluacionOferta" class="" value="${concursoInstance?.fechaInicioEvaluacionOferta}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </div> <!-- fin col 1 -->

    <div class="span5">


        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Límite res. fin.
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaLimiteResultadosFinales" class="" value="${concursoInstance?.fechaLimiteResultadosFinales}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Adjudicación
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaAdjudicacion" class="" value="${concursoInstance?.fechaAdjudicacion}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Inicio
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaInicio" class="" value="${concursoInstance?.fechaInicio}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Calificación
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaCalificacion" class="" value="${concursoInstance?.fechaCalificacion}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Inicio Puja
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaInicioPuja" class="" value="${concursoInstance?.fechaInicioPuja}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha Fin Puja
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaFinPuja" class="" value="${concursoInstance?.fechaFinPuja}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>

        <div class="control-group">
            <div>
                <span class="control-label label label-inverse">
                    Fecha aceptación proveedor
                </span>
            </div>

            <div class="controls">
                <elm:datepicker name="fechaAceptacionProveedor" class="" value="${concursoInstance?.fechaAceptacionProveedor}"/>
                <p class="help-block ui-helper-hidden"></p>
            </div>
        </div>
    </div> <!-- fin col 2-->
</div>
</div> <!-- fin tab fechas -->

<div class="tab-pane" id="fechas2">
    %{--<div id="cols" style="float: left;">--}%


    <div class="row">
        <div class="span5">
            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Inicio Preparatorio
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaInicioPreparatorio" class="" value="${concursoInstance?.fechaInicioPreparatorio ?: concursoInstance?.fechaPublicacion}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Fin Preparatorio
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaFinPreparatorio" class="" value="${concursoInstance?.fechaFinPreparatorio}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Inicio Precontractual
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaInicioPrecontractual" class="" value="${concursoInstance?.fechaInicioPrecontractual}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Fin Precontractual
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaFinPrecontractual" class="" value="${concursoInstance?.fechaFinPrecontractual}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

        </div> <!-- fin col 1 -->

        <div class="span5">

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Inicio Contractual
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaInicioContractual" class="" value="${concursoInstance?.fechaInicioContractual}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

            <div class="control-group">
                <div>
                    <span class="control-label label label-inverse">
                        Fecha Fin Contractual
                    </span>
                </div>

                <div class="controls">
                    <elm:datepicker name="fechaFinContractual" class="" value="${concursoInstance?.fechaFinContractual}"/>
                    <p class="help-block ui-helper-hidden"></p>
                </div>
            </div>

        </div> <!-- fin col 2-->
    </div>
</div> <!-- fin tab fechas2 -->

</div>
</g:form>

</div>

<div class="modal grandote hide fade" id="modal-busqueda" style="overflow: hidden">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">x</button>

        <h3 id="modalTitle_busqueda"></h3>

    </div>

    <div class="modal-body" id="modalBody">
        <bsc:buscador name="obras" value="" accion="buscarObra" controlador="concurso" campos="${campos}" label="Obras" tipo="lista"/>

    </div>

    <div class="modal-footer" id="modalFooter_busqueda">

    </div>

</div>

<div class="modal grandote hide fade " id="modal-tramite" style=";overflow: hidden;">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle_tramite">Tramites</h3>
    </div>

    <div class="modal-body" id="modal-tramite-body">

    </div>

    <div class="modal-footer" id="modalTramite_busqueda">
    </div>
</div>

<script type="text/javascript">
    function cargarDatos() {

        $.ajax({type : "POST", url : "${g.createLink(controller: 'concurso',action:'datosObra')}",
            data     : "obra=" + $("#obra_id").val(),
            success  : function (msg) {
//                ////console.log(msg)
                var parts = msg.split("&&")
                $("#presupuestoReferencial").val(number_format(parts[3], 2, ".", " "))
                $("#obra_busqueda").val(parts[0]).attr("title", parts[1])

            }
        });
    }

    $('#myTab a').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    });

    $("#frmSave-Concurso").validate({
        errorPlacement : function (error, element) {
            element.parent().find(".help-block").html(error).show();
        },
        success        : function (label) {
            label.parent().hide();
        },
        errorClass     : "label label-important",
        submitHandler  : function (form) {
            $(".btn-success").replaceWith(spinner);
            form.submit();
        }
    });

    $("#min").click(function () {
        if ($(this).hasClass("active")) {
            $(".header").hide("slide");
            $("#msg").hide();
            $("#min").removeClass("icon-arrow-left").removeClass("active").addClass("icon-arrow-right");
            $(this).attr("title", "Mostrar");
            $(this).parent().parent().animate({
                height : 35
            });
            $("#mostrar").show()
        } else {
            $(".header").show("slide");
            $("#msg").show();
            $("#min").removeClass("icon-arrow-right").addClass("active").addClass("icon-arrow-left");
            $(this).attr("title", "Ocultar");
            $("#mostrar").hide("");
            $(this).parent().parent().animate({
                height : 150
            })
        }

    });
    $("#msg").click(function () {
        $("#min").click();
    });
    $("#mostrar").click(function () {
        $("#min").click();
    });

    $("#obra_busqueda").dblclick(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle_busqueda").html("Lista de obras");
        $("#modalFooter_busqueda").html("").append(btnOk);
        $("#modal-busqueda").modal("show");
        $("#contenidoBuscador").html("")
    });

    $("#btnSave").click(function () {
        $("#frmSave-Concurso").submit();
    });

    $("input").keyup(function (ev) {
        if (ev.keyCode == 13) {
            submitForm($(".btn-success"));
        }
    });

    $("#btnRegi").click(function () {
        var obraId = $.trim($("#obra_id").val());
        if (obraId != "") {
            var esta = $("#estado").val();
            if (esta == 'R') {
                $("#estado").val("N");
            } else {
                $("#estado").val("R");
            }
            $("#frmSave-Concurso").submit();
        } else {
            alert("Seleccione una obra!");
        }
    });
    $("#tramites").click(function(){
        $.ajax({
            type    : "POST",
            url     : "${g.createLink(action:'verTramitesAjax',controller: 'tramite',id: concursoInstance?.memoRequerimiento)}",
            success : function (msg) {
                $("#modal-tramite-body").html(msg);
                $("#modal-tramite").modal("show");
            }
        });
    })

</script>
</body>
</html>
