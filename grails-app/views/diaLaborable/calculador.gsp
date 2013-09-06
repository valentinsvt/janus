<%--
  Created by IntelliJ IDEA.
  User: luz
  Date: 9/5/13
  Time: 3:57 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title>Calculador de días laborables</title>
    </head>

    <body>

        <fieldset>
            <legend>Días laborables entre 2 fechas</legend>

            <div class="row">
                <div class="span1">Fecha 1</div>

                <div class="span2"><elm:datepicker name="fecha1" class="input-small"/></div>

                <div class="span1">Fecha 2</div>

                <div class="span2"><elm:datepicker name="fecha2" class="input-small"/></div>

                <div class="span1">
                    <a href="#" id="btnCalcEntre" class="btn btn-primary">Calcular</a>
                </div>
            </div>

            <div class="alert alert-success hide" id="respuestaEntre">Hay</div>
        </fieldset>

        <fieldset>
            <legend>Fecha n días laborables después</legend>

            <div class="row">
                <div class="span1">Fecha</div>

                <div class="span2"><elm:datepicker name="fecha" class="input-small"/></div>

                <div class="span1">Días</div>

                <div class="span2"><g:field type="number" class="input-mini" name="dias"/></div>

                <div class="span1">
                    <a href="#" id="btnCalcDias" class="btn btn-primary">Calcular</a>
                </div>
            </div>

            <div class="alert alert-success hide" id="respuestaDias">Hay</div>
        </fieldset>

        <script type="text/javascript">
            $(function () {
                $("#btnCalcEntre").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'calcEntre')}",
                        data    : {
                            fecha1 : $("#fecha1").val(),
                            fecha2 : $("#fecha2").val()
                        },
                        success : function (msg) {
                            var obj = $.parseJSON(msg);
                            if (obj[0]) {
                                var html = "<div>Hay " + obj[1] + " días laborables</div>";
                                html += obj[2];
                                $("#respuestaEntre").removeClass("alert-error").addClass("alert-success").html(html).show();
                            } else {
                                $("#respuestaEntre").removeClass("alert-success").addClass("alert-error").html(obj[1]).show();
                            }
                        }
                    });
                    return false;
                });
                $("#btnCalcDias").click(function () {
                    $.ajax({
                        type    : "POST",
                        url     : "${createLink(action: 'calcDias')}",
                        data    : {
                            fecha : $("#fecha").val(),
                            dias  : $("#dias").val()
                        },
                        success : function (msg) {
                            var obj = $.parseJSON(msg);
                            if (obj[0]) {
                                var html = "<div>La fecha es " + obj[2] + "</div>";
                                html += obj[3];
                                $("#respuestaDias").removeClass("alert-error").addClass("alert-success").html(html).show();
                            } else {
                                $("#respuestaDias").removeClass("alert-success").addClass("alert-error").html(obj[1]).show();
                            }
                        }
                    });
                    return false;
                });
            });
        </script>

    </body>
</html>