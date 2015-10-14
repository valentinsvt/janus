<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 8/26/13
  Time: 4:26 PM
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
        <g:select name="buscador" from="${['nmbr':'Nombre', '_ruc': 'Cédula'
                , 'espe': 'Especialidad',]}" value="${params.buscador}"
                  optionKey="key" optionValue="value" id="buscador_cont" style="width: 150px"/>
        <b>Criterio: </b>
        <g:textField name="criterio" id="criterio_cont" style="width: 250px; margin-right: 10px" value="${params.criterio}"/>
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

<div style="width: 1000px; height: 500px; overflow-y:auto; overflow-x: auto" >


<div style="width: 1800px; height: 500px;">
<table class="table table-bordered table-striped table-condensed table-hover">
    <thead>
    <tr>

        <th style="width: 100px;">
            Cédula/RUC
        </th>
        <th style="width: 60px;">
            Sigla
        </th>
        <th style="width: 60px">
            Tit
        </th>
        <th style="width: 300px">
            Nombre
        </th>
        <th style="width: 180px">
            Especialidad
        </th>
        <th style="width: 280px">
           Contacto
        </th>
        <th style="width: 80px">
            Cámara Const.
        </th>
        <th style="width: 400px">
            Dirección
        </th>
        <th style="width: 120px">
            Teléfono
        </th>
        <th style="width: 280px">
            Garante
        </th>
        <th style="width: 80px">
            Fecha Cont.
        </th>
        <th style="width: 80px">
            Fecha Contrato.
        </th>



    </tr>
    </thead>


    <tbody id="tabla_material">

    <g:if test="${params.buscador != 'undefined'}">


        <g:each in="${res}" var="cont" status="j">
        <tr class="obra_row" id="${cont.id}">
            <td>${cont.ruc}</td>
            <td>${cont.sigla}</td>
            <td>${cont.titulo}</td>
            <td>${cont.nombre}</td>
            <td>${cont.especialidad}</td>
            <td>${cont.nombrecon + " " + cont.apellidocon} </td>
            <td>${cont.camara}</td>
            <td>${cont.direccion}</td>
            <td>${cont.telefono}</td>
            <td>${cont.garante}</td>
            <td><g:formatDate date="${cont.fecha}" format="dd-MM-yyyy"/></td>
            <td><g:formatDate date="${cont.fechacontrato}" format="dd-MM-yyyy"/></td>

        </tr>


    </g:each>

    </g:if>


    </tbody>
</table>

</div>
</div>



<script type="text/javascript">

    var checkeados = []

    $("#buscar").click(function(){

        var datos = "si=${"si"}&buscador=" + $("#buscador_cont").val() + "&criterio=" + $("#criterio_cont").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaContratistas')}",
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


        location.href="${g.createLink(controller: 'reportes4', action:'reporteContratistas' )}?buscador=" + $("#buscador_cont").val() + "&criterio=" + $("#criterio_cont").val()

    });
    $("#excel").click(function () {


        location.href="${g.createLink(controller: 'reportes4', action:'reporteExcelContratistas' )}?buscador=" + $("#buscador_cont").val() + "&criterio=" + $("#criterio_cont").val()

    });

</script>