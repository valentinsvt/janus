<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="utf-8">
        <title>
            <g:layoutTitle default="${g.message(code: 'default.app.name', default: 'Janus')}"/>
        </title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="">

        %{--<script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.8.2.js')}"></script>--}%
        %{--<script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.9.1.custom.min.js')}"></script>--}%
        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.9.1.js')}"></script>
        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.10.2.custom.min.js')}"></script>

        %{--<g:if test="${janus.Parametros.findByEmpresaLike('CNSL-GADPP')}">--}%
        <g:if test="${janus.Parametros.findByEmpresaLike(message(code: 'ambiente2'))}">
            <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrapV2.css')}" rel="stylesheet">
        </g:if>
        <g:else>
            <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap.css')}" rel="stylesheet">
        </g:else>
        <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap-responsive.css')}" rel="stylesheet">

        %{--<link href="${resource(dir: 'js/jquery/css/twitBoot', file: 'jquery-ui-1.9.1.custom.min.css')}" rel="stylesheet">--}%
        <link href="${resource(dir: 'js/jquery/css/bw', file: 'jquery-ui-1.10.2.custom.min.css')}" rel="stylesheet">

      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>

        <link rel="shortcut icon" href="${resource(dir: 'images/ico', file: 'janus_16.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="${resource(dir: 'images/ico', file: 'janus_144.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="${resource(dir: 'images/ico', file: 'janus_114.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="${resource(dir: 'images/ico', file: 'janus_72.png')}">
        <link rel="apple-touch-icon-precomposed" href="${resource(dir: 'images/ico', file: 'janus_57.png')}">
        <link href='${resource(dir: "font/open", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
        <link href='${resource(dir: "font/tulpen", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
        <g:layoutHead/>
    </head>

    <body style="background: rgba(0,0,0,0.15)">

        <div style="width: 1000px;margin-top: 50px;margin-left: 30px;">
            <g:layoutBody/>
        </div>

        <script src="${resource(dir: 'css/bootstrap/js', file: 'bootstrap.js')}"></script>
        <script type="text/javascript">
            %{--var url = "${resource(dir:'images', file:'spinner_24.gif')}";--}%
            var url = "${resource(dir:'images', file:'spinnerLogin_24.gif')}";
            var spinner = $("<img style='margin-left:15px;' src='" + url + "' alt='Cargando...'/>");
            var urlLogin = "${resource(dir:'images', file:'spinnerLogin_24.gif')}";
            var spinnerLogin = $("<img style='margin-left:15px;' src='" + urlLogin + "' alt='Cargando...'/>");
        </script>
    </body>
</html>
