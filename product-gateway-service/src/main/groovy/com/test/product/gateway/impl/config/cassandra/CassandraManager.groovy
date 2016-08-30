package com.test.product.gateway.impl.config.cassandra
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.Session
import com.test.product.gateway.impl.config.CassandraConfiguration
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean

@Slf4j
class CassandraManager implements InitializingBean, DisposableBean {

    CassandraConfiguration.MainConfig config
    Cluster cluster
    Session session

    CassandraManager(CassandraConfiguration.MainConfig config) {
        this.config = config
    }

    @Override
    void afterPropertiesSet() throws Exception {
       log.info('connecting to cassandra node address: {}', config.nodeAddress)

        Cluster.Builder builder = Cluster.builder()
        builder = builder.addContactPoint(config.nodeAddress)
        builder = builder.withPort(config.port)
        cluster = builder.build()
        session = cluster.connect()
        initializeSchema()
    }

    synchronized void initializeSchema() {
        CassandraTemplate template = createTemplate()

        config.keyspaces.each { CassandraConfiguration.Keyspace keyspace ->
            template.execute(new SessionCallback<Object>() {
                @Override
                Object doInSession(Session s) {
                    keyspace.checkSchema(cluster, s)
                    return null
                }
            })
        }
    }

    CassandraTemplate createTemplate() {
        return new CassandraTemplate(config, session)
    }

    void destroy() throws Exception {
        try {
            session.close()
        } catch (Exception e) {
            log.error('session close failed', e)
        }
        try {
            cluster.close()
        } catch (Exception e) {
            log.error('cluster close failed', e)
        }
    }

}
