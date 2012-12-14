<%--
  Created by IntelliJ IDEA.
  User: fabricio
  Date: 12/6/12
  Time: 3:11 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta name="layout" content="main">

    <title>Formato de Impresión</title>
</head>

<body>

<div id="tabs" style="width: 800px; height: 800px">

    <ul>

        <li><a href="#tab-presupuesto">Presupuesto</a></li>
        <li><a href="#tab-memorando">Memorando</a></li>
        <li><a href="#tab-polinomica">F. Polinómica</a></li>
        <li><a href="#tab-textosFijos">Textos Fijos</a></li>

    </ul>

    <div id="tab-presupuesto" class="tab">

        <div class="tipoReporte">

         <fieldset class="borde">

             <legend>Tipo de Reporte</legend>

             <div class="span6" style="margin-bottom: 10px">


                 <input type="radio" name="tipo" value="1" checked="checked" />  Base de Contrato

                 <input type="radio" name="tipo" value="2" style="margin-left: 220px"/> Presupuesto Referencial



             </div>
         </fieldset>

        </div>


           <div style="margin-top: 10px; margin-bottom: 20px">

            <g:checkBox name="forzar" checked="true"/> Forzar nueva Página para las Notas de Pie de Página

            <div class="span3">
            Tipo de Obra <g:textField name="tipoObra" style="width: 15px;height: 15px"/>
            </div>

                </div>


        <div class="piePagina" style="margin-top: 10px; margin-bottom: 10px">

            <fieldset class="borde">

                <legend>Pie de Página</legend>

                <div class="span6">

                    <g:select name="piePaginaSel" from="${""}" style="width: 350px"/>

                    <div class="btn-group" style="margin-left: 400px; margin-top: -40px; margin-bottom: 10px">
                        <button class="btn" id="btnNuevo">Nuevo</button>
                        <button class="btn" id="btnCancelar">Cancelar</button>
                        <button class="btn" id="btnEditar">Editar</button>
                        <button class="btn" id="btnAceptar">Aceptar</button>
                     </div>


                </div>

                <div class="span6">

                    <g:textField name="piePaginaEdit" style="width: 685px"/>

                </div>
                <div class="span6">


                    <g:textArea name="piePaginaArea" rows="5" cols="5" style="height: 125px; width:685px ; resize: none"/>

                </div>


                <div class="span6" style="margin-top: 5px; margin-bottom: 10px">

                    <g:checkBox name="notaAdicional" checked="true"/> Nota al Pie Adicional (15 líneas aprox)

                </div>

                <div class="span6">


                    <g:textArea name="piePaginaAdicional" rows="5" cols="5" style="height: 125px; width:685px ; resize: none"/>

                </div>



            </fieldset>



        </div>

        <div class="setFirmas">

            <fieldset class="borde">

                <legend>Set de Firmas</legend>


                <div class="span6">

                    <g:select name="setFirmas" from="${""}" style="width: 350px"/>

                    <div class="btn-group" style="margin-left: 400px; margin-top: -40px; margin-bottom: 10px">
                        <button class="btn" id="btnAdicionar">Adicionar</button>
                        <button class="btn" id="btnQuitar">Quitar</button>

                    </div>


                </div>

                <div class="span6" style="width: 700px">

                    <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmas">

                        <thead>
                        <tr>
                            <th style="width: 50px">N°</th>
                            <th style="width: 350px">Nombre</th>
                            <th style="width: 250px">Puesto</th>

                        </tr>

                        </thead>

                        <tbody>

                        </tbody>

                    </table>


                </div>


            </fieldset>



        </div>


    </div>

    <div id="tab-memorando" class="tab">

        <div class="tipoReporteMemo">

            <fieldset class="borde">

                <legend>Tipo de Reporte</legend>

                <div class="span6" style="margin-bottom: 10px">


                    <input type="radio" name="tipoMemo" value="1" checked="checked" />  Base de Contrato

                    <input type="radio" name="tipoMemo" value="2" style="margin-left: 220px"/> Presupuesto Referencial



                </div>
            </fieldset>

        </div>
       <div class="cabecera">

           <fieldset class="borde">
               <legend>Cabecera</legend>

               <div class="span6">
               <div class="span1"> Memo N°</div>
               <div class="span3"><g:textField name="numeroMemo"/></div>
               </div>

               <div class="span6">
                   <div class="span1"> DE: </div>
                   <div class="span3"><g:textField name="deMemo" style="width: 470px"/></div>
               </div>

               <div class="span6">
                   <div class="span1"> PARA: </div>
                   <div class="span3"><g:textField name="paraMemo" style="width: 470px"/></div>
               </div>

               <div class="span7">
                   <div class="span1"> Valor de la Base: </div>
                   <div class="span2"><g:textField name="baseMemo" style="width: 100px"/></div>

                   <div class="span1" style="margin-left: -30px"> Valor de Reajuste: </div>
                   <div class="span2"><g:textField name="reajusteMemo" style="width: 100px"/></div>


                   <div class="span1" style="margin-left: -30px"> % </div>
                   <div class="span1" style="margin-left: -30px"><g:textField name="porcentajeMemo" style="width: 35px"/>
                       %{--<button class="btn" id="btnCalcMemo"><i class="icon-calendar"></i></button>--}%
                   </div>



               </div>




           </fieldset>


       </div>

        <div class="texto">

            <fieldset class="borde">
                <legend>Texto</legend>

                <div class="span6">
                    <div class="span1">Texto</div>

                    <div class="span3"><g:textArea name="textoMemo" rows="4" cols="4" style="width: 600px; height: 55px; margin-left: -50px;resize: none;"/></div>

                </div>


                <div class="span6">
                    <div class="span1">Pie</div>

                    <div class="span3"><g:textArea name="pieMemo" rows="4" cols="4" style="width: 600px; height: 55px; margin-left: -50px; resize: none"/></div>

                </div>

                <div class="span6" style="margin-top: 10px">
                <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                    <button class="btn" id="btnEditarMemo">Editar</button>
                    <button class="btn" id="btnAceptarMemo">Aceptar</button>

                </div>
                </div>


            </fieldset>


        </div>



        <div class="setFirmas">

            <fieldset class="borde">

                <legend>Set de Firmas</legend>


                <div class="span6">

                    <g:select name="setFirmasMemo" from="${""}" style="width: 350px"/>

                    <div class="btn-group" style="margin-left: 400px; margin-top: -40px; margin-bottom: 10px">
                        <button class="btn" id="btnAdicionarMemo">Adicionar</button>
                        <button class="btn" id="btnQuitarMemo">Quitar</button>

                    </div>


                </div>

                <div class="span6" style="width: 700px">

                    <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmasMemo">

                        <thead>
                        <tr>
                            <th style="width: 50px">N°</th>
                            <th style="width: 350px">Nombre</th>
                            <th style="width: 250px">Puesto</th>

                        </tr>

                        </thead>

                        <tbody>

                        </tbody>

                    </table>


                </div>


            </fieldset>



        </div>



    </div>

    <div id="tab-polinomica" class="tab">

        <div class="textoFormula">

            <fieldset class="borde">

                <div class="span6" style="margin-top: 10px">
                    <div class="span2"> Fórmula Polinómica N°</div>
                    <div class="span3"><g:textField name="numeroFor"/></div>
                </div>

                <div class="span6">
                    <div class="span2"> Fecha de Lista de Precios: </div>
                    <div class="span3"><elm:datepicker name="fechaFor" style="width: 100px"/></div>
                </div>

                <div class="span6">
                    <div class="span2"> Monto del Contrato: </div>
                    <div class="span3"><g:textField name="montoFor"/></div>
                </div>


            </fieldset>


        </div>





        <div class="texto">

            <fieldset class="borde">
                <legend>Nota</legend>

                <div class="span6">
                   <div class="span3"><g:textArea name="textFor" rows="4" cols="4" style="width: 690px; margin-left: -30px; height: 70px; resize: none"/></div>

                </div>

                <div class="span6" style="margin-top: 10px">
                    <div class="btn-group" style="margin-left: 280px; margin-bottom: 10px">
                        <button class="btn" id="btnEditarFor">Editar</button>
                        <button class="btn" id="btnAceptarFor">Aceptar</button>

                    </div>
                </div>


            </fieldset>


        </div>


        <div class="setFirmas">

            <fieldset class="borde">

                <legend>Set de Firmas</legend>


                <div class="span6">

                    <g:select name="setFirmasFor" from="${""}" style="width: 350px"/>

                    <div class="btn-group" style="margin-left: 400px; margin-top: -40px; margin-bottom: 10px">
                        <button class="btn" id="btnAdicionarFor">Adicionar</button>
                        <button class="btn" id="btnQuitarFor">Quitar</button>

                    </div>


                </div>

                <div class="span6" style="width: 700px">

                    <table class="table table-bordered table-striped table-hover table-condensed" id="tablaFirmasFor">

                        <thead>
                        <tr>
                            <th style="width: 50px">N°</th>
                            <th style="width: 350px">Nombre</th>
                            <th style="width: 250px">Puesto</th>

                        </tr>

                        </thead>

                        <tbody>

                        </tbody>

                    </table>


                </div>


            </fieldset>



        </div>

    </div>

    <div id="tab-textosFijos" class="tab">


        <div class="cabecera">

            <fieldset class="borde">
                <legend>Cabecera</legend>

                <div class="span6">
                    <div class="span1">Título</div>

                    <div class="span3"><g:textField name="tituloTextos" style="width: 560px"/></div>

                </div>



                <div class="span6">
                    <div class="span1">General</div>
                </div>

                    <div class="span6">
                    <div class="span3"><g:textArea name="general" rows="4" cols="4" style="width: 665px; height: 130px; resize: none;"/></div>

                </div>


                <div class="span6">
                    <div class="span2">Base de Contratos</div>
                </div>

                <div class="span6">
                    <div class="span3"><g:textArea name="baseContratos" rows="4" cols="4" style="width: 665px; height: 35px; resize: none;"/></div>

                </div>


                <div class="span6">
                    <div class="span2">Presupuesto Referencial</div>
                </div>

                <div class="span6">
                    <div class="span3"><g:textArea name="presupuestoRefe" rows="4" cols="4" style="width: 665px; height: 35px; resize: none;"/></div>

                </div>





            </fieldset>


        </div>

        <div class="cabecera">

            <fieldset class="borde">
                <legend>Pie de Página</legend>

                <div class="span6">
                    <div class="span1">Retenciones</div>

                    <div class="span3"><g:textField name="retenciones" style="width: 560px"/></div>

                </div>



                <div class="span6">
                    <div class="span3">NOTA (15 líneas aproximadamente)</div>
                </div>

                <div class="span6">
                    <div class="span3"><g:textArea name="nota" rows="4" cols="4" style="width: 665px; height: 130px; resize: none;"/></div>

                </div>

            </fieldset>


        </div>



    </div>


<div class="btn-group" style="margin-bottom: 10px; margin-top: 20px; margin-left: 240px">
    <button class="btn" id="lista"><i class="icon-book"></i> Lista</button>
    <button class="btn" id="nuevo"><i class="icon-plus"></i> Nuevo</button>
    <button class="btn" id="aceptar"><i class="icon-ok"></i> Aceptar</button>
    <button class="btn" id="cancelarObra"><i class="icon-ban-circle"></i> Cancelar</button>
</div>




</div>

<script type="text/javascript">


    $("#tabs").tabs({
        heightStyle:"fill"
    });

</script>

</body>
</html>