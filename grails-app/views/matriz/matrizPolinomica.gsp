<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        MatrizFP
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src',file: 'jquery.ui.position.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src',file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src',file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css" />
    <style type="text/css">
    .gris{
        background-color: #ececec;
    }
    .activo{
        background-color : rgba(255, 172, 55, 1)
    }
    .blanco{
        background-color: transparent;
    }
    .estaticas{
        background:linear-gradient(to bottom, #FFFFFF, #E6E6E6) ;
        font-weight: bold;
    }
    tr{
        cursor: pointer !important;
    }
    th{
        padding-left: 0px;
        padding-right: 0px;
    }
    td{
        line-height: 12px !important;
        padding: 3px !important;
    }
    </style>
</head>
<body>
<div class="span12">
    <g:if test="${flash.message}">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </g:if>
</div>
<div class="span12 btn-group" role="navigation" style="margin-left: 0px;">
    <a href="${g.createLink(controller: 'obra',action: 'registroObra',params: [obra:obraId])}" class="btn btn-ajax btn-new" id="atras" title="Regresar a la obra">
        <i class="icon-arrow-left"></i>
        Regresar
    </a>
    <a href="#" class="btn btn-ajax btn-new" id="calcular" title="Coeficientes">
        <i class="icon-table"></i>
        Coeficientes de la matriz
    </a>
    <a href="${g.createLink(controller: 'reportes',action: 'imprimeMatriz')}" class="btn btn-ajax btn-new" id="imprimir" title="Imprimir">
        <i class="icon-print"></i>
        Imprimir
    </a>
</div>
<div id="list-grupo" class="span12" role="main" style="margin-top: 10px;margin-left: 0px;width: 100%;max-width: 100%;overflow-x: hidden">
    <div style="width: 1000px;overflow-x: auto;max-width: 1000px;">
        <table class="table table-bordered table-condensed  " style="width: ${cols.size()*150-140}px;max-width: ${cols.size()*150-140}px;float:left">
            <thead>
            <tr style="font-size: 10px !important;">
                <th style="width: 30px;max-width: 30px;font-size: 12px !important" class="h_0">#</th>
                <th style="width: 100px;;font-size: 12px !important" class="h_1">Código</th>
                <th style="width: 300px !important;;font-size: 12px !important">Rubro</th>
                <th style="width: 40px;;font-size: 12px !important">Unidad</th>
                <th style="width: 70px;;font-size: 12px !important">Cantidad</th>
                <g:each in="${cols}" var="c">
                    <g:if test="${c[2]!='R'}">
                        <th style="width: 150px;font-size: 12px !important">${c[1]}</th>
                    </g:if>
                </g:each>
            </tr>
            </thead>
            <tbody>
            <g:each in="${filas}" var="f" status="j">
                <tr style="font-size: 10px !important;" class="item_row ${(j%2==0)?'gris':''} fila_${j}" color="${(j%2==0)?'gris':'blanco'}" fila="fila_${j}">
                    <g:each in="${f}" var="v" status="i">
                        <td style='${(i==2)?"width: 100px":(i>4)?"width:150px;text-align:right":""}' class="col_${i}">${v}</td>
                    </g:each>
                </tr>
            </g:each>

            </tbody>
        </table>
    </div>



</div>
<script type="text/javascript">

    //    $("#list-grupo").scroll(function(){
    //        console.log("scroll")
    //        console.log($(this).scrollLeft())
    //        console.log($(this).scrollWidth())
    //    });

    function copiaTabla(){
        var tabla = $('<table class="table table-bordered  table-condensed " style="width:140px;max-width: 140px;float: left">')
        tabla.append('<thead><th style="width: 30px;max-width: 30px;font-size: 12px !important">#</th><th style="width: 100px;;font-size: 12px !important" >Código</th></thead>')
        var body =$('<tbody>')
        var cont = 0;
        $(".item_row").each(function(){
            var tr = $("<tr class='item_row fila_"+cont+"' fila='fila_"+cont+"'>")
            tr.css({"height":$(this).innerHeight()})
            tr.attr("color",$(this).attr("color"))
            $(this).css({"height":$(this).innerHeight()})
            var col0 = $(this).find(".col_0")
            var col1 = $(this).find(".col_1")
            var c0 = col0.clone()
            var c1 = col1.clone()
            c0.removeClass("col_0").addClass("estaticas")
            c1.removeClass("col_1").addClass("estaticas")
            tr.append(c0)
            tr.append(c1)
//            $(".h_0").hide()
//            $(".h_1").hide()
//            col0.hide()
//            col1.hide()
            cont++
            body.append(tr)
        });
        tabla.append(body)
        $("#list-grupo").prepend(tabla)
        $(".h_0").hide()
        $(".h_1").hide()
        $(".col_0").hide()
        $(".col_1").hide()
    }
    $(function () {
        copiaTabla()
        $(".item_row").click(function(){
            $(".activo").addClass($(".activo").attr("color")).removeClass("activo")
            $("."+$(this).attr("fila")).addClass("activo")
            $("."+$(this).attr("fila")).removeClass("gris")
            $("."+$(this).attr("fila")).removeClass("blanco")
        });
    });


</script>


</body>
</html>