import sentencepiece as spm

# Cargar modelo
sp = spm.SentencePieceProcessor()
sp.load("spam_tokenizer.model")

# Leer mensajes
with open("corpus_spam_normalized.txt", "r", encoding="utf-8") as f_in, \
     open("tokens_spam.txt", "w", encoding="utf-8") as f_out:
    for linea in f_in:
        tokens = sp.encode(linea.strip(), out_type=int)
        f_out.write(" ".join(map(str, tokens)) + "\n")
