package com.allen.common.serialize;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


/**
 * Created by hHui on 2016/5/24.
 */
class SaxParser implements IXmlParser {

    SAXParser parser = null;

    SaxParser() {
        try {
            parser = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String parse(String content) {
        if (content == null || content.length() == 0) {
            return null;
        }
        return parse(toInputStream(content));
    }

    @Override
    public String parse(InputStream is) {
        Result result = new Result();
        try {
            parser.parse(is, new SaxDefaultHandler(result));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.json;
    }

    private InputStream toInputStream(String content) {
        ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
        return bais;
    }

    private class SaxDefaultHandler extends DefaultHandler {
        private Result mResult;

        private Node mRootNode;
        private Stack<Node> mNodeStack;
        private StringBuilder mCurText = new StringBuilder();

        SaxDefaultHandler(Result result) {
            mResult = result;
        }

        @Override
        public void startDocument() throws SAXException {
            mNodeStack = new Stack<Node>();
        }

        @Override
        public void endDocument() throws SAXException {
            mResult.json = mRootNode.toJsonString(false, true, true);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String name = (localName == null || localName.length() == 0) ? qName : localName;
            Node node = new Node(name);
            for (int i = 0; i < attributes.getLength(); i++) {
                String key = attributes.getLocalName(i);
                if (key == null || key.length() == 0) {
                    key = attributes.getQName(i);
                }
                String value = attributes.getValue(i);
                node.addProperty(key, value);
            }
            mNodeStack.push(node);

            mCurText.delete(0, mCurText.length());
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String temp = new String(ch, start, length);
            if (!Pattern.matches("[\\s]*?", temp)) { //非空字符，\t,\r,\n,\f,space
                mCurText.append(temp);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            //栈顶node就是当前正在解析的node
            Node node = mNodeStack.pop();
            node.setText(mCurText.toString());

            //绑定node父子关系
            if (mNodeStack.empty()) {
                mRootNode = node;
            } else {
                mNodeStack.peek().addChild(node);
            }
            mCurText.delete(0, mCurText.length());
        }
    }

    private class Result {
        String json;
        String error;
    }
}
