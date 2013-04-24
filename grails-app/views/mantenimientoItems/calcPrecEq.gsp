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
                <g:textField type="number" class="input-mini calcular" name="hvb"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="hva"/>
            </td>
            <td id="hv">
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="hab"/>
            </td>
            <td>
                <g:textField type="number" class="input-mini calcular" name="haa"/>
            </td>
            <td id="ha">
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
                <g:textField type="number" class="input-mini calcular" name="s" value="${g.formatNumber(number: valoresAnuales.seguro, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
            </td>
            <td>
                <div class="input-append">
                    <g:textField type="number" class="input-mini calcular" name="i" value="${g.formatNumber(number: valoresAnuales.tasaInteresAnual, minFractionDigits: 0, maxFractionDigits: 3, locale: 'ec')}"/>
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

<h4>Cálculo del costo total de la hora <a href="#" class="btn btn-success" id="btnCalcular">Calcular</a></h4>

<div class="calculos">
    <div class="row">
        <div class="span3">
            <table border="1">
                <tr>
                    <th>Valor de adquisición del equipo</th>
                    <th>Va</th>
                    <td id="tdVa"></td>
                </tr>
                <tr>
                    <th>Años de vida</th>
                    <th>Av</th>
                    <td id="tdAv"></td>
                </tr>
                <tr>
                    <th>Valor de rescate</th>
                    <th>Vr</th>
                    <td id="tdVr"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Depreciación del equipo</th>
                    <th>D</th>
                    <td id="tdD"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1">
                <tr>
                    <th>Costo del dinero</th>
                    <th>i</th>
                    <td id="tdCi"></td>
                </tr>
                <tr>
                    <th>Factor de recuperación del capital</th>
                    <th>frc</th>
                    <td id="tdFrc"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Intereses</th>
                    <th>I</th>
                    <td id="tdI"></td>
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
                            <td id="tdCs"></td>
                        </tr>
                        <tr class="totalParcial">
                            <th>Costo de seguros</th>
                            <th>S</th>
                            <td id="tdS"></td>
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
                            <td id="tdM"></td>
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
                            <td id="tdK"></td>
                        </tr>
                        <tr class="totalParcial">
                            <th>Costo de repuestos</th>
                            <th>R</th>
                            <td id="tdR"></td>
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
                            <td id="tdMor"></td>
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
                    <td id="tdDi"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Costo diesel</th>
                    <th>CD</th>
                    <td id="tdCd"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1">
                <tr>
                    <th>Aceite lubricante</th>
                    <th>Ac</th>
                    <td id="tdAc"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Costo lubricante</th>
                    <th>CL</th>
                    <td id="tdCl"></td>
                </tr>
            </table>
        </div>

        <div class="span2">
            <table border="1">
                <tr>
                    <th>Grasa</th>
                    <th>Gr</th>
                    <td id="tdGr"></td>
                </tr>
                <tr class="totalParcial">
                    <th>Costo grasa</th>
                    <th>CG</th>
                    <td id="tdCg"></td>
                </tr>
            </table>
        </div>

        <div class="span3">
            <table border="1" style="margin-top: 15px;">
                <tr class="totalFinal">
                    <th>Costo total de la hora</th>
                    <th>Ch</th>
                    <td id="tdCh"></td>
                </tr>
            </table>
        </div>
    </div>
</div>

<script type="text/javascript">

    var va, av, vr, d, ci, frc, i, cs, s, m, k, r, mor, di, cd, ac, cl, gr, cg, ch;

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
        }
    });

</script>