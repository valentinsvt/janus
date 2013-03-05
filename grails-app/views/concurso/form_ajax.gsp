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
        <a href="#" class="btn btn-success" id="btnSave">
            <i class="icon-save"></i> Guardar
        </a>
        <a href="#" class="btn" id="btnRegi">
            <i class="icon-exchange"></i> Cambiar Estado
        </a>
    </div>
</div>


<g:form class="form-horizontal" name="frmSave-Concurso" action="save" id="${concursoInstance?.id}">
<div class="row">
<div class="span10">
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Objeto
            </span>
        </div>

        <div class="controls">
            <g:textField name="objeto" class="span8" value="${concursoInstance?.objeto}"/>
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
</div>

<div class="span5">
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Obra
            </span>
        </div>

        <div class="controls">
            <input type="hidden" id="obra_id" name="obra.id" value="${concursoInstance?.obra?.id}">
            <input type="text" id="obra_busqueda" value="${concursoInstance?.obra?.codigo}" title="${concursoInstance?.obra?.nombre}">
            %{--<g:select id="obra" name="obra.id" from="${janus.Obra.list([sort: 'nombre'])}" optionKey="id" class="many-to-one " value="${concursoInstance?.obra?.id}"--}%
                      %{--noSelection="['null': '']" optionValue="${{ it.descripcion && it.descripcion.size() > 55 ? it.nombre + " " + it.descripcion[0..45] + '...' : it.nombre + " " + it.descripcion }}"/>--}%
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
                Fe. Lím. Preguntas
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
                Fec. Lím. Entrega Ofertas
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
                Fec. Lím. Res. Convalidación
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
                Estado
            </span>
        </div>

        <div class="controls">
            %{--<g:textField name="estado" class="" value="${concursoInstance?.estado}" />--}%
            %{--<p class="help-block ui-helper-hidden"></p>--}%
            <g:hiddenField name="estado" value="${concursoInstance?.estado}"/>   ${concursoInstance?.estado}
        </div>
    </div>

    %{--<div class="control-group">--}%
        %{--<div>--}%
            %{--<span class="control-label label label-inverse">--}%
                %{--Presupuesto referencial--}%
            %{--</span>--}%
        %{--</div>--}%

        %{--<div class="controls">--}%
            %{--<g:field type="number" name="presupuestoReferencial" class="required number" value="${concursoInstance?.presupuestoReferencial ?: 0}" style="text-align: right"   />--}%
            %{--<p class="help-block ui-helper-hidden"></p>--}%
        %{--</div>--}%
    %{--</div>--}%


</div>

<div class="span5">
    <div class="control-group">
        <div>
            <span class="control-label label label-inverse">
                Administración
            </span>
        </div>

        <div class="controls">
            %{--<g:select id="administracion" name="administracion.id" from="${janus.Administracion.list()}" optionKey="id" class="many-to-one " value="${concursoInstance?.administracion?.id}" noSelection="['null': '']"/>--}%
            <g:hiddenField name="administracion.id" value="${concursoInstance?.administracion?.id}"/>
            ${concursoInstance?.administracion?.fechaInicio?.format("dd-MM-yyyy")} a ${concursoInstance?.administracion?.fechaFin?.format("dd-MM-yyyy")} (${concursoInstance?.administracion?.nombrePrefecto})
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
            %{--<g:textField name="codigo" class="" value="${concursoInstance?.codigo}"/>--}%
            <span class="uneditable-input">${concursoInstance?.codigo}</span>

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
                Fec. Lím. Respuestas
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
                Fec. Lím. Sol. Convalidación
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
                Presupuesto referencial
            </span>
        </div>

        <div class="controls">
            <g:field type="number" name="presupuestoReferencial" class="required number" value="${concursoInstance?.presupuestoReferencial ?: 0}" style="text-align: right"   />
            <p class="help-block ui-helper-hidden"></p>
        </div>
    </div>
</div>

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
</g:form>
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

<script type="text/javascript">
    function cargarDatos(){

        $.ajax({type : "POST", url : "${g.createLink(controller: 'concurso',action:'datosObra')}",
            data     : "obra="+$("#obra_id").val(),
            success  : function (msg) {
//                console.log(msg)
                var parts=msg.split("&&")
                $("#presupuestoReferencial").val(number_format(parts[3], 2, ".", " "))
                $("#obra_busqueda").val(parts[0]).attr("title",parts[1])

            }
        });
    }
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

    $("#obra_busqueda").dblclick(function(){
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

        var esta = $("#estado").val()

        if(esta =='R'){

            $("#estado").val("N");
            $("#frmSave-Concurso").submit();
        }else {

            $("#estado").val("R");
            $("#frmSave-Concurso").submit();

        }


    });

</script>
</body>
</html>
