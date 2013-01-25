package janus.utilitarios
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsDomainBinder
class MigracionService {

    def dbConnectionService
    def grailsApplication



    def arreglaDominios(dominio){
        def dom = grailsApplication.getDomainClass(dominio)
        println "dom "+dom
        def mapa =  GrailsDomainBinder.getMapping(dom)
        def cols = mapa.columns
        def tabla=mapa.table.name
        def sql = "update "+tabla+" set &=0 where & is null"
        def html = "Acciones tomadas <br><br><br><br>"
        def cn = dbConnectionService.getConnection()
//        println "mapa "+mapa.columns+" "
        def names = dom.persistentProperties.collect{ it }
        names.each {

            if(it.type.toString()=="double" || it.type.toString()=~"Double" || it.type.toString()=="int" ||  it.type.toString()=~"Int"){
                html+= "<b>Campo "+it.name+" de tipo  "+it.type+" columa ${cols[it.name].getColumn()}</b><br>"
                sql = sql.replaceAll("&",cols[it.name].getColumn())
                html +="sql==> "+sql+"  <br>"
                html +=cn.executeUpdate(sql.toString())+" columnas afectadas <br><br>"
                sql = "update "+tabla+" set &=0 where & is null"
            }
        }

        cn.close()
        return html
    }

    def arreglarSecuencias(){
        def cn = dbConnectionService.getConnection()
        def tablas = cargarTablas(cn)
        def updates = []
        def sqlUpdate = "select setval('&',(select COALESCE(max(!)+1,1) from @))"
        def res
        def html=""
        tablas.each {
            def sec = getSecuencia(cn,it)
            def col = sec[1]
            if (sec && sec!=""){
                sec = sec[0].split("'")
                sec = sec[1]?.toString().trim()
                sqlUpdate=sqlUpdate.replaceAll("&",sec)
                sqlUpdate=sqlUpdate.replaceAll("@",it)
                sqlUpdate=sqlUpdate.replaceAll("!",col)
                updates.add(sqlUpdate)
            }
            sqlUpdate = "select setval('&',(select COALESCE(max(!)+1,1) from @))"
        }

        updates.each{
            html+= "Ejecutado:  "+it+"  ==> "
            try{
                cn.eachRow(it.toString()){r->
                    res = r[0]
                }
                html+=""+res+" <br>"
            }catch(e){
                html+="ERROR: "+e+" <br>"
            }
        }
        cn.close()
        return html

    }

    List cargarTablas(cn){
        def arreglo=[]
        def i=0
        def sql="select table_name as Tabla from information_schema.tables where table_type like '%TABLE%' and table_schema='public' order by table_name";
        cn.eachRow(sql) { d ->
            arreglo[i]=d.Tabla.toString()
            i++
        }
        return arreglo
    }

    def getSecuencia(cn,tabla){
        def sql ="select column_name as Columna, udt_name as Tipo,character_maximum_length as Tam, column_default as sec from information_schema.columns where table_name = '${tabla}' and column_default is not null order by ordinal_position limit 1;"
        def sec = []
        cn.eachRow(sql.toString()){r->
            sec.add(r["sec"])
            sec.add(r["Columna"])
        }
        return sec
    }



}
