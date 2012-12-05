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
</style>

<g:form controller="variables" action="saveVar_ajax">
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
                    <g:select name="cmb_vol" from="${volquetes}" optionKey="id" optionValue="nombre" noSelection="${['-1': 'Seleccione']}"/>
                </div>

                <div class="span1">
                    Costo
                </div>

                <div class="span2">
                    %{--<div class="input-append">--}%
                    <input type="text" class="inputVar" style="" disabled="" id="costo_volqueta"/>
                    %{--<span class="add-on">$</span>--}%
                    %{--</div>--}%
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Chofer
                </div>

                <div class="span5">
                    <g:select name="cmb_chof" from="${choferes}" optionKey="id" optionValue="nombre" noSelection="${['-1': 'Seleccione']}"/>
                </div>

                <div class="span1">
                    Costo
                </div>

                <div class="span2">
                    %{--<div class="input-append">--}%
                    <input type="text" class="inputVar" id="costo_chofer" style="text-align: right" disabled=""/>
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
                    <input type="text" id="dist_peso" class="inputVar" value="0.00"/>
                    %{--<span class="add-on">km</span>--}%
                    %{--</div>--}%
                </div>

                <div class="span3">
                    Distancia volumen
                </div>

                <div class="span1">
                    %{--<div class="input-append">--}%
                    <input type="text" id="dist_vol" class="inputVar" value="0.00"/>
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
                    <input type="text" id="factorReduccion" class="inputVar" value="0.00">
                </div>

                <div class="span3">
                    Velocidad
                </div>

                <div class="span3">
                    <input type="text" id="velocidad" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Capacidad Volquete
                </div>

                <div class="span3">
                    <input type="text" id="capacidadVolquete" class="inputVar" value="0.00">
                </div>

                <div class="span3">
                    Reducción/Tiempo
                </div>

                <div class="span3">
                    <input type="text" id="reduccion" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Factor Volumen
                </div>

                <div class="span3">
                    <input type="text" id="factorVolumen" class="inputVar" value="0.00">
                </div>

                <div class="span3">
                    Distancia
                </div>

                <div class="span3">
                    <input type="text" id="distancia" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span3">
                    Factor Peso
                </div>

                <div class="span3">
                    <input type="text" id="factorPeso" class="inputVar" value="0.00">
                </div>
            </div>
        </div>

        <div id="tab-indirecto" class="tab">
            <div class="row-fluid">
                <div class="span10">
                    Control y Administración (Fiscalización) - no se usa en obras nuevas
                </div>

                <div class="span2">
                    <input type="text" id="control" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Dirección de obra
                </div>

                <div class="span2">
                    <input type="text" id="direccionObra" class="inputVar" value="0.00">
                </div>

                <div class="span4">
                    Promoción
                </div>

                <div class="span2">
                    <input type="text" id="promoción" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Mantenimiento y gastos de oficina
                </div>

                <div class="span2">
                    <input type="text" id="mantenimiento" class="inputVar" value="0.00">
                </div>

                <div class="span4 bold">
                    Gastos Generales (subtotal)
                </div>

                <div class="span2">
                    <input type="text" id="gastos" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Administrativos
                </div>

                <div class="span2">
                    <input type="text" id="administrativos" class="inputVar" value="0.00">
                </div>

                <div class="span4 bold">
                    Imprevistos
                </div>

                <div class="span2">
                    <input type="text" id="imprevistos" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Garantías
                </div>

                <div class="span2">
                    <input type="text" id="garantias" class="inputVar" value="0.00">
                </div>

                <div class="span4 bold">
                    Utilidad
                </div>

                <div class="span2">
                    <input type="text" id="utilidad" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Costos financieros
                </div>

                <div class="span2">
                    <input type="text" id="costosFinancieros" class="inputVar" value="0.00">
                </div>

                <div class="span4 bold">
                    Timbres provinciales
                </div>

                <div class="span2">
                    <input type="text" id="timbresProvinciales" class="inputVar" value="0.00">
                </div>
            </div>

            <div class="row-fluid">
                <div class="span4">
                    Vehículos
                </div>

                <div class="span2">
                    <input type="text" id="vehiculos" class="inputVar" value="0.00">
                </div>

                <div class="span4 bold" style="border-top: solid 1px #D3D3D3;">
                    Total Costos Indirectos
                </div>

                <div class="span2">
                    <input type="text" id="totalIndirectos" class="inputVar" value="0.00">
                </div>
            </div>

        </div>
    </div>
</g:form>

<script type="text/javascript">
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
                    console.log(precios);
                    for (i = 0; i < precios.length; i++) {
                        if ($.trim(precios[i]) != "") {
                            var parts = precios[i].split(";")
//                        console.log(parts,parts.length)
                            if (parts.length > 1) {
                                console.log($("#costo_volqueta"), parts, parts[1]);
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