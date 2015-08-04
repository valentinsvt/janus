si<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 04/08/15
  Time: 08:18 AM
--%>

<%@ page import="janus.ejecucion.ReajustePlanilla; janus.ejecucion.Planilla" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Modificar reajuste de planilla</title>

        <script src="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.css')}" rel="stylesheet"/>
        <link href="${resource(dir: 'js/jquery/plugins/jgrowl', file: 'jquery.jgrowl.customThemes.css')}" rel="stylesheet"/>

        <style type="text/css">
        .num {
            text-align : right !important;
        }

        .changed {
            background : #b6cead !important;
        }

        .table th {
            font-size : 12pt;
        }

        .table td {
            vertical-align : middle;
        }

        .table, .table td, .table .num {
            font-size : 10pt !important;
        }

        .table .num {
            height        : 12px;
            margin-bottom : 0;
        }

        .readonly {
            background : #AAA !important;
            color      : #EFEFEF !important;
        }

        .readonly.changed {
            background : #71886f !important;
        }
        </style>
    </head>

    <body>

        <div class="well">
            <label class="control-label">Planilla:</label>
            <label>${planilla.numero} - ${planilla.tipoPlanilla.nombre} - ${g.formatNumber(number: planilla.valor, type: 'currency')}</label>

            <form class="form-inline">
                <label class="control-label" for="planilla">Planilla reajustada</label>
                <g:select name="planilla" from="${ReajustePlanilla.findAllByPlanilla(planilla)}" optionKey="id"
                          optionValue="${{
                              it.planillaReajustada.numero + ' - ' + it.planillaReajustada.tipoPlanilla.nombre + ' - ' + g.formatNumber(number: it.planillaReajustada.valor, type: 'currency')
                          }}" noSelection="['': 'Seleccione una planilla para empezar']" class="input-xlarge"/>

                <a href="#" class="btn btn-primary" id="btnCargar"><i class="icon-trello"></i> Reajustar</a>
            </form>
        </div>

        <div class="row">
            <div class="span12" id="divTabla">

            </div>
        </div>

        <script type="text/javascript">
            $.jGrowl.defaults.closerTemplate = '<div>[ cerrar todo ]</div>';

            function log(msg, error) {
                var sticky = false;
                var theme = "success";
                if (error) {
                    sticky = true;
                    theme = "error";
                }
                $.jGrowl(msg, {
                    speed      : 'slow',
                    sticky     : sticky,
                    theme      : theme,
                    themeState : ''
                });
            }

            function cargarTabla() {
                var $div = $("#divTabla");
                var val = $("#planilla").val();

                if (val == '') {
                    $div.html('');
                } else {
                    $div.html('<h3><i class="icon-spinner icon-spin icon-large"></i> Por favor espere</h3>');
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'tablaReajuste')}",
                        data    : {
                            id : val
                        },
                        success : function (msg) {
                            $div.html(msg);
                        }
                    });
                }
            }

            $(function () {
                $("#btnCargar").click(function () {
                    cargarTabla();
                    return false;
                });

            });
        </script>

    </body>
</html>