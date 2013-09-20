package com.suhorukov.newserver;

import com.suhorukov.newserver.util.HTMLGenerator.HTMLGenerator;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

public class Launcher {
    public static void main(String[] args){
        File test = new File("D:\\");
//        HTMLGenerator gen = new HTMLGenerator(test);
//        System.out.println(gen.generateHTMLList());
        Server server = new Server(80, test);
        while(true){
            server.listen();
        }
        //System.out.println(new MimetypesFileTypeMap().getContentType(test));
    }
}
