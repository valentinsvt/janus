<style type="text/css">
.tab {
    height     : 410px !important;
    overflow-x : hidden;
    overflow-y : hidden;
}

.inputVar {
    width : 65px;
}
.margen {
    margin-left: 40px;
    width: 500px;
}

    /*.sum1 {*/
    /*background : #adff2f !important;*/
    /*}*/

    /*.sum2 {*/
    /*border : solid 2px green !important;*/
    /*}*/
</style>

<g:form controller="variables" action="saveVar_ajax" name="frmSave-var">
    <g:hiddenField name="id_lgarMO" value="${obra?.listaManoObra?.id}"/>
    <div id="tabs" style="height: 465px;">
        <ul>
            <li><a href="#tab-transporte">Transporte</a></li>
            <li><a href="#tab-factores">Factores</a></li>
            <li><a href="#tab-indirecto">Costos Indirectos</a></li>
            <li class="desglose"><a href="#tab-desglose">Desglose de Transporte</a></li>
        </ul>

        <div id="tab-transporte" class="tab">
            <div class="row-fluid">
                <div class="span2">
                    Volquete
                </div>

                <div class="span6">
                    <g:select name="volquete.id" id="cmb_vol" from="${volquetes2}" optionKey="id" optionValue="nombre" class="num"
                              noSelection="${['': 'Seleccione']}" value="${(obra.volquete) ? obra?.volqueteId : par?.volquete?.id}"
                              style="width: 300px;"/>
                </div>

                <div class="span1" style="margin-left: 50px;">
                    Costo
                </div>

                <div class="span2">
                    %{--<div class="input-append">--}%
                    <g:textField class="inputVar num" style="width: 80px" disabled="" name="costo_volqueta" value=""/>
                    %{--<span class="add-on">$</span>--}%
                    %{--</div>--}%
                </div>
            </div>

            <div class="row-fluid">
                <div class="span2">
                    Chofer
                </div>

                <div class="span6">
                    <g:select name="chofer.id" id="cmb_chof" from="${choferes}" optionKey="id" optionValue="nombre" class="num"
                              noSelection="${['': 'Seleccione']}" value="${(obra.chofer) ? obra?.choferId : par?.chofer?.id}"
                              style="width: 300px;"/>
                </div>

                <div class="span1" style="margin-left: 50px;">
                    Costo
                </div>

                <div class="span2">
                    %{--<div class="input-append">--}%
                    <g:textField class="inputVar num" name="costo_chofer" disabled="" style="width: 80px"/>
                    %{--<span class="add-on">$</span>--}%
                    %{--</div>--}%
                </div>
            </div>


            <div class="span6" style="margin-bottom: 20px; margin-top: 5px">
                <div class="span3" style="font-weight: bold;">
                    Distancia Peso
                </div>

                <div class="span2" style="font-weight: bold;">
                    Distancia Volumen
                </div>
            </div>


            <div class="row-fluid">

                <div class="span3">
                    Capital de Cantón
                </div>

                <div class="span2">
                    <g:textField type="text" name="distanciaPeso" class="inputVar num" value="${g.formatNumber(number: obra?.distanciaPeso, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>


                <div class="span5">
                    Materiales Petreos Hormigones
                </div>

                <div class="span1">
                    <g:textField type="text" name="distanciaVolumen" class="inputVar num" value="${g.formatNumber(number: obra?.distanciaVolumen, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>

            </div>

            <div class="row-fluid">

                <div class="span3">
                    Especial
                </div>

                <div class="span2">
                    %{--<div class="input-append">--}%
                    <g:textField type="text" name="distanciaPesoEspecial" class="inputVar num" value="${g.formatNumber(number: obra?.distanciaPesoEspecial, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                    %{--<span class="add-on">km</span>--}%
                    %{--</div>--}%
                </div>

                <div class="span5">
                    Materiales Mejoramiento
                </div>

                <div class="span1">
                    %{--<div class="input-append">--}%
                    <g:textField type="text" name="distanciaVolumenMejoramiento" class="inputVar num" value="${g.formatNumber(number: obra?.distanciaVolumenMejoramiento, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                    %{--<span class="add-on">km</span>--}%
                    %{--</div>--}%
                </div>

            </div>

            <div class="row-fluid">

                <div class="span3"></div>

                <div class="span2"></div>


                <div class="span5">
                    Materiales Carpeta Asfáltica
                </div>

                <div class="span1">
                    %{--<div class="input-append">--}%
                    <g:textField type="text" name="distanciaVolumenCarpetaAsfaltica" class="inputVar num" value="${g.formatNumber(number: obra?.distanciaVolumenCarpetaAsfaltica, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                    %{--<span class="add-on">km</span>--}%
                    %{--</div>--}%
                </div>

            </div>

            <div style="margin: 5px; font-weight: bold; text-align: center">
                Listas de Precios para Peso y Volumen
            </div>
            <div class="row-fluid" style="margin-top: 10px; width: 640px">

                <div class="span2" style="width: 70px;">
                    Cantón
                </div>

                <div class="span4">
                    <g:select name="lugar.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=1')}"
                              optionKey="id" optionValue="descripcion" value="${obra?.lugar?.id}" class="span10"
                              noSelection="['null': 'Seleccione...']" />
                </div>

                <div class="span4" style="margin-left: -20px; width: 150px;">
                    Petreos Hormigones
                </div>
                <div class="span4">
                    <g:select name="listaVolumen0.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=3')}"
                              optionKey="id" optionValue="descripcion" value="${obra?.listaVolumen0?.id}"
                              class="span10" noSelection="['null': 'Seleccione...']" style="margin-left: -10px;"/>
                </div>

            </div>

            <div class="row-fluid" style="margin-top: 10px; width: 640px">

                <div class="span2" style="width: 70px;">
                    Especial
                </div>

                <div class="span4">
                    <g:select name="listaPeso1.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=2')}"
                              optionKey="id" optionValue="descripcion" value="${obra?.listaPeso1?.id}" class="span10"
                              noSelection="['null': 'Seleccione...']" />
                </div>

                <div class="span4" style="margin-left: -20px; width: 150px;">
                    Mejoramiento
                </div>

                <div class="span4">
                     <g:select name="listaVolumen1.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=4')}"
                               optionKey="id" optionValue="descripcion" value="${obra?.listaVolumen1?.id}"
                               class="span10" noSelection="['null': 'Seleccione...']" style="margin-left: -10px;"/>
                </div>

            </div>

            <div class="row-fluid" style="margin-top: 10px; width: 640px">

                <div class="span3" style="margin-left: 265px">
                    Carpeta Asfáltica
                </div>

                <div class="span4">
                    <g:select name="listaVolumen2.id" from="${janus.Lugar.findAll('from Lugar  where tipoLista=5')}"
                              optionKey="id" optionValue="descripcion" value="${obra?.listaVolumen2?.id}"
                              class="span10" noSelection="['null': 'Seleccione...']"/>
                </div>

            </div>

        </div>

        <div id="tab-factores" class="tab">
            <div class="row-fluid margen" style="margin-top: 20px;">
                <div class="span5">
                    Factor de reducción
                </div>

                <div class="span2">
                    <g:textField type="text" name="factorReduccion" class="inputVar num" value="${g.formatNumber(number: (obra?.factorReduccion) ?: par.factorReduccion, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>
            </div>
            <div class="row-fluid margen">
                <div class="span5">
                    Velocidad
                </div>

                <div class="span2">
                    <g:textField type="text" name="factorVelocidad" class="inputVar num" value="${g.formatNumber(number: (obra?.factorVelocidad) ?: par.factorVelocidad, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>
            </div>

            <div class="row-fluid margen">
                <div class="span5">
                    Capacidad Volquete
                </div>

                <div class="span2">
                    <g:textField type="text" name="capacidadVolquete" class="inputVar num" value="${g.formatNumber(number: (obra?.capacidadVolquete) ?: par.capacidadVolquete, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>
            </div>
            <div class="row-fluid margen">

                <div class="span5">
                    Reducción/Tiempo
                </div>

                <div class="span2">
                    <g:textField type="text" name="factorReduccionTiempo" class="inputVar num" value="${g.formatNumber(number: (obra?.factorReduccionTiempo) ?: par.factorReduccionTiempo, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>
            </div>

            <div class="row-fluid margen">
                <div class="span5">
                    Factor Volumen
                </div>

                <div class="span2">
                    <g:textField type="text" name="factorVolumen" class="inputVar num" value="${g.formatNumber(number: (obra?.factorVolumen) ?: par.factorVolumen, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>
            </div>
            <div class="row-fluid margen">
                <div class="span5">
                    Factor Peso
                </div>

                <div class="span2">
                    <g:textField type="text" name="factorPeso" class="inputVar num" value="${g.formatNumber(number: (obra?.factorPeso) ?: par.factorPeso, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>
            </div>

            %{--<div class="row-fluid">--}%

            %{--<div class="span3">--}%
            %{--Distancia Volumen--}%
            %{--</div>--}%

            %{--<div class="span3">--}%
            %{--<g:textField type="text" name="distanciaVolumen" class="inputVar" value="0.00"/>--}%
            %{--</div>--}%

            %{--<div class="span3">--}%
            %{--Distancia Peso--}%
            %{--</div>--}%

            %{--<div class="span3">--}%
            %{--<g:textField type="text" name="distanciaPeso" class="inputVar" value="0.00"/>--}%
            %{--</div>--}%
            %{--</div>--}%
        </div>

        <div id="tab-indirecto" class="tab">
            <div class="row-fluid">
                <div class="span10">
                    Control y Administración (Fiscalización) - no se usa en obras nuevas
                </div>

                <div class="span2">
                    <g:textField type="text" name="contrato" class="inputVar num" value="${g.formatNumber(number: obra?.contrato, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
                </div>
            </div>

            <div class="row-fluid" style="margin-top: 20px;">
                <div class="span4">
                    Dirección de obra
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosObra" class="inputVar sum1 num" value="${g.formatNumber(number: (obra?.indiceCostosIndirectosObra), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="1"/>
                </div>

                <div class="span4">
                    Promoción
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosPromocion" class="inputVar sum1 num" value="${g.formatNumber(number: (obra?.indiceCostosIndirectosPromocion), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="7"/>
                </div>
            </div>

            <div class="row-fluid" style="margin-top: 10px;">
                <div class="span4">
                    Mantenimiento y gastos de oficina
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosMantenimiento" class="inputVar sum1 num" value="${g.formatNumber(number: (obra?.indiceCostosIndirectosMantenimiento), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="2"/>
                </div>

                <div class="span4 bold">
                    Gastos Generales (subtotal)
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceGastosGenerales" class="inputVar sum2 num" value="${g.formatNumber(number: (obra?.indiceGastosGenerales), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" readonly=""/>
                </div>
            </div>

            <div class="row-fluid" style="margin-top: 10px;">
                <div class="span4">
                    Administrativos
                </div>

                <div class="span2">
                    <g:textField type="text" name="administracion" class="inputVar sum1 num" value="${g.formatNumber(number: (obra?.administracion), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="3"/>
                </div>

                <div class="span4 bold">
                    Imprevistos
                </div>

                <div class="span2">
                    <g:textField type="text" name="impreso" class="inputVar  sum2 num" value="${g.formatNumber(number: (obra?.impreso), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="8"/>
                </div>
            </div>

            <div class="row-fluid" style="margin-top: 10px;">
                <div class="span4">
                    Garantías
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosGarantias" class="inputVar sum1 num" value="${g.formatNumber(number: (obra?.indiceCostosIndirectosGarantias), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="4"/>
                </div>

                <div class="span4 bold">
                    Utilidad
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceUtilidad" class="inputVar sum2  num" value="${g.formatNumber(number: (obra?.indiceUtilidad), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="9"/>
                </div>
            </div>

            <div class="row-fluid" style="margin-top: 10px;">
                <div class="span4">
                    Costos financieros
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosCostosFinancieros" class="inputVar sum1 num" value="${g.formatNumber(number: (obra?.indiceCostosIndirectosCostosFinancieros), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="5"/>
                </div>

                <div class="span4 bold">
                    Timbres provinciales
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosTimbresProvinciales" class="inputVar sum2 num" value="${g.formatNumber(number: (obra?.indiceCostosIndirectosTimbresProvinciales), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="10"/>
                </div>
            </div>

            <div class="row-fluid" style="margin-top: 10px;">
                <div class="span4">
                    Vehículos
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosVehiculos" class="inputVar sum1 num" value="${g.formatNumber(number: (obra?.indiceCostosIndirectosVehiculos), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" tabindex="6"/>
                </div>

                <div class="span4 bold" style="border-top: solid 1px #D3D3D3;">
                    Total Costos Indirectos
                </div>

                <div class="span2">
                    <g:textField type="text" name="totales" class="inputVar num" value="${g.formatNumber(number: (obra?.totales) ?: 0, maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}" readonly=""/>
                </div>
            </div>

        </div>
    <div id="tab-desglose" class="tab">

        <div class="row-fluid margen">
            <div class="span5">
               Desglose Equipos
            </div>

            <div class="span2">
                <g:textField type="text" name="desgloseEquipo" class="inputVar num" value="${g.formatNumber(number: (obra?.desgloseEquipo), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
            </div>
        </div>
        <div class="row-fluid margen">
            <div class="span5">
                Desglose Repuestos
            </div>

            <div class="span2">
                <g:textField type="text" name="desgloseRepuestos" class="inputVar num" value="${g.formatNumber(number: (obra?.desgloseRepuestos), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
            </div>
        </div>


        <div class="row-fluid margen">
            <div class="span5">
                Desglose Combustibles
            </div>

            <div class="span2">
                <g:textField type="text" name="desgloseCombustible" class="inputVar num" value="${g.formatNumber(number: (obra?.desgloseCombustible), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
            </div>
        </div>
        <div class="row-fluid margen">

            <div class="span5">
                Desglose Mecánico
            </div>

            <div class="span2">
                <g:textField type="text" name="desgloseMecanico" class="inputVar num" value="${g.formatNumber(number: (obra?.desgloseMecanico), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
            </div>
        </div>

        <div class="row-fluid margen">
            <div class="span5">
               Desglose Saldo
            </div>

            <div class="span2">
                <g:textField type="text" name="desgloseSaldo" class="inputVar num" value="${g.formatNumber(number: (obra?.desgloseSaldo), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
            </div>


        </div>

        <div class="row-fluid margen" style="margin-bottom: 5px">
        ______________________________________

        </div>

        <div class="row-fluid margen">

            <div class="span5">
                Total Desglose:
            </div>

            <div class="span2">
                %{--<g:textField type="text" name="totalDesglose" class="inputVar num" value="${g.formatNumber(number: (obra?.desgloseSaldo + obra?.desgloseMecanico + obra?.desgloseCombustible + obra?.desgloseRepuestos + obra?.desgloseEquipo), maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>--}%
                <input type="text" id="totalDesglose" class="inputVar num" value="0.0", maxFractionDigits: 2, minFractionDigits: 2, format: '##,##0', locale: 'ec')}"/>
            </div>
        </div>
    </div>


    </div>
</g:form>

<script type="text/javascript">

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

    $(".sum1, .sum2, .num").keydown(function (ev) {
        if (ev.keyCode == 190 || ev.keyCode == 188) {
            if ($(this).val().indexOf(".") > -1) {
                return false
            }
        }
        return validarNum(ev);
    });

    function suma(items, update) {
        var sum1 = 0;
        items.each(function () {
            sum1 += parseFloat($(this).val());
        });
        update.val(number_format(sum1, 2, ".", ""));
    }

    function costoItem($campo, $update) {
        var id = $campo.val();
        var fecha = $("#fechaPreciosRubros").val();
        /*var ciudad = $("#lugar\\.id").val();*/
        var ciudad = $("#id_lgarMO").val();
//        ////console.log(id, fecha, ciudad);
        if (id != "" && fecha != "" && ciudad != "") {
            $.ajax({
                type    : "POST",
                url     : "${g.createLink(controller: 'rubro',action:'getPreciosTransporte')}",
                data    : {
                    fecha  : fecha,
                    ciudad : ciudad,
                    ids    : id
                },
                success : function (msg) {
                    var precios = msg.split("&");
                    for (var i = 0; i < precios.length; i++) {
                        if ($.trim(precios[i]) != "") {
                            var parts = precios[i].split(";");
                            if (parts.length > 1) {
                                $update.val(parts[1].toString().trim());
                            }
                        }
                    }
                }
            });
        } else {
            $update.val("0.00");
        }
    }

    $(function () {
        $(".sum1").keyup(function (ev) {
            suma($(".sum1"), $("#indiceGastosGenerales"));
            suma($(".sum2"), $("#totales"));
        }).blur(function () {
                    suma($(".sum1"), $("#indiceGastosGenerales"));
                    suma($(".sum2"), $("#totales"));
                });
        $(".sum2").keyup(function (ev) {
            suma($(".sum2"), $("#totales"));
        }).blur(function () {
                    suma($(".sum2"), $("#totales"));
                });
        $(".sum1").blur();
        $(".sum2").blur();
        $("#tabs").tabs({
            heightStyle : "fill"
        });

        costoItem($("#cmb_vol"), $("#costo_volqueta"));
        costoItem($("#cmb_chof"), $("#costo_chofer"));

        $("#cmb_vol").change(function () {
            costoItem($(this), $("#costo_volqueta"));
        });
        $("#cmb_chof").change(function () {
            costoItem($(this), $("#costo_chofer"));
        });
    });

    $(function () {
/*
        $(".desglose").click(function() {
            sumaDesglose();
        });
*/
        $("#desgloseEquipo").keyup(function() {
            sumaDesglose();
        });
        $("#desgloseRepuestos").keyup(function() {
            sumaDesglose();
        });
        $("#desgloseCombustible").keyup(function() {
            sumaDesglose();
        });
        $("#desgloseMecanico").keyup(function() {
            sumaDesglose();
        });
        $("#desgloseSaldo").keyup(function() {
            sumaDesglose();
        });
    });

    function sumaDesglose() {
        var smDesglose = 0.0
        //console.log("sumadesglose")
        smDesglose = parseFloat($("#desgloseEquipo").val()) + parseFloat($("#desgloseRepuestos").val()) +
                parseFloat($("#desgloseCombustible").val()) + parseFloat($("#desgloseMecanico").val()) +
                parseFloat($("#desgloseSaldo").val())
        $("#totalDesglose").val(number_format(smDesglose, 2, ".", ""));
    }


</script>