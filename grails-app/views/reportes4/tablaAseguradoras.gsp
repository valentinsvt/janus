<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 8/26/13
  Time: 3:14 PM
--%>

<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 8/23/13
  Time: 11:26 AM
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
        <g:select name="buscador" from="${['nmbr':'Nombre', 'tipo': 'Tipo', 'telf': 'Teléfono'
                , 'faxx': 'Fax', 'rspn':'Contacto', 'dire':'Dirección']}" value="${params.buscador}"
                  optionKey="key" optionValue="value" id="buscador_as" style="width: 150px"/>
        <b>Criterio: </b>
        <g:textField name="criterio" id="criterio_as" style="width: 250px; margin-right: 10px" value="${params.criterio}"/>
        <a href="#" class="btn  " id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>
        <a href="#" class="btn hide" id="imprimir">
            <i class="icon-print"></i>
            Imprimir
        </a>
        <a href="#" class="btn hide" id="excel">
            <i class="icon-table"></i>
            Excel
        </a>
    </div>

</div>



<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr>

        <th style="width: 250px;">
            Nombre
        </th>
        <th style="width: 120px;">
            Tipo
        </th>
        <th style="width: 110px;">
            Teléfono
        </th>
        <th style="width: 110px">
            Fax
        </th>
        <th style="width: 250px">
            Dirección
        </th>
        <th style="width: 200px">
            Observaciones
        </th>
        <th style="width: 170px">
            Contacto
        </th>
        <th style="width: 80px">
            Fecha Contacto
        </th>


    </tr>
    </thead>


    <tbody id="tabla_material">

    <g:if test="${params.buscador != 'undefined'}">


        <g:each in="${res}" var="aseg" status="j">
        <tr class="obra_row" id="${aseg.id}">
            <td>${aseg.nombre}</td>
            <td>${aseg.tipoaseguradora}</td>
            <td>${aseg.telefono}</td>
            <td>${aseg.fax}</td>
            <td>${aseg.direccion}</td>
            <td>${aseg.observaciones}</td>
            <td>${aseg.contacto}</td>
            <td><g:formatDate date="${aseg.fecha}" format="dd-MM-yyyy"/></td>

        </tr>


    </g:each>

    </g:if>


    </tbody>
</table>



<script type="text/javascript">

    var checkeados = []

    $("#buscar").click(function(){

        var datos = "si=${"si"}&buscador=" + $("#buscador_as").val() + "&criterio=" + $("#criterio_as").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaAseguradoras')}",
            data     : datos,
            success  : function (msg) {
                clearInterval(interval)
                $("#detalle").html(msg)
                $("#imprimir").removeClass("hide");
                $("#excel").removeClass("hide");
            }
        });
    });



    $("#regresar").click(function () {

        location.href = "${g.createLink(controller: 'reportes', action: 'index')}"

    });


    $("#imprimir").click(function () {


        location.href="${g.createLink(controller: 'reportes4', action:'reporteAseguradoras' )}?buscador=" + $("#buscador_as").val() + "&criterio=" + $("#criterio_as").val()

    });

    $("#excel").click(function () {


        location.href="${g.createLink(controller: 'reportes4', action:'reporteExcelAseguradoras' )}?buscador=" + $("#buscador_as").val() + "&criterio=" + $("#criterio_as").val()

    });

</script>