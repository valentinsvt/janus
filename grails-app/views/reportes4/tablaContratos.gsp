<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 8/27/13
  Time: 12:02 PM
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
        <g:select name="buscador" from="${['cdgo':'N° Contrato', 'memo': 'Memo', 'fcsb': 'Fecha Suscrip', 'tipo': 'Tipo Contrato', 'cncr': 'Concurso',
                                           'obra':'Obra', 'nmbr': 'Nombre', 'cntn':'Cantón', 'parr': 'Parroquia', 'clas':'Clase', 'mnto': 'Monto', 'cont': 'Contratista',
                                           'tppz':'Tipo Plazo', 'inic':'Fecha Inicio', 'fin':'Fecha Fin']}" value="${params.buscador}"
                  optionKey="key" optionValue="value" id="buscador_tra" style="width: 150px"/>
        <b>Fecha: </b>


        <g:set var="fechas" value="${['fcsb','inic','fin']}" />

        <g:if test="${fechas.contains(params.buscador)}">
            <elm:datepicker name="fecha" id="fecha_tra" value="${params.fecha}"/>
            <b>Criterio: </b>
            <g:textField name="criterio" id="criterio_tra" readonly="readonly" style="width: 250px; margin-right: 10px" value="${params.criterio}"/>
        </g:if>
        <g:else>

            <elm:datepicker name="fecha" id="fecha_tra" disabled="disabled" value="${params.fecha}"/>
            <b>Criterio: </b>
            <g:textField name="criterio" id="criterio_tra" style="width: 250px; margin-right: 10px" value="${params.criterio}"/>

        </g:else>
        <a href="#" class="btn  " id="buscar">
            <i class="icon-search"></i>
            Buscar
        </a>
        <a href="#" class="btn hide" id="imprimir">
            <i class="icon-print"></i>
            Imprimir
        </a>
        <a href="#" class="btn  hide" id="excel">
            <i class="icon-print"></i>
            Excel
        </a>
    </div>

</div>

<div style="width: 1100px; height: 500px; overflow-y:auto; overflow-x: auto" >


    <div style="width: 1800px; height: 500px; ">
        <table class="table table-bordered table-striped table-condensed table-hover">
            <thead>
            <tr>

                <th style="width: 100px;">
                    N° Contrato
                </th>
                <th style="width: 60px;">
                    Suscripción
                </th>
                <th style="width: 280px">
                    Concurso
                </th>
                <th style="width: 180px">
                    Obra
                </th>
                <th style="width: 350px">
                    Nombre de la Obra
                </th>
                <th style="width: 80px">
                    Parroquia
                </th>
                <th style="width: 100px">
                    Cantón
                </th>
                <th style="width: 120px">
                    Clase de Obra
                </th>
                <th style="width: 250px">
                    Tipo de Contrato
                </th>
                <th style="width: 80px">
                    Monto
                </th>
                <th style="width: 80px">
                    %
                </th>
                <th style="width: 80px">
                    Anticipo
                </th>
                <th style="width: 80px">
                    Contratista
                </th>
                <th style="width: 80px">
                    Fecha Inicio
                </th>
                <th style="width: 80px">
                    Fecha Fin
                </th>
                <th style="width: 80px">
                    Plazo
                </th>


            </tr>
            </thead>


            <tbody id="tabla_material">

            %{--<g:if test="${params.criterio || params.fecha}">--}%
            <g:if test="${params.buscador != 'undefined'}">
                <g:each in="${res}" var="cont" status="j">
                    <tr class="obra_row" id="${cont.id}">
                        <td>${cont.codigo}</td>
                        <td><g:formatDate date="${cont.fechasu}" format="dd-MM-yyyy"/></td>
                        %{--<td>${cont.memo}</td>--}%
                        <td>${cont.concurso}</td>
                        <td>${cont.obracodigo}</td>
                        <td>${cont.obranombre} </td>
                        <td>${cont.parroquia}</td>
                        <td>${cont.canton}</td>
                        <td>${cont.tipoobra}</td>
                        <td>${cont.tipocontrato}</td>
                        <td>${cont.monto}</td>
                        <td>${cont.porcentaje}</td>
                        <td>${cont.anticipo}</td>
                        <td>${cont.nombrecontra}</td>
                        <td><g:formatDate date="${cont.fechainicio}" format="dd-MM-yyyy"/></td>
                        <td><g:formatDate date="${cont.fechafin}" format="dd-MM-yyyy"/></td>
                        <td>${cont.plazo}</td>

                    </tr>


                </g:each>
            </g:if>
            %{--</g:if>--}%


            </tbody>
        </table>

    </div>
</div>



<script type="text/javascript">




    function validarNumDec(ev) {
        /*
         48-57      -> numeros
         96-105     -> teclado numerico
         188        -> , (coma)
         190        -> . (punto) teclado
         110        -> . (punto) teclado numerico
         8          -> backspace
         46         -> delete
         9          -> tab
         37         -> flecha izq
         39         -> flecha der
         */
        return ((ev.keyCode >= 48 && ev.keyCode <= 57) ||
        (ev.keyCode >= 96 && ev.keyCode <= 105) ||
        ev.keyCode == 8 || ev.keyCode == 46 || ev.keyCode == 9 ||
        ev.keyCode == 37 || ev.keyCode == 39 || ev.keyCode == 190 || ev.keyCode == 110);
    }


    var val
    var dec

    $("#criterio_tra").keydown(function (ev) {

        val = $(this).val();

        dec = 6

        if($("#buscador_tra").val() == 'mnto'){

            if (ev.keyCode == 110 || ev.keyCode == 190) {

                if (!dec) {
                    return false;
                } else {
                    if (val.length == 0) {
                        $(this).val("0");
                    }
                    if (val.indexOf(".") > -1) {
                        return false;
                    }
                }

            } else {


                if (val.indexOf(".") > -1) {
                    if (dec) {
                        var parts = val.split(".");
                        var l = parts[1].length;
                        if (l >= dec) {
                            return false;
                        }
                    }
                } else {
                    return validarNumDec(ev);
                }


            }


            return validarNumDec(ev);
        }
        return true
    }).keyup(function () {



    });



    var checkeados = []

    $("#buscar").click(function(){

        var datos = "si=${"si"}&buscador=" + $("#buscador_tra").val() + "&criterio=" + $("#criterio_tra").val() + "&fecha=" + $("#fecha_tra").val()
        var interval = loading("detalle")
        $.ajax({type : "POST", url : "${g.createLink(controller: 'reportes4',action:'tablaContratos')}",
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


        location.href="${g.createLink(controller: 'reportes4', action:'reporteContratos' )}?buscador=" + $("#buscador_tra").val() + "&criterio=" + $("#criterio_tra").val()

    });
    $("#excel").click(function () {


        location.href="${g.createLink(controller: 'reportes4', action:'reporteExcelContratos' )}?buscador=" + $("#buscador_tra").val() + "&criterio=" + $("#criterio_tra").val()

    });

    $("#buscador_tra").change(function () {

        if($(this).val() == 'fcsb' || $(this).val() == 'inic' || $(this).val() == 'fin'){

            $("#fecha_tra").removeAttr("disabled")

            $("#criterio_tra").attr("readonly", "true").val("")
        }
        else {


            if($(this).val() == 'mnto'){

                $("#criterio_tra").val("")

            }


            $("#fecha_tra").attr("disabled", true).val("")
            $("#criterio_tra").removeAttr("readonly")




        }


    })

</script>