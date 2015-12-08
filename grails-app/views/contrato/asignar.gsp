<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 30/11/15
  Time: 01:11 PM
--%>

<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/17/13
  Time: 4:34 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'tableHandler.css')}">

    <style type="text/css">

    .formato {
        font-weight : bolder;
        font-size: large;
    }

    </style>


    <title>Asignar F. Polinómica</title>
</head>

<body>

<div class="row" style="margin-bottom: 10px;">
    <div class="span9 btn-group" role="navigation">
        <g:link controller="contrato" action="registroContrato" params="[contrato: contrato?.id]" class="btn btn-ajax btn-new" title="Regresar al contrato">
            <i class="icon-double-angle-left"></i>
            Contrato
        </g:link>
        <g:if test="${fxs}">
            <a href="#" class="btn btn-success" id="btnGuardar"><i class="icon-save"></i> Guardar</a>
        </g:if>
        <g:else>
            <a href="#" class="btn btn-success" id="btnSave"><i class="icon-save"></i> Guardar</a>
        </g:else>

    </div>

</div>

<div id="divTabla">

    <h3 style="text-align: center">Asignación de las Fórmulas Polinómicas</h3>
    <div style="min-height: 40px;"> <span>Contrato:</span> <span class="titulo" style="display: inline-block; width: 800px; vertical-align:top;"> ${contrato.objeto}</span></div>
    <div class="span12" style="margin-top: 10px" id="dirSalida">

        <div class="borde_abajo" style="position: relative;float: left;width: 95%;">
            %{--<p class="css-vertical-text">Asignación F. Polinómica</p>--}%

            %{--<div class="linea" style="height: 98%;"></div>--}%

            %{--<div style="width: 99.7%;height: 250px;overflow-y: auto;float: right;" id="detalle">--}%


                <g:set var="contador" value="${subpresupuesto.size()}"/>
                <g:set var="arr" value="${[]}"/>
                %{--<g:set var="arr2" value="${[]}"/>--}%

                <div class="span7 formato"> Subpresupuestos de la Obra
                <g:each in="${subpresupuesto}" var="sub" status="i">
                    <g:textField name="sub_${i}" class="subi span7" value="${sub?.descripcion}" data-id="${sub.id}" readonly="true"
                                title="${sub?.descripcion}" />
                    <g:set var="arr" value="${arr += sub.id}"/>
                  </g:each>
                </div>

                <div class="span4 formato"> Fórmula Polinómica a utilizar
                <g:if test="${fxs}">
                    <g:each in="${fxs}" var="f">
                        <g:select name="pol_${f}" data-id="${f}" from="${formulas}" optionKey="id"
                                  optionValue="descripcion" class="sle span4" value="${f.reajuste.id}" title="Fórmulas Polinómicas"
                                  />
                    </g:each>
                </g:if>
                <g:else>
                    <g:each in="${subpresupuesto}" var="pol" status="j">
                        <g:select style="width: 340px;" name="pol_${j}" data-id="${j}" from="${formulas}" optionKey="id"
                                  optionValue="descripcion" class="sle" value="" title="Fórmulas Polinómicas"
                                  noSelection="['null': 'Seleccione ...']"/>

                    </g:each>
                </g:else>

                </div>
            %{--</div>--}%

        %{--</div>--}%
    </div>


</div>



<script type="text/javascript">


    $("#btnSave").click(function () {

        var data = {};
        var veri = 0
        $(".sle").each(function () {
            var $input = $(this);
            data[$input.data("id")] = $input.val();
            if($input.val() == 'null'){
                veri = 1
            }
        });

        if(veri != 1) {
            $.ajax({
                type    : "POST",
                url     : "${createLink(action:'saveAsignarFormula')}",
                data    : {
                    sub: ${arr},
                    formu: data

                },
                success : function (msg) {
                    if(msg == 'si'){
                        alert("Fórmulas polinómicas asignadas correctamente");
                        window.location.reload(true)
                    }else{
                        alert("Error al asignar las fórmulas polinómicas")
                    }
                }
            });
        } else {
            confirm("Una o más fórmulas polinómicas no han sido seleccionadas");
        }
    });

    $("#btnGuardar").click(function () {


        var data = [];
        $(".sle").each(function () {
            var $input = $(this);
            data += ( $input.val() + "_");
        });

        $.ajax({
            type    : "POST",
            url     : "${createLink(action:'saveAsignarFormula2')}",
            data    : {
                sub: ${arr},
                formu: data,
                fxs: ${fxs.id}

            },
            success : function (msg) {
                if(msg == 'si'){
                    alert("Fórmulas polinómicas asignadas correctamente");
                    window.location.reload(true)
                }else{
                    alert("Error al asignar las fórmulas polinómicas")
                }
            }
        });
    });

</script>

</body>
</html>