<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Proyectos de Obras</title>
    <meta name="layout" content="main"/>
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
        margin-top: 20px;
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
<div class="dialog">
    <div style="text-align: center;"><h1 style="font-family: 'open sans condensed';font-weight: bold;font-size: 25px;text-shadow: -2px 2px 1px rgba(0, 0, 0, 0.25);color:#0088CC;">Control de Proyectos, Contratación, Ejecución y Seguimiento de Obras del GADPP</h1></div>
    <div class="body" style="width: 100%;height: 540px;position: relative;margin: auto;overflow-y: auto;">

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


    </div>
    <div style="height: 25px;width: 100%;text-align: right;float: right;">&copy; TEDEIN S.A. Versión ${message(code: 'version', default: '0.1.0x')}</div>
</div>
<script type="text/javascript">
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
</script>
</body>
</html>

%{--
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <meta name="layout" content="main">
        <title></title>
    </head>
    <body>
    </body>
</html>--}%
