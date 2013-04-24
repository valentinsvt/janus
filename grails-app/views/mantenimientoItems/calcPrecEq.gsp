<style type="text/css">
th, td {
    text-align     : center;
    vertical-align : middle;
    padding        : 3px;
}

.calculos {
    height     : 240px;
    overflow-x : hidden;
    overflow-y : auto;
}

.totalParcial, .totalParcial td, .totalParcial th {
    background : #ADADAD;
}

.totalFinal, .totalFinal td, .totalFinal th {
    background : #5E5E5E;
    color      : #f5f5f5;
}

.totalParcial, .totalParcial td, .totalParcial th, .totalFinal, .totalFinal td, .totalFinal th {
    font-weight : bold;
}

.num {
    text-align : right;
}
</style>

<h4>Datos del equipo</h4>
<table border="1">
    <thead>
        <tr>
            <th>Equipo</th>
            <th>Potencia</th>
            <th>Valor de adquisición del equipo</th>
            <th colspan="3">Vida Económica (h)</th>
            <th colspan="3">Horas al año (h)</th>
        </tr>
        <tr>
            <th>&nbsp;</th>
            <th>(HP)</th>
            <th>$ (Va)</th>
            <th>Baja</th>
            <th>Alta</th>
            <th>Prom (Hv)</th>
            <th>Baja</th>
            <th>Alta</th>
            <th>Prom (Ha)</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>${item.nombre}</td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="hp"/>
            </td>
            <td>
                <div class="input-append">
                    <g:textField class="input-mini calcular" name="va" type="text"/>
                    <span class="add-on">$</span>
                </div>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular prom" data-prom="hv" name="hvb"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular prom" data-prom="hv" name="hva"/>
            </td>
            <td id="hv" class="num">
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular prom" data-prom="ha" name="hab"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular prom" data-prom="ha" name="haa"/>
            </td>
            <td id="ha" class="num">
            </td>
        </tr>
    </tbody>
</table>

<h4>Valores anuales</h4>
<table border="1">
    <thead>
        <tr>
            <th>Año</th>
            <th>Seguro (s)</th>
            <th>Tasa interés anual (i)</th>
            <th>Factor costo respuestos reparaciones (k)</th>
            <th>Costo diesel (Di)</th>
            <th>Costo lubricante (Ac)</th>
            <th>Costo grasa (Gr)</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>${valoresAnuales.anio}</td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="cs" value="${g.formatNumber(number: valoresAnuales.seguro, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
            </td>
            <td>
                <div class="input-append">
                    <g:textField type="number" class="input-mini calcular" name="ci" value="${g.formatNumber(number: valoresAnuales.tasaInteresAnual, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
                    <span class="add-on">%</span>
                </div>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="k" value="${g.formatNumber(number: valoresAnuales.factorCostoRepuestosReparaciones, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="di" value="${g.formatNumber(number: valoresAnuales.costoDiesel, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="ac" value="${g.formatNumber(number: valoresAnuales.costoLubricante, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="gr" value="${g.formatNumber(number: valoresAnuales.costoGrasa, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
            </td>
            <td>
                <a href="#" class="btn" id="btnSaveValoresAnuales">Modificar valores anuales</a>
            </td>
        </tr>
    </tbody>
</table>

<h4>Cálculo del costo total de la hora</h4> %{--<a href="#" class="btn btn-success" id="btnCalcular">Calcular</a></h4>--}%

<div class="calculos">
    <div class="row">
        <div class="span3">
            <table border="1">
                <tr>
                    <th>Valor de adquisición del equipo</th>
                    <th>Va</th>
                    <td id="tdVa" class="num dol" data-dec="2"></td>
                </tr>
                <tr>
                    <th>Años de vida</th>
                    <th>Av</th>
                    <td id="tdAv" class="num" data-dec="5"></td>
                </tr>
                <tr>
                    <th>Valor de rescate</th>
                    <th>Vr</th>
                    <td id="tdVr" class="num dol" data-dec="2"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Depreciación del equipo</th>
                    <th>D</th>
                    <td id="tdD" class="num dol" data-dec="2"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1">
                <tr>
                    <th>Costo del dinero</th>
                    <th>i</th>
                    <td id="tdCi" class="num" data-dec="2"></td>
                </tr>
                <tr>
                    <th>Factor de recuperación del capital</th>
                    <th>frc</th>
                    <td id="tdFrc" class="num" data-dec="5"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Intereses</th>
                    <th>I</th>
                    <td id="tdI" class="num" data-dec="5"></td>
                </tr>
            </table>
        </div>

        <div class="span2">
            <div class="row">
                <div class="span2">
                    <table border="1">
                        <tr>
                            <th>Seguros</th>
                            <th>s</th>
                            <td id="tdCs" class="num" data-dec="2"></td>
                        </tr>
                        <tr class="totalParcial">
                            <th>Costo de seguros</th>
                            <th>S</th>
                            <td id="tdS" class="num" data-dec="5"></td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="row" style="margin-top: 5px;">
                <div class="span2">
                    <table border="1">
                        <tr class="totalParcial">
                            <th>Matrícula</th>
                            <th>m</th>
                            <td id="tdM" class="num" data-dec="5"></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>

        <div class="span3">
            <div class="row">
                <div class="span3">
                    <table border="1">
                        <tr>
                            <th>Factor de costo de respuestos reparaciones</th>
                            <th>k</th>
                            <td id="tdK" class="num" data-dec="2"></td>
                        </tr>
                        <tr class="totalParcial">
                            <th>Costo de repuestos</th>
                            <th>R</th>
                            <td id="tdR" class="num" data-dec="5"></td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="row" style="margin-top: 5px;">
                <div class="span3">
                    <table border="1">
                        <tr class="totalParcial">
                            <th>Costo M.O. reparaciones</th>
                            <th>MOR</th>
                            <td id="tdMor" class="num" data-dec="5"></td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>
    </div>


    <div class="row" style="margin-top: 10px;">
        <div class="span3">
            <table border="1">
                <tr>
                    <th>Costo diesel - combustibles</th>
                    <th>Di</th>
                    <td id="tdDi" class="num" data-dec="0"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Costo diesel</th>
                    <th>CD</th>
                    <td id="tdCd" class="num" data-dec="5"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1">
                <tr>
                    <th>Aceite lubricante</th>
                    <th>Ac</th>
                    <td id="tdAc" class="num" data-dec="0"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Costo lubricante</th>
                    <th>CL</th>
                    <td id="tdCl" class="num" data-dec="5"></td>
                </tr>
            </table>
        </div>

        <div class="span2">
            <table border="1">
                <tr>
                    <th>Grasa</th>
                    <th>Gr</th>
                    <td id="tdGr" class="num" data-dec="0"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Costo grasa</th>
                    <th>CG</th>
                    <td id="tdCg" class="num" data-dec="5"></td>
                </tr>
            </table>
        </div>
    </div>
</div>

<table border="1">
    <tr class="totalFinal">
        <th>Costo total de la hora</th>
        <th>Ch</th>
        <td id="tdCh" class="num dol" data-dec="2"></td>
    </tr>
</table>

<script type="text/javascript">

    var data = {
        va  : 0,
        av  : 0,
        vr  : 0,
        d   : 0,
        ci  : ${valoresAnuales.tasaInteresAnual/100},
        frc : 0,
        i   : 0,
        s   : 0,
        cs  : ${valoresAnuales.seguro},
        m   : 0,
        k   : ${valoresAnuales.factorCostoRepuestosReparaciones},
        r   : 0,
        mor : 0,
        di  : ${valoresAnuales.costoDiesel},
        cd  : 0,
        ac  : ${valoresAnuales.costoLubricante},
        cl  : 0,
        gr  : ${valoresAnuales.costoGrasa},
        cg  : 0,
        ch  : 0,
        hp  : 0,
        hvb : 0,
        hva : 0,
        hv  : 0,
        hab : 0,
        haa : 0,
        ha  : 0
    };

    calc();

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

    function calc() {

        data.av = data.hv / data.ha;
        data.vr = 72.47 / (Math.pow(data.av, 0.32)) * data.va / 100;
        data.d = (data.va - data.vr) / data.hv;

        data.frc = 1 / data.av + (data.av + 1) / (2 * data.av) * data.ci;
        data.i = data.va * data.frc * data.ci / data.hv;

        data.s = (data.va + data.vr) * data.cs / (2 * data.ha);

        data.m = 0.001 * (data.va - data.vr) * data.av / data.hv;

        data.r = 0.7425 * data.d * data.k;

        data.mor = 0.23 * data.d * data.k;

        data.cd = 0.04 * data.di * data.hp;

        data.cl = 0.00035 * data.ac * data.hp;

        data.cg = 0.001 * data.gr * data.hp;

        data.ch = data.d + data.i + data.s + data.m + data.r + data.mor + data.cd + data.cl + data.cg;

        $.each(data, function (d, i) {
            if (i != 0) {
                var td = $("#td" + d.capitalize());
                if (td) {
                    var dec = parseInt(td.data("dec"));
                    var t = number_format(i, dec, ".", ",");
                    if (td.hasClass("dol")) {
                        t = "$" + t
                    }
                    td.text(t);
                }
            }
        });
    }

    $(".calcular").bind({
        keydown : function (ev) {
            var dec = 2;
            var val = $(this).val();
            if (ev.keyCode == 188 || ev.keyCode == 190 || ev.keyCode == 110) {
                if (!dec) {
                    return false;
                } else {
                    if (val.length == 0) {
                        $(this).val("0");
                    }
                    if (val.indexOf(".") > -1 || val.indexOf(",") > -1) {
                        return false;
                    }
                }
            } else {
                if (val.indexOf(".") > -1 || val.indexOf(",") > -1) {
                    if (dec) {
                        var parts = val.split(".");
                        var l = parts[1].length;
                        if (l >= dec) {
                            return false;
                        }
                    }
                } else {
                    return validarNum(ev);
                }
            }
        },
        keyup   : function () {
            var name = $(this).attr("name");
            data[name] = parseFloat($(this).val());
            if (name == "ci") {
                data[name] = parseFloat($(this).val()) / 100;
            }
            if ($(this).hasClass("prom")) {
                var prom = $(this).data("prom");

                var b = $("#" + prom + "b").val();
                var a = $("#" + prom + "a").val();

                if (a != "" && b != "") {
                    a = parseFloat(a);
                    b = parseFloat(b);
                    var m = (a + b) / 2;
                    data[prom] = m;
                    $("#" + prom).text(number_format(m, 2, ".", ""));
                }
            }
            calc();
        }
    });

</script>