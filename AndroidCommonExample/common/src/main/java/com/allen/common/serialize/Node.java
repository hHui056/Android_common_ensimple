package com.allen.common.serialize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hHui on 2016/5/24.
 */
class Node {
    private String name;
    private Map<String, String> attrs = new LinkedHashMap<String, String>();
    private String text;
    private List<Node> children = new ArrayList<Node>();

    public Node(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addProperty(String key, String value) {
        if (key == null || key.length() == 0 || value == null) {
            return;
        }
        attrs.put(key, value);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addChild(Node child) {
        if (child != null) {
            children.add(child);
        }
    }

    /**
     * 转换成json字符串。
     *
     * @param readable true,格式化为易读形式；false，单行。
     */
    public String toJsonString(boolean readable, boolean isRootNode, boolean hasName) {
        StringBuilder json = new StringBuilder();
        if (isRootNode) {
            json.append(Symbols.ObjectBegin);
            json.append(toJsonObject(hasName));
            json.append(Symbols.ObjectEnd);
        } else {
            json.append(toJsonObject(hasName));
        }
        return json.toString();
    }

    /**
     * 不带外层的括号。
     *
     * @param hasName true,添加node名称，用于json对象；false，不添加node名称，用于json数组。
     * @return
     */
    private String toJsonObject(boolean hasName) {
        StringBuilder json = new StringBuilder();

        if (hasName) {
            json.append(wrapQuotation(this.name));
            json.append(Symbols.colon);
        }

        json.append(Symbols.ObjectBegin);

        writeAttrs2Json(json);

        writeText2Json(json);

        //识别子node应该是对象还是数组
        Map<String, List<Node>> childCategoryMap = new LinkedHashMap<String, List<Node>>();
        for (int i = 0; i < children.size(); i++) {
            Node temp = children.get(i);
            if (childCategoryMap.containsKey(temp.name)) {
                childCategoryMap.get(temp.name).add(temp);
            } else {
                List<Node> nodeList = new ArrayList<Node>();
                nodeList.add(temp);
                childCategoryMap.put(temp.name, nodeList);
            }
        }

        Set<String> childCategoryKeySet = childCategoryMap.keySet();
        Iterator<String> iterator1 = childCategoryKeySet.iterator();
        while (iterator1.hasNext()) {
            String key = iterator1.next();
            List<Node> childList = childCategoryMap.get(key);
            if (childList.size() > 1) { //数组
                json.append(wrapQuotation(childList.get(0).name));
                json.append(Symbols.colon);
                json.append(Symbols.ArrayBegin);
                for (int j = 0; j < childList.size(); j++) {
                    Node node = childList.get(j);
                    json.append(node.toJsonString(false, false, false));
                    if (j != childList.size() - 1) {
                        json.append(Symbols.comma);
                    }
                }
                json.append(Symbols.ArrayEnd);
                json.append(Symbols.comma);
            } else {//对象
                Node node = childList.get(0);
                json.append(node.toJsonString(false, false, true));
                json.append(Symbols.comma);
            }
        }

        //移除最后一个逗号
        char lastCh = json.charAt(json.length() - 1);
        if (lastCh == Symbols.comma) {
            json.deleteCharAt(json.length() - 1);
        }

        json.append(Symbols.ObjectEnd);

        return json.toString();
    }

    private void writeAttrs2Json(StringBuilder json) {
        Set<String> keySet = attrs.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = attrs.get(key);
            json.append(wrapQuotation(key)).append(Symbols.colon).append(wrapQuotation(value));
            json.append(Symbols.comma);
        }
    }

    /**
     * 文本用node名称作为key。
     */
    private void writeText2Json(StringBuilder json) {
        if (this.text != null && this.text.length() != 0) {
            json.append(wrapQuotation(this.name)).append(Symbols.colon).append(wrapQuotation(this.text));
            json.append(Symbols.comma);
        }
    }

    private String wrapQuotation(String key) {
        StringBuilder sb = new StringBuilder();
        sb.append(Symbols.QuotationLeft).append(key).append(Symbols.QuotationRight);
        return sb.toString();
    }

}
