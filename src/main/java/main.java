
import au.com.bytecode.opencsv.CSVWriter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {

    static Map<String, String> all_category_collection = new HashMap<>();

    private static Document getPage(String url) throws IOException {

        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    private static Document getPageFromFile(String url, String file) throws IOException {

        Document page = Jsoup.parse(new File(file), "utf-8", url);
        return page;
    }

    private static void save_category_to_file(String url, String file, String fileAllCategory) throws IOException {
        Document page = getPageFromFile(url, file);
        List<Element> all_productgroup_href = new ArrayList<>();
        all_productgroup_href = page.select("div[class=modern-menu__item js-mm__item]");
        for (Element item :
                all_productgroup_href) {

            all_category_collection.put(item.select("a").first().text().strip(), item.select("a").first().attr("href"));

        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(all_category_collection);
        Files.write(Paths.get(fileAllCategory), json.getBytes());

    }

    private static void saveListToCSV(String file, List<item> items) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file),';', CSVWriter.NO_QUOTE_CHARACTER);
        for (item item:
                items ) {
          String res  = item.getId()+","+ item.getParrent()+","+item.getName()+","+String.valueOf(item.getPrice());
          String[] record =  res.split(",");
          writer.writeNext(record);
        }
        //String [] record = "4,David,Miller,Australia,30".split(",");
        writer.close();
    }

    private static void saveListToJSON() {
    }


    private static void save_url_to_file(String url, String file) throws IOException {
        Document page = getPage(url);
        PrintWriter out = new PrintWriter(file);
        out.print(page.outerHtml());
    }

    private static Map<String, String> read_category_from_file(String file) throws IOException {
        Map<String, String> result = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        result = objectMapper.readValue(
                new File(file),
                new TypeReference<Map<String, Object>>() {
                });
        return result;
    }

    public static void main(String[] args) throws IOException {
        String url = "https://bm14.ru";
        String file = "src/main/java/index.html";
        String fileAllCategory = "src/main/java/all_category_collection.json";
        String path_data = "src/main/java/data/";
        String path_csv = "src/main/java/csv/";
        String path_all = "src/main/java/all/";

        //save_url_to_file(url,file);
        //save_category_to_file(url,file,fileAllCategory);

        all_category_collection = read_category_from_file(fileAllCategory);
        int iterationCount = all_category_collection.size();
        for (Map.Entry<String, String> entry :
                all_category_collection.entrySet()) {
            //  System.out.println(entry);
            String category_name = entry.getKey().replaceAll("[^\\p{L}\\p{N}]+", "");
            String category_href = url + entry.getValue() + "?alfaction=coutput&alfavalue=1000000";
            String pathfileItems = path_data + category_name + ".html";

                    File fileitems = new File(pathfileItems);
            if (!fileitems.exists()) {
                save_url_to_file(category_href, pathfileItems);
            }
            iterationCount = iterationCount - 1;
            //   System.out.println("осталось " + iterationCount + " итераций!");


        }

        int count = 1;


        List<item> allitems = new ArrayList<>();
        for (Map.Entry<String, String> entry :
                all_category_collection.entrySet()) {
//            if (count > 1) {
//                break;
//            }
            String category_name = entry.getKey().replaceAll("[^\\p{L}\\p{N}]+", "");
            String pathfileItems = path_data + category_name + ".html";
            String filecsv = path_csv + category_name + ".csv";
            Document page = getPageFromFile(entry.getValue(), pathfileItems);
            //  System.out.println(page);
            List<Element> all_product = new ArrayList<>();

            all_product = page.select("article[class=catalog_item product js-product col col-xs-6  col-sm-4 col-md-4 col-lg-3]");
            List<item> currenitems = new ArrayList<>();
            for (Element item :
                    all_product) {

                System.out.println(item);
                String item_id = item.attr("data-product-id");
                String item_name ="";
                if (item.select("a[class=catalog_item__name text_fade js-product__name]").size()>0) {
                    item_name = item.select("a[class=catalog_item__name text_fade js-product__name]").first().text().trim().replaceAll(",", "").replace('"', ' ').trim();
                }
                double item_price = 0;

                //System.out.println(item.attr("data-product-id"));
                //System.out.println(item.select("a[class=catalog_item__name text_fade js-product__name]").first().text().strip());
                //System.out.println(item.select("div[class=price__pdv js-price_pdv-2]"));
                if (item.select("div[class=price__pdv js-price_pdv-2]").size() > 0) {

                   // System.out.println(item.select("div[class=price__pdv js-price_pdv-2]").first().text().strip().replaceAll("[\\p{L}]+", "").replaceAll(" ", ""));//.replaceAll("[\\p{N}]+","")
                    String prstr = item.select("div[class=price__pdv js-price_pdv-2]").first().text().trim().replaceAll("[\\p{L}]+", "").replaceAll(" ", "").trim();
                    int last = prstr.length()-1;
                    char ch = prstr.charAt(last);
                    System.out.println(ch);
                    if (ch == '.') {
                        prstr = prstr.substring(0,last);
                    }


                    item_price = Double.parseDouble(prstr);
                }
                currenitems.add(new item(item_id, category_name.replaceAll(",",""), item_name, item_price));
                allitems.add(new item(item_id, category_name, item_name, item_price));

                System.out.println("*********************************************************************");
            }

            saveListToCSV(filecsv,currenitems);
            count = count + 1;
        }

        saveListToCSV(path_all+"all.csv",allitems);

    }
}

