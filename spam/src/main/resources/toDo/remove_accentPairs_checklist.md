# ✅ Checklist para eliminar completamente la lógica de accentPairs

## 🔥 Archivos a eliminar
- [x] Eliminar archivo: `src/main/resources/static/dictionary/accented_pairs.json`

## 🧼 Modificaciones en código

### 📄 SpamDictionaryService.java
- [x] Eliminar llamada a `initializeAccentPairs(...)`
- [x] Eliminar método `initializeAccentPairs(String path)`
- [x] Eliminar referencia a `accentPairsFilePath`

### 📄 SpamDictionary.java
- [x] Eliminar campo `private Map<String, Pair> accentPairs`
- [x] Eliminar método `initializeAccentPairs(Map<String, Pair>)`
- [x] Eliminar método `getAccentPairs()`

### 📄 MessageProcessor.java
- [x] Eliminar campo `private static Map<String, Pair> accentPairs`
- [x] Eliminar línea `MessageProcessor.accentPairs = ...`
- [x] Eliminar método `isInAccentPairs(String token)`
- [x] Eliminar cualquier `if (isInAccentPairs(...))` o similar


## 📝 Documentación
- [ ] Actualizar README o documentación técnica eliminando referencias a `accentPairs`
- [ ] Confirmar que `Etapa 2.v1` y otros documentos ya reflejan este cambio

## ✅ Verificación Final
- [ ] Correr el proyecto sin errores de compilación
- [ ] Verificar que el flujo usa solo `structured_lexemes_repository.json`