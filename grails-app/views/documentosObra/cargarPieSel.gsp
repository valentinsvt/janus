<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 2/11/14
  Time: 4:35 PM
--%>


<g:select name="piePaginaSel" from="${nota}" value="${idFinal}" optionValue="descripcion"
          optionKey="id" style="width: 300px;" noSelection="['-1':'Seleccione una nota...']" />


<script type="text/javascript">

    $("#piePaginaSel").change(function () {

        loadNota();
    });



    function loadNota() {
        var idPie = $("#piePaginaSel").val();

        $.ajax({
            type: "POST",
            dataType: 'json',
            url: "${g.createLink(action:'getDatos')}",
            data: {id: idPie},
            success: function (msg) {

                $("#descripcion").val(msg.descripcion);
                $("#texto").val(msg.texto);
                $("#adicional").val(msg.adicional);

            }
        });
    }

</script>

