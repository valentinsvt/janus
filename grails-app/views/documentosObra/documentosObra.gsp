<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 12/6/12
  Time: 3:11 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>



    <style type="text/css">

    .texto {
        font-family : arial;
        font-size: 12px;
    }


    </style>


    <title>Formato de Impresión</title>
</head>

<body>

<div id="tabs" style="width: 800px; height: 1150px">

<ul>

    <li><a href="#tab-presupuesto">Presupuesto</a></li>
    <li><a href="#tab-memorando">Memorando</a></li>
    <li><a href="#tab-polinomica">F. Polinómica</a></li>
    <li><a href="#tab-memorandoPresu">Adm. Directa</a></li>
    <li><a href="#tab-textosFijos">Textos Fijos</a></li>

</ul>

<div id="tab-presupuesto" class="tab">

<div class="tipoReporte">

    <fieldset class="borde">

        <legend>Tipo de Reporte</legend>

        <div class="span6" style="margin-bottom: 10px; margin-top: -20px">

            <input type="radio" name="tipoPresupuesto" class="radioPresupuesto" value="1"/>  Base de Contrato

            <input type="radio" name="tipoPresupuesto" class="radioPresupuesto" value="2"
                   style="margin-left: 220px"/> Presupuesto Referencial

        </div>
    </fieldset>

</div>



<div class="piePagina" style="margin-bottom: 10px">


    <g:form class="registroNota" name="frm-nota" controller="nota" action="save">
        <fieldset class="borde">

            <div style="margin-top: -20px; margin-bottom: 20px; margin-left: 30px">

                <g:checkBox name="forzar" checked="false"/> Forzar nueva Página para las Notas de Pie de Página

            </div>

            <legend>Pie de Página</legend>

            <div class="span6" style="margin-top: -10px">

                <g:select name="piePaginaSel" from="${nota?.list()}" value="${nota?.id}" optionValue="descripcion"
                          optionKey="id" style="width: 350px"/>

                <div class="btn-group" style="margin-left: 350px; margin-top: -60px; margin-bottom: 10px">
                    <a class="btn" id="btnNuevo">Nuevo</a>
                    <a class="btn" id="btnCancelar">Cancelar</a>
                    <a class="btn" id="btnEditar">Editar</a>
                    <a class="btn" id="btnAceptar">Aceptar</a>
                </div>

            </div>

            %{--<g:hiddenField name="nota" value="${nota?.id}"/>--}%
            <g:hiddenField name="obra" value="${obra?.id}"/>

            <div class="span6">

                <g:textField name="descripcion" value="${nota?.descripcion}" style="width: 685px" disabled="true"/>

            </div>

            <div class="span6">

                <g:textArea name="texto" value="${nota?.texto}" rows="5" cols="5"
                            style="height: 125px; width:685px ; resize: none" disabled="true"/>

            </div>


            <div class="span6" style="margin-top: 5px; margin-bottom: 10px">

                <g:checkBox name="notaAdicional" checked="false"
                            disabled="true"/> Nota al Pie Adicional (15 líneas aprox)

            </div>


            <div class="span6">

                <g:textArea name="adicional" value="${nota?.adicional}" rows="5" cols="5"
                            style="height: 125px; width:685px ; resize: none" disabled="true"/>

            </div>

            <g:hiddenField name="obraTipo" value="${obra?.claseObra?.tipo}"/>

        </fieldset>

    </g:form>

</div>

<div class="setFirmas" style="margin-top: -20px">

    <fieldset class="borde">

        <legend>Set de Firmas</legend>


        %{--<div class="span6" style="margin-top: -20px">--}%
            %{--from="${firmas}"--}%
            %{--<elm:select name="setFirmas" id="cmb_presupuesto" class="selFirmas" from="${firmaDirector}"--}%
                        %{--optionClass="${{ it?.cargo }}"--}%
                        %{--optionKey="id" optionValue="${{ it?.nombre + ' ' + it?.apellido }}" style="width: 350px"/>--}%


            %{--<div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">--}%
                %{--<button class="btn btnAdicionar" id="presupuesto">Adicionar</button>--}%

            %{--</div>--}%

        %{--</div>--}%

        <div class="span6" style="width: 700px; margin-top: -20px">

            <table class="table table-bordered table-striped table-hover table-condensed " id="tablaFirmas">

                <thead>
                <tr>
                    %{--<th style="width: 50px">N°</th>--}%
                    <th style="width: 350px">Nombre</th>
                    <th style="width: 250px">Rol</th>
                    %{--<th style="width: 20px"></th>--}%

                </tr>

                </thead>


                <tbody id="firmasFijasPresu">

                %{--Director--}%

                %{--<g:if test="${obra?.inspector}">--}%

                    %{--<tr data-id="${obra?.inspector?.id}">--}%
                    <tr data-id="${firmaDirector?.id}">



                        %{--<td id="${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }">--}%

                            %{--${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }--}%

                        %{--</td>--}%
                        %{--<td>--}%
                            %{--${obra?.inspector?.cargo}--}%

                        %{--</td>--}%
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaPresu'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%

                        <td id="${firmaDirector?.nombre + " " + firmaDirector?.apellido}">

                         ${firmaDirector?.nombre + " " + firmaDirector?.apellido}

                        </td>
                        <td>

                        DIRECTOR

                        </td>





                    </tr>
                %{--</g:if>--}%


                %{--<g:if test="${obra?.revisor}">--}%
                    <tr data-id="${obra?.revisor?.id}">

                        %{--<td>--}%

                        %{--</td>--}%
                        <td id=" ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">
                            ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido}
                        </td>
                        <td>
                            %{--${obra?.revisor?.cargo}--}%
                            REVISOR
                        </td>
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaPresu'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%

                    </tr>
                %{--</g:if>--}%
                %{--<g:if test="${obra?.responsableObra}">--}%

                    <tr data-id="${obra?.responsableObra?.id}">

                        %{--<td>--}%

                        %{--</td>--}%
                        <td id=" ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (ELABORO)"}">
                            ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido}
                        </td>
                        <td>
                            %{--${obra?.responsableObra?.cargo}--}%
                            ELABORÓ
                        </td>
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaPresu'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%
                    </tr>

                %{--</g:if>--}%

                </tbody>

                <tbody id="bodyFirmas_presupuesto">


                </tbody>

            </table>

        </div>

    </fieldset>

</div>

</div>

<div id="tab-memorando" class="tab" style="">

<div class="tipoReporteMemo">

    <fieldset class="borde">

        <legend>Tipo de Reporte</legend>

        <div class="span6" style="margin-bottom: 10px; margin-top: -20px">

            <input type="radio" name="tipoPresupuestoMemo" class="radioPresupuestoMemo" value="1"
                   checked="true"/>  Base de Contrato

            <input type="radio" name="tipoPresupuestoMemo" class="radioPresupuestoMemo" value="2"
                   style="margin-left: 220px"/> Presupuesto Referencial

        </div>
    </fieldset>
</div>

<div class="cabecera">

    <fieldset class="borde">
        <legend>Cabecera</legend>

        <div class="span6">
            <div class="span1">Memo N°</div>

            <div class="span3"><g:textField name="numeroMemo" value="${obra?.memoSalida}" disabled="true"/></div>
        </div>

        <div class="span6">
            <div class="span1">DE:</div>


            <div class="span3"><g:textField name="deMemo" style="width: 470px"
                                            value="${obra?.departamento?.direccion?.nombre}"
                                            disabled="true"/></div>

        </div>

        <div class="span6">
            <div class="span1">PARA:</div>

            %{--<div class="span3"><g:textField name="paraMemo" value="${obra?.departamento?.descripcion}"--}%
            %{--style="width: 470px" disabled="true"/></div>--}%

            <div class="span3"><g:select name="paraMemo" from="${janus.Direccion.list()}" optionKey="id" optionValue="nombre" style="width: 485px"/></div>
        </div>

        <div class="span7">
            <div class="span1">Valor de la Base:</div>

            <div class="span2">
                %{--<g:textField name="baseMemo" style="width: 100px" disabled="true"--}%
                %{--value="${formatNumber(number: totalPresupuestoBien, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}"/>--}%

                <g:textField name="baseMemo" style="width: 100px" disabled="true"  value="${totalPresupuestoBien}"/>
            </div>

            %{--<div class="span1" style="margin-left: -30px">Valor de Reajuste:</div>--}%

            %{--<div class="span2"><g:textField name="reajusteMemo" id="reajusteMemo" style="width: 100px; margin-left: -20px" value="" disabled="true"/></div>--}%

            %{--<div class="span2" style="margin-left: -45px"><g:textField name="porcentajeMemo" id="porcentajeMemo" style="width: 35px; margin-right: 10px" disabled="false"--}%
            %{--maxlength="3"/>--}%

            %{--<button class="btn" id="btnCalBase" style="width: 35px; margin-top: -9px; margin-left: -14px"><i class="icon-table"></i>--}%
            %{--</button>--}%
            %{--</div>--}%

        </div>

    </fieldset>

</div>

<div class="texto">

    <fieldset class="borde">
        <legend>Texto</legend>

        <g:form class="memoGrabar" name="frm-memo" controller="auxiliar" action="saveDoc">

            <g:hiddenField name="id" value="${"1"}"/>

            <g:hiddenField name="obra" value="${obra?.id}"/>

            <div class="span6">

                <div class="span1">Texto</div>

                <div class="span3"><g:textArea name="memo1" value="${auxiliarFijo?.memo1}" rows="4" cols="4"
                                               style="width: 600px; height: 55px; margin-left: -50px;resize: none;"
                                               disabled="true"/></div>

            </div>


            <div class="span6">
                <div class="span1">Pie</div>

                <div class="span3"><g:textArea name="memo2" value="${auxiliarFijo?.memo2}" rows="4" cols="4"
                                               style="width: 600px; height: 55px; margin-left: -50px; resize: none;"
                                               disabled="true"/></div>

            </div>

        </g:form>

        <div class="span6" style="margin-top: 10px">
            <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                <button class="btn" id="btnEditarMemo">Editar</button>
                <button class="btn" id="btnAceptarMemo">Aceptar</button>

            </div>
        </div>

    </fieldset>

</div>


<div class="setFirmas" style="margin-top: -10px">

    <fieldset class="borde">

        <legend>Set de Firmas</legend>

        %{--<div class="span6">--}%

            %{--<elm:select name="setFirmas" id="cmb_memo" class="selFirmas" from="${firmas}"--}%
                        %{--optionKey="id" optionValue="${{ it?.nombre + ' ' + it?.apellido }}"--}%
                        %{--optionClass="${{ it?.cargo }}" style="width: 350px"/>--}%


            %{--<div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">--}%
                %{--<button class="btn btnAdicionar" id="memo">Adicionar</button>--}%

            %{--</div>--}%

        %{--</div>--}%

        <div class="span6" style="width: 700px; margin-top: -20px">

            <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmasMemo">

                <thead>
                <tr>
                    %{--<th style="width: 50px">N°</th>--}%
                    <th style="width: 350px">Nombre</th>
                    <th style="width: 250px">Rol</th>
                    %{--<th style="width: 20px"></th>--}%

                </tr>

                </thead>

                <tbody id="firmasFijasMemo">

                %{--<g:if test="${obra?.inspector}">--}%

                    %{--<tr data-id="${obra?.inspector?.id}">--}%
                <tr data-id="${firmaDirector?.id}">

                        %{--<td>--}%

                        %{--</td>--}%

                        %{--<td id="${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }">--}%

                            %{--${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }--}%

                        %{--</td>--}%
                        %{--<td>--}%
                            %{--${obra?.inspector?.cargo}--}%

                        %{--</td>--}%
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaMemo'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%

                        <td id="${firmaDirector?.nombre + " " + firmaDirector?.apellido}">

                            ${firmaDirector?.nombre + " " + firmaDirector?.apellido}

                        </td>
                        <td>

                            DIRECTOR

                        </td>


                    </tr>
                %{--</g:if>--}%


                %{--<g:if test="${obra?.revisor}">--}%
                    <tr data-id="${obra?.revisor?.id}">


                        %{--<td>--}%

                        %{--</td>--}%
                        %{--<td id=" ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">--}%
                            %{--${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--${obra?.revisor?.cargo}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaMemo'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%

                        <td id=" ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">
                            ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido}
                        </td>
                        <td>
                            REVISOR
                        </td>


                    </tr>
                %{--</g:if>--}%
                %{--<g:if test="${obra?.responsableObra}">--}%

                    <tr data-id="${obra?.responsableObra?.id}">

                        %{--<td>--}%

                        %{--</td>--}%
                        %{--<td id=" ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (RESPONSABLE OBRA)"}">--}%
                            %{--${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (RESPONSABLE OBRA)"}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--${obra?.responsableObra?.cargo}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaMemo'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%

                    <td id=" ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (ELABORO)"}">
                        ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido}
                    </td>
                    <td>
                        ELABORÓ
                    </td>
                    </tr>

                %{--</g:if>--}%

                </tbody>



                <tbody id="bodyFirmas_memo">


                </tbody>

            </table>

        </div>

    </fieldset>

</div>

</div>

<div id="tab-polinomica" class="tab" style="">

    <div class="textoFormula">

        <fieldset class="borde">

            <div class="span6" style="margin-top: 10px">
                <div class="span2">Fórmula Polinómica N°</div>

                <div class="span3"><g:textField name="numeroFor" value="${obra?.formulaPolinomica}"
                                                disabled="true"/></div>
            </div>

            <div class="span6">
                <div class="span2">Fecha de Lista de Precios:</div>

                <div class="span3"><g:textField name="fechaFor"
                                                value="${formatDate(date: obra?.fechaPreciosRubros, format: "yyyy-MM-dd")}"
                                                style="width: 100px" disabled="true"/></div>
            </div>

            <div class="span6">
                <div class="span2">Monto del Contrato:</div>

                <div class="span3">
                    <g:textField name="montoFor"
                                 value="${formatNumber(number: totalPresupuestoBien, format: '##,##0', maxFractionDigits: 2, minFractionDigits: 2, locale: 'ec')}"
                                 disabled="true"/>
                </div>
            </div>

        </fieldset>

    </div>


    <div class="texto">

        <fieldset class="borde">
            <legend>Nota</legend>

            <g:form class="memoGrabar" name="frm-formula" controller="auxiliar" action="saveDoc">

                <g:hiddenField name="id" value="${"1"}"/>

                <g:hiddenField name="obra" value="${obra?.id}"/>

                <div class="span6">
                    <div class="span3"><g:textArea name="notaFormula" rows="4" value="${auxiliarFijo?.notaFormula}"
                                                   cols="4"
                                                   style="width: 690px; margin-left: -30px; height: 70px; resize: none"
                                                   disabled="true"/></div>

                </div>
            </g:form>
            <div class="span6" style="margin-top: 10px">
                <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                    <button class="btn" id="btnEditarFor">Editar</button>
                    <button class="btn" id="btnAceptarFor">Aceptar</button>

                </div>
            </div>

        </fieldset>

    </div>


    <div class="setFirmas" style="margin-top: -10px">

        <fieldset class="borde">

            <legend>Set de Firmas</legend>


            %{--<div class="span6">--}%

                %{--<elm:select name="setFirmas" id="cmb_polinomica" class="selFirmas" from="${firmas}"--}%
                            %{--optionKey="id"--}%
                            %{--optionValue="${{ it?.nombre + " " + it?.apellido }}" optionClass="${{ it?.cargo }}"--}%
                            %{--style="width: 350px"/>--}%

                %{--<div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">--}%
                    %{--<button class="btn btnAdicionar" id="polinomica">Adicionar</button>--}%

                %{--</div>--}%

            %{--</div>--}%

            <div class="span6" style="width: 700px; margin-top: -20px">

                <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmasFor">

                    <thead>
                    <tr>
                        %{--<th style="width: 50px">N°</th>--}%
                        <th style="width: 350px">Nombre</th>
                        <th style="width: 250px">Rol</th>
                        %{--<th style="width: 20px"></th>--}%

                    </tr>

                    </thead>

                    <tbody id="firmasFijasPoli">

                    %{--<g:if test="${obra?.inspector}">--}%

                        %{--<tr data-id="${obra?.inspector?.id}">--}%
                    <tr data-id="${firmaDirector?.id}">

                            %{--<td>--}%

                            %{--</td>--}%

                            %{--<td id="  ${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }">--}%

                                %{--${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }--}%

                            %{--</td>--}%
                            %{--<td>--}%
                                %{--${obra?.inspector?.cargo}--}%

                            %{--</td>--}%
                            %{--<td>--}%
                                %{--<a href='#' class='btn btn-danger borrarFirmaPoli'><i class='icon-trash icon-large'></i></a>--}%
                            %{--</td>--}%

                        <td id="${firmaDirector?.nombre + " " + firmaDirector?.apellido}">

                            ${firmaDirector?.nombre + " " + firmaDirector?.apellido}

                        </td>
                        <td>

                            DIRECTOR

                        </td>


                        </tr>
                    %{--</g:if>--}%


                    %{--<g:if test="${obra?.revisor}">--}%
                        <tr data-id="${obra?.revisor?.id}">

                            %{--<td>--}%

                            %{--</td>--}%
                            %{--<td id="${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">--}%
                                %{--${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}--}%
                            %{--</td>--}%
                            %{--<td>--}%
                                %{--${obra?.revisor?.cargo}--}%
                            %{--</td>--}%
                            %{--<td>--}%
                                %{--<a href='#' class='btn btn-danger borrarFirmaPoli'><i class='icon-trash icon-large'></i></a>--}%
                            %{--</td>--}%

                        <td id=" ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">
                            ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido}
                        </td>
                        <td>
                            REVISOR
                        </td>

                        </tr>
                    %{--</g:if>--}%
                    %{--<g:if test="${obra?.responsableObra}">--}%

                        <tr data-id="${obra?.responsableObra?.id}">

                            %{--<td>--}%

                            %{--</td>--}%
                            %{--<td id="${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (RESPONSABLE OBRA)"}">--}%
                                %{--${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (RESPONSABLE OBRA)"}--}%
                            %{--</td>--}%
                            %{--<td>--}%
                                %{--${obra?.responsableObra?.cargo}--}%
                            %{--</td>--}%
                            %{--<td>--}%
                                %{--<a href='#' class='btn btn-danger borrarFirmaPoli'><i class='icon-trash icon-large'></i></a>--}%

                            %{--</td>--}%

                        <td id=" ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (ELABORO)"}">
                            ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido}
                        </td>
                        <td>
                            ELABORÓ
                        </td>


                        </tr>

                    %{--</g:if>--}%

                    </tbody>



                    <tbody id="bodyFirmas_polinomica">

                    </tbody>

                </table>

            </div>

        </fieldset>

    </div>

</div>

<div id="tab-textosFijos" class="tab" style="">

    <div class="cabecera">

        <fieldset class="borde">

            <legend>Cabecera</legend>


            <g:form class="memoGrabar" name="frm-textoFijo" controller="auxiliar" action="saveTextoFijo">

                <g:hiddenField name="id" value="${"1"}"/>

                <g:hiddenField name="obra" value="${obra?.id}"/>


                <div class="span6">
                    <div class="span1">Título</div>

                    <div class="span3"><g:textField name="titulo" value="${auxiliarFijo?.titulo}" style="width: 560px"
                                                    disabled="true"/></div>

                </div>


                <div class="span6">
                    <div class="span1">General</div>
                </div>

                <div class="span6">
                    <div class="span3"><g:textArea name="general" value="${auxiliarFijo?.general}" rows="4" cols="4"
                                                   style="width: 665px; height: 130px; resize: none;"
                                                   disabled="true"/></div>

                </div>


                <div class="span6">
                    <div class="span2">Base de Contratos</div>
                </div>

                <div class="span6">
                    <div class="span3"><g:textArea name="baseCont" value="${auxiliarFijo?.baseCont}" rows="4" cols="4"
                                                   style="width: 665px; height: 35px; resize: none;"
                                                   disabled="true"/></div>

                </div>


                <div class="span6">
                    <div class="span2">Presupuesto Referencial</div>
                </div>

                <div class="span6">
                    <div class="span3"><g:textArea name="presupuestoRef" value="${auxiliarFijo?.presupuestoRef}"
                                                   rows="4" cols="4" style="width: 665px; height: 35px; resize: none;"
                                                   disabled="true"/></div>

                </div>

            </g:form>


            <div class="span6" style="margin-top: 10px">
                <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                    <button class="btn" id="btnEditarTextoF">Editar</button>
                    <button class="btn" id="btnAceptarTextoF">Aceptar</button>

                </div>
            </div>

        </fieldset>

    </div>

    <div class="cabecera">

        <fieldset class="borde">
            <legend>Pie de Página</legend>

            <g:form class="memoGrabar" name="frm-textoFijoRet" controller="auxiliar" action="saveDoc">

                <g:hiddenField name="id" value="${"1"}"/>

                <g:hiddenField name="obra" value="${obra?.id}"/>

            %{--<div class="span6">--}%
            %{--<div class="span1">Retenciones</div>--}%

            %{--<div class="span3"><g:textField name="retencion" value="${auxiliarFijo?.retencion}"--}%
            %{--style="width: 560px" disabled="true"/></div>--}%

            %{--</div>--}%


                <div class="span6">
                    <div class="span3">NOTA (15 líneas aproximadamente)</div>
                </div>

                <div class="span6">
                    <div class="span3"><g:textArea name="notaAuxiliar" value="${auxiliarFijo?.notaAuxiliar}" rows="4"
                                                   cols="4" style="width: 665px; height: 130px; resize: none;"
                                                   disabled="true"/></div>

                </div>

            </g:form>

            <div class="span6" style="margin-top: 10px">
                <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                    <button class="btn" id="btnEditarTextoRet">Editar</button>
                    <button class="btn" id="btnAceptarTextoRet">Aceptar</button>

                </div>
            </div>

        </fieldset>

    </div>

</div>


<div id="tab-memorandoPresu" class="tab" style="">

<div class="cabecera" >

    <fieldset class="borde" style="margin-top: -10px">
        <legend>Cabecera</legend>

        <div class="span6" style="margin-top: -10px">
            <div class="span1">PARA:</div>
            <div class="span3"><g:select name="paraMemoPresu" from="${janus.Direccion.list()}" optionKey="id" optionValue="nombre" style="width: 485px"/></div>
        </div>

        <div class="span6">
            <div class="span1">DE:</div>


            <div class="span3"><g:textField name="deMemoPresu" style="width: 470px"
                                            value="${persona?.departamento?.descripcion}"
                                            disabled="true"/></div>

        </div>
        <div class="span6">
            <div class="span1">FECHA:</div>
            <div class="span3"><g:textField name="fechaMemoPresu" style="width: 200px" value="${new java.util.Date().format("dd-MM-yyyy")}" disabled="true"/> </div>

        </div>

        <div class="span6">
            <div class="span1">ASUNTO:</div>
            <div class="span3"><g:textField name="asuntoMemoPresu" style="width: 470px" value="" maxlength="100"/></div>

        </div>


    </fieldset>

</div>

<div class="texto">

    <fieldset class="borde">
        <legend>Texto</legend>

        <g:form class="memoGrabarPresu" name="frm-memoPresu" controller="auxiliar" action="saveMemoPresu">

            <g:hiddenField name="id" value="${"1"}"/>

            <g:hiddenField name="obra" value="${obra?.id}"/>

            <div class="span6" style="margin-top: -10px">

                <div class="span1">Texto</div>

                <div class="span3"><g:textArea name="notaMemoAd" value="${auxiliarFijo?.notaMemoAd}" rows="4" cols="4"
                                               style="width: 600px; height: 55px; margin-left: -50px;resize: none;"
                                               disabled="true"/></div>

            </div>

            %{--<div class="span6">--}%


            %{--</div>--}%

        </g:form>

        <div class="span6">
            <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                <button class="btn" id="btnEditarMemoPresu">Editar</button>
                <button class="btn" id="btnAceptarMemoPresu">Aceptar</button>

            </div>
        </div>

        <g:form class="memoGrabarAdjunto" name="frm-memoAdj" controller="auxiliar" action="saveMemoAdj">

            <g:hiddenField name="id" value="${"1"}"/>

            <g:hiddenField name="obra" value="${obra?.id}"/>

            <div class="span6">

                <div class="span1">Adjunto</div>

                <div class="span3"><g:textArea name="notaPieAd" value="${auxiliarFijo?.notaPieAd}" rows="4" cols="4"
                                               style="width: 600px; height: 55px; margin-left: -50px;resize: none;"
                                               disabled="true"/></div>

            </div>

        </g:form>

        <div class="span6">
            <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                <button class="btn" id="btnEditarAdjunto">Editar</button>
                <button class="btn" id="btnAceptarAdjunto">Aceptar</button>

            </div>
        </div>




    </fieldset>

</div>

<div class="valores">
    <fieldset class="borde">
        <legend>Valores</legend>
        <div class="span7">
            %{--<div class="span3">Presupuesto Referencial por administración directa:</div>--}%
            <div class="span7">
                <div class="span3">Presupuesto Referencial por Contrato:</div>

                <div class="span2">
                    <g:textField name="baseMemoPresu" style="width: 100px" disabled="true"
                                 value="${formatNumber(number: totalPresupuestoBien, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}"/>
                </div>
            </div>

            <div class="span7">
                <div class="span3">Materiales:</div>

                <g:set var="totalMaterial" value="${0}"/>
                <g:each in="${resComp}" var="r">
                    <g:set var="totalMaterial" value="${totalMaterial + ((r.transporte+r.precio)*r.cantidad)}"/>
                </g:each>
                <div class="span2">
                    <g:hiddenField name="tMaterial" value="${totalMaterial}"/>
                    <g:textField name="materialesMemo" style="width: 100px" value="${formatNumber(number: totalMaterial, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}" readonly="true"/>
                </div>
            </div>
            <div class="span7">
                <div class="span3">Mano de Obra:</div>
                <g:set var="totalMano" value="${0}"/>
                <g:each in="${resMano}" var="r">
                    <g:set var="totalMano" value="${totalMano + ((r.transporte+r.precio)*r.cantidad)}"/>
                </g:each>
                <div class="span2">
                    <g:hiddenField name="tMano" value="${totalMano}"/>
                    <g:textField name="manoObraMemo" style="width: 100px" value="${formatNumber(number: totalMano, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}" readonly="true"/>
                </div>
            </div>
            <div class="span7">
                <div class="span3">Equipos:</div>
                <g:set var="totalEquipo" value="${0}"/>
                <g:each in="${resEq}" var="r">
                    <g:set var="totalEquipo" value="${totalEquipo + ((r.transporte+r.precio)*r.cantidad)}"/>
                </g:each>
                <div class="span2">
                    <g:hiddenField name="tEquipo" value="${totalEquipo}"/>
                    <g:textField name="equiposMemo" style="width: 100px" value="${formatNumber(number: totalEquipo, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}" readonly="true"/>
                </div>
            </div>
            <div class="span7">

                <div class="span2">Costos Indirectos:</div>
                <div class="span3" style="margin-left: 62px">
                    %{--<div class="input-append">--}%
                    <g:textField name="costoPorcentaje" type="number" style="width: 30px" maxlength="3"/>
                    <span class="add-on">%</span>
                    %{--</div>--}%
                    <g:textField name="costoMemo" style="width: 100px" disabled="true"/>
                </div>

            </div>
            <div class="span7">
                <div class="span3">TOTAL:</div>
                <div class="span2"><g:textField name="totalMemoPresu" style="width: 100px" disabled="true"/></div>
            </div>
        </div>

    </fieldset>

</div>


<div class="setFirmas" style="margin-top: -10px">

    <fieldset class="borde">

        <legend>Set de Firmas</legend>

        %{--<div class="span6">--}%

            %{--<elm:select name="setFirmas" id="cmb_memoPresu" class="selFirmas" from="${firmas}"--}%
                        %{--optionKey="id" optionValue="${{ it?.nombre + ' ' + it?.apellido }}"--}%
                        %{--optionClass="${{ it?.cargo }}" style="width: 350px"/>--}%


            %{--<div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">--}%
                %{--<button class="btn btnAdicionar" id="memoPresu">Adicionar</button>--}%

            %{--</div>--}%

        %{--</div>--}%

        <div class="span6" style="width: 700px; margin-top: -20px">

            <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmasMemoPresu">

                <thead>
                <tr>
                    %{--<th style="width: 50px">N°</th>--}%
                    <th style="width: 350px">Nombre</th>
                    <th style="width: 250px">Rol</th>
                    %{--<th style="width: 20px"></th>--}%

                </tr>

                </thead>

                <tbody id="firmasFijasMemoPresu">

                %{--<g:if test="${obra?.inspector}">--}%

                    %{--<tr data-id="${obra?.inspector?.id}">--}%
                    <tr data-id="${firmaDirector?.id}">

                        %{--<td>--}%

                        %{--</td>--}%

                        %{--<td id="${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }">--}%

                            %{--${obra?.inspector?.nombre + " " + obra?.inspector?.apellido + " " + " (INSPECTOR)" }--}%

                        %{--</td>--}%
                        %{--<td>--}%
                            %{--${obra?.inspector?.cargo}--}%

                        %{--</td>--}%
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaMemoPresu'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%

                    <td id="${firmaDirector?.nombre + " " + firmaDirector?.apellido}">

                        ${firmaDirector?.nombre + " " + firmaDirector?.apellido}

                    </td>
                    <td>

                        DIRECTOR

                    </td>



                    </tr>
                %{--</g:if>--}%


                %{--<g:if test="${obra?.revisor}">--}%
                    <tr data-id="${obra?.revisor?.id}">

                        %{--<td>--}%

                        %{--</td>--}%
                        %{--<td id=" ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">--}%
                            %{--${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--${obra?.revisor?.cargo}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaMemoPresu'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%

                    <td id=" ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">
                        ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido}
                    </td>
                    <td>
                        REVISOR
                    </td>

                    </tr>
                %{--</g:if>--}%
                %{--<g:if test="${obra?.responsableObra}">--}%

                    <tr data-id="${obra?.responsableObra?.id}">

                        %{--<td>--}%

                        %{--</td>--}%
                        %{--<td id=" ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (RESPONSABLE OBRA)"}">--}%
                            %{--${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (RESPONSABLE OBRA)"}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--${obra?.responsableObra?.cargo}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                            %{--<a href='#' class='btn btn-danger borrarFirmaMemoPresu'><i class='icon-trash icon-large'></i></a>--}%
                        %{--</td>--}%



                    <td id=" ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (ELABORO)"}">
                        ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido}
                    </td>
                    <td>
                        ELABORÓ
                    </td>
                    </tr>

                %{--</g:if>--}%

                </tbody>



                <tbody id="bodyFirmas_memoPresu">


                </tbody>

            </table>

        </div>

    </fieldset>

</div>

</div>


<div class="btn-group" style="margin-bottom: 10px; margin-top: 20px; margin-left: 210px">
    <button class="btn" id="btnSalir"><i class="icon-arrow-left"></i> Regresar</button>
    <button class="btn" id="btnImprimir"><i class="icon-print"></i> Imprimir</button>
</div>


<div id="tipoReporteDialog">

    <fieldset>
        <div class="span3">

            Debe elegir un Tipo de Reporte antes de imprimir el documento!!

        </div>
    </fieldset>
</div>

<div id="reajustePresupuestoDialog" class="texto">

    <fieldset>
        <div class="span3" style="margin-top: 10px">

            Incluir Iva <g:checkBox name="reajusteIva" style="margin-left: 132px"/>

        </div>

        <div class="span3" style="margin-top: 10px">
            Incluir Proyección del reajuste <g:checkBox name="proyeccionReajuste" style="margin-left: 20px"/>

        </div>

        <div class="span3" style="margin-top: 10px">

            Meses <g:textField name="mesesReajuste" style="width: 55px; margin-left: 20px"/>
        </div>

    </fieldset>
</div>


<div id="maxFirmasDialog">

    <fieldset>
        <div class="span3">

            A ingresado el número máximo de firmas para este documento.

        </div>
    </fieldset>
</div>


<div id="mesesCeroDialog">

    <fieldset>
        <div class="span3">

            Es necesario colocar un número válido en el campo Meses!!

        </div>
    </fieldset>
</div>


<div id="cambioMonedaExcel">

    <fieldset>
        <div class="span3" style="margin-bottom: 20px">

            Coloque la tasa de cambio que se aplicará a los valores del reporte en excel.

        </div>

        <div class="span3">

            Tasa de Cambio: <g:textField name="cambioMoneda" style="width: 55px; margin-left: 20px"/>

        </div>

    </fieldset>
</div>


<div id="tasaCeroDialog">

    <fieldset>
        <div class="span3">

            Si desea que su reporte sea calculado con una tasa de cambio se debe colocar un número válido en el campo Tasa!!

        </div>
    </fieldset>
</div>


<div id="borrarFirmaPresuDialog">
    <fieldset>
        <div class="span3">
            Está seguro que desea remover esta firma del documento a ser impreso?
        </div>
    </fieldset>
</div>


<div class="modal hide fade" id="modal-borrarFirma">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>

        <div id="modalTitle"></div>
    </div>

    <div class="modal-body" id="modalBody">
    </div>

    <div class="modal-footer" id="modalFooter">
    </div>
</div>



<div id="reajusteMemoDialog" class="texto">

    <fieldset>
        <div class="span3" style="margin-top: 10px">

            Incluir Iva <g:checkBox name="reajusteIvaMemo" style="margin-left: 132px"/>

        </div>

        <div class="span3" style="margin-top: 10px">
            Incluir Proyección del reajuste <g:checkBox name="proyeccionReajusteMemo" style="margin-left: 20px"/>

        </div>

        <div class="span3" style="margin-top: 10px">

            Meses <g:textField name="mesesReajusteMemo" style="width: 55px; margin-left: 20px"/>
        </div>

    </fieldset>
</div>

</div>

<script type="text/javascript">


    var tipoClick;

    var tipoClickMemo = $(".radioPresupuestoMemo").attr("value");

    var tg = 0;

    var forzarValue;

    var notaValue;

    var firmasId = [];
    var firmasIdMP = [];

    var firmasIdMemo = []

    var firmasIdFormu = []

    var firmasFijas = []
    var firmasFijasMP = []

    var firmasFijasMemo = []

    var firmasFijasFormu = []

    var firmasIdMemoPresu = [];

    //    var firmasFijasMemoPresu = [];

    var totalPres = $("#baseMemo").val()

    var reajusteMemo = 0;

    var proyeccion;

    var reajusteIva;

    var reajusteMeses;

    var tasaCambio;

    var idObraMoneda;

    var proyeccionMemo;

    var reajusteIvaMemo;

    var reajusteMesesMemo;

    var paraMemo1;


    function validarNum(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39);
    }

    function validarNumDec(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
                (ev.keyCode >= 96 && ev.keyCode <= 105) ||
                ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                ev.keyCode == 37 || ev.keyCode == 39 || ev.keyCode == 190 || ev.keyCode == 110);
    }


    $("#mesesReajuste").keydown(function (ev) {

        return validarNum(ev);

    }).keyup(function () {

                var enteros = $(this).val();


                if (parseFloat(enteros) > 100) {

                    $(this).val(100)

                }
                if (parseFloat(enteros) <= 0) {

                    $(this).val(1)

                }

            });


    $("#mesesReajusteMemo").keydown(function (ev) {

        return validarNum(ev);

    }).keyup(function () {

                var enteros = $(this).val();


                if (parseFloat(enteros) > 100) {

                    $(this).val(100)

                }
                if (parseFloat(enteros) <= 0) {

                    $(this).val(1)

                }

            });


    $("#cambioMoneda").keydown(function (ev) {
//
        var val = $(this).val();
        var dec = 2;


        if (ev.keyCode == 110 || ev.keyCode == 190) {

            if (!dec) {
                return false;
            } else {
                if (val.length == 0) {
                    $(this).val("0");
                }
                if (val.indexOf(".") > -1) {
                    return false;
                }
            }

        } else {


            if (val.indexOf(".") > -1) {
                if (dec) {
                    var parts = val.split(".");
                    var l = parts[1].length;
                    if (l >= dec) {
                        return false;
                    }
                }
            } else {
                return validarNumDec(ev);
            }


        }

        return validarNumDec(ev);

    }).keyup(function () {

                var enteros = $(this).val();


                if (parseFloat(enteros) > 10000) {

                    $(this).val(1)

                }
                if (parseFloat(enteros) <= 0) {

                    $(this).val(1)

                }

            });


    $("#porcentajeMemo").keydown(function (ev) {

        return validarNum(ev);

    }).keyup(function () {

                var enteros = $(this).val();

                if (parseFloat(enteros) > 100) {

                    $(this).val(100)

                }

            });

    function loadNota() {
        var idPie = $("#piePaginaSel").val();

        $.ajax({
            type: "POST",
            dataType: 'json',
            url: "${g.createLink(action:'getDatos')}",
            data: {id: idPie},
            success: function (msg) {

                $("#descripcion").val(msg.descripcion);
                $("#texto").val(msg.texto);
                $("#adicional").val(msg.adicional);

            }
        });
    }

    loadNota();

    $("#tabs").tabs();

    $("#btnSalir").click(function () {

        location.href = "${g.createLink(controller: 'obra', action: 'registroObra')}" + "?obra=" + "${obra?.id}";

    });

    $(".borrarFirmaPresu").click(function () {


//        $("#borrarFirmaPresuDialog").dialog("open")
//        $(this).parents("tr").remove()

        var ob = $(this)

        var obNombre =  ob.parents("tr").children("td").first().attr("id")

        //modal

        var btnOk = $('<a href="#" data-dismiss="modal" class="btn"> Cancelar</a>');
        var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

        btnDelete.click(function () {

            $(ob).parents("tr").remove()
            $("#modal-borrarFirma").modal("hide");



            return false;
        });

        $("#modalTitle").html("Eliminar Firma");
        $("#modalBody").html("<p>¿Está seguro que desea remover esta firma del documento ha ser impreso?</p>" + obNombre);
        $("#modalFooter").html("").append(btnDelete).append(btnOk);
        $("#modal-borrarFirma").modal("show");

        return false;

    });

    $(".borrarFirmaMemo").click(function () {

//        $(this).parents("tr").remove()

        var ob = $(this)

        var obNombre =  ob.parents("tr").children("td").first().attr("id")

        //modal

        var btnOk = $('<a href="#" data-dismiss="modal" class="btn"> Cancelar</a>');
        var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

        btnDelete.click(function () {

            $(ob).parents("tr").remove()
            $("#modal-borrarFirma").modal("hide");



            return false;
        });

        $("#modalTitle").html("Eliminar Firma");
        $("#modalBody").html("<p>¿Está seguro que desea remover esta firma del documento ha ser impreso?</p>" + obNombre);
        $("#modalFooter").html("").append(btnDelete).append(btnOk);
        $("#modal-borrarFirma").modal("show");

        return false;


    });

    $(".borrarFirmaPoli").click(function () {

//        $(this).parents("tr").remove()

        var ob = $(this)

        var obNombre =  ob.parents("tr").children("td").first().attr("id")

        //modal

        var btnOk = $('<a href="#" data-dismiss="modal" class="btn"> Cancelar</a>');
        var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

        btnDelete.click(function () {

            $(ob).parents("tr").remove()
            $("#modal-borrarFirma").modal("hide");



            return false;
        });

        $("#modalTitle").html("Eliminar Firma");
        $("#modalBody").html("<p>¿Está seguro que desea remover esta firma del documento ha ser impreso?</p>" + obNombre);
        $("#modalFooter").html("").append(btnDelete).append(btnOk);
        $("#modal-borrarFirma").modal("show");



        return false;

    });

    $(".borrarFirmaMemoPresu").click(function () {

//        $(this).parents("tr").remove()

        var ob = $(this)

        var obNombre =  ob.parents("tr").children("td").first().attr("id")

        //modal

        var btnOk = $('<a href="#" data-dismiss="modal" class="btn"> Cancelar</a>');
        var btnDelete = $('<a href="#" class="btn btn-danger"><i class="icon-trash"></i> Eliminar</a>');

        btnDelete.click(function () {

            $(ob).parents("tr").remove()
            $("#modal-borrarFirma").modal("hide");



            return false;
        });

        $("#modalTitle").html("Eliminar Firma");
        $("#modalBody").html("<p>¿Está seguro que desea remover esta firma del documento ha ser impreso?</p>" + obNombre);
        $("#modalFooter").html("").append(btnDelete).append(btnOk);
        $("#modal-borrarFirma").modal("show");



        return false;

    });



    $(".btnAdicionar").click(function () {

        var maxFirmas

        var tipo = $(this).attr("id");

        var tbody = $("#bodyFirmas_" + tipo);
        var id = $("#cmb_" + tipo).val();

        var tbodyPresu = $("#firmasFijasPresu");
        var tbodyMemo  = $("#firmasFijasMemo");
        var tbodyPoli  = $("#firmasFijasPoli");
        var tbodyMemoPresu = $("#firmasFijasMemoPresu")

        var active = $("#tabs").tabs("option", "active");

        if(active == 0){
            maxFirmas = (tbody.children("tr").length) + (tbodyPresu.children("tr").length) + 1;
        }
        if(active == 1){
            maxFirmas = (tbody.children("tr").length) + (tbodyMemo.children("tr").length) + 1;
        }
        if(active == 2){
            maxFirmas = (tbody.children("tr").length) + (tbodyPoli.children("tr").length) + 1;
        }
        if(active == 3){

            maxFirmas = (tbody.children("tr").length) + (tbodyMemoPresu.children("tr").length) + 1;

        }

//        console.log(maxFirmas)

        if (maxFirmas > 4) {

            $("#maxFirmasDialog").dialog("open");

        } else {

            var cont = true;

            tbody.children("tr").each(function () {


                var curId = $(this).data("id");

                if (curId.toString() == id.toString()) {
                    cont = false;
                }
            });

            if (cont) {
                var nombre = $.trim($("#cmb_" + tipo + " option:selected").text());
                var puesto = $.trim($("#cmb_" + tipo + " option:selected").attr("class"));
                var rows = tbody.children("tr").length;

                var num = rows + 3;

                var tr = $("<tr>");
//                var tdNumero = $("<td>");
                var tdNombre = $("<td>");
                var tdPuesto = $("<td>");
                var tdDel = $("<td>");
                var btnDel = $("<a href='#' class='btn btn-danger'><i class='icon-trash icon-large'></i></a>");

//                tdNumero.html(num);
                tdNombre.html(nombre);
                tdPuesto.html(puesto);
                tdDel.append(btnDel);


//                tr.append(tdNumero).append(tdNombre).append(tdPuesto).append(tdDel).data({nombre: nombre, puesto: puesto, id: id});
                tr.append(tdNombre).append(tdPuesto).append(tdDel).data({nombre: nombre, puesto: puesto, id: id});


                tbody.append(tr);

                btnDel.click(function () {
                    tr.remove();

                    return false
                });

            }

        }


    });

    $("#btnEditarMemo").click(function () {

        $("#memo1").attr("disabled", false);
        $("#memo2").attr("disabled", false)

    });

    $("#btnEditarMemoPresu").click(function () {

        $("#notaMemoAd").attr("disabled", false);

    });


    $("#btnEditarAdjunto").click(function () {

        $("#notaPieAd").attr("disabled", false);

    });




    $(".radioPresupuesto").click(function () {

        tipoClick = $(this).attr("value")

        return tipoClick
    });

    $(".radioPresupuestoMemo").click(function () {

        tipoClickMemo = $(this).attr("value")

        if (tipoClickMemo == '1') {

            $("#reajusteMemo").attr("disabled", true)

            $("#porcentajeMemo").attr("disabled", false)

            $("#btnCalBase").attr("disabled", false)

        }
        if (tipoClickMemo == '2') {

            $("#reajusteMemo").attr("disabled", true)
            $("#reajusteMemo").val(" ");
            $("#porcentajeMemo").attr("disabled", true)
            $("#porcentajeMemo").val(" ")
            $("#btnCalBase").attr("disabled", true)

        }

        return tipoClickMemo
    });

    $("#btnCalBase").click(function () {

        var porcentajeCal = $("#porcentajeMemo").val();

        var totalPres = $("#baseMemo").val()

        var base

        base = (porcentajeCal * (totalPres)) / 100;

        $("#reajusteMemo").val(number_format(base, 2, ".", ""))

//
//        ////console.log(porcentajeCal)
//        ////console.log(totalPres)
//        ////console.log(base)
//        ////console.log("entro cal!")

    });

    var active2 = $("#tabs").tabs("option", "event")

    //    ////console.log(active2)



    $("#tabs").click(function () {

        var active = $("#tabs").tabs("option", "active")

        if(active == 4){

      $("#btnImprimir").hide()
//            $("#btnImprimir").addClass("disabled");

        }
        else {
//            $("#btnImprimir").removeClass("disabled");
           $("#btnImprimir").show()

        }

    });


    $("#btnImprimir").click(function () {
        if(!$(this).hasClass("disabled")) {
            reajusteMemo = $("#reajusteMemo").val()

//        ////console.log("Memo:" + reajusteMemo)

            var active = $("#tabs").tabs("option", "active");

//        ////console.log(active)

            if (active == 0) {


                firmasId = [];
                firmasFijas = [];

                $("#bodyFirmas_presupuesto").children("tr").each(function (i) {
                    firmasId[i] = $(this).data("id")


                });

                $("#firmasFijasPresu").children("tr").each(function (i) {


                    firmasFijas[i] = $(this).data("id")


                });

//           console.log("1:" + firmasFijas)

                notaValue = $("#piePaginaSel").val();


                if ($("#forzar").attr("checked") == "checked") {

                    forzarValue = 1;

                } else {

                    forzarValue = 2;

                }

                if (tipoClick == null) {

                    $("#tipoReporteDialog").dialog("open");

                } else {


                    $("#reajustePresupuestoDialog").dialog("open")

                    %{--var tipoReporte = tipoClick;--}%

                    %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObra',id: obra?.id)}?tipoReporte=" + tipoReporte + "&forzarValue=" + forzarValue + "&notaValue=" + notaValue--}%
                    %{--+ "&firmasId=" + firmasId--}%

                }

            }

            if (active == 1) {

                firmasIdMemo = [];
                firmasFijasMemo = [];

                var paraMemo =  $("#paraMemo").val()

                $("#bodyFirmas_memo").children("tr").each(function (i) {

                    firmasIdMemo[i] = $(this).data("id")
                })

                $("#firmasFijasMemo").children("tr").each(function (i) {


                    firmasFijasMemo[i] = $(this).data("id")

                });

//            console.log("2:" + firmasFijasMemo)

                if (firmasIdMemo.length == 0) {

                    firmasIdMemo = "";
                }
                if(firmasFijasMemo.length == 0){

                    firmasFijasMemo = "";
                }


                if (tipoClickMemo == 1) {

                    $("#reajusteMemoDialog").dialog("open")

                }
                else {

                    var tipoReporte = tipoClickMemo;

                    location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo
                            + "&totalPresupuesto=" + totalPres + "&proyeccionMemo=" + proyeccionMemo + "&reajusteIvaMemo=" + reajusteIvaMemo + "&reajusteMesesMemo=" + reajusteMesesMemo + "&para=" + paraMemo + "&firmasFijasMemo=" + firmasFijasMemo

                }


                %{--var tipoReporte = tipoClickMemo;--}%

                %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo--}%
                %{--+ "&totalPresupuesto=" + totalPres--}%
                %{--+ "&reajusteMemo=" + reajusteMemo--}%

            }

            if (active == 2) {

                firmasIdFormu = [];
                firmasFijasFormu = [];

                $("#bodyFirmas_polinomica").children("tr").each(function (i) {
                    firmasIdFormu[i] = $(this).data("id")

                })

                $("#firmasFijasPoli").children("tr").each(function (i) {


                    firmasFijasFormu[i] = $(this).data("id")

                });

//            console.log("3:" + firmasFijasFormu)



                if (firmasIdFormu.length == 0) {
                    firmasIdFormu = "";
                }
                if(firmasFijasFormu.length == 0){

                    firmasFijasFormu="";

                }

                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraFormu',id: obra?.id)}?firmasIdFormu=" + firmasIdFormu + "&totalPresupuesto=" + totalPres + "&firmasFijasFormu=" + firmasFijasFormu

            }

            //memoAdmi

            if(active == 3){

                var materiales = $("#materialesMemo").val()
                var manoObra = $("#manoObraMemo").val()
                var equipos = $("#equiposMemo").val()
                var costoPorcentaje = $("#costoPorcentaje").val()
                var costo = $("#costoMemo").val()
                var total = $("#totalMemoPresu").val()
                var texto = $("#notaMemoAd").val()
                var para = $("#paraMemoPresu").val()
                var de = $("#deMemoPresu").val()
                var fecha = $("#fechaMemoPresu").val()
                var asunto = $("#asuntoMemoPresu").val()

                firmasIdMP = [];
                firmasFijasMP = [];

                $("#bodyFirmas_memoPresu").children("tr").each(function (i) {
                    firmasIdMP[i] = $(this).data("id")


                });

                $("#firmasFijasMemoPresu").children("tr").each(function (i) {


                    firmasFijasMP[i] = $(this).data("id")


                });


                location.href = "${g.createLink(controller: 'reportes' ,action: 'reportedocumentosObraMemoAdmi',id: obra?.id)}?firmasIdMP=" +
                        firmasIdMP + "&totalPresupuesto=" + totalPres + "&firmasFijasMP=" + firmasFijasMP + "&materiales=" + materiales +
                        "&manoObra=" + manoObra + "&equipos=" + equipos + "&costoPorcentaje=" + costoPorcentaje + "&costo=" + costo + "&total=" + total +
                        "&texto=" + texto + "&para=" + para + "&de=" + de + "&fecha=" + fecha + "&asunto=" + asunto




            }
        }


    });

    $("#btnExcel").click(function () {


        $("#cambioMonedaExcel").dialog("open")

        %{--location.href = "${g.createLink(controller: 'reportes',action: 'documentosObraExcel',id: obra?.id)}"--}%

    });

    $("#btnAceptarMemo").click(function () {

        $("#frm-memo").submit();

        %{--success_func(location.href = "${g.createLink(controller: 'documentosObra',action: 'documentosObra',id: obra?.id)}")--}%
        %{--var tipoReporte = tipoClickMemo;--}%

        %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo--}%
        %{--+ "&totalPresupuesto=" + totalPres + "&proyeccionMemo=" + proyeccionMemo + "&reajusteIvaMemo=" + reajusteIvaMemo + "&reajusteMesesMemo=" + reajusteMesesMemo--}%
    });




    $("#btnEditarFor").click(function () {

        $("#notaFormula").attr("disabled", false);

    });

    $("#btnAceptarFor").click(function () {

        $("#frm-formula").submit();
    });

    $("#btnAceptarMemoPresu").click(function () {

        $("#frm-memoPresu").submit();
    });

    $("#btnAceptarAdjunto").click(function () {

        $("#frm-memoAdj").submit();
    });



    $("#btnEditarTextoF").click(function () {

        $("#presupuestoRef").attr("disabled", false);
        $("#baseCont").attr("disabled", false);
        $("#general").attr("disabled", false);
        $("#titulo").attr("disabled", false);

    });

    $("#btnAceptarTextoF").click(function () {

        $("#frm-textoFijo").submit();

    });

    $("#btnEditarTextoRet").click(function () {

        $("#retencion").attr("disabled", false);
        $("#notaAuxiliar").attr("disabled", false);

    });

    $("#btnAceptarTextoRet").click(function () {

        $("#frm-textoFijoRet").submit();
    });

    $("#piePaginaSel").change(function () {


//        ////console.log("entro")

        loadNota();

        $("#piePaginaSel").attr("disabled", false);
        $("#descripcion").attr("disabled", true);
        $("#texto").attr("disabled", true);
        $("#adicional").attr("disabled", true);
        $("#notaAdicional").attr("disabled", true)

    });

    $(".btnQuitar").click(function () {
        var strid = $(this).attr("id");
        var parts = strid.split("_");
        var tipo = parts[1];

    });

    $("#btnAdicionarMemo").click(function () {

        var nombreFirmas = $("#setFirmasMemo").val()

//        ////console.log(nombreFirmas)

        var tbody = $("#bodyFirmasMemo")

        var tr = $("<tr>")
        var tdNombre = $("<td>")
        var tdPuesto = $("<td>")

        var tdNumero = $("<td>")

        tdNombre.html(nombreFirmas)

        tr.append(tdNumero).append(tdNombre).append(tdPuesto).data({nombre: nombreFirmas})
        tbody.append(tr)

    });

    $("#btnAdicionarFor").click(function () {

        var nombreFirmas = $("#setFirmasFor").val()

//        ////console.log(nombreFirmas)

        var tbody = $("#bodyFirmasFor")

        var tr = $("<tr>")
        var tdNombre = $("<td>")
        var tdPuesto = $("<td>")

        var tdNumero = $("<td>")

        tdNombre.html(nombreFirmas)

        tr.append(tdNumero).append(tdNombre).append(tdPuesto).data({nombre: nombreFirmas})
        tbody.append(tr)

    });

    $("#btnAceptar").click(function () {

        $("#frm-nota").submit();

        %{--success_func( location.href="${g.createLink(controller: 'documentosObra',action: 'documentosObra',id: obra?.id)}")--}%

    });

    $("#btnNuevo").click(function () {


//        $("input[type=text]").val("");
//            $("textarea").val("");
        $("#piePaginaSel").attr("disabled", true);

        $("#descripcion").attr("disabled", false);
        $("#texto").attr("disabled", false);

        $("#notaAdicional").attr("checked", true)

        $("#adicional").attr("disabled", false);
        $("#descripcion").val("");
        $("#texto").val("");
        $("#adicional").val("");

        $("#notaAdicional").attr("disabled", false)

    });

    $("#btnCancelar").click(function () {

        $("#piePaginaSel").attr("disabled", false);
        loadNota();

        $("#descripcion").attr("disabled", true);
        $("#texto").attr("disabled", true);
        $("#adicional").attr("disabled", true);
        $("#notaAdicional").attr("disabled", true)
    });

    function desbloquear() {

        $("#piePaginaSel").attr("disabled", false);
        $("#descripcion").attr("disabled", false);
        $("#texto").attr("disabled", false);
//        $("#adicional").attr("disabled",false);
        $("#notaAdicional").attr("disabled", false)

    }

    $("#btnEditar").click(function () {

        loadNota();
        desbloquear();

    });

    $("#notaAdicional").click(function () {


//        ////console.log("click")

        if ($("#notaAdicional").attr("checked") == "checked") {

//            ////console.log("checked")
            $("#adicional").attr("disabled", false)

        }

        else {

//            ////console.log(" no checked")
            $("#adicional").attr("disabled", true)
//            $("#adicional").val("");
        }

    });


    $("#borrarFirmaPresuDialog").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 180,
        position: 'center',
        title: 'Eliminar firma',
        buttons: {
            "Aceptar": function () {

                $("#borrarFirmaPresu").parents("tr").remove();
                console.log($("#borrarFirmaPresu").children("tr"))

                $("#borrarFirmaPresuDialog").dialog("close");
                return false;
            },
            "Cancelar" : function () {

                $("#borrarFirmaPresuDialog").dialog("close");

            }
        }



    }) ;



    $("#tipoReporteDialog").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 180,
        position: 'center',
        title: 'Seleccione un Tipo de Reporte',
        buttons: {
            "Aceptar": function () {

                $("#tipoReporteDialog").dialog("close");

            }
        }

    });


    $("#cambioMonedaExcel").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 250,
        position: 'center',
        title: 'Tasa de cambio',
        buttons: {
            "Aceptar": function () {

                tasaCambio = $("#cambioMoneda").val()

                if (tasaCambio == "") {

                    $("#tasaCeroDialog").dialog("open");

                } else {

                    var url = "${g.createLink(controller: 'reportes',action: 'documentosObraTasaExcel',id: obra?.id)}?tasa=" + tasaCambio
//                             ////console.log(url)
                    location.href = url

                }

                $("#cambioMonedaExcel").dialog("close");


            },
            "Sin cambio": function () {

                location.href = "${g.createLink(controller: 'reportes',action: 'documentosObraExcel',id: obra?.id)}"

                $("#cambioMonedaExcel").dialog("close");


            },
            "Cancelar": function () {

                $("#cambioMonedaExcel").dialog("close");

            }


        }


    });

    $("#reajustePresupuestoDialog").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 230,
        position: 'center',
        title: 'Reajuste del Presupuesto',
        buttons: {
            "Aceptar": function () {


                proyeccion = $("#proyeccionReajuste").is(':checked');
                reajusteIva = $("#reajusteIva").is(':checked');
                reajusteMeses = $("#mesesReajuste").val();

//
//                        ////console.log(proyeccion)
//                        ////console.log(reajusteMeses)

//
                if (proyeccion == true && reajusteMeses == "") {


//                            ////console.log("entro!!")

                    $("#mesesCeroDialog").dialog("open")


                } else {


                    var tipoReporte = tipoClick;

                    location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObra',id: obra?.id)}?tipoReporte=" + tipoReporte + "&forzarValue=" + forzarValue + "&notaValue=" + notaValue
                            + "&firmasId=" + firmasId + "&proyeccion=" + proyeccion + "&iva=" + reajusteIva + "&meses=" + reajusteMeses + "&firmasFijas=" +firmasFijas


                    $("#reajustePresupuestoDialog").dialog("close");

                }


            },
            "Cancelar": function () {


                $("#reajustePresupuestoDialog").dialog("close");

            }
        }

    });


    $("#reajusteMemoDialog").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 230,
        position: 'center',
        title: 'Reajuste Memorando',
        buttons: {
            "Aceptar": function () {


                proyeccionMemo = $("#proyeccionReajusteMemo").is(':checked');
                reajusteIvaMemo = $("#reajusteIvaMemo").is(':checked');
                reajusteMesesMemo = $("#mesesReajusteMemo").val();
                paraMemo1 = $("#paraMemo").val()

                if (proyeccionMemo == true && reajusteMesesMemo == "") {
                    $("#mesesCeroDialog").dialog("open")


                } else {

                    var tipoReporte = tipoClickMemo;

                    location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo
                            + "&totalPresupuesto=" + totalPres + "&proyeccionMemo=" + proyeccionMemo + "&reajusteIvaMemo=" + reajusteIvaMemo + "&reajusteMesesMemo=" + reajusteMesesMemo  + "&para=" + paraMemo1 + "&firmasFijasMemo=" + firmasFijasMemo

                    $("#reajusteMemoDialog").dialog("close");

                }


            },
            "Cancelar": function () {


                $("#reajusteMemoDialog").dialog("close");

            }
        }

    });


    $("#maxFirmasDialog").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 200,
        position: 'center',
        title: 'Máximo Número de Firmas',
        buttons: {
            "Aceptar": function () {

                $("#maxFirmasDialog").dialog("close");

            }
        }

    });


    $("#mesesCeroDialog").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 200,
        position: 'center',
        title: 'No existe un valor en el campo Meses!',
        buttons: {
            "Aceptar": function () {

                $("#mesesCeroDialog").dialog("close");

            }
        }

    });


    $("#tasaCeroDialog").dialog({

        autoOpen: false,
        resizable: false,
        modal: true,
        draggable: false,
        width: 350,
        height: 200,
        position: 'center',
        title: 'No existe una tasa de cambio!',
        buttons: {
            "Aceptar": function () {

                $("#tasaCeroDialog").dialog("close");

            }
        }

    });


    $(function () {

        $("#materialesMemo").click(function () {
//         calculoPorcentaje();
            sumaTotal();

        });

        $("#manoObraMemo").click(function () {
//         calculoPorcentaje();
            sumaTotal();
        });

        $("#equiposMemo").click(function () {
//         calculoPorcentaje();
            sumaTotal();

        });



        $("#equiposMemo,#manoObraMemo,#materialesMemo").keydown(function (ev) {

            return validarNum(ev);

        }).keyup(function () {

                    calculoPorcentaje();
                    sumaTotal()

                });


        $("#costoPorcentaje").keydown(function (ev) {

            return validarNum(ev);


        }).keyup(function () {

                    calculoPorcentaje();
                    sumaTotal();

                });

        function  calculoPorcentaje() {


            var porcentaje = 0
//         porcentaje = ((parseFloat($("#materialesMemo").val()) + parseFloat($("#manoObraMemo").val()) + parseFloat($("#equiposMemo").val()))*($("#costoPorcentaje").val()))/100
            porcentaje = ((parseFloat($("#tMaterial").val()) + parseFloat($("#tMano").val()) + parseFloat($("#tEquipo").val()))*($("#costoPorcentaje").val()))/100

//         console.log("%:" + porcentaje)



            $("#costoMemo").val(number_format(porcentaje, 2, ".", ""));
        }

//



        function sumaTotal() {


            var total = 0.0



//       total = parseFloat($("#materialesMemo").val()) + parseFloat($("#manoObraMemo").val()) +
//               parseFloat($("#equiposMemo").val()) + parseFloat($("#costoMemo").val())


            total = parseFloat($("#tMaterial").val()) + parseFloat($("#tMano").val()) +
                    parseFloat($("#tEquipo").val()) + parseFloat($("#costoMemo").val())

            $("#totalMemoPresu").val(number_format(total,2,".",""))



        }

    });





</script>

</body>
</html>