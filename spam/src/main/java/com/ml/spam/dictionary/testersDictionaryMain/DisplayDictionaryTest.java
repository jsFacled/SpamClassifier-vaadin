package com.ml.spam.dictionary.testersDictionaryMain;

import com.ml.spam.dictionary.service.SpamDictionaryService;

public class DisplayDictionaryTest {

    public static void main(String[] args) {
        SpamDictionaryService service = new SpamDictionaryService();

        System.out.println("===  /  /   /   /   /   /   /   /   /   /   ===  Etapa 2: Actualización del Diccionario  === /  /   /   /   /   /   /   /   /   /   === \n");

        //Mostrar el Map de Dictionary para chequear que estén las categorías vacías
        service.displayCategorizedWordsInDictionary();



    }
}
