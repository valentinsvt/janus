<%--
  Created by IntelliJ IDEA.
  User: gato
  Date: 26/01/18
  Time: 10:34
--%>

$<g:formatNumber number="${contrato.anticipo}" minFractionDigits="2"
                 maxFractionDigits="2" format="##,##0" locale="ec"/>
(anticipo del <g:formatNumber number="${contrato.porcentajeAnticipo}"
                              maxFractionDigits="0" minFractionDigits="0"/>%
                                    de $<g:formatNumber number="${contrato.monto}" minFractionDigits="2"
                                                        maxFractionDigits="2" format="##,##0" locale="ec"/>)