package com.company;

import java.io.*;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main{
    public static void main (String[] args) throws IOException {
        //echo  pwd  ls  mkdir  cp   cat  cd  grep
        Scanner in = new Scanner(System.in);
        String temp = null;
        label:while(in.hasNext()){
            temp = in.nextLine();
            String[] split = temp.split(" ");
            switch (split[0]){
                case "pwd"  : pwd(split);  break;
                case "ls"   : ls(split);   break;
                case "mkdir": mkdir(split);break;
                case "cat"  : cat(split);  break;
                case "cd"   : cd(split);   break;
                case "cp"   : cp(split);   break;
                case "echo" : echo(temp); break;
                case "grep" : grep(split); break;
                case "rm"   : rm(split[1]);   break;
                case "exit" : break label; //直接跳出去
                default: System.out.println("请输入正确的命令!");
            }
        }
        in.close();
    }

    // 获取当前路径
    public static void pwd(String[] str)throws IOException{
        System.out.println(System.getProperty("user.dir"));
    }

    //获取当前文件夹下的目录
    public static void ls(String[] str)throws IOException{
        File dir = new File(System.getProperty("user.dir"));
        String[] fileList = dir.list();
        //返回由此抽象路径名所表示的目录中的文件和目录的名称所组成字符串数组
        if (fileList == null) System.out.println("当前目录为空");
        else {
            for (String s : fileList) System.out.print(s + "\t");
            System.out.println();
        }
    }

    // 创建一个文件夹
    public static void mkdir(String[] str)throws IOException{
        if(str.length == 1) System.out.println("请输入要创建文件夹的路径!");
        else{
            File file = new File(str[1]);
            if (file.exists() && file.isDirectory())
                System.out.println("文件夹已经存在");
            else  file.mkdirs();
        }
    }

    //打印某个目录下的文件,第二个常数是路径名
    public static void cat(String[] str)throws MalformedInputException,IOException{
        //一般（../)只能是当前目录下
        if(str.length == 1)  System.out.println("请输入文件名!");
        else{
            File file = new File(System.getProperty("user.dir")+"\\"+str[1]);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String info = null;
            int i = 1;//行号
            while ((info = reader.readLine()) != null)
                System.out.println(i++ +"."+"\t" + info);//tab
            reader.close();
        }
    }

    //删除(递归)
    public static void rm(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("该文件不存在");
        }
        if (file.isFile()) {
            file.delete();
        } else {
            String[] filenames = file.list();
            for (String f : filenames) {
                rm(f);
            }
            file.delete();
        }
    }

    //切换目录
    public static void cd(String[] directoryName) {
        //切换上级
        if(directoryName[1].equals("..") || directoryName[1].equals("../")){
            File directory = new File(System.getProperty("user.dir"));
            if(directory.getParentFile()==null){
                System.out.println("文件夹不存在");
                return;
            }
            directory = directory.getParentFile();
            System.setProperty("user.dir", directory.getAbsolutePath());
        }else if(directoryName[1].equals("~") || directoryName[1].equals("/")){
            //根目录
            File tempDirectory = new File(System.getProperty("user.dir"));
            File directory = null;
            while(tempDirectory != null){
                directory = tempDirectory;
                tempDirectory = tempDirectory.getParentFile();
            }
            System.setProperty("user.dir", directory.getAbsolutePath());
        } else{
            //切换指定的目录
            File directory = new File(System.getProperty("user.dir")+"\\"+directoryName[1]);
            if(directory.exists() && directory.isDirectory())
                System.setProperty("user.dir", directory.getAbsolutePath());
            else System.out.println("文件夹不存在");
        }
    }

    //复制
    public static void cp(String[] str)throws IOException{
        ArrayList<String> content = new ArrayList<>();
        File file_Read = new File(System.getProperty("user.dir")+"\\"+str[1]);//只能获取当前文件夹下的文件
        BufferedReader reader = new BufferedReader(new FileReader(file_Read));
        String info = null;
        while ((info = reader.readLine()) != null)
            content.add(info);
        reader.close();
        //先读进来放在数组里，之后再写
        File file_Write = new File(System.getProperty("user.dir")+"\\"+str[2]);
        if(!file_Write.createNewFile()){
            System.out.println("文件创建失败");
            return;
        }
        FileWriter fileWriter = new FileWriter(file_Write.getAbsoluteFile());
        BufferedWriter writer = new BufferedWriter(fileWriter);
        for(String i : content)
            writer.write(i+'\n');
        writer.close();
    }

    //打印字符串
    public static void echo(String str)throws IOException {
        StringBuilder sb = new StringBuilder();
        //从echo 空格后的第一个开始，可能是"
        //” “或者‘ ’就输出了，遇到空格就换行（有点问题）
        for(int i=5; i<str.length(); i++){
            char temp = str.charAt(i);
            if(temp == '\"'){
                i++;
                while(i<str.length()){
                    char tempMatch = str.charAt(i);
                    if(tempMatch=='\"'){
                        temp = '\n';
                        break;
                    }
                    sb.append(tempMatch);
                    i++;
                }
            }
            else if(temp == '\''){
                i++;
                while(i<str.length()){
                    char tempMatch = str.charAt(i);
                    if(tempMatch=='\''){
                        temp = '\n';
                        break;
                    }
                    sb.append(tempMatch);
                    i++;
                }
            }else if(temp == ' ')
                temp = '\n';
            sb.append(temp);
        }
        System.out.println(sb.toString());
    }

    //匹配
    public static void grep(String[] str)throws IOException{
        //grep test *file
        ArrayList<String> content = new ArrayList<>();
        File file = new File(System.getProperty("user.dir")+"\\"+str[2]);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String info = null;
        while ((info = reader.readLine()) != null)
            content.add(info);
        reader.close();

        Pattern match_str = Pattern.compile(str[1]);//任何形式都可
        for(String i : content){
            if(match_str.matcher(i).find())
                System.out.println(i);
        }
    }
}