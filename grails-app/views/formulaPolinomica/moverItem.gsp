<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 17/04/17
  Time: 13:53
--%>
<div>
    %{--<div class="row">--}%
        %{--<div class="span">--}%
            %{--Cambiar <strong>${formula.indice.descripcion}</strong>--}%
        %{--</div>--}%
    %{--</div>--}%

    <div class="row">
        <div class="span">
            Mover Item al coeficiente:
            <g:select name="indice" from="${cof?.numero}" class="span4"/>
        </div>
    </div>

    %{--<div class="row">--}%
        %{--<div class="span">--}%
            %{--Modificar valor &nbsp;&nbsp;&nbsp;--}%
            %{--<input type="number" step="0.001" pattern="#.###"/>--}%
            %{--<g:field type="number" name="valor" step="0.001" pattern="#.###" value="${formula.valor}" class="input-mini"/>--}%
            %{--(suma <g:formatNumber number="${total}" format="##,##0.#####" locale="ec"/>)--}%
        %{--</div>--}%
    %{--</div>--}%
</div>