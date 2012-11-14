package janus

class PreciosService {

    def dbConnectionService
    def getPrecioItems(fecha,lugar,items){
        def cn = dbConnectionService.getConnection()
        def itemsId =""
        def res = [:]
        items.eachWithIndex{item,i->
            itemsId+=""+item.id
            if(i< items.size()-1)
                itemsId+=","
        }
        def sql ="SELECT r1.item__id,(SELECT r2.rbpcpcun from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) from rbpc r1 where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and r1.rbpcfcha < '${fecha.format('MM-dd-yyyy')}' group by 1"
        println "sql "+sql
        cn.eachRow(sql.toString()){row->
            res.put(row[0],row[1])
        }
        cn.close()
        return res
    }

    def getPrecioItemsString(fecha,lugar,items){
        def cn = dbConnectionService.getConnection()
        def itemsId =""
        def res = ""
        items.eachWithIndex{item,i->
            itemsId+=""+item.id
            if(i< items.size()-1)
                itemsId+=","
        }
        def sql ="SELECT r1.item__id,(SELECT r2.rbpcpcun from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) from rbpc r1 where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and r1.rbpcfcha < '${fecha.format('MM-dd-yyyy')}' group by 1"
        cn.eachRow(sql.toString()){row->
            res+=""+row[0]+";"+row[1]+"&"
        }
        cn.close()
        return res
    }

    def getPrecioRubroItem(fecha,lugar,items){
        def cn = dbConnectionService.getConnection()
        def itemsId = items
        def res = []
//        items.eachWithIndex{item,i->
//            itemsId+=""+item//.id
//            if(i< items.size()-1)
//                itemsId+=","
//        }
        def sql="SELECT r1.item__id,i.itemcdgo,(SELECT r2.rbpc__id from rbpc r2 where r2.item__id=r1.item__id and r2.rbpcfcha = max(r1.rbpcfcha) and r2.lgar__id=${lugar.id}) from rbpc r1,item i where r1.item__id in (${itemsId}) and r1.lgar__id=${lugar.id} and r1.rbpcfcha < '${fecha.format('MM-dd-yyyy')}' and i.item__id=r1.item__id group by 1,2 order by 2"

//        println "sql "+sql
        cn.eachRow(sql.toString()){row->
            res.add(row[2])
        }
        cn.close()
        return res
    }

}
