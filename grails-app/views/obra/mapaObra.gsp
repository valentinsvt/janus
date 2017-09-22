<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 1/31/13
  Time: 11:21 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <link href='${resource(dir: "css", file: "print.css")}' rel='stylesheet' type='text/css' media="print"/>

        <script type="text/javascript"
                src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBpasnhIQUsHfgCvC3qeJpEgcB9_ppWQI0&sensor=true"></script>
        <style>

        #mapaPichincha img {
            max-width : none;;
        }

        .control-label {
            font-weight : bold;
        }

        .soloPrint {
            display : none;
        }
        </style>

        <title>Localización de la Obra</title>
    </head>

    <body>

        <div class="row hide" id="divError">
            <div class="span12 alert alert-error" id="spanError">
            </div>
        </div>

        <div class="datosObra span12" style="margin-bottom: 20px; width: 900px; text-align: center">
            <div style="margin-left: -50px; font-size: medium; width: 100%;">NOMBRE DE LA OBRA: ${obra?.nombre}</div>
        </div>

        <div>
            <div id="mapaPichincha" style="width: 900px; height: 500px; margin-left: 10px; float: left; margin-bottom: 20px;"></div>
        </div>

        <div style="float: left; width: 200px;" class="noprint">
            <div style="margin: 20px; margin-top: 80px;" class="noprint">
                <b>Nota:</b>

                <p>Si usa el botón "Imprimir", use la configuración de página definir la horientación del papel horizontal y
                una escala de 100% para cubrir toda la hoja en tamaño A4</p>

                <p>Se puede usar también la opción "Vista preliminar" desde el menú de Firefox: <span style="color: #000">Archivo -> Imprimir
                -> Vista preliminar</span>, para fijar la horientación del papel a horizontal y
                la escala que desee según sus requerimientos</p>
            </div>

        </div>

        <div class="noprint">

            <div style="margin-top: 40px; width: 900px;">
                <div style="margin: 0 0 0 20px;">
                    <span class="control-label label label-inverse">
                        Coordenadas Originales de la Obra:
                    </span>
                    <span style="margin-left: 20px;">${obra.coordenadas}</span>

                </div>

                <div style="margin: 20px;">
                    <span id="coordNuevas11" class="control-label label label-inverse">
                        Coordenadas Nuevas de la Obra:
                    </span>
                    <span style="margin-left: 34px; color: #008" id="divCoords">${obra.coordenadas}</span>
                </div>
            </div>
        </div>

        %{--<div style="float: left;" class="soloPrint">--}%
        <div style="width: 900px;" class="soloPrint">
            <div style="margin-top: 20px; width: 900px;">
                <span class="control-label ">
                    COORDENADAS DE LA OBRA:
                </span>

                ${obra.coordenadas}
                <br>
                <span class="control-label ">
                    CANTÓN:
                </span>
                ${obra.comunidad.parroquia.canton.nombre}

                <span class="control-label" style="margin-left: 50px;">
                    PARROQUIA:
                </span>
                ${obra.comunidad.parroquia.nombre}


                <span class="control-label" style="margin-left: 50px;">
                    COMUNIDAD:
                </span>
                ${obra.comunidad?.nombre}
            </div>
        </div>


        <div class="btn-group" style="margin-top: 10px; margin-left: 300px">
            <button class="btn noprint" id="btnVolver"><i class="icon-arrow-left"></i> Regresar</button>
            <g:if test="${obra?.liquidacion == 0}">
            <g:if test="${(obra?.responsableObra?.departamento?.direccion?.id == persona?.departamento?.direccion?.id && duenoObra == 1) || obra?.id == null }">
                <button class="btn noprint" id="btnGuardar"><i class="icon-check"></i> Guardar</button>
            </g:if>
            </g:if>
            <button class="btn noprint" id="btnImprimir"><i class="icon-print"></i> Imprimir</button>
        </div>


        <script type="text/javascript">
            //            window.onbeforeprint = preparar;
            //            window.onafterprint = despues;

            var map;
            var lat;
            var longitud;
            var latorigen;
            var longorigen;
            var lastValidCenter;
            //    var allowedBounds;

            var countryCenter = new google.maps.LatLng(-0.15, -78.35);

            var allowedBounds = new google.maps.LatLngBounds(
                    new google.maps.LatLng(-0.41, -79.56),
                    new google.maps.LatLng(-0.50, -76.44),
                    new google.maps.LatLng(-0.28690, -76.59190)
            );

            var marker = new google.maps.Marker({
                position  : countryCenter,
                draggable : true
            });

            function initialize() {

                var latitudObra = ${lat};
                var longitudObra = ${lng};

                $("#divCoords").data("coords", "${obra.coordenadas}");

                var myOptions = {
                    center             : countryCenter,
                    zoom               : 7,
                    maxZoom            : 16,
                    minZoom            : 8,
                    panControl         : false,
                    zoomControl        : true,
                    mapTypeControl     : false,
                    scaleControl       : false,
                    streetViewControl  : false,
                    overviewMapControl : false,

                    mapTypeId : google.maps.MapTypeId.ROADMAP //SATELLITE, ROADMAP, HYBRID, TERRAIN
                };

                map = new google.maps.Map(document.getElementById('mapaPichincha'), myOptions);
                limites2();

//                var kmzLayer = new google.maps.KmlLayer("http://www.nth-development.com/fine/Vias_Principales.kmz");
//                var kmzLayer = new google.maps.KmlLayer("http://www.tedein.com.ec/archivos/Vias_Principales.kmz");
//                var kmzLayer = new google.maps.KmlLayer("http://www.tedein.com.ec/archivos/Parroquias_Pichincha.kmz");
                var kmzLayerVias = new google.maps.KmlLayer("http://www.tedein.com.ec/archivos/vias_ok.kmz");
//                var kmzLayer = new google.maps.KmlLayer("http://www.tedein.com.ec/archivos/parroquias_25_prueba.kmz");
                var kmzLayer = new google.maps.KmlLayer("http://www.tedein.com.ec/archivos/parroquias.kmz");
                kmzLayerVias.setMap(map);
                kmzLayer.setMap(map);
//        limites();

                var posicion;
                if (latitudObra == 0 || longitudObra == 0) {
//            ////console.log("entro")
                    posicion = new google.maps.LatLng(-0.21, -78.52)
                } else {
                    posicion = new google.maps.LatLng(latitudObra, longitudObra)
                }
                var marker2 = new google.maps.Marker({
                    map       : map,
//            position: new google.maps.LatLng(-0.21, -78.52),
//            position: new google.maps.LatLng(latitudObra, longitudObra),
                    position  : posicion,
                    draggable : true
                });

                google.maps.event.addListener(marker2, 'drag', function (event) {
                    var latlng = marker2.getPosition();

                    var coords = "";

                    lat = latlng.lat();
                    longitud = latlng.lng();

                    if (lat >= 0) {
                        coords += "N ";
                    } else {
                        coords += "S "
                    }
                    var pa = lat.toString().split(".");
                    var ng = Math.abs(parseFloat(pa[0]));
                    var nm = (Math.abs(lat) - ng) * 60;

                    coords += ng + " " + nm + " ";

                    if (longitud >= 0) {
                        coords += "E ";
                    } else {
                        coords += "W "
                    }
                    var pn = longitud.toString().split(".");
                    var eg = Math.abs(parseFloat(pn[0]));
                    var em = (Math.abs(longitud) - eg) * 60;

                    coords += eg + " " + em + " ";
                    $("#divCoords").text(coords).data("coords", coords);

//            $("#latitud").val(lat);
//                    $("#latitud").val(number_format(lat, 8, ".", ","));
//                    $("#longitud").val(number_format(longitud, 8, ".", ","));
//            $("#longitud").val(longitud);
                });

                google.maps.event.addListenerOnce(marker2, 'dragstart', function () {

                    var posicion = marker2.getPosition();

                    latorigen = posicion.lat();
                    longorigen = posicion.lng();

//            $("#lato").val(latorigen);
                    $("#lato").val(number_format(latorigen, 5, ".", ","));
                    $("#longo").val(number_format(longorigen, 5, ".", ","));
//            $("#longo").val(longorigen);

                });
//        var paths = [new google.maps.LatLng(0.3173,-79.26 ),
//            new google.maps.LatLng(0.169 ,-77.96 ),
//            new google.maps.LatLng(-0.06,-77.31 )];
//
//        var shape = new google.maps.Polygon({
//            paths: paths,
//            strokeColor: '#ff0000',
//            strokeOpacity: 0.8,
//            strokeWeight: 2,
//            fillColor: '#ff0000',
//            fillOpacity: 0.35
//        });
//
//        shape.setMap(map);

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
                        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
                        ev.keyCode == 37 || ev.keyCode == 39);
            }

            //            $("#latitud").bind({
            //                keydown : function (ev) {
            //                    // esta parte valida el punto: si empieza con punto le pone un 0 delante, si ya hay un punto lo ignora
            //                    if (ev.keyCode == 190 || ev.keyCode == 110) {
            //                        var val = $(this).val();
            //                        if (val.length == 0) {
            //                            $(this).val("0");
            //                        }
            //                        return val.indexOf(".") == -1;
            //                    } else {
            //                        // esta parte valida q sean solo numeros, punto, tab, backspace, delete o flechas izq/der
            //                        return validarNum(ev);
            //                    }
            //                }, //keydown
            //                keyup   : function () {
            //                    var val = $(this).val();
            //                    // esta parte valida q no ingrese mas de 2 decimales
            //                    var parts = val.split(".");
            //                    if (parts.length > 1) {
            //                        if (parts[1].length > 5) {
            //                            parts[1] = parts[1].substring(0, 5);
            //                            val = parts[0] + "." + parts[1];
            //                            $(this).val(val);
            //                        }
            //                    }
            //                }
            //            });
            //
            //            $("#longitud").bind({
            //                keydown : function (ev) {
            //                    // esta parte valida el punto: si empieza con punto le pone un 0 delante, si ya hay un punto lo ignora
            //                    if (ev.keyCode == 190 || ev.keyCode == 110) {
            //                        var val = $(this).val();
            //                        if (val.length == 0) {
            //                            $(this).val("0");
            //                        }
            //                        return val.indexOf(".") == -1;
            //                    } else {
            //                        // esta parte valida q sean solo numeros, punto, tab, backspace, delete o flechas izq/der
            //                        return validarNum(ev);
            //                    }
            //                }, //keydown
            //                keyup   : function () {
            //                    var val = $(this).val();
            //                    // esta parte valida q no ingrese mas de 2 decimales
            //                    var parts = val.split(".");
            //                    if (parts.length > 1) {
            //                        if (parts[1].length > 5) {
            //                            parts[1] = parts[1].substring(0, 5);
            //                            val = parts[0] + "." + parts[1];
            //                            $(this).val(val);
            //                        }
            //                    }
            //                }
            //            });

            function limites() {

                google.maps.event.addListener(map, 'center_changed', function () {

                    if (allowedBounds.contains(map.getCenter())) {
                        lastValidCenter = map.getCenter();
                        return
                    }
                    map.panTo(lastValidCenter);
                });
            }

            function limites2() {

                google.maps.event.addListenerOnce(map, 'idle', function () {
                    allowedBounds = map.getBounds();
                });
                google.maps.event.addListener(map, 'drag', function () {
                    checkBounds();
                });

                function checkBounds() {
                    if (!allowedBounds.contains(map.getCenter())) {
                        var C = map.getCenter();
                        var X = C.lng();
                        var Y = C.lat();
                        var AmaxX = allowedBounds.getNorthEast().lng();
                        var AmaxY = allowedBounds.getNorthEast().lat();
                        var AminX = allowedBounds.getSouthWest().lng();
                        var AminY = allowedBounds.getSouthWest().lat();
                        if (X < AminX) {
                            X = AminX;
                        }
                        if (X > AmaxX) {
                            X = AmaxX;
                        }
                        if (Y < AminY) {
                            Y = AminY;
                        }
                        if (Y > AmaxY) {
                            Y = AmaxY;
                        }
                        map.panTo(new google.maps.LatLng(Y, X));
                    }
                }
            }

            $(function () {
                initialize();
            });

            $("#btnVolver").click(function () {
                %{--location.href="${createLink(action: 'registroObra')}";--}%
                location.href = "${g.createLink(controller: 'obra', action: 'registroObra')}" + "?obra=" + "${obra?.id}";
            });

            $("#btnGuardar").click(function () {
                $.ajax({
                    type    : "POST",
                    url     : "${createLink(action:'saveCoords')}",
                    data    : {
                        id     : "${obra.id}",
                        coords : $("#divCoords").data("coords")
                    },
                    success : function (msg) {
                        if (msg == "OK") {
                            location.href = "${createLink(action:'registroObra', params: [obra: obra.id])}";
                        } else {
                            $("#spanError").html("Ha ocurrido un error al guardar las coordenadas de la obra.").show();
                            $("#divError").show();
                        }
                    }
                });
            });

            $("#btnImprimir").click(function () {
                window.print()
            });


        </script>

    </body>
</html>