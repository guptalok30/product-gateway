package com.test.product.gateway.impl.config
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.KeyspaceMetadata
import com.datastax.driver.core.Session
import com.datastax.driver.core.TableMetadata
import com.test.product.gateway.impl.config.cassandra.CassandraTemplate
import com.test.product.gateway.impl.config.cassandra.CassandraManager
import groovy.util.logging.Slf4j
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnClass(Cluster)
@ConfigurationProperties('cassandra')
@Slf4j
class CassandraConfiguration {

    @Bean
    MainConfig mainConfig() {
        return new MainConfig()
    }

    @Bean
    CassandraManager cassandraManager() {
        return new CassandraManager(mainConfig())
    }

    @Bean
    CassandraTemplate cassandraTemplate() {
        return cassandraManager().createTemplate()
    }

    @ConfigurationProperties('cassandra')
    static class MainConfig {
        @NotEmpty
        String nodeAddress
        @NotEmpty
        List<Keyspace> keyspaces
        int port = 9042
        ConsistencyLevel defaultQueryConsistency = ConsistencyLevel.ONE
        ConsistencyLevel defaultUpdateConsistency = ConsistencyLevel.ONE
        Integer updateTimeoutMillis = 5000
    }

    static class Keyspace {

        String name
        String createCommand

        List<Table> tables = []

        void checkSchema(Cluster cluster, Session session) {
            log.info('Checking keyspace: {}', name)

            KeyspaceMetadata keyspaceMetaData = cluster.metadata.getKeyspace(name)
            if (!keyspaceMetaData) {
                createSchema(session)
            } else {
                tables.each { Table table ->
                    TableMetadata tableMetaData = keyspaceMetaData.getTable(table.name)
                    if (!tableMetaData) {
                        table.createTable(session)
                    }
                }
            }
        }

        void createSchema(Session session) {
            log.info('creating keyspace with query: {}', createCommand)

            session.execute(createCommand)
            tables.each { Table table ->
                table.createTable(session)
            }
        }
    }

    static class Table {
        String createCommand
        String name

        void createTable(Session session) {
            log.info('creating table with query: {}', createCommand)
            session.execute(createCommand)
        }
   }
}
