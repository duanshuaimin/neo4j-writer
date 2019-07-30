package vsdatax.writer.neo4j;

import com.alibaba.datax.common.element.Record;
import com.alibaba.datax.common.exception.DataXException;
import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.plugin.TaskPluginCollector;
import org.neo4j.driver.v1.Driver;
import vskg.neo4j.base.cypher.CreateTypeConfig;
import vskg.neo4j.base.cypher.CypherExecutor;
import vskg.neo4j.base.env.JavaDriverFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author JerryHuang
 * @Date 2019/5/30
 */
public class Neo4jWriterHelper {
    public static void write(RecordReceiver recordReceiver, TaskPluginCollector taskPluginCollector, List<String> columns, int columnNumber, CreateTypeConfig createTypeConfig, int batchSize) {
        List<Record> writeBuffer = new ArrayList<Record>(batchSize);
        Driver driver= JavaDriverFactory.getDriver();
        try {
            Record record;
            while ((record = recordReceiver.getFromReader()) != null) {
                if (record.getColumnNumber() != columnNumber) {
                    // 源头读取字段列数与目的表字段写入列数不相等，直接报错
                    throw DataXException
                            .asDataXException(Neo4jWriterErrorCode.CONF_ERROR,
                                    String.format(
                                            "列配置信息有错误. 因为您配置的任务中，源头读取字段数:%s 与 目的端要写入的字段数:%s 不相等. 请检查您的配置并作出修改.",
                                            record.getColumnNumber(),
                                            columnNumber));
                }

                writeBuffer.add(record);


                if (writeBuffer.size() >= batchSize) {
                    doBatchInsert(driver, writeBuffer,columns,columnNumber,createTypeConfig);
                    writeBuffer.clear();

                }
            }
            if (!writeBuffer.isEmpty()) {
                doBatchInsert(driver, writeBuffer,columns,columnNumber,createTypeConfig);
                writeBuffer.clear();

            }
        } catch (Exception e) {
            throw DataXException.asDataXException(
                    Neo4jWriterErrorCode.WRITE_DATA_ERROR, e);
        } finally {
            writeBuffer.clear();
        }

    }

    private static void doBatchInsert(Driver driver, List<Record> writeBuffer, List<String> columns, int columnNumber, CreateTypeConfig createTypeConfig) {
        List<Map<String, String>> allRecs = new ArrayList<>(writeBuffer.size());
        for (Record record : writeBuffer) {

            Map<String, String> eventElems = record2Map(record, columns, columnNumber);
            allRecs.add(eventElems);
        }
        CypherExecutor.batchExecute(driver, createTypeConfig.getCypherQl(), createTypeConfig.getBatchKey(), allRecs);

    }

    private static Map<String, String> record2Map(Record record, List<String> columns, int columnNumber) {
        Map<String, String> item = new HashMap<>();
        for (int i = 0; i < columnNumber; i++) {

            String key = columns.get(i);
            String val = record.getColumn(i).asString();
            item.put(key, val);
        }
        return item;
    }
}
