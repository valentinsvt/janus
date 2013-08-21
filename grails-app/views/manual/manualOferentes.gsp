<!DOCTYPE html>
<html>

    <head>

        <meta name="layout" content="manuales"/>


        <title>Manual de Usuario - Oferentes</title>
        <style>

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

        .sinsenal {
            list-style-type : none;
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

            <h3>TEDEIN S.A. 2012 - 2013</h3>
            <br><br><br>
        </div>

        <div id="indice">
            <br>

            <h2>
                &Iacutendice de contenido
            </h2>
            <ol class="sinsenal">
                <li>
                    <h4>M&oacutedulo de Oferentes Ganadores</h4>
                    <ul class="sinsenal">
                        <li>
                            <a href="#Introduccion">Introducción</a>
                        </li>
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
                    <a href="#Obras"><h4>Obras</h4></a>
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
            <h2 class="izquierda">M&oacutedulo de Oferentes Ganadores</h2>
            <br>

            <p>El m&oacutedulo de oferentes consiste de un complemento al sistema de gesti&oacuten de
            proyectos, contrataci&oacuten, fiscalizaci&oacuten y control de obras, que sirve para
            el registro de cuentas de oferentes y, un sistema en l&iacutenea completamente
            independiente que estar&aacute a disposici&oacuten de los oferentes mediante un nombre
            de usuario y contrase&ntildea durante el per&iacuteodo de recepci&oacuten de ofertas.
                <a id="Registro de cuentas de Oferentes"></a>
            </p>
        </div>

        <div class="centrado">
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
                        3. El sistema responder&aacute con un mensaje indicando que se han copiado los
                        oferentes o si ya se han copiado y se intenta repetir el proceso, un
                        mensaje de error indicando que ya han sido copiados.
                    </p>
                </li>
            </ol>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen6.png')}"/>
            <br><br>
            <a id="Vinculaci&oacuten de la Obra a cada oferente"></a>
        </div>

        <div>
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
        </div>

        <div class="centrado">
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

        </div>

        <div class="centrado">

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

        </div>

        <div class="centrado">

            <h3 class="izquierda">
                Barra de comandos
            </h3>

            <p>
                Los comandos disponibles en esta pantalla son los siguientes:
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
                Si ya ha registrado un rubro, puede seleccionarlo haciendo un clic
                en el bot&oacuten
                <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen25.png')}"/>que
            aparece a la izquierda de los datos del rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen19.png')}"/><br><br>

            <p>
                Para ver el precio del rubro, haga un clic en el bot&oacuten Calcular
                para desplegar los precios de cada &iacutetem que conforma el rubro, costos
                indirectos y costo total.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen20.png')}"/><br><br>

            <p>
                A continuaci&oacuten se describir&aacute uno a uno los botones que aparecen en
                la barra de heramientas.
            </p>

            <p>
                <strong>Nuevo:</strong> El bot&oacuten nuevo limpia la pantalla y la prepara para el
            registro de un nuevo rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen21.png')}"/><br><br>

            <p>
                En esta pantalla se deben llenar todos los campos de la secci&oacuten
                rubro antes de proceder a ingresar los &iacutetemes de su
                composici&oacuten,
                los cuales pueden ser ingresados en cualquier orden. Para a&ntildeadir un &iacutetem use la
                secci&oacuten:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen22.png')}"/><br><br>

            <p>
                Al hacer un clic en el casillero de c&oacutedigo, vuelve a
                aparecer la
                pantalla de b&uacutesquedas para permitirnos buscar el &iacutetem deseado luego
                al seleccionarlo de la lista se regresa a la pantalla de rubros y
                se completan los datos de cantidad y rendimiento. El rendimiento s&oacutelo
                se define para equipos y manos de obra, si se ingresa un valor de
                rendimiento para materiales, el sistema lo desecha autom&aacuteticamente.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen23.png')}"/><br><br>

            <p>
                Ingrese el valor del precio unitario del &iacutetem incluyendo
                transporte
                al sitio de la obra, en caso de que ya se haya registrado este
                material para un rubro anterior, el sistema mostrar&aacute el precio en
                forma autom&aacutetica. Para completar el proceso de inclusi&oacuten del &iacutetem
                en el rubro se debe presionar el bot&oacuten Agregar (&#34;+&#34;).
            </p>

            <p>
                Los botones Guardar, Canelar y Borrar sirven para almacenar los datos
                del rubro en el sistema, descartar cambios o eliminar el rubro del sistema
                respectivamente. S&oacutelo los rubros que no hayan sido utilizados en una
                obra podr&aacuten ser eliminados.
            </p>

            <p>
                El bot&oacuten &#34;Copiar composici&oacuten&#34; permite crear un
                rubro en base a uno
                existente. Al hacer un clic en este bot&oacuten aparece una ventana donde se
                selecciona el rubro desde el cual se ha de copiar la composici&oacuten al rubro
                en pantalla que se est&aacute creando. Los &iacutetemes del rubro seleccionado se a&ntildeaden
                a los existentes del rubro en pantalla.
            </p>

            <p>
                Para poder usar este bot&oacuten se debe crear primero la cabecera del rubro y
                hacer un clic en el bot&oacuten Guardar. Con esto se llenan los datos de la secci&oacuten
                Rubro de esta pantalla.
            </p>

            <p>
                Seguidamente presione el bot&oacuten Copiar composici&oacuten con lo
                cual aparecer&aacute una
                pantalla de b&uacutesqueda para ubicar el rubro del cual se quiere copiar la
                composici&oacuten.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen24.png')}"/><br><br>

            <p>
                Para copiar la composici&oacuten se hace un clic en el &iacutecono
                <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen25.png')}"/>

                y seguidamente confirme la orden. Los &iacutetemes del rubro seleccionado se a&ntildeaden a
                los del rubro que se tiene en pantalla
            </p>

            <p>
                Si se desea corregir la composici&oacuten, se puede dar doble clic en el &iacutetem
                en cuesti&oacuten, para que sus datos se copien en la zona de edici&oacuten y se poderlos corregir.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen26.png')}"/><br><br>

            <p>
                En el caso de que se desee eliminar un &iacutetem se debe usar el bot&oacuten &#34;Eliminar&#34;
                que aparece al lado derecho de cada &iacutetem en la composici&oacuten del rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen27.png')}"/><br><br>

            <p><strong>Calcular:</strong> este bot&oacuten cambia el aspecto de la pantalla para
            mostrar los precios de los componentes del rubro incluyendo los costos indirectos
            . Si existen &iacutetemes que a&uacuten no poseen precios, el sistema mostrar&aacute
            el valor de 0.000.
            Para ingresar los precios, simplemente haga doble clic en la l&iacutenea del
            &iacutetem deseado
            para que esta se copie a la zona superior (Items) y pueda registrar el precio. Este
            proceso sirve tambi&eacuten para editar o modificar los datos registrados para
            cada &iacutetem.
            En el caso de la cantidad del &iacutetem de &#34;Herramienta menor&#34;, el
            valor se calcula en
            forma autom&aacutetica de la sumatoria del valor de mano de obra que emplea el rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen28.png')}"/><br><br>

            <p>
                Los precios que se despliegan son los correspondientes a la lista de precios
                seleccionada, a la fecha fijada en pantalla y al valor del porcentaje de costos
                indirectos.
            </p>

            <p>
                <strong>Borrar:</strong> Borra el Rubro y toda su composici&oacuten de insumos. Para
            poder borrar es necesario que el Rubro no se encuentre registrado y que no sea
            utilizado en una obra.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen29.png')}"/><br><br>

            <p>
                <strong>Imprimir:</strong> genera un reporte del rubro en formato
            dpf, con los par&aacutemetros
            seleccionados
            de lista de precios, fecha, costos indirectos y las variables de transporte ingresadas.
            </p>

            <p>
                <strong>Excel:</strong> genera un archivo en formato de hoja de c&aacutelculo con los datos del
            rubro que se
            halla en pantalla. Una vez generado el archivo aparecer&aacute un
            cuadro de di&aacutelogo que permite
            descargarlo.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen30.png')}"/><br><br>

            <p>
                <strong>Especificaciones:</strong> Muestra una ventana para el registro de las
            especificaciones t&eacutecnicas del
            rubro. Esta ventana permite el ingreso de hasta 12 l&iacuteneas de
            texto aproximadamente,
            por lo
            que se recomienda hacer una descripci&oacuten objetiva y simple del rubro.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen31.png')}"/><br><br>

            <p>
                Una vez ingresada la especificaci&oacuten presione el bot&oacuten Guardar
                para que se grabe en el
                sistema.
            </p>

            <p>
                Ilustraci&oacuten: Sirve para cargar al sistema una ilustraci&oacuten o
                detalle gr&aacutefico del rubro.
                Para
                hacerlo debe contar con una foto o imagen digital del rubro y luego usar
                el bot&oacuten
                examinra para
                ubicarlo. Una vez que se haya cargado el archivo se mostrar&aacute la ilustraci&oacuten en la
                zona inferior
                de la venta, algo similar a:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen32.png')}"/><br><br>
            <a id="Obras"></a>
        </div>

        <div class="centrado">
            <br> <a id="Obras"></a>

            <h2 class="izquierda">
                Obras
            </h2>

            <p>
                El ingreso de las Obras al sistema est&aacute restringido para el oferente, este
                simplemente debe completar la informaci&oacuten con los datos de su oferta, es decir,
                sus vol&uacutemenes de obra, presupuesto, f&oacutermula polin&oacutemica y cronograma.
            </p>

            <p>
                Al ingresar a la secci&oacuten obras mediante la opci&oacuten de men&uacute
                Obras &#8211&gt Registro
                de Obras, el sistema autom&aacuteticamente ubica la primera obra que posea el
                oferente, en el caso de no tratarse de la obra que se desea ofertar, se
                debe usar el bot&oacuten Lista para cargar la obra correcta.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen33.png')}"/><br><br>

            <p>
                La parte superior de esta pantalla presenta s&oacutelo el bot&oacuten Lista
                e Imprimir.
                A continuaci&oacuten se ir&aacuten explicando cada uno de estas funcionalidades.
                <a id="Lista"></a>
            </p>

        </div>

        <div class="centrado">

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
                Con esta pantalla se pueden ubicar obras aplicando varios criterios de b&uacutesqueda
                como son: c&oacutedigo, nombre, descripci&oacuten, memorando de ingreso, memorando de
                salida, sitio, plazo, parroquia y comunidad.
            </p>

            <p>
                Para cada criterio de b&uacutesqueda se pueden aplicar operaciones como contiene,
                empieza o igual. Estas operaciones se aplican al texto de los datos de la obra,
                as&iacute, si se selecciona en &#34;Buscar por&#34; nombre, se aplica
                &#34;Contiene&#34; y se
                ingresa un criterio como calles, se listar&aacuten toas las obras que contengan
                la palabra calles como nombre de la obra.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen35.png')}"/><br><br>

            <p>
                Para seleccionar una de las obras se debe hacer un clic en el bot&oacuten
                <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen72.png')}"/>
                de la obra deseada.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen36.png')}"/><br><br>

            <p>
                A continuaci&oacuten se explicar&aacute la barra de botones de la parte inferior.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen37.png')}"/>
            <a id="Variables"></a>

        </div>

        <div class="centrado">

            <h3 class="izquierda">
                Variables
            </h3>

            <p>
                Esta secci&oacuten permite el registro de las variables asociadas a la obra,
                estas son las de costos indirectos y valor por hora del mec&aacutenico.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen38.png')}"/><br><br>

            <p>
                Ingrese los valores necesarios y presiones Guardar para almacenarlos en
                el sistema.
                <a id=" Vol&uacutemenes de Obra"></a>

            </p>

        </div>

        <div class="centrado">

            <h3 class="izquierda">
                Vol&uacutemenes de Obra
            </h3>

            <p>
                Este bot&oacuten nos lleva al registro de los vol&uacutemenes de obra. Esta pantalla
                presenta dos secciones, una destinada al ingreso de datos del rubro
                dentro de un subpresupuesto y la otra, denominada composici&oacuten, que
                muestra una lista de los rubros.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen39.png')}"/><br><br>

            <p>
                Al presionar el bot&oacuten &#34;Calcular&#34; se presentan los valores.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen40.png')}"/><br><br>

            <p>
                El bot&oacuten Regresar sirve para volver a la pantalla de registro de obras.
            </p>

            <p>
                Cada l&iacutenea de los rubros que conforman el volumen de obra, posee
                un men&uacute de se muestra al hacer clic con el bot&oacuten derecho del rat&oacuten,
                tal como se ve en la siguiente ilustraci&oacuten.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen41.png')}"/><br><br>

            <p>
                La opci&oacuten Editar, copia los datos del rubro en la zona de edici&oacuten:
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen42.png')}"/><br><br>

            <p>
                Al hacer clic en Editar se copian los datos a los casilleros (campos)
                respectivos :
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen43.png')}"/><br><br>

            <p>
                Luego de hacer los cambios deseados se guardan los datos haciendo un
                clic en el bot&oacuten &#34;+&#34;.
                <a id=" Matriz de la F&oacutermula Polin&oacutemica"></a>

            </p>

        </div>

        <div class="centrado">

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
                Si se desea hacer una matriz de un subpresupuesto espec&iacutefico,
                selecci&oacutenelo desde el
                combo, caso contrario, s&oacutelo verifique que la casilla de &#34Generar
                con transporte&#34 est&eacute
                de acuerdo a lo que requiera, es decir, con un visto si si desea hacer el desglose de
                transporte o sin el visto si no. Presione Generar para iniciar el proceso.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen46.png')}"/><br><br>

            <p>
                Esta ventana se mantendr&aacute mientras se realizan los c&aacutelculos de la f&oacutermula
                polin&oacutemica y se despliega la matriz.
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

        </div>

        <div class="centrado">

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

            <p>
                En la parte superior de esta pantalla figuran 3 botones, Regresar para
                volver a la pantalla de obras, F&oacutermula polin&oacutemica y Cuadrilla tipo.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen51.png')}"/><br><br>

            <p>
                Por defecto la pantalla que se muestra corresponde a la de la f&oacutermula
                polin&oacutemica, lo que equivale a presionar el bot&oacuten F&oacutermula
                polin&oacutemica, al
                presionar el bot&oacuten Cuadrilla tipo, se muestra la pantalla para trabajar
                en la composici&oacuten de la cuadrilla tipo.
                <a id="Trabajando en la composici&oacuten de la f&oacutermula polin&oacutemica"></a>
            </p>

        </div>

        <div class="centrado">

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
                <a id="Rubros de la obra"></a>
            </p>

        </div>

        <div class="centrado">

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

        </div>

        <div class="centrado">

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
                generan autom&aacuteticamente seg&uacuten el plazo asignado a la obra.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen59.png')}"/><br><br>

            <p>
                El bot&oacuten
                <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen73.png')}"/> permite
            regresar a la
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
                El
                bot&oacuten<img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen74.png')}"/>
                limpia los campos del
                rubro seleccionado,
                despu&eacutes de mostrar una pantalla de confirmaci&oacuten de la orden.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen63.png')}"/><br><br>

            <p>
                Esta modificaci&oacuten es solo visual, es decir, los datos existentes
                no se eliminan, s&oacutelo se limpian los casilleros. Los valores
                ser&aacuten modificados s&oacutelo si se ingresan nuevos datos en estos
                casilleros. Esto quiere decir que si no se ingresa ning&uacuten dato que
                sobreescriba los limpiados, la siguiente vez que se abra el cronograma
                volver&aacuten a aparecer.
            </p>

            <p>
                El bot&oacuten
                <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen64.png')}"/>
                limpia los campos de todos los rubros
                de la tabla, despu&eacutes de mostrar una notificaci&oacuten.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen65.png')}"/><br><br>

            <p>
                Al igual que la opci&oacuten anterior, esta modificaci&oacuten es solo
                visual, es decir, los datos existentes solo se reemplazar&aacuten si se
                ingresan nuevos datos. Esto quiere decir que si no se ingresa
                ning&uacuten dato que sobreescriba los limpiados, la siguiente vez que
                se abra el cronograma volver&aacuten a aparecer.
            </p>

            <p>
                Si se desea eliminar todos los datos del cronograma se debe hacer un
                clic en el bot&oacuten &#34Eliminar cronograma&#34 y confirmar la orden.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen66.png')}"/><br><br>

            <p>
                Esta modificaci&oacuten es inmediata, es decir, al dar clic en
                Aceptar se eliminar&aacuten los valores de la base de datos y no hay
                manera de recuperarlos.
            </p>

            <p>
                El bot&oacuten
                <img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen67.png')}"/> exporta
            la tabla del
            cronograma a
            una hoja Excel.
            </p>

            <p>
                El
                bot&oacuten<img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen68.png')}"/>permite
            ver los gr&aacuteficos de
            avance econ&oacutemico y f&iacutesico de la obra.
            </p>
            <br><img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen69.png')}"/><br><br>

            <p>
                El
                bot&oacuten<img src="${resource(dir: 'images/imagenesManuales/imagenesOferentes', file: 'imagen70.png')}"/>
                permite regresar a la pantalla del cronograma.
                <a id="Composici&oacuten de la Obra"></a>
            </p>

        </div>

        <div class="centrado">

            <h3 class="izquierda">
                Composici&oacuten de la Obra
            </h3>

            <p>
                Los &iacutetems que componen la obra se muestra al hacer un clic
                en el bot&oacuten &#34Composici&oacuten&#34, con ello, se muestra
                la pantalla de composici&oacuten organizada por grupos de &iacutetems,
                es decir, por materiales, mano de obra y equipos.
            </p>

            <p>
                Este es b&aacutesicamente un reporte de la obra y puede ser
                obtenido en pdf o exportado a excel.

            </p>

            <p>
                Los comandos de esta pantalla son:
            </p>

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
        </div>

    </body>

</html>
