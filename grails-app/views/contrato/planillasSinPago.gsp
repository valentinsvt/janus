<%--
  Created by IntelliJ IDEA.
  User: svt
  Date: 2/26/2015
  Time: 11:55 AM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">


    <title>Planillas y Pagos</title>
</head>
<body>
<div class="row">
    <div class="col-md-12">
        <h1>
            Planillas y Pagos
        </h1>
    </div>
    Detalle de planillas odenado por fecha de ingreso.
</div>
<div class="row" style="margin-top:15px">
    <div class="col-md-12">
        <table class="table table-hover table-bordered table-striped">
            <thead>
            <tr>
                <th>Contrato</th>
                <th>Contratista</th>
                <th>Tipo</th>
                <th>Ingreso</th>
                <th>Presentación</th>
                <th>Valor</th>
                <th>Memorando<br/> pedido de pago</th>
                <th>Fecha <br/>Mem. pedido</th>
                <th>Fecha de acreditación</th>

            </tr>
            </thead>
            <tbody>
            <g:each in="${planillas}" var="p" status="i">
                <tr>
                    <td>${p.contrato?.codigo}</td>
                    <td>${p.contrato?.oferta.proveedor.nombre}</td>
                    <td>${p.tipoPlanilla?.nombre}</td>
                    <td style="text-align: center">${p.fechaIngreso?.format("dd-MM-yyyy")}</td>
                    <td style="text-align: center">${p.fechaPresentacion?.format("dd-MM-yyyy")}</td>
                    <td style="text-align: right">
                        <g:formatNumber number="${p.valor}" type="currency"/>
                    </td>
                    <td>${p.memoPedidoPagoPlanilla}</td>
                    <td style="text-align: center">${p.fechaMemoPedidoPagoPlanilla?.format("dd-MM-yyyy")}</td>
                    <td style="text-align: center">${p.fechaPago?.format("dd-MM-yyyy")}</td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>