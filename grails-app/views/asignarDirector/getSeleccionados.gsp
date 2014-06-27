<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 10/28/13
  Time: 3:27 PM
--%>




<div class="span1" style="font-weight: bold">Persona:</div>
<g:select name="persona.id" class="persona" from="${personas}" optionValue="${{it.nombre + ' ' + it.apellido}}" optionKey="id"
          style="width: 400px"/>


<script type="text/javascript">

cargarMensaje();


    //borrar
    function borrar($btn) {
        var tr = $btn.parents("tr");
        var idRol = $btn.attr("id");

        $.box({
            imageClass: "box_info",
            text      : "Esta seguro que desea eliminar esta función de la persona seleccionada?",
            title     : "Confirmación",
            iconClose : false,
            dialog    : {
                resizable    : false,
                draggable    : false,
                closeOnEscape: false,
                buttons      : {
                    "Aceptar" : function () {
                        $.ajax({
                            type: "POST",
                            url: "${g.createLink(controller: "asignarDirector", action: 'delete')}",
                            data: { id:idRol},
                            success: function (msg) {
                                if(msg == "OK") {

                                    tr.remove();

                                    var confirmacion = $("#confirmacion")

                                    var dir = $("<div class='span12' id='directorSel' style='font-weight: bold; color: #ff2a08'>Director Actual: La Dirección seleccionada no cuenta con un director asignado actualmente.</div>");


                                    confirmacion.html(dir)
                                }
                            }

                        });
                    },
                    "Cancelar": function () {


                    }
                }
            }
        });
    }


    if($(".persona").val() != null){
        cargarFuncion();
    }else {
        $("#funcionPersona").html("");
    }



    $(".persona").change(function () {
        cargarFuncion();



    });

    function cargarFuncion () {

        var idPersona = $(".persona").val();
        $.ajax({
            type: "POST",
            url: "${g.createLink(action: 'obtenerFuncionDirector')}",
            data : { id: idPersona
            } ,
            success: function (msg) {

                $("#funcionPersona").html(msg);
            }
        });
    }


</script>