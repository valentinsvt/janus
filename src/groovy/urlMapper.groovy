class Arbol{
    def url = ""
    def links = []

    public Arbol(url){
        this.url=url
    }


    def imprimir(num){
        def  espacio =""
        num.times{
            espacio+="\t"
        }
        println espacio+url
        links.each{
            //println " --> "+it.url
            it.imprimir(num+1)
        }
    }

}




def urlMapper(nodo,inicio){
    println "iniciando . . . "+nodo.url
    try {
        def raiz = nodo
        new URL(raiz.url).eachLine{
            def parts = it.split("<a")
            if(parts.size()>1){
                parts.each{p->
                    if(p=~"href"){
                        def parts2 = p.split(" ")
                        parts2.each{p2->
                            if(p2=~"href"){
                                p2=p2.trim()
                                def p3
                                if(p2.charAt(5)=="'"){
                                    p3=p2.split("'")
                                    // println "comilla simple "+p3
                                }
                                if(p2.charAt(5)=='"'){
                                    p3=p2.split('"')
                                    // println "comilla doble "+p3
                                }
                                if(p3.size()>1){
                                    if(p3[1].findAll("http:").size()<1){
                                        p3[1]=raiz.url+p3[1]
                                    }
                                    if(verificar(inicio,p3[1])){
                                        raiz.links.add(new Arbol(p3[1]))
                                    }
                                }
                                //println "es link "+p2
                            }
                        }
                    }

                }
            }
        }


    } catch (MalformedURLException ex) {
        println ex.message
    }
}

boolean verificar(nodo,url){

    if(nodo.url==url)
        return false
    nodo.links.each{
        if(!verificar(it,url))
            return false
    }
    return true

}

def inicio = new Arbol('http://www.tedein.com.ec')
urlMapper(inicio,inicio)
if(inicio.links.size()>0){
    inicio.links.each {
        urlMapper(it,inicio)
    }
}

inicio.imprimir(1)







// titleBytes example