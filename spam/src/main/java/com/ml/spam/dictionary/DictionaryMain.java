package com.ml.spam.dictionary;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Desde aquì se generará el diccionario principal
 *
 * Rol:
 *      Punto de entrada para inicializar, probar y verificar el diccionario.
 *
 * Responsabilidades:
 *      Instanciar y coordinar el uso de SpamDictionary y SpamDictionaryService.
 *      Probar inicialización y enriquecimiento.
 *      Visualizar el contenido del diccionario.
 *
 */

public class DictionaryMain {
    public static void main(String[] args) {
        System.out.println("Generando el diccionario de spam...");

        // Instanciar el diccionario y el servicio
            /// Se pasa el Singleton SpamDictionary al servicio para que pueda operar sobre
            /// la misma instancia global del diccionario, centralizando los datos y
            /// separando responsabilidades entre almacenamiento y lógica de negocio.
        SpamDictionary dictionary = SpamDictionary.getInstance();
        SpamDictionaryService service = new SpamDictionaryService(dictionary);

        // Opción 1: Inicializar desde una lista de palabras
        Set<String> initialWords = Set.of(
                "compromiso", "exclusiva", "urgencia", "deuda", "tarifas", "trampa", "ganar", "beneficios", "solicitud", "prohibido", "accesible", "única", "segura", "regalo", "milagro", "precio", "satisfacción", "salvar", "vida", "empieza", "contacto", "especial", "hazte", "mejora", "sin esfuerzo", "orgánico", "invierte", "revelación", "billón", "sorpresa", "ahora", "reembolso", "stock", "pérdida", "prosperidad", "premio", "actuación", "lanzamiento", "promocional", "vip", "prima", "negocio", "simple", "alerta", "oportunidad", "privacidad", "actuar", "descargar", "garantía", "ahorra", "efectivo", "estimado", "aprovecha", "actualización", "sexy", "prueba gratuita", "empleo", "lujo", "felicitaciones", "casino", "confirmación", "limitado", "ganador", "rolex", "cheque", "intereses", "solicita", "irresistible", "probar", "venta", "correo", "búsqueda", "amplíe", "notificación", "comercio", "rueda", "pierda", "clic", "estado", "ahorrar", "personalizado", "gastos", "visite", "sorteo", "compañero", "existencias", "diazepam", "marca", "instantáneo", "fabuloso", "viagra", "celebridad", "compite", "comparar", "relámpago", "enlace", "instrucciones", "suscríbete", "contraseña", "activación", "autenticación", "transferencias", "texas", "membresía", "emergencia", "descuento", "cancelación", "amor", "liquidación", "video", "actúe", "ganga", "información", "multiplicar", "renovable", "riesgo", "ganancias", "resultados", "compra", "ahorro", "blackjack", "transferencia", "vacaciones", "formulario", "seleccionado", "cierre", "ventas", "personalizada", "sin", "acción", "clasificación", "barato", "consulta", "vista", "cura", "rendimiento", "miembro", "secreto", "registrarse", "suministros", "sexo", "bonificación", "seguridad", "interés", "potencial", "marketing", "lotería", "$$$", "miles", "oferta especial", "amigo", "increíble", "100%", "flash", "garantizada", "mejore", "spam", "bancarrota", "bono", "ingreso", "asombroso", "sin costo", "reducido", "diagnósticos", "pago", "reembolsado", "vende", "cliente", "llamada", "reembolsable", "colección", "visa", "confidencial", "acceso", "arrugas", "asequible", "primeros", "motores", "caduca", "devolución", "ingresos", "tiempo", "crédito", "adquirir", "dominio", "premios", "reserva", "comprador", "válida", "facturación", "dieta", "rebaja", "garantizado", "poderoso", "aumente", "autorización", "multiplicación", "click", "publicidad", "recompensa", "retiro", "exclusivo", "factura", "rapidez", "oferta", "suscriptores", "libertad", "escaso", "limitada", "millonario", "dinero", "muestra", "último", "verificación", "millón", "inversión", "rebajas", "rápido", "suscripción", "tráfico", "levita", "aceptación", "éxito", "fácil", "expira", "protección", "pronto", "aprobado", "términos", "haz", "asistencia", "seguro", "único", "renovación", "eliminar", "prioridad", "testimonios", "gana", "urgente", "natural", "introducción", "orden", "comprar", "atención", "obligación", "préstamo", "revolucionario", "lotes", "participar", "medicina", "millones", "inmediato", "bancario", "gratis", "instalación", "calificado", "finanzas", "anuncio", "encuentros", "irrepetible", "transfiere", "sistema", "solución", "legal", "certificado", "medicamento", "tarjeta", "gratuito", "jefe", "agotado", "enriquezca", "cupón", "prueba", "aviso", "banco", "hipoteca", "refinanciar", "cantidad", "ganancia", "letra pequeña", "final", "mágico", "donación", "enriquecerse", "promoción", "fortuna"
        );

        Set<String> rareSymbols = Set.of("!", "$", "%", "&", "*", "#", "_", "@", "?", "~", "^", "+", "=", "|", ":", ";", "/", "\"", "'", "<", ">", "(", ")"
        );
        Set<String> stopWords = Set.of(
                "a", "al", "algo", "algunos", "algunas", "así", "aunque", "con", "cómo", "cuándo", "dónde",
                "de", "del", "desde", "el", "él", "ella", "ellas", "ellos", "en", "entre", "esa", "esas",
                "ese", "esos", "esta", "estas", "este", "estos", "fue", "fui", "fuera", "fueron", "ha",
                "había", "habían", "hasta", "hay", "he", "hemos", "la", "las", "le", "les", "lo", "los",
                "más", "me", "mi", "mí", "mis", "muy", "ni", "no", "nos", "nosotros", "o", "para", "pero",
                "por", "porque", "qué", "que", "quien", "quienes", "se", "será", "sí", "sin", "sobre",
                "sólo", "su", "sus", "también", "tan", "tanto", "te", "ti", "tiene", "tienen", "todo",
                "todos", "tu", "tus", "tú", "un", "una", "unas", "uno", "unos", "ya", "yo"
        );


        // Inicializar las palabras, símbolos raros y stopwords
        service.initializeItems(initialWords);
        service.initializeItems);
        service.initializeItems();

        // Inicializa el diccionario con las palabras de la lista
        //service.initializeFromList(Set.copyOf(initialWords));


        // Imprime el contenido del diccionario para verificar
        service.displayDictionary();

        System.out.println("Diccionario generado exitosamente.");
    }
}
