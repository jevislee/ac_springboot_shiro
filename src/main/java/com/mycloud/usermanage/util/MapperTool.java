package com.mycloud.usermanage.util;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 该工具用于
 * 1.删除mapper.java和mapper.xml里的insert和updateByPrimaryKey
 * 2.实体类的类名后面增加implements Serializable
 * 3.mapper.xml的insertSelective增加属性useGeneratedKeys=\"true\" keyProperty=\"id\" keyColumn=\"id\"
 */
public class MapperTool {

    public static String mapperDir = "E:\\idea_projects\\ac_springboot_shiro\\src\\main\\java\\com\\mycloud\\usermanage";

    public static void main(String[] args) throws Exception {
        recursion(new File(mapperDir));
    }
    public static void recursion(File dir) throws Exception {
        File[] files = dir.listFiles();
        for (File f : files) {
            if(f.isDirectory()) {
                recursion(f);
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
                List<String> lines = new ArrayList<>();
                boolean modified = false;
                String line;
                
                if(dir.getName().equals("entity")) {
                    boolean hasImportedSerializable = false;
                    while ((line = reader.readLine()) != null) {
                        if(!hasImportedSerializable) {
                            if(line.indexOf("import java.io.Serializable;") != -1) {
                                hasImportedSerializable = true;
                            }
                        }
                        
                        if (line.indexOf("public class") >= 0 && line.indexOf("implements Serializable") == -1) {
                            modified = true;
                            
                            if(!hasImportedSerializable) {
                                lines.add(2, "import java.io.Serializable;");
                            }
                            
                            String replaced;
                            if(line.indexOf(" {") != -1) {
                                replaced = line.replace("{", "implements Serializable {");
                            } else {
                                replaced = line.replace("{", " implements Serializable {");
                            }
                            lines.add(StringUtils.stripEnd(replaced, null));
                        } else {
                            lines.add(StringUtils.stripEnd(line, null));
                        }
                    }
                    reader.close();
                } else if (f.getName().endsWith("Mapper.java")) {
                    while ((line = reader.readLine()) != null) {
                        if (line.indexOf("insert(") >= 0 || line.indexOf("updateByPrimaryKey(") >= 0) {
                            modified = true;
                            line = reader.readLine();
                            //下面是多余的换行就去掉
                            if(StringUtils.isNotBlank(line)) {
                                lines.add(StringUtils.stripEnd(line, null));
                            }
                            continue;
                        }

                        lines.add(StringUtils.stripEnd(line, null));
                    }
                    reader.close();
                } else if (f.getName().endsWith("Mapper.xml")) {
                    boolean ignoreLine = false;
                    
                    while ((line = reader.readLine()) != null) {
                        if (line.indexOf("<insert id=\"insert\"") >= 0 || line.indexOf("<update id=\"updateByPrimaryKey\"") >= 0) {
                            modified = true;
                            ignoreLine = true;
                            continue;
                        }

                        if(line.indexOf("</insert>") >= 0 || line.indexOf("</update>") >= 0) {
                            if(ignoreLine == true) {
                                //防止insertSelective和updateByPrimaryKeySelective的结束节点被过滤
                                ignoreLine = false;
                                continue;
                            }
                        }

                        if(ignoreLine == true)
                            continue;

                        /*
                         * 不能用mybatis generator生成的
                         *  <selectKey keyProperty="id" order="BEFORE" resultType="java.lang.Integer">
                         *    SELECT LAST_INSERT_ID()
                         *  </selectKey>
                         * 会导致插入数据时自增主键重复
                         */
                        if(line.indexOf("id=\"insertSelective\"") >= 0) {
                            line = line.replace(">", " ");
                            line += "useGeneratedKeys=\"true\" keyProperty=\"id\" keyColumn=\"id\">";
                        }

                        lines.add(StringUtils.stripEnd(line, null));
                    }
                    reader.close();
                }
                
                if(modified) {
                    System.out.println("processed " + f.getName());
                    BufferedWriter writer = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
                    for (String l : lines) {
                        writer.write(l);
                        writer.newLine();
                    }
                    writer.close();
                }
            }
        }
    }
}
