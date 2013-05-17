
<%@ page import="janus.Grupo" %>
<!doctype html>
<html>
<head>
    <meta name="layout" content="main">
    <title>
        Copiar Rubros
    </title>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'jquery.validate.min.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jquery-validation-1.9.0', file: 'messages_es.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/', file: 'jquery.livequery.js')}"></script>
    <script src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href="${resource(dir: 'js/jquery/plugins/box/css', file: 'jquery.luz.box.css')}" rel="stylesheet">
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.ui.position.js')}" type="text/javascript"></script>
    <script src="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.js')}" type="text/javascript"></script>
    <link href="${resource(dir: 'js/jquery/plugins/jQuery-contextMenu-gh-pages/src', file: 'jquery.contextMenu.css')}" rel="stylesheet" type="text/css"/>
</head>

<body>



<div class="row-fluid" style="margin-left: 0px">
    <div class="span5" style="width: 550px">
        <b>Subpresupuesto de origen:</b>
        <g:select name="subpresupuesto" from="${subPres}" optionKey="id" optionValue="descripcion" style="width: 300px;font-size: 10px; margin-left: 50px" id="subPres_desc" value="${subPre}"></g:select>
    </div>

    <div class="span4" style="width: 500px">

        <a href="#" class="btn  " id="copiar_todos">
            <i class="icon-copy"></i>
            Copiar Todos los Rubros
        </a>
        <a href="#" class="btn  " id="copiar_sel">
            <i class="icon-copy"></i>
            Copiar rubros seleccionados
        </a>


    </div>



</div>

<div class="row-fluid" style="margin-left: 0px">
    <div class="span5" style="width: 550px">
        <b>Subpresupuesto de destino:</b>
        <g:select name="subpresupuestoDes" from="${janus.SubPresupuesto.list([order: 'descripcion', sort: 'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 300px;font-size: 10px; margin-left: 45px" id="subPres_destino"
                  noSelection="['' : ' - Seleccione un subpresupuesto - ']"></g:select>
    </div>
</div>

<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr>
        <th style="width: 10px;">
            *
        </th>
        <th style="width: 20px;">
            #
        </th>
        <th style="width: 200px;">
            Subpresupuesto
        </th>
        <th style="width: 80px;">
            Código
        </th>
        <th style="width: 400px;">
            Rubro
        </th>
        <th style="width: 60px" class="col_unidad">
            Unidad
        </th>
        <th style="width: 80px">
            Cantidad
        </th>
        <th class="col_precio" style="display: none;">Unitario</th>
        <th class="col_total" style="display: none;">C.Total</th>
        %{--<th style="width: 40px" class="col_delete"></th>--}%
    </tr>
    </thead>
    <tbody id="tabla_material">

    <g:each in="${detalle}" var="vol" status="i">

        <tr class="item_row" id="${vol.id}" item="${vol.item.id}" sub="${vol.subPresupuesto.id}">

            <td style="width: 10px" class="sel"><g:checkBox name="sel"/></td>
            <td style="width: 20px" class="orden">${vol.orden}</td>
            <td style="width: 200px" class="sub">${vol?.subPresupuesto?.descripcion}</td>
            <td class="cdgo">${vol.item.codigo}</td>
            <td class="nombre">${vol.item.nombre}</td>
            <td style="width: 60px !important;text-align: center" class="col_unidad">${vol.item.unidad.codigo}</td>
            <td style="text-align: right" class="cant">
                <g:formatNumber number="${vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
            </td>
            <td class="col_precio" style="display: none;text-align: right" id="i_${vol.item.id}"><g:formatNumber number="${precios[vol.id.toString()]}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
            <td class="col_total total" style="display: none;text-align: right"><g:formatNumber number="${precios[vol.id.toString()]*vol.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
        </tr>

    </g:each>
    </tbody>
</table>

<script type="text/javascript">


    function loading(div) {
        y = 0;
        $("#" + div).html("<div class='tituloChevere' id='loading'>Sistema Janus - Cargando, Espere por favor</div>")
        var interval = setInterval(function () {
            if (y == 30) {
                $("#detalle").html("<div class='tituloChevere' id='loading'>Cargando, Espere por favor</div>")
                y = 0
            }
            $("#loading").append(".");
            y++
        }, 500);
        return interval
    }
    function cargarTabla() {
//        var interval = loading("detalle")
        var datos = ""
        if ($("#subPres_desc").val() * 1 > 0) {
            datos = "obra=${obra.id}&sub=" + $("#subPres_desc").val()
        } else {
            datos = "obra=${obra.id}"
        }
        $.ajax({type : "POST",
            url : "${g.createLink(controller: 'volumenObra',action:'copiarRubros')}",
            data     : datos,
            success  : function (msg) {
//                clearInterval(interval)
                $("#detalle").html(msg)
            }
        });
    }

  $(function () {

      cargarTabla();
      $("#vol_id").val("")

  });

    %{--$.contextMenu({--}%
        %{--selector: '.item_row',--}%
        %{--callback: function(key, options) {--}%
            %{--if(key=="edit"){--}%
                %{--$(this).dblclick()--}%
            %{--}--}%
            %{--if(key=="print"){--}%
                %{--var dsps=${obra.distanciaPeso}--}%
                %{--var dsvs=${obra.distanciaVolumen}--}%
                %{--var volqueta=${precioVol}--}%
                %{--var chofer=${precioChof}--}%
                %{--var datos = "?fecha=${obra.fechaPreciosRubros?.format('dd-MM-yyyy')}Wid="+$(this).attr("item")+"Wobra=${obra.id}"--}%
                %{--var url = "${g.createLink(controller: 'reportes3',action: 'imprimirRubroVolObra')}"+datos--}%
                %{--location.href="${g.createLink(controller: 'pdf',action: 'pdfLink')}?url="+url--}%
            %{--}--}%
            %{--if(key=="foto"){--}%
                %{--var child = window.open('${createLink(controller:"rubro",action:"showFoto")}/'+$(this).attr("item"), 'Mies', 'width=850,height=800,toolbar=0,resizable=0,menubar=0,scrollbars=1,status=0');--}%
                %{--if (child.opener == null)--}%
                    %{--child.opener = self;--}%
                %{--window.toolbar.visible = false;--}%
                %{--window.menubar.visible = false;--}%
            %{--}--}%
        %{--},--}%
        %{--<g:if test="${obra?.estado!='R'}">--}%
        %{--items: {--}%
            %{--"edit": {name: "Editar", icon: "edit"},--}%
            %{--"print": {name: "Imprimir", icon: "print"},--}%
            %{--"foto":{name:"Ilustración",icon:"doc"}--}%
        %{--}--}%
        %{--</g:if>--}%
        %{--<g:else>--}%
        %{--items: {--}%
            %{--"print": {name: "Imprimir", icon: "print"},--}%
            %{--"foto":{name:"Foto",icon:"doc"}--}%
        %{--}--}%
        %{--</g:else>--}%
    %{--});--}%



    $("#subPres_desc").change(function(){
        $("#divTotal").html("")
        %{--var datos = "obra=${obra.id}&sub="+$("#subPres_desc").val()--}%
        %{--var interval = loading("detalle")--}%
        %{--$.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'copiarRubros')}",--}%
            %{--data     : datos,--}%
            %{--success  : function (msg) {--}%
                %{--clearInterval(interval)--}%
                %{--$("#detalle").html(msg)--}%
            %{--}--}%
        %{--});--}%
    });

    %{--$("#ver_todos").click(function(){--}%
%{--//        $("#calcular").removeClass("active")--}%
        %{--$("#divTotal").html("")--}%
        %{--if ($(this).hasClass("active")) {--}%
            %{--$(this).removeClass("active")--}%

            %{--var datos = "obra=${obra.id}&sub="+$("#subPres_desc").val()--}%
            %{--var interval = loading("detalle")--}%
            %{--$.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'copiarRubros')}",--}%
                %{--data     : datos,--}%
                %{--success  : function (msg) {--}%
%{--//                    clearInterval(interval)--}%
                    %{--$("#detalle").html(msg)--}%
                %{--}--}%
            %{--});--}%

        %{--}else{--}%
            %{--$(this).addClass("active")--}%
            %{--var datos = "obra=${obra.id}"--}%
            %{--var interval = loading("detalle")--}%
            %{--$.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'copiarRubros')}",--}%
                %{--data     : datos,--}%
                %{--success  : function (msg) {--}%
%{--//                    clearInterval(interval)--}%
                    %{--$("#detalle").html(msg)--}%
                %{--}--}%
            %{--});--}%
        %{--}--}%
        %{--return false--}%

    %{--}) ;--}%


    $(".item_row").dblclick(function(){
        $("#calcular").removeClass("active")
        $(".col_delete").show()
        $(".col_precio").hide()
        $(".col_total").hide()
        $("#divTotal").html("")
        $("#vol_id").val($(this).attr("id"))
        $("#item_codigo").val($(this).find(".cdgo").html())
        $("#item_id").val($(this).attr("item"))
        $("#subPres").val($(this).attr("sub"))
        $("#item_nombre").val($(this).find(".nombre").html())
        $("#item_cantidad").val($(this).find(".cant").html().toString().trim())
        $("#item_orden").val($(this).find(".orden").html() )

    });



</script>
</body>
</html>