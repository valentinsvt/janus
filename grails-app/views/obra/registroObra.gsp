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
    <style type="text/css">

    .formato {

        font-weight: bolder;

    }

    .titulo {

        font-size: 20px;

    }




    </style>

    <title>Registro de Obras</title>
</head>

<body>

<div class="btn-group" style="margin-bottom: 10px">
    <button class="btn"><i class="icon-book"></i> Lista</button>
    <button class="btn"><i class="icon-plus"></i> Nuevo</button>
    <button class="btn"><i class="icon-ok"></i> Aceptar</button>
    <button class="btn"><i class="icon-ban-circle"></i> Cancelar</button>
    <button class="btn"><i class="icon-remove"></i> Eliminar la Obra</button>
    <button class="btn"><i class="icon-print"></i> Imprimir</button>
    <button class="btn"><i class="icon-retweet"></i> Cambiar de Estados</button>
</div>

%{--<div class="navbar navbar-inverse" style="margin-top: 10px; padding-left: 35px" align="center">--}%

%{--<div class="navbar-inner">--}%
%{--<div class="botones">--}%

%{--<ul class="nav">--}%
%{--<li class="active">--}%
%{--<a href="#"><i class="icon-book"></i>Lista</a>--}%
%{--</li>--}%
%{--<li><a href="#"><i class="icon-plus"></i>Nuevo</a></li>--}%
%{--<li><a href="#"><i class="icon-ok"></i>Aceptar</a></li>--}%
%{--<li><a href="#"><i class="icon-ban-circle"></i>Cancelar</a></li>--}%
%{--<li><a href="#"><i class="icon-remove"></i>Eliminar la Obra</a></li>--}%
%{--<li><a href="#"><i class="icon-print"></i>Imprimir</a></li>--}%
%{--<li><a href="#"><i class="icon-retweet"></i>Cambiar de Estado</a></li>--}%
%{--</ul>--}%

%{--</div>--}%
%{--</div>--}%


%{--</div>--}%



<fieldset class="borde" style="position: relative; height: 100px">
    <g:hiddenField name="id" value="388"/>
    <div class="span12" style="margin-top: 20px" align="center">

        <p class="css-vertical-text">Ingreso</p>

        <div class="linea" style="height: 85%;"></div>

    </div>

    <div class="span12" style="margin-top: 10px">

        <div class="span1 formato">MEMO</div>

        <div class="span3"><g:textField name="memo" class="memo"/></div>

        <div class="span2 formato">CANTIDAD DE OBRA</div>

        <div class="span3"><g:textField name="cantidad" class="cantidad"/></div>

        <div class="span1 formato">FECHA</div>

        <div class="span1"><elm:datepicker name="fecha" class="fecha datepicker input-small" value=""/></div>

    </div>

</fieldset>
<fieldset class="borde">
    <div class="span12" style="margin-top: 10px">
        <div class="span1">Código</div>

        <div class="span3"><g:textField name="codigo" class="codigo"/></div>

        <div class="span1">Nombre</div>

        <div class="span6"><g:textField name="nombre" class="nombre" style="width: 608px"/></div>
    </div>

    <div class="span12">
        <div class="span1">Programa</div>

        <div class="span3"><g:select name="programa" from=""/></div>

        <div class="span1">Tipo</div>

        <div class="span3"><g:textField name="tipo" class="tipo"/></div>

        <div class="span1">Clase</div>

        <div class="span1"><g:select name="clase" from=""/></div>
    </div>

    <div class="span12">
        <div class="span1">Referencias</div>

        <div class="span6"><g:textField name="refrencias" class="referencias" style="width: 610px"/></div>

        <div class="span1" style="margin-left: 130px">Estado</div>

        <div class="span1"><g:textField name="estado" class="estado"/></div>
    </div>

    <div class="span12">
        <div class="span1">Descripción</div>

        <div class="span6"><g:textArea name="descripcion" rows="5" cols="5"
                                       style="width: 1007px; height: 72px; resize: none" maxlength="511"/></div>
    </div>

    <div class="span12">

        <div class="span2"><button class="btn btn-buscar" id="btn-buscar"><i class="icon-globe"></i> Buscar
        </button>
        </div>


        <div class="span1" style="margin-left: -50px">Provincia</div>

        <div class="span3" style="margin-left: -5px"><g:select name="provincia" from="${prov}"
                                                               noSelection="['-1': 'Seleccione...']" optionKey="id" optionValue="nombre"/></div>

        <div class="span1" style="margin-left: -15px">Cantón</div>

        <div class="span3" style="margin-left: -9px"><g:select name="canton" from="${cant}" id="canton"
                                                               noSelection="['-1': 'Seleccione...']"
                                                               optionValue="nombre" optionKey="id"/></div>

        <div class="span1">Parroquia</div>

        <div class="span1"><g:select name="parroquia" from="${parr}"
                                     noSelection="['-1': 'Seleccione...']" optionValue="nombre" optionKey="id"/></div>

    </div>

    <div class="span12">
        <div class="span1">Comunidad</div>

        <div class="span3"><g:textField name="comunidad" class="comunidad"/></div>

        <div class="span1">Sitio</div>

        <div class="span3"><g:textField name="sitio" class="sitio"/></div>

        <div class="span1">Plazo</div>

        <div class="span2"><g:textField name="plazoMeses" class="plazoMeses" style="width: 28px"
                                        maxlength="3" type="number"/>Meses</div>

        <div class="span1" style="margin-left: -40px"><g:textField name="plazoDias" class="plazoDias"
                                                                   style="width: 28px" maxlength="3"/>Días</div>

    </div>

    <div class="span12">
        <div class="span1">Inspección</div>

        <div class="span3"><g:select name="inspeccion" from=""/></div>

        <div class="span1">Revisión</div>

        <div class="span3"><g:select name="revision" from=""/></div>

        <div class="span1">Responsable</div>

        <div class="span1"><g:select name="responsable" from=""/></div>
    </div>

    <div class="span12">
        <div class="span1">Observaciones</div>

        <div class="span6"><g:textField name="observaciones" class="observaciones" style="width: 610px;"/></div>

        <div class="span1" style="margin-left: 130px">Anticipo</div>

        <div class="span2"><g:textField name="anticipo" class="anticipo" style="width: 70px"/> %</div>

    </div>

    <div class="span12">

        <div class="span1">Lista</div>

        <div class="span2" style="margin-right: 70px"><g:textField name="lista" class="lista"/></div>

        <div class="span1">Fecha</div>

        <div class="span2"><elm:datepicker name="fechaLista" class="fechaLista datepicker input-small" value=""/></div>

        <div class="span1" style="margin-left: -40px">Dist.Peso</div>

        <div class="span2"><g:textField name="peso" class="peso" style="width: 70px"/>(km)</div>

        <div class="span1" style="margin-left: -20px">Dist.Volumen</div>

        <div class="span2" style="width: 150px"><g:textField name="volumen" class="volumen"
                                                             style="width: 70px"/>(km)</div>

    </div>

</fieldset>

<fieldset class="borde" style="position: relative;">

    <div class="span12" style="margin-top: 10px">

        <p class="css-vertical-text">Salida</p>

        <div class="linea" style="height: 85%;"></div>

    </div>

    <div class="span12" style="margin-top: 10px">

        <div class="span1 formato" style="width: 80px">OFICIO SAL.</div>

        <div class="span3" style="margin-left: 18px"><g:textField name="oficio" class="oficio"/></div>

        <div class="span1 formato">MEMO</div>

        <div class="span3"><g:textField name="memoSal" class="memoSal"/></div>

    </div>

    <div class="span12" style="margin-top: 10px">
        <div class="span1 formato">FORMULA</div>

        <div class="span3"><g:textField name="formula" class="formula"/></div>

        <div class="span1 formato">DESTINO</div>

        <div class="span3" style="margin-right: 95px"><g:select name="destino" from="" style="width: 300px"/></div>

        <div class="span1 formato">FECHA</div>

        <div class="span1"><elm:datepicker name="fechaSalida" class="fechaSalida datepicker input-small"
                                           value=""/></div>

    </div>

</fieldset>

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


<div class="navbar navbar-inverse" style="margin-top: 10px;padding-left: 5px" align="center">

    <div class="navbar-inner">
        <div class="botones">

            <ul class="nav">
                <li><a href="#" id="btnVar"><i class="icon-pencil"></i>Variables</a></li>
                <li><a href="#">Vol. Obra</a></li>
                <li><a href="#">Matriz FP</a></li>
                <li><a href="#">Fórmula Pol.</a></li>
                <li><a href="#">FP Liquidación</a></li>
                <li><a href="#"><i class="icon-money"></i>Rubros</a></li>
                <li><a href="#"><i class="icon-file"></i>Documentos</a></li>
                <li><a href="#"><i class="icon-calendar"></i>Cronograma</a></li>
                <li><a href="#">Composición</a></li>
                <li><a href="#">Trámites</a></li>
                %{--<li><a href="#"><i class="icon-legal"></i>Delegados</a></li>--}%
            </ul>

        </div>
    </div>

</div>


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



<script type="text/javascript">
    $(function () {

        $("#canton").change(function () {

             var c = $("#canton").val();

            console.log($("#canton").val());
            $.ajax({
                  type:"POST",
                  url:"${createLink(action: 'registroObra')}",
                  data:{
                      c:c
                  },
                success:function (msg) {

//                    $("#parroquia").html();
                     console.log($("#canton").val())
                    console.log(${parr})
                }

            })


        });


        $("#btn-buscar").click(function () {


//                    $("#dlgLoad").dialog("close");
            $("#busqueda").dialog("open");
//

        });

        $("#btn-consultar").click(function () {


            $("#dlgLoad").dialog("open");

            busqueda();

        });

        $("#btnVar").click(function () {
            $.ajax({
                type:"POST",
                url:"${createLink(controller: 'variables', action:'variables_ajax')}",
                success:function (msg) {
                    var btnCancel = $('<a href="#" data-dismiss="modal" class="btn">Cancelar</a>');
                    var btnSave = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Guardar</a>');

                    btnSave.click(function () {
                        if ($("#frmSave-var").valid()) {
                            btnSave.replaceWith(spinner);
                        }
                        $("#frmSave-var").submit();
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

            autoOpen:false,
            resizable:false,
            modal:true,
            draggable:false,
            width:800,
            height:600,
            position:'center',
            title:'Datos de Situación Geográfica'

        });


        function busqueda() {

            var buscarPor = $("#buscarPor").val();
            var criterio = $("#criterio").val();
            var ordenar = $("#ordenar").val();

//
//                   console.log("buscar" + buscarPor)
//                    console.log("criterio" + criterio)
//                    console.log("ordenar" + ordenar)


            $.ajax({
                type:"POST",
                url:"${createLink(action:'situacionGeografica')}",
                data:{
                    buscarPor:buscarPor,
                    criterio:criterio,
                    ordenar:ordenar

                },
                success:function (msg) {

                    $("#divTabla").html(msg);
                    $("#dlgLoad").dialog("close");
                }
            });


        }
    });
</script>

</body>
</html>