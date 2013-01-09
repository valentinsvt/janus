<%@ page import="janus.pac.ParametroEvaluacion" %>
<!doctype html>
<html>
    <head>
        <meta name="layout" content="main">
        <title>
            Lista de Parametro Evaluacions
        </title>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>

        <style type="text/css">
        .lbl {
            font-weight : bold;
        }

        .inputError {
            border : solid 1px #995157 !important;
        }

        </style>

    </head>

    <body>

        <div class="tituloTree">
            Parámetros de evaluación de <span style="font-weight: bold; font-style: italic;">${concurso.objeto}</span>
        </div>

        <g:if test="${flash.message}">
            <div class="row">
                <div class="span12">
                    <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </div>
            </div>
        </g:if>

        <div class="row">
            <div class="span9 btn-group" role="navigation">
                <g:link controller="concurso" action="list" class="btn">
                    <i class="icon-caret-left"></i>
                    Regresar
                </g:link>
            </div>

            <div class="span3" id="busqueda-ParametroEvaluacion"></div>
        </div>

        <div id="list-ParametroEvaluacion" role="main" style="margin-top: 10px;">

            <g:form name="frmAdd">
                <div class="well">

                    <div class="alert alert-error hide" id="alert">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        <h5 id="ttlAlert">Se han encontrado los siguientes errores:</h5>
                        <span id="msgAlert"></span>
                    </div>

                    <div class="row">
                        <div class="span3 lbl">Padre</div>

                        <div class="span3 lbl">Parámetro</div>

                        <div class="span2 lbl">Puntaje</div>

                        <div class="span2 lbl">Mínimo</div>
                    </div>

                    <div class="row">
                        <div class="span3">
                            Ninguno
                        </div>

                        <div class="span3">
                            <g:textArea name="parametro" class="span3"/>
                        </div>

                        <div class="span2">
                            <g:textField name="puntaje" class="span2"/>
                            <span class="help-block">Puntaje máximo a obtener.</span>
                        </div>

                        <div class="span2">
                            <g:textField name="minimo" class="span2"/>
                            <span class="help-block">Puntaje inferior descalifica automáticamente la oferta.</span>
                        </div>

                        <div class="span1">
                            <a href="#" class="btn btn-success" id="btnAdd"><i class="icon-plus"></i></a>
                        </div>
                    </div>
                </div>
            </g:form>


            <table class="table table-bordered table-striped table-condensed table-hover" style="width: auto;">
                <thead>
                    <tr>
                        <th style="width: 40px;">#</th>
                        <th style="width: 530px;">Parámetro</th>
                        <th style="width: 60px;">Puntaje</th>
                        <th style="width: 60px;">Mínimo</th>
                        <th style="width:110px;">Acciones</th>
                    </tr>
                </thead>
                <tbody id="tbdPar">
                </tbody>
            </table>

        </div>


        <script type="text/javascript">
            var $par = $("#parametro"), $pnt = $("#puntaje"), $min = $("#minimo"), $tbody = $("#tbdPar");

            function reset() {
                $par.val("");
                $pnt.val("");
                $min.val("");
            }

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
                        ev.keyCode == 190 || ev.keyCode == 110 ||
                        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                        ev.keyCode == 37 || ev.keyCode == 39);
            }

            $(function () {
                $('[rel=tooltip]').tooltip();

                $("#puntaje, #minimo").bind({
                    keydown : function (ev) {
                        if (ev.keyCode == 190 || ev.keyCode == 110) {
                            var val = $(this).val();
                            if (val.length == 0) {
                                $(this).val("0");
                            }
                            return val.indexOf(".") == -1;
                        } else {
                            return validarNum(ev);
                        }
                    },
                    keyup    : function () {
                        if ($.trim($(this).val()) != "" && $(this).hasClass("inputError")) {
                            $(this).removeClass("inputError");
                            $("#err_" + $(this).attr("id")).remove();
                        }
                    }
                });

                $("#btnAdd").click(function () {
                    var c = 0;
                    var data = {
                        par : $.trim($par.val()),
                        pnt : $.trim($pnt.val()),
                        min : $.trim($min.val()),
                        ord : $tbody.children("tr").size() + 1
                    };
                    var msg = "";
                    if (data.par == "") {
                        c++;
                        msg += "<li id='err_parametro'>Ingrese el parámetro</li>";
                        $par.addClass("inputError");
                    }
                    if (data.pnt == "") {
                        c++;
                        msg += "<li id='err_puntaje'>Ingrese el puntaje</li>";
                        $pnt.addClass("inputError");
                    }
                    if (data.min == "") {
                        data.min = 0;
                        $min.val(0);
                    }

                    if (msg == "") {
                        console.log(data);
                    } else {
                        $("#ttlAlert").text("Se encontr" + (c == 1 ? "ó el siguiente error" : "aron los siguientes errores"));
                        $("#msgAlert").html("<ul>" + msg + "</ul>");
                        $("#alert").show();
                    }

                    return false;
                });

            });

        </script>

    </body>
</html>
