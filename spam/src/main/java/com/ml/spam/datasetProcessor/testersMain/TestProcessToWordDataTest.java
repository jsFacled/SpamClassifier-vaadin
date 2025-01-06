package com.ml.spam.datasetProcessor.testersMain;

import com.ml.spam.config.FilePathsConfig;
import com.ml.spam.dictionary.models.SpamDictionary;
import com.ml.spam.dictionary.models.WordData;
import com.ml.spam.dictionary.service.SpamDictionaryService;
import com.ml.spam.datasetProcessor.MessageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TestProcessToWordDataTest {

    private static final String catWordsPath = FilePathsConfig.CATEGORIZED_WORDS_FREQUENCIES_ZERO_JSON_PATH;
    private static final String accentPairsPath = FilePathsConfig.ACCENTED_PAIRS_JSON_PATH;
    private static final String lexemePath = FilePathsConfig.LEXEMES_REPOSITORY_JSON_PATH;


    public static void main(String[] args) {

        String[] mensajeExaustivo = new String[]{
                "\n" +
                        "\n" +
                        "Banco Galicia\n" +
                        "De:\n" +
                        "hacetegalicia@mktins.galicia.ar\n" +
                        "Para:\n" +
                        "facled@yahoo.com.ar\n" +
                        "\n" +
                        "jue, 12 dic a las 1:30 p. m.\n" +
                        "\n" +
                        "Por tu seguridad, hemos deshabilitado todas las imágenes y enlaces contenido en este correo electrónico. Si crees que es seguro usarlos, marca este mensaje indicando que no es spam.Mostrar imágenes\n" +
                        "Solicitala hoy y recibila donde sea!\n" +
                        "Ver en mi navegador\n" +
                        "\n" +
                        " \n" +
                        "Banco Galicia\n" +
                        " \n" +
                        "Viví lo mejor de ser Galicia Controlá tus gastos, recibí reintegros inmediatos en tu cuenta e invertí tu plata desde la App Galicia.\n" +
                        "¡Por esto deberías pedir tus tarjetas Galicia Éminent!\n" +
                        "\n" +
                        " \n" +
                        "\t\n" +
                        "Tenés hasta 18 cuotas sin interés\n" +
                        "en electrónica, indumentaria y hasta 12 cuotas sin interés en vuelos y hotelería\n" +
                        "\n" +
                        "\t\n" +
                        "Tenés ahorros de hasta el 40%\n" +
                        "en restaurantes, supermercados, farmacias y perfumerías\n" +
                        "\n" +
                        "\t\n" +
                        "Podés pedirlas gratis\n" +
                        "y tenerlas 100% bonificadas por seis meses(1)\n" +
                        "\n" +
                        " \n" +
                        " \n" +
                        "¿Qué estás esperando para pedir las tuyas?\n" +
                        "No dejes pasar la oportunidad de ser Galicia Éminent\n" +
                        "\n" +
                        "< table=\"\"> <>\n" +
                        "Quiero mis tarjetas Éminent\n" +
                        " \n" +
                        "Facebook\tTwitter\tInstagram\t \tBanco Galicia\n" +
                        " \n" +
                        "Este mail es enviado automáticamente. Por favor, no lo respondas.\n" +
                        "Cartera de Consumo: Condiciones de Bonificación para Servicios Galicia. Sujeta a previa verificación comercial, crediticia y cumplimiento de requisitos legales. Válido en el país desde 1/04/2024 hasta el 31/12/2024 (1) Condiciones de Bonificación para Servicios Galicia. Bonificación del 100% sujeta a condiciones del convenio correspondiente y aplicable sobre los primeros seis meses de vigencia desde el alta del servicio, es decir que cada $100 el cliente se ahorra $100. A partir del séptimo mes, o en caso de que el cliente tenga una antigüedad mayor a seis meses, y en caso de cumplir los requisitos, el cliente podrá obtener una bonificación del 100% en el costo del Servicio Galicia vigente a ese momento.\n" +
                        "\n" +
                        "Para más información y condiciones o limitaciones aplicables consultá en: https://www.galicia.ar/personas/hacete-galicia Ofrecido por Banco de Galicia y Buenos Aires S.A.U. CUIT: 30-50000173-5, Tte. J.D. Perón 430 (C1038AAI), Buenos Aires, Argentina.\n" +
                        "\n" +
                        "¿Sabías que podés elegir qué mails recibir?\n" +
                        "En nuestro Centro de preferencias Galicia, podés elegir qué mails querés recibir o pedirnos que eliminemos tu contacto de nuestra base de datos para dejar de recibirlos. Administrá tus preferencias aquí. Tené en cuenta que podés volver a ingresar, las veces que necesites, al Centro de preferencias Galicia para cambiar tus intereses. También podés ejercer el derecho a que te retiremos o bloqueemos de forma total o parcial de la base de datos mediante una solicitud formal en cualquiera de nuestras sucursales.\n" +
                        "Te compartimos dos artículos importantes de la ley de protección de datos personales (N° 25326):\n" +
                        "1- Artículo 27, inc. 3ro.: “El titular podrá en cualquier momento solicitar el retiro bloqueo de su nombre de los bancos de datos a los que se refiere el presente artículo”.\n" +
                        "2- Artículo 27 - Anexo I - Decreto 1558/0: “En toda comunicación con fines de publicidad que se realice por correo, teléfono, correo electrónico, Internet u otro medio a distancia a conocer, se deberá indicar, en forma expresa y destacada, la posibilidad del titular del dato de solicitar el retiro o bloqueo, total o parcial, de su nombre de la base de datos. A pedido del interesado, se deberá informar el nombre del responsable o usuario del banco de datos que proveyó la información”.  Recordatorio de seguridad: nunca te contactaremos para validar información personal, tal como: número de documento, claves de acceso, números de la tarjeta de coordenadas o número de cuenta. Si alguien se comunica con esta intención podés llamar al 0810-444-6500, para que te podamos asesorar. La información personal que eventualmente se recopile es usada conforme a la Política de Privacidad que podrás consultar en: https://www.galicia.ar/personas/politica-de-privacidad\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "En virtud de lo establecido por la disposición de Protección de Datos Personales usted tiene derecho a solicitar al emisor de este mensaje la rectificación, actualización, inclusión o supresión de los datos personales incluidos en su base de contactos, listas o cadenas de mensajes en los cuales usted se encuentre. Conozca más\n" +
                        "\n" +
                        "Quiero desuscribirme | Términos y Condiciones\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "\n",
                "spam"
        };
        String[] mensajeCombinado = new String[]{
                "toto@gmail.com chacha@ Comprá abc123$ http://test.com 35kg promo@2024 😊 cuándo niño$ 2999 http://promo123.net 24hs $10off! ",
                "spam"
        };

        String[] mensaje = new String[]{
                "35kg $100 24hs 5000pesos",
                "spam"
        };
        String[] mensajeMixto = new String[]{
                "http://promo123.com abc123$ 50kg! promo@2024 23@abc# $10off!",
                "spam"
        };
        String[] mensajeMixto2 = new String[]{
                "123@xyz# $45sale! 😊 http://test.com abc123$ 50kg! promo@2024",

                "spam"
        };
        String[] mensajeTextSymbol = new String[]{
                "\"@ño niño$ piñ@ta sueño! casa123 año2024 día1\"\n",
                "spam"
        };
        String[] mensajeAccent = new String[]{
                "cuándo cuando cacatúa había servidor",
                "spam"
        };

        String[] mensajeNumText = new String[]{
                "5m2 8meses, 48litros pesos25000",
                "spam"
        };// 10km, 48litros pesos25000"
        String[] mEmojis = new String[]{
                "❤️ \uD83D\uDD25\uD83D\uDD25 +++ ❤\uFE0F\uD83C\uDF89 == \uD83D\uDE0A\uD83D\uDC8E !!! ??? $100 !! @@@ ***\n",
                "spam"
        };

        String[] m = new String[]{
                "hola CHAU 123despuesdelnumero antesdelnumero456",
                "spam"
        };

        try {
            // Inicializar el Service
            SpamDictionaryService dictionaryService = new SpamDictionaryService();
            System.out.println("=== Inicializando Diccionarios Completos ===");

            // Inicializar diccionarios desde JSON
            dictionaryService.initializeDictionaryFromJsonIfContainOnlyZeroFrequencies(catWordsPath, accentPairsPath, lexemePath);
            System.out.println("=== Diccionarios Inicializados Correctamente ===");

            // Simulación de datos crudos (rawRows) en formato [mensaje, etiqueta]
            List<String[]> rawRows = new ArrayList<>();

            rawRows.add(
                    m
                    //mensajeCombinado
                    // mensajeExaustivo
            );


            //Mostrar lexemerepository en dictionary
            // dictionaryService.displayLexemeRepository();

            // Mostrar datos iniciales para referencia
            System.out.println("//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
            System.out.println("\n[INFO Inicio de lextura de Mensajes ] * * * Datos crudos iniciales:");
            for (String[] row : rawRows) {
                System.out.println("[Mensaje: " + row[0] + ", Etiqueta: " + row[1] + "]");
            }

            System.out.println("\n[INFO] Iniciando procesamiento de datos...");

            // Procesar las filas crudas con acceso a los diccionarios
            List<List<WordData>> processedData = MessageProcessor.processToWordData(
                    rawRows,
                    dictionaryService.getAccentPairs(),
                    dictionaryService.getLexemesRepository()
            );

            // Mostrar los resultados procesados
            System.out.println("\n[INFO] Resultados del procesamiento:");
            for (int i = 0; i < processedData.size(); i++) {
                System.out.println("Nuevo mensaje procesado (" + (i + 1) + "):");
                System.out.println(Arrays.toString(rawRows.get(0)));
                for (WordData wordData : processedData.get(i)) {
                    System.out.println(" - " + wordData);
                }
                System.out.println("--------------------");
            }

            // Mostrar una vista general de todos los datos procesados
            System.out.println("\n[INFO] Resumen de datos procesados:");
            System.out.println("Total de mensajes procesados: " + processedData.size());


            // displayAccentPairs(dictionaryService.getAccentPairs());
        } catch (IllegalArgumentException e) {
            System.err.println("[ERROR] Datos de entrada inválidos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[ERROR] Error inesperado durante el procesamiento: " + e.getMessage());
            e.printStackTrace();
        }


        System.out.println("\n[INFO] Fin del procesamiento de datos.");

    }

    private static void displayAccentPairs(Map<String, SpamDictionary.Pair> accentPairs) {
        System.out.println("\n[DEBUG] Contenido de accentPairs:");
        if (accentPairs == null || accentPairs.isEmpty()) {
            System.out.println("El mapa de accentPairs está vacío o no ha sido inicializado.");
        } else {
            for (Map.Entry<String, SpamDictionary.Pair> entry : accentPairs.entrySet()) {
                System.out.println(" - Palabra con acento: " + entry.getKey() + ", Sin acento: "
                        + entry.getValue().nonAccented() + ", Categoría: " + entry.getValue().category());
            }
        }
    }
}
