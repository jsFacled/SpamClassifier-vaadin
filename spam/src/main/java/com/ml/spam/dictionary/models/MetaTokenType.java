package com.ml.spam.dictionary.models;

public enum MetaTokenType {
    META_WEBADDRESS("meta_webaddress"),
    META_WEBEMAIL("meta_webemail"),
    META_WEBIMAGE("meta_webimage"),
    META_DOCUMENTFILE("meta_documentfile"),
    META_NUMBERSTRING("meta_numberstring"),
    META_ENCODEDDATA("meta_encodeddata"),
    META_UNKNOWNEMOJIS("meta_unknownemojis");

    private final String word;

    MetaTokenType(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
