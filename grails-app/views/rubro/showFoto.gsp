<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Foto - ${rubro?.nombre}</title>

    <script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.8.2.js')}"></script>
    <script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.9.1.custom.min.js')}"></script>
    <link href='${resource(dir: "font/open", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
    <link href='${resource(dir: "font/tulpen", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
    <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap.css')}" rel="stylesheet">

    <link href="${resource(dir: 'css', file: 'font-awesome.css')}" rel="stylesheet">

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
    .container{
        width: 1200px !important;
        min-width: 1200px !important;
        max-width: 1200px !important;
        resize: none;

    }
    @media (min-width: 1200px)
    </style>
    %{--<link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap-responsive.css')}" rel="stylesheet">--}%

    <link href="${resource(dir: 'js/jquery/css/twitBoot', file: 'jquery-ui-1.9.1.custom.min.css')}" rel="stylesheet">


    <script src="${resource(dir: 'js', file: 'functions.js')}"></script>
    <link href="${resource(dir: 'css', file: 'custom.css')}" rel="stylesheet">
    <link href="${resource(dir: 'css', file: 'customButtons.css')}" rel="stylesheet">
</head>
<body style="padding: 20px;">
<g:if test="${flash.message}">
    <div class="row">
        <div class="span12">
            <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
                <a class="close" data-dismiss="alert" href="#">×</a>
                ${flash.message}
            </div>
        </div>
    </div>
</g:if>
<div class="tituloTree" style="width: 700px;">
    Rubro: ${rubro?.nombre}
</div>
<fieldset class="borde_abajo" style="position: relative;width: 650px;padding-left: 50px;"  >
    <div class="linea" style="height: 98%;"></div>
    <g:uploadForm action="uploadFile" method="post" name="frmUpload" enctype="multipart/form-data">

        <div class="fieldcontain required">
            <b>Archivo:</b>
            <input type="file" id="file" name="file" class=""/>
            <input type="hidden" name="rubro" value="${rubro?.id}">
            <input type="submit" value="Guardar" class="btn btn-primary" >
            <a href="#" id="salir" class="btn btn-danger" style="float: right">Salir</a>
        </div>


    </g:uploadForm>
</fieldset>
<fieldset style="width: 650px;min-height: 500px;margin: 10px;margin-left: 0px;position: relative;padding-left: 50px;" class="borde_abajo">
    <p class="css-vertical-text">Foto</p>
    <div class="linea" style="height: 98%;"></div>
    <img src="${rubro?.foto}" alt=""  style="margin-bottom: 10px;max-width: 600px">
</fieldset>
<script type="text/javascript">
    $("#salir").click(function(){
        window.close()
    })
</script>

</body>
</html>