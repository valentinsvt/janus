<%@ page contentType="text/html;charset=UTF-8" %>
<html xmlns="http://www.w3.org/1999/html">
<head>
    <title>${empr.empresa}</title>
    <meta name="layout" content="main"/>
    <style type="text/css">
    @page {
        size: 8.5in 11in;  /* width height */
        margin: 0.25in;
    }

    .item {
        width: 260px;
        height: 220px;
        float: left;
        margin: 4px;
        font-family: 'open sans condensed';
        border: none;

    }

    .imagen {
        width: 167px;
        height: 100px;
        margin: auto;
        margin-top: 10px;
    }

    .texto {
        width: 90%;
        height: 50px;
        padding-top: 0px;
        /*border: solid 1px black;*/
        margin: auto;
        margin: 8px;
        /*font-family: fantasy; */
        font-size: 16px;

        /*
                font-weight: bolder;
        */
        font-style: normal;
        /*text-align: justify;*/
    }

    .fuera {
        margin-left: 15px;
        margin-top: 20px;
        /*background-color: #317fbf; */
        background-color: rgba(200, 200, 200, 0.9);
        border: none;

    }

    .desactivado {
        color: #bbc;
    }

    .titl {
        font-family: 'open sans condensed';
        font-weight: bold;
        text-shadow: -2px 2px 1px rgba(0, 0, 0, 0.25);
    %{--<g:if test="${janus.Parametros.findByEmpresaLike('CNSL-GADPP')}">--}%
    <g:if test="${empr.empresa == message(code: 'ambiente2')}">
        color: #1a7031;
    </g:if>
    <g:else>
        color: #0070B0;
    </g:else>

        margin-top: 20px;
    }

    .bordes {
    %{--<g:if test="${janus.Parametros.findByEmpresaLike('CNSL-GADPP')}">--}%
    <g:if test="${empr.empresa == message(code: 'ambiente2')}">
        background: #1a7031;
    </g:if>
    <g:else>
        background: #2080b0;
    </g:else>

    }

    </style>
</head>

<body>
<div class="dialog">
    <div style="text-align: center;"><h1 class="titl" style="font-size: 26px;">${empr.nombre}</h1></div>

    <div class="body ui-corner-all bordes" style="width: 850px;position: relative;margin: auto;margin-top: 0px;height: 510px;">

        <g:if test="${prms.contains('rubroPrincipal')}">
            <a href="${createLink(controller: 'rubro', action: 'rubroPrincipal')}" title="Análisis de Precios Unitarios">
        </g:if>
        <div class="ui-corner-all  item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'apu1.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Precios y análisis de precios unitarios</b>: registro y mantenimiento de
                ítems y rubros. Análisis de precios, rendimientos y listas de precios...</div>
            </div>
        </div>
        <g:if test="${prms.contains('rubroPrincipal')}">
            </a>
        </g:if>

        <g:if test="${prms.contains('registroObra')}">
            <a href= "${createLink(controller:'obra', action: 'registroObra')}" title="Registro de Obras">
        </g:if>
        <div class="ui-corner-all item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'obra100.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Obras</b>: registro de Obras, georeferenciación, los volúmenes de obra,
                variables de transporte y costos indirectos ...</div>
            </div>
        </div>
        <g:if test="${prms.contains('registroObra')}">
            </a>
        </g:if>

        <g:if test="${prms.contains('registrarPac')}">
            <a href= "${createLink(controller:'pac', action: 'registrarPac')}" title="Plan Anual de Compras Públicas">
        </g:if>
        <div class="ui-corner-all item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'compras.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Compras Públicas</b>: plan anual de contrataciones, gestión de pliegos y
                control y seguimiento del PAC de obras ...</div>
            </div>
        </div>
        <g:if test="${prms.contains('registrarPac')}">
            </a>
        </g:if>

        <g:if test="${prms.contains('verContrato')}">
            <a href= "${createLink(controller:'contrato', action: 'verContrato')}" title="Contratos y Ejecución de Obras">
        </g:if>
        <div class="ui-corner-all  item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'fiscalizar.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Ejecución</b>: seguimiento a la ejecución de las obras: incio de obra,
                planillas, reajuste de precios, cronograma ...</div>
            </div>
        </div>
        <g:if test="${prms.contains('verContrato')}">
            </a>
        </g:if>

        <g:link controller="reportes" action="index" title="Reportes del Sistema">
            <div class="ui-corner-all  item fuera">
                <div class="ui-corner-all ui-widget-content item">
                    <div class="imagen">
                        <img src="${resource(dir: 'images', file: 'reporte.png')}" width="100%" height="100%"/>
                    </div>

                    <div class="texto"><b>Reportes</b>: formatos pdf, hoja de cálculo, texto plano y html.
                    obras, concursos, contratos, contratistas, avance de obra...</div>
                </div>
            </div>
        </g:link>
    %{--<g:link  controller="documento" action="list" title="Documentos de los Proyectos">--}%
        <div class="ui-corner-all  item fuera">
            <div class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'manuales1.png')}" width="100%" height="100%"/>
                </div>

                <div class="texto"><b>Manuales del sistema:</b>
                    <g:link controller="manual" action="manualIngreso" target="_blank">Ingreso al Sistema</g:link>,
                    %{--<g:link controller="manual" action="manualIngresoObras" target="_blank">Análisis de Precios Unitarios</g:link>,--}%
                    %{--<g:link controller="manual" action="manualRegistroObra" target="_blank">Obras</g:link>,--}%
                    %{--<g:link controller="manual" action="manualComprasPublicas" target="_blank">Contratación</g:link>,--}%
                    %{--<g:link controller="manual" action="manualEjecucion" target="_blank">Fiscalización</g:link>,--}%
                    %{--<g:link controller="manual" action="manualFinanciero" target="_blank">Financiero</g:link>,--}%
                    %{--<g:link controller="manual" action="manualOferentes" target="_blank">Oferentes</g:link>,--}%
                    %{--<g:link controller="manual" action="manualReportes" target="_blank">Reportes</g:link>--}%
                    <a href="${resource(dir: 'manuales', file: 'Manual obras.html')}" title="Manual de Obras">Obras</a>,
                    <a href="${resource(dir: 'manuales', file: 'Manual APU.html')}" title="Manual de Obras">Análisis de Precios Unitarios</a>,
                    <a href="${resource(dir: 'manuales', file: 'Manual contrataciones.html')}" title="Manual de Contratación">Contratación</a>,
                    <a href="${resource(dir: 'manuales', file: 'Manual de ejecución.html')}" title="Manual de Fiscalización">Fiscalización</a>,
                    <a href="${resource(dir: 'manuales', file: 'Manual financiero.html')}" title="Manual de Financiero">Financiero</a>,
                    <a href="${resource(dir: 'manuales', file: 'Manual sep-oferentes.html')}" title="Manual de Oferentes">Oferentes</a>,
                    <a href="${resource(dir: 'manuales', file: 'Manual de reportes.html')}" title="Manual de Reportes">Reportes</a>,
                    <a href="${resource(dir: 'manuales', file: 'Manual administración directa.html')}" title="Manual de Administración Directa">Administración Directa</a>
                </div>
            </div>
        </div>

        %{--<div style="text-align: center ; color:#004060">Desarrollado por: TEDEIN S.A. Versión ${message(code: 'version', default: '1.1.0x')}</div>--}%
        <div style="text-align: center ; color:#ffffff">Desarrollado por: TEDEIN S.A. Versión ${message(code: 'version', default: '1.1.0x')}</div>

    </div>
    <script type="text/javascript">
        $(".fuera").hover(function () {
            var d = $(this).find(".imagen")
            d.width(d.width() + 10)
            d.height(d.height() + 10)
//        $.each($(this).children(),function(){
//            $(this).width( $(this).width()+10)
//        });
        }, function () {
            var d = $(this).find(".imagen")
            d.width(d.width() - 10)
            d.height(d.height() - 10)
        })
    </script>
</body>
</html>

%{--
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title></title>
    </head>
    <body>
    </body>
</html>--}%
