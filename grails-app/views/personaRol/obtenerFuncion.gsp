
<g:each in="${rol}" var="r" status="i" >
<tr data-id="${r?.funcionId}" data-valor="${r?.funcion?.descripcion}">

<td style="width: 50px">${i+1}</td>

<td style="width: 350px"> ${r?.funcion?.descripcion}</td>
<td style="width: 20px"> <a href='#' class='btn btn-danger btnBorrar' id="${r.id}"><i class='icon-trash icon-large'></i></a></td>


</tr>
</g:each>





<script type="text/javascript">

    $(".btnBorrar").click(function () {
        borrar($(this));
        /*
        function borrar($btn) {
            var tr = $btn.parents("tr");
            var idRol = $btn.attr("id"):
        }
         */

        %{--var tr =   $(this).parents("tr");--}%
        %{--var idRol = $(this).attr("id");--}%

        %{--$.box({--}%
            %{--imageClass: "box_info",--}%
            %{--text      : "Esta seguro que desea eliminar esta función de la persona seleccionada?",--}%
            %{--title     : "Confirmación",--}%
            %{--iconClose : false,--}%
            %{--dialog    : {--}%
                %{--resizable    : false,--}%
                %{--draggable    : false,--}%
                %{--closeOnEscape: false,--}%
                %{--buttons      : {--}%
                    %{--"Aceptar" : function () {--}%
                        %{--$.ajax({--}%
                            %{--type: "POST",--}%
                            %{--url: "${g.createLink(controller: "personaRol", action: 'delete')}",--}%
                            %{--data: { id:idRol},--}%
                            %{--success: function (msg) {--}%
                                %{--tr.remove();--}%
                            %{--}--}%

                        %{--});--}%
                    %{--},--}%
                    %{--"Cancelar": function () {--}%


                    %{--}--}%
                %{--}--}%
            %{--}--}%
        %{--});--}%

    });



</script>
