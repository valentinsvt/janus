<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <title>Información</title>


    <style type="text/css">
    .scroll {
        max-height: 310px;
        overflow: auto;
    }
    </style>
</head>


<body>

<g:if test="${delete == '1'}">
    <div class="alert alert-info">
        El item pertenece a ${rubro.size()} rubros,
        ${precios.size()} listas de precios y
        ${fpItems.size()} fórmulas polinómicas.
        <g:if test="${rubro.size()==0 && precios.size()==0 && fpItems.size()==0}">
            <a href="#" class="btn btn-danger"  id="btnEliminar" style="margin-left: 280px">
                <i class="icon-trash"></i> Eliminar
            </a>
        </g:if>
        <g:else>
            <div class="alert alert-danger" style="font-weight: bold">
                El item no puede eliminarse.
            </div>


        </g:else>

    </div>
</g:if>

<div id="tabs">
    <ul>
        <li><a href="#tab-rubro">Rubro</a></li>
        <li><a href="#tab-precioRubro">Precio Rubros</a></li>
        <li><a href="#tab-formula">Fórmula Polinómica</a></li>
    </ul>

    <div id="tab-rubro" class="tab">

        <div class="control-group scroll">

            <fieldset class="borde">
                <legend>Rubro</legend>

                <div class="span6" style="width: 700px">
                    <table class="table table-bordered table-striped table-hover table-condensed " id="tablaRubros">

                        <thead>
                        <tr>
                            <th style="width: 100px">Código</th>
                            <th style="width: 300px">Nombre</th>
                        </tr>

                        </thead>

                        <tbody id="bodyRubros">

                        <g:each in="${rubro}" var="r">
                            <tr>
                                <td style="width: 100px">${r?.rubro?.codigo}</td>
                                <td style="width: 300px">${r?.rubro?.nombre}</td>
                            </tr>
                        </g:each>


                        </tbody>

                    </table>
                </div>


            </fieldset>


        </div>


    </div>

    <div id="tab-precioRubro" class="tab">


        <div class="control-group scroll">

            <fieldset class="borde">
                <legend>Lista de precios</legend>



                <div class="span6" style="width: 700px">
                    <table class="table table-bordered table-striped table-hover table-condensed " id="tablaListaPrecios">

                        <thead>
                        <tr>
                            <th style="width: 100px">Fecha</th>
                            <th style="width: 100px">Precio</th>
                            <th style="width: 300px">Lista</th>
                        </tr>

                        </thead>

                        <tbody id="bodyListaPrecios">

                        <g:each in="${precios}" var="p">
                            <tr>
                                <td style="width: 100px"><g:formatDate date="${p?.fecha}" format="dd-MM-yyyy"/></td>
                                <td style="width: 100px">${p?.precioUnitario}</td>
                                <td style="width: 300px">${p?.lugar?.descripcion}</td>
                            </tr>
                        </g:each>


                        </tbody>

                    </table>
                </div>

            </fieldset>


        </div>


    </div>


    <div id="tab-formula" class="tab">



        <div class="control-group scroll">

            <fieldset class="borde">
                <legend>Fórmula Polinómica de la Obra</legend>



                <div class="span6" style="width: 700px">
                    <table class="table table-bordered table-striped table-hover table-condensed " id="tablaFormulaPolinomica">

                        <thead>
                        <tr>
                            <th style="width: 100px">F.P. Número</th>
                            <th style="width: 100px">Código</th>
                            <th style="width: 300px">Nombre de la Obra</th>
                        </tr>

                        </thead>

                        <tbody id="bodyFormulaPolinomica">

                        <g:each in="${fpItems}" var="f">
                            <tr>
                                <td style="width: 100px">${f?.formulaPolinomica?.numero}</td>
                                <td style="width: 100px">${f?.formulaPolinomica?.obra?.codigo}</td>
                                <td style="width: 300px">${f?.formulaPolinomica?.obra?.nombre}</td>
                            </tr>
                        </g:each>


                        </tbody>

                    </table>
                </div>




            </fieldset>


        </div>



    </div>






</div>


<script type="text/javascript">



    $("#tabs").tabs({
    });



    $("#btnEliminar").click(function () {

       ////console.log("entro");


        var idItem = ("it_") + ${item?.id};

        ////console.log(idItem);

        $.ajax({

                    type    : "POST",
                    url     : "${createLink(action: 'deleteIt_ajax')}",
                    data    : {
                        id: "${item?.id}"
                    },
                    success : function (msg) {

                        $("#tree").jstree('delete_node', $("#" + idItem));
                        $("#modal-tree").modal("hide");

                    }



    });

    });

</script>


</body>
</html>