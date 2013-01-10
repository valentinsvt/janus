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

        <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
        <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">

        <style type="text/css">
        .lbl {
            font-weight : bold;
        }

        .inputError {
            border : solid 1px #995157 !important;
        }

        .selected, .selected td {
            background : #B5CBD1 !important;
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
                        <div class="span3" id="divPadre">
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

            function updateOrden() {
                var c = 1;
                $tbody.children("tr").each(function () {
                    var $tr = $(this);
                    $tr.data("ord", c);
                    $tr.find(".orden").text(c);
                    c++;
                });
            }

            function addRow(data) {
                var $tr = $("<tr>").data(data);
                var $tdOrd;
                if (!data.pdr) {
                    $tdOrd = $("<td class='orden'>").html(data.ord);
                } else {
                    $tdOrd = $("<td class='orden'>");
                }
                var $tdPar = $("<td class='parametro'>").html(data.par);
                var $tdPnt = $("<td class='puntaje'>").html(data.pnt);
                var $tdMin = $("<td class='minimo'>").html(data.min);
                var $tdAcc = $("<td class='btn-group'>");

                var $btnUp = $("<a href='#' class='btn btn-small btn-info' title='Mover hacia arriba'><i class='icon-arrow-up'></i></a>");
                var $btnDn = $("<a href='#' class='btn btn-small btn-info' title='Mover hacia abajo'><i class='icon-arrow-down'></i></a>");
                var $btnEd = $("<a href='#' class='btn btn-small btn-warning' title='Editar'><i class='icon-pencil'></i></a>");
                var $btnDl = $("<a href='#' class='btn btn-small btn-danger' title='Eliminar'><i class='icon-trash'></i></a>");
                $tdAcc.append($btnUp).append($btnDn).append($btnEd).append($btnDl);

                $btnUp.click(function () {
                    var ord = $(this).parents("tr").data("ord");
                    if (ord > 1) {
                        $tr.prev().before($tr);
                        updateOrden();
                    }
                    return false;
                });
                $btnDn.click(function () {
                    var ord = $(this).parents("tr").data("ord");
                    if (ord < $tbody.children("tr").size()) {
                        $tr.next().after($tr);
                        updateOrden();
                    }
                    return false;
                });
                $btnDl.click(function () {
                    var $btn = $(this);
                    $.box({
                        imageClass : "box_info",
                        text       : "Está seguro de querer eliminar este parámetro?",
                        title      : "Confirmación",
                        iconClose  : false,
                        dialog     : {
                            resizable     : false,
                            draggable     : false,
                            closeOnEscape : false,
                            buttons       : {
                                "Aceptar"  : function () {
                                    $btn.parents("tr").remove();
                                    updateOrden();
                                },
                                "Cancelar" : function () {
                                }
                            }
                        }
                    });
                });

                $tr.click(function () {
                    $(this).toggleClass("selected");
                    $(".selected").not($(this)).removeClass("selected");
                    if ($(this).hasClass("selected")) {
                        $("#divPadre").text($(this).find(".parametro").text()).data("id", $(this).data("id"));
                    } else {
                        $("#divPadre").text("").data("id", "");
                    }
                });

                $tr.append($tdOrd).append($tdPar).append($tdPnt).append($tdMin).append($tdAcc);
                if (!data.pdr) {
                    $tbody.append($tr);
                } else {
                    $tbody.children("tr").each(function () {
                        if ($(this).data("id") == data.pdr) {
                            $(this).after($tr);
                        }
                    });
                }

                reset();
            }

            $(function () {

                addRow({
                    id  : "ni_1",
                    par : "Par 1",
                    pnt : 1,
                    min : 0,
                    ord : 1
                });
                addRow({
                    id  : "ni_2",
                    par : "Par 2",
                    pnt : 2,
                    min : 0,
                    ord : 2
                });
                addRow({
                    id  : "ni_3",
                    par : "Par 3",
                    pnt : 3,
                    min : 0,
                    ord : 3
                });

                $('[rel=tooltip]').tooltip();

                $("#puntaje, #minimo").keydown(function (ev) {
                    if (ev.keyCode == 190 || ev.keyCode == 110) {
                        var val = $(this).val();
                        if (val.length == 0) {
                            $(this).val("0");
                        }
                        return val.indexOf(".") == -1;
                    } else {
                        return validarNum(ev);
                    }
                });
                $("#puntaje, #parametro").keyup(function () {
                    if ($.trim($(this).val()) != "" && $(this).hasClass("inputError")) {
                        $(this).removeClass("inputError");
                        $("#err_" + $(this).attr("id")).remove();
                        if ($("#msgAlert").find("li").size() == 0) {
                            $("#alert").hide();
                        }
                    }
                });

                $("#btnAdd").click(function () {
                    var c = 0;
                    var data = {
                        id  : "ni_" + $tbody.children("tr").size() + 1,
                        par : $.trim($par.val()),
                        pnt : $.trim($pnt.val()),
                        min : $.trim($min.val()),
                        ord : $tbody.children("tr").size() + 1,
                        pdr : $("#divPadre").data("id")
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
                        addRow(data);
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
