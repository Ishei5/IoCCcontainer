package com.pankov.roadtosenior.ioccontainer.reader;

import com.pankov.roadtosenior.ioccontainer.entity.BeanDefinition;
import com.pankov.roadtosenior.ioccontainer.exception.ParseException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class SaxXMLBeanDefinitionReader implements BeanDefinitionReader {

    private final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    private final BeanDefinitionHandlerOnDeque beanDefinitionHandler = new BeanDefinitionHandlerOnDeque();
    private String[] paths;

    @Override
    public List<BeanDefinition> getBeanDefinitions() {
        return Arrays.stream(paths)
                .map(path -> parseXMLToBeanDefinitionList(this.getClass().getClassLoader().getResourceAsStream(path)))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    List<BeanDefinition> parseXMLToBeanDefinitionList(InputStream inputStream) {
        try {
            saxParserFactory.newSAXParser().parse(inputStream, beanDefinitionHandler);
        } catch (SAXException | ParserConfigurationException | IOException exception) {
            throw new ParseException("Error during parse config file");
        }

        return beanDefinitionHandler.getBeanDefinitions();
    }
}
