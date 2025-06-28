# tokenizar_mensaje_individual.py
import sys
import sentencepiece as spm

# Leer argumentos
model_path = sys.argv[1]
mensaje = sys.argv[2]

# Tokenizar
sp = spm.SentencePieceProcessor()
sp.load(model_path)
tokens = sp.encode(mensaje.strip(), out_type=int)

# Imprimir tokens como CSV
print(','.join(map(str, tokens)))
