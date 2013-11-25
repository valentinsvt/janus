
<g:each in="${filas}" var="f" status="j">
    <tr style="font-size: 10px !important;" class="item_row ${(j%2==0)?'gris':''} fila_${j+offset}" color="${(j%2==0)?'gris':'blanco'}" fila="fila_${j+offset}" num="${j+offset}"  >
        <g:each in="${f}" var="v" status="i">
            <g:if test="${i<5}">
            <td style='${(i==2)?"width: 100px":(i>4)?"width:150px;text-align:right":""}' class="col_${i}" col="${i}">${v}</td>
            </g:if>
            <g:else>
                <td style='${(i==2)?"width: 100px":(i>4)?"width:150px;text-align:right":""}' class="col_${i} ${(indices['mano'].contains(i))?'mano':''} ${(indices['saldo'].contains(i))?'saldo':''}  ${(indices['total'].contains(i))?'totalCol':''} " col="${i}">${(v.toString()!="0.000000")?v:""}</td>
            </g:else>
        </g:each>
    </tr>
</g:each>


