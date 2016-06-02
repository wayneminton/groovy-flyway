import groovy.sql.Sql
import org.flywaydb.core.Flyway
import spock.lang.Specification

class FlywayBasicsTest extends Specification {

    Sql sql

    // Seems like an in-memory H2 database is getting dropped after Flyway
    // does it's thing; either use file-based db or tell H2 not to close the DB
    // when any of the JDBC connections are closed; the DB will still be closed
    // by a shutdown hook on exit()
    //
    final def db = [
        //url: 'jdbc:h2:./build/tmp/testdb',
        url: 'jdbc:h2:mem:test;DB_CLOSE_DELAY=-1',
        user: 'sa',
        password: '',
        driver: 'org.h2.Driver',
    ]

    def setup() {

        Properties props = new Properties()
        props.setProperty('locations', 'filesystem:foo')

        Flyway flyway = new Flyway()
        flyway.configure(props)
        flyway.setDataSource(db.url, db.user, db.password)
        flyway.clean()
        flyway.migrate()

        sql = Sql.newInstance(db.url, db.user, db.password, db.driver)
        addTestData(sql)
    }

    def cleanup() {
        sql.close()
    }

    def addTestData(Sql sql) {
        sql.execute """
            insert into persons (name) values ('Axel');
            insert into persons (name) values ('Bluto');
            insert into persons (name) values ('Chad');
        """
    }

    def "SQL Insert OK"() {
        setup:
        assert sql != null

        when:
        sql.execute """
            insert into persons (name) values ('Axel');
            insert into persons (name) values ('Bluto');
            insert into persons (name) values ('Chad');
        """
        def rows = sql.rows('select * from persons where name = \'Axel\'')

        then:
        assert rows.size() == 2
    }

    def "Flyway clean OK"() {
        setup:
        assert sql != null

        when:
        def rows = sql.rows('select * from persons')

        then:
        assert rows.size() == 3
    }
}
