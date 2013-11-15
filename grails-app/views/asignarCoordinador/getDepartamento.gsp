<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 11/14/13
  Time: 4:40 PM
--%>


<div class="span1" style="font-weight: bold; margin-left: -1px">Departamento:</div>
<g:select name="departamento.id" id="departamento" class="departamento" from="${departamento}" optionValue="descripcion" optionKey="id"
          style="width: 400px; margin-left: 30px"/>


<div class="span12 " id="personasSel"></div>


<script type="text/javascript">

    cargarPersonas();
    cargarMensaje()

    function cargarPersonas() {

        var idDep = $("#departamento").val();

//                        console.log("dep-->>" + idDep)
        $.ajax({
            type: "POST",
            url: "${g.createLink(controller: 'asignarCoordinador', action:'getPersonas')}",
            data: {id: idDep

            },
            success: function (msg) {

                $("#personasSel").html(msg);

            }
        });
    }


    $("#departamento").change(function () {

        if ($("#departamento").val() != -1) {

            cargarPersonas();
            cargarMensaje()

        }else {

//            $("#personasSel").html("");

            var confirmacion = $("#confirmacion")

            var dir = $("<div class='span12' id='directorSel' style='font-weight: bold'> </div>");

            confirmacion.html(dir)

            var comboPersonas = $("#personasSel")

            comboPersonas.html(dir)

            var bodyTabla = $("#funcionPersona")

            bodyTabla.html("")

        }





    });


</script>
