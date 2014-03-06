<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 06/03/14
  Time: 01:48 PM
--%>

<g:select name="selFormu" from="${notaFormu}" value="${nota?.id}" optionValue="descripcion"
          optionKey="id" style="width: 300px;" noSelection="['-1':'Seleccione una nota...']" />

<script type="text/javascript">
    $("#selFormu").change(function () {
        loadNotaFormu();
    });

    function loadNotaFormu () {
        var idPie = $("#selFormu").val();
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: "${g.createLink(action: 'getDatosFormu')}",
            data: {id: idPie},
            success: function (msg){
                $("#descripcionFormu").val(msg.descripcion);
                $("#notaFormula").val(msg.texto);
            }
        })
    }
</script>