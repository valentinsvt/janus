<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 9/13/13
  Time: 4:29 PM
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
                  optionKey="key" optionValue="value" id="buscador_reg1" style="width: 150px"/>
        %{--<b style="margin-left: 10px">Estado: </b>--}%
        %{--<g:select name="estado" from="${['1':'Todas', '2':'Ingresadas', '3':'Registradas']}" optionKey="key"--}%
        %{--optionValue="value" id="estado_reg" value="${params.estado}" style="width: 150px"/>--}%
        <b>Criterio: </b>
        <g:textField name="criterio" style="width: 250px; margin-right: 10px" value="${params.criterio}"/>
        <a href="#" class="btn" id="buscar1">
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

        <th style="width: 80px;">
            Código
        </th>
        <th style="width: 250px;">
            Nombre
        </th>
        <th style="width: 120px;">
            Tipo
        </th>
        <th style="width: 80px">
            Fecha Reg
        </th>
        <th style="width: 250px">
            Cantón-Parroquia-Comunidad
        </th>
        <th style="width: 100px">
            Valor
        </th>
        <th style="width: 230px">
            Elaborado
        </th>
        <th style="width: 70px">
            Doc.Referencia
        </th>
        <th style="width: 100px">
            Estado
        </th>

    </tr>
    </thead>


    <tbody id="tabla_material">

    %{--<g:if test="${params.criterio || bandera == 1}">--}%
    <g:if test="${params.buscador != 'undefined'}">

        <g:each in="${res}" var="obra" status="j">
            <tr class="obra_row" id="${obra.id}">
                <td>${obra.codigo}</td>
                <td>${obra.nombre}</td>
                <td>${obra.tipoobra}</td>
                <td><g:formatDate date="${obra.fecha}" format="dd-MM-yyyy"/></td>
                <td>${obra.canton} - ${obra.parroquia} - ${obra.comunidad}</td>
                <td style="text-align: right">${valoresTotales[j]}</td>
                <td>${obra.elaborado}</td>
                <td>${obra.ingreso}</td>
                <td>${obra.estado == "R"? "Registrada":"No registrada"}</td>

            </tr>


        </g:each>

    </g:if>


    </tbody>
</table>




<script type="text/javascript">

    var checkeados = []

    $("#buscar1").click(function(){

        %{--var datos = "si=${"si"}&buscador=" + $("#buscador_reg").val() + "&estado=" + $("#estado_reg").val() + "&criterio=" + $("#criterio").val()--}%
        var datos = "si=${"si"}&buscador=" + $("#buscador_reg1").val() + "&estado=" + ${1} + "&criterio=" + $("#criterio").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaPresupuestadas')}",
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


        %{--location.href="${g.createLink(controller: 'reportes4', action:'reporteRegistradas' )}?buscador=" + $("#buscador_reg").val() + "&estado=" + $("#estado_reg").val() + "&criterio=" + $("#criterio").val()--}%
        location.href="${g.createLink(controller: 'reportes4', action:'reportePresupuestadas' )}?buscador=" + $("#buscador_reg1").val() + "&estado=" + ${1} + "&criterio=" + $("#criterio").val()

    });

    $("#excel").click(function () {


        %{--location.href="${g.createLink(controller: 'reportes4', action:'reporteRegistradasExcel' )}?buscador=" + $("#buscador_reg").val() + "&estado=" + $("#estado_reg").val() + "&criterio=" + $("#criterio").val()--}%
        location.href="${g.createLink(controller: 'reportes4', action:'reporteExcelPresupuestadas' )}?buscador=" + $("#buscador_reg1").val() + "&estado=" + ${1} + "&criterio=" + $("#criterio").val()

    });


    $.contextMenu({
        selector: '.obra_row',
        callback: function (key, options) {

            var m = "clicked: " + $(this).attr("id");

            var idFila = $(this).attr("id")

//            console.log("id:" + idFila)


            if(key == "registro"){

                location.href = "${g.createLink(controller: 'obra', action: 'registroObra')}" + "?obra=" + idFila;


            }



            %{--if (key == "print") {--}%

                %{--var datos = "?obra="+idFila+"Wsub="+${-1}--}%
                %{--var url = "${g.createLink(controller: 'reportes3',action: 'imprimirTablaSub' )}" + datos--}%
                %{--location.href = "${g.createLink(controller: 'pdf',action: 'pdfLink')}?url=" + url--}%

            %{--}--}%


        },
        items: {

            "registro": {name: "Ir al Registro de esta Obra", icon:"info"}

//            "print": {name: "Imprimir Subpresupuesto", icon: "print"


//            }

        }

    });





</script>