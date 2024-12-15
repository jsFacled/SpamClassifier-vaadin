package com.ml.spam.undefined;


import java.util.List;

//Se utiliza solamente para realizar pruebas y prácticas.
public class TestAndPractice {

    public static void main(String[] args) {

        String accentList ="""

                (cómo, como, stop_word),
        (qué, que, stop_word),
        (té, te, stop_word)
        (dejó, dejo, undefined),
        
""";
String wordIn = "Cómo";
//tokenizar
        //Buscar en Dictionary:
            //True: ++freq
            //False: Buscar en accentList:
                //True: [ A definir ??]
                //False: Asignar a undefined




    }//END main
}//END TestAndPractice
