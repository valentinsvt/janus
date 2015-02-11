<div class="row-fluid" style="margin-left: 0px;margin-bottom: 10px;">

    <div class="span1">
        <div class="btn-group" data-toggle="buttons-checkbox">
            <button type="button" id="ver_todos" class="btn btn-info ${(todos=="1")?'active':''} " style="font-size: 14px">Ver todos</button>

        </div>

    </div>
    <div class="span3">
        <a href="#" class="btn  " id="imprimir">
            <i class="icon-file"></i>
            Imprimir
        </a>
        <a href="#" class="btn  " id="excel">
            <i class="icon-file"></i>
            Excel
        </a>
    </div>
    <div class="span4">
        <b>Coordinación:</b> ${dep}
    </div>
    <div class="span2">
        <b>Año:</b> ${anio}
    </div>

</div>
<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr style="font-size: 10px !important;">
        <th style="width: 30px">#</th>
        <th style="width: 40px">Año</th>
        <th style="width: 100px">Partida</th>
        <th style="width: 50px;">CCP</th>
        <th style="width: 50px;">Tipo <br>Compra</th>
        <th style="width: 300px;">Descripción</th>
        <th style="width: 40px;">Cant.</th>
        <th style="width: 40px">Unidad</th>
        <th style="width: 60px;">Costo <br>Unitario</th>
        <th style="width: 60px">Costo <br>Total</th>
        <th style="width: 35px;">C1</th>
        <th style="width: 35px;">C2</th>
        <th style="width: 35px;">C3</th>
        <th style="width: 35px" class="col_delete"></th>
    </tr>
    </thead>
    <tbody id="tabla_pac">
    <g:set var="total" value="${0}"></g:set>
    <g:each in="${pac}" var="p" status="i">

        <tr class="item_row" id="${p.id}"  dpto="${p.departamento.id}" req="${p.requiriente}" memo="${p.memo}" tipoP="${p.tipoProcedimiento?.id}">
            <td style="text-align: center" title="Requiriente: ${p.requiriente}, Memo: ${p.memo}">${i+1}</td>
            <td style="width: 40px" class="anio" anio="${p.anio.id}">${p.anio.anio}</td>
            <td class="prsp" prsp="${p.presupuesto.id}" title="${p.presupuesto.descripcion} - Fuente: ${p.presupuesto.fuente} - Programa: ${p.presupuesto.programa} - Subprograma: ${p.presupuesto.subPrograma} - Proyecto: ${p.presupuesto.proyecto}">${p.presupuesto.numero}</td>
            <td class="cpac" cpac="${p.cpp?.id}" title="${p.cpp?.descripcion}">${p.cpp?.numero}</td>
            <td class="tipo" tipo="${p.tipoCompra.id}">${p.tipoCompra.descripcion}</td>
            <td class="desc">${p.descripcion}</td>
            <td style="text-align: right" class="cant">
                <g:formatNumber number="${p.cantidad}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
            </td>
            <td style="width: 40px !important;text-align: center" class="unidad" unidad="${p.unidad.id}">${p.unidad.codigo}</td>
            <td style="text-align: right" class="costo"><g:formatNumber number="${p.costo}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
            <td style="text-align: right" class="total"><g:formatNumber number="${p.cantidad*p.costo}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/></td>
            <g:set var="total" value="${total+p.cantidad*p.costo}"></g:set>
            <td style="text-align: center" class="c1">${p.c1}</td>
            <td style="text-align: center" class="c2">${p.c2}</td>
            <td style="text-align: center" class="c3">${p.c3}</td>
            <td style="width: 40px;text-align: center" class="col_delete">
                <a class="btn btn-small btn-danger borrarItem" href="#" rel="tooltip" title="Eliminar" iden="${p.id}">
                    <i class="icon-trash"></i></a>
            </td>
        </tr>

    </g:each>
    </tbody>
</table>
<div style="width: 99.7%;height: 30px;overflow-y: auto;float: right;text-align: right" id="total">
    <b>TOTAL:</b>
    <div id="divTotal" style="width: 150px;float: right;height: 30px;font-weight: bold;font-size: 12px;margin-right: 20px">
        <g:formatNumber number="${total}" format="##,##0" minFractionDigits="2" maxFractionDigits="2" locale="ec"/>
    </div>
</div>
<script type="text/javascript">

    $.contextMenu({
        selector: '.item_row',
        callback: function(key, options) {
            if(key=="edit"){
                $(this).dblclick()
            }
            %{--if(key=="print"){--}%
            %{--var dsps=${obra.distanciaPeso}--}%
            %{--var dsvs=${obra.distanciaVolumen}--}%
            %{--var volqueta=${precioVol}--}%
            %{--var chofer=${precioChof}--}%
            %{--var datos = "?dsps="+dsps+"&dsvs="+dsvs+"&prvl="+volqueta+"&prch="+chofer+"&fecha="+$("#fecha_precios").val()+"&id=${rubro?.id}&lugar="+$("#ciudad").val()--}%
            %{--location.href="${g.createLink(controller: 'reportes3',action: 'imprimirRubro')}"+datos--}%
            %{--var datos = "?dsps="+dsps+"Wdsvs="+dsvs+"Wprvl="+volqueta+"Wprch="+chofer+"Wfecha=${obra.fechaPreciosRubros.format('dd-MM-yyyy')}Wid="+$(this).attr("item")+"Wlugar=${obra.lugar.id}Windi=${indirectos}Wobra=${obra.id}"--}%
            %{--var url = "${g.createLink(controller: 'reportes3',action: 'imprimirRubro')}"+datos--}%
            %{--location.href="${g.createLink(controller: 'pdf',action: 'pdfLink')}?url="+url--}%
            %{--}--}%
        },
        items: {
            "edit": {name: "Editar", icon: "edit"}
        }
    });

    $("#imprimir").click(function(){
        var datos=""
        if($(this).hasClass("active")){
            datos+="todos=1"

        }else{
            if($("#item_depto").val()*1>0){
                datos = "dpto="+$("#item_depto").val()+"Wanio="+$("#item_anio").val()
            }else{
                datos="anio="+$("#item_anio").val()
            }
        }
        location.href="${createLink(controller: 'pdf',action: 'pdfLink')}?url=${createLink(controller: 'reportes',action: 'pac')}?"+datos
        %{--location.href="${createLink(controller: 'reportes',action: 'pac')}?"+datos--}%
    });

    $("#excel").click(function(){
        var datos=""
        if($(this).hasClass("active")){
            datos+="todos=1"

        }else{
            if($("#item_depto").val()*1>0){
                datos = "dpto="+$("#item_depto").val()+"&anio="+$("#item_anio").val()
            }else{
                datos="anio="+$("#item_anio").val()
            }
        }
        location.href="${createLink(controller: 'reportes',action: 'pacExcel')}?"+datos
    })

    $("#ver_todos").click(function(){
        if($(this).hasClass("active")){
            cargarTabla()
        }else{
            $.ajax({type : "POST", url : "${g.createLink(controller: 'pac',action:'tabla')}",
                data     : "todos=1",
                success  : function (msg) {
                    $("#detalle").html(msg)
                }
            });
        }
    })

    $(".item_row").dblclick(function(){

        $("#item_id").val($(this).attr("id"))

        $("#item_depto").val($(this).attr("dpto"))
        $("#item_anio").val($(this).find(".anio").attr("anio"))
        $("#item_prsp").val($(this).find(".prsp").attr("prsp"))
        $("#item_presupuesto").val($(this).find(".prsp").html())
        $("#item_presupuesto").attr("title",$(this).find(".prsp").attr("title"))
        $("#item_cpac").val($(this).find(".cpac").attr("cpac"))
        $("#item_codigo").val($(this).find(".cpac").html())
        $("#item_codigo").attr("title",$(this).find(".cpac").attr("title"))
        $("#item_tipo").val($(this).find(".tipo").attr("tipo"))
        $("#item_desc").val($(this).find(".desc").html())
        $("#item_cantidad").val($(this).find(".cant").html().trim())
        $("#item_precio").val($(this).find(".costo").html())
        $("#item_precio").attr("valAnt",$(this).find(".costo").html())
        $("#item_unidad").val($(this).find(".unidad").attr("unidad"))
        $("#item_req").val($(this).attr("req"))
        $("#item_memo").val($(this).attr("memo"))
        $("#item_tipoProc").val($(this).attr("tipoP"))

        $("#item_c1").removeClass("active")
        $("#item_c2").removeClass("active")
        $("#item_c3").removeClass("active")

        if($(this).find(".c1").html()=="S")
            $("#item_c1").addClass("active")
        if($(this).find(".c2").html()=="S")
            $("#item_c2").addClass("active")
        if($(this).find(".c3").html()=="S")
            $("#item_c3").addClass("active")
        cargarTecho()


    });
    $(".borrarItem").click(function(){
        if(confirm("Esta seguro de eliminar este registro del PAC?")){
            $.ajax({type : "POST", url : "${g.createLink(controller: 'pac',action:'eliminarPac')}",
                data     : "id=" + $(this).attr("iden"),
                success  : function (msg) {
                    cargarTabla()

                }
            });
        }
    });
</script>