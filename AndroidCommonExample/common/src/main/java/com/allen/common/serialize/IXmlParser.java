package com.allen.common.serialize;

import java.io.InputStream;

/**
 * Created by hHui on 2016/5/24.
 */
public interface IXmlParser {


    String parse(String content);

    String parse(InputStream content);
}
