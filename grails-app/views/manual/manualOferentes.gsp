<!DOCTYPE html>
<html>
    <head>
        %{--<meta name="layout" content="main">--}%
        <title>Manual de Usuario - Oferentes</title>
        <style>
        p {

        }

        * {
            max-width : 1100px;
            margin    : auto;
        }

        div {
            margin : auto;
        }

        #header2 {
            z-index    : 1;
            position   : relative;
            width      : 97.5%;
            height     : 60px;
            text-align : center;
            /*background-color : #ffffff;*/
            margin-top : -9px;
        }

        .centrado {
            text-align : center;
        }

        .sinsenal {
            /*list-style-type : none;*/
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

        .cuadro {
            width  : 600px;
            margin : auto;
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
            margin-right     : 40px;
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

        table tr {
            text-align : justify;
        }

        </style>
    </head>

    <body>

        <g:link controller="inicio" action="index">&lt;&lt;&nbsp;Regresar al sistema</g:link>

        <div id="header2">
            <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'encabezado.png')}"/>
        </div>

        <div class="centrado">
            <br><br><br><br>
            <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'logo.png')}"/>
            <br><br><br><br>

            <h1>Sistema Integrado de Gesti&oacuten de Proyectos, Fiscalizaci&oacuten,
            Contrataci&oacuten y
            Ejecuci&oacuten de Obras</h1>
            <br><br>

            <h2>Manual del Usuario</h2>

            <h2>Tercera Parte</h2>
            <br><br>

            <h3>Portal de Oferentes Ganadores</h3>
            <br><a id="volverIndice"></a>
            <br><br>

            <h3>TEDEIN S.A.  2012 - 2013</h3>
            <br><br><br>
        </div>

        <div id="indice">
            <br>

            <h2>
                Índice de contenido
            </h2>
            <ol class="sinsenal">
                <li><a href="#ModuloGanadores">M&oacutedulo de Oferentes Ganadores</a>
                    <ul class="sinsenal">
                        <li>
                            <a href="#Registro de cuentas de Oferentes">Registro de cuentas de Oferentes</a>
                        </li>
                        <li>
                            <a href="#Vinculaci&oacuten de la Obra a cada oferente">Vinculaci&oacuten
                            de la Obra a cada oferente</a>
                        </li>
                        <li>
                            <a href="#M&oacutedulo de Oferentes">M&oacutedulo
                            de Oferentes</a>
                        </li>
                        <li>
                            <a href="#Registro de An&aacutelisis de Precios">Registro
                            de An&aacutelisis de Precios</a>
                        </li>
                        <li>
                            <a href="#Barra de comandos">Barra de comandos</a>
                        </li>
                    </ul>
                </li>
                <li>
                    <a href="#Obras">Obras</a>
                    <ul class="sinsenal">
                        <li>
                            <a href="#Lista">Lista</a>
                        </li>
                        <li>
                            <a href="#Variables">Variables</a>
                        </li>
                        <li>
                            <a href="# Vol&uacutemenes de Obra">Vol&uacutemenes de Obra</a>
                        </li>
                        <li>
                            <a href="# Matriz de la F&oacutermula Polin&oacutemica">Matriz
                            de la F&oacutermula Polin&oacutemica</a>
                        </li>
                        <li>
                            <a href="# F&oacutermula polin&oacutemica">F&oacutermula
                            polin&oacutemica</a>
                        </li>
                        <li>
                            <a href="#Trabajando en la composici&oacuten de la f&oacutermula polin&oacutemica">
                                Trabajando en la composici&oacuten de la
                                f&oacutermula polin&oacutemica</a>
                        </li>
                        <li>
                            <a href="#Rubros de la obra">Rubros de la obra</a>
                        </li>
                        <li>
                            <a href="#Cronograma">Cronograma</a>
                        </li>
                        <li>
                            <a href="#Composici&oacuten de la Obra">Composici&oacuten de la Obra</a>
                        </li>
                    </ul>
                </li>
            </ol>

            <a id="ModuloGanadores"></a>
            <br>
        </div>

        <div class="centrado">
            <br><br><br>

            <h2 class="izquierda">M&oacutedulo de Oferentes Ganadores</h2>
            <br>

            <p>El m&oacutedulo de oferentes consiste de un complemento al sistema de gesti&oacuten de
            proyectos, contrataci&oacuten, fiscalizaci&oacuten y control de obras, que sirve para
            el registro de cuentas de oferentes y, un sistema en l&iacutenea completamente
            independiente que estar&aacute a disposici&oacuten de los oferentes mediante un nombre
            de usuario y contrase&ntildea durante el per&iacuteodo de recepci&oacuten de ofertas.
                <a id="Registro de cuentas de Oferentes"></a>
            </p>
            <br><br>

            <h3 class="izquierda">
                Registro de cuentas de Oferentes
            </h3>
            <br>

            <p>Como primer paso para el proceso de registro de ofertas en l&iacutenea se deben
            crear las cuentas de oferentes, lo cual consiste en registrar en el sistema
            los oferentes autorizados a registrar sus ofertas en l&iacutenea. Para cada uno de
            ellos de crear&aacute un nombre de usuario y contrase&ntildea con las cuales podr&aacute
            ingresar al sistema de oferentes y en el registrar tanto los an&aacutelisis de
            precios como los vol&uacutemenes de obra, f&oacutermula polin&oacutemica y el cronograma.
            </p>

            <p>En s&iacutentesis el subsistema de oferentes permite:
            </p>
            <ul>
                <li>
                    <p>
                        Registro de los an&aacutelisis de precios unitarios para cada rubro,
                        detallando
                        los materiales, mano de obra, equipos y rendimientos.
                    </p>
                </li>
                <li>
                    <p>
                        Registro de precios de materiales, mano de obra y equipos.
                    </p>
                </li>
                <li>
                    <p>
                        Registro de los vol&uacutemenes de obra.
                    </p>
                </li>
                <li>
                    <p>
                        Generaci&oacuten de la matriz de descomposici&oacuten de la obra,
                        necesaria para componer
                        la f&oacutermula polin&oacutemica.
                    </p>
                </li>
                <li>
                    <p>
                        F&oacutermula polin&oacutemica y cuadrilla tipo.
                    </p>
                </li>
                <li>
                    <p>
                        Registro del cronograma de ejecuci&oacuten de la obra.
                    </p>
                </li>
                <li>
                    <p>
                        Generaci&oacuten de documentos de presupuesto, an&aacutelisis de precios unitarios,
                        f&oacutermula polin&oacutemica y cronograma.
                    </p>
                </li>
            </ul>
            <table>
                <tr>
                    <td>
                        <p>Para crear la cuenta de un oferente seleccionamos la opci&oacuten Oferentes del
                        men&uacute de Administraci&oacuten. El sistema muestra la lista de oferentes registrados.
                        </p>
                    </td>
                    <td>
                        <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen1.png')}"/><br><br>
                    </td>
                </tr>
            </table>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen2.png')}"/><br><br>

            <p>
                Desde la pantalla de lista de oferente, use el bot&oacuten &#34Crear Oferente&#34 para
                ingresar los datos del oferente y crear una cuenta de usuario en el el sistema
                para que pueda acceder a el y registrar su oferta. El sistema mostrar&aacute una
                ventana con una plantilla para crear el oferente
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen3.png')}"/><br><br>

            <p>En la columna de acciones aparece unos botones que permiten ver, editar, cambiar
            la contrase&ntildea y cambiar el estado del oferente.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen4.png')}"/><br><br>

            <p>
                Para desactivar un oferente, use el bot&oacuten Cambiar estado. El sistema confirma
                la orden antes de pasar el estado de activo a inactivo, o viceversa.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen5.png')}"/><br><br>

            <p>
                Luego de creada la cuenta del oferente se debe crear la plantilla de la obra
                para que se pueda ingresar los datos de la oferta, los cuales comprender&aacuten los
                an&aacutelisis de precios unitarios (rubros), precios de cada &iacutetem (mano de obra,
                materiales y equipos), vol&uacutemenes de obra, f&oacutermula
                polin&oacutemica y cronograma.
            </p>

            <p>
                Una vez que se ha creado el oferente es necesario pasarlo al m&oacutedulo de oferentes,
                para ello se debe usar el bot&oacuten &#34Copiar al sistema de Oferentes&#34.
            </p>

            <p>
                El proceso a seguir es el siguiente:
            </p>
            <ol>
                <li>
                    <p>
                        Haga un clic en la casilla de chequeo del oferente que se desea pasar,
                        puede seleccionar uno o varios. Use casillero de la cabecera para
                        se&ntildealar todos si as&iacute lo desea. Tambi&eacuten pude ubicar
                        un oferente espec&iacutefico
                        usando la casilla de b&uacutesqueda y presionando el bot&oacuten buscar.
                    </p>
                </li>
                <li>
                    <p>
                        Haga un clic en el bot&oacuten Copiar al sistema de Oferentes
                    </p>
                </li>
                <li>
                    <p>
                        El sistema responder&aacute con un mensaje indicando que se han copiado los
                        oferentes o si ya se han copiado y se intenta repetir el proceso, un
                        mensaje de error indicando que ya han sido copiados.
                    </p>
                </li>
            </ol>
            %{--<br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen6.png')}"/>--}%
            <br><br>
            <a id="Vinculaci&oacuten de la Obra a cada oferente"></a>

            <div class="regresa">
                <p><a href="#ModuloGanadores">M&oacutedulo de Oferentes Ganadores</a></p>
            </div> <br><br><br>

            <h3 class="izquierda">
                Vinculaci&oacuten de la Obra a cada oferente
            </h3>

            <p>
                Use la opci&oacuten de men&uacute Obras &#8211&gt Registro de Obras para acceder a la pantalla de
                obras, una vez all&iacute, siga el siguiente proceso:
            </p>
            <ol>
                <li>
                    <p>
                        Ubique la obra que ha salido a proceso, usando el bot&oacuten Lista
                    </p>
                    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen7.png')}"/><br><br>
                </li>
                <li>
                    <p>
                        Una vez posicionado en la obra, use el bot&oacuten Copiar Obra a Oferentes
                        para crear una entrada para que el oferente pueda registrar su oferta.
                    </p>
                    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen8.png')}"/><br><br>
                </li>
                <li>
                    <p>
                        Seleccione el nombre del oferente y haga un clic en Aceptar.
                    </p>
                    <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen9.png')}"/><br><br>
                </li>
            </ol>

            <p>
                Con esto se ha creado la obra en el m&oacutedulo de Oferentes para que el oferente
                seleccionado pueda registrar su oferta.
            </p>

            <p>
                En adelante se describir&aacute el m&oacutedulo de oferentes y el proceso de como registrar
                una oferta.
                <a id="M&oacutedulo de Oferentes"></a>

            </p>

            <div class="regresa">
                <p><a href="#ModuloGanadores">M&oacutedulo de Oferentes Ganadores</a></p>
            </div> <br><br><br>


            <h3 class="izquierda">
                M&oacutedulo de Oferentes
            </h3>

            <p>
                El sistema en l&iacutenea creado para los oferentes cuenta con una portada que permite
                el acceso r&aacutepido a sus componentes, los cuales son: Precios unitarios, Obras y
                Generaci&oacuten de documentaci&oacuten para la entrega o respaldo de la oferta.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen10.png')}"/><br><br>

            <p>
                Para poder ingresar al sistema es necesario contar con un nombre de usuario y
                contrase&ntildea, los mismos que se asignar&aacuten para cada proveedor que
                participe en el
                proceso de contrataci&oacuten.
            </p>

            <p>
                Se inicia por hacer un clic en el bot&oacuten &#34;Ingresar&#34;, con lo
                cual aparece la pantalla:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen11.png')}"/><br><br>

            <p>
                Ingrese su nombre de usuario y contrase&ntildea y presione el bot&oacuten ingresar. En el caso
                de que haya olvidado la contrase&ntildea, haga clic en &#34;Olvid&oacute
                su contrase&ntildea&#34; y se enviar&aacute
                por mail su contrase&ntildea al correo electr&oacutenico registrado del oferente.

            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen11.2.png')}"/><br><br>

            <p>
                Si se ha pulsado Ingresar aparecerá la pantalla de selección de perfil, seleccione Oferente y haga clic en Entrar:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen12.png')}"/><br><br>

            <p>
                Seguidamente aparece la pantalla principal del sistema:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen13.png')}"/><br><br>

            <p>

                Esta pantalla se halla compuesta de un men&uacute principal en cual figuran las opciones:
                Administraci&oacuten (no visible para los oferentes), APU o an&aacutelisis de precios, Obras y Salir.
                <a id="Registro de An&aacutelisis de Precios"></a>

            </p>


            <div class="regresa">
                <p><a href="#ModuloGanadores">M&oacutedulo de Oferentes Ganadores</a></p>
            </div> <br><br><br>


            <h3 class="izquierda">
                Registro de An&aacutelisis de Precios
            </h3>

            <p>
                Este es un m&oacutedulo en el cual el oferente tendr&aacute toda la
                libertad de armar sus rubros o
                an&aacutelisis de precios unitarios conforme a sus necesidades. El
                sistema proporcionar&aacute la
                base de datos de los &iacutetemes de mano de obra, equipos y materiales como insumos para la
                creaci&oacuten de los rubros que requiera el oferente.
            </p>

            <p>
                Para acceder a la pantalla de administraci&oacuten de rubros , se
                debe usar la opci&oacuten del
                men&uacute APU &#8211&gt Rubros del men&uacute principal.

            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen14.png')}"/><br><br>

            <p>
                La pantalla muestra una plantilla donde se deben definir los rubros:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen15.png')}"/><br><br>

            <p>
                Desde esta pantalla se puede consultar la estructura de un rubro existente
                en el sistema, crear uno nuevo, borrar uno existente, modificar la
                composici&oacuten de un rubro y ver el detalle de precios con o sin desglose de
                transporte de un rubro espec&iacutefico.
            </p>

            <p>
                La pantalla de ingreso est&aacute dividida en tres partes: barra de comandos,
                datos del rubro, lista de precios e ingreso de &iacutetemes en la
                composici&oacuten del
                rubro y el detalle de su composici&oacuten organizada por equipos, mano de obra y
                materiales. Esta estructura se modifica ligeramente cuando se presenta adem&aacutes
                los valores de los precios incluyendo transporte y los costos indirectos.
                <a id="Barra de comandos"></a>
            </p>

            <div class="regresa">
                <p><a href="#ModuloGanadores">M&oacutedulo de Oferentes Ganadores</a></p>
            </div> <br><br><br>


            <h3 class="izquierda">
                Barra de comandos
            </h3>

            <p>
                Cuando la pantalla se halla en blanco, es decir, sin un rubro cargado, la barra de comandos sólo muestra el botón Lista y Cancelar.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen16.1.png')}"/><br><br>

            <p>
                Si se ha cargado un rubro en pantalla en la barra aumentan varios botones que permiten otras funcionalidades.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen16.png')}"/><br><br>

            <p>
                <strong>Lista:</strong> Muestra una ventana donde se pueden buscar rubros
            ingresados al sistema.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen17.png')}"/><br><br>

            <p>
                En esta pantalla se puede realizar b&uacutesquedas por c&oacutedigo o por el nombre
                o descripci&oacuten del rubro. Las b&uacutesquedas pueden ser con criterios como
                &#34;Empieza con&#34;, &#34;Contiene&#34; o &#34;es igual a&#34;,
                adem&aacutes se puede obtener el
                resultado de la b&uacutesqueda ordenado por el c&oacutedigo o la descripci&oacuten en
                forma ascendente o descendente. Al presionar el bot&oacuten Buscar se muestra
                el resultado dela b&uacutesqueda.
            </p>

            <p>
                La ilustraci&oacuten a continuaci&oacuten muestra el resultado de buscar por
                descripci&oacuten, rubros que contiene la palabra &#34;cemento&#34;, ordenado por
                c&oacutedigo en forma ascendente.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen18.png')}"/><br><br>

            <p>
                Para seleccionar un rubro, haga un clic en el botón que aparece a la izquierda de cada línea de datos del rubro.
                Al hacer esto aparece el rubro en la pantalla principal como:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen19.png')}"/><br><br>

            <p>
                Inicialmente todos los rubros aparecen vacíos y, es el oferente quien debe conforme a su criterio incluir los elementos de Equipo,
                Mano de obra y Materiales que se deben emplear, junto con los precios y rendimientos respectivos (Los rendimientos no se aplican a los materiales,
                de modo que el sistema desecha el valor de rendimiento cuando se ingresan materiales).
            </p>
            <br/>

            <p>
                Para ingresar los ítems del rubro, haga doble clic en el casillero “Código” en la sección Ítems, con ello aparecerá la pantalla de
                búsquedas para permitirnos buscar el ítem deseado luego al seleccionarlo de la lista se regresa a la pantalla de rubros y se completan
                los datos de precio (este valor incluye transporte a la obra), cantidad y rendimiento. El rendimiento sólo se define para equipos y mano de obra,
                si se ingresa un valor de rendimiento para materiales, el sistema lo desecha automáticamente.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen19.2.png')}"/><br><br>

            <p>
                Ingrese el valor del precio unitario del ítem incluyendo transporte al sitio de la obra, en caso de que ya se haya registrado este
                material para un rubro anterior, el sistema mostrará el precio en forma automática. Para completar el proceso de inclusión del ítem en el
                rubro se debe presionar el botón Agregar (“+”).
            </p>

            <br/>

            <p>
                El botón <strong>Cancelar</strong> sirve sólo para descartar y limpiar la pantalla.
            </p>
            <br/>

            <p>Si se desea modificar la composición, se puede dar doble clic en el ítem en cuestión, para que sus datos se copien en la zona de edición y poderlos corregir.</p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen19.3.png')}"/><br><br>

            <p>
                Si se ingresa un valor en precio, este se usa para todos los rubros que contengan el ítem en cuestión.
            </p>
            <br/>

            <p>
                Finalmente, se debe hacer un clic en <strong>Calcular</strong> para ver en detalle los precios y el valor final del rubro incluido costos indirectos.
            </p>

            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen20.png')}"/><br><br>

            <p>
                Mientras se muestra la pantalla de cálculos no se pueden editar los valores, el sistema presenta una pantalla de aviso indicando que se cambie el modo de
                presentación del rubro  para poder editar los datos.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen20.2.png')}"/><br><br>

            <p>
                Vuelva a presionar el botón “Calcular” para cambiar el modo de visualización de la pantalla de rubros.
            </p>
            <br/>

            <p>
                En el caso de que se desee eliminar un ítem se debe usar el botón “Eliminar” que aparece al lado derecho de cada ítem en la composición del rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen20.3.png')}"/><br><br>

            <p>
                <strong>Calcular:</strong> este botón cambia el aspecto de la pantalla para mostrar los precios de los componentes del rubro incluyendo los
            costos indirectos. Si existen ítemes que aún no poseen precios, el sistema mostrará el valor de 0.000. Para ingresar los precios,
            simplemente haga doble clic en la línea del ítem deseado para que esta se copie a la zona superior (Ítems) y pueda registrar el precio.
            Este proceso sirve también para editar o modificar los datos registrados para cada ítem. En el caso de la cantidad del ítem de “Herramienta menor”,
            el valor se calcula en forma automática de la sumatoria del valor de mano de obra que emplea el rubro, de modo que si se ingresa un valor de precio,
            el sistema lo ignora.
            </p>
            <br/>

            <p>
                Si desea inspeccionar los precios al cambiar el valor del porcentaje de costos indirectos, ingrese el nuevo valor en este casillero y vuelva a presionar
                Calcular para desplegar los nuevos valores. “Calcular” cambia alternativamente el modo de visualización del rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen20.4.png')}"/><br><br>
            %{-- ********************************************************** --}%

            <p>
                El botón <strong>Imprimir</strong> genera un reporte del rubro en formato
            pdf, con los par&aacutemetros
            seleccionados
            de lista de precios, fecha, costos indirectos y las variables de transporte ingresadas.
            </p>

            <p>
                El botón <strong>Excel</strong> genera un archivo en formato de hoja de c&aacutelculo con los datos del
            rubro que se
            halla en pantalla. Una vez generado el  archivo aparecer&aacute un
            cuadro de di&aacutelogo que permite
            descargarlo.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen30.png')}"/><br><br>

            <p>
                <strong>Especificaciones:</strong> Muestra una ventana para el registro de las especificaciones técnicas del rubro.
            Esta ventana permite el ingreso de un archivo en formato pdf con las especifica­ciones del rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen31.png')}"/><br><br>

            <p>
                El oferente sólo puede mirar las especificaciones, no es posible cargar o alimentar el sistema con especificaciones.
            </p>

            <br/>

            <p>
                <strong>Ilustraci&oacuten:</strong> Es similar a las especificaciones de cada rubro con a diferencia que en este caso se visualiza una
            ilustración o “foto” de detalle de rubro. Igualmente, el oferente sólo puede mirar o descargar la ilustración como una ayuda para evitar falsas
            interpretaciones respecto de lo que se requiere en una determinada obra o proyecto.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen32.png')}"/><br><br>
            <a id="Obras"></a>
        </div>


        <div class="regresa">
            <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
        </div> <br>


        <div class="centrado">
            <br> <a id="Obras"></a>

            <h2 class="izquierda">
                Proyecto: Registro de obras
            </h2>

            <p>
                El ingreso de las Obras al sistema está restringido para el oferente, este simplemente debe completar la información con los datos
                de su oferta, es decir, composición de cada uno de los rubros o sus propios análisis de precios, con lo cual se podrá visualizar los
                valores del presupuesto conforme a los volúmenes de obra, luego podrá completar su presupuesto mediante el ingreso de las “variables” o
                sus propios costos indirectos y utilidad. Finalmente podrá generar la matriz d ella fórmula polinómica, armar la fórmula polinómica y conformar
                su cronograma de ejecución de la obra.
            </p>
            <br/>

            <p>
                El sistema le permitirá imprimir los formularios de presupuesto, cronograma, análisis de precios unitarios de todos los rubros que conforman
                la obra y otros documentos como un descriptivo de la obra, matriz de la fórmula polinómica y el detalle de la composición en materiales mano de obra y equipos.
            </p>
            <br/>

            <p>
                Al ingresar a la sección obras mediante la opción de menú Detalle del Proyecto → Registro de Obras, el sistema automáticamente ubica
                la primera obra que posea el oferente, en el caso de no tratarse de la obra que se desea ofertar, se debe usar el botón Lista para cargar la obra correcta.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen33.png')}"/><br><br>

            <p>
                La parte superior de esta pantalla presenta sólo los botones Lista, Imprimir y cambiar de estado, este último servirá
                para “registrar” la obra una vez que se haya completado el proceso de oferta y se bloquea sus datos para que no sean cambiados o modificados
                en el futuro. A continuación se irán explicando cada una de estas funcionalidades.
                <a id="Lista"></a>
            </p>


            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>

            <h3 class="izquierda">
                Lista
            </h3>

            <p>
                Muestra una pantalla donde se puede listar las obras que se encuentran
                ingresadas en el sistema, conforme a criterios de b&uacutesqueda que ayudan a
                localizarlas r&aacutepidamente.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen34.png')}"/><br><br>

            <p>
                Con esta pantalla se pueden ubicar obras aplicando varios criterios de búsqueda como son: código, nombre, descripción, etc.
                Esta pantalla sólo mostrará las obras que se hallen en proceso de contratación relacionadas con el oferente que las está consultando, en el caso de haber varias.
            </p>
            <br/>

            <p>
                Para seleccionar una de las obras se debe hacer un clic en el botón
                <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen72.png')}"/> del lado izquierdo de la obra deseada.
            </p>

            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen36.png')}"/><br><br>

            <p>
                A continuaci&oacuten se explicar&aacute la barra de botones de la parte inferior.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen37.png')}"/>
            <a id="Variables"></a>

            <br><br>

            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>


            <h3 class="izquierda">
                Variables
            </h3>

            <p>
                Esta secci&oacuten permite el registro de las variables asociadas a la obra,
                estas son las de costos indirectos y valor por hora del mec&aacutenico.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen38.png')}"/><br><br>

            <p>
                Ingrese su valores de costos indirectos y el costo hora del mecánico, luego presione Guardar para almacenarlos en el sistema.
                <a id=" Vol&uacutemenes de Obra"></a>

            </p>


            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>

            <h3 class="izquierda">
                Vol&uacutemenes de Obra
            </h3>

            <p>
                Este botón nos lleva al registro de los volúmenes de obra. Esta pantalla presenta dos secciones, una destinada al
                ingreso de datos del rubro dentro de un subpresupuesto y la otra, denominada composición, que muestra una lista de los rubros.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen39.png')}"/><br><br>

            <p>
                Al presionar el botón “Calcular” se presentan los valores unitarios de cada rubro y el total del presupuesto
                o valor de la obra. Si no han registrado aun los análisis de precios o el detalle de composición del cada rubro, los valores unitarios aparecerán con ceros.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen40.png')}"/><br><br>

            <p>
                El botón Regresar sirve para volver a la pantalla de la obra.
            </p>

            <p>
                Cada línea de los rubros que conforman el volumen de obra, posee un menú de se muestra al hacer clic con el botón derecho del ratón,
                tal como se ve en la siguiente ilustración.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen41.png')}"/><br><br>

            <p>
                El botón Imprimir Presupuesto muestra en formato pdf el presupuesto conforme el formulario No. 2 del INCOP.
            </p>
            <br/>

            <p>
                También es posible exportar el detalle de volúmenes de obra a una hoja de calculo tipo excel, presionando el botón excel.
                <br/>
                <br/>
                <a id=" Matriz de la F&oacutermula Polin&oacutemica"></a>
            </p>


            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>


            <h3 class="izquierda">
                Matriz de la F&oacutermula Polin&oacutemica
            </h3>

            <p>
                El bot&oacuten Matriz FP, nos lleva a ejecutar un proceso de c&aacutelculo de la
                matriz de la f&oacutermula polin&oacutemica. Como prerrequisitos, se debe contar con:
            </p>
            <ol>
                <li>
                    <p>
                        Que se hallen ingresados los vol&uacutemenes de Obra
                    </p>
                </li>
                <li>
                    <p>
                        Que se halle definida la lista precios para esta Obra
                    </p>
                </li>
                <li>
                    <p>
                        Que se hayan ingresado todas las distancias al Peso y al
                        Volumen, en el caso de manejarse una sola distancia al peso
                        o al volumen, este valor se debe repetir en las otras distancias.
                    </p>
                </li>
                <li>
                    <p>
                        Todos los rubros de vol&uacutemenes de obras deben tener cantidades
                        positivas y mayores que cero.
                    </p>
                </li>
                <li>
                    <p>
                        Todos los &iacutetems que componen los rubros deben contar con nombres
                        cortos &uacutenicos y sin caracteres especiales.
                    </p>
                </li>
            </ol>

            <p>
                Al presionar el bot&oacuten Matriz FP se presenta una ventana de
                confirmaci&oacuten de la orden:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen44.png')}"/><br><br>

            <p>
                Si ya ha generado la matriz y desea conservar los valores en el caso de que no se
                hayan cambiado los vol&uacutemenes de obra ni actualizado precios,
                conteste &#34No&#34 a esta
                pregunta, caso contrario haga un clic en &#34Si&#34. Seguidamente aparecer&aacute:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen45.png')}"/><br><br>

            <p>
                Presione Generar para iniciar el proceso.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen46.png')}"/><br><br>

            <p>
                Esta ventana se mantendr&aacute mientras se realizan los c&aacutelculos de la f&oacutermula
                polin&oacutemica y se despliega la matriz.
            </p>
            <br/>

            <p>
                Luego de completado el proceso, se despliega la matriz de la f&oacutermula polin&oacutemica.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen47.png')}"/><br><br>

            <p>
                En esta pantalla existen varios botones en la zona superior:
            </p>

            <p>
                <strong>Regresar:</strong> sirve para volver a la pantalla de la obra.
            </p>

            <p>
                <strong>Coeficientes de la matriz:</strong> invoca a la pantalla de
            definici&oacuten de la f&oacutermula polin&oacutemica.
            </p>

            <p>
                <strong>Imprimir:</strong> genera un reporte en pdf de la matriz, este
            halla organizado en forma primero vertical y luego horizontal.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen48.png')}"/><br><br>

            <p>
                Un buscador para poder ubicar f&aacutecilmente las columnas de los distintos
                &iacutetems, para ello, escriba parte del nombre del &iacutetem (nombre corto) en
                la casilla de b&uacutesqueda y haga un clic en el bot&oacuten Buscar. La siguiente
                ilustraci&oacuten muestra el resultado de buscar la columna de transporte.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen49.png')}"/><br><br>

            <p>
                Finalmente, el bot&oacuten limpiar selecci&oacuten sirve para desmarcar las columnas
                que han sido resaltadas como resultado de la b&uacutesqueda.
                <a id=" F&oacutermula polin&oacutemica"></a>
            </p>

            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>


            <h3 class="izquierda">
                F&oacutermula polin&oacutemica
            </h3>

            <p>
                Para acceder a los ajustes de la f&oacutermula polin&oacutemica use el
                bot&oacuten
                &#34Formula Pol.&#34, una vez que se haya corrido el proceso de
                c&aacutelculo de la
                matriz de la f&oacutermula polin&oacutemica.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen50.png')}"/><br><br>

            <p>
                Esta pantalla sirve para componer la f&oacutermula polin&oacutemica en
                funci&oacuten de
                los distintos aportes de materiales obtenidos de la matriz de la f&oacutermula.
            </p>
            <br/>

            <p>
                En la parte superior de esta pantalla figuran 3 botones, Regresar para
                volver a la pantalla de obras, F&oacutermula polin&oacutemica y Cuadrilla tipo.
            </p>
            <br/>

            <p>
                Por defecto la pantalla que se muestra corresponde a la de la fórmula polinómica, lo que equivale a presionar el botón
                Fórmula polinómica, al presionar el botón Cuadrilla tipo, se muestra la pantalla para trabajar en la composición de la cuadrilla tipo.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen51.png')}"/><br><br>

            <p>
                <a id="Trabajando en la composici&oacuten de la f&oacutermula polin&oacutemica"></a>
            </p>

            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>


            <h3>
                Trabajando en la composici&oacuten de la f&oacutermula polin&oacutemica
            </h3>

            <p>
                Para incluir varios materiales en un coeficiente, siga el siguiente proceso:
            </p>
            <ol>
                <li>
                    <p>
                        Haga clic en el coeficiente al cual desea a&ntildeadir la
                        participaci&oacuten del
                        o de los materiales. Con esto queda se&ntildealado el coeficiente.
                    </p>
                </li>
                <li>
                    <p>
                        Haga clic sobre los materiales que desea adicionar o incluir en el
                        coeficiente.
                    </p>
                </li>
                <li>
                    <p>
                        Haga un clic en el bot&oacuten verde Agregar a … que aparece sobre la
                        columna de materiales.
                    </p>
                </li>
            </ol>

            <p>
                Como resultado de este proceso, se reestructura la composici&oacuten de los
                coeficientes, recalcul&aacutendose el valor del coeficiente. La pantalla
                quedar&aacute similar a:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen52.png')}"/><br><br>

            <p>
                Si se desea ajustar el valor del coeficiente o cambiar la descripci&oacuten
                o material del &iacutendice, haga clic derecho sobre el &iacutecono del
                coeficiente y seleccione Editar.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen53.png')}"/><br><br>

            <p>
                Al seleccionar editar aparecer&aacute la pantalla ilustrada a
                continuaci&oacuten en la cual se podr&aacute cambiar el material y ajustar
                el valor del coeficiente.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen54.png')}"/><br><br>

            <p>
                Para quitar un material de un coeficiente se debe seguir el siguiente
                proceso:
            </p>
            <ol>
                <li>
                    <p>
                        Haga clic derecho en el material a eliminar, y seleccione Eliminar.
                    </p>
                </li>
                <li>
                    <p>
                        Confirme la orden dada.
                    </p>

                </li>
            </ol>
            <table>
                <tr>
                    <td>
                        <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen55.png')}"/><br><br>
                    </td>
                    <td>
                        <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen56.png')}"/><br><br>
                    </td>
                </tr>
            </table>

            <p>
                Tan s&oacutelo el concepto de p01 no puede se ajustado, todos los valores
                de los coeficientes se pueden ajustar para que no haya descuadre en los decimales.
            </p>

            <p>
                En el caso de que la pantalla no muestre todos los datos, se debe presionar
                F5 o refrescarla para que se desplieguen todos los datos.
                <br/>
                <br/>
                <a id="Rubros de la obra"></a>
            </p>

            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>

            <h3 class="izquierda">
                Rubros de la obra
            </h3>

            <p>
                Haciendo un clic en el bot&oacuten Rubros podemos imprimir el todos los
                rubros de la obra con o sin desglose de transporte.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen57.png')}"/><br><br>

            <p>
                De acuerdo como se desee, se generar&aacute un reporte en formato pdf
                con los an&aacutelisis de precios de todos los rubros que se hallan en la
                obra, en hojas separadas. El reporte es extenso y puede tomar unos pocos
                minutos su generaci&oacuten.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen58.png')}"/><br>
            <a id="Cronograma"></a>
            <br>


            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>


            <h3 class="izquierda">
                Cronograma
            </h3>

            <p>
                El cronograma de la obra se lo puede trabajar en base a las cantidades de obra
                o en base a porcentajes de avance.
            </p>

            <p>
                Para acceder a la pantalla del cronograma en el bot&oacuten &#34Cronograma&#34.
                Al abrirla por primera vez, se muestra la tabla vac&iacutea. Las columnas
                &#34Mes #&#34 se
                generan autom&aacuteticamente seg&uacuten el plazo  asignado a la obra.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen59.png')}"/><br><br>

            <p>
                El bot&oacuten <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen73.png')}"/> permite regresar a la
            pantalla de
            registro de la obra.
            </p>

            <p>
                Al hacer clic en una fila, se marcan en azul las 3 filas
                correspondientes al rubro
                correspondiente. Este es el rubro seleccionado.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen60.png')}"/><br><br>

            <p>
                Las celdas de los meses es donde se deben ingresar los datos de la
                planificaci&oacuten. Para hacer esto, se da doble clic en cualquiera de las
                tres celdas que corresponden a los per&iacuteodos mensuales &#34Mes #&#34, ya
                sea $, % o F. Esto abre una ventana que muestra los datos del rubro y un
                formulario para asignar un valor.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen61.png')}"/><br><br>

            <p>
                Si se han seleccionado varios rubros el único valor que puede modificarse es el del porcentaje (%) y se aplicará
                el mismo porcentaje a todos los rubros seleccionados, eliminando cualquier otro valor ya existente en estos.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen61.2.png')}"/><br><br>

            <p>
                El &aacuterea de per&iacuteodos permite seleccionar un periodo, o un rango de
                periodos. Al seleccionar un rango se dividir&aacute lo asignado entre el
                n&uacutemero de periodos que contenga el rango; por ejemplo, al asignar 50%
                a los periodos del 1 al 2 asignar&aacute 25% al periodo 1 y 25% al periodo
                2. Se puede ingresar la cantidad en cualquiera de los campos de cantidad,
                porcentaje o precio, y los otros campos se calcular&aacuten de acuerdo al
                rango de per&iacuteodos especificado. El sistema valida los valores que ya
                est&eacuten ingresados y no permite ingresar valores superiores al total.
                Al hacer clic en el bot&oacuten Aceptar se guardan los valores y se
                actualiza la tabla del cronograma.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen62.png')}"/><br><br>

            <p>
                El botón “Eliminar rubro” borra los campos del rubro seleccionado, después de mostrar una pantalla de confirmación de la orden.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen62.2.png')}"/><br><br>

            <p>
                El botón “Eliminar Cronograma” borra todos los campos de todos los rubros de la tabla, después de mostrar una notificación.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen62.3.png')}"/><br><br>

            <p>
                El bot&oacuten<img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen68.png')}"/>permite ver los gr&aacuteficos de
            avance econ&oacutemico y f&iacutesico de la obra.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen69.png')}"/><br><br>

            <p>
                El bot&oacuten<img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen70.png')}"/> permite regresar a la pantalla del cronograma.
                <a id="Composici&oacuten de la Obra"></a>
            </p>

            <div class="regresa">
                <p><a href="#Obras">Volver al Obras</a></p>
            </div> <br><br>


            <h3 class="izquierda">
                Composici&oacuten de la Obra
            </h3>

            <p>
                Los &iacutetems que componen la obra se muestra al hacer un clic
                en el bot&oacuten &#34Composici&oacuten&#34, con ello, se muestra
                la pantalla de composici&oacuten organizada por grupos de &iacutetems,
                es decir, por materiales, mano de obra y equipos.
            </p>
            <br/>

            <p>
                Este es b&aacutesicamente un reporte de la obra y puede ser
                obtenido en pdf o exportado a excel.

            </p>

            <p>
                Los comandos de esta pantalla son:
            </p>
            <br/>

            <p>
                <strong>Regresar:</strong> que sirve para regresar a la pantalla
            de la obra.
            </p>

            <p>
                <strong>Todos:</strong> muestra todos los &iacutetems de la obra:
            materiales, mano de obra y equipos.
            </p>

            <p>
                <strong>Materiales:</strong> muestra s&oacutelo los materiales que
            componen la obra.
            </p>

            <p>
                <strong>Mano de obra:</strong> muestra los &iacutetems de mano de
            obra.
            </p>

            <p>
                <strong>Equipos:</strong> muestra los &iacutetems de equipos
            </p>

            <p>
                <strong>Pdf:</strong> genera un reporte en formato pdf de los
            &iacutetems mostrados en pantalla.
            </p>

            <p>
                <strong>Excel:</strong> genera un archivo de hoja de c&aacutelculo
            xls, de los &iacutetems mostrados en pantalla.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen71.png')}"/><br><br>

            <p>
                Se puede obtener la composición para cada subpresupuesto, seleccionándolo desde el combo que inicialmente aparece como “Todos los subpresupuestos”.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen71.2.png')}"/><br><br>

            <div class="regresa">
                <p><a href="#volverIndice">Volver al &Iacutendice</a></p>
            </div> <br><br>
        </div>
        <g:link controller="inicio" action="index">&lt;&lt;&nbsp;Regresar al sistema</g:link>
    </body>

</html>
