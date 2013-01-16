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

        return html



    }



}
