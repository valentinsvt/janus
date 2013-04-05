<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <title>
            <g:layoutTitle default="${g.message(code: 'default.app.name')}"/>
        </title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        %{--<script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.8.2.js')}"></script>--}%
        %{--<script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.9.1.custom.min.js')}"></script>--}%
        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.9.1.js')}"></script>
        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.10.2.custom.min.js')}"></script>

        <script src="${resource(dir: 'js/jquery/plugins/jquery.countdown', file: 'jquery.countdown.min.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery.countdown', file: 'jquery.countdown-es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins', file: 'date.js')}"></script>

        %{--Fuentes--}%
        <link href='${resource(dir: "font/open", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
        <link href='${resource(dir: "font/tulpen", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>

        <!-- Le styles -->
        <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap.css')}" rel="stylesheet">


        <link href="${resource(dir: 'css', file: 'mobile2.css')}" rel="stylesheet">
        <link href="${resource(dir: 'css', file: 'custom.css')}" rel="stylesheet">
        <style>

        .hasCountdown {
            background : none !important;
            border     : none !important;
        }

        .countdown_amount {
            font-size : 150% !important;
        }

        .highlight {
            color : red !important;
        }
        </style>
        %{--<link href="${resource(dir: 'js/jquery/css/twitBoot', file: 'jquery-ui-1.9.1.custom.min.css')}" rel="stylesheet">--}%
        <link href="${resource(dir: 'js/jquery/css/bw', file: 'jquery-ui-1.10.2.custom.min.css')}" rel="stylesheet">

        <link href="${resource(dir: 'js/jquery/plugins/jquery.countdown', file: 'jquery.countdown.css')}" rel="stylesheet">


        <!-- Le fav and touch icons -->
        <link rel="shortcut icon" href="${resource(dir: 'images/ico', file: 'janus_16.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="${resource(dir: 'images/ico', file: 'janus_144.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="${resource(dir: 'images/ico', file: 'janus_114.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="${resource(dir: 'images/ico', file: 'janus_72.png')}">
        <link rel="apple-touch-icon-precomposed" href="${resource(dir: 'images/ico', file: 'janus_57.png')}">

        <script src="${resource(dir: 'js', file: 'functions.js')}"></script>
        <g:layoutHead/>
    </head>

    <body>

        <mn:menu title="${g.layoutTitle(default: g.message(code: 'default.app.name'))}"/>


        <div class="container">

            <g:layoutBody/>

        </div> <!-- /container -->

        <script type="text/javascript">
            var ot = document.title;

            function resetTimer() {
                var ahora = new Date();
                var fin = ahora.clone().add(20).minute();
                $("#countdown").countdown('change', {
                    until : fin
                });
                $(".countdown_amount").removeClass("highlight");
                document.title = ot;
            }

            function validarSesion() {
                $.ajax({
                    url     : '${createLink(controller: "login")}',
                    success : function (msg) {
                        if (msg == "NO") {
                            location.href = "${g.createLink(controller: 'login', action: 'login')}";
                        } else {
                            resetTimer();
                        }
                    }
                });
            }

            function highlight(periods) {
                if ((periods[5] == 5 && periods[6] == 0) || (periods[5] < 5)) {
                    document.title = "Fin de sesión en " + (periods[5].toString().lpad('0', 2)) + ":" + (periods[6].toString().lpad('0', 2)) + " - " + ot;
                    $(".countdown_amount").addClass("highlight");
                }
            }

            $(function () {
                var ahora = new Date();
                var fin = ahora.clone().add(20).minute();

                $('#countdown').countdown({
                    until    : fin,
                    format   : 'MS',
                    compact  : true,
                    onExpiry : validarSesion,
                    onTick   : highlight
                });

                $(".btn-ajax").click(function () {
                    resetTimer();
                });
            });
        </script>
    </body>
</html>
