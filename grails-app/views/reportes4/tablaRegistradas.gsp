
<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 8/20/13
  Time: 12:10 PM
--%>

<g:if test="${flash.message}">
    <div class="span12" style="height: 35px;margin-bottom: 10px; margin-left: -25px">
        <div class="alert ${flash.clase ?: 'alert-info'}" role="status" style="text-align: center">
            <a class="close" data-dismiss="alert" href="#">×</a>
            ${flash.message}
        </div>
    </div>
</g:if>


<div class="row-fluid">
    <div class="span12">

        <b>Buscar Por: </b>
        <g:select name="buscador" from="${['cdgo':'Codigo', 'nmbr':'Nombre', 'tipo': 'Tipo', 'cntn': 'Cantón', 'parr': 'Parroquia'
                , 'cmnd': 'Comunidad', 'insp':'Inspector', 'rvsr':'Revisor', 'ofig':'Of. Ingreso', 'ofsl': 'Of. Salida'
                ,'mmsl':'Memo Salida', 'frpl':'F. Polinómica']}" value="${params.buscador}"
                  optionKey="key" optionValue="value" id="buscador_reg" style="width: 150px"/>
        <b style="margin-left: 10px">Estado: </b>
        <g:select name="estado" from="${['1':'Todas', '2':'Ingresadas', '3':'Registradas']}" optionKey="key"
                  optionValue="value" id="estado_reg" value="${params.estado}" style="width: 150px"/>
        <b>Criterio: </b>
        <g:textField name="criterio" style="width: 250px; margin-right: 10px" value="${params.criterio}"/>
        <a href="#" class="btn  " id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>
        <a href="#" class="btn  " id="imprimir">
            <i class="icon-print"></i>
            Imprimir
        </a>
        <a href="#" class="btn" id="regresar">
            <i class="icon-arrow-left"></i>
            Regresar
        </a>
    </div>

</div>



<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr>

        <th style="width: 80px;">
            Código
        </th>
        <th style="width: 250px;">
            Nombre
        </th>
        <th style="width: 80px;">
            Tipo
        </th>
        <th style="width: 80px">
            Fecha Reg
        </th>
        <th style="width: 320px">
            Cantón-Parroquia-Comunidad
        </th>
        <th style="width: 100px">
            Valor
        </th>
        <th style="width: 80px">
            Of. Salida
        </th>
        <th style="width: 80px">
            MM Salida
        </th>
        <th style="width: 100px">
            Destino
        </th>
        <th style="width: 250px">
            Elaborado
        </th>
        <th style="width: 80px">
            Ingresada
        </th>
        <th style="width: 50px">
            Estado
        </th>

    </tr>
    </thead>


    <tbody id="tabla_material">

    <g:each in="${res}" var="obra" status="j">
        <tr class="obra_row" id="${obra.id}">
            <td>${obra.codigo}</td>
            <td>${obra.nombre}</td>
            <td>${obra.tipo}</td>
            <td><g:formatDate date="${obra.fecha}" format="dd-MM-yyyy"/></td>
            <td>${obra.canton} - ${obra.parroquia} - ${obra.comunidad}</td>
            <td style="text-align: right">${valoresTotales[j]}</td>
            <td>${obra.oficio}</td>
            <td>${obra.memo}</td>
            <td>${obra.destino}</td>
            <td>${obra.elaborado}</td>
            <td>${obra.ingreso}</td>
            <td>${obra.estado == "R"? "Registrada":"No registrada"}</td>

        </tr>


    </g:each>




    </tbody>
</table>



<script type="text/javascript">

    var checkeados = []

    $("#buscar").click(function(){

        var datos = "si=${"si"}&buscador=" + $("#buscador_reg").val() + "&estado=" + $("#estado_reg").val() + "&criterio=" + $("#criterio").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaRegistradas')}",
            data     : datos,
            success  : function (msg) {
                clearInterval(interval)
                $("#detalle").html(msg)
            }
        });
    });



    $("#regresar").click(function () {

        location.href = "${g.createLink(controller: 'reportes', action: 'index')}"

    });


    $("#imprimir").click(function () {


        location.href="${g.createLink(controller: 'reportes4', action:'reporteRegistradas' )}?buscador=" + $("#buscador_reg").val() + "&estado=" + $("#estado_reg").val() + "&criterio=" + $("#criterio").val()

    });

</script>