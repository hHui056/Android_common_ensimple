package com.allen.common.serialize;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;


/**
 * Created by hHui on 2016/5/24.
 */
class PullParser implements IXmlParser {

    private Node mRootNode;
    private Stack<Node> mNodeStack;
    private StringBuilder mCurText = new StringBuilder();


    PullParser() {

    }

    @Override
    public String parse(String content) {
        if (content == null) {
            return null;
        }
        return this.parse(new ByteArrayInputStream(content.getBytes()));
    }

    @Override
    public String parse(InputStream content) {
        try {
            XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
            pullParser.setInput(content, "utf-8");

            int eventType = pullParser.getEventType();
            while (true) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT: {
                        mNodeStack = new Stack<Node>();
                        break;
                    }
                    case XmlPullParser.END_DOCUMENT: {
                        String json = mRootNode.toJsonString(false, true, true);
                        return json;
                    }
                    case XmlPullParser.START_TAG: {
                        String name = pullParser.getName();
                        Node node = new Node(name);
                        for (int i = 0; i < pullParser.getAttributeCount(); i++) {
                            String key = pullParser.getAttributeName(i);
                            String value = pullParser.getAttributeValue(i);
                            node.addProperty(key, value);
                        }
                        mNodeStack.push(node);

                        mCurText.delete(0, mCurText.length());
                        break;
                    }
                    case XmlPullParser.END_TAG: {
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
                        break;
                    }
                    case XmlPullParser.TEXT: {
                        if (!pullParser.isWhitespace()) {
                            mCurText.append(pullParser.getText());
                        }
                        break;
                    }

                }
                eventType = pullParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
