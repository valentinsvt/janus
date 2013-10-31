<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 10/28/13
  Time: 3:37 PM
--%>


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


    });



</script>
