package com.pankov.roadtosenior.ioccontainer.reader;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import com.pankov.roadtosenior.ioccontainer.exception.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

public class BeanDefinitionHandlerOnDeque extends DefaultHandler {

    private final static String BEAN = "bean";
    private final static String PROPERTY = "property";

    private final Stack<BeanDefinition> beanDefinitions = new Stack<>();

    public List<BeanDefinition> getBeanDefinitions() {
        return beanDefinitions;
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
            beanDefinitions.add(new BeanDefinition(id, clazz));
        } else if (qName.equals(PROPERTY)) {
            BeanDefinition beanDefinition = beanDefinitions.peek();

            String name = attributes.getValue("name");
            if (name == null) {
                throw new ParseException("There is no 'NAME' for property");
            }

            Map<String, String> valueProperties = beanDefinition.getValueProperties();
            if (valueProperties == null) {
                valueProperties = new HashMap<>();
            }
            
            Map<String, String> refProperties = beanDefinition.getRefProperties();
            if (refProperties == null) {
                refProperties = new HashMap<>();
            }

            String value = attributes.getValue("value");
            String ref = attributes.getValue("ref");

            if (value != null) {
                valueProperties.put(name, value);
            }

            if (ref != null) {
                refProperties.put(name, ref);
            }

            if (!valueProperties.isEmpty()) {
                beanDefinition.setValueProperties(valueProperties);
            }

            if (!refProperties.isEmpty()) {
                beanDefinition.setRefProperties(refProperties);
            }
        }
    }
}
