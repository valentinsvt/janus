<!DOCTYPE html>
<html>
    <head>

        %{--<meta name="layout" content="main">--}%

        <title>Manual de Usuario - Ejecuci&oacute;n</title>
        <style>
        * {
            max-width : 1100px;
            margin    : auto;
        }

        div {
            margin : auto;
        }

        #header2 {
            z-index          : 1;
            position         : relative;
            width            : 97.5%;
            height           : 60px;
            text-align       : center;
            background-color : #ffffff;
            margin-top       : -9px;
        }

        .centrado {
            text-align : center;
        }

        #indice {
            width            : 650px;
            position         : relative;
            background-color : #c8cac9;
            font-family      : Verdana, sans-serif;
            font-size        : 14px;
            color            : #000000;
            margin           : auto;
            text-align       : justify;
        }

        p {
            text-align : justify;
        }

        a {
            text-decoration : none;
            color           : #000000;
        }

        a:hover {
            font-weight : bold;
        }

        .cursiva {
            font-style : italic;
        }

        .regresa {
            float            : right;
            background-color : #c8cac9;
            width            : 120px;
            border           : 2px solid black;
            border-radius    : 10px;
        }

        .regresa p {
            text-align : center;
        }

        .izquierda {
            text-align : left;
        }

        .boton {
            position : relative;
            top      : 12px;
        }

        </style>
    </head>

    <body>
        <div id="header2">
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'encabezado.png')}"/>
        </div>

        <div class="centrado">
            <br><br><br><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'logo.png')}"/>
            <br><br>

            <h3>Sistema Integrado de Gesti&oacuten de Proyectos, Fiscalizaci&oacuten,
            Contrataci&oacuten y Ejecuci&oacuten de Obras</h3>
            <br><a id="volverIndice"></a>

            <h2>Manual del Usuario</h2><br>
            <h4>Secci&oacuten Contratos, Planillas y Portal de Oferentes Ganadores</h4>
            <br>
        </div>

        <div id="indice">
            <h3>&Iacutendice de contenido</h3> <br>
            <ol>
                <li><a href="#ModCompras">M&oacutedulo de Compras P&uacuteblicas</a></li>
                <ul>
                    <li><a href="#ModCompras">Asignaciones Presupuestarias</a></li>
                    <li><a href="#PlanAnual">Plan Anual de Compras</a></li>
                </ul>
                <li><a href="#Concursos">Proceso</a></li>
                <li><a href="#Contratos">Contratos</a></li>
                <ul>
                    <li><a href="#CronoCon">Cronograma contractual</a></li>
                    <li><a href="#RegFormula">Registro de la f&oacutermula polin&oacutemica del contrato</a></li>
                    <li><a href="#BiblioCont">Biblioteca de Contratos</a></li>
                </ul>
                <li><a href="#RegControl">Registro y control de Planillas</a></li>
                <ul>
                    <li><a href="#RegIndices">Registro de &Iacutendices</a></li>
                    <li><a href="#RegDatos">Registro de datos desde el INEC</a></li>
                    <li><a href="#ProDes">Proceso para descargar el archivo desde el INEC</a></li>
                    <li><a href="#RegCron">Registro del cronograma de ejecuci&oacuten</a></li>
                    <li><a href="#EstPlan">Estado de la planilla</a></li>
                    <li><a href="#TipoPlan">Tipo de Planilla</a></li>
                    <li><a href="#TipoDesc">Tipo de descuentos por planilla</a></li>
                    <li><a href="#DescPlan">Descuentos por planilla</a></li>
                    <li><a href="#RegPlan">Registro de Planillas</a></li>
                    <li><a href="#IngNueva">Ingreso de nuevas planillas</a></li>
                </ul>
                <li><a href="#ModFin">M&oacutedulo financiero y tesorer&iacutea</a></li>
                <ul>
                    <li><a href="#ModFin">Garant&iacuteas</a></li>
                    <li><a href="#RegPagos">Registro de pagos de las Planillas</a></li><a id="ModCompras"></a>
                </ul>
            </ol>
        </div>
        <br><br><br><br>


        <!-- Modulo de compras publicas, Asignaciones Presupuestarias -->
        <h2 class="cursiva">M&oacutedulo de Compras P&uacuteblicas</h2>

        <div class="centrado">
            <br>

            <h2 class="izquierda">Asignaciones Presupuestarias</h2><br>

            <p>Antes de proceder a definir el plan anual de compras se debe registrar
            en el sistema los valores asignados para cada partida presupuestaria.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen1.png')}"/><br><br>

            <p>Desde esta pantalla de debe hacer doble clic en el casillero de la partida
            para acceder a la pantalla donde se puede buscar la partida presupuestaria
            de la cual se desea ingresar la asignaci&oacuten o techo de inversiones para
            el año.
            </p><br>

            <p>Una vez seleccionada la partida se debe ingresar el a&#241o y finalmente el
            valor del techo de la asignaci&oacuten para ese a&#241o.
            </p>

            <p>En el caso de que una partida no exista, se la puede ingresar al sistema
            utilizando el bot&oacuten &#34Crear nueva partida&#34
            </p><br>

            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen2.png')}"/><br><br>



            <p>Una vez realizada la asignaci&oacuten, se presenta en la zona inferior
            una lista con los valores de las asignaciones realizadas para el
            a&#241o que se seleccione.
            </p> <br>

            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen3.png')}"/><br><br>

            %{--<img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen4.png')}"/>--}%
        </div>

        <a id="PlanAnual"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!--Plan anual e compras -->
        <div class="centrado">
            <h2 class="izquierda">Plan Anual de Compras</h2><br>

            <p>Esta pantalla permite agregar, modificar y eliminar planes anuales
            de compras (PAC). Se accede desde el men&uacute &#34Contrataci&oacuten&#34.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen5.png')}"/><br><br><br>

            <p>La cabecera de esta pantalla (P.A.C.), permite registrar los procesos
            de compras p&uacuteblicas que se van a realizar dentro del plan anual
            de compras. Cada uno de los procesos ingresados aparece en la zona
            inferior denominada Detalle.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen6.png')}"/><br><br><br>

            <p>Al hacer doble clic en el campo de texto de &#34Partida presupuestaria&#34
            se abre un dialogo que permite buscar una partida para seleccionarla
            haciendo clic en el bot&oacuten &#34Visto&#34 a la derecha de la fila deseada.
            </p><br>

            <p>Si no existe la partida necesaria para el P.A.C. que va a ser creado, se puede
            crear una nueva. Se hace clic en el <img class="boton" src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen7.png')}"/>
                bot&oacuten   que aparece a la derecha de la partida.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen8.png')}"/><br><br><br>

            <p>Aqu&iacute se completan los datos necesarios y se hace clic en el bot&oacuten
            &#34Guardar&#34, y la nueva partida presupuestaria ya puede ser utilizada
            para crear el P.A.C.
            </p><br>

            <p>Tambi&eacuten existe una pantalla de ayuda para ubicar el c&oacutedigo de compras
            p&uacuteblicas de un determinado bien o servicio, esta pantalla se muestra
            al hacer doble clic en el campo de texto &#34C&oacutedigo C.P.&#34.
            </p><br>

        <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen9_1.png')}"/><br><br><br>

            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen9.png')}"/><br><br><br>

            <p>Se terminan de completar los datos requeridos y se seleccionan los cuatrimestres
            en los cuales se aplica el P.A.C. haciendo clic en los botones. Al hacer clic
            en el bot&oacuten Agregar se almacenan los datos y se agrega la fila c
            orrespondiente en la tabla de detalle.
            </p><br>

            <p>Para modificar una fila ya ingresada, se hace doble clic, o clic derecho y en
            &#34Editar&#34 en el men&uacute que aparece, o tambi&eacuten se puede hacer un doble
            clic sobre la l&iacutenea que se quiere modificar.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen10.png')}"/><br><br><br>

            <p>Esto ubica los datos del P.A.C. en el formulario de la parte superior,
            permitiendo as&iacute modificar los datos  y guardarlos. Los campos techo
            y usado son informaci&oacuten de los saldos presupuestarios para cada partida
            y se actualizan en forma inmediata con cada registro que se haga del PAC.
            </p><br><br>

        <p>Si se desea tambi&eacuten se puede cargar los datos del P.A.C. directamente de un
           archivo Excel (xls solamente). Para hacer esto, se presiona el bot&oacuten "Subir Excel"
           (ubicado en la parte inferior derecha del &aacuterea de datos del P.A.C. Esto muestra la
            pantalla de carga.

        </p><br><br>


        <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen10_1.png')}"/><br><br><br>

        <p>
            En esta pantalla se debe completar el requirente, el n&uacute;mero de memorando y la coordinaci&oacute;n antes
            de seleccionar el archivo. El archivo debe tener la extensi&oacute;n xls para poder
            se utilizado (Debe ser guardado en modo de compatibilidad con Excel 2003). El archivo debe adem&aacute;s
            tener un formato en particular (ilustrado m&aacute;s adelante) para que el sistema pueda interpretarlo.
            El sistema solamente procesa las filas en las cuales el tipo de copra es “OBRA” o “CONSULTORIA”.

        </p><br><br>

        <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen10_2.png')}"/><br><br><br>

        <p>

            Las filas de los t&iacute;tulos deben ser la 7 y la 8.
        </p>
        <p>

            Las columnas deben ser (comenzando en la A):
        </p>
        <p>
            * Partida presupuestaria / Cuenta contable
        </p>
        <p>
            * C&oacute;digo categor&iacute;a CPC a nivel 8
        </p>
        <p>
            * Tipo compra
        </p>
        <p>
            * Detalle del producto
        </p>
        <p>
            * Cantidad anual
        </p>
        <p>
            * Unidad
        </p>
        <p>
            * Costo unitario
        </p>
        <p>
            * Cuatrimestre 1 (marcado con una “S” o vac&iacuteo)
        </p>
        <p>
            * Cuatrimestre 2 (marcado con una “S” o vac&iacute;o)
        </p>
        <p>
            * Cuatrimestre 3 (marcado con una “S” o vac&iacute;o)
        </p><br>
        <p>

            Al presionar el bot&oacute;n “Subir”, el sistema procesa la informaci&oacute;n y muestra un resumen de lo realizado.
            Esta operaci&oacute;n puede tardar algunos minutos dependiendo del tamaño del archivo.
        </p><br>


        <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen10_3.png')}"/><br><br><br>

       </div>

        <a id="Concursos"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Concursos -->
        <h2 class="cursiva">Concursos</h2><br>

        <div class="centrado">
            <p>Esta secci&oacuten permite crear, editar y eliminar procesos, as&iacute
            como subir documentos, definir par&aacutemetros de evaluaci&oacuten y
            agregar ofertas. Las acciones de esta pantalla se encuentran en un men&uacute
            que aparece al hacer clic derecho en las filas de la tabla.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen11.png')}"/><br><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen12.png')}"/><br><br>
            <p>Al hacer clic en &#34Nuevo proceso&#34 se muestra una ventana para buscar
            y seleccionar el P.A.C. para el cual se va a crear el proceso. Al seleccionar
            el P.A.C., se crea un proceso que tiene &uacutenicamente el P.A.C. y el
            objeto, en el cual se copia la descripci&oacuten del P.A.C.
            </p><br>

            <p>La opci&oacuten Documentos  muestra una pantalla que permite subir archivos,
            completar su informaci&oacuten y descargar los documentos ya subidos.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen12_1.png')}"/><br><br>

            <p>Si desea incluir nuevos documentos haga un clic en el bot&oacuten Nuevo
            Documento. La pantalla de subida de archivos aparece.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen13.png')}"/><br><br>

            <p>Los datos m&aacutes importantes del registro de documentos comprenden el
            resumen, la descripci&oacuten y las palabras clave, sobre las cuales se
            realizar&aacuten b&uacutesquedas para poder consultar en la biblioteca.
            </p><br>
            <p>
                La columna “Acciones” de la tabla tiene varios botones. De izquierda a derecha
                &eacute;stos son: “Ver”, “Editar”, “Descargar” y “Eliminar”
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen14.png')}"/><br><br>

            <p>
                El bot&oacute;n “Ver” muestra los datos registrados del documento.
            </p><br>

            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen15.png')}"/><br><br>

            <p>
                El bot&oacute;n “Editar” muestra el mismo formulario que el bot&oacute;n de creaci&oacute;n,
                pero con los datos del documento seleccionado, de manera a poder editar la informaci&oacute;n y guardar los cambios.
            </p>
            <p>
                El botón “Descargar” permite guardar de manera local el documento.

            </p>
            <p>
                El botón “Eliminar” elimina definitivamente el documento y sus datos.
            </p>

            <p>La opci&oacuten Ofertas permite crear, editar y eliminar ofertas, as&iacute
            como evaluarlas. S&oacute;cutelo los procesos registrados admiten el ingreso de
            ofertas y evaluaci&oacuten, de modo que estas opciones del men&uacute
            aparecer&aacuten desactivadas para procesos no registrados. El registro de
            los procesos se realiza mediante el bot&oacuten Registrar en la pantalla
            de proceso.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen16.png')}"/><br><br>

            <p>Para editar los datos de la oferta use el bot&oacuten Editar.</p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen17.png')}"/><br><br>

            <p>Junto al casillero para el registro del proveedor aparece un bot&oacuten
            que permite crear un proveedor en el caso de que este no haya sido
            ingresado en el sistema (Proveedores nuevos).
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen18.png')}"/><br><br>


            <p>Para crear una oferta use el bot&oacuten &#34Crear Oferta&#34.</p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen20.png')}"/>
        </div>

        <a id="Contratos"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Contratos -->
        <h2 class="cursiva">Contratos</h2><br>

        <div class="centrado">
            <p>En este m&oacutedulo se registran los contratos y sus datos e
            informaci&oacuten que deber&aacuten regular la ejecuci&oacuten de la obra.
            Adem&aacutes de registrar los datos m&aacutes importantes del contrato se
            puede tambi&eacuten subir al sistema el archivo digitalizado o pdf del contrato,
            de tal forma que sirva como fuente de consulta inmediata para lograr el
            correcto cumplimiento del contrato.
            </p><br>

            <p>Para acceder a contratos se debe usar la opci&ocuten contratos del men&uacute
            Ejecuci&oacuten.</p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen21.png')}"/><br><br>

            <p>Al presionar el bot&oacuten lista aparece una pantalla de b&uacutesqueda que
            nos permite ubicar los contratos por c&oacutedigo o n&uacutemero de contrato
            y por nombre de la obra.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen24.png')}"/><br><br>


            <p>Al presionar en el bot&oacuten &#34visto&#34 en la l&iacutenea deseada, se
            cargan los datos del contrato y aparece la barra de botones o de herramientas
            antes descrita.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen25.png')}"/><br><br>

            <p>En la parte inferior de la pantalla de contratos aparece una barra de herramientas
            una vez que se ha registrado el contrato o cuando se halla visualizando un contrato
            ya registrado en el sistema.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen23.png')}"/><br><br>

            <p>Esta barra de herramientas permite acceder al registro de garant&iacuteas
            , el cronograma contractual de la obra, la f&oacutermula polin&oacutemica contractual, una biblioteca de
            documentos relativos al contrato y la ejecuci&oacuten de la obra.
            </p><br>

            <p>La zona para el registro del objeto del contrato puede almacenar
            aproximadamente hasta 16 l&iacuteneas.
            </p>

            <p>Esta pantalla registra s&oacutelo los datos m&aacutes importantes del
            contrato, para acceder al texto completo del contrato hay que consultar
            en la biblioteca de contratos.
            </p>
        </div>

        <a id="CronoCon"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!--Cronograma contractual -->
        <div class="centrado">
            <h2 class="izquierda">Cronograma contractual</h2><br>

            <p>A la pantalla de registro del cronograma contractual se accede haciendo
            un clic en el bot&oacuten &#34Cronograma&#34, por defecto o como plantilla
            aparece el cronograma de la obra que se ha ofertado (este ser&aacute tomado
            del m&oacutedulo de oferentes ganadores que se halla a&uacuten en
            construcci&oacuten, por el momento se ha tomado el cronograma de referencia
            de la obra, preparado en el m&oacutedulo de obras).
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen26.png')}"/><br><br>
            <p>
                Se pueden filtrar los rubros por subpresupuesto, o mostrar todo.
            </p><br>
            <p>
                Para registrar los valores en el cronograma se debe hacer clic sobre la o las filas a modificar (se muestran el azul las filas seleccionadas) y
                doble clic en la casilla del mes correspondiente. El sistema muestra la pantalla para el registro
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen27.png')}"/><br><br>

            <p>
                El modo de registro del cronograma es id&eacute;ntico al cronograma de la obra,
                de tal forma que se pueden ingresar valores para varios per&iacute;odos en forma
                prorrateada tanto de las cantidades como del porcentaje o del valor
                monetario del avance. Cuando se han seleccionado varias filas simult&aacute;neamente
                la edición puede &uacute;nicamente realizarse por porcentajes, y los valores
                ya existentes serán remplazados.

                <br> Desde la pantalla de cronograma se puede acceder al
            gr&aacutefico de avance de la obra.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen28.png')}"/><br><br>

            <p>Para regresar al cronograma del contrato presione el bot&oacuten &#34Cronograma&#34.
            </p>
        </div>

        <a id="RegFormula"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Registro de la formula polinomica del contrato -->
        <div class="centrado">
            <h2 class="izquierda">Registro de la f&oacutermula polin&oacutemica del contrato</h2><br>

            <p>A diferencia de la obra, la f&oacutermula polin&oacutemica del contrato se
            registra en base a los valores prefijados en el documento del contrato y no es
            necesario agrupar &iacutetems para componer los distintos coeficientes.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen29.png')}"/><br><br>

            <p>Las dos formulas deben registrarse, tanto la correspondiente a la cuadrilla tipo
            como la f&oacutermula polin&oacutemica del reajuste de precios.  Para los dos
            casos, s&oacutelo se hallan disponibles los &iacutendices que se manejan en
            el INEC y los de la contralor&iacutea para el caso de la cuadrilla tipo.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen30.png')}"/>
        </div>

        <a id="BiblioCont"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Biblioteca de Contratos -->
        <div class="centrado">
            <h2 class="izquierda">Biblioteca de Contratos</h2><br>

            <p>La biblioteca de contratos permite almacenar en el sistema documentos en formato
            digital ya sea estos archivos de texto (de Microsoft office o de libre office),
            hojas de c&aacutelculo, im&aacutegenes o archivos en formato pdf. Esta biblioteca
            almacena documentos relativos al contrato desde la etapa del proceso hasta
            la ejecuci&oacuten de la obra.
            </p><br>

            <p>Para acceder a la biblioteca haga clic en el bot&oacuten Biblioteca de la barra de
            herramientas inferior de la pantalla de contratos.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen31.png')}"/><br><br>

            <p>El sistema muestra la pantalla de documentos cargados obra por obra. En esta
            pantalla existe un buscador que permite ubicar documentos basados en palabras
            clave para un r&aacutepido acceso a la informaci&oacuten.
            </p><br>

            <p>A continuaci&oacuten se muestra la pantalla resultado de buscar en la biblioteca
            la palabra "archivo".
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen32.png')}"/><br><br>

            <p>Si desea incluir nuevos documentos haga un clic en el bot&oacute;n Nuevo Documento. La pantalla de subida de archivos aparece.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen33.png')}"/><br><br>

            <p>
            Los datos m&aacute;s importantes del registro de documentos comprenden el resumen,
            la descripci&oacute;n y las palabras clave, sobre las cuales se realizar&aacute;n b&uacute;squedas
            para poder consultar en la biblioteca.

            </p><br>
            <p>
            La columna “Acciones” de la tabla tiene varios botones.
            De izquierda a derecha &eacute;stos son: “Ver”, “Editar”, “Descargar” y “Eliminar”
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen34.png')}"/><br><br>

            <p>
                El botón “Ver” muestra los datos registrados del documento.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen35.png')}"/><br><br>

            <p>El bot&oacute;n “Editar” muestra el mismo formulario que el bot&oacute;n de creaci&oacute;n,
            pero con los datos del documento seleccionado, de manera a poder editar la informaci&oacute;n y guardar los cambios.
            </p>

            <p>El bot&oacute;n “Descargar” permite guardar de manera local el documento.

            </p>
        <p>
            El bot&oacute;n “Eliminar” elimina definitivamente el documento y sus datos.
        </p><br>

        <p>
            La opci&oacute;n Ofertas permite crear, editar y eliminar ofertas, as&iacute; como evaluarlas. S&oacute;lo los
            procesos registrados admiten el ingreso de ofertas y evaluaci&oacute;n, de modo que estas opciones
            del men&uacute; aparecer&aacute;n desactivadas para procesos no registrados. El registro de los procesos se
            realiza mediante el bot&oacute;n Registrar en la pantalla de concursos.
        </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen36.png')}"/><br><br>

            <p>
                Para editar los datos de la oferta use el botón Editar.
            </p><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen37.png')}"/><br><br><br>
        </div>




        <!-- Registro y control de Planillas -->
        <br><br><br>

        <h2 class="cursiva">Registro y control de Planillas</h2><br>

        <div class="centrado">
            <p>Una vez culminado el proceso de contrataci&oacuten y en base al contrato,
            se procede a la ejecuci&oacuten de la obra, la cual se da inicio con
            la orden de pago del anticipo mediante el registro de una planilla de
            anticipo por el valor correspondiente al monto fijado en el contrato
            y el porcentaje correspondiente.
            </p>

            <p>Para el control de los pagos y para el c&aacutelculo del reajuste de
            precios el sistema se sirve de los &iacutendices de precios que proporciona
            en INEC para el caso de materiales y equipos, y de los &iacutendices
            proporcionados por la Contralor&iacutea, para el caso de los valores de
            mano de obra.
            </p>

            <p>Con el prop&oacutesito de evitar cualquier confusi&oacuten al momento
            de calcular la variaci&oacuten de los &iacutendices, la f&oacutermula
            polin&oacutemica se elabora tomando en cuenta s&oacutelo los &iacutendices
            publicados por el INEC, en lo que se refiere al nombre de los materiales
            y equipos.
            </p>
        </div>

        <a id="RegIndices"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Registro de Indices -->
        <div class="centrado">
            <h2 class="izquierda">Registro de &Iacutendices</h2><br>

            <p>Para el registro de &iacutendices el sistema presenta la siguiente pantalla:</p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen38.png')}"/><br><br>

            <p>Salvo que el INEC a&#241ada nuevos materiales o equipos a sus &iacutendices,
            esta lista deber&iacutea mantenerse sin modificaciones.
            </p>

            <p>Para mirar en detalle cada &iacutendice podemos hacer clic en el
            bot&oacuten &#34Ver&#34 bajo la columna de acciones. Para editar haga un
            clic en Editar, con lo cual aparece una ventana similar a:
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen39.png')}"/>
        </div>

        <a id="RegDatos"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Registro de datos desde el INEC -->
        <div class="centrado">
            <h2 class="izquierda">Registro de datos desde el INEC</h2><br>

            <p>Cada mes en el sistema se debe cargar los &iacutendices del INEC para
            poder realizar los reajustes de precios. El proceso consiste en crear un
            nuevo periodo de &iacutendices, descargar el archivo excel desde el
            INEC y finalmente subir los datos al sistema.
            </p>

            <p>Para crear un nuevo periodo de &Icutendices ingrese a la opci&ocuten
            &#34Periodos de &Iacutendices&#34 del men&uacute Ejecuci&oacuten.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen40.png')}"/><br><br>

            <p>Para crear un nuevo periodo haga clic en &#34Nuevo Periodos INEC&#34.</p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen41.png')}"/><br><br>

            <p>Para cargar el archivo de datos de los &iacutendices haga clic en &#34Subir
            &Iacutendices&#34 y luego ubique el archivo correspondiente, luego de
            haber seleccionado el periodo en el cual se va a cargar los datos.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen42.png')}"/><br><br>

            <p>En esta pantalla seleccione el periodo para el cual se van a subir los valores,
            ubique el archivo y presione el bot&oacuten Aceptar. <br> Una vez cargados
            los datos se pueden visualizar a modo de tabla anual mediante la opci&oacuten
            de men&uacute Valores de &Iacutendices.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen43.png')}"/>
        </div>

        <a id="ProDes"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Proceso para descargar el archivo desde el INEC -->
        <div class="centrado">
            <h2 class="izquierda">Proceso para descargar el archivo desde el INEC</h2><br>

            <p>Se inicia por ingresar al sitio web del INEC:
                <a href="http://www.inec.gob.ec">http://www.inec.gob.ec,</a> luego se ingresa
            a estad&iacutesticas, se hace clic en el men&uacute Estad&iacutesticas
            Econ&oacutemicas y luego en &#34Indice de precios de la construcci&oacuten&#34.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen44.png')}"/><br><br>

            <p>Finalmente se descarga el archivo en excel requerido, haciendo un clic en
            el enlace correspondiente. En nuestro caso:  &#34&Iacutendice de Precios
            de la Construcci&oacuten Nivel Nacional Febrero 2013 Formato Excel&#34.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen45.png')}"/>
        </div>

        <a id="RegCron"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Registro del cronograma de ejecucion -->
        <div class="centrado">
            <h2 class="izquierda">Registro del cronograma de ejecuci&oacuten</h2><br>

            <p>Para el registro y modificaci&oacuten del cronograma de ejecuci&oacuten
            de la obra seleccione la opci&oacuten &#34Cronograma de ejecuci&oacuten&#34
            del men&uacute de Ejecuci&oacuten.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen46.png')}"/><br><br>

            <p>Esta pantalla cuenta con varios comandos en su parte superior:</p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen47.png')}"/><br><br>

            <p>Ampliaci&oacuten permite ampliar el plazo de ejecuci&oacuten de la obra.
            Suspensi&oacuten sirve para crear periodos de suspensi&oacuten de la
            obra y el bot&oacuten &#34Cambiar fecha de fin&#34 sirve para modificar
            la fecha de finalizaci&oacuten del &uacuteltimo periodo de
            ejecuci&oacuten de la obra. <br> Para registrar un ampliaci&oacuten
            se debe hacer clic en Ampliaci&oacuten:
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen48.png')}"/><br><br>

            <p>De forma similar para el registro de periodos de suspensi&oacuten
            de la obra se debe hacer clic en  &#34Suspensi&oacuten&#34, con lo cual
            aparece la pantalla:
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen49.png')}"/><br><br>

            <p>La &uacuteltima opci&oacuten sirve para poner fecha de finalizaci&oacuten
            a cada periodo del cronograma.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen50.png')}"/>
        </div>

        <a id="EstPlan"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Estado de la planilla -->
        <div class="centrado">
            <h2 class="izquierda">Estado de la planilla</h2><br>

            <p>Nos presenta un listado de los diferentes estados que puede tener una
            planilla. En la columna Acciones se encuentran las diferentes tareas
            que se pueden realizar, estas son:  <em>Ver, Editar, Eliminar.</em>
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen51.png')}"/><br><br>

            <p>Al hacer clic en el bot&oacuten <em>Crear Estado Planilla</em> nos
            permitir&aacute crear un nuevo estado. <br> Una ves ingresados
            los datos, se procede a dar clic en <em>Guardar.</em>
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen52.png')}"/>
        </div>

        <a id="TipoPlan"></a>
        <br><br>


        <!-- Topo de Plantilla -->
        <div class="centrado"><br><br><br>

            <h2 class="izquierda">Tipo de Planilla</h2><br>

            <p>Presenta una lista de los diferente tipos de planillas.</p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen53.png')}"/><br><br>

            <p>Click en el bot&oacuten Crear Tipo Planilla, nos permitir&aacute
            crear un nuevo tipo de planilla. <br> Terminado de ingresar los
            datos se procede a dar click en Guardar.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen54.png')}"/>
        </div>

        <a id="TipoDesc"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Tipo de descuentos por planilla -->
        <div class="centrado">
            <h2 class="izquierda">Tipo de descuentos por planilla</h2><br>

            <p>Presenta una lista de los diferentes descuentos que pueden ser
            aplicados a las planillas. <br> La columna Acciones nos presenta
            un conjunto de acciones las mismas que son: <em>Ver, Editar y Eliminar.</em>
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen55.png')}"/><br><br>

            <p>Clic en el bot&oacuten <em>Crear Tipo de Descuento</em> para crear un
            nuevo tipo de descuento. Para guardar los cambios clic en el
            bot&oacuten <em>Guardar.</em>
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen56.png')}"/>
        </div>

        <a id="DescPlan"></a>
        <br><br>


        <!-- Descuentos por planilla -->
        <div class="centrado"><br><br><br>

            <h2 class="izquierda">Descuentos por planilla</h2><br>

            <p>Nos presenta una lista con los descuentos por los diferentes tipo de planillas.
                <br>La columna <em>Acciones</em> posee un conjunto de tareas las
            cuales son: <em>Ver, Editar, Eliminar.</em>
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen57.png')}"/><br><br>

            <p>Clic en el bot&oacuten <em>Crear Descuento Tipo Planilla,</em> permite
            crear un nuevo tipo de descuento.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen58.png')}"/>
        </div>

        <a id="RegPlan"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!--Registro de Planillas -->
        <div class="centrado">
            <h2 class="izquierda">Registro de Planillas</h2>

            <p>Para el registro de planillas se debe ingresar desde el contrato.
            Al presionar el bot&oacuten Planillas de la barra de herramientas inferior
            de la pantalla de contrato se muestra la lista de planilla registras para
            este contrato u obra.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen59.png')}"/><br><br>

            <p>Para crear una nueva planilla se hace clic en Nueva Planilla:</p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen60.png')}"/><br><br>

            <p>Si se desea actualizar los datos de la planilla se debe hacer uso del
            bot&oacuten modificar planilla.
            </p>

            <p>Para el registro de los vol&uacutemenes de obra planillados en el caso
            de las planilla de avance de obra, se debe crear una planilla del tipo
            Avance de obra y hacer un clic en &#34Detalle&#34, bot&oacuten que
            aparece bajo la columna de acciones.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen61.png')}"/><br><br>

            <p>El registro del detalle de la planilla se hace en base a la plantilla
            de los vol&uacutemenes de obra contratados.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen62.png')}"/><br><br>

            <p>Para ingresar valores simplemente seleccione la casilla correspondiente
            y escriba los valores de cantidad de obra planillados.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen63.png')}"/><br><br>

            <p>Con estos datos ingresados el sistema calcula los valores monetarios
            de la planilla, luego los valores de Bo y Po a aplicarse para el
            reajuste de precios conforme los &iacutendices de materiales,
            equipos y mano de obra.
            </p>

            <p>La fecha que se toma como referencia para el c&aacutelculo de los
            diferentes periodos de reajuste corresponde a la del pago de la
            planilla del anticipo, la misma que se registra utilizando el
            m&oacutedulo financiero (a&uacuten en construcci&oacuten).
            </p>

            <p>Si la fecha de inicio de obra es menor al 15 del mes, se puede aceptar
            una planilla en ese mes, caso contrario, se aceptar&aacute una planilla
            acumulados los d&Icuteas que restan del mes actual.
            </p>

            <p>Como ejemplo de c&aacutelculo se presenta a continuaci&oacuten un
            detalle de los valores de las variaciones de los &iacutendices
            calculados seg&uacuten los coeficientes de la f&oacutermula
            polin&oacutemica.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen64.png')}"/><br><br>

            <p>Una vez definidos los valores de Bo, se calculan los de Po y se
            obtienen los reajustes correspondientes a aplicarse al valor
            planillado (Fr -1). El detalle se imprime como la planilla a pagarse.
            </p>

            <p>El sistema hace un c&aacutelculo en funci&oacuten de los valores de
            ellos &iacutendices ingresados o cargados desde el INEC. Bo y cada
            uno de los coeficientes de la f&oacutermula se calculan para determinar
            el valor de Fr y Fr – 1, con los cuales se calcula el valor del reajuste a pagar.
            </p>

            <p>El resumen de los c&aacutelculos realizados por el sistema se muestra
            en la pantalla a continuaci&oacuten, donde se puede observar tanto el
            c&aacutelculo de Bo, Po, los valores de Fr y Pr y los datos de la planilla
            presentada para la cual se ha calculado.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen65.png')}"/><br><br>

            <p>Para el caso de planillas de avance de obra se a&#241ade la secci&oacuten de Multas:
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen66.png')}"/><br><br>

            <p>Para el caso de la planilla de liquidaci&oacuten se hace el c&aacutelculo de las
            multas aplicadas, los descuentos correspondientes al anticipo y un calculo
            actualizado del reajuste al cual se restan los reajustes aplicados para
            obtener la diferencia de reajuste a aplicar, algo similar a lo mostrado
            en el cuadro antes se&#241alado.
            </p>

            <p>El sistema imprime tanto los valores Bo como los Po. Adem&aacutes lleva un
            registro hist&oacuterico de los valores de los &iacutendices aplicados
            en cada reajuste y los de Fr, valores planillados, pagados y reajustes pagados.
            </p>
        </div>

        <a id="IngNueva"></a>
        <br><br>


        <!--Ingreso de nuevas planillas-->
        <div class="centrado"><br><br><br>

            <h2 class="izquierda">Ingreso de nuevas planillas</h2><br>

            <p>Al hacer clic en el bot&oacuten Nueva Planilla de la pantalla de lista de
            planillas a la cual se accede desde el contrato, se abrir&aacute la pantalla
            para la creaci&oacuten de una nueva planilla.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen67.png')}"/><br><br>

            <p>Una vez se ha ingresado los datos procedemos a dar clic en Guardar para
            terminar el proceso de creaci&oacuten de una planilla.
            </p>
        </div>

        <a id="ModFin"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Modulo Financiero y Tesoreria, Garantias -->
        <h2 class="cursiva">M&oacutedulo financiero y tesorer&iacutea</h2><br>

        <div class="centrado">
            <h2 class=izquierda>Garant&iacuteas</h2><br>

            <p>Para ingresar en la parte correspondiente a garant&iacuteas, clic en
            Ejecuci&oacuten en el men&uacute superior, se desplegar&aacute una lista
            de opciones elegimos, Contratos y Ejecuci&oacuten.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen68.png')}"/><br><br>

            <p>Se abrir&aacute la pantalla de registro de contratos; elegimos un contrato
            al dar clic en Lista del men&uacute superior.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen69.png')}"/><br><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen70.png')}"/><br><br>

            <p>Una vez se ha elegido un contrato de la lista de contratos, aparecer&aacute
            un men&uacute inferior abajo de los datos del contrato. <br> Procedemos
            a dar clic en Garant&iacuteas del men&uacute inferior.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen71.png')}"/><br><br>

            <p>Se desplegar&aacute la pantalla de Garant&iacuteas.</p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen72.png')}"/><br><br>

            <p>Para poder agregar una garant&iacutea al contrato es necesario llenar
            los campos en blanco y que son obligatorios de la secci&oacuten
            intermedia Garant&iacutea en la pantalla.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen73.png')}"/><br><br>

            <p>A continuaci&oacuten clic en el <img class="boton" src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen74.png')}"/>
                bot&oacuten verde    para agregar la garant&iacutea al contrato.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen75.png')}"/><br><br>

            <p>Si se desea editar, cambiar de estado o borrar la garant&iacutea ingresada,
            procedemos a dar doble clic en la garant&iacutea que se necesite editar.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen76.png')}"/><br><br>

            <p>El primer bot&oacuten nos permitir&aacute ingresar una nueva garant&iacutea,
            el segundo bot&oacuten guardar&aacute los cambios hechos a la actual
            garant&iacutea que est&aacute siendo editada, el tercer bot&oacuten
            permite agregar una nueva garant&iacutea pero con el estado de la misma
            cambiada a Renovado, el cuarto bot&oacuten elimina la garant&iacutea seleccionada.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen77.png')}"/>
        </div>

        <a id="RegPagos"></a><br>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>


        <!-- Registro de pagos de las Planillas -->
        <div class="centrado">
            <h2 class="izquierda">Registro de pagos de las Planillas</h2><br>

            <p>Para ingresar en la pantalla de registro de planillas, clic en Ejecuci&oacuten
            del men&uacute superior una vez desplegadas las opciones elegimos Contratos y
            Ejecuci&oacuten.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen78.png')}"/><br><br>

            <p>En la pantalla de Registro de Contratos elegimos un contrato de la lista de contratos.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen79.png')}"/><br><br>

            <p>Una vez escogido un contrato de la lista aparecer&aacute el men&uacute
            inferior; en el cual debemos elegir Planillas.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen80.png')}"/><br><br>

            <p>Se desplegar&aacute una lista de planillas con sus diferentes datos y
            un conjunto de acciones en la &uacuteltima columna.
            </p>

            <p>El primer <img class="boton" src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen81.png')}"/> bot&oacuten
            desplegar&aacute los detalles de la planilla en una nueva p&aacutegina.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen82.png')}"/><br><br>

            <p>El segundo <img class="boton" src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen83.png')}"/> bot&oacuten
            presentar&aacute un resumen de la planilla; con los c&aacutelculos
            de B0, P0, Fr y Pr.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen84.png')}"/><br><br>

            <p>El &uacuteltimo <img class="boton" src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen85.png')}"/> bot&oacuten
            corresponde al pago de la planilla, si se encuentra en color verde
            quiere decir que la planilla debe pagarse, si esta de color blanco
            la planilla ya ha sido pagada y solo podremos ver un resumen del pago.<br><br>
                Planilla a pagarse.
            </p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen86.png')}"/><br><br>

            <p>Planilla Pagada.</p><br>
            <img src="./${resource(dir: 'images/imagenesManuales/imagenesEjecucion', file: 'imagen87.png')}"/><br><br>
        </div>

        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div><br><br><br>

    </body>
</html>