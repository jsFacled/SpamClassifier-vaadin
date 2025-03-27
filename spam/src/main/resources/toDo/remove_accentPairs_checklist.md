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
- [ ] Eliminar campo `private static Map<String, Pair> accentPairs`
- [ ] Eliminar línea `MessageProcessor.accentPairs = ...`
- [ ] Eliminar método `isInAccentPairs(String token)`
- [ ] Eliminar cualquier `if (isInAccentPairs(...))` o similar

### 📄 ResourcesHandler.java
- [ ] Eliminar método `loadAccentPairs(...)` si existiera
- [ ] Eliminar uso de `accentPairs` en métodos de carga de JSON

### 📄 JsonUtils.java
- [ ] Eliminar validaciones relacionadas con accentPairs (si las hay)

## 📝 Documentación
- [ ] Actualizar README o documentación técnica eliminando referencias a `accentPairs`
- [ ] Confirmar que `Etapa 2.v1` y otros documentos ya reflejan este cambio

## ✅ Verificación Final
- [ ] Correr el proyecto sin errores de compilación
- [ ] Verificar que el flujo usa solo `structured_lexemes_repository.json`