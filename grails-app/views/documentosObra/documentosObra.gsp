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
        font-size   : 12px;
    }

    .aparecer {
        display : none;
    }

    .error {
        color       : #ff072f;
        margin-left : 140px;
    }

    </style>

    <title>Formato de Impresión</title>
</head>

<body>
<g:if test="${flash.message}">
    <div class="span6" style="float:right; width: 400px">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status" style="margin-bottom: 0px; margin-left: 15px;">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>

<div class="span12 hide" style="margin-bottom: 10px;" id="divError">
    <div class="alert alert-error" role="status">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <span id="spanError"></span>
    </div>
</div>

<div class="span12 hide" style="margin-bottom: 10px;" id="divOk">
    <div class="alert alert-info" role="status">
        <a class="close" data-dismiss="alert" href="#">×</a>
        <span id="spanOk"></span>
    </div>
</div>

<div style='page-break-after: auto'></div>

<div id="tabs" style="width: 800px; height: 1060px">
    <ul>
        <li><a href="#tab-presupuesto">Presupuesto</a></li>
        <li><a href="#tab-memorando">Memorando</a></li>
        <li><a href="#tab-polinomica">F. Polinómica</a></li>
        <li><a href="#tab-memorandoPresu">Adm. Directa/Cogestión</a></li>
        <li><a href="#tab-textosFijos">Textos Fijos</a></li>
    </ul>

    <div id="tab-presupuesto" class="tab">

        <div class="tipoReporte">

            <fieldset class="borde">

                <legend>Tipo de Reporte</legend>

                <div class="span6" style="margin-bottom: 10px; margin-top: -20px">

                    <input type="radio" name="tipoPresupuesto" class="radioPresupuesto uno" value="1" checked="true"/>  Base de Contrato

                    <input type="radio" name="tipoPresupuesto" class="radioPresupuesto" value="2"
                           style="margin-left: 220px"/> Presupuesto Referencial

                </div>
                <div class="span6" style="margin-bottom: 10px; ">

                    <input type="radio" name="encabezado" class="encabezado uno" value="1" checked="true"/>  Con encabezado

                    <input type="radio" name="encabezado" class="encabezado" value="0"
                           style="margin-left: 220px"/> Sin encabezado

                </div>
            </fieldset>

        </div>


        <div class="piePagina" style="margin-bottom: 10px">

            <g:form class="registroNota" name="frm-nota" controller="nota" action="save">
                <fieldset class="borde">

                    %{--<div style="margin-top: -20px; margin-bottom: 20px; margin-left: 30px">--}%

                    %{--<g:checkBox name="forzar" checked="false"/> Forzar nueva Página para las Notas de Pie de Página--}%

                    %{--</div>--}%

                    <legend>Pie de Página</legend>

                    <div class="span6" style="margin-top: -10px">

                        <div id="div_sel">
                            <g:select name="piePaginaSel" from="${janus.Nota.findAllByTipoIsNull()}" value="${nota?.id}" optionValue="descripcion"
                                      optionKey="id" style="width: 300px;" noSelection="['-1': 'Seleccione una nota...']"/>
                        </div>

                        <div class="btn-group" style="margin-left: 310px; margin-top: -60px; margin-bottom: 10px">
                            <a class="btn" id="btnNuevo"><i class="icon-pencil"></i> Nuevo</a>
                            <a class="btn" id="btnCancelar"><i class="icon-eraser"></i> Cancelar</a>
                            <a class="btn" id="btnAceptar"><i class="icon-ok"></i> Grabar</a>
                            <a class="btn" id="btnEliminar"><i class="icon-remove"></i> Eliminar</a>
                        </div>

                    </div>
                    <g:hiddenField name="obra" value="${obra?.id}"/>

                    <div class="span7">
                        <div style="margin-left: -1px">Nombre de la Nota: <g:textField name="descripcion" value="${nota?.descripcion}" style="width: 480px; margin-left: 20px" class="required" maxlength="253"/></div>
                    </div>


                    <div class="span6">
                        <div class="span2" style="margin-left: -1px">Descripción de la Nota</div>

                        <g:textArea name="texto" value="${nota?.texto}" rows="5" cols="5"
                                    style="height: 125px; width:685px ; resize: none" maxlength="1023"/>
                    </div>

                    <div class="span6" style="margin-top: 5px; margin-bottom: 10px">

                        %{--<g:checkBox name="notaAdicional" checked="false"--}%
                        %{--/> --}%
                        Nota al Pie Adicional (15 líneas aprox)

                    </div>

                    <div class="span6">
                        <g:textArea name="adicional" value="${nota?.adicional}" rows="5" cols="5"
                                    style="height: 125px; width:685px ; resize: none" maxlength="1023"/>
                    </div>

                    <g:hiddenField name="obraTipo" value="${obra?.claseObra?.tipo}"/>

                </fieldset>

            </g:form>

        </div>

        <div class="setFirmas" style="margin-top: -20px">

            <fieldset class="borde">

                <legend>Firmas</legend>


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
                        %{--<g:if test="${firmaDirector != null}">--}%
                        %{--<tr data-id="${firmaDirector?.persona?.id}">--}%
                        %{--<td id="${firmaDirector?.persona?.nombre + " " + firmaDirector?.persona?.apellido}">--}%

                        %{--${firmaDirector?.persona?.nombre + " " + firmaDirector?.persona?.apellido}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                        %{--DIRECTOR                           --}%
                        %{--</td>--}%
                        %{--</tr>--}%
                        %{--</g:if>--}%
                        %{--<g:else>--}%
                        %{--<tr>--}%
                        %{--<td style="color: #ff2a08">--}%
                        %{--DIRECCIÓN SIN DIRECTOR--}%
                        %{--</td>--}%
                        %{--<td>--}%
                        %{--DIRECTOR--}%
                        %{--</td>--}%
                        %{--</tr>--}%
                        %{--</g:else>--}%

                        %{--<g:if test="${obra?.inspector?.id != null}">--}%
                        %{--<tr data-id="${obra?.inspector?.id}">--}%

                        %{--<td id=" ${obra?.inspector?.nombre + " " + obra?.inspector?.apellido}">--}%
                        %{--${obra?.inspector?.nombre + " " + obra?.inspector?.apellido}--}%
                        %{--</td>--}%
                        %{--<td>--}%

                        %{--RESPONSABLE DEL PROYECTO--}%
                        %{--</td>--}%
                        %{--</tr>--}%
                        %{--</g:if>--}%
                        %{--<g:else>--}%

                        %{--<tr>--}%
                        %{--<td style="color: #ff2a08">--}%

                        %{--SIN RESPONSABLE DEL PROYECTO--}%
                        %{--</td>--}%
                        %{--<td>--}%
                        %{--RESPONSABLE DEL PROYECTO--}%
                        %{--</td>--}%

                        %{--</tr>--}%
                        %{--</g:else>--}%


                        %{--//nuevo--}%

                        <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
                            <g:if test="${duenoObra == 1}">
                                <tr>
                                    <td>
                                        <g:select name="coordinador" from="${personasUtfpuCoor}" optionValue="persona" optionKey="id" style="width: 380px"/>
                                    </td>
                                    <td>
                                        COORDINADOR
                                    </td>
                                </tr>
                            </g:if>
                            <g:else>
                                <g:if test="${cordinadorOtros[0]}">
                                    <tr>
                                        <td style="color: #ff2a08">
                                            <g:hiddenField name="coordinador" value="${cordinadorOtros[0]?.id}"/>
                                            <g:textField name="coordinadorText" value="${cordinadorOtros[0]?.persona?.nombre + ' ' + cordinadorOtros[0]?.persona?.apellido}" readonly="readonly" style="width: 380px"/>
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:if>
                                <g:else>
                                    <tr>
                                        <td style="color: #ff2a08">
                                            SIN COORDINADOR
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:else>

                            </g:else>

                        </g:if>
                        <g:else>
                            <g:if test="${duo == 1}">

                                <tr>
                                    <td style="color: #ff2a08">
                                        <g:hiddenField name="coordinador" value="${personasUtfpuCoor[0]?.id}"/>
                                        <g:textField name="coordinadorText" value="${personasUtfpuCoor[0]?.persona?.nombre + ' ' + personasUtfpuCoor[0]?.persona?.apellido}" readonly="readonly" style="width: 380px"/>
                                    </td>
                                    <td>
                                        COORDINADOR
                                    </td>
                                </tr>

                            </g:if>
                            <g:else>
                                <g:if test="${coordinadores}">
                                    <tr>
                                        <td style="color: #ff2a08">
                                            <g:select name="coordinador" from="${coordinadores}" optionValue="persona" optionKey="id" style="width: 380px"/>
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:if>
                                <g:else>
                                    <tr>
                                        <td style="color: #ff2a08">
                                            SIN COORDINADOR
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:else>

                            </g:else>
                        </g:else>




                        %{--//old--}%
                        %{--<g:if test="${coordinadores != null}">--}%
                        %{--<g:if test="${duenoObra == 1 && persona?.departamento?.codigo == 'UTFPU'}">--}%
                        %{--<tr>--}%
                        %{--<td>--}%
                        %{--<g:select name="coordinador" from="${personasUtfpuCoor}" optionValue="persona" optionKey="id" style="width: 380px"/>--}%
                        %{--</td>--}%
                        %{--<td>--}%
                        %{--COORDINADOR--}%
                        %{--</td>--}%
                        %{--</tr>--}%
                        %{--</g:if>--}%
                        %{--<g:else>--}%
                        %{--<tr>--}%
                        %{--<td>--}%
                        %{--<g:select name="coordinador" from="${coordinadores}" optionValue="persona" optionKey="id" style="width: 380px"/>--}%
                        %{--</td>--}%
                        %{--<td>--}%
                        %{--COORDINADOR--}%
                        %{--</td>--}%
                        %{--</tr>--}%
                        %{--</g:else>--}%

                        %{--</g:if>--}%
                        %{--<g:else>--}%
                        %{--<tr>--}%
                        %{--<td style="color: #ff2a08">--}%
                        %{--SIN COORDINADOR--}%
                        %{--</td>--}%
                        %{--<td>--}%
                        %{--COORDINADOR--}%
                        %{--</td>--}%
                        %{--</tr>--}%
                        %{--</g:else>--}%





                        %{--<tr data-id="${obra?.revisor?.id}">--}%

                        %{----}%
                        %{--<td id=" ${obra?.revisor?.nombre + " " + obra?.revisor?.apellido + " " + "       (REVISOR)"}">--}%
                        %{--${obra?.revisor?.nombre + " " + obra?.revisor?.apellido}--}%
                        %{--</td>--}%
                        %{--<td>--}%

                        %{--SUPERVISIÓN--}%
                        %{--</td>--}%
                        %{----}%

                        %{--</tr>--}%


                        %{--<tr data-id="${obra?.responsableObra?.id}">--}%

                        %{--<td id=" ${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido + " " + " (ELABORO)"}">--}%
                        %{--${obra?.responsableObra?.nombre + " " + obra?.responsableObra?.apellido}--}%
                        %{--</td>--}%
                        %{--<td>--}%
                        %{--ELABORÓ--}%
                        %{--</td>                     --}%
                        %{--</tr>--}%

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

                %{--<input type="radio" name="tipoPresupuestoMemo" class="radioPresupuestoMemo" value="2"--}%
                %{--style="margin-left: 220px"/> Presupuesto Referencial--}%

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


                    %{--<div class="span3"><g:textField name="deMemo" style="width: 470px"--}%
                    %{--value="${obra?.departamento?.direccion?.nombre}"--}%
                    %{--disabled="true"/></div>--}%


                    <div class="span3"><g:textField name="deMemo" style="width: 470px" value="${'COORDINACIÓN DE FIJACIÓN DE PRECIOS UNITARIOS'}" disabled="true"/></div>

                </div>

                <div class="span6">
                    <div class="span1">PARA:</div>

                    <div class="span3"><g:textField name="paraMemo" value="${obra?.departamento?.direccion?.nombre + ' - ' + obra?.departamento?.descripcion}"
                                                    style="width: 470px" disabled="true"/></div>

                    %{--<div class="span3"><g:select name="paraMemo" from="${janus.Direccion.list()}" optionKey="id" optionValue="nombre" style="width: 485px"/></div>--}%
                </div>

                <div class="span7">
                    <div class="span1">Valor de la Base:</div>

                    <div class="span2">
                        <g:textField name="baseMemo" style="width: 100px" disabled="true" value="${totalPresupuestoBien}"/>
                    </div>
                </div>

            </fieldset>

        </div>

        <div class="texto">

            <fieldset class="borde">
                <legend>Texto</legend>

                <g:form class="memoGrabar" name="frm-memo" controller="auxiliar" action="saveDoc">

                    <g:hiddenField name="id" value="${"1"}"/>

                    <g:hiddenField name="obra" value="${obra?.id}"/>

                    <div class="span6" style="margin-top: -10px; margin-left: 110px">
                        <div id="divSelMemo">
                            <g:select name="selMemo" from="${notaMemo}" value="${nota?.id}" optionValue="descripcion"
                                      optionKey="id" style="width: 300px;" noSelection="['-1': 'Seleccione una nota...']"/>
                        </div>

                        <div class="span6" style="margin-top: 10px">
                            <div class="btn-group" style="margin-left: 310px; margin-bottom: 10px; margin-top: -80px">
                                %{--<button class="btn" id="btnEditarMemo"><i class="icon-pencil"></i> Editar</button>--}%
                                %{--<button class="btn" id="btnAceptarMemo"><i class="icon-ok"></i> Aceptar</button>--}%
                                <a class="btn" id="btnNuevoMemo"><i class="icon-pencil"></i> Nuevo</a>
                                %{--<a class="btn" id="btnCancelarMemo"><i class="icon-eraser"></i> Cancelar</a>--}%
                                <a class="btn" id="btnAceptarMemo"><i class="icon-ok"></i> Grabar</a>
                                <a class="btn" id="btnEliminarMemo"><i class="icon-remove"></i> Eliminar</a>
                            </div>
                        </div>
                    </div>

                    <div class="span7" style="margin-left: 100px">
                        <div style="margin-left: -1px">Nombre de la Nota: <g:textField name="descripcionMemo" value="${nota?.descripcion}" style="width: 480px; margin-left: 20px" maxlength="253"/></div>
                    </div>


                    <div class="span6">

                        <div class="span1">Texto</div>

                        <div class="span3"><g:textArea name="memo1" value="${auxiliarFijo?.memo1}" rows="4" cols="4"
                                                       style="width: 600px; height: 55px; margin-left: -50px;resize: none;" maxlength="1023"/></div>

                    </div>


                    <div class="span6">
                        <div class="span1">Pie</div>

                        <div class="span3"><g:textArea name="memo2" value="${auxiliarFijo?.memo2}" rows="4" cols="4"
                                                       style="width: 600px; height: 55px; margin-left: -50px; resize: none;" maxlength="1023"/></div>

                    </div>

                </g:form>

            </fieldset>

        </div>


        <div class="setFirmas" style="margin-top: -10px">

            <fieldset class="borde">

                <legend>Firmas</legend>

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
                            <th style="width: 350px">Nombre</th>
                            <th style="width: 250px">Rol</th>
                        </tr>

                        </thead>

                        <tbody id="firmasFijasMemo">

                        <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
                            <g:if test="${duenoObra == 1}">
                                <tr>
                                    <td>
                                        <g:select name="coordinador" from="${personasUtfpuDire}" optionValue="persona" optionKey="id" style="width: 380px"/>
                                    </td>
                                    <td>
                                        DIRECTOR/COORDINADOR
                                    </td>
                                </tr>
                            </g:if>
                            <g:else>
                                <g:if test="${cordinadorOtros[0]}">
                                    <tr>
                                        <td style="color: #ff2a08">
                                            <g:hiddenField name="coordinador" value="${cordinadorOtros[0]?.id}"/>
                                            <g:textField name="coordinadorText" value="${cordinadorOtros[0]?.persona?.nombre + ' ' + cordinadorOtros[0]?.persona?.apellido}" readonly="readonly" style="width: 380px"/>
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:if>
                                <g:else>
                                    <tr>
                                        <td style="color: #ff2a08">
                                            SIN COORDINADOR
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:else>

                            </g:else>

                        </g:if>
                        <g:else>
                            <g:if test="${duo == 1}">

                                <tr>
                                    <td style="color: #ff2a08">
                                        <g:hiddenField name="coordinador" value="${personasUtfpuCoor[0]?.id}"/>
                                        <g:textField name="coordinadorText" value="${personasUtfpuCoor[0]?.persona?.nombre + ' ' + personasUtfpuCoor[0]?.persona?.apellido}" readonly="readonly" style="width: 380px"/>
                                    </td>
                                    <td>
                                        COORDINADOR
                                    </td>
                                </tr>

                            </g:if>
                            <g:else>
                                <g:if test="${coordinadores}">
                                    <tr>
                                        <td style="color: #ff2a08">
                                            <g:select name="coordinador" from="${coordinadores}" optionValue="persona" optionKey="id" style="width: 380px"/>
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:if>
                                <g:else>
                                    <tr>
                                        <td style="color: #ff2a08">
                                            SIN COORDINADOR
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:else>

                            </g:else>
                        </g:else>

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

                </g:form>
                <div class="span6">

                    <div id="divSelFormu">
                        <g:select name="selFormu" from="${notaFormu}" value="${nota?.id}" optionValue="descripcion"
                                  optionKey="id" style="width: 300px;" noSelection="['-1': 'Seleccione una nota...']"/>
                    </div>

                    <div class="span6">
                        <div class="btn-group" style="margin-left: 310px; margin-bottom: 10px; margin-top: -60px">
                            <a class="btn" id="btnNuevoFormu"><i class="icon-pencil"></i> Nuevo</a>
                            <a class="btn" id="btnAceptarFormu"><i class="icon-ok"></i> Grabar</a>
                            <a class="btn" id="btnEliminarFormu"><i class="icon-remove"></i> Eliminar</a>
                        </div>
                    </div>
                </div>


                <div class="span7" style="margin-left: 30px">
                    <div style="margin-left: -1px">Nombre de la Nota: <g:textField name="descripcionFormu" value="${nota?.descripcion}" style="width: 480px; margin-left: 20px" maxlength="253"/></div>
                </div>


                <div class="span6">
                    <div class="span3"><g:textArea name="notaFormula" rows="4" value="${notaFormu}"
                                                   cols="4"
                                                   style="width: 690px; margin-left: -30px; height: 70px; resize: none" maxlength="1022"/></div>

                </div>
            </fieldset>

        </div>


        <div class="setFirmas" style="margin-top: -10px">

            <fieldset class="borde">

                <legend>Firmas</legend>

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

                        <g:if test="${persona?.departamento?.codigo == 'UTFPU'}">
                            <g:if test="${duenoObra == 1}">
                                <tr>
                                    <td>
                                        <g:select name="coordinador" from="${personasUtfpuCoor}" optionValue="persona" optionKey="id" style="width: 380px"/>
                                    </td>
                                    <td>
                                        COORDINADOR
                                    </td>
                                </tr>
                            </g:if>
                            <g:else>
                                <g:if test="${cordinadorOtros[0]}">
                                    <tr>
                                        <td style="color: #ff2a08">
                                            <g:hiddenField name="coordinador" value="${cordinadorOtros[0]?.id}"/>
                                            <g:textField name="coordinadorText" value="${cordinadorOtros[0]?.persona?.nombre + ' ' + cordinadorOtros[0]?.persona?.apellido}" readonly="readonly" style="width: 380px"/>
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:if>
                                <g:else>
                                    <tr>
                                        <td style="color: #ff2a08">
                                            SIN COORDINADOR
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:else>
                            </g:else>
                        </g:if>
                        <g:else>
                            <g:if test="${duo == 1}">
                                <tr>
                                    <td style="color: #ff2a08">
                                        <g:hiddenField name="coordinador" value="${personasUtfpuCoor[0]?.id}"/>
                                        <g:textField name="coordinadorText" value="${personasUtfpuCoor[0]?.persona?.nombre + ' ' + personasUtfpuCoor[0]?.persona?.apellido}" readonly="readonly" style="width: 380px"/>
                                    </td>
                                    <td>
                                        COORDINADOR
                                    </td>
                                </tr>
                            </g:if>
                            <g:else>
                                <g:if test="${coordinadores}">
                                    <tr>
                                        <td style="color: #ff2a08">
                                            <g:select name="coordinador" from="${coordinadores}" optionValue="persona" optionKey="id" style="width: 380px"/>
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:if>
                                <g:else>
                                    <tr>
                                        <td style="color: #ff2a08">
                                            SIN COORDINADOR
                                        </td>
                                        <td>
                                            COORDINADOR
                                        </td>
                                    </tr>
                                </g:else>

                            </g:else>
                        </g:else>

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
                        <button class="btn" id="btnEditarTextoF"><i class="icon-pencil"></i> Editar</button>
                        <button class="btn" id="btnAceptarTextoF"><i class="icon-ok"></i> Aceptar</button>
                    </div>
                </div>
            </fieldset>

        </div>

        <div class="cabecera">

            <fieldset class="borde">
                <legend>Pie de Página</legend>

            %{--<g:form class="memoGrabar" name="frm-textoFijoRet" controller="auxiliar" action="saveDoc">--}%
                <g:form class="memoGrabar" name="frm-textoFijoRet" controller="auxiliar" action="savePiePaginaTF">

                    <g:hiddenField name="id" value="${"1"}"/>
                    <g:hiddenField name="obra" value="${obra?.id}"/>

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
                        <button class="btn" id="btnEditarTextoRet"><i class="icon-pencil"></i> Editar</button>
                        <button class="btn" id="btnAceptarTextoRet"><i class="icon-ok"></i> Aceptar</button>

                    </div>
                </div>

            </fieldset>

        </div>

    </div>


    <div id="tab-memorandoPresu" class="tab" style="">

        <div class="cabecera">

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
                    <div class="span3"><g:textField name="fechaMemoPresu" style="width: 200px" value="${new java.util.Date().format("dd-MM-yyyy")}" disabled="true"/></div>
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
                </g:form>

                <div class="span6">
                    <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                        <button class="btn" id="btnEditarMemoPresu"><i class="icon-pencil"></i> Editar</button>
                        <button class="btn" id="btnAceptarMemoPresu"><i class="icon-ok"></i> Aceptar</button>

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
                        <button class="btn" id="btnEditarAdjunto"><i class="icon-pencil"></i> Editar</button>
                        <button class="btn" id="btnAceptarAdjunto"><i class="icon-ok"></i> Aceptar</button>

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
                        <div class="span5">Presupuesto Referencial por Contrato:</div>

                        <div>
                            <g:textField name="baseMemoPresu" style="width: 100px" disabled="true"
                                         value="${formatNumber(number: totalPresupuestoBien, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}"/>
                        </div>
                    </div>

                    <div class="span7">
                        <div class="span5">Materiales:</div>

                        <g:set var="totalMaterial" value="${0}"/>
                        <g:each in="${resComp}" var="r">
                            <g:set var="totalMaterial" value="${totalMaterial + ((r.transporte + r.precio) * r.cantidad)}"/>
                        </g:each>
                        <div>
                            <g:hiddenField name="tMaterial" value="${totalMaterial}"/>
                            <g:textField name="materialesMemo" style="width: 100px" value="${formatNumber(number: totalMaterial, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}" readonly="true"/>
                        </div>
                    </div>

                    <div class="span7">
                        <div class="span5">Mano de Obra:</div>
                        <g:set var="totalMano" value="${0}"/>
                        <g:each in="${resMano}" var="r">
                            <g:set var="totalMano" value="${totalMano + ((r.transporte + r.precio) * r.cantidad)}"/>
                        </g:each>
                        <div>
                            <g:hiddenField name="tMano" value="${totalMano}"/>
                            <g:textField name="manoObraMemo" style="width: 100px" value="${formatNumber(number: totalMano, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}" readonly="true"/>
                        </div>
                    </div>

                    <div class="span7">
                        <div class="span5">Equipos:</div>
                        <g:set var="totalEquipo" value="${0}"/>
                        <g:each in="${resEq}" var="r">
                            <g:set var="totalEquipo" value="${totalEquipo + ((r.transporte + r.precio) * r.cantidad)}"/>
                        </g:each>
                        <div>
                            <g:hiddenField name="tEquipo" value="${totalEquipo}"/>
                            <g:textField name="equiposMemo" style="width: 100px" value="${formatNumber(number: totalEquipo, format: '##,##0', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}" readonly="true"/>
                        </div>
                    </div>

                    <div class="span8">

                        <div class="span5">Costos Indirectos:</div>

                        <div class="span3" style="margin-left: -100px; width: 240px">
                            %{--<div class="input-append">--}%
                            %{--<g:textField name="costoPorcentaje" type="number" style="width: 30px" maxlength="3"/>--}%
                            <input id="costoPorcentaje" name="costoPorcentaje" type="number" style="width: 60px" maxlength="3" max="25" min="0" step="0.1"/>
                            <span class="add-on">%</span>
                            %{--</div>--}%
                            <g:textField name="costoMemo" style="width: 100px" disabled="true"/>
                        </div>

                        <div class="span5">Timbres y costos financieros (para materiales):</div>

                        <div class="span3" style="margin-left: -90px; width: 240px" >
                            %{--<div class="input-append">--}%
                            %{--<g:textField name="pcntFinanciero" type="number" style="width: 30px" maxlength="3" max="5" min="0"/>--}%
                            <input id="pcntFinanciero" name="pcntFinanciero" type="number" style="width: 50px" maxlength="3" max="5" min="0" step="0.1"/>
                            <span class="add-on">%</span>
                            %{--</div>--}%
                            <g:textField name="costoFinanciero" style="width: 100px" disabled="true"/>
                        </div>

                    </div>

                    <div class="span8">
                        <div class="span5">TOTAL:</div>

                        <div class="span2" style="margin-left: 0px"><g:textField name="totalMemoPresu" style="width: 100px" disabled="true"/></div>
                    </div>
                </div>

            </fieldset>

        </div>


        <div class="setFirmas" style="margin-top: -10px">

            <fieldset class="borde">

                <legend>Firmas</legend>

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
                        <g:if test="${firmaDirector != null}">
                            <tr data-id="${firmaDirector?.persona?.id}">

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

                                <td id="${firmaDirector?.persona?.nombre + " " + firmaDirector?.persona?.apellido}">

                                    ${firmaDirector?.persona?.nombre + " " + firmaDirector?.persona?.apellido}

                                </td>
                                <td>

                                    DIRECTOR

                                </td>

                            </tr>
                        %{--</g:if>--}%
                        </g:if>
                        <g:else>

                            <tr>
                                <td style="color: #ff2a08">

                                    DIRECCIÓN SIN DIRECTOR
                                </td>
                                <td>
                                    DIRECTOR
                                </td>

                            </tr>

                        </g:else>

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
                                SUPERVISIÓN
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


    <div class="btn-group" style="margin-bottom: 10px; margin-top: 20px; margin-left: 180px; text-align: center">
        <button class="btn" id="btnSalir"><i class="icon-arrow-left"></i> Regresar</button>
        <a href="#" class="btn" id="btnImprimir"><i class="icon-print"></i> Imprimir</a>
        <a href="#" class="btn" id="btnImprimirDscr"><i class="icon-print"></i> con descripción</a>
        <a href="#" class="btn" id="btnImprimirVae"><i class="icon-print"></i> Imprimir VAE</a>
        <button class="btn aparecer" id="btnDocExcel"><i class="icon-list-alt"></i> Excel</button>

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

                Meses: <g:textField name="mesesReajuste" style="width: 55px; margin-left: 20px"/>
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
                <br>
                Es necesario colocar un número válido en el campo <b>Meses</b> !!
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

                Meses: <g:textField name="mesesReajusteMemo" style="width: 55px; margin-left: 20px"/>
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
    var notaMemoValue;

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

    var firmaCoordinador;
    var firmaElaboro;

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
            type     : "POST",
            dataType : 'json',
            url      : "${g.createLink(action:'getDatos')}",
            data     : {id : idPie},
            success  : function (msg) {

                $("#descripcion").val(msg.descripcion);
                $("#texto").val(msg.texto);
                $("#adicional").val(msg.adicional);

            }
        });
    }

    loadNota();

    function loadNotaMemo() {
        var idPie = $("#selMemo").val();
        $.ajax({
            type     : 'POST',
            dataType : 'json',
            url      : "${g.createLink(action: 'getDatosMemo')}",
            data     : {id : idPie},
            success  : function (msg) {
                $("#descripcionMemo").val(msg.descripcion);
                $("#memo1").val(msg.texto);
                $("#memo2").val(msg.adicional);
            }
        })
    }

    loadNotaMemo();

    function loadNotaFormu() {
        var idPie = $("#selFormu").val();
        $.ajax({
            type     : 'POST',
            dataType : 'json',
            url      : "${g.createLink(action: 'getDatosFormu')}",
            data     : {id : idPie},
            success  : function (msg) {
                $("#descripcionFormu").val(msg.descripcion);
                $("#notaFormula").val(msg.texto);
            }
        })
    }

    loadNotaFormu();

    $("#tabs").tabs();

    $("#btnSalir").click(function () {

        location.href = "${g.createLink(controller: 'obra', action: 'registroObra')}" + "?obra=" + "${obra?.id}";

    });

    $(".borrarFirmaPresu").click(function () {


//        $("#borrarFirmaPresuDialog").dialog("open")
//        $(this).parents("tr").remove()

        var ob = $(this)

        var obNombre = ob.parents("tr").children("td").first().attr("id")

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

        var obNombre = ob.parents("tr").children("td").first().attr("id")

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

        var obNombre = ob.parents("tr").children("td").first().attr("id")

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

        var obNombre = ob.parents("tr").children("td").first().attr("id")

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
        var tbodyMemo = $("#firmasFijasMemo");
        var tbodyPoli = $("#firmasFijasPoli");
        var tbodyMemoPresu = $("#firmasFijasMemoPresu")

        var active = $("#tabs").tabs("option", "active");

        if (active == 0) {
            maxFirmas = (tbody.children("tr").length) + (tbodyPresu.children("tr").length) + 1;
        }
        if (active == 1) {
            maxFirmas = (tbody.children("tr").length) + (tbodyMemo.children("tr").length) + 1;
        }
        if (active == 2) {
            maxFirmas = (tbody.children("tr").length) + (tbodyPoli.children("tr").length) + 1;
        }
        if (active == 3) {

            maxFirmas = (tbody.children("tr").length) + (tbodyMemoPresu.children("tr").length) + 1;

        }

//        //console.log(maxFirmas)

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
                tr.append(tdNombre).append(tdPuesto).append(tdDel).data({nombre : nombre, puesto : puesto, id : id});

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

    //    $(".radioPresupuesto").click(function () {
    //
    //        tipoClick = $(this).attr("value")
    //
    //        return tipoClick
    //    });

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
//        //////console.log(porcentajeCal)
//        //////console.log(totalPres)
//        //////console.log(base)
//        //////console.log("entro cal!")

    });

    var active2 = $("#tabs").tabs("option", "event")

    $("#tabs").click(function () {

        var active = $("#tabs").tabs("option", "active")

        if(active == 0){
            $("#btnImprimirVae").show()
            $("#btnImprimirDscr").show()
        }else{
            $("#btnImprimirDscr").hide()
            $("#btnImprimirVae").hide()
        }

        if (active != 2) {
            $("#btnDocExcel").hide();
        }
        else {
            $("#btnDocExcel").show();
        }

        if (active == 4) {

            $("#btnImprimir").hide()

        }
        else {
            $("#btnImprimir").show()
        }
    });

    $("#btnImprimir").click(function () {
        if (!$(this).hasClass("disabled")) {
            reajusteMemo = $("#reajusteMemo").val()
            //////console.log("Memo:" + reajusteMemo)

            var active = $("#tabs").tabs("option", "active");
            console.log("activo:" + active)
            if (active == 0) {

                var idCoordinador = $("#coordinador").val()
                var idFirmaCoor

                if (idCoordinador != null) {
                    idFirmaCoor = $("#coordinador").val()
                } else {
                    idFirmaCoor = ''
                }

                firmasId = '';
                firmasFijas = '';
                firmaCoordinador = idFirmaCoor
                firmaElaboro = ${obra?.responsableObra?.id}

                        $("#firmasFijasPresu").children("tr").each(function (i) {
                            if ($(this).data("id")) {
                                if (firmasFijas != '') {
                                    firmasFijas += ','
                                } else {
                                    firmasFijas += '-1,'
                                }
                                firmasFijas += $(this).data("id")
                            }
                        });
//           //console.log("1:" + firmasFijas)

                notaValue = $("#piePaginaSel").val();

                if ($("#forzar").attr("checked") == "checked") {
                    forzarValue = 1;
                } else {
                    forzarValue = 2;
                }
                if (1 != 1) {
                    $("#tipoReporteDialog").dialog("open");
                } else {
                    proyeccion = $("#proyeccionReajuste").is(':checked');
                    reajusteIva = $("#reajusteIva").is(':checked');
                    reajusteMeses = $("#mesesReajuste").val();

                    var tipoReporte

                    if ($(".uno").is(':checked')) {
                        tipoReporte = 1
                    } else {
                        tipoReporte = 2
                    }

                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'nota', action: 'saveNota')}",
                        data    : {
                            piePaginaSel : $("#piePaginaSel").val(),
                            obra         : ${obra?.id},
                            descripcion  : $("#descripcion").val(),
                            texto        : $("#texto").val(),
                            adicional    : $("#adicional").val(),
                            obraTipo     : "${obra?.claseObra?.tipo}"
                        },
                        success : function (msg) {
                            var part = msg.split('_');
//                            //console.log(msg)
                            if (part[0] == 'ok') {
//                                $("#divOk").show(msg);
                                //console.log($(".encabezado:checked").val());
                                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObra',id: obra?.id)}?tipoReporte=" + tipoReporte +
                                "&forzarValue=" + forzarValue + "&notaValue=" + part[1] + "&firmasId=" + firmasId +
                                "&proyeccion=" + proyeccion + "&iva=" + reajusteIva + "&meses=" + reajusteMeses +
                                "&firmasFijas=" + firmasFijas + "&firmaCoordinador=" + firmaCoordinador +
                                "&firmaElaboro=" + firmaElaboro + "&encabezado=" + $(".encabezado:checked").val();

                            }
                        }
                    });
                }
            }

            if (active == 1) {   //Memorando

                var idCoordinador = $("#coordinador").val()

                var idFirmaCoor

                if (idCoordinador != null) {

                    idFirmaCoor = $("#coordinador").val()
                } else {

                    idFirmaCoor = ''
                }

                firmaCoordinador = idFirmaCoor

                firmasIdMemo = [];
                firmasFijasMemo = [];

                notaMemoValue = $("#selMemo").val()

                var paraMemo = $("#paraMemo").val()

                var textoMemo = $("#memo1").val()
                var pieMemo = $("#memo2").val()

                $("#bodyFirmas_memo").children("tr").each(function (i) {
                    firmasIdMemo[i] = $(this).data("id")
                })

                $("#firmasFijasMemo").children("tr").each(function (i) {
                    firmasFijasMemo[i] = $(this).data("id")
                });

//            //console.log("2:" + firmasFijasMemo)

                if (firmasIdMemo.length == 0) {

                    firmasIdMemo = "";
                }
                if (firmasFijasMemo.length == 0) {

                    firmasFijasMemo = "";
                }

                if (tipoClickMemo == 1) {
                    $("#reajusteMemoDialog").dialog("open")
                }
                else {
                    var tipoReporte = tipoClickMemo;

                    $.ajax({

                        type    : "POST",
                        url     : "${createLink(controller: 'nota', action: 'saveNotaMemo')}",
                        data    : {
                            piePaginaSel : $("#selMemo").val(),
                            obra         : ${obra?.id},
                            descripcion  : $("#descripcionMemo").val(),
                            texto        : $("#memo2").val(),
                            adicional    : $("#memo1").val(),
                            obraTipo     : "${obra?.claseObra?.tipo}"
                        },
                        success : function (msg) {
                            var part = msg.split('_');
//                            //console.log(msg)
                            if (part[0] == 'ok') {
//                                $("#divOk").show(msg);

                                %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObra',id: obra?.id)}?tipoReporte=" + tipoReporte + "&forzarValue=" + forzarValue + "&notaValue=" + part[1]--}%
                                %{--+ "&firmasId=" + firmasId + "&proyeccion=" + proyeccion + "&iva=" + reajusteIva + "&meses=" + reajusteMeses + "&firmasFijas=" +firmasFijas + "&firmaCoordinador=" + firmaCoordinador--}%
//                                //console.log("LINK 1")
                                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte +
                                "&firmasIdMemo=" + firmasIdMemo  + "&totalPresupuesto=" + totalPres + "&proyeccionMemo=" + proyeccionMemo +
                                "&reajusteIvaMemo=" + reajusteIvaMemo + "&reajusteMesesMemo=" + reajusteMesesMemo +
                                "&para=" + paraMemo + "&firmasFijasMemo=" + firmasFijasMemo + "&texto=" + textoMemo + "&pie=" + pieMemo +
                                "&notaValue=" + part[1] + "&firmaCoordinador=" + firmaCoordinador+"&firmaNueva="+$("#tab-memorando").find("#coordinador").val()
                            }
                        }
                    });
                }
            }

            if (active == 2) {     /* fórmula polinómica */

                firmasIdFormu = [];
                firmasFijasFormu = [];

                var idCoordinador = $("#coordinador").val()
                var idFirmaCoor

                if (idCoordinador != null) {
                    idFirmaCoor = $("#coordinador").val()
                } else {
                    idFirmaCoor = ''
                }

                firmaCoordinador = idFirmaCoor

                $("#bodyFirmas_polinomica").children("tr").each(function (i) {
                    firmasIdFormu[i] = $(this).data("id")
                });

                $("#firmasFijasPoli").children("tr").each(function (i) {
                    firmasFijasFormu[i] = $(this).data("id")
                });

//            //console.log("3:" + firmasIdFormu)
                if (firmasIdFormu.length == 0) {
                    firmasIdFormu = "";
                }
                if (firmasFijasFormu.length == 0) {
                    firmasFijasFormu = "";
                }
                %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraFormu',id: obra?.id)}?firmasIdFormu=" + firmasIdFormu + "&totalPresupuesto=" + totalPres + "&firmasFijasFormu=" + firmasFijasFormu + "&notaFormula=" + $("#notaFormula").val()--}%

                $.ajax({
                    type    : "POST",
                    url     : "${createLink(controller: 'nota', action: 'saveNotaFormu')}",
                    data    : {
                        selFormu    : $("#selFormu").val(),
                        obra        : ${obra?.id},
                        descripcion : $("#descripcionFormu").val(),
                        texto       : $("#notaFormula").val(),
                        obraTipo    : "${obra?.claseObra?.tipo}"
                    },
                    success : function (msg) {
                        var part = msg.split('_');
//                            //console.log(msg)
                        if (part[0] == 'ok') {
                            location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraFormu',id: obra?.id)}?firmasIdFormu=" + firmasIdFormu + "&totalPresupuesto=" + totalPres + "&firmasFijasFormu=" + firmasFijasFormu
                            + "&notaFormula=" + $("#notaFormula").val() + "&notaValue=" + part[1] + "&firmaElaboro=" + ${obra?.responsableObra?.id} +"&firmaCoordinador=" + firmaCoordinador
                        }
                    }
                });
            }

            //memoAdmi

            if (active == 3) {   /* administracion directa */

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
                var financiero = $("#pcntFinanciero").val()

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
                "&texto=" + texto + "&para=" + para + "&de=" + de + "&fecha=" + fecha + "&asunto=" + asunto + "&financiero=" + financiero
            }
        }
        return false;
    });

    $("#btnImprimirDscr").click(function () {
        if (!$(this).hasClass("disabled")) {
            reajusteMemo = $("#reajusteMemo").val()
            var active = $("#tabs").tabs("option", "active");
            if (active == 0) {

                var idCoordinador = $("#coordinador").val()
                var idFirmaCoor
                if (idCoordinador != null) {
                    idFirmaCoor = $("#coordinador").val()
                } else {
                    idFirmaCoor = ''
                }

                firmasId = '';
                firmasFijas = '';
                firmaCoordinador = idFirmaCoor
                firmaElaboro = ${obra?.responsableObra?.id}

                        $("#firmasFijasPresu").children("tr").each(function (i) {
                            if ($(this).data("id")) {
                                if (firmasFijas != '') {

                                    firmasFijas += ','
                                } else {
                                    firmasFijas += '-1,'
                                }
                                firmasFijas += $(this).data("id")

                            }

                        });
//           //console.log("1:" + firmasFijas)

                notaValue = $("#piePaginaSel").val();

                if ($("#forzar").attr("checked") == "checked") {
                    forzarValue = 1;
                } else {
                    forzarValue = 2;
                }
                if (1 != 1) {

                    $("#tipoReporteDialog").dialog("open");

                } else {

                    proyeccion = $("#proyeccionReajuste").is(':checked');
                    reajusteIva = $("#reajusteIva").is(':checked');
                    reajusteMeses = $("#mesesReajuste").val();

                    var tipoReporte

                    if ($(".uno").is(':checked')) {
                        tipoReporte = 1

                    } else {
                        tipoReporte = 2
                    }

                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'nota', action: 'saveNota')}",
                        data    : {
                            piePaginaSel : $("#piePaginaSel").val(),
                            obra         : ${obra?.id},
                            descripcion  : $("#descripcion").val(),
                            texto        : $("#texto").val(),
                            adicional    : $("#adicional").val(),
                            obraTipo     : "${obra?.claseObra?.tipo}"
                        },
                        success : function (msg) {
                            var part = msg.split('_');
                            if (part[0] == 'ok') {
                                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraDscr',id: obra?.id)}?tipoReporte=" + tipoReporte +
                                "&forzarValue=" + forzarValue + "&notaValue=" + part[1] + "&firmasId=" + firmasId +
                                "&proyeccion=" + proyeccion + "&iva=" + reajusteIva + "&meses=" + reajusteMeses +
                                "&firmasFijas=" + firmasFijas + "&firmaCoordinador=" + firmaCoordinador +
                                "&firmaElaboro=" + firmaElaboro + "&encabezado=" + $(".encabezado:checked").val();

                            }
                        }
                    });
                }
            }
        }
        return false;
    });


    $("#btnImprimirVae").click(function () {
        if (!$(this).hasClass("disabled")) {
            reajusteMemo = $("#reajusteMemo").val()
            var active = $("#tabs").tabs("option", "active");
            if (active == 0) {

                var idCoordinador = $("#coordinador").val()
                var idFirmaCoor
                if (idCoordinador != null) {
                    idFirmaCoor = $("#coordinador").val()
                } else {
                    idFirmaCoor = ''
                }

                firmasId = '';
                firmasFijas = '';
                firmaCoordinador = idFirmaCoor
                firmaElaboro = ${obra?.responsableObra?.id}

                        $("#firmasFijasPresu").children("tr").each(function (i) {
                            if ($(this).data("id")) {
                                if (firmasFijas != '') {

                                    firmasFijas += ','
                                } else {
                                    firmasFijas += '-1,'
                                }
                                firmasFijas += $(this).data("id")

                            }

                        });
//           //console.log("1:" + firmasFijas)

                notaValue = $("#piePaginaSel").val();

                if ($("#forzar").attr("checked") == "checked") {
                    forzarValue = 1;
                } else {
                    forzarValue = 2;
                }
                if (1 != 1) {

                    $("#tipoReporteDialog").dialog("open");

                } else {

                    proyeccion = $("#proyeccionReajuste").is(':checked');
                    reajusteIva = $("#reajusteIva").is(':checked');
                    reajusteMeses = $("#mesesReajuste").val();

                    var tipoReporte

                    if ($(".uno").is(':checked')) {
                        tipoReporte = 1

                    } else {
                        tipoReporte = 2
                    }

                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'nota', action: 'saveNota')}",
                        data    : {
                            piePaginaSel : $("#piePaginaSel").val(),
                            obra         : ${obra?.id},
                            descripcion  : $("#descripcion").val(),
                            texto        : $("#texto").val(),
                            adicional    : $("#adicional").val(),
                            obraTipo     : "${obra?.claseObra?.tipo}"
                        },
                        success : function (msg) {
                            var part = msg.split('_');
                            if (part[0] == 'ok') {
                                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraVae',id: obra?.id)}?tipoReporte=" + tipoReporte +
                                "&forzarValue=" + forzarValue + "&notaValue=" + part[1] + "&firmasId=" + firmasId +
                                "&proyeccion=" + proyeccion + "&iva=" + reajusteIva + "&meses=" + reajusteMeses +
                                "&firmasFijas=" + firmasFijas + "&firmaCoordinador=" + firmaCoordinador +
                                "&firmaElaboro=" + firmaElaboro + "&encabezado=" + $(".encabezado:checked").val();

                            }
                        }
                    });
                }
            }
        }
        return false;
    });

    $("#btnDocExcel").click(function () {

        if (!$(this).hasClass("disabled")) {
            reajusteMemo = $("#reajusteMemo").val()
            var active = $("#tabs").tabs("option", "active");
            var notaPoli = $("#selFormu").val()

            if (active == 2) {
                firmasIdFormu = [];
                firmasFijasFormu = [];

                $("#bodyFirmas_polinomica").children("tr").each(function (i) {
                    firmasIdFormu[i] = $(this).data("id")
                });

                $("#firmasFijasPoli").children("tr").each(function (i) {
                    firmasFijasFormu[i] = $(this).data("id")
                });

                if (firmasIdFormu.length == 0) {
                    firmasIdFormu = "";
                }
                if (firmasFijasFormu.length == 0) {
                    firmasFijasFormu = "";
                }

                var idCoordinador = $("#coordinador").val()


                if (idCoordinador != null) {
                    idFirmaCoor = $("#coordinador").val()
                } else {
                    idFirmaCoor = ''
                }

                firmaCoordinador = idFirmaCoor

                $.ajax({
                    type    : "POST",
                    url     : "${createLink(controller: 'nota', action: 'saveNotaFormu')}",
                    data    : {
                        selFormu    : $("#selFormu").val(),
                        obra        : ${obra?.id},
                        descripcion : $("#descripcionFormu").val(),
                        texto       : $("#notaFormula").val(),
                        obraTipo    : "${obra?.claseObra?.tipo}"
                    },
                    success : function (msg) {
                        var part = msg.split('_');
                        if (part[0] == 'ok') {
                            location.href = "${g.createLink(controller: 'reportes5' ,action: 'reporteFormulaExcel',id: obra?.id)}?firmasIdFormu=" + firmasIdFormu + "&totalPresupuesto=" + totalPres + "&firmasFijasFormu="
                            + firmasFijasFormu + "&notaPoli=" + part[1] +  "&notaFormula=" + $("#notaFormula").val() +"&firmaCoordinador=" + firmaCoordinador
                        }
                    }
                });

                %{--location.href = "${g.createLink(controller: 'reportes5' ,action: 'reporteFormulaExcel',id: obra?.id)}?firmasIdFormu=" + firmasIdFormu + "&totalPresupuesto=" + totalPres + "&firmasFijasFormu=" + firmasFijasFormu + "&notaPoli=" + notaPoli--}%

            }

        }

    });

    $("#btnExcel").click(function () {
        $("#cambioMonedaExcel").dialog("open")
    });

    //    $("#btnAceptarMemo").click(function () {
    //
    //        $("#frm-memo").submit();
    //    });

    $("#btnEditarFor").click(function () {

        $("#notaFormula").attr("disabled", false);

    });

    //    $("#btnAceptarFor").click(function () {
    //
    //        $("#frm-formula").submit();
    //    });

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

        loadNota();
    });

    $("#selMemo").change(function () {
        loadNotaMemo();
    });

    $("#selFormu").change(function () {
        loadNotaFormu();
    });

    $(".btnQuitar").click(function () {
        var strid = $(this).attr("id");
        var parts = strid.split("_");
        var tipo = parts[1];

    });

    $("#btnAdicionarMemo").click(function () {

        var nombreFirmas = $("#setFirmasMemo").val()

//        //////console.log(nombreFirmas)

        var tbody = $("#bodyFirmasMemo")

        var tr = $("<tr>")
        var tdNombre = $("<td>")
        var tdPuesto = $("<td>")

        var tdNumero = $("<td>")

        tdNombre.html(nombreFirmas)

        tr.append(tdNumero).append(tdNombre).append(tdPuesto).data({nombre : nombreFirmas})
        tbody.append(tr)

    });

    $("#btnAdicionarFor").click(function () {

        var nombreFirmas = $("#setFirmasFor").val()

//        //////console.log(nombreFirmas)

        var tbody = $("#bodyFirmasFor")

        var tr = $("<tr>")
        var tdNombre = $("<td>")
        var tdPuesto = $("<td>")

        var tdNumero = $("<td>")

        tdNombre.html(nombreFirmas)

        tr.append(tdNumero).append(tdNombre).append(tdPuesto).data({nombre : nombreFirmas})
        tbody.append(tr)

    });

    $("#frm-nota").validate();

    $("#btnAceptar").click(function () {

//        $("#frm-nota").submit();
        $.ajax({
            type    : "POST",
            url     : "${createLink(controller: 'nota', action: 'save')}",
            data    : {
                piePaginaSel : $("#piePaginaSel").val(),
                obra         : ${obra?.id},
                descripcion  : $("#descripcion").val(),
                texto        : $("#texto").val(),
                adicional    : $("#adicional").val(),
                obraTipo     : "${obra?.claseObra?.tipo}"
            },
            success : function (msg) {
//                var part = msg.split('_')
                var part = msg
                if (part == '1') {
//                    $("#divOk").show(msg);

//                    location.reload(true)

                    alert("Nota actualizada correctamente")

                } else if (part == '2') {

                    alert("Nota creada correctamente")

                    $.ajax({type : "POST", url : "${g.createLink(controller: 'documentosObra',action:'cargarPieSel')}",
                        data     : "id=" + $(this).attr("sub"),
                        success  : function (msg) {
                            $("#div_sel").html(msg)
                        }
                    });
                }
            }
        });
    });

    $("#btnAceptarMemo").click(function () {
        $.ajax({
            type    : 'POST',
            url     : '${createLink(controller: 'nota', action: 'saveNotaMemo')}',
            data    : {
                obra        : ${obra?.id},
                texto       : $("#memo1").val(),
                pie         : $("#memo2").val(),
                obraTipo    : "${obra?.claseObra?.tipo}",
                selMemo     : $("#selMemo").val(),
                descripcion : $("#descripcionMemo").val()
            },
            success : function (msg) {
                var part = msg.split("_")
                if (part[0] != 'ok') {
                    alert("Error al crear la nota")
                } else if (part[0] == 'ok') {
                    alert("Nota guardada correctamente")
                    $.ajax({
                        type    : 'POST',
                        url     : "${createLink(controller: 'documentosObra', action: 'cargarSelMemo')}",
                        data    : "id=" + $(this).attr("sub"),
                        success : function (msg) {
                            $("#divSelMemo").html(msg);
                        }

                    })
                }

            }
        });
    });

    $("#btnAceptarFormu").click(function () {
        $.ajax({
            type    : 'POST',
            url     : '${createLink(controller: 'nota', action: 'saveNotaFormu')}',
            data    : {
                obra        : ${obra?.id},
                texto       : $("#notaFormula").val(),
                obraTipo    : "${obra?.claseObra?.tipo}",
                selFormu    : $("#selFormu").val(),
                descripcion : $("#descripcionFormu").val()
            },
            success : function (msg) {
                var part = msg.split("_")
                if (part[0] != 'ok') {
                    alert("Error al crear la nota")
                } else if (part[0] == 'ok') {
                    alert("Nota guardada correctamente");
                    $.ajax({
                        type    : 'POST',
                        url     : "${createLink(controller: 'documentosObra', action: 'cargarSelFormu')}",
                        data    : "id=" + $(this).attr("sub"),
                        success : function (msg) {
                            $("#divSelFormu").html(msg);
                        }
                    })
                }
            }
        });
    });

    $("#btnNuevo").click(function () {
        $("#piePaginaSel").val('-1')
        $("#notaAdicional").attr("checked", true)
        $("#descripcion").val("");
        $("#texto").val("");
        $("#adicional").val("");
    });

    $("#btnNuevoMemo").click(function () {
        $("#memo1").val("");
        $("#memo2").val("");
        $("#descripcionMemo").val("");
        $("#selMemo").val('-1')
    });

    $("#btnNuevoFormu").click(function () {
        $("#notaFormula").val("");
        $("#descripcionFormu").val("");
        $("#selFormu").val('-1');
    });

    $("#btnCancelar").click(function () {
        loadNota();
    });

    function desbloquear() {
        $("#piePaginaSel").attr("disabled", false);
        $("#descripcion").attr("disabled", false);
        $("#texto").attr("disabled", false);
//        $("#adicional").attr("disabled",false);
        $("#notaAdicional").attr("disabled", false)
    }

    $("#borrarFirmaPresuDialog").dialog({
        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 180,
        position  : 'center',
        title     : 'Eliminar firma',
        buttons   : {
            "Aceptar"  : function () {

                $("#borrarFirmaPresu").parents("tr").remove();
//                //console.log($("#borrarFirmaPresu").children("tr"))
                $("#borrarFirmaPresuDialog").dialog("close");
                return false;
            },
            "Cancelar" : function () {
                $("#borrarFirmaPresuDialog").dialog("close");
            }
        }
    });

    $("#tipoReporteDialog").dialog({
        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 180,
        position  : 'center',
        title     : 'Seleccione un Tipo de Reporte',
        buttons   : {
            "Aceptar" : function () {

                $("#tipoReporteDialog").dialog("close");

            }
        }

    });

    $("#cambioMonedaExcel").dialog({

        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 250,
        position  : 'center',
        title     : 'Tasa de cambio',
        buttons   : {
            "Aceptar"    : function () {
                tasaCambio = $("#cambioMoneda").val()
                if (tasaCambio == "") {
                    $("#tasaCeroDialog").dialog("open");
                } else {
                    var url = "${g.createLink(controller: 'reportes',action: 'documentosObraTasaExcel',id: obra?.id)}?tasa=" + tasaCambio
                    location.href = url
                }
                $("#cambioMonedaExcel").dialog("close");
            },
            "Sin cambio" : function () {
                location.href = "${g.createLink(controller: 'reportes',action: 'documentosObraExcel',id: obra?.id)}"
                $("#cambioMonedaExcel").dialog("close");
            },
            "Cancelar"   : function () {
                $("#cambioMonedaExcel").dialog("close");
            }
        }
    });

    $("#reajustePresupuestoDialog").dialog({

        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 230,
        position  : 'center',
        title     : 'Reajuste del Presupuesto',
        buttons   : {
            "Aceptar"  : function () {

                proyeccion = $("#proyeccionReajuste").is(':checked');
                reajusteIva = $("#reajusteIva").is(':checked');
                reajusteMeses = $("#mesesReajuste").val();

                if (proyeccion == true && reajusteMeses == "") {
                    $("#mesesCeroDialog").dialog("open")
                }
                else {
                    var tipoReporte = tipoClick;
                    location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObra',id: obra?.id)}?tipoReporte=" + tipoReporte + "&forzarValue=" + forzarValue + "&notaValue=" + notaValue
                    + "&firmasId=" + firmasId + "&proyeccion=" + proyeccion + "&iva=" + reajusteIva + "&meses=" + reajusteMeses + "&firmasFijas=" + firmasFijas + "&firmaCoordinador=" + firmaCoordinador
                    $("#reajustePresupuestoDialog").dialog("close");
                }
            },
            "Cancelar" : function () {
                $("#reajustePresupuestoDialog").dialog("close");
            }
        }
    });

    $("#reajusteMemoDialog").dialog({

        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 230,
        position  : 'center',
        title     : 'Reajuste Memorando',
        buttons   : {
            "Aceptar"  : function () {

                proyeccionMemo = $("#proyeccionReajusteMemo").is(':checked');
                reajusteIvaMemo = $("#reajusteIvaMemo").is(':checked');
                reajusteMesesMemo = $("#mesesReajusteMemo").val();
                paraMemo1 = $("#paraMemo").val()

                var idCoordinador = $("#coordinador").val()
                var idFirmaCoor

                if (idCoordinador != null) {
                    idFirmaCoor = $("#coordinador").val()
                } else {
                    idFirmaCoor = ''
                }

                firmaCoordinador = idFirmaCoor

                if (proyeccionMemo == true && reajusteMesesMemo == "") {
                    $("#mesesCeroDialog").dialog("open")
                } else {

                    %{--var tipoReporte = tipoClickMemo;--}%

                    %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo--}%
                    %{--+ "&totalPresupuesto=" + totalPres + "&proyeccionMemo=" + proyeccionMemo + "&reajusteIvaMemo=" + reajusteIvaMemo + "&reajusteMesesMemo=" + reajusteMesesMemo  + "&para=" + paraMemo1 + "&firmasFijasMemo=" + firmasFijasMemo + "&texto=" + $("#memo1").val() + "&pie=" + $("#memo2").val()--}%

                    var tipoReporte = tipoClickMemo;
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(controller: 'nota', action: 'saveNotaMemo')}",
                        data    : {
                            piePaginaSel : $("#selMemo").val(),
                            obra         : ${obra?.id},
                            descripcion  : $("#descripcionMemo").val(),
                            texto        : $("#memo1").val(),
                            pie          : $("#memo2").val(),
                            obraTipo     : "${obra?.claseObra?.tipo}"
                        },
                        success : function (msg) {
                            var part = msg.split('_');
//                            //console.log(msg)
                            if (part[0] == 'ok') {
//                                //console.log("LINK 3 : ", $("#tab-memorando").find("#coordinador"), );
                                location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo
                                + "&totalPresupuesto=" + totalPres + "&proyeccionMemo=" + proyeccionMemo +
                                "&reajusteIvaMemo=" + reajusteIvaMemo + "&reajusteMesesMemo=" + reajusteMesesMemo + "&para=" + paraMemo1 +
                                "&firmasFijasMemo=" + firmasFijasMemo + "&texto=" + $("#memo1").val() + "&pie=" + $("#memo2").val() + "&notaValue=" + part[1] +
                                "&firmaCoordinador=" + firmaCoordinador+"&firmaNueva="+$("#tab-memorando").find("#coordinador").val()

                            }
                        }
                    });
                    $("#reajusteMemoDialog").dialog("close");
                }
            },
            "Cancelar" : function () {
                $("#reajusteMemoDialog").dialog("close");
            }
        }

    });

    $("#maxFirmasDialog").dialog({
        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 200,
        position  : 'center',
        title     : 'Máximo Número de Firmas',
        buttons   : {
            "Aceptar" : function () {
                $("#maxFirmasDialog").dialog("close");
            }
        }
    });

    $("#mesesCeroDialog").dialog({
        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 200,
        position  : 'center',
        title     : 'No existe un valor en el campo Meses',
        buttons   : {
            "Aceptar" : function () {
                $("#mesesCeroDialog").dialog("close");
            }
        }
    });

    $("#tasaCeroDialog").dialog({
        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 200,
        position  : 'center',
        title     : 'No existe una tasa de cambio!',
        buttons   : {
            "Aceptar" : function () {
                $("#tasaCeroDialog").dialog("close");
            }
        }
    });

    $("#btnEliminar").click(function () {
        var idNota = $("#piePaginaSel").val()
        if (idNota == "-1") {
            alert("No se puede eliminar!")
        } else {
            $.ajax({
                type    : "POST",
                url     : "${createLink(controller:'nota' ,action: 'delete')}",
                data    : {
                    id   : idNota,
                    obra : ${obra?.id}
                },
                success : function (msg) {
                    if (msg == "ok") {
                        alert("Nota Eliminada!");
                        $.ajax({type : "POST", url : "${g.createLink(controller: 'documentosObra',action:'cargarPieSel')}",
                            data     : "id=" + $(this).attr("sub"),
                            success  : function (msg) {
                                $("#div_sel").html(msg)
                            }
                        });
                        loadNota();
                    } else {
//                    $("#spanOk").html("Ha ocurrido un error al eliminar");
//                    $("#divOk").show();
                        location.reload(true);
                    }
                }
            })
        }
    });

    $("#btnEliminarMemo").click(function () {

        var idNota = $("#selMemo").val()
        if (idNota == "-1") {
            alert("No se puede eliminar!")
        } else {
            $.ajax({
                type    : "POST",
                url     : "${createLink(controller:'nota' ,action: 'delete')}",
                data    : {
                    id   : idNota,
                    obra : ${obra?.id}
                },
                success : function (msg) {
                    if (msg == "ok") {
//
                        alert("Nota eliminada!")
                        $.ajax({
                            type    : 'POST',
                            url     : "${createLink(controller: 'documentosObra', action: 'cargarSelMemo')}",
                            data    : "id=" + $(this).attr("sub"),
                            success : function (msg) {
                                $("#divSelMemo").html(msg);
                            }

                        })
                        loadNotaMemo();
                    } else {
                        location.reload(true);
                    }
                }
            })
        }

    });

    $("#btnEliminarFormu").click(function () {

        var idNota = $("#selFormu").val()
        if (idNota == "-1") {
            alert("No se puede eliminar!")
        } else {
            $.ajax({
                type    : "POST",
                url     : "${createLink(controller:'nota' ,action: 'delete')}",
                data    : {
                    id   : idNota,
                    obra : ${obra?.id}
                },
                success : function (msg) {
                    if (msg == "ok") {
//
                        alert("Nota eliminada!")
                        $.ajax({
                            type    : 'POST',
                            url     : "${createLink(controller: 'documentosObra', action: 'cargarSelFormu')}",
                            data    : "id=" + $(this).attr("sub"),
                            success : function (msg) {
                                $("#divSelFormu").html(msg);
                            }

                        })
                        loadNotaFormu();
                    } else {
                        location.reload(true);
                    }
                }
            })
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

        $(".uno").prop("checked", true)

        $("#equiposMemo,#manoObraMemo,#materialesMemo").keydown(function (ev) {
            return validarNum(ev);
        }).keyup(function () {
            calculoPorcentaje();
            sumaTotal()
        });

        $("#costoPorcentaje").change(function () {
            calculoPorcentaje();
            sumaTotal();
        });

        $("#pcntFinanciero").change(function() {
            calculoPorcentaje();
            sumaTotal();
        });

        $(document).ready(function(){
            $("#costoPorcentaje").val(0);
            $("#pcntFinanciero").val(0);
            calculoPorcentaje();
            sumaTotal();
        });

        function calculoPorcentaje() {
            var porcentaje = 0
            var financiero = 0
            porcentaje = ((parseFloat($("#tMaterial").val()) + parseFloat($("#tMano").val()) + parseFloat($("#tEquipo").val())) * ($("#costoPorcentaje").val())) / 100
            financiero = (parseFloat($("#tMaterial").val()) * ($("#pcntFinanciero").val())) / 100

            $("#costoMemo").val(number_format(porcentaje, 2, ".", ""));
            $("#costoFinanciero").val(number_format(financiero, 2, ".", ""));
        }

        function sumaTotal() {
            var total = 0.0

            total = parseFloat($("#tMaterial").val()) + parseFloat($("#tMano").val()) +
            parseFloat($("#tEquipo").val()) + parseFloat($("#costoMemo").val()) +
            parseFloat($("#costoFinanciero").val())

            $("#totalMemoPresu").val(number_format(total, 2, ".", ""))
        }

    });
</script>

</body>
</html>