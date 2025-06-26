import sentencepiece as spm

spm.SentencePieceTrainer.Train(
    '--input=spam/src/main/resources/static/datasets/separateMessagesAndLabels/full_joined_normalized_noduplicates_NoLabel.txt --model_prefix=messages_spamham_tokenizer --vocab_size=4000 --max_sentence_length=10000'
)
