<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 10/31/13
  Time: 3:33 PM
--%>


<g:if test="${obtenerDirector != null}">

    <div class="span12" id="directorSel" style="font-weight: bold; color: #4f5dff">Director Actual: ${obtenerDirector?.persona?.nombre + " " +  obtenerDirector?.persona?.apellido}</div>

</g:if>
<g:else>
    <div class="span12" id="directorSel" style="font-weight: bold; color: #ff2a08">Director Actual: La Dirección seleccionada no cuenta con un director asignado actualmente.</div>

</g:else>

<script type="text/javascript">

</script>