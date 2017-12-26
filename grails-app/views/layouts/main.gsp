<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <title>
            <g:layoutTitle default="${g.message(code: 'default.app.name')}"/>
        </title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="tedein">

        <elm:favicon/>
        %{--<script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.8.2.js')}"></script>--}%
        %{--<script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.9.1.custom.min.js')}"></script>--}%
        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.9.1.js')}"></script>
        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.10.2.custom.min.js')}"></script>



        %{--<script src="${resource(dir: 'js/jquery/plugins/jquery.countdown', file: 'jquery.countdown.min.js')}"></script>--}%
        <script src="${resource(dir: 'js/jquery/plugins/jquery.countdown', file: 'jquery.countdown.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/jquery.countdown', file: 'jquery.countdown-es.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins', file: 'date.js')}"></script>
        <script src="${resource(dir: 'js/jquery/plugins/paginate/js', file: 'jquery.luz.paginate.js')}"></script>

        <script src="${resource(dir: 'js/jquery/i18n', file: 'jquery.ui.datepicker-es.js')}"></script>

        %{--Fuentes--}%
        <link href='${resource(dir: "font/open", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
        <link href='${resource(dir: "font/tulpen", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>

        <!-- Le styles -->
        <g:if test="${janus.Parametros.findByEmpresaLike(message(code: 'ambiente2'))}">
            <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrapV2.css')}" rel="stylesheet">
        </g:if>
        <g:else>
            <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap.css')}" rel="stylesheet">
        </g:else>
        %{--<link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap.readable.css')}" rel="stylesheet">--}%

        <link href="${resource(dir: 'fontawsome/css', file: 'font-awesome.css')}" rel="stylesheet">

        <link href="${resource(dir: 'css', file: 'mobile2.css')}" rel="stylesheet">
        <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.highlight.js')}"></script>
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

        .container {
            width     : 1200px !important;
            min-width : 1200px !important;
            max-width : 1200px !important;
            resize    : none;

        }

        @media (min-width: 1200px)
        </style>
        %{--<link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap-responsive.css')}" rel="stylesheet">--}%

        %{--<link href="${resource(dir: 'js/jquery/css/twitBoot', file: 'jquery-ui-1.9.1.custom.min.css')}" rel="stylesheet">--}%
        <link href="${resource(dir: 'js/jquery/css/bw', file: 'jquery-ui-1.10.2.custom.min.css')}" rel="stylesheet">

        <link href="${resource(dir: 'js/jquery/plugins/jquery.countdown', file: 'jquery.countdown.css')}" rel="stylesheet">

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

        <link rel="shortcut icon" href="${resource(dir: 'images/ico', file: 'janus_16.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="${resource(dir: 'images/ico', file: 'janus_144.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="${resource(dir: 'images/ico', file: 'janus_114.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="${resource(dir: 'images/ico', file: 'janus_72.png')}">
        <link rel="apple-touch-icon-precomposed" href="${resource(dir: 'images/ico', file: 'janus_57.png')}">

        <script src="${resource(dir: 'js', file: 'functions.js')}"></script>
        <g:layoutHead/>

        <g:if test="${janus.Parametros.findByEmpresaLike(message(code: 'ambiente2'))}">
            <link href="${resource(dir: 'css', file: 'customV2.css')}" rel="stylesheet">
        </g:if>
        <g:else>
            <link href="${resource(dir: 'css', file: 'custom.css')}" rel="stylesheet">
        </g:else>

        <link href="${resource(dir: 'css', file: 'customButtons.css')}" rel="stylesheet">
    </head>

    <body>

        <mn:menu title="${g.layoutTitle(default: g.message(code: 'default.app.name'))}"/>

        <script type="text/javascript">
            var url = "${resource(dir:'images', file:'spinner_24.gif')}";
            %{--var url = "${resource(dir:'images', file:'spinnerLogin_24.gif')}";--}%
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");
            var urlBg = "${resource(dir:'images', file:'spinner64.gif')}";
            var spinnerBg = $("<img src='" + url + "' alt='Cargando...'/>");
            var url2 = "${resource(dir:'images', file:'spinner.gif')}";
            var spinner2 = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");
            var urlLogin = "${resource(dir:'images', file:'spinnerLogin_24.gif')}";
            var spinnerLogin = $("<img style='margin-left:15px;' src='" + urlLogin + "' alt='Cargando...'/>");

            var ot = document.title;

//            function resetTimer() {
//                var ahora = new Date();
//                var fin = ahora.clone().add(20).minute();
//                $("#countdown").countdown('change', {
//                    until : fin
//                });
//                $(".countdown_amount").removeClass("highlight");
//                document.title = ot;
//            }
            function resetTimer() {
                var ahora = new Date();
                var fin = ahora.clone().add(20).minute();
//                fin.add(1).second()
                $("#countdown").countdown('option', {
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

                $("#dlgLoad").dialog({
                    modal         : true,
                    autoOpen      : false,
                    closeOnEscape : false,
                    draggable     : false,
                    resizable     : false,
                    zIndex        : 9000,
                    open          : function (event, ui) {
                        $(event.target).parent().find(".ui-dialog-titlebar-close").remove();
                    }
                });

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

                $("#ingresar").click(function () {
                    $("#modal-ingreso").modal("show");
                });
            });
        </script>

        <div id="dlgLoad" class="ui-helper-hidden" title="CARGANDO..." style="text-align:center;">
            Cargando.....Por favor espere......<br/><br/>
            <img src="${resource(dir: 'images', file: 'spinner64.gif')}" alt=""/>
        </div>

        <div class="container principal">

            <g:layoutBody/>

        </div>


        <script src="${resource(dir: 'css/bootstrap/js', file: 'bootstrap.js')}"></script>

    </body>
</html>
