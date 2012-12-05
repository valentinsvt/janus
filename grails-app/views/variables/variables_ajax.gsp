%{--<ul class="nav nav-tabs" id="myTab">--}%
%{--<li class="active"><a href="#home" class="tab">Home</a></li>--}%
%{--<li><a href="#profile" class="tab">Profile</a></li>--}%
%{--<li><a href="#messages" class="tab">Messages</a></li>--}%
%{--<li><a href="#settings" class="tab">Settings</a></li>--}%
%{--</ul>--}%

%{--<div class="tab-content">--}%
%{--<div class="tab-pane active" id="home">home</div>--}%

%{--<div class="tab-pane" id="profile">prof</div>--}%

%{--<div class="tab-pane" id="messages">mes</div>--}%

%{--<div class="tab-pane" id="settings">set</div>--}%
%{--</div>--}%

%{--<script type="text/javascript">--}%
%{--$(function () {--}%

%{--$(".tab").click(function () {--}%
%{--var tab = $(this).parents("li").index();--}%
%{--console.log(tab);--}%
%{--$('#myTab li:eq(' + tab + ') a').tab('show');--}%

%{--//            var tab = $(this).attr("href");--}%
%{--//            $('#myTab a[href="#' + tab + '"]').tab('show');--}%
%{--//            $('#myTab a:last').tab('show');--}%
%{--return false;--}%
%{--});--}%

%{--//        $('#myTab a:last').tab('show');--}%

%{--//        $('#myTab a[href="#profile"]').tab('show'); // Select tab by name--}%
%{--//        $('#myTab a:first').tab('show'); // Select first tab--}%
%{--//        $('#myTab a:last').tab('show'); // Select last tab--}%
%{--//        $('#myTab li:eq(2) a').tab('show'); // Select third tab (0-indexed)--}%
%{--})--}%
%{--</script>--}%


<style type="text/css">
.tab {
    height     : 260px !important;
    overflow-x : hidden;
    overflow-y : auto;
}

.inputVar {
    width : 65px;
}

/*.sum1 {*/
    /*background : #adff2f !important;*/
/*}*/

/*.sum2 {*/
    /*border : solid 2px green !important;*/
/*}*/
</style>

<g:form controller="variables" action="saveVar_ajax" name="frmSave-var">
    <div id="tabs" style="height: 335px;">
        <ul>
            <li><a href="#tab-transporte">Variables de Transporte</a></li>
            <li><a href="#tab-factores">Factores</a></li>
            <li><a href="#tab-indirecto">Costos Indirectos</a></li>
        </ul>

        <div id="tab-transporte" class="tab">
            <div class="row-fluid">
                <div class="span3">
                    Volquete
                </div>

                <div class="span5">
                    <g:select name="volquete.id" id="cmb_vol" from="${volquetes}" optionKey="id" optionValue="nombre" noSelection="${['': 'Seleccione']}"/>
                </div>

                <div class="span1">
                    Costo
                </div>

                <div class="span2">
                    %{--<div class="input-append">--}%
                    <g:textField class="inputVar" style="" disabled="" name="costo_volqueta"/>
                    %{--<span class="add-on">$</span>--}%
                    %{--</div>--}%
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Chofer
                </div>

                <div class="span5">
                    <g:select name="chofer.id" id="cmb_chof" from="${choferes}" optionKey="id" optionValue="nombre" noSelection="${['': 'Seleccione']}"/>
                </div>

                <div class="span1">
                    Costo
                </div>

                <div class="span2">
                    %{--<div class="input-append">--}%
                    <g:textField class="inputVar" name="costo_chofer" style="text-align: right" disabled=""/>
                    %{--<span class="add-on">$</span>--}%
                    %{--</div>--}%
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Distancia peso
                </div>

                <div class="span3">
                    %{--<div class="input-append">--}%
                    <g:textField type="text" name="distanciaPeso" class="inputVar" value="0.00"/>
                    %{--<span class="add-on">km</span>--}%
                    %{--</div>--}%
                </div>

                <div class="span3">
                    Distancia volumen
                </div>

                <div class="span1">
                    %{--<div class="input-append">--}%
                    <g:textField type="text" name="distanciaVolumen" class="inputVar" value="0.00"/>
                    %{--<span class="add-on">km</span>--}%
                    %{--</div>--}%
                </div>
            </div>
        </div>

        <div id="tab-factores" class="tab">
            <div class="row-fluid">
                <div class="span3">
                    Factor de reducción
                </div>

                <div class="span3">
                    <g:textField type="text" name="factorReduccion" class="inputVar" value="0.00"/>
                </div>

                <div class="span3">
                    Velocidad
                </div>

                <div class="span3">
                    <g:textField type="text" name="factorVelocidad" class="inputVar" value="0.00"/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Capacidad Volquete
                </div>

                <div class="span3">
                    <g:textField type="text" name="capacidadVolquete" class="inputVar" value="0.00"/>
                </div>

                <div class="span3">
                    Reducción/Tiempo
                </div>

                <div class="span3">
                    <g:textField type="text" name="factorReduccionTiempo" class="inputVar" value="0.00"/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Factor Volumen
                </div>

                <div class="span3">
                    <g:textField type="text" name="factorVolumen" class="inputVar" value="0.00"/>
                </div>

                <div class="span3">
                    Factor Peso
                </div>

                <div class="span3">
                    <g:textField type="text" name="factorPeso" class="inputVar" value="0.00"/>
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
                    <g:textField type="text" name="contrato" class="inputVar" value="0.00"/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Dirección de obra
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosObra" class="inputVar sum1" value="0.00" tabindex="1"/>
                </div>

                <div class="span4">
                    Promoción
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosPromocion" class="inputVar sum1" value="0.00" tabindex="7"/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Mantenimiento y gastos de oficina
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosMantenimiento" class="inputVar sum1" value="0.00" tabindex="2"/>
                </div>

                <div class="span4 bold">
                    Gastos Generales (subtotal)
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceGastosGenerales" class="inputVar sum2" value="0.00" disabled=""/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Administrativos
                </div>

                <div class="span2">
                    <g:textField type="text" name="administracion" class="inputVar sum1" value="0.00" tabindex="3"/>
                </div>

                <div class="span4 bold">
                    Imprevistos
                </div>

                <div class="span2">
                    <g:textField type="text" name="impreso" class="inputVar  sum2" value="0.00" tabindex="8"/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Garantías
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosGarantias" class="inputVar sum1" value="0.00" tabindex="4"/>
                </div>

                <div class="span4 bold">
                    Utilidad
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceUtilidad" class="inputVar sum2 " value="0.00" tabindex="9"/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Costos financieros
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosCostosFinancieros" class="inputVar sum1" value="0.00" tabindex="5"/>
                </div>

                <div class="span4 bold">
                    Timbres provinciales
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosTimbresProvinciales" class="inputVar sum2" value="0.00" tabindex="10"/>
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Vehículos
                </div>

                <div class="span2">
                    <g:textField type="text" name="indiceCostosIndirectosVehiculos" class="inputVar sum1" value="0.00" tabindex="6"/>
                </div>

                <div class="span4 bold" style="border-top: solid 1px #D3D3D3;">
                    Total Costos Indirectos
                </div>

                <div class="span2">
                    <g:textField type="text" name="totales" class="inputVar" value="0.00" disabled=""/>
                </div>
            </div>

        </div>
    </div>
</g:form>

<script type="text/javascript">

    $(".sum1, .sum2").keydown(function (ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         190        -> . teclado
         110        -> . teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         */
//        console.log(ev.keyCode);
        if ((ev.keyCode >= 48 && ev.keyCode <= 57) || (ev.keyCode >= 96 && ev.keyCode <= 105) || ev.keyCode == 190 || ev.keyCode == 110 || ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9) {
            return true;
        } else {
            return false;
        }
    });

    function suma(items, update) {
        var sum1 = 0;
        items.each(function () {
            var val = parseFloat($(this).val());
            sum1 += val;
        });
        update.val(number_format(sum1, 2, ".", ""));
    }

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

    $("#tabs").tabs({
        heightStyle : "fill"
    });

    $("#cmb_vol").change(function () {
        if ($("#cmb_vol").val() != "-1") {
            $.ajax({
                type    : "POST",
                url     : "${g.createLink(controller: 'rubro',action:'getPreciosTransporte')}",
                data    : {
                    fecha  : $("#fechaLista").val(),
                    ciudad : $("#lista").val(),
                    ids    : $("#cmb_vol").val()
                },
                success : function (msg) {
                    var precios = msg.split("&")
//                    console.log(precios);
                    for (i = 0; i < precios.length; i++) {
                        if ($.trim(precios[i]) != "") {
                            var parts = precios[i].split(";")
//                        console.log(parts,parts.length)
                            if (parts.length > 1) {
//                                console.log($("#costo_volqueta"), parts, parts[1]);
//                                $("#costo_volqueta").after($("<span class='add-on'>" + parts[1] + "</span>"));
                                $("#costo_volqueta").val(parts[1].toString().trim());
                            }
                        }
                    }
                }
            });
        } else {
            $("#costo_volqueta").val("0.00")
        }
    });
    $("#cmb_chof").change(function () {
        if ($("#cmb_chof").val() != "-1") {
            var datos = "fecha=" + $("#fecha_precios").val() + "&ciudad=" + $("#ciudad").val() + "&ids=" + $("#cmb_chof").val()
            $.ajax({type : "POST", url : "${g.createLink(controller: 'rubro',action:'getPreciosTransporte')}",
                data     : {
                    fecha  : $("#fechaLista").val(),
                    ciudad : $("#lista").val(),
                    ids    : $("#cmb_chof").val()
                },
                success  : function (msg) {
                    var precios = msg.split("&")
                    for (i = 0; i < precios.length; i++) {
                        var parts = precios[i].split(";")
                        if (parts.length > 1)
                            $("#costo_chofer").val(parts[1].trim())
                    }
                }
            });
        } else {
            $("#costo_chofer").val("0.00")
        }
    });

</script>