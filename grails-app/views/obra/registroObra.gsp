<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 11/27/12
  Time: 11:54 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">


    <style type="text/css">

    .formato {
        font-weight : bolder;
    }

    .titulo {
        font-size : 20px;
    }

    .error {
        background: #c17474;
    }
    </style>

    <title>Registro de Obras</title>
</head>

<body>
%{--Todo Arreglar: la comunidad no guarda nada--}%
%{--Todo Por hacer: imprimir, Formula pol, Fp liquidacion, rubros , documentos, composicion, tramites--}%
<div class="btn-group" style="margin-bottom: 10px">
    <button class="btn" id="lista"><i class="icon-book"></i> Lista</button>
    <button class="btn" id="nuevo"><i class="icon-plus"></i> Nuevo</button>
    <button class="btn" id="btn-aceptar"><i class="icon-ok"></i> Grabar</button>
    <button class="btn" id="cancelarObra"><i class="icon-ban-circle"></i> Cancelar</button>
    <button class="btn"><i class="icon-remove"></i> Eliminar la Obra</button>
    <button class="btn"><i class="icon-print"></i> Imprimir</button>
    <button class="btn" id="cambiarEstado"><i class="icon-retweet"></i> Cambiar de Estados</button>
</div>

<g:form class="registroObra" name="frm-registroObra" action="save">

<fieldset class="borde" style="position: relative; height: 100px">
    <g:hiddenField name="id" value="${obra?.id}"/>
    <div class="span12" style="margin-top: 20px" align="center">

        <p class="css-vertical-text">Ingreso</p>

        <div class="linea" style="height: 85%;"></div>

    </div>

    <div class="span12" style="margin-top: 10px">

        <div class="span1 formato">MEMO</div>

        <div class="span3"><g:textField name="oficioIngreso" class="memo" value="${obra?.oficioIngreso}"/></div>

        <div class="span2 formato">CANTIDAD DE OBRA</div>

        <div class="span3"><g:textField name="memoCantidadObra" class="cantidad" value="${obra?.memoCantidadObra}" /></div>

        <div class="span1 formato">FECHA</div>

        <div class="span1"><elm:datepicker name="fechaCreacionObra" class="fechaCreacionObra datepicker input-small" value="${obra?.fechaCreacionObra}"/></div>

    </div>

</fieldset>
<fieldset class="borde">
    <div class="span12" style="margin-top: 10px">
        <div class="span1">Código</div>

        <g:if test="${obra?.codigo != null}">

            <div class="span3"><g:textField name="codigo" class="codigo required" value="${obra?.codigo}" disabled="true"/></div>

        </g:if>
        <g:else>

            <div class="span3"><g:textField name="codigo" class="codigo required" value="${obra?.codigo}"/></div>

        </g:else>

        <div class="span1">Nombre</div>

        <div class="span6"><g:textField name="nombre" class="nombre required" style="width: 608px" value="${obra?.nombre}"/></div>
    </div>

    <div class="span12">
        <div class="span1">Programa</div>

        <div class="span3"><g:select name="programacion.id" class="programacion required" from="${janus.Programacion?.list()}"  value="${obra?.programacion?.id}" optionValue="descripcion" optionKey="id" /></div>

        <div class="span1">Tipo</div>

        <div class="span3"><g:select name="tipoObjetivo.id" class="tipoObjetivo required" from="${janus.TipoObjetivo?.list()}" value="${obra?.tipoObjetivo?.id}" optionValue="descripcion" optionKey="id" /></div>

        <div class="span1">Clase</div>

        <div class="span1"><g:select name="claseObra.id" class="claseObra required" from="${janus.ClaseObra?.list()}" value="${obra?.claseObra?.id}" optionValue="descripcion" optionKey="id"/></div>
    </div>

    <div class="span12">
        <div class="span1">Referencias</div>

        <div class="span6"><g:textField name="referencia" class="referencia" style="width: 610px" value="${obra?.referencia}"/></div>

        <div class="span1" style="margin-left: 130px">Estado</div>

        <div class="span1">

            <g:hiddenField name="estadoObra" id="hiddenEstado"/>

            <g:if test="${obra?.estado == 'R'}">
                <g:textField name="estado2" id="estadoNombre" class="estado" value="${"Registrado"}" disabled="true"/>

            </g:if>

            <g:else>

                <g:textField name="estado2" id="estadoNombre" class="estado" value="${"No Registrado"}" disabled="true"/>


            </g:else>

        </div>
    </div>

    <div class="span12">
        <div class="span1">Descripción</div>

        <div class="span6"><g:textArea name="descripcion" rows="5" cols="5" class="required"
                                       style="width: 1007px; height: 72px; resize: none" maxlength="511" value="${obra?.descripcion}" /></div>
    </div>

    <div class="span12">

        <div class="span1">Parroquia</div>

        <g:hiddenField name="parroquia.id" id="hiddenParroquia" value="${obra?.comunidad?.parroquia?.id}"/>
        <div class="span3"><g:textField name="parroquia.id" id="parrNombre"  class="parroquia required nowhitespace" value="${obra?.comunidad?.parroquia?.nombre}"  style="width: 215px" disabled="true"/></div>

        <div class="span1">Comunidad</div>

        <g:hiddenField name="comunidad.id" id="hiddenComunidad" value="${obra?.comunidad?.id}"/>
        <div class="span3"><g:textField name="comunidad.id" id="comuNombre" class="comunidad required nowhitespace" value="${obra?.comunidad?.nombre}"  disabled="true"/></div>


        <div class="span2"><button class="btn btn-buscar btn-info" id="btn-buscar"><i class="icon-globe"></i> Buscar
        </button>
        </div>


    </div>

    <div class="span12">

        <div class="span1">Sitio</div>

        <div class="span3"><g:textField name="sitio" class="sitio" value="${obra?.sitio}"/></div>

        <div class="span1">Plazo</div>

        <div class="span2"><g:textField name="plazo" class="plazoMeses required number" style="width: 28px"
                                        maxlength="3" type="number" value="${obra?.plazo}" /> Meses</div>

    </div>

    <div class="span12">
        <div class="span1">Inspección</div>

        <div class="span3"><g:select name="inspector.id" class="inspector required" from="${janus.Persona?.list()}" value="${obra?.inspector?.id}" optionValue="nombre" optionKey="id" /></div>

        <div class="span1">Revisión</div>

        <div class="span3"><g:select name="revisor.id"  class="revisor required" from="${janus.Persona?.list()}" value="${obra?.revisor?.id}" optionValue="nombre" optionKey="id" /></div>

        <div class="span1">Responsable</div>

        <div class="span1"><g:select name="responsableObra.id" class="responsableObra required" from="${janus.Persona?.list()}" value="${obra?.responsableObra?.id}" optionValue="nombre" optionKey="id"/></div>
    </div>

    <div class="span12">
        <div class="span1">Observaciones</div>

        <div class="span6"><g:textField name="observaciones" class="observaciones" style="width: 610px;" value="${obra?.observaciones}"/></div>

        <div class="span1" style="margin-left: 130px">Anticipo</div>

        <div class="span2"><g:textField name="porcentajeAnticipo" type="number" class="anticipo number required" style="width: 70px" value="${obra?.porcentajeAnticipo}"/> %</div>

    </div>

    <div class="span12">

        <div class="span1">Lista</div>
        %{--todo esto es un combo--}%
        %{--<div class="span2" style="margin-right: 70px"><g:textField name="lugar.id" class="lugar" value="${obra?.lugar?.id}" optionKey="id"/></div>--}%


        <div class="span2" style="margin-right: 70px"><g:select name="lugar.id" from="${janus.Lugar.list()}" optionKey="id" optionValue="descripcion"/></div>




        <div class="span1" >Fecha</div>

        <div class="span2"><elm:datepicker name="fechaPreciosRubro" class="fechaPreciosRubro datepicker input-small" value="${obra?.fechaPreciosRubros}"/></div>


    </div>

</fieldset>

<fieldset class="borde" style="position: relative;">

    <div class="span12" style="margin-top: 10px">

        <p class="css-vertical-text">Salida</p>

        <div class="linea" style="height: 85%;"></div>

    </div>

    <div class="span12" style="margin-top: 10px">

        <div class="span1 formato" style="width: 80px">OFICIO SAL.</div>

        <div class="span3" style="margin-left: 18px"><g:textField name="oficioSalida" class="oficio" value="${obra?.oficioSalida}"/></div>

        <div class="span1 formato">MEMO</div>

        <div class="span3"><g:textField name="memoSalida" class="memoSalida" value="${obra?.memoSalida}"/></div>

        <div class="span1 formato">FECHA</div>

        <div class="span1"><elm:datepicker name="fechaOficioSalida" class="fechaOficioSalida datepicker input-small"
                                           value="${obra?.fechaOficioSalida}"/></div>


    </div>

    <div class="span12" style="margin-top: 10px">
        <div class="span1 formato">FORMULA</div>

        <div class="span3"><g:textField name="formulaPolinomica" class="formula" value="${obra?.formulaPolinomica}"/></div>

    </div>

</fieldset>

</g:form>

<div id="busqueda">

    <fieldset class="borde">
        <div class="span7">

            <div class="span2">Buscar Por</div>

            <div class="span2">Criterio</div>

            <div class="span1">Ordenar</div>

        </div>

        <div>
            <div class="span2"><g:select name="buscarPor" class="buscarPor"
                                         from="['1': 'Provincia', '2': 'Cantón', '3': 'Parroquia', '4': 'Comunidad']"
                                         style="width: 120px" optionKey="key"
                                         optionValue="value"/></div>

            <div class="span2" style="margin-left: -20px"><g:textField name="criterio" class="criterio"/></div>

            <div class="span1"><g:select name="ordenar" class="ordenar" from="['1': 'Ascendente', '2': 'Descendente']"
                                         style="width: 120px; margin-left: 60px;" optionKey="key"
                                         optionValue="value"/></div>

            <div class="span2" style="margin-left: 140px"><button class="btn btn-info" id="btn-consultar"><i
                    class="icon-check"></i> Consultar
            </button></div>

        </div>
    </fieldset>

    <fieldset class="borde">

        <div id="divTabla" style="height: 460px; overflow-y:auto; overflow-x: auto;">

        </div>

    </fieldset>

</div>

<div id="estadoDialog">

    <fieldset>
        <div class="span3">
            Está seguro de querer cambiar el estado de la obra:<div style="font-weight: bold;">${obra?.nombre} ?</div>

        </div>
    </fieldset>
</div>


<g:if test="${obra?.id}">
    <div class="navbar navbar-inverse" style="margin-top: 10px;padding-left: 5px" align="center">

        <div class="navbar-inner">
            <div class="botones">

                <ul class="nav">
                    <li><a href="#" id="btnVar"><i class="icon-pencil"></i>Variables</a></li>
                    <li><a href="${g.createLink(controller: 'volumenObra',action: 'volObra',id:obra?.id)}"><i class="icon-list-alt"></i>Vol. Obra</a></li>
                    <li><a href="${g.createLink(controller: 'matriz',action: 'matrizPolinomica',id: obra?.id)}"><i class="icon-th"></i>Matriz FP</a></li>
                    <li><a href="#">Fórmula Pol.</a></li>
                    <li><a href="#">FP Liquidación</a></li>
                    <li><a href="#"><i class="icon-money"></i>Rubros</a></li>
                    <li><a href="#" id="btnDocumentos"><i class="icon-file"></i>Documentos</a></li>
                    <li><a href="${g.createLink(controller: 'cronograma',action: 'cronogramaObra',id:obra?.id)}"><i class="icon-calendar"></i>Cronograma</a></li>
                    <li><a href="#">Composición</a></li>
                    <li><a href="#">Trámites</a></li>

                </ul>

            </div>
        </div>

    </div>
</g:if>


<div class="modal hide fade mediumModal" id="modal-var" style=";overflow: hidden;">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modal_title_var">
        </h3>
    </div>

    <div class="modal-body" id="modal_body_var">

    </div>

    <div class="modal-footer" id="modal_footer_var">
    </div>
</div>

<div class="modal grandote hide fade " id="modal-busqueda" style=";overflow: hidden;">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <h3 id="modalTitle_busqueda"></h3>
    </div>

    <div class="modal-body" id="modalBody" >
        <bsc:buscador name="obra?.buscador.id" value="" accion="buscarObra" controlador="obra" campos="${campos}" label="Obra" tipo="lista"/>
    </div>

    <div class="modal-footer" id="modalFooter_busqueda">
    </div>
</div>



<script type="text/javascript">
    $(function () {

        $("#lista").click(function(){
            var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
            $("#modalTitle_busqueda").html("Lista de obras");
//        $("#modalBody").html($("#buscador_rubro").html());
            $("#modalFooter_busqueda").html("").append(btnOk);
            $(".contenidoBuscador").html("");
            $("#modal-busqueda").modal("show");
        });

        $("#nuevo").click(function() {
//            $("input[type=text]").val("");
//            $("textarea").val("");
//            $("select").val("-1");


            location.href =  "${g.createLink(action: 'registroObra')}";



        });

        $("#cancelarObra").click(function () {

            location.href =  "${g.createLink(action: 'registroObra')}" + "?obra=" + "${obra?.id}";


        });

        $("#cambiarEstado").click(function () {


            if(${obra?.id != null}) {

                $("#estadoDialog").dialog("open")

            }




        });


        $("#frm-registroObra").validate();

        $("#btn-aceptar").click(function () {



            $("#frm-registroObra").submit();


        });


        $("#btn-buscar").click(function () {
            $("#dlgLoad").dialog("close");
            $("#busqueda").dialog("open");
            return false;
//
        });

        $("#btn-consultar").click(function () {
            $("#dlgLoad").dialog("open");
            busqueda();
        });

        $("#btnDocumentos").click(function () {

            location.href  = "${g.createLink(controller: 'documentosObra', action: 'documentosObra')}";


        });

        $("#btnVar").click(function () {
            $.ajax({
                type    : "POST",
                url     : "${createLink(controller: 'variables', action:'variables_ajax')}",
                data    : {
                    //TODO CAMBIAR AQUI!!!
                    obra : "${obra?.id}"
                },
                success : function (msg) {
                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                    btnSave.click(function () {
                        if ($("#frmSave-var").valid()) {
                            btnSave.replaceWith(spinner);
                        }
                        var data = $("#frmSave-var").serialize() + "&id=" + $("#id").val();
                        var url = $("#frmSave-var").attr("action");

//                                console.log(url);
//                                console.log(data);

                        $.ajax({
                            type    : "POST",
                            url     : url,
                            data    : data,
                            success : function (msg) {
                                console.log("Data Saved: " + msg);
                            }
                        });

                        return false;
                    });

                    $("#modal_title_var").html("Variables");
                    $("#modal_body_var").html(msg);
                    $("#modal_footer_var").html("").append(btnCancel).append(btnSave);
                    $("#modal-var").modal("show");
                }
            });
            return false;
        });

        $("#busqueda").dialog({

            autoOpen  : false,
            resizable : false,
            modal     : true,
            draggable : false,
            width     : 800,
            height    : 600,
            position  : 'center',
            title     : 'Datos de Situación Geográfica'


        });


        $("#estadoDialog").dialog({

            autoOpen  : false,
            resizable : false,
            modal     : true,
            draggable : false,
            width     : 350,
            height    : 220,
            position  : 'center',
            title     : 'Cambiar estado de la Obra',
            buttons: {
                "Aceptar": function() {

                    var estadoCambiado = $("#estadoNombre").val();

                    if(estadoCambiado == 'No Registrado') {

                        estadoCambiado = 'Registrado';

                        $("#hiddenEstado").val( $(this).attr("estado"));

                        $("#estadoNombre").val(estadoCambiado)

                        $("#estadoDialog").dialog("close");
                    }

                    else{

                        estadoCambiado = 'No Registrado';
                        $("#hiddenEstado").val( $(this).attr("estado"));

                        $("#estadoNombre").val(estadoCambiado);
                        $("#estadoDialog").dialog("close");

                    }



                },
                "Cancelar": function() {
                    $("#estadoDialog").dialog("close");
                }
            }

        });

        function busqueda() {

            var buscarPor = $("#buscarPor").val();
            var criterio = $(".criterio").val();

            var ordenar = $("#ordenar").val();


//                   console.log("buscar" + buscarPor)
//                    console.log("criterio" + criterio)
//                    console.log("ordenar" + ordenar)

            $.ajax({
                type    : "POST",
                url     : "${createLink(action:'situacionGeografica')}",
                data    : {
                    buscarPor : buscarPor,
                    criterio  : criterio,
                    ordenar   : ordenar

                },
                success : function (msg) {

                    $("#divTabla").html(msg);
                    $("#dlgLoad").dialog("close");
                }
            });

        }

    });
</script>

</body>
</html>