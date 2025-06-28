import sentencepiece as spm

# Cargar modelo
sp = spm.SentencePieceProcessor()
sp.load("messages_spamham_tokenizer.model")

# Leer mensajes
with open("spam/src/main/resources/static/datasets/separateMessagesAndLabels/full_joined_normalized_noduplicates_NoLabel.txt", "r", encoding="utf-8") as f_in, \
     open("messages_spamham_numtokens.txt", "w", encoding="utf-8") as f_out:
    for linea in f_in:
        tokens = sp.encode(linea.strip(), out_type=int)
        f_out.write(" ".join(map(str, tokens)) + "\n")
