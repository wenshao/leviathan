package com.alibaba.study.message;

import java.nio.charset.Charset;

public interface TLVConstants {

    public final static Charset UTF8              = Charset.forName("UTF-8");

    public static final int     TAG_PREFIX_LENGTH = 6;

    public static final short   RAW_BYTES         = 0;
    public static final short   STRING_ASCII      = 1;
    public static final short   STRING_UTF8       = 2;
    public static final short   JSON_UTF8         = 3;
}
