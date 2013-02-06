<g:set var="meses" value="${obra.plazo}"/>
<g:set var="sum" value="${0}"/>

<g:if test="${meses > 0}">
   ${tabla}
</g:if>
<g:else>
    <div class="alert alert-error">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <i class="icon-warning-sign icon-2x pull-left"></i>
        <h4>Error</h4>
        La obra tiene una planificación de 0 meses...Por favor corrija esto para continuar con el cronograma.
    </div>
</g:else>