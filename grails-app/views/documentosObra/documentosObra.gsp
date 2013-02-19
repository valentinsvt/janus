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

        <title>Formato de Impresión</title>
    </head>

    <body>

        <div id="tabs" style="width: 800px; height: 900px">

            <ul>

                <li><a href="#tab-presupuesto">Presupuesto</a></li>
                <li><a href="#tab-memorando">Memorando</a></li>
                <li><a href="#tab-polinomica">F. Polinómica</a></li>
                <li><a href="#tab-textosFijos">Textos Fijos</a></li>

            </ul>

            <div id="tab-presupuesto" class="tab">

                <div class="tipoReporte">

                    <fieldset class="borde">

                        <legend>Tipo de Reporte</legend>

                        <div class="span6" style="margin-bottom: 10px">

                            <input type="radio" name="tipoPresupuesto" class="radioPresupuesto" value="1"/>  Base de Contrato

                            <input type="radio" name="tipoPresupuesto" class="radioPresupuesto" value="2" style="margin-left: 220px"/> Presupuesto Referencial

                        </div>
                    </fieldset>

                </div>


                <div style="margin-top: 10px; margin-bottom: 20px">

                    <g:checkBox name="forzar" checked="false"/> Forzar nueva Página para las Notas de Pie de Página

                    <div class="span3">
                        Tipo de Obra <g:textField name="tipoObra" value="${obra?.tipo}" style="width: 15px;height: 15px" disabled="true"/>
                    </div>

                </div>


                <div class="piePagina" style="margin-top: 10px; margin-bottom: 10px">

                    <g:form class="registroNota" name="frm-nota" controller="nota" action="save">
                        <fieldset class="borde">

                            <legend>Pie de Página</legend>

                            <div class="span6">

                                <g:select name="piePaginaSel" from="${nota?.list()}" value="${nota?.id}" optionValue="descripcion" optionKey="id" style="width: 350px"/>

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

                                <g:textArea name="texto" value="${nota?.texto}" rows="5" cols="5" style="height: 125px; width:685px ; resize: none" disabled="true"/>

                            </div>


                            <div class="span6" style="margin-top: 5px; margin-bottom: 10px">

                                <g:checkBox name="notaAdicional" checked="false" disabled="true"/> Nota al Pie Adicional (15 líneas aprox)

                            </div>


                            <div class="span6">

                                <g:textArea name="adicional" value="${nota?.adicional}" rows="5" cols="5" style="height: 125px; width:685px ; resize: none" disabled="true"/>

                            </div>

                            <g:hiddenField name="obraTipo" value="${obra?.claseObra?.tipo}"/>

                        </fieldset>

                    </g:form>

                </div>

                <div class="setFirmas">

                    <fieldset class="borde">

                        <legend>Set de Firmas</legend>


                        <div class="span6">

                            <elm:select name="setFirmas" id="cmb_presupuesto" class="selFirmas" from="${firmas}" optionClass="${{ it?.cargo }}"
                                        optionKey="id" optionValue="${{ it?.nombre + ' ' + it?.apellido }}" style="width: 350px"/>


                            <div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">
                                <button class="btn btnAdicionar" id="presupuesto">Adicionar</button>

                            </div>

                        </div>

                        <div class="span6" style="width: 700px">

                            <table class="table table-bordered table-striped table-hover table-condensed " id="tablaFirmas">

                                <thead>
                                    <tr>
                                        <th style="width: 50px">N°</th>
                                        <th style="width: 350px">Nombre</th>
                                        <th style="width: 250px">Puesto</th>
                                        <th style="width: 20px"></th>

                                    </tr>

                                </thead>

                                <tbody id="bodyFirmas_presupuesto">

                                    %{--<tr>--}%
                                    %{--<th style="width: 50px">1</th>--}%
                                    %{--<th style="width: 350px">${personasFirmas?.nombre + " " + personasFirmas?.apellido}</th>--}%
                                    %{--<th style="width: 250px">${personasFirmas?.cargo}</th>--}%
                                    %{--<th style="width: 20px"> </th>--}%

                                    %{--</tr>--}%


                                    %{--<tr>--}%
                                    %{--<th style="width: 50px">2</th>--}%
                                    %{--<th style="width: 350px">${personasFirmas2?.nombre + " " + personasFirmas2?.apellido}</th>--}%
                                    %{--<th style="width: 250px">${personasFirmas2?.cargo}</th>--}%
                                    %{--<th style="width: 20px"> </th>--}%

                                    %{--</tr>--}%

                                    %{--<tr id="bodyFirmas_tr">--}%


                                    %{--</tr>--}%

                                </tbody>

                            </table>

                        </div>

                    </fieldset>

                </div>

            </div>

            <div id="tab-memorando" class="tab">

                <div class="tipoReporteMemo">

                    <fieldset class="borde">

                        <legend>Tipo de Reporte</legend>

                        <div class="span6" style="margin-bottom: 10px">

                            <input type="radio" name="tipoPresupuestoMemo" class="radioPresupuestoMemo" value="1" checked="true"/>  Base de Contrato

                            <input type="radio" name="tipoPresupuestoMemo" class="radioPresupuestoMemo" value="2" style="margin-left: 220px"/> Presupuesto Referencial

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


                            <g:if test="${obra?.tipo == 'C'}">

                                <div class="span3"><g:textField name="deMemo" style="width: 470px" value="${"Dpto. Infraestructura Comunitaria"}" disabled="true"/></div>

                            </g:if>

                            <g:if test="${obra?.tipo == 'V'}">

                                <div class="span3"><g:textField name="deMemo" style="width: 470px" value="${"Dpto de Estudios Viales"}" disabled="true"/></div>

                            </g:if>

                        </div>

                        <div class="span6">
                            <div class="span1">PARA:</div>

                            <div class="span3"><g:textField name="paraMemo" value="${obra?.departamento?.descripcion}" style="width: 470px" disabled="true"/></div>
                        </div>

                        <div class="span7">
                            <div class="span1">Valor de la Base:</div>

                            <div class="span2">
                                <g:textField name="baseMemo" style="width: 100px" disabled="true" value="${formatNumber(number: totalPresupuesto, format: '####.##', minFractionDigits: 2, maxFractionDigits: 2, locale: 'ec')}"/>
                            </div>

                            <div class="span1" style="margin-left: -30px">Valor de Reajuste:</div>

                            <div class="span2"><g:textField name="reajusteMemo" id="reajusteMemo" style="width: 100px; margin-left: -20px" value="" disabled="true"/></div>

                            <div class="span2" style="margin-left: -45px"><g:textField name="porcentajeMemo" id="porcentajeMemo" style="width: 35px; margin-right: 10px" disabled="false"
                                                                                       maxlength="3"/>

                                <button class="btn" id="btnCalBase" style="width: 35px; margin-top: -9px; margin-left: -14px"><i class="icon-table"></i>
                                </button>
                            </div>

                        </div>

                    </fieldset>

                </div>

                <div class="texto">

                    <fieldset class="borde">
                        <legend>Texto</legend>

                        <g:form class="memoGrabar" name="frm-memo" controller="auxiliar" action="save">

                            <g:hiddenField name="id" value="${"1"}"/>

                            <g:hiddenField name="obra" value="${obra?.id}"/>

                            <div class="span6">

                                <div class="span1">Texto</div>

                                <div class="span3"><g:textArea name="memo1" value="${auxiliarFijo?.memo1}" rows="4" cols="4" style="width: 600px; height: 55px; margin-left: -50px;resize: none;" disabled="true"/></div>

                            </div>


                            <div class="span6">
                                <div class="span1">Pie</div>

                                <div class="span3"><g:textArea name="memo2" value="${auxiliarFijo?.memo2}" rows="4" cols="4" style="width: 600px; height: 55px; margin-left: -50px; resize: none;" disabled="true"/></div>

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


                <div class="setFirmas">

                    <fieldset class="borde">

                        <legend>Set de Firmas</legend>

                        <div class="span6">

                            <elm:select name="setFirmas" id="cmb_memo" class="selFirmas" from="${firmas}"
                                        optionKey="id" optionValue="${{ it?.nombre + ' ' + it?.apellido }}" optionClass="${{ it?.cargo }}" style="width: 350px"/>


                            <div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">
                                <button class="btn btnAdicionar" id="memo">Adicionar</button>

                            </div>

                        </div>

                        <div class="span6" style="width: 700px">

                            <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmasMemo">

                                <thead>
                                    <tr>
                                        <th style="width: 50px">N°</th>
                                        <th style="width: 350px">Nombre</th>
                                        <th style="width: 250px">Puesto</th>
                                        <th style="width: 20px"></th>

                                    </tr>

                                </thead>

                                <tbody id="bodyFirmas_memo">

                                </tbody>

                            </table>

                        </div>

                    </fieldset>

                </div>

            </div>

            <div id="tab-polinomica" class="tab">

                <div class="textoFormula">

                    <fieldset class="borde">

                        <div class="span6" style="margin-top: 10px">
                            <div class="span2">Fórmula Polinómica N°</div>

                            <div class="span3"><g:textField name="numeroFor" value="${obra?.formulaPolinomica}" disabled="true"/></div>
                        </div>

                        <div class="span6">
                            <div class="span2">Fecha de Lista de Precios:</div>

                            <div class="span3"><g:textField name="fechaFor" value="${formatDate(date: obra?.fechaPreciosRubros, format: "yyyy-MM-dd")}" style="width: 100px" disabled="true"/></div>
                        </div>

                        <div class="span6">
                            <div class="span2">Monto del Contrato:</div>

                            <div class="span3">
                                <g:textField name="montoFor" value="${formatNumber(number: totalPresupuesto, format: '####.##', maxFractionDigits: 2, minFractionDigits: 2, locale: 'ec')}" disabled="true"/>
                            </div>
                        </div>

                    </fieldset>

                </div>


                <div class="texto">

                    <fieldset class="borde">
                        <legend>Nota</legend>

                        <g:form class="memoGrabar" name="frm-formula" controller="auxiliar" action="save">

                            <g:hiddenField name="id" value="${"1"}"/>

                            <g:hiddenField name="obra" value="${obra?.id}"/>

                            <div class="span6">
                                <div class="span3"><g:textArea name="notaFormula" rows="4" value="${auxiliarFijo?.notaFormula}" cols="4"
                                                               style="width: 690px; margin-left: -30px; height: 70px; resize: none" disabled="true"/></div>

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


                <div class="setFirmas">

                    <fieldset class="borde">

                        <legend>Set de Firmas</legend>


                        <div class="span6">

                            <elm:select name="setFirmas" id="cmb_polinomica" class="selFirmas" from="${firmas}"
                                        optionKey="id"
                                        optionValue="${{ it?.nombre + " " + it?.apellido }}" optionClass="${{ it?.cargo }}" style="width: 350px"/>

                            <div class="btn-group" style="margin-left: 400px; margin-top: -60px; margin-bottom: 10px">
                                <button class="btn btnAdicionar" id="polinomica">Adicionar</button>

                            </div>

                        </div>

                        <div class="span6" style="width: 700px">

                            <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmasFor">

                                <thead>
                                    <tr>
                                        <th style="width: 50px">N°</th>
                                        <th style="width: 350px">Nombre</th>
                                        <th style="width: 250px">Puesto</th>
                                        <th style="width: 20px"></th>

                                    </tr>

                                </thead>

                                <tbody id="bodyFirmas_polinomica">

                                </tbody>

                            </table>

                        </div>

                    </fieldset>

                </div>

            </div>

            <div id="tab-textosFijos" class="tab">

                <div class="cabecera">

                    <fieldset class="borde">

                        <legend>Cabecera</legend>


                        <g:form class="memoGrabar" name="frm-textoFijo" controller="auxiliar" action="save">

                            <g:hiddenField name="id" value="${"1"}"/>

                            <g:hiddenField name="obra" value="${obra?.id}"/>


                            <div class="span6">
                                <div class="span1">Título</div>

                                <div class="span3"><g:textField name="titulo" value="${auxiliarFijo?.titulo}" style="width: 560px" disabled="true"/></div>

                            </div>


                            <div class="span6">
                                <div class="span1">General</div>
                            </div>

                            <div class="span6">
                                <div class="span3"><g:textArea name="general" value="${auxiliarFijo?.general}" rows="4" cols="4" style="width: 665px; height: 130px; resize: none;" disabled="true"/></div>

                            </div>


                            <div class="span6">
                                <div class="span2">Base de Contratos</div>
                            </div>

                            <div class="span6">
                                <div class="span3"><g:textArea name="baseCont" value="${auxiliarFijo?.baseCont}" rows="4" cols="4" style="width: 665px; height: 35px; resize: none;" disabled="true"/></div>

                            </div>


                            <div class="span6">
                                <div class="span2">Presupuesto Referencial</div>
                            </div>

                            <div class="span6">
                                <div class="span3"><g:textArea name="presupuestoRef" value="${auxiliarFijo?.presupuestoRef}" rows="4" cols="4" style="width: 665px; height: 35px; resize: none;" disabled="true"/></div>

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

                        <g:form class="memoGrabar" name="frm-textoFijoRet" controller="auxiliar" action="save">

                            <g:hiddenField name="id" value="${"1"}"/>

                            <g:hiddenField name="obra" value="${obra?.id}"/>

                            <div class="span6">
                                <div class="span1">Retenciones</div>

                                <div class="span3"><g:textField name="retencion" value="${auxiliarFijo?.retencion}" style="width: 560px" disabled="true"/></div>

                            </div>


                            <div class="span6">
                                <div class="span3">NOTA (15 líneas aproximadamente)</div>
                            </div>

                            <div class="span6">
                                <div class="span3"><g:textArea name="notaAuxiliar" value="${auxiliarFijo?.notaAuxiliar}" rows="4" cols="4" style="width: 665px; height: 130px; resize: none;" disabled="true"/></div>

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


            <div class="btn-group" style="margin-bottom: 10px; margin-top: 20px; margin-left: 210px">
                <button class="btn" id="btnSalir"><i class="icon-arrow-left"></i> Regresar</button>
                <button class="btn" id="btnImprimir"><i class="icon-print"></i> Imprimir</button>
                <button class="btn" id="btnExcel"><i class="icon-table"></i> Presupuesto a Excel</button>

            </div>


            <div id="tipoReporteDialog">

                <fieldset>
                    <div class="span3">

                        Debe elegir un Tipo de Reporte antes de imprimir el documento!!

                    </div>
                </fieldset>
            </div>


            <div id="maxFirmasDialog">

                <fieldset>
                    <div class="span3">

                        A ingresado el número máximo de firmas para este documento!!

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

            var firmasIdMemo = []

            var firmasIdFormu = []

            var totalPres = $("#baseMemo").val()

            var reajusteMemo = 0;

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

            $("#tabs").tabs({
                heightStyle : "fill"
            });

            $("#btnSalir").click(function () {

                location.href = "${g.createLink(controller: 'obra', action: 'registroObra')}" + "?obra=" + "${obra?.id}";

            });

            $(".btnAdicionar").click(function () {

                var tipo = $(this).attr("id");
                var tbody = $("#bodyFirmas_" + tipo);
                var id = $("#cmb_" + tipo).val();

                var maxFirmas = (tbody.children("tr").length) + 1;

//        console.log(maxFirmas)

                if (maxFirmas > 3) {

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
                        var tdNumero = $("<td>");
                        var tdNombre = $("<td>");
                        var tdPuesto = $("<td>");
                        var tdDel = $("<td>");
                        var btnDel = $("<a href='#' class='btn btn-danger'><i class='icon-trash icon-large'></i></a>");

                        tdNumero.html(num);
                        tdNombre.html(nombre);
                        tdPuesto.html(puesto);
                        tdDel.append(btnDel);

                        tr.append(tdNumero).append(tdNombre).append(tdPuesto).append(tdDel).data({nombre : nombre, puesto : puesto, id : id});
                        tbody.append(tr);

                        btnDel.click(function () {
                            tr.remove();
                        });

                    }

                }

            });

            $("#btnEditarMemo").click(function () {

                $("#memo1").attr("disabled", false);
                $("#memo2").attr("disabled", false)

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
//        console.log(porcentajeCal)
//        console.log(totalPres)
//        console.log(base)
//        console.log("entro cal!")

            });

            var active2 = $("#tabs").tabs("option", "event")

            //    console.log(active2)

            $("#btnImprimir").click(function () {

                reajusteMemo = $("#reajusteMemo").val()

//        console.log("Memo:" + reajusteMemo)

                var active = $("#tabs").tabs("option", "active");

//        console.log(active)

                if (active == 0) {

                    firmasId = [];

                    $("#bodyFirmas_presupuesto").children("tr").each(function (i) {
//      $("#bodyFirmas_tr").each(function(i){

                        firmasId[i] = $(this).data("id")

//          console.log(firmasId[i])

//          console.log(  $(this).data("id") )
//        console.log(  $(this).data("nombre") )
//        console.log(  $(this).data("puesto") )

                    })

                    notaValue = $("#piePaginaSel").attr("value");

                    if ($("#forzar").attr("checked") == "checked") {

                        forzarValue = 1;

                    } else {

                        forzarValue = 2;

                    }

                    if (tipoClick == null) {

                        $("#tipoReporteDialog").dialog("open");

                    } else {

                        var tipoReporte = tipoClick;

                        location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObra',id: obra?.id)}?tipoReporte=" + tipoReporte + "&forzarValue=" + forzarValue + "&notaValue=" + notaValue
                                                + "&firmasId=" + firmasId

                    }

                }

                if (active == 1) {

                    firmasIdMemo = [];

                    $("#bodyFirmas_memo").children("tr").each(function (i) {
                        firmasIdMemo[i] = $(this).data("id")

                    })

                    if (firmasIdMemo.length == 0) {
                        firmasIdMemo = "";
                    }

                    var tipoReporte = tipoClickMemo;

                    location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo
                                            + "&totalPresupuesto=" + totalPres
                                            + "&reajusteMemo=" + reajusteMemo

                }

                if (active == 2) {

                    firmasIdFormu = [];

                    $("#bodyFirmas_polinomica").children("tr").each(function (i) {
                        firmasIdFormu[i] = $(this).data("id")

                    })

                    if (firmasIdFormu.length == 0) {
                        firmasIdFormu = "";
                    }

                    location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraFormu',id: obra?.id)}?firmasIdFormu=" + firmasIdFormu + "&totalPresupuesto=" + totalPres

                }

            });

            $("#btnExcel").click(function () {

                location.href = "${g.createLink(controller: 'reportes',action: 'documentosObraExcel',id: obra?.id)}"

            });

            $("#btnAceptarMemo").click(function () {

                $("#frm-memo").submit();

                success_func(location.href = "${g.createLink(controller: 'documentosObra',action: 'documentosObra',id: obra?.id)}")

            });

            $("#btnEditarFor").click(function () {

                $("#notaFormula").attr("disabled", false);

            });

            $("#btnAceptarFor").click(function () {

                $("#frm-formula").submit();
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


//        console.log("entro")

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

        console.log(nombreFirmas)

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

//        console.log(nombreFirmas)

                var tbody = $("#bodyFirmasFor")

                var tr = $("<tr>")
                var tdNombre = $("<td>")
                var tdPuesto = $("<td>")

                var tdNumero = $("<td>")

                tdNombre.html(nombreFirmas)

                tr.append(tdNumero).append(tdNombre).append(tdPuesto).data({nombre : nombreFirmas})
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


//        console.log("click")

                if ($("#notaAdicional").attr("checked") == "checked") {

//            console.log("checked")
                    $("#adicional").attr("disabled", false)

                }

                else {

//            console.log(" no checked")
                    $("#adicional").attr("disabled", true)
//            $("#adicional").val("");
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

            $("#maxFirmasDialog").dialog({

                autoOpen  : false,
                resizable : false,
                modal     : true,
                draggable : false,
                width     : 350,
                height    : 180,
                position  : 'center',
                title     : 'Máximo Número de Firmas',
                buttons   : {
                    "Aceptar" : function () {

                        $("#maxFirmasDialog").dialog("close");

                    }
                }

            });






        </script>

    </body>
</html>