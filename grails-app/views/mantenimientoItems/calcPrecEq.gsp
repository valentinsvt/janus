<style type="text/css">
th, td {
    text-align     : center;
    vertical-align : middle;
    padding        : 3px;
    font-size      : 12px;
}

h4 {
    font-size : 15px;
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

.st-title {
    width     : 150px;
    font-size : 12px;
}

.st-cod {
    width     : 33px;
    font-size : 12px;
}

.st-total {
    width     : 70px;
    font-size : 12px;
}
</style>

<h4>Datos del equipo</h4>
<table border="1">
    <thead>
        <tr>
            <th>Equipo</th>
            <th>Potencia</th>
            <th>Valor de adquisición del equipo</th>
            <th>Llantas</th>
            <th colspan="3">Vida Económica (h)</th>
            <th colspan="3">Horas al año (h)</th>
            <th colspan="3">Vida llantas al año (h)</th>
        </tr>
        <tr>
            <th>&nbsp;</th>
            <th>(HP)</th>
            <th>$ (V<sub>C</sub>)</th>
            <th>$ (V<sub>LL</sub>)</th>
            <th>Baja</th>
            <th>Alta</th>
            <th>Prom (H<sub>V</sub>)</th>
            <th>Baja</th>
            <th>Alta</th>
            <th>Prom (H<sub>a</sub>)</th>
            <th>Baja</th>
            <th>Alta</th>
            <th>Prom (H<sub>LL</sub>)</th>
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
                    <g:textField class="input-mini calcular" name="vc" type="text"/>
                    <span class="add-on">$</span>
                </div>
            </td>
            <td>
                <div class="input-append">
                    <g:textField class="input-mini calcular" name="vll" type="text"/>
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
            <td>
                <g:textField type="number" class="input-mini calcular prom" data-prom="hll" name="hllb"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular prom" data-prom="hll" name="hlla"/>
            </td>
            <td id="hll" class="num">
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
                <a href="#" class="btn btn-primary" id="btnSaveValoresAnuales">Modificar valores anuales</a>
            </td>
        </tr>
    </tbody>
</table>

<h4>Cálculo del costo total de la hora %{--<a href="#" class="btn btn-success" id="btnCalcular">Calcular</a></h4>--}%</h4>

<div class="calculos">
    <div class="row">
        <div class="span3">
            <table border="1">
                <tr>
                    <th class="st-title">Valor comercial equipo</th>
                    <th class="st-cod">Vc</th>
                    <td class="tdVc num dol st-total" data-dec="2"></td>
                </tr>
                <tr>
                    <th class="st-title">Precio de las llantas nuevas</th>
                    <th class="st-cod">V<sub>LL</sub></th>
                    <td class="tdVll num dol st-total" data-dec="2"></td>
                </tr>
                <tr>
                    <th class="st-title">Valor adquisición equipo</th>
                    <th class="st-cod">Va</th>
                    <td class="tdVa num dol st-total" data-dec="2"></td>
                </tr>
                <tr>
                    <th class="st-title">Años de vida</th>
                    <th class="st-cod">Av</th>
                    <td class="tdAv num st-total" data-dec="5"></td>
                </tr>
                <tr>
                    <th class="st-title">Valor de rescate</th>
                    <th class="st-cod">Vr</th>
                    <td class="tdVr num dol st-total" data-dec="2"></td>
                </tr>
                <tr class="totalParcial">
                    <th class="st-title">Depreciación del equipo</th>
                    <th class="st-cod">D</th>
                    <td class="tdD num dol st-total" data-dec="2"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1">
                <tr>
                    <th class="st-title">Costo del dinero</th>
                    <th class="st-cod">i</th>
                    <td class="tdCi num st-total" data-dec="2"></td>
                </tr>
                <tr>
                    <th class="st-title">Factor de recuperación del capital</th>
                    <th class="st-cod">frc</th>
                    <td class="tdFrc num st-total" data-dec="5"></td>
                </tr>
                <tr class="totalParcial">
                    <th class="st-title">Intereses</th>
                    <th class="st-cod">I</th>
                    <td class="tdI num st-total" data-dec="5"></td>
                </tr>
            </table>
        </div>

        <div class="span2">
            <div class="row">
                <div class="span2">
                    <table border="1">
                        <tr>
                            <th class="st-title">Seguros</th>
                            <th class="st-cod">s</th>
                            <td class="tdCs num st-total" data-dec="2"></td>
                        </tr>
                        <tr class="totalParcial">
                            <th class="st-title">Costo de seguros</th>
                            <th class="st-cod">S</th>
                            <td class="tdS num st-total" data-dec="5"></td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="row" style="margin-top: 5px;">
                <div class="span2">
                    <table border="1">
                        <tr class="totalParcial">
                            <th class="st-title">Matrícula</th>
                            <th class="st-cod">m</th>
                            <td class="tdM num st-total" data-dec="5"></td>
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
                            <th class="st-title">Factor de costo de respuestos reparaciones</th>
                            <th class="st-cod">k</th>
                            <td class="tdK num st-total" data-dec="2"></td>
                        </tr>
                        <tr class="totalParcial">
                            <th class="st-title">Costo de repuestos</th>
                            <th class="st-cod">R</th>
                            <td class="tdR num st-total" data-dec="5"></td>
                        </tr>
                    </table>
                </div>
            </div>

            <div class="row" style="margin-top: 5px;">
                <div class="span3">
                    <table border="1">
                        <tr class="totalParcial">
                            <th class="st-title">Costo M.O. reparaciones</th>
                            <th class="st-cod">MOR</th>
                            <td class="tdMor num st-total" data-dec="5"></td>
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
                    <th class="st-title">Costo diesel - combustibles</th>
                    <th class="st-cod">Di</th>
                    <td class="tdDi num st-total" data-dec="2"></td>
                </tr>
                <tr class="totalParcial">
                    <th class="st-title">Costo diesel</th>
                    <th class="st-cod">CD</th>
                    <td class="tdCd num st-total" data-dec="5"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1">
                <tr>
                    <th class="st-title">Aceite lubricante</th>
                    <th class="st-cod">Ac</th>
                    <td class="tdAc num st-total" data-dec="2"></td>
                </tr>
                <tr class="totalParcial">
                    <th class="st-title">Costo lubricante</th>
                    <th class="st-cod">CL</th>
                    <td class="tdCl num st-total" data-dec="5"></td>
                </tr>
            </table>
        </div>

        <div class="span2">
            <table border="1">
                <tr>
                    <th class="st-title">Grasa</th>
                    <th class="st-cod">Gr</th>
                    <td class="tdGr num st-total" data-dec="2"></td>
                </tr>
                <tr class="totalParcial">
                    <th class="st-title">Costo grasa</th>
                    <th class="st-cod">CG</th>
                    <td class="tdCg num st-total" data-dec="5"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1">
                <tr>
                    <th class="st-title">Precio de las llantas</th>
                    <th class="st-cod">V<sub>LL</sub></th>
                    <td class="tdVll num st-total" data-dec="2"></td>
                </tr>
                <tr>
                    <th class="st-title">Horas de vida útil de las llantas</th>
                    <th class="st-cod">H<sub>LL</sub></th>
                    <td class="tdHll num st-total" data-dec="2"></td>
                </tr>
                <tr class="totalParcial">
                    <th class="st-title">Costo horario por llantas</th>
                    <th class="st-cod">C<sub>LL</sub></th>
                    <td class="tdCll num st-total" data-dec="5"></td>
                </tr>
            </table>
        </div>
    </div>
</div>
<table border="1">
    <tr class="totalFinal">
        <th>Costo total de la hora</th>
        <th>Ch</th>
        <td class="tdCh num dol" data-dec="2"></td>
    </tr>
</table>

<script type="text/javascript">

    var data = {
        vc   : 0,
        vll  : 0,
        va   : 0,
        av   : 0,
        vr   : 0,
        d    : 0,
        ci   : ${valoresAnuales.tasaInteresAnual/100},
        frc  : 0,
        i    : 0,
        s    : 0,
        cs   : ${valoresAnuales.seguro},
        m    : 0,
        k    : ${valoresAnuales.factorCostoRepuestosReparaciones},
        r    : 0,
        mor  : 0,
        di   : ${valoresAnuales.costoDiesel},
        cd   : 0,
        ac   : ${valoresAnuales.costoLubricante},
        cl   : 0,
        gr   : ${valoresAnuales.costoGrasa},
        cg   : 0,
        ch   : 0,
        cll  : 0,
        hp   : 0,
        hvb  : 0,
        hva  : 0,
        hv   : 0,
        hab  : 0,
        haa  : 0,
        ha   : 0,
        hllb : 0,
        hlla : 0,
        hll  : 0
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

        data.va = data.vc - data.vll;

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

        data.cll = data.vll / data.hll;

        data.ch = data.d + data.i + data.s + data.m + data.r + data.mor + data.cd + data.cl + data.cg + data.cll;

        $.each(data, function (d, i) {
            if (i != 0) {
                var td = $(".td" + d.capitalize());
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