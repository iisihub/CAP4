package com.iisigroup.cap.base.service.impl;

import java.lang.management.MemoryUsage;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.iisigroup.cap.base.dao.ErrorCodeDao;
import com.iisigroup.cap.base.model.ErrorCode;
import com.iisigroup.cap.base.service.ErrorCodeService;
import com.iisigroup.cap.component.Request;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.model.Page;
import com.iisigroup.cap.exception.CapException;
import com.iisigroup.cap.exception.CapMessageException;
import com.iisigroup.cap.security.CapSecurityContext;
import com.iisigroup.cap.utils.CapAppContext;
import com.iisigroup.cap.utils.CapBeanUtil;
import com.iisigroup.cap.utils.CapDate;
import com.iisigroup.cap.utils.CapString;
import com.iisigroup.cap.utils.ManagementUtil;

/**
 * <pre>
 * 訊息代碼表
 * </pre>
 *
 * @since 2012/03/29
 * @author UFOJ
 * @version
 *          <ul>
 *          <li>2012/03/29,UFO,new
 *          </ul>
 */
@Service("errorCodeService")
public class ErrorCodeServiceImpl implements ErrorCodeService {
    private final Logger logger = LoggerFactory.getLogger(ErrorCodeServiceImpl.class);

    @Resource
    private ErrorCodeDao errorCodeDao;

    private SoftReferenceCache<String, ErrorCode> errorCodeCache = new SoftReferenceCache<String, ErrorCode>();

    private boolean cacheMode = true;

    /*
     * (non-Javadoc)
     *
     * @see com.mega.sso.service.BranchService#reload()
     */
    @PostConstruct
    @Override
    public synchronized void reload() {
        long t1 = System.currentTimeMillis();
        MemoryUsage heap1 = ManagementUtil.getCurrentMemUsage();

        errorCodeCache.clear();
        List<ErrorCode> list = errorCodeDao.findByAll();
        for (ErrorCode origin : list) {
            ErrorCode targe = new ErrorCode();
            try {
                CapBeanUtil.copyBean(origin, targe);
                errorCodeCache.put(getCacheKey(origin.getCode(), origin.getLocale()), targe);
            } catch (CapException ex) {
                logger.info("[reload] EXCEPTION!", ex);
            }
        }
        logger.info("[reload] ErrorCodeCache size={} ", errorCodeCache.size());
        logger.info("[reload] {}", ManagementUtil.formatHeapMemoryUsage(heap1, ManagementUtil.getCurrentMemUsage()));
        logger.info("[reload] TOTAL COST= {} ms ", (System.currentTimeMillis() - t1));

    }

    /**
     * get the cache key name
     *
     * @param code
     *            the error code
     * @param locale
     *            the locale value
     * @return key
     */
    private String getCacheKey(String code, String locale) {
        return CapString.concat(StringUtils.trimToEmpty(code), ".", StringUtils.trimToEmpty(locale));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.ErrorCodeService#addErrorCode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void addErrorCode(String code, String locale, String severity, String message, String suggestion) {
        ErrorCode errorCode = getErrorCode(code, locale);
        if (errorCode != null) {
            throw new CapMessageException(CapAppContext.getMessage("js.data.exists"), getClass()); // 資料已存在
        }
        saveOrUpdateErrorCode(new ErrorCode(), code, locale, severity, message, suggestion);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.ErrorCodeService#modifyErrorCode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void modifyErrorCode(String oid, String code, String locale, String severity, String message, String suggestion) {
        ErrorCode errorCode = errorCodeDao.find(oid);
        saveOrUpdateErrorCode(errorCode, code, locale, severity, message, suggestion);
    }

    private void saveOrUpdateErrorCode(ErrorCode errorCode, String code, String locale, String severity, String message, String suggestion) {
        errorCode.setCode(code.toUpperCase());
        errorCode.setLocale(locale);
        errorCode.setSeverity(severity);
        errorCode.setMessage(message);
        errorCode.setSuggestion(suggestion);
        errorCode.setLastModifyBy(CapSecurityContext.getUserId());
        errorCode.setLastModifyTime(CapDate.getCurrentTimestamp());
        errorCodeDao.save(errorCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.ErrorCodeService#getErrorCode(java.lang.String, java.lang.String)
     */
    @Override
    public ErrorCode getErrorCode(String code, String locale) {
        ErrorCode errorCode = errorCodeCache.get(this.getCacheKey(code, locale));

        if (errorCode == null) {
            logger.warn("[getErrorCode]!!! GET_ERRORCODE_FROM_DB !!!");
            errorCode = errorCodeDao.findByCode(code, locale);

            if (cacheMode) {
                errorCodeCache.put(this.getCacheKey(code, locale), errorCode);
            }
        }

        return errorCode;
    }

    @Override
    public List<ErrorCode> getErrorCodeListBySysId(String sysId, String locale) {
        return errorCodeDao.findListBySysId(sysId, locale);
    }

    /**
     * @param codeDao
     *            the codeDao to set
     */
    public void setErrorCodeDao(ErrorCodeDao codeDao) {
        this.errorCodeDao = codeDao;
    }

    private class SoftReferenceCache<K extends Comparable<K>, V> {
        protected Map<K, SoftReference<V>> map = new ConcurrentHashMap<K, SoftReference<V>>();

        public V get(K key) {
            V result = null;
            SoftReference<V> softRef = map.get(key);
            if (softRef != null) {
                result = softRef.get();
                if (result == null) {
                    map.remove(key);
                }
            }
            return result;
        }

        public void put(K key, V value) {
            map.put(key, new SoftReference<V>(value));
        }

        public int size() {
            return map.size();
        }

        public synchronized void clear() {
            this.map.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.ErrorCodeService#deleteErrorCodeByOid(java.lang.String)
     */
    @Override
    public void deleteErrorCodeByOid(String oid) {
        ErrorCode errorCode = errorCodeDao.find(oid);
        if (errorCode != null) {
            errorCodeDao.delete(errorCode);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.iisigroup.cap.base.service.ErrorCodeService#findPage(com.iisigroup.cap.db.dao.SearchSetting, com.iisigroup.cap.component.Request)
     */
    @Override
    public Page<ErrorCode> findPage(SearchSetting search, Request params) {
        String code = params.get("code");
        String locale = params.get("locale");
        String sysId = params.get("sysId");
        if (!CapString.isEmpty(code)) {
            search.addSearchModeParameters(SearchMode.LIKE, "code", code);
        }
        if (!CapString.isEmpty(locale)) {
            search.addSearchModeParameters(SearchMode.EQUALS, "locale", locale);
        }
        if (!CapString.isEmpty(sysId)) {
            search.addSearchModeParameters(SearchMode.LIKE, "sysId", sysId);
        }
        search.addOrderBy("code");
        return errorCodeDao.findPage(ErrorCode.class, search);
    }
}
