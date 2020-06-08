<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>${titulo} - ${rubro?.nombre}</title>

    <script src="${resource(dir: 'js/jquery/js', file: 'jquery-1.8.2.js')}"></script>
    <script src="${resource(dir: 'js/jquery/js', file: 'jquery-ui-1.9.1.custom.min.js')}"></script>
    <link href='${resource(dir: "font/open", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
    <link href='${resource(dir: "font/tulpen", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
    <link href="${resource(dir: 'css/bootstrap/css', file: 'bootstrap.css')}" rel="stylesheet">

    <link href="${resource(dir: 'css', file: 'font-awesome.css')}" rel="stylesheet">

    <link href="${resource(dir: 'css', file: 'mobile2.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins', file: 'jquery.highlight.js')}"></script>

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
<div class="tituloTree" style="width: 720px;">
    Código: ${rubro?.codigo}<br/>Nombre: ${rubro?.nombre}
</div>
<fieldset class="borde_abajo" style="position: relative;width: 670px;padding-left: 50px;">
    <div class="linea" style="height: 98%;"></div>
    <p class="css-vertical-text">Archivo</p>
    <g:uploadForm action="uploadFile" method="post" name="frmUpload" enctype="multipart/form-data">
        <g:hiddenField name="tipo" value="${tipo}"/>
        <g:hiddenField name="rubro" value="${rubro?.id}"/>
        <div class="fieldcontain required">
            <b>Cargar archivo:</b>
            <input type="file" id="file" name="file" class=""/>
            <a href="#" id="submit" class="btn">
                <i class="icon-save"></i> Guardar
            </a>
            <a href="#" id="salir" class="btn ">
                <i class="icon-minus-sign"></i> Salir
            </a>
            <div class="btn-group" style="margin-top: 0px;">
                <g:if test="${filePath}">
                    <g:if test="${tipo == 'il'}">
                        <g:link action="downloadFile" id="${rubro.id}" params="[tipo: tipo]" class="btn btn-info">
                            <i class="icon-download-alt"></i> Descargar
                        </g:link>
                    </g:if>
                    <g:if test="${tipo == 'dt'}">
                        <g:link action="downloadFileAres" id="${ares}" params="[tipo: tipo, rubro: rubro.id]" class="btn btn-info">
                            <i class="icon-download-alt"></i> Descargar
                        </g:link>
                    </g:if>
                </g:if>
            </div>
        </div>
    </g:uploadForm>
    <g:if test="${!filePath}">
        <p style="color: #800">No se ha cargado ningún archivo para este rubro</p>
    </g:if>
    <g:else>
        Archivo cargado: <strong>${filePath}</strong>
        <g:if test="${session.perfil.codigo == 'CSTO'}">
            <g:if test="${tipo == 'il'}">
                <a href="#" name="botonB" class="btn btn-danger btnBorrar" data-id="${ares}" data-tipo="${tipo}" data-rubro="${rubro?.id}" style="float: right">
                    <i class="icon-delete"></i> Borrar
                </a>
            </g:if>
        </g:if>
    </g:else>
</fieldset>
<g:if test="${ext?.toLowerCase() == 'pdf'}">
    <div class="alert alert-info">
        <p>
            <i class="icon-info-sign icon-2x pull-left"></i>
            <p style="color: #000; font-size: 14px;">El archivo cargado para este rubro es un pdf. Por favor descárguelo para visualizarlo.</p>
        </p>
    </div>
</g:if>
<g:elseif test="${ext != ''}">
    <fieldset style="width: 650px;min-height: 500px;margin: 10px;margin-left: 0px;position: relative;padding-left: 50px;" class="borde_abajo">
        <p class="css-vertical-text">
            Foto
        </p>
        <div class="linea" style="height: 98%;"></div>
        <img src="${resource(dir: 'rubros', file: filePath)}" alt="" style="margin-bottom: 10px;max-width: 600px"/>
    </fieldset>
</g:elseif>
<script type="text/javascript">
    $("#submit").click(function () {
        $("#frmUpload").submit();
    });
    $("#salir").click(function () {
        window.close()
    });
</script>

</body>
</html>

<script type="text/javascript">

    $(".btnBorrar").click(function () {
        var id = $(this).data("id");
        var rubro = $(this).data("rubro");
        var tipo = $(this).data("tipo");

        if(confirm("Está seguro de borrar este archivo?")){
            $.ajax({
                type    : "POST",
                url     : "${createLink(controller: 'rubro', action:'borrarArchivo')}",
                data    : {
                    id : id,
                    tipo: tipo,
                    rubro: rubro
                },
                success : function (msg) {
                    if(msg == 'ok'){
                        alert("Archivo borrado");
                        setTimeout(function () {
                            window.top.close()
                        }, 500);
                    }else{
                        alert("Error al borrar el archivo");
                    }
                }
            });
        }else{
        }

        %{--$.box({--}%
        %{--imageClass : "box_info",--}%
        %{--title      : "Alerta",--}%
        %{--text       : "Está seguro de eliminar este archivo?" + "Esta acción no se puede deshacer.",--}%
        %{--iconClose  : false,--}%
        %{--dialog     : {--}%
        %{--resizable     : false,--}%
        %{--draggable     : false,--}%
        %{--buttons       : {--}%
        %{--"Aceptar"  : function () {--}%
        %{--$.ajax({--}%
        %{--type    : "POST",--}%
        %{--url     : "${createLink(action:'borrarArchivo')}",--}%
        %{--data    : {--}%
        %{--id : id--}%
        %{--},--}%
        %{--success : function (msg) {--}%

        %{--}--}%
        %{--});--}%
        %{--},--}%
        %{--"Cancelar" : function () {--}%
        %{--}--}%
        %{--}--}%
        %{--}--}%
        %{--});--}%
    });



</script>