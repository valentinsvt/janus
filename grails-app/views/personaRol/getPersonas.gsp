



<div class="span1" style="font-weight: bold; margin-left: -1px">Persona:</div>
<g:select name="persona.id" class="persona" from="${personas}" optionValue="${{it.nombre + ' ' + it.apellido}}" optionKey="id"
style="width: 400px"/>


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


    %{--$("#adicionar").click(function () {--}%

        %{--var idPersona = $(".persona").val();--}%

        %{--var valorAdicionar = $("#funcion option:selected").attr("class");--}%
        %{--var idAcicionar =   $("#funcion").val();--}%


        %{--var tbody = $("#funcionPersona");--}%
        %{--var rows = tbody.children("tr").length;--}%
        %{--var continuar = true;--}%

        %{--tbody.children("tr").each(function() {--}%
            %{--var fila = $(this);--}%
            %{--var id = fila.data("id");--}%
            %{--var valor = fila.data("valor");--}%

            %{--if(id == idAcicionar || valor == valorAdicionar) {--}%
                %{--continuar = false;--}%
            %{--}--}%
        %{--});--}%

        %{--if(continuar) {--}%

            %{--//AJAX--}%

            %{--$.ajax({--}%
                %{--type: "POST",--}%
                %{--url: "${g.createLink(controller: "personaRol", action: 'grabarFuncion')}",--}%
                %{--data: { id: idPersona,--}%

                    %{--rol:idAcicionar--}%
                %{--},--}%
                %{--success: function (msg) {--}%
                    %{--var parts = msg.split("_");--}%
                    %{--if(parts[0] == "OK") {--}%
                        %{--var tr = $("<tr>");--}%
                        %{--var tdNumero = $("<td>");--}%
                        %{--var tdFuncion = $("<td>");--}%
                        %{--var tdAccion = $("<td>");--}%
                        %{--var boton = $("<a href='#' class='btn btn-danger btnBorrar'><i class='icon-trash icon-large'></i></a>");--}%
                        %{--var id = parts[1];--}%
                        %{--boton.attr("id",id);--}%
                        %{--boton.click(function() {--}%
                            %{--borrar(boton);--}%
                        %{--});--}%
                        %{--tdAccion.append(boton);--}%

                        %{--tr.data({--}%
                            %{--id:  idAcicionar ,--}%
                            %{--valor: valorAdicionar--}%
                        %{--});--}%

                        %{--tdNumero.html(rows+1);--}%
                        %{--tdFuncion.html(valorAdicionar);--}%
                        %{--tr.append(tdNumero).append(tdFuncion).append(tdAccion);--}%
                        %{--tbody.append(tr);--}%
                    %{--}--}%
                %{--}--}%
            %{--});--}%


        %{--} else {--}%
            %{--//avisar q ya existe--}%
        %{--}--}%

    %{--});--}%

</script>