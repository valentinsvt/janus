<html>
<head>
    <meta name="layout" content="main">
    <title>Reportes</title>

    <style type="text/css">
    .lista, .desc {
        float: left;
        min-height: 150px;
        margin-left: 25px;
    }

    .lista {
        width: 615px;
    }

    .desc {
        width: 265px;
    }

    .link {
        font-weight: bold;
        text-decoration: none;
    }

    .noBullet {
        list-style: none;
        margin: 1em;
        padding: 0;
    }

    .noBullet li {
        margin-bottom: 10px;
    }

    .linkHover {
        text-decoration: overline underline;
    }
    .leyenda {
        float: left;
        width: 260px;
        height: 360px;
        margin-top: -20px;
        margin-left: 30px;
        display: none;
        padding:15px;
    }

    </style>

</head>

<body>

<div class="contenedor">
    <h2>Reportes del Sistema</h2>
    <g:if test="${flash.message}">
         <div class="message ${flash.clase}" role="status"><span class="ss_sprite ${flash.ico}">&nbsp;</span>${flash.message}
         </div>
    </g:if>



    <div class="ui-widget-content ui-corner-all lista">
      <ul class="noBullet">
        <li text="obraprsp" class="item" texto="obraprsp">
            <g:link controller="reportes" action="planDeCuentas" file="Plan_de_Cuentas.pdf" class="link" dialog="dlgContabilidad">
                Obras presupuestadas:
            </g:link>
            Listado de obras que se hallan elaboradas los presupuestos y se hallan lsitas para entrar en el proceso de
            contratación.
        </li>

        <li text="obracntr" class="item" texto="obracntr">
            <g:link controller="reportes" action="gestorContable" file="Gestor_Contable.pdf" class="link" dialog="dlgContabilidad">
                Obras contratadas
            </g:link>
            Sirve para determinar las cuentas que son afectadas en un proceso contable
        </li>

        <li text="obrargst" class="item" texto="obrargst">
            <g:link controller="reportes" action="presupuesto" file="Presupuesto.pdf" class="link" dialog="dlgContabilidad">
                Obras registradas
            </g:link>
            Reporta el presupuesto anual.
        </li>

        <li text="obracntn" class="item" texto="obracntn">
            <g:link controller="reportes" action="comprobante" file="Comprobante.pdf" class="link" dialog="dlgComprobante">
                Obras contratadas por cantón
            </g:link>
            Permite imprimir un comprobante
        </li>

        <li text="cncr" class="item" texto="cncr">
            <g:link controller="reportes" action="balanceComprobacion" file="Balance_Comprobacion.pdf" class="link" dialog="dlgContabilidadPeriodo">
                Concursos
            </g:link>
            Muestra el balance de comprobaci&oacute;n en detalle a todos los niveles
        </li>

        <li text="cntr" class="item" texto="cntr">
            <g:link controller="reportes" action="situacionFinanciera" file="Situacion_Financiera.pdf" class="link" dialog="dlgContabilidadPeriodo">
                Contratos
            </g:link>
            Conocido anteriormente como Balance General
        </li>

        <li text="prve" class="item" texto="prve">
            <g:link controller="reportes" action="resultadoIntegral" file="Estado_Resultado_Integral.pdf" class="link" dialog="dlgContabilidadPeriodo">
                Contratistas
            </g:link>
            Este reporte se conocía como Estado de Pérdidas y Ganancias
        </li>

        <li text="fldf" class="item" texto="obraprsp">
            <g:link controller="reportes" action="flujoEfectivo" file="Estado_Flujos_Efectivo.pdf" class="link" dialog="dlgContabilidadPeriodo">
                Aseguradoras
            </g:link>
            Estado de flujo defectivo en función del año anterior
        </li>

        <li text="patr" class="item" texto="obraprsp">
            <g:link controller="reportes" action="cambiosPatrimonio" file="Estado_Cambios_Patrimonio.pdf" class="link" dialog="dlgContabilidadPeriodo">
                Garantías por contrato y contratistas
            </g:link>
            Estado de cambio sen el patrimonio durante el ejercicio fiscal
        </li>
        <li text="vnts" class="item" texto="obraprsp">
            <g:link controller="reportes" action="cambiosPatrimonio" file="Estado_Cambios_Patrimonio.pdf" class="link" dialog="dlgVentas">
                Transferencias y/o cheques pagados
            </g:link>
            Ventas
        </li>
        <li text="vnts" class="item" texto="obraprsp">
            <g:link controller="reportes" action="cambiosPatrimonio" file="Estado_Cambios_Patrimonio.pdf" class="link" dialog="dlgVentas">
                Avance de obras
            </g:link>
            Ventas
        </li>
        <li text="vnts" class="item" texto="obraprsp">
            <g:link controller="reportes" action="cambiosPatrimonio" file="Estado_Cambios_Patrimonio.pdf" class="link" dialog="dlgVentas">
                Obras finalizadas
            </g:link>
            Ventas
        </li>
      </ul>
   </div>

   <div id="tool" class="leyenda ui-widget-content ui-corner-all">
   </div>

   <div id="obraprsp" style="display:none">
    <h3>Obras Presupuestadas</h3><br>

    <p>Listado de obras que se hallan elaboradas los presupuestos y se hallan lsitas para entrar en el proceso de
    contratación.
    </p> De cada una de las obbras se han realizado ya el registro de volúmen de obra, la matriz de la fórmula
    polinómica, la fórmula polinómica, el cronograma y los documentos precontractuales necesarios
  </div>
   <div id="obracntr" style="display:none">
    <h3>Obras Contratadas</h3><br>

    <p>Listado de obras que se hallan elaboradas los presupuestos y se hallan lsitas para entrar en el proceso de
    contratación.
    </p> De cada una de las obbras se han realizado ya el registro de volúmen de obra, la matriz de la fórmula
    polinómica, la fórmula polinómica, el cronograma y los documentos precontractuales necesarios
  </div>

</div>

<div id="dlgContabilidad" class="ui-helper-hidden">
    Contratos:
    <g:select name="cont" from="${Contratos.findAll()}" optionKey="id" optionValue="Contrato"
              class="ui-widget-content ui-corner-all"/>
</div>

<div id="dlgComprobante" class="ui-helper-hidden">
    Comprobante: <g:textField type="text" class="ui-widget-content ui-corner-all" name="comprobante"/> <a href="#"
                                                                                                          id="btnComprobantes">Buscar</a>
</div>

<div id="dlgVentas">
    Desde: <elm:datepicker class="field ui-corner-all" title="Desde" name="fechaPago" format="yyyy-MM-dd"
                           style="width: 80px" id="desde"/>
    Hasta: <elm:datepicker class="field ui-corner-all" title="Hasta" name="fechaPago" format="yyyy-MM-dd"
                           style="width: 80px" id="hasta"/>
</div>

<div id="dlgContabilidadPeriodo" class="ui-helper-hidden">
    <div>
        Contabilidad:
        %{--
                        <g:select name="contP" id="contP" from="${cratos.Contabilidad.findAllByInstitucion(session.empresa, [sort: 'fechaInicio'])}" optionKey="id" optionValue="descripcion"
                                  class="ui-widget-content ui-corner-all"/>
        --}%
    </div>

    <div id="divPeriodo">
        Periodo:
    </div>
</div>

<script type="text/javascript">

    $(document).ready(function () {
        $('.item').hover(function () {
            //$('.item').click(function(){
            //entrar
            $('#tool').html($("#" + $(this).attr('texto')).html());
            $('#tool').show();
        }, function () {
            //sale
            $('#tool').hide();
        });

        $('#info').tabs({
            //event: 'mouseover', fx: {
            cookie: { expires: 30 },
            event: 'click', fx: {
                opacity: 'toggle',
                duration: 'fast'
            },
            spinner: 'Cargando...',
            cache: true
        });
    });

    var actionUrl = "";

    function updatePeriodo() {
        var cont = $("#contP").val();

        $.ajax({
            type: "POST",
            url: "${createLink(action:'updatePeriodo')}",
            data: {
                cont: cont
            },
            success: function (msg) {
                $("#divPeriodo").html(msg);
            }
        });

//                console.log(cont);
    }

    $(function () {

        $(".link").hover(
                function () {
                    $(this).addClass("linkHover");
                    $(".notice").hide();
                    var id = $(this).parent().attr("text");
                    $("#" + id).show();
                },
                function () {
                    $(this).removeClass("linkHover");
                    $(".notice").hide();
                }).click(function () {
                    var url = $(this).attr("href");
                    var file = $(this).attr("file");

                    var dialog = trim($(this).attr("dialog"));

                    var cont = trim($(this).text());

                    $("#" + dialog).dialog("option", "title", cont);
                    $("#" + dialog).dialog("open");

                    actionUrl = "${createLink(controller:'pdf',action:'pdfLink')}?filename=" + file + "&url=" + url;

//                            console.log(actionUrl);

                    %{--<g:link action="pdfLink" controller="pdf" params="[url: g.createLink(controller: 'reportes', action: 'planDeCuentas'), filename: 'Plan_de_Cuentas.pdf']">--}%
                    %{--plan de cuentas--}%
                    %{--</g:link>--}%

//                            console.log(url, file);

                    return false;
                });

        $("#contP").change(function () {
            updatePeriodo();
        });

        $("#dlgContabilidad").dialog({
            modal: true,
            resizable: false,
            autoOpen: false,
            buttons: {
                "Aceptar": function () {
                    var cont = $("#cont").val();
                    var url = actionUrl + "?cont=" + cont + "Wemp=${session.empresa?.id}";
//                            console.group("URL");
//                            console.log(actionUrl);
//                            console.log(url);
//                            console.groupEnd();

                    location.href = url;
                },
                "Cancelar": function () {
                    $("#dlgContabilidad").dialog("close");
                }
            }
        });
        $("#dlgVentas").dialog({
            modal: true,
            width: 400,
            height: 300,
            title: "Reporte de ventas",
            autoOpen: false,
            buttons: {
                "Ver": function () {
                    var desde = $("#desde").val()
                    var hasta = $("#hasta").val()
                    location.href = "${g.createLink(action: 'ventas')}?desde=" + desde + "&hasta=" + hasta;
                }
            }
        });

        $("#dlgContabilidadPeriodo").dialog({
            modal: true,
            resizable: false,
            autoOpen: false,
            width: 400,
            open: function () {
                updatePeriodo();
            },
            buttons: {
                "Aceptar": function () {
                    var cont = $("#contP").val();
                    var per = $("#periodo").val();
                    var url = actionUrl + "?cont=" + cont + "Wper=" + per + "Wemp=${session.empresa?.id}";
//                            console.group("URL");
//                            console.log(actionUrl);
//                            console.log(url);
//                            console.groupEnd();
                    location.href = url;
                },
                "Cancelar": function () {
                    $("#dlgContabilidadPeriodo").dialog("close");
                }
            }
        });


        $("#btnComprobantes").button({
            icons: {
                primary: "ui-icon-search"
            }
        });

        $("#dlgComprobante").dialog({
            resizable: false,
            autoOpen: false,
            modal: true,
            width: 400,
            buttons: {
                "Aceptar": function () {
                    var cont = $("#cont").val();
                    var per = $("#periodo").val();
                    var url = actionUrl + "?cont=" + cont + "Wper=" + per + "Wemp=${session.empresa?.id}";
//                            console.group("URL");
//                            console.log(actionUrl);
//                            console.log(url);
//                            console.groupEnd();
                    location.href = url;
                },
                "Cancelar": function () {
                    $("#dlgComprobante").dialog("close");
                }
            }
        });

    });
</script>

</body>
</html>