package com.pankov.roadtosenior.ioccontainer.reader;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import com.pankov.roadtosenior.ioccontainer.exception.ParseException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class XMLBeanDefinitionReader implements BeanDefinitionReader {
    private String[] paths;

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        return Arrays.stream(paths)
                .map(path -> parseXMLToBeanDefinitionList(this.getClass().getClassLoader().getResourceAsStream(path)))
                .flatMap(list -> list.stream())
                .collect(Collectors.toList());
    }

    List<BeanDefinition> parseXMLToBeanDefinitionList(InputStream inputStream) {
        List<BeanDefinition> listBeanDefinitions = new ArrayList<>();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        BeanDefinition beanDefinition = null;
        Map<String, String> valuePropertiesMap = null;
        Map<String, String> refPropertiesMap = null;

        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("bean")) {
                        valuePropertiesMap = new HashMap<>();
                        refPropertiesMap = new HashMap<>();
                        Attribute idAttr = startElement.getAttributeByName(new QName("id"));

                        if (idAttr == null) {
                            throw new ParseException("ID is not declared");
                        }

                        beanDefinition = new BeanDefinition(idAttr.getValue());

                        Attribute classAttr = startElement.getAttributeByName(new QName("class"));

                        if (classAttr == null) {
                            throw new ParseException("Class is not declared");
                        }
                        beanDefinition.setClassName(classAttr.getValue());
                    } else if (startElement.getName().getLocalPart().equals("property")) {
                        Attribute nameAttr = startElement.getAttributeByName(new QName("name"));
                        Attribute valueAttr = startElement.getAttributeByName(new QName("value"));
                        Attribute refAttr = startElement.getAttributeByName(new QName("ref"));

                        if (refAttr == null) valuePropertiesMap.put(nameAttr.getValue(), valueAttr.getValue());
                        if (valueAttr == null) refPropertiesMap.put(nameAttr.getValue(), refAttr.getValue());
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("bean")) {
                        if (valuePropertiesMap.size() > 0) {
                            beanDefinition.setValueProperties(valuePropertiesMap);
                        }
                        if (refPropertiesMap.size() > 0) {
                            beanDefinition.setRefProperties(refPropertiesMap);
                        }
                        listBeanDefinitions.add(beanDefinition);
                    }
                }
            }
        } catch (XMLStreamException exception) {
            throw new ParseException("Parse XML failed", exception);
        }

        return listBeanDefinitions;
    }
}
