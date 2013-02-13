<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Evaluación
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
        <style>
        input.numero {
            font-size  : 12px !important;
            height     : 18px !important;
            padding    : 2px !important;
            width      : 60px !important;
            text-align : right;
        }

        .numero {
            text-align : right !important;
        }

        .desc {
            font-size   : 11px !important;
            height      : 60px !important;
            padding     : 2px !important;
            width       : 400px !important;
            line-height : 12px;

        }
        </style>
    </head>

    <body>

        <div class="span12">
            <g:if test="${flash.message}">
                <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                    <a class="close" data-dismiss="alert" href="#">×</a>
                    ${flash.message}
                </div>
            </g:if>
        </div>

        <div class="tituloTree">Evaluación de la oferta del oferente: ${oferta.proveedor.nombre}</div>

        <div class="span12 btn-group" role="navigation">
            <g:link controller="concurso" action="list" class="btn">
                <i class="icon-caret-left"></i>
                Regresar
            </g:link>
            <a href="#" class="btn btn-ajax btn-new" id="guardar">
                <i class="icon-file"></i>
                Guardar
            </a>
        </div>


        <div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: -10px;">

            <div style="border-bottom: 1px solid black;padding-left: 50px;margin-top: 10px;position: relative;">
                <p class="css-vertical-text">Parámetros de evaluación</p>

                <div class="linea" style="height: 98%;"></div>

                <div style="width: 95%;margin-left: 0px">
                    ${html}
                </div>
            </div>
        </div>


        <div class="modal hide fade " id="modal-transporte" style=";overflow: hidden;">
            <div class="modal-header btn-primary">
                <button type="button" class="close" data-dismiss="modal">×</button>

                <h3 id="modal_trans_title">
                    Variables de transporte
                </h3>
            </div>

            <div class="modal-body" id="modal_trans_body">

            </div>

            <div class="modal-footer" id="modal_trans_footer">
                <a href="#" data-dismiss="modal" class="btn">OK</a>
            </div>
        </div>


        <script type="text/javascript">
            $(".i_t").not(".val").blur(function () {
                var t = $(this)
                var val = $(this).val()
                if (isNaN(val))
                    val = -1
                val = val * 1
                if (val > -1) {
                    var max = $(this).parent().parent().find(".max").html() * 1

                    if (val > max) {

                        $.box({
                            imageClass : "box_info",
                            text       : "Error: El valor asignado (" + val + ") supera al máximo (" + max + ")",
                            title      : "Alerta",
                            iconClose  : false,
                            dialog     : {
                                resizable : false,
                                draggable : false,
                                buttons   : {
                                    "Aceptar" : function () {
                                        t.focus()
                                    }
                                },
                                width     : 500
                            }
                        });

                    }

                } else {
                    $.box({
                        imageClass : "box_info",
                        text       : "Error: Ingrese un número positivo",
                        title      : "Alerta",
                        iconClose  : false,
                        dialog     : {
                            resizable : false,
                            draggable : false,
                            buttons   : {
                                "Aceptar" : function () {
                                    t.val("")
                                    t.focus()
                                }
                            },
                            width     : 500
                        }
                    });
                    $(this).focus()
                }
            });
            $("#guardar").click(function () {
                if (confirm("Esta seguro?")) {
                    var datos = ""
                    $(".i_t").not(".val").each(function () {
                        var t = $(this)
                        if (t.val() != "") {
                            datos += t.attr("iden") + "&" + t.val() + "&"
                            datos += $(this).parent().next().children().val() + "&"
                            datos += $(this).parent().next().next().children().val() + ";"
                        }
                    });
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action:'guardarParametros')}",
                        data    : {
                            concurso : "${concurso.id}",
                            oferta   : "${oferta.id}",
                            data     : datos
                        },
                        success : function (msg) {
                            $.box({
                                imageClass : "box_info",
                                text       : "Datos guardados",
                                title      : "Guardar",
                                iconClose  : false,
                                dialog     : {
                                    resizable : false,
                                    draggable : false,
                                    buttons   : {
                                        "Aceptar" : function () {

                                        }
                                    },
                                    width     : 500
                                }
                            });
                        }
                    });
                }
            })

        </script>

    </body>
</html>
