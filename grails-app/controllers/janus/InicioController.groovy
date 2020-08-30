package janus

import janus.seguridad.Prfl

//import com.linuxense.javadbf.DBFField
//import com.linuxense.javadbf.DBFReader
//
//import com.linuxense.javadbf.*

class InicioController extends janus.seguridad.Shield {
    def dbConnectionService
    def oferentesService

    def index() {
        def cn = dbConnectionService.getConnection()
        def prms = []
        def acciones = "'rubroPrincipal', 'registroObra', 'registrarPac', 'verContrato'"
        def tx = "select accnnmbr from prms, accn where prfl__id = " + Prfl.findByNombre(session.perfil.toString()).id +
                " and accn.accn__id = prms.accn__id and accnnmbr in (${acciones})"
        cn.eachRow(tx) { d ->
            prms << d.accnnmbr
        }
        cn.close()
        def empr = Parametros.get(1)

        //println "formula "
        //oferentesService.copiaFormula(1457,1485)
       // println " crono "
        //oferentesService.copiaCrono(1457,1485)
        return [prms: prms, empr: empr]
    }


    def inicio() {
        redirect(action: "index")
    }

    def parametros = {

    }

    def arbol () {

    }

    def manualObras = {

    }

    def variables () {
        def paux = Parametros.get(1);
        def par = Parametros.list()
        def total1 = (paux?.indiceAlquiler ?: 0) + (paux?.administracion ?: 0) + (paux?.indiceCostosIndirectosMantenimiento ?: 0) + (paux?.indiceProfesionales ?: 0) + (paux?.indiceSeguros ?: 0)  + (paux?.indiceSeguridad ?: 0)
        def total2 = (paux?.indiceCampo ?: 0) + (paux?.indiceCostosIndirectosCostosFinancieros ?: 0) + (paux?.indiceCostosIndirectosGarantias ?: 0) + (paux?.indiceCampamento ?: 0)
        def total3 = (total1 ?:0 ) + (total2 ?: 0) + (paux?.impreso ?: 0) + (paux?.indiceUtilidad ?: 0)
        
        paux.totales = total3
        paux.save(flush: true)

        return[paux: paux, par: par, totalCentral: total1, totalObra: total2]
    }


    /** carga datos desde un CSV - utf-8: si ya existe lo actualiza
     * */
    def leeCSV() {
//        println ">>leeCSV.."
        def contador = 0
        def cn = dbConnectionService.getConnection()
        def estc
        def rgst = []
        def cont = 0
        def repetidos = 0
        def procesa = 5
        def crea_log = false
        def inserta
        def fcha
        def magn
        def sqlp
        def directorio
//        def tipo = 'prueba'
        def tipo = 'prod'

        if (grails.util.Environment.getCurrent().name == 'development') {
            directorio = '/home/guido/proyectos/GADPP/data'
        } else {
            directorio = '/home/obras/data/'
        }

        if (tipo == 'prueba') { //botón: Cargar datos Minutos
            procesa = 5000
            crea_log = false
        } else {
            procesa = 100000000000
            crea_log = true
        }

        def nmbr = ""
        def arch = ""
        def cuenta = 0
        new File(directorio).traverse(type: groovy.io.FileType.FILES, nameFilter: ~/.*\.csv/) { ar ->
            nmbr = ar.toString() - directorio
            arch = nmbr.substring(nmbr.lastIndexOf("/") + 1)

            /*** procesa las 5 primeras líneas del archivo  **/
            def line
            cont = 0
            repetidos = 0
            ar.withReader('UTF-8') { reader ->
                print "Cargando datos desde: $ar "
                while ((line = reader.readLine()) != null) {
                    if (cuenta > 0 && cuenta < procesa) {
//                        println "${line}"

                        rgst = line.split('\t')
                        rgst = rgst*.trim()
//                        println "***** $rgst"

                        inserta = cargaData(rgst)
                        cont += inserta.insertados
                        repetidos += inserta.repetidos

                        if (rgst.size() > 2 && rgst[-2] != 0) cuenta++  /* se cuentan sólo si hay valores */

                    } else {
                        cuenta++
                    }
                }

            }
            println "---> archivo: ${ar.toString()} --> cont: $cont, repetidos: $repetidos"
        }
//        return "Se han cargado ${cont} líneas de datos y han existido : <<${repetidos}>> repetidos"
        render "Se han cargado ${cont} líneas de datos y han existido : <<${repetidos}>> repetidos"
    }


    def cargaData(rgst) {
        def errores = ""
        def cnta = 0
        def insertados = 0
        def repetidos = 0
        def cn = dbConnectionService.getConnection()
        def sqlParr = ""
        def sql = ""
        def cpac = 0

//        println "\n inicia cargado de datos para $rgst"
        cnta = 0
        if (rgst[1].toString().size() > 0) {
            sqlParr = "select count(*) cnta from cpac where cpacnmro = '${rgst[0]}'"
//            println "sqlParr: $sqlParr"
            cnta = cn.rows(sqlParr.toString())[0]?.cnta
            if (cnta == 0) {
                sql = "insert into cpac (cpac__id, cpacnmro, cpacdscr, cpacnvel, cpacfcha, cpacmvnt) " +
                        "values(default, '${rgst[0]}', '${rgst[1]}', ${rgst[2]}, now()::date, 1) "
//                        "on conflict (parr__id, cmndnmbr) DO NOTHING"

//                println "sql: $sql"
            }

            try {
                cn.execute(sql.toString())
                if (cn.updateCount > 0) {
                    insertados++
                }
            } catch (Exception ex) {
                repetidos++
                println "Error al insertar $ex"
                println "sql: $sql"
            }

        }
        cnta++
        return [errores: errores, insertados: insertados, repetidos: repetidos]
    }



}
