<%@ page import="janus.Unidad" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>

<head>
    <meta name="layout" content="main">
    <title>Janus -Ingreso-</title>

    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    %{--<link href='http://fonts.googleapis.com/css?family=Tulpen+One' rel='stylesheet' type='text/css'>--}%
    %{--<link href='http://fonts.googleapis.com/css?family=Open+Sans+Condensed:300' rel='stylesheet' type='text/css'>--}%
    <link href='${resource(dir: "css", file: "custom.css")}' rel='stylesheet' type='text/css'>
    <link href='${resource(dir: "font/open", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
    <link href='${resource(dir: "font/tulpen", file: "stylesheet.css")}' rel='stylesheet' type='text/css'>
    <style type="text/css">
    @page {
        size: 8.5in 11in;  /* width height */
        margin: 0.25in;
    }
    .item{
        width: 260px;height: 220px;float: left;margin: 4px;
        font-family: 'open sans condensed';
        border: none;



    }
    .imagen{
        width: 167px;
        height: 100px;
        margin: auto;
        margin-top: 10px;
    }
    .texto{
        width: 90%;
        height: 50px;
        padding-top: 0px;
        /*border: solid 1px black;*/
        margin: auto;
        margin: 8px;
        /*font-family: fantasy; */
        font-size: 16px;

        /*
                font-weight: bolder;
        */
        font-style: oblique;
        /*text-align: justify;*/
    }
    .fuera{
        margin-left: 15px;
        margin-top: 10px;
        /*background-color: #317fbf; */
        background-color: rgba(200,200,200,0.9);
        border: none;

    }
    .desactivado{
        color: #bbc;
    }
    h1 {
        font-size: 24px;
    }
    </style>
</head>

<body>
<g:if test="${flash.message}">
    <div class="alert alert-info" role="status">
        <a class="close" data-dismiss="alert" href="#">×</a>
        ${flash.message}
    </div>
</g:if>

<div class="alert hide" id="divError" role="status">
    <a class="close" data-dismiss="alert" href="#">×</a>
    <span id="spanError"></span>
</div>

<div class="dialog ui-corner-all" style="height: 595px;background: #31789f;;padding: 10px;width: 910px;margin: auto;margin-top: 5px" >
    <div style="text-align: center;">
        <h1 style="font-family: 'open sans condensed';font-weight: bold;font-size: 25px;text-shadow: -2px 2px 1px rgba(0, 0, 0, 0.25);color:#fff;">
            Control de Proyectos, Contratación, Ejecución y Seguimiento de Obras del GADPP
        </h1>
    </div>
    <div class="body" style="width: 850px;position: relative;margin: auto;margin-top: 0px;height: 480px">

        %{--<g:link  controller="proyecto" action="list" title="Gestión de proyectos">--}%
        <div  class="ui-corner-all  item fuera">
            <div  class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'apu1.png')}" width="100%" height="100%"/>
                </div>
                <div class="texto"><b>Precios unitarios y análisis de precios</b>: registro y mantenimiento de
                ítems y rubros. Análisis de precios, rendimientos y listas de precios...</div>
            </div>
        </div>
        %{--</g:link>--}%
        %{--<g:link  controller="asignacion" action="asignacionesCorrientesv2"  id="${session.unidad.id}" title="Programación del gasto corriente">--}%
        <div  class="ui-corner-all item fuera">
            <div  class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'obra100.png')}" width="100%" height="100%"/>
                </div>
                <div class="texto"><b>Obras</b>: registro de Obras, georeferen-ciación, los volúmenes de obra,
                variables de transporte y costos indirectos ...</div>
            </div>
        </div>
        %{--</g:link>--}%

        %{--<g:link  controller="entidad" action="arbol_asg"  id="${session.unidad.id}" title="Plan Anual de Compras - gasto corriente ">--}%
        <div  class="ui-corner-all item fuera">
            <div  class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'compras.png')}" width="100%" height="100%"/>
                </div>
                <div class="texto"><b>Compras Públicas</b>: plan anual de contrataciones, gestión de pliegos y
                control y seguimiento del PAC de obras ...</div>
            </div>
        </div>
        %{--</g:link>--}%

        %{--<g:link  controller="documento" action="list" title="Documentos de los Proyectos">--}%
        <div  class="ui-corner-all  item fuera">
            <div  class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'fiscalizar.png')}" width="100%" height="100%"/>
                </div>
                <div class="texto"><b>Fiscalización</b>: seguimiento a la ejecución de las obras: incio de obra,
                planillas, reajuste de precios, cronograma ...</div>
            </div>
        </div>
        %{--</g:link>--}%
        %{--<g:link  controller="documento" action="list" title="Documentos de los Proyectos">--}%
        <div  class="ui-corner-all  item fuera">
            <div  class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'reporte.png')}" width="100%" height="100%"/>
                </div>
                <div class="texto"><b>Reportes</b>: formatos pdf, hoja de cálculo, texto plano y html.
                obras, concursos, contratos, contratistas, avance de obra...</div>
            </div>
        </div>
        %{--</g:link>--}%
        %{--<g:link  controller="documento" action="list" title="Documentos de los Proyectos">--}%
        <div  class="ui-corner-all  item fuera">
            <div  class="ui-corner-all ui-widget-content item">
                <div class="imagen">
                    <img src="${resource(dir: 'images', file: 'oferta.png')}" width="100%" height="100%"/>
                </div>
                <div class="texto"><b>Oferentes ganadores</b>:registro en línea los valores de precios unitarios,
                rubros, volúmenes de obra y cronograma de las ofertas </div>
            </div>
        </div>
        %{--</g:link>--}%
        %{--<div  class="ui-corner-all  item fuera" style="width: 543px">--}%
        %{--<div  class="ui-corner-all ui-widget-content item" style="width: 543px">--}%
        %{--<img src="${resource(dir: 'images', file: 'logo_gpp.png')}" style="width: 543px;height: 217px;" class="ui-corner-all"/>--}%
        %{--</div>--}%
        %{--</div>--}%


    </div>
    <div style="width: 100%;height: 30px;float: left;margin-top: 10px;text-align: center">
        <a href="#" id="ingresar" class="btn btn-inverse">
            <i class="icon-off icon-white"></i>
            Ingresar
        </a>
    </div>
    <div style="text-align:right; color:#eea781">&copy; TEDEIN S.A. Versión ${message(code: 'version', default: '1.1.0x')}</div>
</div>

<div class="modal login hide fade " id="modal-ingreso" style=";overflow: hidden;">
    <div class="modal-body" id="modalBody" style="padding: 0px">

        <g:form class="well form-horizontal span " action="validar" name="frmLogin" style="border: 5px solid #525E67;background: #012a3a;color: #939Aa2;width: 300px;position: relative;padding-left: 60px;margin: 0px">
            <p class="css-vertical-text tituloGrande" style="left: 12px;;font-family: 'Tulpen One',cursive;font-weight: bold;font-size: 35px">Sistema Janus</p>

            <div class="linea" style="height: 95%;left: 45px"></div>
            <button type="button" class="close" data-dismiss="modal" style="color: white;opacity: 1;">×</button>
            <fieldset style="">
                <legend style="color:#c0c0c0;border:none;font-family: 'Open Sans Condensed', serif;font-weight: bolder;font-size: 25px">Ingreso</legend>

                <g:if test="${flash.message}">
                    <div class="alert alert-info" role="status">
                        <a class="close" data-dismiss="alert" href="#">×</a>
                        ${flash.message}
                    </div>
                </g:if>

                <div class="control-group" style="margin-top: 0">
                    <label class="control-label" for="login" style="width: 100%;text-align: left;font-size: 25px;font-family: 'Tulpen One',cursive;font-weight: bolder">Usuario:</label>

                    <div class="controls" style="width: 100%;margin-left: 5px">
                        <g:textField name="login" class="span2" style="width: 90%"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="control-group">
                    <label class="control-label" for="login" style="width: 100%;text-align: left;font-size: 25px;font-family: 'Tulpen One',cursive;font-weight: bolder">Password:</label>

                    <div class="controls" style="width: 100%;margin-left: 5px">
                        <g:passwordField name="pass" class="span2" style="width: 90%"/>
                        <p class="help-block ui-helper-hidden"></p>
                    </div>
                </div>

                <div class="control-group">

                    <a href="#" class="btn btn-primary" id="btnLogin">Continuar</a>
                    <a href="#" id="btnOlvidoPass" style="color: #ffffff;margin-left: 70px;text-decoration: none;font-family: 'Open Sans Condensed', serif;font-weight: bold">
                        Olvidó su contraseña?
                    </a>
                </div>
            </fieldset>
        </g:form>
    </div>
</div>

</div>


<div id="recuperarPass-dialog"  class="dialog ui-corner-all" style="height: 595px;background: #d0d0d0;;padding: 10px;width: 910px;margin: auto;margin-top: 5px">

    %{--<fieldset>--}%


        <div style="text-align: center;">
            <h1 style="font-family: 'open sans condensed';font-weight: bold;font-size: 12px;text-shadow: -2px 2px 1px rgba(0, 0, 0, 0.25);color:#fff;">

            </h1>
        </div>

        <div class="span3">
        Ingrese el email registrado a su usuario y se le enviará una nueva contraseña para ingresar al sistema.

        </div>

        <div class="span3" style="margin-top: 20px">

            <g:textField name="mailPass" type="text" id="email" class="span2" placeholder="Email" style="width: 300px"/>

        </div>
    %{--</fieldset>--}%
</div>




<script type="text/javascript">
    $(function () {

        $("#btnOlvidoPass").click(function () {



            $("#recuperarPass-dialog").dialog("open");
            $("#modal-ingreso").modal("hide");

            %{--var p = $("<p>Ingrese el email registrado a su usuario y se le enviará una nueva contraseña para ingresar al sistema.</p>");--}%
            %{--var div = $('<div class="control-group" />');--}%
            %{--var input = $('<input type="text" class="" id="email" placeholder="Email"/>');--}%
            %{--div.append(input);--}%

            %{--var btnOk = $('<a href="#" data-dismiss="modal" class="btn">Cerrar</a>');--}%
            %{--var btnSend = $('<a href="#"  class="btn btn-success"><i class="icon-ok"></i> Enviar</a>');--}%

            %{--var send = function () {--}%
                %{--var email = $.trim(input.val());--}%
                %{--if (email != "") {--}%
                    %{--btnSend.replaceWith(spinner);--}%
                    %{--$.ajax({--}%
                        %{--type    : "POST",--}%
                        %{--url     : "${createLink(action:'olvidoPass')}",--}%
                        %{--data    : {--}%
                            %{--email : email--}%
                        %{--},--}%
                        %{--success : function (msg) {--}%
                            %{--var parts = msg.split("*");--}%
                            %{--if (parts[0] == "OK") {--}%
                                %{--$("#modalBody").addClass("alert alert-success");--}%
                            %{--} else {--}%
                                %{--$("#modalBody").addClass("alert alert-error");--}%
                            %{--}--}%
                            %{--$("#modalBody").html(parts[1]);--}%
                            %{--spinner.remove();--}%
                        %{--}--}%
                    %{--});--}%
                %{--} else {--}%
                    %{--$("#divMail").addClass("error");--}%
                %{--}--}%
            %{--};--}%

            %{--btnSend.click(function () {--}%
                %{--send();--}%
                %{--return false;--}%
            %{--});--}%
            %{--input.keyup(function (ev) {--}%
                %{--if (ev.keyCode == 13) {--}%
                    %{--send();--}%
                %{--}--}%
            %{--});--}%

            %{--$("#modalTitle").html("Olvidó su contraseña?");--}%
            %{--$("#modalBody").html("").append(p).append(div);--}%
            %{--$("#modalFooter").html("").append(btnOk).append(btnSend);--}%
            %{--$("#modal-pass").modal("show");--}%
        });



        $("#recuperarPass-dialog").dialog({

            autoOpen  : false,
            resizable : false,
            modal     : true,
            draggable : false,
            width     : 400,
            height    : 250,
            position  : 'center',
            title     : 'Recuperar Password',
            buttons   : {
                "Aceptar" : function () {

//                    var email = $.trim(mail.val());


                      var emailPass = $.trim(email.value)

//                    ////console.log(emailPass)

                    if (email != "") {
//                        btnSend.replaceWith(spinner);
                        $.ajax({
                            type    : "POST",
                            url     : "${createLink(action:'olvidoPass')}",
                            data    : {
                                email : emailPass
                            },
                            success : function (msg) {
                                var parts = msg.split("*");
                                if (parts[0] == "OK") {
                                     $("#divError").removeClass("alert-error").addClass("alert-success").show();
                                     $("#spanError").html(parts[1]);
                                    $("#recuperarPass-dialog").dialog("close");

                                } else {
                                    $("#divError").addClass("alert-error").removeClass("alert-success").show();
                                    $("#spanError").html(parts[1]);
                                    $("#recuperarPass-dialog").dialog("close")
                                }
//                                $("#modalBody").html(parts[1]);
//                                spinner.remove();
                            }
                        });
                    } else {
                        $("#divMail").addClass("error");
                    }




                },

                "Cancelar": function (){

                    $("#recuperarPass-dialog").dialog("close")

                }


            }

        });


        $("input").keypress(function (ev) {
            if (ev.keyCode == 13) {
                $("#frmLogin").submit();
            }
        });

        $("#btnLogin").click(function () {
            $("#frmLogin").submit();
            return false;
        });

        $("#frmLogin").validate({
            errorPlacement : function (error, element) {
                element.parent().find(".help-block").html(error).show();
            },
            success        : function (label) {
                label.parent().hide();
            },
            errorClass     : "label label-important",
            submitHandler  : function (form) {
                $("#btnLogin").replaceWith(spinnerLogin);
                form.submit();
            }
        });

        $(".fuera").hover(function(){
            var d =  $(this).find(".imagen")
            d.width(d.width()+10)
            d.height(d.height()+10)
//        $.each($(this).children(),function(){
//            $(this).width( $(this).width()+10)
//        });
        },function(){
            var d =  $(this).find(".imagen")
            d.width(d.width()-10)
            d.height(d.height()-10)
        })


    });
</script>

</body>
</html>