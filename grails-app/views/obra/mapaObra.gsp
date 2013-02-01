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

    <script type="text/javascript"
            src="http://maps.googleapis.com/maps/api/js?key=AIzaSyBpasnhIQUsHfgCvC3qeJpEgcB9_ppWQI0&sensor=true"></script>

    <style>

    #mapaPichincha img {

        max-width: none;;
    }

    </style>


    <title>Localización Geográfica de la Obra</title>
</head>
<body>



<div id="mapaPichincha" style="width: 700px; height: 500px">



</div>


<div class="" style="margin-top: 20px">


    <div class="coordenadasOriginales">

        <span>Coordenadas Originales de la Obra:</span>
        <input class="span2" id="lato">
        <input class="span2" id="longo">


    </div>

    <div class="coordenadas">
        <span>Coordenadas Nuevas de la Obra:   </span>
        <input class="span2" id="lati">
        <input class="span2" id="longi">

    </div>

</div>

<div class="btn-group" style="margin-top: 20px; margin-left: 250px">

    <button class="btn" id="btnVolver"><i class="icon-arrow-left"></i> Regresar</button>
    <button class="btn" id="btnGuardar"><i class="icon-check"></i> Guardar</button>

</div>


<script type="text/javascript">

    var map;
    var lat;
    var longitud;
    var latorigen;
    var longorigen;
    var lastValidCenter;

    var countryCenter = new google.maps.LatLng(-0.15, -78.35);

    var allowedBounds = new google.maps.LatLngBounds (

            new google.maps.LatLng(-0.41, -80.56),
            new google.maps.LatLng(-0.50,-76.44)

    );

    var marker = new google.maps.Marker ( {
        position:countryCenter,
        draggable:true

    });



    function initialize() {

        %{--var latitudObra = ${obra?.latitud};--}%

        %{--var longitudObra = ${obra?.longitud};--}%

        var myOptions = {

            center:countryCenter,
            zoom:7,
            maxZoom:16,
            minZoom:8,
            panControl:false,
            zoomControl:true,
            mapTypeControl:false,
            scaleControl:false,
            streetViewControl:false,
            overviewMapControl:false,

            mapTypeId:google.maps.MapTypeId.ROADMAP //SATELLITE, ROADMAP, HYBRID, TERRAIN
        };

        map = new google.maps.Map(document.getElementById('mapaPichincha'), myOptions);

        limites2();

        var marker2 = new google.maps.Marker({
            map: map,
            position: new google.maps.LatLng(-0.21, -78.52),
//            position: new google.maps.LatLng(latitudObra, longitudObra),
            draggable: true
        });

        google.maps.event.addListener(marker2, 'drag',function (event)  {

            var latlng = marker2.getPosition();

            lat = latlng.lat();
            longitud= latlng.lng();

            $("#lati").val(lat);
            $("#longi").val(longitud);


        });

        google.maps.event.addListenerOnce(marker2, 'dragstart', function () {

            var posicion = marker2.getPosition();

            latorigen = posicion.lat();
            longorigen = posicion.lng();

            $("#lato").val(latorigen);
            $("#longo").val(longorigen);


        });


    }

    function limites2 () {

        google.maps.event.addListenerOnce(map,'idle',function() {
            allowedBounds = map.getBounds();
        });
        google.maps.event.addListener(map,'drag',function() {
            checkBounds();
        });

        function checkBounds() {
            if(! allowedBounds.contains(map.getCenter()))
            {
                var C = map.getCenter();
                var X = C.lng();
                var Y = C.lat();
                var AmaxX = allowedBounds.getNorthEast().lng();
                var AmaxY = allowedBounds.getNorthEast().lat();
                var AminX = allowedBounds.getSouthWest().lng();
                var AminY = allowedBounds.getSouthWest().lat();
                if (X < AminX) {X = AminX;}
                if (X > AmaxX) {X = AmaxX;}
                if (Y < AminY) {Y = AminY;}
                if (Y > AmaxY) {Y = AmaxY;}
                map.panTo(new google.maps.LatLng(Y,X));
            }
        }

    }


    $(function () {

        initialize();



    });


    $("#btnVolver").click(function () {

        location.href="${createLink(action: 'registroObra')}";


    });

</script>

</body>
</html>