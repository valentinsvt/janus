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

        </style>


        <title>Localización Geográfica de la Obra</title>
    </head>

    <body>

        <div id="mapaPichincha" style="width: 700px; height: 500px">

        </div>

        <script type="text/javascript">

            var map;
            var lat;
            var longitud;
            var latorigen;
            var longorigen;
            var lastValidCenter;
            //    var allowedBounds;

            var countryCenter = new google.maps.LatLng(-0.15, -78.35);

            function initialize() {

//                var myOptions = {
//                    center    : new google.maps.LatLng(58.33, -98.52),
//                    zoom      : 11,
//                    mapTypeId : google.maps.MapTypeId.ROADMAP
//                };
//                var map = new google.maps.Map(document.getElementById("mapaPichincha"), myOptions);
//                var kmzLayer = new google.maps.KmlLayer('http://xeenat.com/energy/data.kmz');
//                kmzLayer.setMap(map);

                var myOptions = {

                    center  : countryCenter,
                    zoom    : 11,
                    maxZoom : 16,
                    minZoom : 1
                    //                    panControl         : false,
                    //                    zoomControl        : true,
                    //                    mapTypeControl     : false,
                    //                    scaleControl       : false,
                    //                    streetViewControl  : false,
                    //                    overviewMapControl : false,

//                mapTypeId : google.maps.MapTypeId.ROADMAP //SATELLITE, ROADMAP, HYBRID, TERRAIN
                };

                map = new google.maps.Map(document.getElementById('mapaPichincha'), myOptions);

                var kmzLayer = new google.maps.KmlLayer("http://www.nth-development.com/fine/Vias_Principales.kmz");
                kmzLayer.setMap(map);

            }

            $(function () {

                initialize();

            });
        </script>

    </body>
</html>