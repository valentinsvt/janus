
<g:if test="${flash.message}">
    <div class="span12" style="height: 35px;margin-bottom: 10px; margin-left: -25px">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status" style="text-align: center">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>



<div class="row-fluid" style="margin-left: 0px">
    <div class="span5" style="width: 550px">
        <b>Subpresupuesto de origen:</b>
        <g:select name="subpresupuestoOrg" from="${subPres}" optionKey="id" optionValue="descripcion"  noSelection="['' : ' - Seleccione un subpresupuesto - ']" style="width: 300px;font-size: 10px; margin-left: 50px" id="subPres_desc" value="${subPre}"></g:select>

        %{--todo descomentar esto--}%
        %{--<g:select name="subpresupuestoOrg" from="${subPresupuesto1}" optionKey="id" optionValue="descripcion"  noSelection="['' : ' - Seleccione un subpresupuesto - ']" style="width: 300px;font-size: 10px; margin-left: 50px" id="subPres_desc" value="${subPre}"></g:select>--}%
    </div>



    <div class="span4" style="width: 200px">
        <a href="#" class="btn" id="regresar">
            <i class="icon-arrow-left"></i>
            Regresar
        </a>

    </div>



</div>

<div class="row-fluid" style="margin-left: 0px">
    <div class="span5" style="width: 550px">
        <b>Subpresupuesto de destino:</b>
        <g:select name="subpresupuestoDes" from="${janus.SubPresupuesto.list([sort: 'descripcion'])}" optionKey="id" optionValue="descripcion" style="width: 300px;font-size: 10px; margin-left: 45px" id="subPres_destino"
                  noSelection="['' : ' - Seleccione un subpresupuesto - ']"></g:select>

        %{--todo descomentar esto--}%
        %{--<g:select name="subpresupuestoDes" from="${subPresupuesto1}" optionKey="id" optionValue="descripcion" style="width: 300px;font-size: 10px; margin-left: 45px" id="subPres_destino"--}%
                  %{--noSelection="['' : ' - Seleccione un subpresupuesto - ']"></g:select>--}%


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
    </tr>
    </thead>


    <tbody id="tabla_material">

    <g:each in="${valores}" var="val" status="">

        <tr class="item_row" id="${val.item__id}" item="${val}" sub="${val.sbpr__id}" ord="${val.vlobordn}" cant="${val.vlobcntd}">

            <td style="width: 10px" class="sel"><g:checkBox class="chec" name="selec" checked="false" id="seleccionar1" value="${val.item__id}"/></td>
            %{--<td style="width: 10px" class="sel"><input type="checkbox" id="seleccionar1" class="chec" checked="false" value="${val.item__id}"></td>--}%
            <td style="width: 20px" class="orden">${val.vlobordn}</td>
            <td style="width: 200px" class="sub">${val.sbprdscr.trim()}</td>
            <td class="cdgo">${val.rbrocdgo.trim()}</td>
            <td class="nombre">${val.rbronmbr.trim()}</td>
            <td style="width: 60px !important;text-align: center" class="col_unidad">${val.unddcdgo.trim()}</td>
            <td style="text-align: right" class="cant">
                <g:formatNumber number="${val.vlobcntd}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
            </td>
            <td class="col_precio" style="display: none;text-align: right" id="i_${val.item__id}"><g:formatNumber number="${val.pcun}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
            <td class="col_total total" style="display: none;text-align: right"><g:formatNumber number="${val.totl}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
        </tr>

    </g:each>
    </tbody>
</table>


<div id="faltaOrigenDialog">

    <fieldset>
        <div class="span3">

            Es necesario elegir los subpresupuesto de Origen y de Destino

        </div>
    </fieldset>
</div>



<script type="text/javascript">

    var checkeados = []

    $("#subPres_desc").change(function(){

        var datos = "obra=${obra.id}&sub="+$("#subPres_desc").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'tablaCopiarRubro')}",
            data     : datos,
            success  : function (msg) {
                clearInterval(interval)
                $("#detalle").html(msg)
            }
        });
    });


    $("#copiar_todos").click(function () {



        var tbody = $("#tabla_material");
        var datos
        var subPresDest = $("#subPres_destino").val()
        var subPre = $("#subPres_desc").val()

        if(subPre == "" || subPresDest == ""){

            $("#faltaOrigenDialog").dialog("open")


        } else {

            tbody.children("tr").each(function () {

                var trId = $(this).attr("id")

                //console.log("ID:" + trId)


                datos ="rubro=" + trId + "&subDest=" + subPresDest + "&obra=" + ${obra.id} + "&sub=" + subPre

                $.ajax({type : "POST", url : "${g.createLink(controller: 'volumenObra',action:'copiarItem')}",
                    data     : datos,
                    success  : function (msg) {
                        $("#detalle").html(msg)

                    }
                });


            });
        }

    });


    $("#copiar_sel").click(function () {

        var tbody = $("#tabla_material");
//        var trP = $("#item_row")
        var datos
        var subPresDest = $("#subPres_destino").val()
        var subPre = $("#subPres_desc").val()
        var rbros = []


        tbody.children("tr").each(function () {

//            //console.log($(this).children("td").children().get(1).is(":checked"))

            if(($(this).children("td").children().get(1).checked) == true){

                var selec = []

                var trId = $(this).attr("id")
                var ord = $(this).attr("ord")
                var canti = $(this).attr("cant")


                datos ="&rubro=" + trId + "&subDest=" + subPresDest + "&obra=" + ${obra.id} + "&sub=" + subPre + "&orden=" + ord + "&canti=" + canti

                $.ajax({
                    type : "POST",
                    async : false,
                    url : "${g.createLink(controller: 'volumenObra',action:'copiarItem')}",
                    data     : datos,
                    success  : function (msg) {
                        $("#detalle").html(msg)

                    }
                });


//                //console.log("id:" + trId)
            } else {


                //console.log("entro else")


            }




        });



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


//    $(".item_row").dblclick(function(){
//        $("#calcular").removeClass("active")
//        $(".col_delete").show()
//        $(".col_precio").hide()
//        $(".col_total").hide()
//        $("#divTotal").html("")
//        $("#vol_id").val($(this).attr("id"))
//        $("#item_codigo").val($(this).find(".cdgo").html())
//        $("#item_id").val($(this).attr("item"))
//        $("#subPres").val($(this).attr("sub"))
//        $("#item_nombre").val($(this).find(".nombre").html())
//        $("#item_cantidad").val($(this).find(".cant").html().toString().trim())
//        $("#item_orden").val($(this).find(".orden").html() )
//
//    });

    $("#faltaOrigenDialog").dialog({

        autoOpen  : false,
        resizable : false,
        modal     : true,
        draggable : false,
        width     : 350,
        height    : 150,
        position  : 'center',
        title     : 'Elegir subpresupuestos!',
        buttons   : {
            "Aceptar" : function () {


                $("#faltaOrigenDialog").dialog("close");

            }
        }


    });

    $("#regresar").click(function () {

       location.href = "${g.createLink(controller: 'volumenObra', action: 'volObra', id: obra?.id)}"

    });

</script>