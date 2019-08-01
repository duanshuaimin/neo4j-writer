package vsdatax.writer.neo4j;

/**
 * @author JerryHuang
 */

import com.alibaba.datax.core.Engine;

import java.time.LocalTime;

public class DataxInvokerTest {

    public static void main(String[] args) {
        String dataxHome = "D:\\work\\java_res\\datax\\datax_aio";
//        String logbackConfFile= StringUtils.join(new String[] {
//                dataxHome, "conf", "logback.xml" }, File.separator);
        System.setProperty("datax.home", dataxHome);
        System.setProperty("num", "1000");

        System.setProperty("now", LocalTime.now().toString());// 替换job中的占位符

        String jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts\\oracle2neo4j_node.json";
//        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts\\mysql2neo4j_node.json";
//        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts\\mysql2neo4j_ql.json";
        //        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts\\mysql2neo4j_node2.json";
        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts\\mysql2neo4j_relationship.json";
//        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts\\mysql2neo4j_full.json";
        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts_public_demo\\public_demo_full.json";
        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts_public_demo\\public_demo_book.json";
        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts_public_demo\\public_demo_publisher.json";
        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts_public_demo\\public_demo_book_relationship.json";
        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts_public_demo\\public_demo_full.json";
        jsonFile = "D:\\work\\projs\\incubator\\vsetl\\vsdatax\\plugins\\neo4j-writer\\scripts_public_demo\\public_demo_ql.json";

        String[] datxArgs = {"-job", jsonFile, "-mode", "standalone", "-jobid", "-1"};
        try {
            Engine.entry(datxArgs);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }




}
