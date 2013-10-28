<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 10/28/13
  Time: 3:27 PM
--%>


<div class="span1" style="font-weight: bold">Persona:</div>
<g:select name="persona.id" class="persona" from="${personas}" optionValue="${{it.nombre + ' ' + it.apellido}}" optionKey="id"
          style="width: 300px"/>


<script type="text/javascript">



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
                            url: "${g.createLink(controller: "personaRol", action: 'delete')}",
                            data: { id:idRol},
                            success: function (msg) {
                                if(msg == "OK") {
                                    tr.remove();
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
            url: "${g.createLink(action: 'obtenerFuncion')}",
            data : { id: idPersona
            } ,
            success: function (msg) {
                $("#funcionPersona").html(msg);
            }
        });
    }


</script>