<%@ page import="janus.pac.CronogramaEjecucion; janus.pac.CronogramaContrato" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">


        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>

        <title>Cronograma ejecución</title>


        <style type="text/css">
        th {
            vertical-align : middle !important;
        }

        .item_row {
            background : #999999;
        }

        .item_prc {
            background : #C0C0C0;
        }

        .item_f {
            background : #C9C9C9;
        }

        td {
            vertical-align : middle !important;
        }

        .num {
            text-align : right !important;
            width      : 60px;
            /*background : #c71585 !important;*/
        }

        .spinner {
            width : 60px;
        }

        .radio {
            margin : 0 !important;
        }

        .sm {
            margin-bottom : 10px !important;
        }

        .totalRubro {
            width : 75px;
        }

        .item_row.rowSelected {
            background : #75B2DE !important;
        }

        .item_prc.rowSelected {
            background : #84BFEA !important;
        }

        .item_f.rowSelected {
            background : #94CDF7 !important;
        }

        .graf {
            width  : 870px;
            height : 410px;
            /*background : #e6e6fa;*/
        }

            /*.btn {*/
            /*z-index : 9999 !important;*/
            /*}*/

            /*.modal-backdrop {*/
            /*z-index : 9998 !important;*/
            /*}*/

            /*.modal {*/
            /*z-index : 9999 !important;*/
            /*}*/
        </style>

    </head>

    <body>
        <g:set var="meses" value="${obra.plazo}"/>

        <div class="tituloTree">
            Cronograma del contrato de la obra ${obra.descripcion} (${meses} mes${obra.plazoEjecucionMeses == 1 ? "" : "es"})
        </div>

        <div class="btn-toolbar">
            <div class="btn-group">
                <a href="${g.createLink(controller: 'obra', action: 'registroObra', params: [obra: obra?.id])}" class="btn btn-ajax btn-new" id="atras" title="Regresar a la obra">
                    <i class="icon-arrow-left"></i>
                    Regresar
                </a>
            </div>

            <g:if test="${meses > 0}">
                <div class="btn-group">
                    <a href="#" class="btn btn-info" id="btnSusp">
                        <i class="icon-resize-small"></i>
                        Suspensión
                    </a>
                    <a href="#" class="btn btn-info" id="btnAmpl">
                        <i class="icon-resize-full"></i>
                        Ampliación
                    </a>
                </div>

                <div class="btn-group">
                    <a href="#" class="btn" id="btnGrafico">
                        <i class="icon-bar-chart"></i>
                        Gráficos de avance
                    </a>
                %{--<a href="#" class="btn" id="btnGraficoEco">--}%
                %{--<i class="icon-bar-chart"></i>--}%
                %{--Gráfico de avance económico--}%
                %{--</a>--}%
                %{--<a href="#" class="btn" id="btnGraficoFis">--}%
                %{--<i class="icon-bar-chart"></i>--}%
                %{--Gráfico de avance físico--}%
                %{--</a>--}%
                    <g:link action="excel" class="btn" id="${obra.id}">
                        <i class="icon-table"></i>
                        Exportar a Excel
                    </g:link>
                </div>
            </g:if>
        </div>

        <div id="divTabla">

        </div>

        <div class="modal longModal tallModal fade hide " id="modal-graf">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modalTitle-graf"></h3>
            </div>

            <div class="modal-body" id="modalBody-graf">
                <div class="graf" id="graf"></div>
            </div>

            <div class="modal-footer" id="modalFooter-graf">
            </div>
        </div>


        <script type="text/javascript">

            function updateTabla() {
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action: 'tabla')}",
                    data    : {
                        id : ${obra.id}
                    },
                    success : function (msg) {
                        $("#divTabla").html(msg);
                    }
                });
            }

            function log(msg) {
                ////console.log(msg);
            }

            $(function () {
                updateTabla();
            });


        </script>

    </body>
</html>