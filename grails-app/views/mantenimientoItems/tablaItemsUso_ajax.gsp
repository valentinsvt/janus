<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 16/06/20
  Time: 10:56
--%>
<div class="" style="width: 99.7%;height: 650px; overflow-y: auto;float: right; margin-top: -20px">
    <table class="table table-bordered table-striped table-hover table-condensed" style="width: 100%">
        <g:each in="${items}" var="item">

            <tr data-id="${item.item__id}">
                <td style="width: 10%">
                    ${item.itemcdgo}
                </td>

                <td style="width: 46%">
                    ${item.itemnmbr}
                </td>

                <td style="width: 10%; text-align: center">
                    ${item.unddcdgo}
                </td>
                <td style="width: 10%; text-align: center">
                    ${item.itemfcha}
                </td>
                <td style="width: 10%; text-align: center">
                    ${item.itemetdo}
                </td>
                <td style="width: 15%; text-align: center" class="chk">
                    %{--<g:if test="${item.itemetdo == 'A'}">--}%
                        %{--<input type="checkbox" class="chequear" checked/>--}%
                    %{--</g:if>--}%
                    %{--<g:else>--}%
                        <input type="checkbox" class="chequear"/>
                    %{--</g:else>--}%
                </td>
            </tr>
        </g:each>
    </table>
</div>
