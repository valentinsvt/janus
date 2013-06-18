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

        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.9.1.js')}"></script>
        <script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.10.2.custom.min.js')}"></script>

        <!-- Le styles -->
        <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap.css')}" rel="stylesheet">

        <link href="${resource(dir: 'css', file: 'font-awesome.css')}" rel="stylesheet">


        <link rel="stylesheet" type="text/css" href="${resource(dir: 'css', file: 'timeline.css')}"/>
        <script type="text/javascript" src="${resource(dir: 'js', file: 'timeline.js')}"></script>

        <link href="${resource(dir: 'js/jquery/css/bw', file: 'jquery-ui-1.10.2.custom.min.css')}" rel="stylesheet">

        <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

        <link rel="shortcut icon" href="${resource(dir: 'images/ico', file: 'janus_16.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="144x144" href="${resource(dir: 'images/ico', file: 'janus_144.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="114x114" href="${resource(dir: 'images/ico', file: 'janus_114.png')}">
        <link rel="apple-touch-icon-precomposed" sizes="72x72" href="${resource(dir: 'images/ico', file: 'janus_72.png')}">
        <link rel="apple-touch-icon-precomposed" href="${resource(dir: 'images/ico', file: 'janus_57.png')}">

        <g:layoutHead/>

    </head>

    <body>

        %{--<div class="container principal">--}%

        <g:layoutBody/>

        %{--</div>--}%


        <script src="${resource(dir: 'css/bootstrap/js', file: 'bootstrap.js')}"></script>

    </body>
</html>
