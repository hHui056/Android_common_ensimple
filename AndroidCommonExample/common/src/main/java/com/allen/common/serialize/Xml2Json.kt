package com.allen.common.serialize

import java.io.FileInputStream
import java.io.InputStream

/**
 * Created by hHui on 2016/5/20.
 */
object Xml2Json {

    enum class ParserType {
        PullParser, SaxParser
    }

    /**
     * 把xml字符串转换成json字符串。
     * @param xml        xml字符串
     * @param parserType xml解释器，[ParserType.PullParser]或者[ParserType.SaxParser]
     * @return json字符串
     */
    @JvmStatic @JvmOverloads fun convert(xml: String, parserType: ParserType = ParserType.SaxParser): String {
        return when (parserType) {
            ParserType.PullParser -> PullParser().parse(xml)
            ParserType.SaxParser -> SaxParser().parse(xml)
        }
    }

    /**
     * 把xml字符串转换成json字符串。
     * @param xmlInputStream xml数据源
     * @param parserType     xml解释器，[ParserType.PullParser]或者[ParserType.SaxParser]
     * @return json字符串
     */
    @JvmStatic @JvmOverloads fun convertFile(xmlInputStream: InputStream, parserType: ParserType = ParserType.SaxParser): String {
        return when (parserType) {
            ParserType.PullParser -> PullParser().parse(xmlInputStream)
            ParserType.SaxParser -> SaxParser().parse(xmlInputStream)
        }
    }

    /**
     * 把xml字符串转换成json字符串。
     * @param xmlFilePath    xml文件路径
     * @param parserType     xml解释器，[ParserType.PullParser]或者[ParserType.SaxParser]
     * @return json字符串
     */
    @JvmStatic @JvmOverloads fun convertFile(xmlFilePath: String, parserType: ParserType = ParserType.SaxParser): String {
        return convertFile(FileInputStream(xmlFilePath), parserType)
    }

}
