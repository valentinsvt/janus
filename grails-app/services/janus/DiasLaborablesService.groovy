package janus

class DiasLaborablesService {

    /**
     * diasLaborablesEntre()
     *      retorna el numero de dias laborables entre 2 fechas
     * @param fecha1 la una fecha
     * @param fecha2 la otra fecha
     * @param noLaborables true: si una de las fechas es no laborable pasa al primer dia laborable futuro.
     *                      false: si una de las fechas es no laborable retorna false
     *                      Por default pasa true
     * @return array        en posicion 0: boolean true:  hizo el calculo correctamente
     *                                             false: hubo un error
     *                                  1: el numero de dias cuando el calculo fue correcto
     *                                     el error si hubo error
     *                                  2: si hubo algun mensaje aunque haya hecho el calculo
     *                                     si hubo error (no hay los dias laborables), el año para configurar los días laborables
     */
    def diasLaborablesEntre(Date fecha1, Date fecha2, boolean noLaborables) {
//        println "****"
//        println fecha1
//        println fecha2
//        println noLaborables
//        println "****"
        def mensaje = ""
        def dl1 = DiaLaborable.findAllByFecha(fecha1)
        if (dl1.size() == 1) {
            dl1 = dl1[0]
            def ord1 = dl1.ordinal
            if (ord1 == 0) {
                if (noLaborables) {
                    def nuevaFecha1 = fecha1
                    while (ord1 == 0) {
                        println "while1"
                        nuevaFecha1++
                        dl1 = DiaLaborable.findByFecha(nuevaFecha1)
                        ord1 = dl1.ordinal
                    }
                    mensaje += "<li>La fecha " + fecha1.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha1.format("dd-MM-yyyy") + "</li>"
                    println mensaje
                } else {
                    return [false, "La fecha " + fecha1.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                    return false
                }
            }
            def dl2 = DiaLaborable.findAllByFecha(fecha2)
            if (dl2.size() == 1) {
                dl2 = dl2[0]
                def ord2 = dl2.ordinal
                if (ord2 == 0) {
                    if (noLaborables) {
                        def nuevaFecha2 = fecha2
                        while (ord2 == 0) {
                            println "while2"
                            nuevaFecha2++
                            dl2 = DiaLaborable.findByFecha(nuevaFecha2)
                            ord2 = dl2.ordinal
                        }
                        mensaje += "<li>La fecha " + fecha2.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha2.format("dd-MM-yyyy") + "</li>"
                        println mensaje
                    } else {
                        return [false, "La fecha " + fecha2.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                        return false
                    }
                }
                return [true, Math.abs(ord2 - ord1), mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
            } else if (dl2.size() == 0) {
                return [false, "No se encontró el registro de días laborables para la fecha " + fecha2.format("dd-MM-yyyy"), fecha2.format("yyyy")]
//                return false
            } else {
                return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha2.format("dd-MM-yyyy"), fecha2.format("yyyy")]
//                return false
            }
        } else if (dl1.size() == 0) {
            return [false, "No se encontró el registro de días laborables para la fecha " + fecha1.format("dd-MM-yyyy"), fecha1.format("yyyy")]
//            return false
        } else {
            return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha1.format("dd-MM-yyyy"), fecha1.format("yyyy")]
//            return false
        }
    }

    def diasLaborablesEntre(Date fecha1, Date fecha2) {
        return diasLaborablesEntre(fecha1, fecha2, true)
    }

    /**
     * diasLaborablesDesde
     *      retorna la fecha n dias laborables despues de una fecha
     * @param fecha la fecha
     * @param dias el numero de dias
     * @param noLaborables true: si la fecha es no laborable pasa al primer dia laborable futuro.
     *                      false: si la fecha es no laborable retorna false
     *                      Por default pasa true
     * @return array        en posicion 0: boolean true:  hizo el calculo correctamente
     *                                             false: hubo un error
     *                                  1: la fecha (Date) cuando el calculo fue correcto
     *                                     el error si hubo error
     *                                  2: si hizo el calculo, la fecha en string con format dd-MM-yyyy
     *                                     si hubo error (no hay los dias laborables), el año para configurar los días laborables
     *                                  3: si hubo algun mensaje aunque haya hecho el calculo
     */
    def diasLaborablesDesde(Date fecha, int dias, boolean noLaborables) {
        def mensaje = ""
        def dl = DiaLaborable.findAllByFecha(fecha)
        if (dl.size() == 1) {
            dl = dl[0]
            def ord = dl.ordinal
            if (ord == 0) {
                if (noLaborables) {
                    def nuevaFecha = fecha
                    while (ord == 0) {
                        nuevaFecha++
                        dl = DiaLaborable.findByFecha(nuevaFecha)
                        ord = dl.ordinal
                    }
                    mensaje += "<li>La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Se utilizó " + nuevaFecha.format("dd-MM-yyyy") + "</li>"
//                    println mensaje
                } else {
                    return [false, "La fecha " + fecha.format("dd-MM-yyyy") + " no es un día laborable. Para calcular con el siguiente dia laborable pasar true como 3r parametro"]
//                    return false
                }
            }
            def nuevoOrd = ord + dias

            def anioFecha = fecha.format("yyyy").toInteger()
            def c = DiaLaborable.createCriteria()
            def diaMaxAnio = c.get {
                eq("anio", anioFecha)
                projections {
                    max "ordinal"
                }
            }
//            println "0 anio: " + anioFecha
//            println "0 ordinal nuevo dia: " + nuevoOrd
//            println "0 dias max anio: " + diaMaxAnio
//            println "0 ord fecha inicio: " + ord
            if (nuevoOrd <= diaMaxAnio) {
                def nuevoDia = DiaLaborable.withCriteria {
                    eq("anio", anioFecha)
                    eq("ordinal", nuevoOrd)
                }
//                println ">>>>>>>>>>>>>" + nuevoDia
                if (nuevoDia.size() == 1) {
                    return [true, nuevoDia[0].fecha, nuevoDia[0].fecha.format("dd-MM-yyyy"), mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
                } else if (nuevoDia.size() == 0) {
                    return [false, "No se encontró el registro del día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                } else {
                    return [false, "Se encontraron ${nuevoDia.size()} registros para día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                }
            } else {
                def anioAct = anioFecha + 1
                def diasRestantesAnio = diaMaxAnio - ord
                def ordAct = nuevoOrd - diasRestantesAnio
                c = DiaLaborable.createCriteria()
                def nuevoDiaMax = c.get {
                    eq("anio", anioAct)
                    projections {
                        max "ordinal"
                    }
                }
                def cont = ordAct > nuevoDiaMax

//                println "1 anio: " + anioAct
//                println "1 ordinal nuevo dia: " + ordAct
//                println "1 dias max anio: " + nuevoDiaMax
//                println "1 dias rest anio: " + diasRestantesAnio
//                println "1 continua? " + cont

                while (cont) {
                    if (nuevoDiaMax) {
                        anioAct++
                        ordAct = ordAct - nuevoDiaMax
                        c = DiaLaborable.createCriteria()
                        nuevoDiaMax = c.get {
                            eq("anio", anioAct)
                            projections {
                                max "ordinal"
                            }
                        }
                        cont = ordAct > nuevoDiaMax
//                        println "\tanio: " + anioAct
//                        println "\tordinal nuevo dia: " + ordAct
//                        println "\tdias max anio: " + nuevoDiaMax
//                        println "\tcontinua? " + cont
                    } else {
                        return [false, "No se encontraron registros para días laborables del año ${anioAct}", anioAct]
                    }
                }

                def nuevoDia = DiaLaborable.withCriteria {
                    eq("anio", anioAct)
                    eq("ordinal", ordAct)
                }
//                println ">>>>>>>>>>>>>" + nuevoDia
                if (nuevoDia.size() == 1) {
                    return [true, nuevoDia[0].fecha, nuevoDia[0].fecha.format("dd-MM-yyyy"), mensaje != "" ? "<ul>" + mensaje + "</ul>" : ""]
                } else if (nuevoDia.size() == 0) {
                    return [false, "No se encontró el registro del día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                } else {
                    return [false, "Se encontraron ${nuevoDia.size()} registros para día laborable n. ${nuevoOrd} del año ${anioFecha}", anioFecha]
                }

//                println "OK anio: " + anioAct
//                println "OK ordinal nuevo dia: " + ordAct
//                println "OK dias max anio:" + nuevoDiaMax

//                println "AQUI"
            }

        } else if (dl.size() == 0) {
            return [false, "No se encontró el registro de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
//            return false
        } else {
            return [false, "Se encontraron varios registros de días laborables para la fecha " + fecha.format("dd-MM-yyyy"), fecha.format("yyyy")]
//            return false
        }
    }

    def diasLaborablesDesde(Date fecha, int dias) {
        return diasLaborablesDesde(fecha, dias, true)
    }
}
