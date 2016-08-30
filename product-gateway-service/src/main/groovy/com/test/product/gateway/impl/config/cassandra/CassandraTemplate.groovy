package com.test.product.gateway.impl.config.cassandra
import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.ResultSetFuture
import com.datastax.driver.core.Session
import com.datastax.driver.core.Statement
import com.test.product.gateway.impl.config.CassandraConfiguration
import org.springframework.util.Assert

class CassandraTemplate {
    Session session
    CassandraConfiguration.MainConfig config

    CassandraTemplate(CassandraConfiguration.MainConfig config, Session session) {
        this.session = session
        this.config = config
    }

    @SuppressWarnings('UnnecessaryPublicModifier')
    public <T> T execute(SessionCallback<T> callback) {
        Assert.notNull(callback)
        return callback.doInSession(session)
    }

    ResultSetFuture executeAsync(Statement statement) {
        return session.executeAsync(statement)
    }

    ConsistencyLevel getDefaultUpdateConsistency() {
        return config.defaultUpdateConsistency
    }

    ConsistencyLevel getDefaultQueryConsistency() {
        return config.defaultQueryConsistency
    }

    long getUpdateTimeoutMillis() {
        return config.updateTimeoutMillis
    }

}
