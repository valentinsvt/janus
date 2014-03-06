<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 06/03/14
  Time: 11:30 AM
--%>

    <g:select name="selMemo" from="${notaMemo}" value="${nota?.id}" optionValue="descripcion"
              optionKey="id" style="width: 300px;" noSelection="['-1':'Seleccione una nota...']" />

<script type="text/javascript">

    $("#selMemo").change(function () {
        loadNotaMemo();
    });

    function loadNotaMemo () {
        var idPie = $("#selMemo").val();
        console.log("id:" + idPie)
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: "${g.createLink(action: 'getDatosMemo')}",
            data: {id: idPie},
            success: function (msg){
                $("#descripcionMemo").val(msg.descripcion);
                $("#memo1").val(msg.texto);
                $("#memo2").val(msg.adicional);
            }
        })
    }


</script>
