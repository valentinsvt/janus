<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Gráficos de avance</title>

        <script language="javascript" type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jqplot', file: 'jquery.jqplot.min.js')}"></script>
        <link rel="stylesheet" type="text/css" href="${resource(dir: 'js/jquery/plugins/jqplot', file: 'jquery.jqplot.min.css')}"/>

        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jqplot/plugins', file: 'jqplot.canvasTextRenderer.min.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jqplot/plugins', file: 'jqplot.canvasAxisLabelRenderer.min.js')}"></script>
        <script type="text/javascript" src="${resource(dir: 'js/jquery/plugins/jqplot/plugins', file: 'jqplot.highlighter.min.js')}"></script>

    </head>

    <body>
        <div class="btn-toolbar">
            <div class="btn-group">
                <g:link action="${!nuevo ? 'index' : 'nuevoCronograma'}" id="${contrato.id}" params="[subpre: params.subpre]" class="btn">
                    <i class="icon-caret-left"></i>
                    Cronograma
                </g:link>
            </div>
        </div>

        <div id="grafEco" class="graf" style="margin-top: 15px;"></div>

        <div id="grafFis" class="graf"></div>

        <script type="text/javascript">
            var plotEco = $.jqplot("grafEco", ${params.datae},
                    {
                        title       : "Avance económico de la obra ${obra.descripcion}",
                        axes        : {
                            xaxis : {
                                min           : 1,
                                ticks         : ${params.txe},
                                pad           : 5.5,
                                label         : 'Mes',
                                labelRenderer : $.jqplot.CanvasAxisLabelRenderer
                            },
                            yaxis : {
                                min           : 0,
                                max           : ${params.me},
                                ticks         : ${params.tye},
                                pad           : 5.5,
                                label         : 'Avance económico ($)',
                                labelRenderer : $.jqplot.CanvasAxisLabelRenderer
                            }
                        },
                        highlighter : {
                            show         : true,
//                            showMarker  : true,
                            showTooltip  : true,
                            tooltipAxes  : 'both',
                            formatString : '<table class="jqplot-highlighter"> <tr><td>mes:</td><td>%s</td></tr> <tr><td>valor:</td><td>%s</td></tr> </table>'
                        },
                        series      : [
                            {
                                color : "#${params.colore}"
                            }
                        ]
                    });
            var plotFis = $.jqplot("grafFis", ${params.dataf},
                    {
                        title       : "Avance físico de la obra ${obra.descripcion}",
                        axes        : {
                            xaxis : {
                                min           : 1,
                                ticks         : ${params.txf},
                                pad           : 5.5,
                                label         : 'Mes',
                                labelRenderer : $.jqplot.CanvasAxisLabelRenderer
                            },
                            yaxis : {
                                min           : 0,
                                max           : ${params.mf},
                                ticks         : ${params.tyf},
                                pad           : 5.5,
                                label         : 'Avance económico (%)',
                                labelRenderer : $.jqplot.CanvasAxisLabelRenderer
                            }
                        },
                        highlighter : {
                            show         : true,
//                            showMarker  : true,
                            showTooltip  : true,
                            tooltipAxes  : 'both',
                            formatString : '<table class="jqplot-highlighter"> <tr><td>mes:</td><td>%s</td></tr> <tr><td>valor:</td><td>%s</td></tr> </table>'
                        },
                        series      : [
                            {
                                color : "#${params.colorf}"
                            }
                        ]
                    });
        </script>
    </body>
</html>