package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class Main {

   public static void main(String[] args) {
      //1 задание
      String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
      String fileName = "data.csv";
      List<Employee> list1 = parseCSV(columnMapping, fileName);
      String json1 = listToJson(list1);
      File jsonFile1 = new File("data.json");
      writeString(json1, jsonFile1);

      //2 задание
      List<Employee> list2 = parseXML("data.xml");
      String json2 = listToJson(list2);
      File jsonFile2 = new File("data2.json");
      writeString(json2, jsonFile2);

      //3 задание
      String json3 = readString("new_data.json");
      List<Employee> list3 = jsonToList(json3);
      for (Employee e:list3) {
         System.out.println(e.toString());
      }

   }

   private static List<Employee> jsonToList(String json3) {
      JSONParser parser = new JSONParser();
      List<Employee> empList = new ArrayList<>();

      try {Object obj = parser.parse(json3);
         JSONArray jso = (JSONArray) obj;
         GsonBuilder gb = new GsonBuilder();
         Gson gson = gb.setPrettyPrinting().create();
         Employee employee;

         for (int i = 0; i < jso.size(); i++) {
            employee = gson.fromJson(jso.get(i).toString(), Employee.class);
            empList.add(employee);
         }
         return empList;
      } catch (ParseException e) {
         e.printStackTrace();
      }
      return null;
   }

   private static String readString (String filePath) {
       try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
          StringBuilder sb = new StringBuilder();
          String string;
          while ((string = br.readLine()) != null) {
             sb.append(string);
          }
          return sb.toString();
       } catch (IOException e) {
          e.printStackTrace();
       }
       return null;
   }

   private static List<Employee> parseXML(String xmlPath) {
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document doc = builder.parse(xmlPath);
         NodeList nodeList = doc.getElementsByTagName("employee");
         List<Employee> employeeList = new ArrayList<>();

         for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
               Element employeeElement = (Element) nodeList.item(i);
               Employee employee = new Employee();
               NodeList children = employeeElement.getChildNodes();

               for (int j = 0; j < children.getLength(); j++) {
                  if(children.item(j).getNodeType() == Node.ELEMENT_NODE){
                     Element child = (Element) children.item(j);

                     switch (child.getNodeName()) {
                        case "id":
                           employee.id = Long.parseLong(child.getTextContent());
                           break;
                        case "firstName":
                           employee.firstName = child.getTextContent();
                           break;
                        case "lastName":
                           employee.lastName = child.getTextContent();
                           break;
                        case "country":
                           employee.country = child.getTextContent();
                           break;
                        case "age":
                           employee.age = Integer.parseInt(child.getTextContent());
                           break;
                     }
                  }
               }
               employeeList.add(employee);
            }
         }
         return employeeList;
      } catch (ParserConfigurationException | IOException | SAXException e) {
         e.printStackTrace();
      }
      return null;
   }

   private static void writeString(String json, File file) {
      try (FileWriter fileWriter = new FileWriter(file)) {
         fileWriter.write(json);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static String listToJson(List<Employee> list) {
      GsonBuilder gb = new GsonBuilder();
      Gson gson = gb.setPrettyPrinting().create();
      Type listType = new TypeToken<List<Type>>() {
      }.getType();
      String json = gson.toJson(list, listType);
      return json;
   }

   private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

      try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
         ColumnPositionMappingStrategy<Employee> cpms = new ColumnPositionMappingStrategy<>();
         cpms.setType(Employee.class);
         cpms.setColumnMapping(columnMapping);

         CsvToBean csvToBean = new CsvToBeanBuilder<Employee>(csvReader)
                 .withMappingStrategy(cpms)
                 .build();
         return csvToBean.parse();

      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }
}