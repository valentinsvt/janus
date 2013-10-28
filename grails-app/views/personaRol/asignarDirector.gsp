<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 10/28/13
  Time: 3:05 PM
--%>


<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">
    <script type="text/javascript"
            src="${resource(dir: 'js/jquery/plugins/box/js', file: 'jquery.luz.box.js')}"></script>
    <link href='${resource(dir: "js/jquery/plugins/box/css", file: "jquery.luz.box.css")}' rel='stylesheet'
          type='text/css'>

    <title>Asignar Director</title>
</head>

<body>

<div class="span6">

    <div class="span1" style="font-weight: bold">Dirección:</div>


    <g:select name="direccion" class="departamento" from="${listaDireccion}" optionValue="nombre"
              optionKey="id" style="width: 300px" noSelection="['-1': '-Escoja la direccion-']"/>

    <div class="span12" id="personasSel"></div>

    <hr>
    <div class="span4" id="funcionDiv" style="margin-top: 10px;">
        <div class="span2" style="margin-left: -50px; font-weight: bold">Función Director:</div>
        <elm:select name="funcion" id="funcion" from="${janus.Funcion?.findAllById(9)}" optionValue="descripcion" optionKey="id"
                    optionClass="${{ it?.descripcion }}" style="margin-left: -10px"/>
    </div>

    <div class="span2 btn-group" style="margin-left: -10px; margin-top: 10px;">
        <button class="btn btnAdicionar" id="adicionar"><i class="icon-plus"></i> Adicionar</button>
        %{--<button class="btn btnRegresar" id="regresar"><i class="icon-arrow-left"></i> Regresar</button>--}%

    </div>


    <div class="span12" style="width: 500px">

        <table class="table table-bordered table-striped table-hover table-condensed " id="tablaFuncion">

            <thead>
            <tr>
                <th style="width: 50px">N°</th>
                <th style="width: 250px">Función</th>
                <th style="width: 20px"><i class="icon-cut"></i></th>
            </tr>

            </thead>

            <tbody id="funcionPersona">

            </tbody>

        </table>

    </div>

</div>

<div class="span6">

</div>



<script type="text/javascript">


    $("#adicionar").click(function () {


          var idDireccion = $("#direccion").val()

        var existe

        $.ajax({

            type:'POST',
            url: "${g.createLink(controller: "personaRol", action: 'sacarFunciones')}",
            data: { id: idDireccion
            },
            success: function (msg) {

//                console.log("Existe")



                if(msg == '0' ){


                    var idPersona = $(".persona").val();

                    var valorAdicionar = $("#funcion option:selected").attr("class");
                    var idAcicionar = $("#funcion").val();


//        console.log("-->" + idAcicionar)


                    var tbody = $("#funcionPersona");
                    var rows = tbody.children("tr").length;
                    var continuar = true;

                    tbody.children("tr").each(function () {
                        var fila = $(this);
                        var id = fila.data("id");
                        var valor = fila.data("valor");

                        if (id == idAcicionar || valor == valorAdicionar) {
                            continuar = false;
                        }
                    });

                    if (continuar) {

                        //AJAX

                        $.ajax({
                            type: "POST",
                            url: "${g.createLink(controller: "personaRol", action: 'grabarFuncion')}",
                            data: { id: idPersona,

                                rol: idAcicionar
                            },
                            success: function (msg) {
                                var parts = msg.split("_");
                                if (parts[0] == "OK") {
                                    var tr = $("<tr>");
                                    var tdNumero = $("<td>");
                                    var tdFuncion = $("<td>");
                                    var tdAccion = $("<td>");
                                    var boton = $("<a href='#' class='btn btn-danger btnBorrar'><i class='icon-trash icon-large'></i></a>");
                                    var id = parts[1];
                                    boton.attr("id", id);
                                    boton.click(function () {
                                        borrar(boton);
                                    });
                                    tdAccion.append(boton);

                                    tr.data({
                                        id: idAcicionar,
                                        valor: valorAdicionar
                                    });

                                    tdNumero.html(rows + 1);
                                    tdFuncion.html(valorAdicionar);
                                    tr.append(tdNumero).append(tdFuncion).append(tdAccion);
                                    tbody.append(tr);
                                }
                            }
                        });


                    } else {
                        //avisar q ya existe

                        alert("La persona ya tiene asignado el rol de Director!")
                    }


                }

                else {

                    alert("Ya existe un director en esta dirección!")

                }

            }




        });



    });

    $("#regresar").click(function () {

        location.href = "${createLink(controller: 'persona', action: 'list')}";


    });

    function loadPersonas() {
        var idDep = $("#direccion").val();

                        console.log("dep-->>" + idDep)
        $.ajax({
            type: "POST",
            url: "${g.createLink(controller: 'personaRol', action:'getSeleccionados')}",
            data: {id: idDep

            },
            success: function (msg) {

                $("#personasSel").html(msg);

            }
        });
    }


    $("#direccion").change(function () {

        if ($("#direccion").val() != -1) {

            loadPersonas();


        }


    });







</script>

</body>
</html>