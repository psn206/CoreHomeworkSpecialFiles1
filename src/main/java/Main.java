import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.InputSource;

public class Main {

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        List<Employee> staff = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<Employee>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    private static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(list, listType);
        return json;
    }

    // Метод 2 задачи
    private static List<Employee> parseXML(String s) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> staff = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(s));
        NodeList employeeElements = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < employeeElements.getLength(); i++) {
            NodeList employee = employeeElements.item(i).getChildNodes();
            if (Node.ELEMENT_NODE == employeeElements.item(i).getNodeType()) {
                HashMap<String, String> map = new HashMap<>();
                for (int j = 0; j < employee.getLength(); j++) {
                    if (Node.ELEMENT_NODE == employee.item(j).getNodeType()) {
                        map.put(employee.item(j).getNodeName(), employee.item(j).getTextContent());
                    }
                }
                staff.add(
                        new Employee(Long.valueOf( map.get("id")) ,
                                map.get("firstName"),
                                map.get("lastName"),
                                map.get("country"),
                              Integer.valueOf(map.get("age"))
                        )
                );
            }
        }
        return staff;
    }


    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        // 1 задача
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        try (FileWriter file = new FileWriter("new_data.json")) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //  2 задача
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        try (FileWriter file = new FileWriter("data2.json")) {
            file.write(json2);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
