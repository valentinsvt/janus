<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 11/15/13
  Time: 12:45 PM
--%>

<g:if test="${getCoordinador != null}">

    <div class="span12" id="directorSel" style="font-weight: bold; color: #4f5dff">Coordinador Actual: ${getCoordinador?.persona?.nombre + " " +  getCoordinador?.persona?.apellido}</div>

</g:if>
<g:else>
    <div class="span12" id="directorSel" style="font-weight: bold; color: #ff2a08">Coordinador Actual: El departamento seleccionado no cuenta con un coordinador asignado actualmente.</div>

</g:else>

<script type="text/javascript">

</script>