<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 23/11/15
  Time: 03:16 PM
--%>


 <div id="tabs" style="width: 700px; height: 700px; text-align: center">

        <ul>
            <li><a href="#tab-formulaPolinomica">Fórmula Polinómica</a></li>
            <li><a href="#tab-cuadrillaTipo">Cuadrilla Tipo</a></li>

        </ul>

        <div id="tab-formulaPolinomica" class="tab">

            <div class="formula">

                <fieldset class="borde">
                    <legend>Fórmula Polinómica</legend>

                    <table class="table table-bordered table-striped table-hover table-condensed" id="tablaPoliContrato">
                        <thead>
                        <tr>
                            <th style="width: 20px; text-align: center">Coeficiente</th>
                            <th style="width: 70px">Nombre del Indice (INEC)</th>
                            <th style="width: 40px">Valor</th>
                        </tr>
                        </thead>
                        <tbody id="bodyPoliContrato">
                        <g:set var="tot" value="${0}"/>
                        <g:each in="${ps}" var="i">
                            <tr>
                                <td>${i?.numero}</td>
                                <td>${i?.indice?.descripcion}</td>
                                <td class="editable" data-tipo="p" data-id="${i.id}" id="${i.id}" data-original="${i.valor}" data-valor="${i.valor}" style="text-align: right; width: 40px">
                                    ${g.formatNumber(number: i?.valor, minFractionDigits: 3, maxFractionDigits: 3)}
                                </td>
                                <g:set var="tot" value="${tot + i.valor}"/>
                            </tr>
                        </g:each>
                        </tbody>
                        <tfoot>
                        <tr>
                            <th colspan="2">TOTAL</th>
                            <th class="total p" style="text-align: right; ">${g.formatNumber(number: tot, maxFractionDigits: 3, minFractionDigits: 3)}</th>
                        </tr>
                        </tfoot>
                    </table>
                </fieldset>
            </div>
        </div>

        <div id="tab-cuadrillaTipo" class="tab">
            <fieldset class="borde">
                <legend>Cuadrilla Tipo</legend>

                <table class="table table-bordered table-striped table-hover table-condensed" id="tablaCuadrilla">
                    <thead>
                    <tr>
                        <th style="width: 20px; text-align: center">Coeficiente</th>
                        <th style="width: 70px">Nombre del Indice (INEC)</th>
                        <th style="width: 40px">Valor</th>
                    </tr>
                    </thead>
                    <tbody id="bodyCuadrilla">
                    <g:set var="tot" value="${0}"/>
                    <g:each in="${cuadrilla}" var="i">
                        <tr>
                            <td>${i?.numero}</td>
                            <td>${i?.indice?.descripcion}</td>
                            <td class="editable" data-tipo="c" data-id="${i.id}" id="${i.id}" data-original="${i.valor}" data-valor="${i.valor}" style="text-align: right; width: 40px">
                                ${g.formatNumber(number: i?.valor, minFractionDigits: 3, maxFractionDigits: 3)}
                            </td>
                            <g:set var="tot" value="${tot + i.valor}"/>
                        </tr>
                    </g:each>
                    </tbody>
                    <tfoot>
                    <tr>
                        <th colspan="2">TOTAL</th>
                        <th class="total c" style="text-align: right; ">${g.formatNumber(number: tot, maxFractionDigits: 3, minFractionDigits: 3)}</th>
                    </tr>
                    </tfoot>
                </table>
            </fieldset>
        </div>
    </div>


<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandlerBody.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js', file: 'tableHandler.js')}"></script>

<script type="text/javascript">
    decimales = 3;
    tabla = $(".table");

    beforeDoEdit = function (sel, tf) {
        var tipo = sel.data("tipo");
        tf.data("tipo", tipo);
    };

    textFieldBinds = {
        keyup : function () {
            var tipo = $(this).data("tipo");
            var td = $(this).parents("td");
            var val = $(this).val();
            var thTot = $("th." + tipo);
            var tds = $(".editable[data-tipo=" + tipo + "]").not(td);

            var tot = parseFloat(val);
            tds.each(function () {
                tot += parseFloat($(this).data("valor"));
            });
            thTot.text(tot);
        }
    };

    $(".editable").first().addClass("selected");

    $("#btnSave").click(function () {
//                var btn = $(this);
        var str = "";
        $(".editable").each(function () {
            var td = $(this);
            var id = td.data("id");
            var valor = parseFloat(td.data("valor"));
            var orig = parseFloat(td.data("original"));

            if (valor != orig) {
                if (str != "") {
                    str += "&";
                }
                str += "valor=" + id + "_" + valor;
            }
        });
        if (str != "") {
//                    btn.hide().after(spinner);
            $.ajax({
                type    : "POST",
                url     : "${createLink(action:'saveCambiosPolinomica')}",
                data    : str,
                success : function (msg) {
//                            spinner.remove();
//                            btn.show();
                    var parts = msg.split("_");
                    var ok = parts[0];
                    var no = parts[1];
                    doHighlight({elem : $(ok), clase : "ok"});
                    doHighlight({elem : $(no), clase : "no"});
                }
            });
        }
        return false;
    });

    $("#tabs").tabs({
        heightStyle : "fill",
        activate    : function (event, ui) {
            ui.newPanel.find(".editable").first().addClass("selected");
        }
    });

</script>



