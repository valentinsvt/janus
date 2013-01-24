<g:set var="meses" value="${obra.plazo}"/>
<g:set var="sum" value="${0}"/>

<g:if test="${meses > 0}">
    <table class="table table-bordered table-condensed table-hover">
        <thead>
            <tr>
                <th>
                    Código
                </th>
                <th>
                    Rubro
                </th>
                <th>
                    Unidad
                </th>
                <th>
                    Cantidad
                </th>
                <th>
                    Unitario
                </th>
                <th>
                    C.Total
                </th>
                <th>
                    T.
                </th>
                <th>
                    Periodo
                </th>
                <th>
                    Total Rubro
                </th>
            </tr>
        </thead>
        <tbody id="tabla_material">

        </tbody>
        <tfoot>

        </tfoot>
    </table>
</g:if>
<g:else>
    <div class="alert alert-error">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
        <i class="icon-warning-sign icon-2x pull-left"></i>
        <h4>Error</h4>
        La obra tiene una planificación de 0 meses...Por favor corrija esto para continuar con el cronograma.
    </div>
</g:else>