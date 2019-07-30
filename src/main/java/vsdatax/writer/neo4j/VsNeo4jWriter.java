package vsdatax.writer.neo4j;


import com.alibaba.datax.common.plugin.RecordReceiver;
import com.alibaba.datax.common.spi.Writer;
import com.alibaba.datax.common.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vskg.neo4j.base.cypher.CreateTypeConfig;
import vskg.neo4j.base.cypher.CreateTypeConfigGenerator;
import vskg.neo4j.base.env.JavaDriverFactory;
import vskg.neo4j.base.utils.CypherParamException;
import vskg.neo4j.base.utils.CypherParamHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static vsdatax.writer.neo4j.Neo4jWriterConstants.DEFAULT_BATCH_SIZE;

/**
 * @Author JerryHuang
 * @Date 2019/5/27
 */


public class VsNeo4jWriter extends Writer {


    public static class Job extends Writer.Job {

        private static final Logger log = LoggerFactory.getLogger(Job.class);

        private Configuration conf = null;

        @Override
        public void init() {
            this.conf = super.getPluginJobConf();//获取配置文件信息{parameter 里面的参数}
            Map<String, String> params = conf.getMap("", String.class);
            try {
                CypherParamHelper.validParameters(params);
            } catch (CypherParamException e) {
                log.error("Neo4jWriter params:{}", conf.toJSON());
                throw new RuntimeException(e);
            }
        }


        @Override
        public void prepare() {

        }

        @Override
        public List<Configuration> split(int mandatoryNumber) {
            //按照reader 配置文件的格式  来 组织相同个数的writer配置文件
            List<Configuration> configurations = new ArrayList<Configuration>(mandatoryNumber);
            for (int i = 0; i < mandatoryNumber; i++) {
                Configuration splitedTaskConfig = this.conf.clone();
                configurations.add(splitedTaskConfig);
            }
            return configurations;
        }


        @Override
        public void post() {

        }

        @Override
        public void destroy() {

        }

    }


    public static class Task extends Writer.Task {
        private static final Logger log = LoggerFactory.getLogger(Task.class);


        private Map<String, String> taskConfig;
        private Configuration rawConf;

        private String dbUrl;
        private String dbUserName;
        private String dbPassword;

        private int batchSize;

        private CreateTypeConfig createTypeConfig = null;

        private List<String> columns;
        private int columnNumber = 0;

        @Override
        public void init() {
            rawConf=super.getPluginJobConf();
            this.columns = rawConf.getList("column", String.class);
            this.batchSize = rawConf.getInt("batchSize", DEFAULT_BATCH_SIZE);
            this.taskConfig = rawConf.getMap("", String.class);
            this.dbUrl = taskConfig.get("db.uri");
            this.dbUserName = taskConfig.get("db.username");
            this.dbPassword = taskConfig.get("db.password");

            this.columnNumber = columns.size();
            this.createTypeConfig = CreateTypeConfigGenerator.generate(taskConfig);
            JavaDriverFactory.init(dbUrl, dbUserName, dbPassword);
        }

        @Override
        public void startWrite(RecordReceiver recordReceiver) {
            Neo4jWriterHelper.write(recordReceiver, super.getTaskPluginCollector(), this.columns, this.columnNumber, this.createTypeConfig, this.batchSize);

        }

        @Override
        public void destroy() {
            JavaDriverFactory.close();
        }
    }


}


