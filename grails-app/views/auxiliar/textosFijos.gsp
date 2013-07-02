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

<div id="tabs" style="width: 800px; height: 970px">

<ul>

    %{--<li><a href="#tab-presupuesto">Presupuesto</a></li>--}%
    <li><a href="#tab-memorando">Memorando</a></li>
    %{--<li><a href="#tab-polinomica">F. Polinómica</a></li>--}%
    <li><a href="#tab-textosFijos">Textos Fijos</a></li>

</ul>


<div id="tab-memorando" class="tab" style="">

    <div class="texto">

        <fieldset class="borde">
            <legend>Texto</legend>

            <g:form class="memoGrabar" name="frm-memo" controller="auxiliar" action="saveText">

                <g:hiddenField name="id" value="${"1"}"/>

                %{--<g:hiddenField name="obra" value="${obra?.id}"/>--}%

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

</div>

<div id="tab-textosFijos" class="tab" style="">

    <div class="cabecera">

        <fieldset class="borde">

            <legend>Cabecera</legend>


            <g:form class="memoGrabar" name="frm-textoFijo" controller="auxiliar" action="saveText">

                <g:hiddenField name="id" value="${"1"}"/>

                %{--<g:hiddenField name="obra" value="${obra?.id}"/>--}%


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

            <g:form class="memoGrabar" name="frm-textoFijoRet" controller="auxiliar" action="saveText">

                <g:hiddenField name="id" value="${"1"}"/>

                %{--<g:hiddenField name="obra" value="${obra?.id}"/>--}%

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
    <div class="btn-group" style="margin-bottom: 10px; margin-top: 10px; margin-left: 300px">
        <button class="btn" id="btnSalir"><i class="icon-arrow-left"></i> Regresar</button>
    </div>

</div>



<script type="text/javascript">


    var tipoClick;

//    var tipoClickMemo = $(".radioPresupuestoMemo").attr("value");

    var tg = 0;

    var forzarValue;

    var notaValue;

    var firmasId = [];

    var firmasIdMemo = []

    var firmasIdFormu = []

    var firmasFijas = []

    var firmasFijasMemo = []

    var firmasFijasFormu = []

//    var totalPres = $("#baseMemo").val()

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


    $("#tabs").tabs();

    $("#btnSalir").click(function () {

        location.href = "${g.createLink(controller: 'inicio', action: 'parametros')}"

    });


    $("#btnEditarMemo").click(function () {

        $("#memo1").attr("disabled", false);
        $("#memo2").attr("disabled", false)

    });

    var active2 = $("#tabs").tabs("option", "event")


    $("#btnAceptarMemo").click(function () {

        $("#frm-memo").submit();
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
//        loadNota();

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

//        loadNota();
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


//    $("#borrarFirmaPresuDialog").dialog({
//
//        autoOpen: false,
//        resizable: false,
//        modal: true,
//        draggable: false,
//        width: 350,
//        height: 180,
//        position: 'center',
//        title: 'Eliminar firma',
//        buttons: {
//            "Aceptar": function () {
//
//                $("#borrarFirmaPresu").parents("tr").remove();
//                console.log($("#borrarFirmaPresu").children("tr"))
//
//                $("#borrarFirmaPresuDialog").dialog("close");
//                return false;
//            },
//            "Cancelar" : function () {
//
//                $("#borrarFirmaPresuDialog").dialog("close");
//
//            }
//        }
//
//
//
//    }) ;


//
//    $("#tipoReporteDialog").dialog({
//
//        autoOpen: false,
//        resizable: false,
//        modal: true,
//        draggable: false,
//        width: 350,
//        height: 180,
//        position: 'center',
//        title: 'Seleccione un Tipo de Reporte',
//        buttons: {
//            "Aceptar": function () {
//
//                $("#tipoReporteDialog").dialog("close");
//
//            }
//        }
//
//    });


    %{--$("#cambioMonedaExcel").dialog({--}%

        %{--autoOpen: false,--}%
        %{--resizable: false,--}%
        %{--modal: true,--}%
        %{--draggable: false,--}%
        %{--width: 350,--}%
        %{--height: 250,--}%
        %{--position: 'center',--}%
        %{--title: 'Tasa de cambio',--}%
        %{--buttons: {--}%
            %{--"Aceptar": function () {--}%

                %{--tasaCambio = $("#cambioMoneda").val()--}%

                %{--if (tasaCambio == "") {--}%

                    %{--$("#tasaCeroDialog").dialog("open");--}%

                %{--} else {--}%

                     %{--var url = "${g.createLink(controller: 'reportes',action: 'documentosObraTasaExcel',id: obra?.id)}?tasa=" + tasaCambio--}%
%{--//                             ////console.log(url)--}%
                    %{--location.href = url--}%

                %{--}--}%

                %{--$("#cambioMonedaExcel").dialog("close");--}%


            %{--},--}%
            %{--"Sin cambio": function () {--}%

                %{--location.href = "${g.createLink(controller: 'reportes',action: 'documentosObraExcel',id: obra?.id)}"--}%

                %{--$("#cambioMonedaExcel").dialog("close");--}%


            %{--},--}%
            %{--"Cancelar": function () {--}%

                %{--$("#cambioMonedaExcel").dialog("close");--}%

            %{--}--}%


        %{--}--}%


    %{--});--}%

    %{--$("#reajustePresupuestoDialog").dialog({--}%

        %{--autoOpen: false,--}%
        %{--resizable: false,--}%
        %{--modal: true,--}%
        %{--draggable: false,--}%
        %{--width: 350,--}%
        %{--height: 230,--}%
        %{--position: 'center',--}%
        %{--title: 'Reajuste del Presupuesto',--}%
        %{--buttons: {--}%
            %{--"Aceptar": function () {--}%


                %{--proyeccion = $("#proyeccionReajuste").is(':checked');--}%
                %{--reajusteIva = $("#reajusteIva").is(':checked');--}%
                %{--reajusteMeses = $("#mesesReajuste").val();--}%

%{--//--}%
%{--//                        ////console.log(proyeccion)--}%
%{--//                        ////console.log(reajusteMeses)--}%

%{--//--}%
                %{--if (proyeccion == true && reajusteMeses == "") {--}%


%{--//                            ////console.log("entro!!")--}%

                    %{--$("#mesesCeroDialog").dialog("open")--}%


                %{--} else {--}%


                    %{--var tipoReporte = tipoClick;--}%

                    %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObra',id: obra?.id)}?tipoReporte=" + tipoReporte + "&forzarValue=" + forzarValue + "&notaValue=" + notaValue--}%
                            %{--+ "&firmasId=" + firmasId + "&proyeccion=" + proyeccion + "&iva=" + reajusteIva + "&meses=" + reajusteMeses + "&firmasFijas=" +firmasFijas--}%


                    %{--$("#reajustePresupuestoDialog").dialog("close");--}%

                %{--}--}%


            %{--},--}%
            %{--"Cancelar": function () {--}%


                %{--$("#reajustePresupuestoDialog").dialog("close");--}%

            %{--}--}%
        %{--}--}%

    %{--});--}%


    %{--$("#reajusteMemoDialog").dialog({--}%

        %{--autoOpen: false,--}%
        %{--resizable: false,--}%
        %{--modal: true,--}%
        %{--draggable: false,--}%
        %{--width: 350,--}%
        %{--height: 230,--}%
        %{--position: 'center',--}%
        %{--title: 'Reajuste Memorando',--}%
        %{--buttons: {--}%
            %{--"Aceptar": function () {--}%


                %{--proyeccionMemo = $("#proyeccionReajusteMemo").is(':checked');--}%
                %{--reajusteIvaMemo = $("#reajusteIvaMemo").is(':checked');--}%
                %{--reajusteMesesMemo = $("#mesesReajusteMemo").val();--}%
                %{--paraMemo1 = $("#paraMemo").val()--}%

                %{--if (proyeccionMemo == true && reajusteMesesMemo == "") {--}%
                    %{--$("#mesesCeroDialog").dialog("open")--}%


                %{--} else {--}%

                    %{--var tipoReporte = tipoClickMemo;--}%

                    %{--location.href = "${g.createLink(controller: 'reportes' ,action: 'reporteDocumentosObraMemo',id: obra?.id)}?tipoReporte=" + tipoReporte + "&firmasIdMemo=" + firmasIdMemo--}%
                            %{--+ "&totalPresupuesto=" + totalPres + "&proyeccionMemo=" + proyeccionMemo + "&reajusteIvaMemo=" + reajusteIvaMemo + "&reajusteMesesMemo=" + reajusteMesesMemo  + "&para=" + paraMemo1 + "&firmasFijasMemo=" + firmasFijasMemo--}%

                    %{--$("#reajusteMemoDialog").dialog("close");--}%

                %{--}--}%


            %{--},--}%
            %{--"Cancelar": function () {--}%


                %{--$("#reajusteMemoDialog").dialog("close");--}%

            %{--}--}%
        %{--}--}%

    %{--});--}%


//    $("#maxFirmasDialog").dialog({
//
//        autoOpen: false,
//        resizable: false,
//        modal: true,
//        draggable: false,
//        width: 350,
//        height: 200,
//        position: 'center',
//        title: 'Máximo Número de Firmas',
//        buttons: {
//            "Aceptar": function () {
//
//                $("#maxFirmasDialog").dialog("close");
//
//            }
//        }
//
//    });


//    $("#mesesCeroDialog").dialog({
//
//        autoOpen: false,
//        resizable: false,
//        modal: true,
//        draggable: false,
//        width: 350,
//        height: 200,
//        position: 'center',
//        title: 'No existe un valor en el campo Meses!',
//        buttons: {
//            "Aceptar": function () {
//
//                $("#mesesCeroDialog").dialog("close");
//
//            }
//        }
//
//    });

//
//    $("#tasaCeroDialog").dialog({
//
//        autoOpen: false,
//        resizable: false,
//        modal: true,
//        draggable: false,
//        width: 350,
//        height: 200,
//        position: 'center',
//        title: 'No existe una tasa de cambio!',
//        buttons: {
//            "Aceptar": function () {
//
//                $("#tasaCeroDialog").dialog("close");
//
//            }
//        }
//
//    });





</script>

</body>
</html>