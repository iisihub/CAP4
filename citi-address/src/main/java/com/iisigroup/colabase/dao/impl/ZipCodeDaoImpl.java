package com.iisigroup.colabase.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.colabase.dao.ZipCodeDao;
import com.iisigroup.colabase.model.ZipCode;
import org.springframework.stereotype.Repository;

@Repository("zipCodeDao")
public class ZipCodeDaoImpl extends GenericDaoImpl<ZipCode> implements ZipCodeDao {

    public List<ZipCode> findByZipCode(String zipCode) {
        EntityManager entityManager = getEntityManager();
        QuerySettings settings = new QuerySettings(entityManager);
        settings.like("zipCode", zipCode + "%");
        TypedQuery<ZipCode> typedQuery = entityManager.createQuery(settings.getQuery());
        return typedQuery.getResultList();
    }

    public List<ZipCode> findByCounty(String county) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "county", county);
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        return find(search);
    }

    public List<ZipCode> findByDistrict(String district) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "district", district);
        search.setFirstResult(0).setMaxResults(Integer.MAX_VALUE);
        return find(search);
    }

    public List<ZipCode> findByCountyAndDist(String county, String district) {
        Query query = getEntityManager().createNativeQuery("select r.* from CO_XSL_ZIPCODE r WITH(NOLOCK) where r.COUNTY= ?1 AND r.DISTRICT= ?2", ZipCode.class);
        query.setParameter(1, county);
        query.setParameter(2, district);
        return query.getResultList();
    }

    private class QuerySettings {
        private EntityManager entityManager;
        private CriteriaQuery<ZipCode> query;
        private CriteriaBuilder builder;
        private Root<ZipCode> root;

        private QuerySettings(EntityManager entityManager) {
            this.entityManager = entityManager;
            this.builder = entityManager.getCriteriaBuilder();
            this.query = builder.createQuery(ZipCode.class);
            this.root = query.from(ZipCode.class);
        }

        public void eq(String fieldName, Object object) {
            Predicate predicate = builder.equal(root.get(fieldName), object);
            query.where(predicate);
        }

        public void like(String fieldName, String pattern) {
            Predicate predicate = builder.like(root.get(fieldName), pattern);
            query.where(predicate);
        }

        public CriteriaQuery<ZipCode> getQuery() {
            return query;
        }

        public CriteriaBuilder getBuilder() {
            return builder;
        }

        public Root<ZipCode> getRoot() {
            return root;
        }
    }
}
