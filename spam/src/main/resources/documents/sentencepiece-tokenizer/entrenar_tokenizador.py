import sentencepiece as spm

spm.SentencePieceTrainer.Train(
    '--input=corpus_spam_normalized.txt --model_prefix=spam_tokenizer --vocab_size=4000'
)