<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/14/13
  Time: 11:49 AM
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

    </style>


    <title>Registro de Contratos</title>
</head>
<body>

<div class="span12 btn-group" role="navigation" style="margin-left: 0px;width: 100%;float: left;height: 35px;">
    <button class="btn" id="btn-lista"><i class="icon-book"></i> Lista</button>
    <button class="btn" id="btn-nuevo"><i class="icon-plus"></i> Nuevo</button>
    <button class="btn" id="btn-aceptar"><i class="icon-ok"></i> Aceptar</button>
    <button class="btn" id="btn-cancelar"><i class="icon-undo"></i> Cancelar</button>
    <button class="btn" id="btn-borrar"><i class="icon-remove"></i> Eliminar Contrato</button>
    <button class="btn" id="btn-salir"><i class="icon-ban-circle"></i> Salir</button>

</div>

<fieldset class="borde" style="position: relative; height: 50px; float: left">
    <div class="span12" style="margin-top: 10px" align="center">

        <div class="span2 formato">Contrato N°</div>

        <div class="span3"><g:textField name="contratoNumero" class="cotratoNumero" value=""/></div>

        <div class="span2 formato">Memo de Distribución</div>

        <div class="span3"><g:textField name="memoDistribucion" class="memo"/></div>


    </div>


</fieldset>


<fieldset class="borde" style="position: relative; height: 220px; float: left">

    <div class="span12" style="margin-top: 20px" align="center">

        <p class="css-vertical-text">Contratación Directa</p>

        <div class="linea" style="height: 85%;"></div>

    </div>
    <div class="span12" style="margin-top: 5px" align="center">

        <div class="span2 formato">Obra</div>

        <div class="span3"><g:textField name="obra" class="obraId" value=""/></div>

        <div class="span1 formato">Nombre</div>

        <div class="span3"><g:textField name="nombre" class="nombre" value="" style="width: 400px"/></div>

    </div>

    <div class="span12" style="margin-top: 5px" align="center">

        <div class="span2 formato">Parroquia</div>

        <div class="span3"><g:textField name="parroquia" class="parroquia" value=""/> </div>

        <div class="span1 formato">Cantón</div>

        <div class="span2"><g:textField name="canton" class="canton" value=""/> </div>

    </div>

    <div class="span12" style="margin-top: 5px" align="center">

        <div class="span2 formato">Clase Obra</div>

        <div class="span3"><g:textField name="claseObra" class="claseObra" value=""/> </div>


    </div>

    <div class="span12" style="margin-top: 5px" align="center">

        <div class="span2 formato">Contratista</div>

        <div class="span3"><g:textField name="contratista" class="contratista" value=""/> </div>

    </div>

</fieldset>

<fieldset class="borde" style="position: relative; height: 150px; float: left">

    <div class="span12" style="margin-top: 10px" align="center">

        <div class="span2 formato">Tipo</div>

        <div class="span3"><g:select from="" name="tipoContrato" class="tipoContrato"/></div>

        <div class="span2 formato">Fecha de Suscripción</div>

        <div class="span2"><elm:datepicker name="fechaSuscripcion" class="fechaSuscripcion datepicker input-small" value=""  /> </div>

    </div>

    <div class="span12" style="margin-top: 5px" align="center">

        <div class="span2 formato">Objeto del Contrato</div>

        <div class="span3"><g:textArea name="objetoContrato" rows="5" cols="5" style="height: 79px; width: 800px; resize: none"/></div>



    </div>

</fieldset>

<fieldset class="borde" style="position: relative; height: 160px; float: left">

    <div class="span12" style="margin-top: 10px">

        <div class="span2 formato">Monto</div>

        <div class="span3"><g:textField name="monto" class="monto" value=""/></div>

        <div class="span2 formato">Plazo</div>

        <div class="span3"><g:textField name="plazo" class="plazo" value=""/> </div>

    </div>

    <div class="span12" style="margin-top: 10px">

        <div class="span2 formato">Anticipo</div>

        <div class="span3"><g:textField name="anticipo" class="anticipo" value=""/></div>

        <div class="span2 formato">Indices de la Oferta</div>

        <div class="span3"><g:select name="indiceOferta" from="" class="indiceOferta"/> </div>


    </div>


    <div class="span12" style="margin-top: 10px">

        <div class="span2 formato">Financiamiento</div>

        <div class="span3"><g:textField name="financiamiento" class="financiamiento" value=""/></div>

        <div class="span2 formato">Financiado Por</div>

        <div class="span3"><g:textField name="financiadoPor" class="financiadoPor"/> </div>


    </div>


    %{--<g:if test="${obra?.id}">--}%
    <div class="navbar navbar-inverse" style="margin-top: 20px;padding-left: 5px;float: left" align="center">

        <div class="navbar-inner">
            <div class="botones">

                <ul class="nav">
                    <li>
                        <g:link controller="garantia" action="garantiasContrato" id="">
                        <i class="icon-pencil"></i>Garantías
                        </g:link>
                    </li>
                    %{--<li><a href="${g.createLink(controller: 'volumenObra', action: 'volObra', id: obra?.id)}"><i class="icon-list-alt"></i>Vol. Obra--}%
                    %{--</a></li>--}%
                    <li><a href="#" id="btnCronograma"><i class="icon-th"></i>Cronograma</a></li>
                    %{--<li>--}%
                    %{--<g:link controller="formulaPolinomica" action="coeficientes" id="${obra?.id}">--}%
                    %{--Fórmula Pol.--}%
                    %{--</g:link>--}%
                    %{--</li>--}%
                    <li><a href="#" id="btnFormula"><i class="icon-file"></i>F. Polinómica</a></li>
                    %{--<li><a href="${g.createLink(controller: 'cronograma', action: 'cronogramaObra', id: obra?.id)}"><i class="icon-calendar"></i>Cronograma--}%
                    %{--</a></li>--}%


                </ul>

            </div>
        </div>

    </div>
    %{--</g:if>--}%




</fieldset>


<div class="modal hide fade mediumModal" id="modal-var" style="overflow: hidden">
    <div class="modal-header btn-primary">
        <button type="button" class="close" data-dismiss="modal">x</button>

        <h3 id="modal_tittle_var">

        </h3>


    </div>

    <div class="modal-body" id="modal_body_var">

    </div>

    <div class="modal-footer" id="modal_footer_var">

    </div>

</div>

<div class="modal grandote hide fade" id="modal-busqueda" style="overflow: hidden">
    <div class="modal-header btn-info">
        <button type="button" class="close" data-dismiss="modal">x</button>

        <h3 id="modalTitle_busqueda"></h3>

    </div>
    <div class="modal-body" id="modalBody">
        <bsc:buscador name="contratos" value="" accion="buscarContrato" controlador="contrato" campos="${campos}" label="Contrato" tipo="lista"/>

    </div>
    <div class="modal-footer" id="modalFooter_busqueda">

    </div>

</div>

<script type="text/javascript">

    $("#btn-lista").click(function () {
        var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');
        $("#modalTitle_busqueda").html("Lista de Contratos");
        $("#modalFooter_busqueda").html("").append(btnOk);
        $("#modal-busqueda").modal("show");

    });







</script>



</body>
</html>