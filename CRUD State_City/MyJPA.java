
import org.hibernate.jpa.HibernatePersistenceProvider;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MyJPA {

    private DataSource getDataSource() {
        final MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setDatabaseName("OrmDemo");
        dataSource.setUser("root");
        dataSource.setPassword("Password");
        dataSource.setUrl("jdbc:postgresql://localhost:3306/city");
        return dataSource;
    }

    private Properties getProperties() {
        final Properties properties = new Properties();
        properties.put( "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect" );
        properties.put( "hibernate.connection.driver_class", "org.postgresql.Driver" );
//        properties.put("hibernate.show_sql", "true");
        return properties;
    }

    private EntityManagerFactory entityManagerFactory(DataSource dataSource, Properties hibernateProperties ){
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan();
        em.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );
        em.setJpaProperties( hibernateProperties );
        em.setPersistenceUnitName( "demo-unit" );
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.afterPropertiesSet();
        return em.getObject();
    }

    public static void main(String[] args) {
        MyJPA jpaDemo = new MyJPA();
        DataSource dataSource = jpaDemo.getDataSource();
        Properties properties = jpaDemo.getProperties();
        EntityManagerFactory entityManagerFactory = jpaDemo.entityManagerFactory(dataSource, properties);
        EntityManager em = entityManagerFactory.createEntityManager();
        PersistenceUnitUtil unitUtil = entityManagerFactory.getPersistenceUnitUtil();
    }
    private static void insertToCity(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        City c = new City();
        c.setName("Los Angeles");
        c.setId("3");
        //em.merge(c);
        em.persist(c);
        tx.commit();
    }

    private static void getCityById(EntityManager em) {
        Query query = em.createQuery("select c from City c left join fetch c.state_city sc where c.id = ?1");
        query.setParameter(1, "3");
        City c = (City)query.getSingleResult();
        System.out.println(c);
    }

    private static void addToJunctionTable1(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        City c = new City();
        c.setName("1th city");
        State s = new State();
        //persist t first to get new id
        em.persist(s);
        s.setName("1th state");
        //build connection between t and s
        State_City sc = new State_City();
        sc.setCity(c);
        sc.setState(s);
        s.addState_City(sc);

        em.persist(c);
        tx.commit();
    }

    private static void addToJunctionTable2(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createNativeQuery("INSERT INTO STATE_CITY (C_ID, S_ID) VALUES (?, ?)");
        query.setParameter(1, 4);
        query.setParameter(2, 4);
        query.executeUpdate();
        tx.commit();
    }

    private static void addToJunctionTable3(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        City c = em.find(City.class, "4");
        State s = em.find(State.class, "5");
        State_City sc = new State_City();
        sc.setCity(c);
        sc.setState(s);
        em.persist(sc);
        tx.commit();
    }

    private static void notOrphanRemoveBiRelation(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select c from City c join fetch c.state_city sc where c.id = ?1");
        query.setParameter(1, "5");
        City c = (City) query.getSingleResult();
        State s = em.find(State.class, "3");
        List<State_City> state_city = new ArrayList<>();
        for(State_City ts: s.getState_City()) {
            if(ts.getState().getId().equals(s.getId())) {
                state_city.add(ts);
                em.remove(ts);
            }
        }
        c.getState_City().removeAll(state_city);
        s.getState_City().removeAll(state_city);
        tx.commit();
    }

    private static void notOrphanRemoveSingleRelation(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select c from City c join fetch c.state_city sc where c.id = ?1");
        query.setParameter(1, "5");
        City c = (City) query.getSingleResult();
        for(State_City sc: c.getState_City()) {
            em.remove(sc);
        }
        c.setState_City(new ArrayList<>());
        tx.commit();
    }

    private static void orphanRemove(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select c from City c join fetch c.state_city sc where c.id = ?1");
        query.setParameter(1, "4");
        City c = (City) query.getSingleResult();
        Iterator<State_City> itr = c.getState_City().iterator();
        while(itr.hasNext()) {
            State_City sc = itr.next();
            if(sc.getState().getId().equals("5")) {
                itr.remove();
            }
        }
        tx.commit();
    }


    private static void withoutOrphanRemove(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select c from City c join fetch c.state_city sc where c.id = ?1");
        query.setParameter(1, "4");
        City c = (City) query.getSingleResult();
        Iterator<State_City> itr = c.getState_City().iterator();
        while(itr.hasNext()) {
            State_City sc = itr.next();
            if(sc.getState().getId().equals("5")) {
                itr.remove();
                em.remove(sc);
            }
        }
        tx.commit();
    }
}
