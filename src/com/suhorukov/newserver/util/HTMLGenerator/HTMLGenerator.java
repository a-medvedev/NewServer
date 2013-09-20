package com.suhorukov.newserver.util.HTMLGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Класс для генерации HTML-списка файлов по заданному пути
public class HTMLGenerator {
    private File path;


    public HTMLGenerator (File p) {
        path = p;
    }

    //Позволяет переопределить путь, по которому будет генериться список
    public void setPath(File p){
        path = p;
    }

    //Возвращает строковое представление HTML-списка
    public String generateHTMLList(){
        if(!path.exists()){
            //Считываем шаблон 404 страницы
            InputStream template = HTMLGenerator.class.getResourceAsStream("404.html");
            StringBuilder templateBuilder = new StringBuilder();
            int ch;
            try {
                while ((ch = template.read()) != -1){
                    templateBuilder.append((char)ch);
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            String page = templateBuilder.toString();
            return page;
        }

        List<File> files = new ArrayList<File>(); //Список файлов
        List<File> dirs = new ArrayList<File>();  //список директорий

        //Разделяем список на файлы и директории
        for (File f : path.listFiles()){
            if (f.isDirectory()){
                dirs.add(f);
            }

            if (f.isFile()){
                files.add(f);
            }
        }

        StringBuilder HTMLList = new StringBuilder();
        if (path.getParent() != null){
            //ссылка на родителя
            HTMLList.append("<a href=\"..\">..</a><br>\n");
        }
        //Генерируем HTML-спиок директорий
        HTMLList.append(generateList(dirs));

        //Генерируем HTML-спиок файлов
        HTMLList.append(generateList(files));

        //таблица соответствий тегов их заменам
        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put("#body#", HTMLList.toString());
        replacements.put("#title#", "File list");

        //Считываем шаблон страницы
        InputStream template = HTMLGenerator.class.getResourceAsStream("template.html");
        StringBuilder templateBuilder = new StringBuilder();
        int ch;
        try {
            while ((ch = template.read()) != -1){
                templateBuilder.append((char)ch);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        String page = templateBuilder.toString();

        //Замена тегов шаблона на реальные значения
        for(String tag : replacements.keySet()){
            page = page.replace(tag, replacements.get(tag));
        }

        return page;
    }

    private String generateList(List<File> files){
        StringBuilder list = new StringBuilder();
        for(File f : files){
            if (f.isDirectory()){
                list.append("[DIR] ");
            }
            if (f.isFile()){
                list.append("[FILE] ");
            }
            list.append("<a href=\"").append(f.getName());
            if(f.isDirectory()){
                list.append("/");
            }
            list.append("\">").append(f.getName()).append("</a><br>\n");
        }
        return list.toString();
    }
}
