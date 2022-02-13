package com.pankov.roadtosenior.ioccontainer.reader;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import com.pankov.roadtosenior.ioccontainer.exception.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefinitionHandler extends DefaultHandler {

    private final static String BEAN = "bean";
    private final static String PROPERTY = "property";

    private List<BeanDefinition> beanDefinitions;
    private BeanDefinition beanDefinition;
    private Map<String, String> valueProperties;
    private Map<String, String> refProperties;

    public List<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
    }

    @Override
    public void startDocument() {
        beanDefinitions = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals(BEAN)) {
            String id = attributes.getValue("id");
            if (id == null) {
                throw new ParseException("ID attribute is required");
            }

            String clazz = attributes.getValue("class");
            if (clazz == null) {
                throw new ParseException("CLASS attribute is required");
            }

            beanDefinition = new BeanDefinition(id);
            beanDefinition.setClassName(clazz);

            valueProperties = new HashMap<>();
            refProperties = new HashMap<>();
        } else if (qName.equals(PROPERTY)) {
            String name = attributes.getValue("name");
            if (name == null) {
                throw new ParseException("There is no 'NAME' for property");
            }
            String value = attributes.getValue("value");
            String ref = attributes.getValue("ref");

            if (value != null) {
                valueProperties.put(name, value);
            }

            if (ref != null) {
                refProperties.put(name, ref);
            }

        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals(BEAN)) {
            if (valueProperties.size() != 0) {
                beanDefinition.setValueProperties(valueProperties);
            }
            if (refProperties.size() != 0) {
                beanDefinition.setRefProperties(refProperties);
            }

            beanDefinitions.add(beanDefinition);
        }
    }
}
