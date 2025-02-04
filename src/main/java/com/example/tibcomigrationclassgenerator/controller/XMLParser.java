package com.example.tibcomigrationclassgenerator.controller;

import com.example.tibcomigrationclassgenerator.model.Tag;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class XMLParser {
    static Map<String, LinkedList<Tag>> keyValueMap = new HashMap<>();

    public static Map<String, LinkedList<Tag>> getKeyValueMap() throws Exception {
        String xmlFilePath = "src/main/resources/condition.xml";  // 替换为实际的 XML 文件路径
        findCCBActivity(xmlFilePath);
        return keyValueMap;
    }

    public static void main(String[] args) throws Exception {
        String xmlFilePath = "src/main/resources/condition.xml";  // 替换为实际的 XML 文件路径
        findCCBActivity(xmlFilePath);
    }

    private static void findCCBActivity(String xmlFilePath) throws Exception {
        File xmlFile = new File(xmlFilePath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);

        // 查找 <pd:activity> 节点，并验证其类型
        NodeList activityNodes = document.getElementsByTagName("pd:activity");

        for (int i = 0; i < activityNodes.getLength(); i++) {
            Node activityNode = activityNodes.item(i);
            NodeList activityChildren = activityNode.getChildNodes();

            // 查找 <pd:type> 节点，确认其值为 "com.tibco.plugin.cobol.CCBRenderActivity"
            for (int j = 0; j < activityChildren.getLength(); j++) {
                Node child = activityChildren.item(j);
                if ("pd:type".equals(child.getNodeName()) && "com.tibco.plugin.cobol.CCBRenderActivity".equals(child.getTextContent())) {
                    System.out.println("Found matching activity node, processing inputBindings...");

                    // 找到并处理 <pd:inputBindings> 节点
                    processInputBindings(document);
                    return;  // 找到匹配的节点后就结束
                }
            }
        }
    }

    private static void processInputBindings(Document document) {
        // 获取所有 <pd:inputBindings> 节点
        NodeList inputBindingNodes = document.getElementsByTagName("pd:inputBindings");
        Map<String, LinkedHashMap<String, String>> keyValueMap = new HashMap<>();

        for (int i = 0; i < inputBindingNodes.getLength(); i++) {
            Node inputBindingNode = inputBindingNodes.item(i);
            NodeList childNodes = inputBindingNode.getChildNodes();

            // 遍历 <pd:inputBindings> 下的子节点
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if ("Input".equals(childNode.getNodeName())) {
                    // 找到 <Input> 节点，进一步处理
                    findNextSiblingAfterForceCharacterSet(childNode);
                }
            }
        }
    }

    private static NodeList removeEmptyTextNodes(NodeList nodeList) {
        // 使用ArrayList来收集非空白的节点
        List<Node> validNodes = new ArrayList<>();

        // 遍历NodeList中的所有节点
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            // 处理文本节点
            if (node.getNodeType() == Node.TEXT_NODE) {
                // 如果文本节点的内容为空白，则跳过该节点
                if (node.getTextContent().trim().isEmpty()) {
                    continue;  // 跳过该空白文本节点
                }
            }

            // 如果是其他类型的节点，直接添加到validNodes列表
            validNodes.add(node);
        }

        // 使用DocumentFragment来临时存储有效节点
        Document document = nodeList.item(0).getOwnerDocument();  // 获取父节点的Document对象
        DocumentFragment fragment = document.createDocumentFragment(); // 创建DocumentFragment

        // 将有效节点添加到DocumentFragment中
        for (Node validNode : validNodes) {
            fragment.appendChild(validNode); // 将节点添加到fragment中
        }

        // 通过返回DocumentFragment中的节点列表，模拟NodeList
        return fragment.getChildNodes(); // 返回一个类似NodeList的集合
    }

    // 找到 forceCharacterSet 后的邻接兄弟节点
    private static Map<String, LinkedList<Tag>> findNextSiblingAfterForceCharacterSet(Node node) {
        NodeList childNodes = node.getChildNodes();
        childNodes = removeEmptyTextNodes(childNodes);


        for (int i = 0; i < childNodes.getLength(); i++) {
            Node currentNode = childNodes.item(i);

            if ("forceCharacterSet".equals(currentNode.getNodeName())) {
                // 找到 forceCharacterSet 后的紧接兄弟节点
                if (i + 1 < childNodes.getLength()) {
                    Node nextSibling = childNodes.item(i + 1);
                    System.out.println("Found next sibling: " + nextSibling.getNodeName() + " with value: " + nextSibling.getTextContent());
                    // 进一步处理该节点（如生成映射规则）
                    LinkedList<Tag> keyValues = handleNode(nextSibling);
                    keyValues = combineSameTags(keyValues);

                    keyValueMap.put(nextSibling.getNodeName(), keyValues);
//                    System.out.println("Parent Node: " + nextSibling.getNodeName());
//                    for (Tag tag : keyValues) {
//                        System.out.println("Tag: " + tag.getTagName() + ", Inside Condition: " + tag.getInsideCondition());
//                    }
                }
            }
        }

        return keyValueMap;
    }

    public static LinkedList<Tag> combineSameTags(LinkedList<Tag> tags) {
        LinkedList<Tag> combinedTags = new LinkedList<>();
        for (Tag tag : tags) {
            boolean found = false;
            StringBuilder combineCondition = new StringBuilder();
            if (tag.isIfWhenCondition()) {
                if (tag.getWhenCondition() != null) {
                    combineCondition.append("       if(" + tag.getWhenCondition() + ")" + "{\n");
                } else {
                    combineCondition.append("       else{\n");
                }
                if (tag.getOutsideTag() != null) {
                    combineCondition.append("       if(" + tag.getOutsideCondition() + ")" + "{\n");
                    combineCondition.append("       "+tag.getInsideCondition() + "\n      }\n     }\n");
                } else {
                    combineCondition.append("       "+tag.getInsideCondition() + "\n      }\n");
                }

                tag.setCombileLogic(combineCondition.toString());
            }

            for (Tag combinedTag : combinedTags) {
                if (tag.getTagName().equals(combinedTag.getTagName()) && tag.isIfWhenCondition()) {
                    found = true;
                    combinedTag.setCombileLogic(combinedTag.getCombileLogic() != null
                            ? combinedTag.getCombileLogic() + combineCondition.toString()
                            : combineCondition.toString());
                    break;
                }
            }
            if (!found) {
                combinedTags.add(tag);
            }
        }
        return combinedTags;
    }

    public static LinkedList<Tag> handleNode(Node nextSibling) {
        // 递归遍历所有子节点，提取键值对
        if (nextSibling.hasChildNodes()) {
            NodeList childNodes = nextSibling.getChildNodes();
//            Map<String, LinkedHashMap<String, String>> keyValueMap = new HashMap<>();
            LinkedList<Tag> keyValues = new LinkedList<>();

//            removeEmptyTextNodes(childNodes);
            // 遍历每个子节点并提取信息
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                // 如果是 xsl:value-of 节点，提取 select 属性值
                String nodeName = childNode.getNodeName();
//                String parentNodeName = childNode.getChildNodes().item(0).getNodeValue();

                // 打印出节点名和 select 的值
//                System.out.println("Node: " + nodeName);

                // 调用 XMLToKeyValue 处理并存储
                if (childNode != null && childNode.getNodeType() == Node.TEXT_NODE && childNode.getTextContent().trim().isEmpty()) {
                    continue;
                }
                switch (childNode.getNodeName()) {
                    case "xsl:if":
                        String tagName = childNode.getNodeName();
                        String ifCondition = childNode.getAttributes().getNamedItem("test").getTextContent();
                        extractKeyValuePairs(childNode.getChildNodes().item(1), keyValues, tagName, ifCondition);
                        break;
                    case "xsl:choose":
                        extractChooseWhenKeyValuePairs(childNode.getChildNodes(), keyValues);
                        break;
                    default:
                        extractKeyValuePairs(childNode, keyValues);
                        break;
                }
            }
//            keyValueMap.put(parentNodeName, keyValues);

//             打印或处理生成的键值对
//            System.out.println("Parent Node: " + parentNodeName);
//            for (Map.Entry<String, String> entry : keyValueMap.get(parentNodeName).entrySet()) {
//                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//            }
            return keyValues;
        }
        return null;
    }

    public static void extractKeyValuePairs(Node node, LinkedList<Tag> keyValueMap) {
        // 处理该节点下的所有子节点
        String parentNodeName = node.getNodeName().toLowerCase();
        Node firstChild = node.getChildNodes().item(1);

        if (firstChild.getNodeName().equals("xsl:value-of")) {
            String selectValue = firstChild.getAttributes().getNamedItem("select").getTextContent();
            // 将父节点的名称和 select 的值作为键值对
//            keyValueMap.put(parentNodeName, selectValue);

            Tag tag = new Tag();
            tag.setTagName(parentNodeName);
            tag.setInsideCondition(selectValue);
            keyValueMap.add(tag);
        }
    }

    public static void extractKeyValuePairs(Node node, LinkedList keyValueMap, String outsideTag, String outsideCondition) {
        // 处理该节点下的所有子节点
        String parentNodeName = node.getNodeName().toLowerCase();
        Node firstChild = node.getChildNodes().item(1);

        if (firstChild.getNodeName().equals("xsl:value-of")) {
            String selectValue = firstChild.getAttributes().getNamedItem("select").getTextContent();
            // 将父节点的名称和 select 的值作为键值对
//            keyValueMap.put(parentNodeName, selectValue);

            Tag tag = new Tag();
            tag.setTagName(parentNodeName);
            tag.setInsideCondition(selectValue);
            tag.setOutsideTag(outsideTag);
            tag.setOutsideCondition(outsideCondition);
            keyValueMap.add(tag);
        }
    }

    public static void extractChooseWhenKeyValuePairs(NodeList nodeList, LinkedList<Tag> keyValueMap) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("xsl:when") || node.getNodeName().equals("xsl:otherwise")) {
                LinkedList<Tag> newKeyValueMap = handleNode(node);
                newKeyValueMap.forEach(tag -> {
                    tag.setIfWhenCondition(true);
                    if (node.getAttributes().getNamedItem("test") != null) {
                        tag.setWhenCondition(node.getAttributes().getNamedItem("test").getTextContent());
                    }
                });
                keyValueMap.addAll(newKeyValueMap);
//                System.out.println(newKeyValueMap);
            }
        }
    }
}
